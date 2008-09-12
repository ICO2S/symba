package net.sourceforge.symba.webapp.util.forms;

import net.sourceforge.fuge.bio.material.Material;
import net.sourceforge.fuge.bio.material.GenericMaterial;
import net.sourceforge.fuge.bio.data.Data;
import net.sourceforge.fuge.common.protocol.*;
import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.common.measurement.ComplexValue;
import net.sourceforge.fuge.common.measurement.AtomicValue;
import net.sourceforge.fuge.service.EntityService;
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

    /**
     * @param symbaEntityService connection to the database
     * @param hierarchy          a HashMap where the key is the assay / material transformation GPA identifier, and the value
     *                           is an ArrayList of the action endurants, in order, from most general to most specific.
     * @param listItem           the current setting of the value of the hierarchy key-value pair
     * @param genericGpa         the current gpa
     * @param topLevelGpa        the top-level gpa of the top-level protocol
     * @return the updated hierarchy variable
     */
    public static HashMap<String, ArrayList<String>> createGpaActionHierarchy( SymbaEntityService symbaEntityService,
                                                                               HashMap<String, ArrayList<String>> hierarchy,
                                                                               ArrayList<String> listItem,
                                                                               GenericProtocolApplication genericGpa,
                                                                               GenericProtocolApplication topLevelGpa ) {
        if ( !genericGpa.getActionApplications().isEmpty() ) {
            Set<ActionApplication> actionApplications = ( Set<ActionApplication> ) genericGpa.getActionApplications();
            // No need to worry about inherent ordering of AAs at the same level of the hierarchy
            for ( ActionApplication actionApplication : actionApplications ) {
                Action action = actionApplication.getAction();
                if ( action instanceof GenericAction ) {
                    GenericAction genericAction = ( GenericAction ) action;
                    // each time you get to a new action in the list and we are back at the top-level protocol,
                    // you need to reset the listItem.
                    if ( genericGpa.getIdentifier().equals( topLevelGpa.getIdentifier() ) ) {
                        listItem = new ArrayList<String>();
                    }
                    ProtocolApplication childProtocolApplication =
                            ( ProtocolApplication ) symbaEntityService.getLatestByEndurant(
                                    actionApplication.getChildProtocolApplication().getEndurant().getIdentifier() );
                    listItem.add( genericAction.getEndurant().getIdentifier() );
                    if ( childProtocolApplication instanceof GenericProtocolApplication ) {
                        hierarchy = createGpaActionHierarchy( symbaEntityService, hierarchy, listItem,
                                ( GenericProtocolApplication ) childProtocolApplication, topLevelGpa );
                        // if this is an assay/mt-level, then remove the last action information after adding
                        // the hierarchy item
                        if ( childProtocolApplication.getActionApplications().isEmpty() ) {
                            listItem.remove( listItem.size() - 1 );
                        }
                    }
                }
            }
        } else {
            // we're at the base of the tree. Add the current list to the hierarchy
            hierarchy.put( genericGpa.getIdentifier(), new ArrayList<String>( listItem ) );
        }
        return hierarchy;
    }

    public static ArrayList<ActionHierarchyScheme> createProtocolActionHierarchy( SymbaEntityService symbaEntityService,
                                                                                  ArrayList<ActionHierarchyScheme> hierarchy,
                                                                                  ActionHierarchyScheme listItem,
                                                                                  GenericProtocol genericProtocol,
                                                                                  GenericProtocol topLevelProtocol ) {

        if ( !genericProtocol.getActions().isEmpty() ) {
            Set<Action> actions = ( Set<Action> ) genericProtocol.getActions();
            // ensure they are added in the order of their action ordinals.
            for ( int count = 1; count <= actions.size(); count++ ) {
                for ( Action action : actions ) {
                    if ( action instanceof GenericAction ) {
                        GenericAction genericAction = ( GenericAction ) action;
                        if ( count == genericAction.getActionOrdinal() ) {
                            // each time you get to a new action in the list and we are back at the top-level protocol,
                            // you need to reset the listItem.
                            if ( genericProtocol.getIdentifier().equals( topLevelProtocol.getIdentifier() ) ) {
                                listItem = new ActionHierarchyScheme();
                            }
                            Protocol childProtocol = ( Protocol ) symbaEntityService.getLatestByEndurant(
                                    genericAction.getChildProtocol().getEndurant().getIdentifier() );
                            ActionInformation ai = new ActionInformation( genericProtocol.getIdentifier(),
                                    genericProtocol.getName(), genericAction.getEndurant().getIdentifier(),
                                    genericAction.getName(),
                                    childProtocol.getIdentifier() );
                            listItem.add( ai );
                            if ( childProtocol instanceof GenericProtocol ) {
                                hierarchy = createProtocolActionHierarchy( symbaEntityService, hierarchy, listItem,
                                        ( GenericProtocol ) childProtocol, topLevelProtocol );
                                // if this is an assay-level, then remove the last action information after adding
                                // the hierarchy item
                                if ( ( ( GenericProtocol ) childProtocol ).getActions().isEmpty() ) {
                                    listItem.remove( listItem.getActionHierarchy().size() - 1 );
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // we're at the base of the tree. Add the current list to the hierarchy
            hierarchy.add( new ActionHierarchyScheme( listItem ) );
        }
        return hierarchy;
    }

    private static StringBuffer parseDummyHierarchy( EntityService entityService,
                                                     ArrayList<ActionHierarchyScheme> hierarchy,
                                                     Integer datafileNumber ) {
        return parseHierarchy( entityService, hierarchy, null, null, null, true, datafileNumber );

    }

    private static StringBuffer parseHierarchy( EntityService entityService, ArrayList<ActionHierarchyScheme> hierarchy,
                                                HashMap<String, ArrayList<Material>> ownedMaterials,
                                                boolean isDummy ) {
        return parseHierarchy( entityService, hierarchy, null, ownedMaterials, null, isDummy, null );
    }

    public static StringBuffer parseHierarchy( EntityService entityService,
                                               ArrayList<ActionHierarchyScheme> hierarchy,
                                               HashMap<String, ArrayList<String>> gpaHierarchy,
                                               HashMap<String, ArrayList<Material>> ownedMaterials,
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
                        printActionContents( entityService, gpaHierarchy, item, actionInformation, ownedMaterials,
                                ownedDataItems, isDummy, noChoice );

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

    private static StringBuffer printActionContents( EntityService entityService,
                                                     HashMap<String, ArrayList<String>> gpaHierarchy,
                                                     ActionHierarchyScheme ahs,
                                                     ActionInformation actionInformation,
                                                     HashMap<String, ArrayList<Material>> ownedMaterials,
                                                     HashMap<String, Set<Data>> ownedDataItems,
                                                     boolean isDummy,
                                                     boolean noChoice ) {

        StringBuffer buffer = new StringBuffer();

        if ( ( ownedMaterials != null && !ownedMaterials.isEmpty() ) ||
             ( ownedDataItems != null && !ownedDataItems.isEmpty() ) ) {
            if ( ownedMaterials != null ) {
                for ( String gpaIdAndPrId : ownedMaterials.keySet() ) {
                    String[] parsedOwnedItemsKey = gpaIdAndPrId.split( "::" );
                    // when displaying an already-loaded material, there is extra information in the third position
                    // describing whether it's an input or output material
                    String ioStatus = "";
                    String justGpaAndPrId = gpaIdAndPrId;
                    if ( parsedOwnedItemsKey.length >= 3 ) {
                        ioStatus = parsedOwnedItemsKey[2];
                        justGpaAndPrId = parsedOwnedItemsKey[0] + "::" + parsedOwnedItemsKey[1];
                    }
                    // check that both share the same hierarchy of actions if the gpaHierarchy is not null,
                    // alternatively check that they have the same *protocol*.
                    boolean matchFound = false;
                    if ( gpaHierarchy == null || gpaHierarchy.isEmpty() ) {
                        matchFound = parsedOwnedItemsKey[1].equals( actionInformation.getProtocolOfActionIdentifier() );
                    } else if ( gpaHierarchy.get( parsedOwnedItemsKey[0] ) != null && ahs.getActionHierarchy()
                            .get( ahs.getActionHierarchy().size() - 1 ).getActionEndurant()
                            .equals( actionInformation.getActionEndurant() ) ) {
                        // it must be a full match across the entire ActionHierarchyScheme, and the current
                        // actionInformation must be the last item.
                        matchFound = checkGpaHierarchyForMatch( gpaHierarchy.get( parsedOwnedItemsKey[0] ),
                                ahs.getActionHierarchy() );
                    }

                    if ( matchFound ) {

                        ahs.setGpaIdentifier( parsedOwnedItemsKey[0] );

                        GenericProtocolApplication gpa =
                                ( GenericProtocolApplication ) entityService.getIdentifiable( parsedOwnedItemsKey[0] );

                        // print the beginning of the list element to contain all info for this gpa
                        buffer.append( printStartOfGpaInfo( gpa ) );
                        buffer.append( printEquipmentUsed( gpa.getEquipmentApplications() ) );

                        for ( Material material : ownedMaterials.get( gpaIdAndPrId ) ) {
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
                                        material ) );
                            } else {
                                buffer.append( MaterialTemplateParser.printMaterialPairSummary(
                                        ( GenericMaterial ) material, ioStatus ) );
                            }
                            buffer.append( "</li>" );
                            buffer.append( System.getProperty( "line.separator" ) );
                        }
                        if ( ownedDataItems != null && ownedDataItems.get( justGpaAndPrId ) != null ) {
                            // add any associated data
                            buffer.append( DataTemplateParser.printDataSummary(
                                    ownedDataItems.get( justGpaAndPrId ), noChoice, ahs.write(),
                                    ahs.writeValueAttribute() ) );
                            buffer.append( System.getProperty( "line.separator" ) );

                            // remove the item you've just printed, so it doesn't get printed twice
                            ownedDataItems.remove( justGpaAndPrId );

                        }
                        // close the list element containing all info for this gpa
                        buffer.append( "      </ul>" );
                        buffer.append( "      </li>" );
                        buffer.append( System.getProperty( "line.separator" ) );

                    }
                }
            }
            if ( ownedDataItems != null ) {
                for ( String gpaIdAndPrId : ownedDataItems.keySet() ) {
                    String[] parsedOwnedItemsKey = gpaIdAndPrId.split( "::" );
                    // check that both share the same hierarchy of actions if the gpaHierarchy is not null,
                    // alternatively check that they have the same *protocol*.
                    boolean matchFound = false;
                    if ( gpaHierarchy == null || gpaHierarchy.isEmpty() ) {
                        matchFound = parsedOwnedItemsKey[1].equals( actionInformation.getProtocolOfActionIdentifier() );
                    } else if ( gpaHierarchy.get( parsedOwnedItemsKey[0] ) != null && ahs.getActionHierarchy()
                            .get( ahs.getActionHierarchy().size() - 1 ).getActionEndurant()
                            .equals( actionInformation.getActionEndurant() ) ) {
                        // it must be a full match across the entire ActionHierarchyScheme, and the current
                        // actionInformation must be the last item.
                        matchFound = checkGpaHierarchyForMatch( gpaHierarchy.get( parsedOwnedItemsKey[0] ),
                                ahs.getActionHierarchy() );
                    }
                    if ( matchFound ) {
                        ahs.setGpaIdentifier( parsedOwnedItemsKey[0] );
                        GenericProtocolApplication gpa =
                                ( GenericProtocolApplication ) entityService.getIdentifiable( parsedOwnedItemsKey[0] );
                        // print the beginning of the list element to contain all info for this gpa
                        buffer.append( printStartOfGpaInfo( gpa ) );
                        buffer.append( printEquipmentUsed( gpa.getEquipmentApplications() ) );

                        // print data information, if present.
                        // ownedDataItems will never contain dummy information
                        buffer.append( DataTemplateParser.printDataSummary(
                                ownedDataItems.get( gpaIdAndPrId ), noChoice, ahs.write(),
                                ahs.writeValueAttribute() ) );
                        buffer.append( System.getProperty( "line.separator" ) );
                        // close the list element containing all info for this gpa
                        buffer.append( "      </ul>" );
                        buffer.append( "      </li>" );
                        buffer.append( System.getProperty( "line.separator" ) );
                    }
                }
            }

        }
        return buffer;
    }

    private static StringBuffer printEquipmentUsed( Collection<EquipmentApplication> equipmentApplications ) {

        StringBuffer buffer = new StringBuffer();
        for ( EquipmentApplication equipmentApplication : equipmentApplications ) {
            buffer.append( "<li>Equipment used: " )
                    .append( equipmentApplication.getAppliedEquipment().getName() );
            buffer.append( "<ul>" );
            // print and save the equipment description
            String equipmentDescription = "User description: ";
            for ( Description description : ( Set<Description> ) equipmentApplication
                    .getDescriptions() ) {
                equipmentDescription += description.getText();
            }
            buffer.append( "<li>" ).append( equipmentDescription ).append( "</li>" );
            // print and save the equipment ontology terms and atomic values
            for ( ParameterValue parameterValue : ( Set<ParameterValue> ) equipmentApplication
                    .getParameterValues() ) {
                if ( parameterValue.getValue() instanceof AtomicValue ) {
                    buffer.append( "<li>" );
                    if ( parameterValue.getParameter().getName() != null ) {
                        buffer.append( parameterValue.getParameter().getName() ).append( ": " );
                    }
                    buffer.append( ( ( AtomicValue ) parameterValue.getValue() ).getValue() )
                            .append( "</li>" );
                } else if ( parameterValue.getValue() instanceof ComplexValue ) {
                    // not sure how to display the ontology sources yet
//                                                    + " (From Ontology Source: " +
//                                                    ( ( ComplexParameterValue ) parameterValue ).getParameterValue().getOntologySource().getName()
//                                                    + ")"
                    buffer.append( "<li>" ).append( parameterValue.getParameter().getName() ).append( ": " )
                            .append( ( ( ComplexValue ) parameterValue.getValue() ).getValue().getTerm() )
                            .append( "</li>" );
                }
            }
            buffer.append( "</ul>" );
            buffer.append( "</li>" );
        }
        return buffer;
    }

    private static StringBuffer printStartOfGpaInfo( GenericProtocolApplication gpa ) {

        StringBuffer buffer = new StringBuffer();

        // print out containing list element for the current GPA (may be >1 GPA per action/protocol.
        // print out any information written about the "protocol" and stored in the GPA
        String text = "<li> User description: ";
        if ( gpa.getDescriptions() != null ) {
            for ( Description description : gpa.getDescriptions() ) {
                String currentText = description.getText();
                if ( currentText.startsWith( "ProtocolDescription = " ) ) {
                    currentText = currentText.substring( 22 );
                }
                text += currentText + " ";
            }
        }
        if ( text.equals( "<li> User description: " ) ) {
            text += "none";
        }
        text += "</li>";

        String parameterText = "";
        // print out information on any atomic parameters of the gpa
        if ( !gpa.getParameterValues().isEmpty() ) {
            // print and save all atomic parameters
            for ( ParameterValue parameterValue : ( Set<ParameterValue> ) gpa
                    .getParameterValues() ) {
                if ( parameterValue.getValue() instanceof AtomicValue ) {
                    parameterText += "<li>";
                    if ( parameterValue.getParameter().getName() != null ) {
                        parameterText += parameterValue.getParameter().getName() + ": ";
                    }
                    parameterText += ( ( AtomicValue ) parameterValue.getValue() ).getValue() + "</li>";
                }
            }
        }

        if ( text.length() > 0 || parameterText.length() > 0 ) {
            buffer.append( "<li>Protocol Information: " );
            buffer.append( "<ul>" );
            buffer.append( text );
            buffer.append( parameterText );
            // don't close this yet - all other info will be part of this section
        }

        return buffer;
    }

    private static boolean checkGpaHierarchyForMatch( ArrayList<String> listOfActions,
                                                      List<ActionInformation> actionInformations ) {

        // first, they should contain the same number of items
        if ( listOfActions.size() != actionInformations.size() ) {
            return false;
        }

        // next, each action, in order, must match.
        for ( int iii = 0; iii < listOfActions.size(); iii++ ) {
            String actionEndurantLoa = listOfActions.get( iii );
            String actionEndurantAi = actionInformations.get( iii ).getActionEndurant();
            if ( !actionEndurantLoa.equals( actionEndurantAi ) ) {
                return false;
            }
        }

        return true;
    }

    private static StringBuffer parseDatafiles( EntityService entityService, SymbaFormSessionBean symbaFormSessionBean,
                                                ArrayList<ActionHierarchyScheme> hierarchy ) {
        StringBuffer buffer = new StringBuffer();

        buffer.append( "<ol>" );
        int counter = 0;
        for ( DatafileSpecificMetadataStore dsms : symbaFormSessionBean.getDatafileSpecificMetadataStores() ) {
            buffer.append( "<li>" );
            buffer.append( "<table width=\"100%\" style=\"table-layout: fixed;\">" );
            buffer.append( System.getProperty( "line.separator" ) );
            buffer.append( "<col width=\"250\"/>" );
            buffer.append( System.getProperty( "line.separator" ) );
            buffer.append( "<col width=\"*\"/>" );
            buffer.append( System.getProperty( "line.separator" ) );
            buffer.append( "<tr><td>" );
            buffer.append( System.getProperty( "line.separator" ) );
            buffer.append( dsms.getOldFilename() ).append( " (" ).append( dsms.getFriendlyId() ).append( "):" );
            buffer.append( "</td><td>" );
            buffer.append( System.getProperty( "line.separator" ) );
            buffer.append( parseDummyHierarchy( entityService, hierarchy, counter ) );
            buffer.append( "</td></tr></table>" );
            buffer.append( System.getProperty( "line.separator" ) );
            buffer.append( "</li>" );
            buffer.append( System.getProperty( "line.separator" ) );
            counter++;
        }
        buffer.append( "</ol>" );

        return buffer;
    }

    private static StringBuffer parseBaseMaterials( ArrayList<ActionHierarchyScheme> hierarchy,
                                                    HashMap<String, ArrayList<Material>> ownedMaterials,
                                                    boolean isDummy, EntityService entityService ) {
        StringBuffer buffer = new StringBuffer();

        buffer.append( parseHierarchy( entityService, hierarchy, ownedMaterials, isDummy ) );

        return buffer;
    }

    /**
     * @param ownedMaterials       holds information about the output materials of MTs. Ignored for assay actions.
     * @param entityService        holds fuge connection to database
     * @param symbaEntityService   holds connection to database
     * @param symbaFormSessionBean holds the session info
     * @param typeToDisplay        PROTOCOL_TYPE MATERIAL_TRANSFORMATION or ASSAY only. Other types will be ignored.
     * @param isDummy              ignored for assay actions. Used in MTs to determine what the id of the form element should be. @return the html to display
     * @return the part of the form that this method creates
     */
    private static StringBuffer parse( HashMap<String, ArrayList<Material>> ownedMaterials,
                                       EntityService entityService, SymbaEntityService symbaEntityService,
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
        ArrayList<ActionHierarchyScheme> hierarchy =
                createProtocolActionHierarchy( symbaEntityService, new ArrayList<ActionHierarchyScheme>(),
                        new ActionHierarchyScheme(), topLevelProtocol, topLevelProtocol );

//        buffer.append( "<!-- FULL LIST OF HIERARCHY:\n" );
//        for ( ActionHierarchyScheme actionHierarchyScheme : hierarchy ) {
//            buffer.append( "Name: " ).append( actionHierarchyScheme.write() );
//            buffer.append( "\n" );
//            buffer.append( "Value attribute: " ).append( actionHierarchyScheme.writeValueAttribute() );
//            buffer.append( "\n" );
//            System.err.println( "Name: " + actionHierarchyScheme.write() );
//            System.err.println( "Value attribute: " + actionHierarchyScheme.writeValueAttribute() );
//        }
//        buffer.append( "-->\n" );

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
            buffer.append( parseDatafiles( entityService, symbaFormSessionBean, hierarchy ) );
        } else if ( typeToDisplay == PROTOCOL_TYPE.MATERIAL_TRANSFORMATION ) {
            buffer.append( parseBaseMaterials( hierarchy, ownedMaterials, isDummy, entityService ) );
        }
        return buffer;
    }

    public static StringBuffer parseMaterialTransformationActions( List<GenericProtocolApplication> associatedMTs,
                                                                   EntityService entityService,
                                                                   SymbaEntityService symbaEntityService,
                                                                   SymbaFormSessionBean symbaFormSessionBean,
                                                                   PROTOCOL_TYPE typeToDisplay ) {

        StringBuffer buffer = new StringBuffer();

        // if there is a pre-existing fuge object in the session, we want to present two forms: one with those
        // specimens that are already extant in this particular experiment, and another with a list of
        // specimens that are present in the database.

        HashMap<String, ArrayList<Material>> ownedMaterials = new HashMap<String, ArrayList<Material>>();
        if ( symbaFormSessionBean.getFuGE() != null ) {
            for ( ProtocolApplication associatedPA : symbaFormSessionBean.getFuGE().getProtocolCollection()
                    .getProtocolApplications() ) {
                if ( associatedPA instanceof GenericProtocolApplication ) {
                    GenericProtocolApplication gpa = ( GenericProtocolApplication ) associatedPA;
                    if ( gpa.getOutputMaterials() != null && !gpa.getOutputMaterials().isEmpty() ) {
                        ownedMaterials.put( gpa.getIdentifier() + "::" + gpa.getProtocol().getIdentifier(),
                                new ArrayList<Material>( gpa.getOutputMaterials() ) );
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
                        parse( ownedMaterials, entityService, symbaEntityService, symbaFormSessionBean, typeToDisplay,
                                false ) );

                // finish form
                buffer.append( "</fieldset>" );
                buffer.append( "    <fieldset class=\"submit\">\n" +
                               "        <input type=\"submit\" value=\"Create New\" onclick=\"disabled=true\"/>\n" +
                               "    </fieldset>\n" +
                               "    </form>" );
            }
        }


        ownedMaterials = new HashMap<String, ArrayList<Material>>();
        for ( GenericProtocolApplication associatedMT : associatedMTs ) {
            ownedMaterials.put( associatedMT.getIdentifier() + "::" + associatedMT.getProtocol().getIdentifier(),
                    ( ArrayList<Material> ) associatedMT.getOutputMaterials() );
        }
        if ( !ownedMaterials.isEmpty() ) {
            buffer.append( "<form action=\"enterSpecimen.jsp\" method=\"post\">" );
            buffer.append( "<fieldset>" );
            buffer.append( "<legend>Create a specimen based on existing specimens</legend>" );

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
                    parse( ownedMaterials, entityService, symbaEntityService, symbaFormSessionBean, typeToDisplay,
                            false ) );

            buffer.append( "</fieldset>" );

            buffer.append( "    <fieldset class=\"submit\">\n" +
                           "        <input type=\"submit\" value=\"Create New\" onclick=\"disabled=true\"/>\n" +
                           "    </fieldset>\n" +
                           "    </form>" );
        }

        return buffer;
    }

    public static StringBuffer parseMaterialTransformationDummyActions( HashMap<String, ArrayList<Material>> ownedMaterials,
                                                                        EntityService entityService,
                                                                        SymbaEntityService symbaEntityService,
                                                                        SymbaFormSessionBean symbaFormSessionBean,
                                                                        PROTOCOL_TYPE typeToDisplay ) {
        return parse( ownedMaterials, entityService, symbaEntityService, symbaFormSessionBean, typeToDisplay, true );
    }

    public static StringBuffer parseAssayActions( EntityService entityService, SymbaEntityService symbaEntityService,
                                                  SymbaFormSessionBean symbaFormSessionBean,
                                                  PROTOCOL_TYPE typeToDisplay ) {
        return parse( null, entityService, symbaEntityService, symbaFormSessionBean, typeToDisplay, true );
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