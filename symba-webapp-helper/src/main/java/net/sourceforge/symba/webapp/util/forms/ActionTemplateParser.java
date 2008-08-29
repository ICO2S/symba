package net.sourceforge.symba.webapp.util.forms;

import net.sourceforge.fuge.bio.material.Material;
import net.sourceforge.fuge.bio.material.GenericMaterial;
import net.sourceforge.fuge.bio.data.Data;
import net.sourceforge.fuge.common.protocol.*;
import net.sourceforge.symba.service.SymbaEntityService;
import net.sourceforge.symba.webapp.util.DatafileSpecificMetadataStore;
import net.sourceforge.symba.webapp.util.SymbaFormSessionBean;
import net.sourceforge.symba.webapp.util.forms.schemes.protocol.ActionHierarchyScheme;
import net.sourceforge.symba.webapp.util.forms.schemes.protocol.ActionInformation;

import java.util.*;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */
public class ActionTemplateParser {

    /**
     * NONE should never occur - there should be one of the other combinations. Therefore NONE should be treated as
     * exceptional, and should be handled appropriately.
     */
    public static enum PROTOCOL_TYPE {
        MATERIAL_TRANSFORMATION, ASSAY, ALL, NONE
    }

    /**
     * check the list of all dummy GPAs for those that either represent data (default, using no special keywords),
     * or those that represent material transformations (using keyword
     * net.sourceforge.symba.keywords.materialTransformation).
     *
     * @param symbaEntityService   used to retrieve the Dummy GPAs
     * @param topLevelProtocolName the name that must be present within a GPA in order to have it searched.
     * @return What combination of material transformation and assay protocols are found in the current investigation
     */
    public static PROTOCOL_TYPE hasMaterialTransformation( SymbaEntityService symbaEntityService,
                                                           String topLevelProtocolName ) {

        boolean hasMT = false, hasAssay = false;
        for ( Object gpaObj : symbaEntityService.getLatestGenericProtocolApplications( true ) ) {
            GenericProtocolApplication genericProtocolApplication = ( GenericProtocolApplication ) gpaObj;
            if ( genericProtocolApplication.getName().trim().contains( topLevelProtocolName ) ) {
                if ( genericProtocolApplication.getName().trim()
                        .contains( "net.sourceforge.symba.keywords.materialTransformation" ) ) {
                    hasMT = true;
                } else {
                    hasAssay = true;
                }
            }
            if ( hasMT && hasAssay ) {
                return PROTOCOL_TYPE.ALL;
            }
        }

        if ( hasMT ) {
            return PROTOCOL_TYPE.MATERIAL_TRANSFORMATION;
        } else if ( hasAssay ) {
            return PROTOCOL_TYPE.ASSAY;
        } else {
            // there were no GPAs at all. Currently, assume that it's a normal Assay type (the default) rather than
            // returning NONE.
            return PROTOCOL_TYPE.ASSAY;
        }
    }

    public static LinkedHashSet<ActionHierarchyScheme> createFullHierarchy( SymbaEntityService symbaEntityService,
                                                                            LinkedHashSet<ActionHierarchyScheme> hierarchy,
                                                                            ActionHierarchyScheme listItem,
                                                                            GenericProtocol genericProtocol ) {

        if ( !genericProtocol.getActions().isEmpty() ) {
            Set<Action> actions = ( Set<Action> ) genericProtocol.getActions();
            // ensure they are added in the order of their action ordinals.
            for ( int count = 1; count <= actions.size(); count++ ) {
                // each time you get to a new action in the list, you need to reset the listItem.
                listItem = new ActionHierarchyScheme();
                for ( Action action : actions ) {
                    if ( action instanceof GenericAction ) {
                        GenericAction genericAction = ( GenericAction ) action;
                        if ( count == genericAction.getActionOrdinal() ) {
                            Protocol childProtocol = ( Protocol ) symbaEntityService.getLatestByEndurant(
                                    genericAction.getChildProtocol().getEndurant().getIdentifier() );
                            ActionInformation ai = new ActionInformation( genericProtocol.getIdentifier(),
                                    genericProtocol.getName(), genericAction.getEndurant().getIdentifier(),
                                    genericAction.getName(),
                                    childProtocol.getIdentifier() );
                            listItem.add( ai );
                            if ( childProtocol instanceof GenericProtocol ) {
                                hierarchy = createFullHierarchy( symbaEntityService, hierarchy, listItem,
                                        ( GenericProtocol ) childProtocol );
                            }
                        }
                    }
                }
            }
        } else {
            // we're at the base of the tree. Add the final assay / MT action information, then add to the hierarchy
            hierarchy.add( listItem );
        }
        return hierarchy;

    }

