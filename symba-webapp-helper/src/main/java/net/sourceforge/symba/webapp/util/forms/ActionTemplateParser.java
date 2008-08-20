package net.sourceforge.symba.webapp.util.forms;

import net.sourceforge.fuge.bio.material.GenericMaterial;
import net.sourceforge.fuge.bio.material.Material;
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
            return PROTOCOL_TYPE.NONE;
        }
    }

    private static LinkedHashSet<ActionHierarchyScheme> createFullHierarchy( SymbaEntityService symbaEntityService,
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

    private static StringBuffer parseHierarchy( SymbaFormSessionBean symbaFormSessionBean,
                                                LinkedHashSet<ActionHierarchyScheme> hierarchy ) {
        return parseHierarchy( symbaFormSessionBean, hierarchy, null, false );

    }

    private static StringBuffer parseHierarchy( SymbaFormSessionBean symbaFormSessionBean,
                                                LinkedHashSet<ActionHierarchyScheme> hierarchy,
                                                HashMap<String, Material> ownedMaterials, boolean dummy ) {

        StringBuffer buffer = new StringBuffer();

        boolean noChoice = false;
        if ( hierarchy.size() == 1 ) {
            noChoice = true;
        }

        ActionHierarchyScheme ahs = new ActionHierarchyScheme();

        if ( dummy ) {
            ahs.setDummy( true );
        }
        String mtLabel = ahs.getElementTitle();

        // todo preselect based on session values

        List<ActionInformation> previousLevels = new ArrayList<ActionInformation>();
        for ( ActionHierarchyScheme item : hierarchy ) {
            item.setDummy( ahs.isDummy() );
            boolean contained;
            int counter = 0;
            for ( ActionInformation actionInformation : item.getActionHierarchy() ) {
                // don't print anything out if it shares any part of the previous item
                contained = previousLevels.equals( actionInformation );
                if ( !contained ) {
                    // A non-matching value may occur either because it is the first hierarchy
                    // item, or it is the bottom of a completely shared hierarchy, or not all of the hierarchy is
                    // shared. In the first case, the previousLevels will be empty. In the second case, the counter
                    // will be at the last list item. Otherwise, it is the last case.
                    if ( previousLevels.isEmpty() ) {
                        // print the beginning of a list.
                        buffer.append( "<ul>" );
                        buffer.append( System.getProperty( "line.separator" ) );
                        buffer.append( "<li>" );
                        buffer.append( actionInformation.getParentProtocolName() );
                        buffer.append( System.getProperty( "line.separator" ) );
                    }
                    // it could be the only item in the action hierarchy. If so, print the beginning and the summary.
                    if ( counter == item.getActionHierarchy().size() - 1 ) {
                        // here we are adding another item to the existing list: no need to do anything other
                        // than create the form item.
                        if ( ownedMaterials != null && !ownedMaterials.isEmpty() ) {
                            // print name of action
                            buffer.append( System.getProperty( "line.separator" ) );
                            buffer.append( "    <ul><li>" );
                            buffer.append( actionInformation.getActionName() );
                            buffer.append( System.getProperty( "line.separator" ) );
                            buffer.append( "        <ul>" );
                            for ( String gpaIdAndPrId : ownedMaterials.keySet() ) {
                                String[] parsedOwnedMaterialsKey = gpaIdAndPrId.split( "::" );
                                // check that both share the same *protocol*
                                if ( parsedOwnedMaterialsKey[1]
                                        .equals( actionInformation.getProtocolOfActionIdentifier() ) ) {

                                    item.setGpaIdentifier( parsedOwnedMaterialsKey[0] );
                                    // print options for specimen, if present.
                                    buffer.append( System.getProperty( "line.separator" ) );
                                    buffer.append( "      <li>" );
                                    buffer.append( "<input type=\"radio\" name=\"" ).append( mtLabel )
                                            .append( "\" value=\"" ).append( item.write() );
                                    if ( noChoice ) {
                                        buffer.append( "\" checked=\"checked\">" );
                                    } else {
                                        buffer.append( "\">" );
                                    }
                                    if ( dummy ) {
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
                            buffer.append( "        </ul>" );
                            buffer.append( System.getProperty( "line.separator" ) );
                            // close the list item for the action
                            buffer.append( "    </li></ul>" );
                            buffer.append( System.getProperty( "line.separator" ) );
                        } else {

                            buffer.append( "<li>" );
                            buffer.append( "<input type=\"radio\" name=\"actionChosen\" value=\"" )
                                    .append( item.write() )
                                    .append( "\"" );
                            if ( noChoice ) {
                                buffer.append( "checked=\"checked\"/>" );
                            } else {
                                buffer.append( "/>" );
                            }
                            buffer.append( "</li>" );
                        }
                    } else if (!previousLevels.isEmpty()) {

                        // it isn't the last item in the hierarchy, and neither is it the first. Because of these
                        // restrictions, and the fact that it isn't contained in the previous levels, it is
                        // a deviation from the previous part of the hierarchy.

                        // remove the incorrect item from the list.
                        previousLevels.remove( previousLevels.size() - 1 );
                        // Now close the old list and start a new one.
                        buffer.append( "</li>" );
                        buffer.append( System.getProperty( "line.separator" ) );
                        buffer.append( "</ul>" );
                        buffer.append( System.getProperty( "line.separator" ) );
                        buffer.append( "<ul>" );
                        buffer.append( "<li>" );
                        buffer.append( actionInformation.getParentProtocolName() );
                    }

                    if ( counter != item.getActionHierarchy().size() - 1 ) {
                        previousLevels.add( actionInformation );
                    }
                }
                counter++;
            }
            buffer.append( "</ul>" );
        }
        return buffer;
    }

    private static StringBuffer parseDatafiles( SymbaFormSessionBean symbaFormSessionBean,
                                                LinkedHashSet<ActionHierarchyScheme> hierarchy ) {
        StringBuffer buffer = new StringBuffer();

        buffer.append( "<ol>" );
        for ( DatafileSpecificMetadataStore dsms : symbaFormSessionBean.getDatafileSpecificMetadataStores() ) {
            buffer.append( "<li>" );
            buffer.append( "<table><tr><td>" );
            buffer.append( dsms.getOldFilename() ).append( " (" ).append( dsms.getFriendlyId() ).append( "):" );
            buffer.append( "</td><td>" );
            buffer.append( parseHierarchy( symbaFormSessionBean, hierarchy ) );
            buffer.append( "</td></tr></table>" );
            buffer.append( "</li>" );
        }
        buffer.append( "</ol>" );

        return buffer;
    }

    private static StringBuffer parseBaseMaterials( SymbaFormSessionBean symbaFormSessionBean,
                                                    LinkedHashSet<ActionHierarchyScheme> hierarchy,
                                                    HashMap<String, Material> ownedMaterials,
                                                    boolean dummy ) {
        StringBuffer buffer = new StringBuffer();

        buffer.append( parseHierarchy( symbaFormSessionBean, hierarchy, ownedMaterials, dummy ) );

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

        // Each item in the list goes straight to the assay / MT level 
        LinkedHashSet<ActionHierarchyScheme> hierarchy =
                createFullHierarchy( symbaEntityService, new LinkedHashSet<ActionHierarchyScheme>(),
                        new ActionHierarchyScheme(), topLevelProtocol );

        Set<String> materialActionEndurants = getBaseMaterialActions( symbaEntityService, topLevelProtocol.getName() );

        // Remove from the hierarchy any protocol types that have not been asked for.
        Iterator hierarchyIterator = hierarchy.iterator();
        while ( hierarchyIterator.hasNext() ) {
            ActionHierarchyScheme item = ( ActionHierarchyScheme ) hierarchyIterator.next();

            // The last item contains the Action endurant identifier.
            String actionEndurant =
                    item.getActionHierarchy().get( item.getActionHierarchy().size() - 1 ).getActionEndurant();
            if ( ( materialActionEndurants.contains( actionEndurant ) &&
                   typeToDisplay == PROTOCOL_TYPE.ASSAY ) || ( !materialActionEndurants.contains( actionEndurant ) &&
                                                               typeToDisplay == PROTOCOL_TYPE
                                                                       .MATERIAL_TRANSFORMATION ) ) {
                // This is either a MT and we are keeping assays, or it is an Assay, and we are keeping MTs.
                // Either way, remove the item
                hierarchyIterator.remove();
            }
        }

        // Now we have a hierarchy that can be printed out for each specimen / data item. If there is only
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

        HashMap<String, Material> ownedMaterials = new HashMap<String, Material>();
        for ( GenericProtocolApplication associatedMT : associatedMTs ) {
            ownedMaterials.put( associatedMT.getIdentifier() + "::" + associatedMT.getProtocol().getIdentifier(),
                    associatedMT.getOutputMaterials().iterator().next() );
        }
        return parse( ownedMaterials, symbaEntityService, symbaFormSessionBean, typeToDisplay, false );
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