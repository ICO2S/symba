package net.sourceforge.symba.webapp.util.forms;

import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.common.ontology.OntologySource;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.bio.data.ExternalData;
import net.sourceforge.symba.webapp.util.DatafileSpecificMetadataStore;
import net.sourceforge.symba.webapp.util.SymbaFormSessionBean;
import net.sourceforge.symba.webapp.util.PersonBean;

import javax.servlet.http.HttpSession;
import java.util.List;

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
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/webapp-helper/src/main/java/uk/ac/cisban/symba/webapp/util/LoadFuge.java $
 */
public class DataTemplateParser {

    public static StringBuffer parse( DatafileSpecificMetadataStore info,
                                      int currentDataFile,
                                      SymbaFormSessionBean symbaFormSessionBean,
                                      PersonBean personBean) {

        StringBuffer buffer = new StringBuffer( );

        String selectDescName = "actionListDescription::" + currentDataFile;

        // Next, print information that is directly associated with the data file: this includes a text description
        // of the file itself, and any file format information.
        buffer.append( "<fieldset>" );
        buffer.append( "<legend>Data</legend>" );
        buffer.append(System.getProperty( "line.separator" ));
        buffer.append( "<ol>" );
        buffer.append(System.getProperty( "line.separator" ));

        // Text Box for the description of the file
        buffer.append( "<li>" );
        buffer.append( "<label for=\"" ).append( selectDescName )
                .append( "\">Description of File <a href=\"help.jsp#cisbanIdentifiers\"" );
        buffer.append( "onClick=\"return popup(this, 'notes')\">[ ? ]</a>:</label>" );
        buffer.append( "<textarea id=\"" ).append( selectDescName ).append( "\" name=\"" ).append( selectDescName )
                .append( "\" rows=\"5\" cols=\"40\">" );
        if ( info.getDataFileDescription() != null ) {
            buffer.append( info.getDataFileDescription() );
        }
        buffer.append( "</textarea>" );
        buffer.append( "<br/>" );
        buffer.append( "</li>" );
        buffer.append(System.getProperty( "line.separator" ));

        // The file format for the ExternalData associated with the experiment.
        // Search the ExternalData for dummies named with the name of the current experiment.
        for ( Object obj : personBean.getSymbaEntityService().getLatestExternalData( true ) ) {
            ExternalData externalData = ( ExternalData ) obj;
            if ( externalData.getName().trim().contains( symbaFormSessionBean.getTopLevelProtocolName().trim() ) ) {

                // Now, retrieve the file format (singular). It references an OntologyTerm, which in turn is associated
                // with an OntologySource. Instead of displaying just the referenced OntologyTerm, a pull-down menu
                // should be displayed of ALL of the OntologyTerms in the database associated with that OntologySource.
                if ( externalData.getFileFormat() != null ) {
                    // unchecked cast warning provided by javac when using generics in Lists/Sets and
                    // casting from Object, even though runtime can handle this.
                    // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
                    @SuppressWarnings( "unchecked" )
                    List<OntologyTerm> ontologyTerms = personBean.getSymbaEntityService()
                            .getLatestTermsWithSource(
                                    externalData.getFileFormat()
                                            .getOntologySource()
                                            .getEndurant().getIdentifier() );
                    if ( !ontologyTerms.isEmpty() ) {

                        // the ontology source for the file format may have a description which tells the user
                        // how to select the correct ontology term. Check for that.
                        OntologySource ontologySource = externalData.getFileFormat().getOntologySource();
                        ontologySource = ( OntologySource ) personBean.getSymbaEntityService()
                                .getLatestByEndurant( ontologySource.getEndurant().getIdentifier() );

                        String fileFormat = "fileFormat" + currentDataFile;
                        String instructions =
                                "<label for=\"" + fileFormat + "\">Please select your file format:</label>";
                        for ( Object descObj : ontologySource.getDescriptions() ) {
                            Description desc = ( Description ) descObj;
                            if ( desc.getText().startsWith( "Instructions: " ) ) {
                                instructions =
                                        "<label for=\"" + fileFormat + "\">" + desc.getText().substring( 14 ) +
                                        "</label>";
                            }
                        }

                        buffer.append( instructions );
                        buffer.append(System.getProperty( "line.separator" ));
                        buffer.append( "<li>" );
                        buffer.append( "<select name=\"" ).append( fileFormat ).append( "\" id=\"" )
                                .append( fileFormat ).append( "\">" );
                        buffer.append( "<option value=\"\"></option>" );//js now allways an empty option on pos 0
                        for ( OntologyTerm ot : ontologyTerms ) {
                            String inputStartValue = "<option value= \"" + ot.getEndurant().getIdentifier() + "\"";
                            if ( info.getFileFormat() != null &&
                                 info.getFileFormat().equals( ot.getEndurant().getIdentifier() ) ) {
                                inputStartValue += " selected=\"selected\"";
                            }
                            buffer.append( inputStartValue ).append( ">" ).append( ot.getTerm() ).append( "</option>" );
                        }
                        buffer.append( "</select>" );
                        buffer.append( "<br/>" );
                        buffer.append( "</li>" );
                        buffer.append(System.getProperty( "line.separator" ));
                    }
                }

                // only allow one dummy external data per experiment, for now only!
                break;
            }
        }

        buffer.append( "</ol>" );
        buffer.append( "</fieldset>" );
        buffer.append(System.getProperty( "line.separator" ));

        return buffer;
    }
}