    private static StringBuffer parseDummyHierarchy( SymbaFormSessionBean symbaFormSessionBean,
                                                     LinkedHashSet<ActionHierarchyScheme> hierarchy,
                                                     Integer datafileNumber ) {
        return parseHierarchy( symbaFormSessionBean, hierarchy, null, null, true, datafileNumber );

    }

    private static StringBuffer parseHierarchy( SymbaFormSessionBean symbaFormSessionBean,
                                                LinkedHashSet<ActionHierarchyScheme> hierarchy,
                                                HashMap<String, Material> ownedMaterials,
                                                boolean isDummy ) {
        return parseHierarchy( symbaFormSessionBean, hierarchy, ownedMaterials, null, isDummy, null );
    }

    public static StringBuffer parseHierarchy( SymbaFormSessionBean symbaFormSessionBean,
                                               LinkedHashSet<ActionHierarchyScheme> hierarchy,
                                               HashMap<String, Material> ownedMaterials,
                                               HashMap<String, Set<Data>> ownedDataItems,
                                               boolean isDummy,
                                               Integer datafileNumber ) {

        StringBuffer buffer = new StringBuffer();

        boolean noChoice = false;
        // if we are printing materials, pre-select only if there is only one material to print
        if ( ownedMaterials != null && ownedMaterials.size() == 1 ) {
            noChoice = true;
        } else if ( ( ownedMaterials == null || ownedMaterials.isEmpty() ) && hierarchy.size() == 1 ) {
            // otherwise, preselect only if there is one assay action in the entire hierarchy
            noChoice = true;
        }

        List<String> previousActions = new ArrayList<String>();
        String previousTopLevelProtocol = "";
        buffer.append( "<ul>" );
        buffer.append( System.getProperty( "line.separator" ) );
        boolean completeListStart = true;
        for ( ActionHierarchyScheme item : hierarchy ) {
            boolean hierarchyStart = true;
            if ( isDummy ) {
                item.setDummy( true );
            }

            if ( datafileNumber != null ) {
                item.setAssay( true );
                item.setDatafileNumber( datafileNumber );
            }

            for ( ActionInformation actionInformation : item.getActionHierarchy() ) {

                if ( hierarchyStart ) {
                    hierarchyStart = false;
                    // clear all parts of the existing hierarchy that don't match with the new one, closing
                    // lists as we go.
                    int numberRemoved = 0;
                    boolean previousActionMismatchFound = false;

                    // you can get a concurrent modification exception if you try to get the sublist within
                    // the removeAll method, and I can't find a simple way around it. this seems OK for the moment.
                    Iterator<String> paIter = previousActions.iterator();
                    while ( paIter.hasNext() ) {
                        String previousAction = paIter.next();
                        if ( !previousActionMismatchFound &&
                             !previousAction.equals( actionInformation.getActionEndurant() ) ) {
                            // we've found the first non-match. Remove everything from here on to the end.
                            previousActionMismatchFound = true;
                            paIter.remove();
                            numberRemoved++;
                        } else if ( previousActionMismatchFound ) {
                            paIter.remove();
                            numberRemoved++;
                        }
                    }
                    if ( previousActionMismatchFound ) {
                        // and close the exact number of lists that match the number of actions removed.
                        for ( int i = 0; i < numberRemoved; i++ ) {
                            buffer.append( "</ul>" );
                            buffer.append( "</li>" );
                        }
                    }

                    // as a completely separate matter, if it's the start of the hierarchy, and the previous parent
                    // protocol doesn't match, print it.
                    if ( !previousTopLevelProtocol.equals( actionInformation.getParentProtocolIdentifier() ) ) {
                        previousTopLevelProtocol = actionInformation.getParentProtocolIdentifier();

                        if ( completeListStart ) {
                            completeListStart = false;
                        } else {
                            buffer.append( "</ul>" );
                            buffer.append( "</li>" );
                        }
                        buffer.append( "<li>" );
                        buffer.append( actionInformation.getParentProtocolName() );
                        buffer.append( System.getProperty( "line.separator" ) );
                        buffer.append( "<ul>" );
                    }
                }

                StringBuffer actionContents =
                        printActionContents( item, actionInformation, ownedMaterials, ownedDataItems, isDummy,
                                noChoice );

                // if action not present, print out.
                if ( !previousActions.contains( actionInformation.getActionEndurant() ) ) {
                    buffer.append( "<li>" );
                    // if nothing was printed out in terms of contents for this action, then instead, allow the user
                    // to select the action itself. In a view, this will allow the user to add data at that point
                    // in the middle of the assay form, it will allow them to choose that assay for their data file.
                    // However, this must also be a bottom-level action in order to get the radio button displayed.
                    if ( actionContents.length() == 0 && item.getActionHierarchy()
                            .get( item.getActionHierarchy().size() - 1 ).getActionEndurant()
                            .equals( actionInformation.getActionEndurant() ) ) {
                        buffer.append( "<input type=\"radio\" name=\"" ).append( item.write() )
                                .append( "\" value=\"" )
                                .append( item.writeValueAttribute() )
                                .append( "\"" );
                        if ( noChoice ) {
                            buffer.append( "checked=\"checked\"/>" );
                        } else {
                            buffer.append( "/>" );
                        }
                    }
                    buffer.append( actionInformation.getActionName() );
                    buffer.append( "<ul>" );
                    buffer.append( System.getProperty( "line.separator" ) );
                    previousActions.add( actionInformation.getActionEndurant() );
                }

                // always try to print out bottom-level information, irrespective of whether or not the
                // action was shared.
                buffer.append( actionContents );
            }
        }
        // close all remaining list elements
        for ( String previousAction : previousActions ) {
            buffer.append( "</ul>" );
            buffer.append( "</li>" );
        }
        buffer.append( "</ul>" ); // this is to close the action item list - not sure about this one.
        buffer.append( "</ul>" ); // this is to close the entire list.
        return buffer;
    }

    private static StringBuffer printActionContents( ActionHierarchyScheme ahs, ActionInformation actionInformation,
                                                     HashMap<String, Material> ownedMaterials,
                                                     HashMap<String, Set<Data>> ownedDataItems,
                                                     boolean isDummy,
                                                     boolean noChoice ) {

        StringBuffer buffer = new StringBuffer();

        if ( ( ownedMaterials != null && !ownedMaterials.isEmpty() ) ||
             ( ownedDataItems != null && !ownedDataItems.isEmpty() ) ) {
            if ( ownedMaterials != null ) {
                for ( String gpaIdAndPrId : ownedMaterials.keySet() ) {
                    String[] parsedOwnedItemsKey = gpaIdAndPrId.split( "::" );
                    // check that both share the same *protocol*
                    if ( parsedOwnedItemsKey[1]
                            .equals( actionInformation.getProtocolOfActionIdentifier() ) ) {

                        ahs.setGpaIdentifier( parsedOwnedItemsKey[0] );
                        // print options for specimen, if present.
                        buffer.append( System.getProperty( "line.separator" ) );
                        buffer.append( "      <li>" );
                        buffer.append( "<input type=\"radio\" name=\"" ).append( ahs.write() )
                                .append( "\" value=\"" ).append( ahs.writeValueAttribute() );
                        if ( noChoice ) {
                            buffer.append( "\" checked=\"checked\">" );
                        } else {
                            buffer.append( "\">" );
                        }
                        if ( isDummy ) {
                            buffer.append( MaterialTemplateParser.printDummyBaseMaterialSummary(
                                    ownedMaterials.get( gpaIdAndPrId ) ) );
                        } else {
                            buffer.append( MaterialTemplateParser.printMaterialPairSummary(
                                    ( GenericMaterial ) ownedMaterials.get( gpaIdAndPrId ) ) );
                        }
                        buffer.append( "</li>" );
                        buffer.append( System.getProperty( "line.separator" ) );
                    }
                }
            }
            if ( ownedDataItems != null ) {
                for ( String gpaIdAndPrId : ownedDataItems.keySet() ) {
                    String[] parsedOwnedItemsKey = gpaIdAndPrId.split( "::" );
                    // check that both share the same *protocol*
                    if ( parsedOwnedItemsKey[1]
                            .equals( actionInformation.getProtocolOfActionIdentifier() ) ) {

                        ahs.setGpaIdentifier( parsedOwnedItemsKey[0] );
                        // print data information, if present.
                        // ownedDataItems will never contain dummy information
                        buffer.append( DataTemplateParser.printDataSummary(
                                ownedDataItems.get( gpaIdAndPrId ), noChoice, ahs.write(),
                                ahs.writeValueAttribute() ) );
                        buffer.append( System.getProperty( "line.separator" ) );
                    }
                }
            }

        }
        return buffer;
    }

    private static StringBuffer parseDatafiles( SymbaFormSessionBean symbaFormSessionBean,
                                                LinkedHashSet<ActionHierarchyScheme> hierarchy ) {
        StringBuffer buffer = new StringBuffer();

        buffer.append( "<ol>" );
        int counter = 0;
        for ( DatafileSpecificMetadataStore dsms : symbaFormSessionBean.getDatafileSpecificMetadataStores() ) {
            buffer.append( "<li>" );
            buffer.append( "<table width=\"100%\" style=\"table-layout: fixed;\">");
            buffer.append( System.getProperty( "line.separator" ) );
            buffer.append( "<col width=\"250\"/>");
            buffer.append( System.getProperty( "line.separator" ) );
            buffer.append( "<col width=\"*\"/>");
            buffer.append( System.getProperty( "line.separator" ) );
            buffer.append("<tr><td>" );
            buffer.append( System.getProperty( "line.separator" ) );
            buffer.append( dsms.getOldFilename() ).append( " (" ).append( dsms.getFriendlyId() ).append( "):" );
            buffer.append( "</td><td>" );
            buffer.append( System.getProperty( "line.separator" ) );
            buffer.append( parseDummyHierarchy( symbaFormSessionBean, hierarchy, counter ) );
            buffer.append( "</td></tr></table>" );
            buffer.append( System.getProperty( "line.separator" ) );
            buffer.append( "</li>" );
            buffer.append( System.getProperty( "line.separator" ) );
            counter++;
        }
        buffer.append( "</ol>" );

        return buffer;
    }

    private static StringBuffer parseBaseMaterials( SymbaFormSessionBean symbaFormSessionBean,
                                                    LinkedHashSet<ActionHierarchyScheme> hierarchy,
                                                    HashMap<String, Material> ownedMaterials,
                                                    boolean isDummy ) {
        StringBuffer buffer = new StringBuffer();

        buffer.append( parseHierarchy( symbaFormSessionBean, hierarchy, ownedMaterials, isDummy ) );

        return buffer;
    }

    /**
     * @param ownedMaterials       holds information about the output materials of MTs. Ignored for assay actions.
     * @param symbaEntityService   holds connection to database
     * @param symbaFormSessionBean holds the session info
     * @param typeToDisplay        PROTOCOL_TYPE MATERIAL_TRANSFORMATION or ASSAY only. Other types will be ignored.
     * @param isDummy              ignored for assay actions. Used in MTs to determine what the id of the form element should be.
     * @return the html to display
     */
    private static StringBuffer parse( HashMap<String, Material> ownedMaterials,
                                       SymbaEntityService symbaEntityService,
                                       SymbaFormSessionBean symbaFormSessionBean,
                                       PROTOCOL_TYPE typeToDisplay,
                                       boolean isDummy ) {

        StringBuffer buffer = new StringBuffer();

        GenericProtocol topLevelProtocol = ( GenericProtocol ) symbaEntityService
                .getLatestByEndurant( symbaFormSessionBean.getTopLevelProtocolEndurant() );

        // Print out a hierarchy (list of lists) from the top-level protocol down to the final assay / MT protocol,
        // whichever that is. the form will contain the endurant identifiers of all protocols down the hierarchy,
        // right down to the chosen assay. A parse of radio buttons across all possible assays / material
        // transformations will be available for each data file / specimen. If there is only one hierarchy in total,
        // and therefore only one choice for each data file / specimen, then that choice will be selected. Otherwise,
        // unless there is already session information, there will be *no* protocols selected, as this might
        // influence users to accept rather than choose.

        // Each ahs in the list goes straight to the assay / MT level
        LinkedHashSet<ActionHierarchyScheme> hierarchy =
                createFullHierarchy( symbaEntityService, new LinkedHashSet<ActionHierarchyScheme>(),
                        new ActionHierarchyScheme(), topLevelProtocol );

        Set<String> materialActionEndurants = getBaseMaterialActions( symbaEntityService, topLevelProtocol.getName() );

        // Remove from the hierarchy any protocol types that have not been asked for.
        Iterator hierarchyIterator = hierarchy.iterator();
        while ( hierarchyIterator.hasNext() ) {
            ActionHierarchyScheme item = ( ActionHierarchyScheme ) hierarchyIterator.next();

            // The last ahs contains the Action endurant identifier.
            String actionEndurant =
                    item.getActionHierarchy().get( item.getActionHierarchy().size() - 1 ).getActionEndurant();
            if ( ( materialActionEndurants.contains( actionEndurant ) &&
                   typeToDisplay == PROTOCOL_TYPE.ASSAY ) || ( !materialActionEndurants.contains( actionEndurant ) &&
                                                               typeToDisplay == PROTOCOL_TYPE
                                                                       .MATERIAL_TRANSFORMATION ) ) {
                // This is either a MT and we are keeping assays, or it is an Assay, and we are keeping MTs.
                // Either way, remove the ahs
                hierarchyIterator.remove();
            }
        }

        // Now we have a hierarchy that can be printed out for each specimen / data ahs. If there is only
        // one option, print the hierarchy but pre-select that radio button. Otherwise, ONLY select if there
        // is already session information.
        if ( typeToDisplay == PROTOCOL_TYPE.ASSAY ) {
            buffer.append( parseDatafiles( symbaFormSessionBean, hierarchy ) );
        } else if ( typeToDisplay == PROTOCOL_TYPE.MATERIAL_TRANSFORMATION ) {
            buffer.append( parseBaseMaterials( symbaFormSessionBean, hierarchy, ownedMaterials, isDummy ) );
        }
        return buffer;
    }

    public static StringBuffer parseMaterialTransformationActions( List<GenericProtocolApplication> associatedMTs,
                                                                   SymbaEntityService symbaEntityService,
                                                                   SymbaFormSessionBean symbaFormSessionBean,
                                                                   PROTOCOL_TYPE typeToDisplay ) {

        StringBuffer buffer = new StringBuffer();

        // if there is a pre-existing fuge object in the session, we want to present two forms: one with those
        // specimens that are already extant in this particular experiment, and another with a list of
        // specimens that are present in the database.

        HashMap<String, Material> ownedMaterials = new HashMap<String, Material>();
        if ( symbaFormSessionBean.getFuGE() != null ) {
            for ( ProtocolApplication associatedPA : symbaFormSessionBean.getFuGE().getProtocolCollection()
                    .getProtocolApplications() ) {
                if ( associatedPA instanceof GenericProtocolApplication ) {
                    GenericProtocolApplication gpa = ( GenericProtocolApplication ) associatedPA;
                    if ( gpa.getOutputMaterials() != null && !gpa.getOutputMaterials().isEmpty() ) {
                        ownedMaterials.put( gpa.getIdentifier() + "::" + gpa.getProtocol().getIdentifier(),
                                gpa.getOutputMaterials().iterator().next() );
                    }
                    associatedMTs.remove( gpa );
                }
            }
            if ( !ownedMaterials.isEmpty() ) {
                // start form
                buffer.append( "<form action=\"enterSpecimen.jsp\" method=\"post\">" );
                buffer.append( "<fieldset>" );
                buffer.append( "<legend>Review available specimens</legend>" );

                // add form content
                buffer.append( "<p class=\"bigger\">" );
                buffer.append( "If the specimens you require for a particular assay are among those " );
                buffer.append( "listed below, then please move on to " );
                // using rawData.jsp won't delete any session variables, as linking to
                // beginNewSession would.
                buffer.append( "<a  class=\"bigger\" href=\"rawData.jsp\">uploading the assay data file</a>. " );
                buffer.append( "Otherwise, please choose the closest specimen to yours, and modify as required. " );
                buffer.append( "Continue to add specimens until you have all that you need. " );
                buffer.append( "</p>" );
                buffer.append(
                        parse( ownedMaterials, symbaEntityService, symbaFormSessionBean, typeToDisplay, false ) );

                // finish form
                buffer.append( "</fieldset>" );
                buffer.append( "    <fieldset class=\"submit\">\n" +
                               "        <input type=\"submit\" value=\"Create New\" onclick=\"disabled=true\"/>\n" +
                               "    </fieldset>\n" +
                               "    </form>" );
            }
        }

        buffer.append( "<form action=\"enterSpecimen.jsp\" method=\"post\">" );
        buffer.append( "<fieldset>" );
        buffer.append( "<legend>Create a specimen based on existing specimens</legend>" );

        ownedMaterials = new HashMap<String, Material>();
        for ( GenericProtocolApplication associatedMT : associatedMTs ) {
            ownedMaterials.put( associatedMT.getIdentifier() + "::" + associatedMT.getProtocol().getIdentifier(),
                    associatedMT.getOutputMaterials().iterator().next() );
        }
        if ( !ownedMaterials.isEmpty() ) {
            buffer.append( "<p class=\"bigger\">Below are a list of currently existing specimens in the database " );
            buffer.append( "that aren't connected to your experiment yet. " );
            buffer.append( "Click on the one that is closest to what you need, and you will be able to modify it. " );
            buffer.append( "If there is one that is an exact match, click on it and rename the material " );
            buffer.append( "appropriately, then submit it. " );
            buffer.append( "</p>" );
            buffer.append( "<p class=\"bigger\">" );
            buffer.append( "Once you have saved it to your experiment, you will be able to associate it " );
            buffer.append( "with a particular data file." );
            buffer.append( "</p>" );
            buffer.append(
                    parse( ownedMaterials, symbaEntityService, symbaFormSessionBean, typeToDisplay, false ) );
        }

        buffer.append( "</fieldset>" );
        buffer.append( "    <fieldset class=\"submit\">\n" +
                       "        <input type=\"submit\" value=\"Create New\" onclick=\"disabled=true\"/>\n" +
                       "    </fieldset>\n" +
                       "    </form>" );
        return buffer;
    }

    public static StringBuffer parseMaterialTransformationDummyActions( HashMap<String, Material> ownedMaterials,
                                                                        SymbaEntityService symbaEntityService,
                                                                        SymbaFormSessionBean symbaFormSessionBean,
                                                                        PROTOCOL_TYPE typeToDisplay ) {
        return parse( ownedMaterials, symbaEntityService, symbaFormSessionBean, typeToDisplay, true );
    }

    public static StringBuffer parseAssayActions( SymbaEntityService symbaEntityService,
                                                  SymbaFormSessionBean symbaFormSessionBean,
                                                  PROTOCOL_TYPE typeToDisplay ) {
        return parse( null, symbaEntityService, symbaFormSessionBean, typeToDisplay, true );
    }

    /**
     * @param symbaEntityService   the service to connect to the database
     * @param topLevelProtocolName the name of the top-level protocol for the investigation
     * @return the list of Actions that are associated with material transformations
     */
    public static Set<String> getBaseMaterialActions( SymbaEntityService symbaEntityService,
                                                      String topLevelProtocolName ) {

        Set<String> materialActionEndurants = new HashSet<String>();

        // material transformations can only be retrieved via the dummy GPAs, as it is only there where the
        // fact that it is a material transformation is stored. So we'll take these and use them later to
        // identify those protocol types we've been asked to print.
        // Dummy material transformations will have exactly one action application.
        for ( Object gpaObj : symbaEntityService.getLatestGenericProtocolApplications( true ) ) {
            GenericProtocolApplication genericProtocolApplication = ( GenericProtocolApplication ) gpaObj;
            if ( genericProtocolApplication.getName().trim().contains( topLevelProtocolName ) ) {
                if ( genericProtocolApplication.getName().trim()
                        .contains( "net.sourceforge.symba.keywords.materialTransformation" ) ) {
                    materialActionEndurants
                            .add( genericProtocolApplication.getActionApplications().iterator().next().getAction()
                                    .getEndurant().getIdentifier() );
                }
            }
        }
        return materialActionEndurants;
    }
}