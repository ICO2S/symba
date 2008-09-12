package net.sourceforge.symba.webapp.util.forms;

import net.sourceforge.symba.webapp.util.DatafileSpecificMetadataStore;
import net.sourceforge.symba.webapp.util.PersonBean;
import net.sourceforge.symba.webapp.util.SymbaFormSessionBean;
import net.sourceforge.symba.webapp.util.forms.schemes.protocol.ActionInformation;

import javax.servlet.http.HttpSession;

/**
 * This builds the current form contents for metaData.jsp. If you ever want to change what is displayed on
 * this page, then change the method calls here.
 * <p/>
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
public class MetaDataWrapper {

    public static StringBuffer createMetaDataFormContents( SymbaFormSessionBean symbaFormSessionBean,
                                                           PersonBean personBean,
                                                           HttpSession session ) {

        StringBuffer buffer = new StringBuffer();

        // todo ensure that all querying for the form fields only happens once
        // todo this entire page needs generification
        for ( int currentDataFile = 0;
              currentDataFile < symbaFormSessionBean.getDatafileSpecificMetadataStores().size();
              currentDataFile++ ) {
            DatafileSpecificMetadataStore info;

            // if there is a template store, then use it for all metadata and don't use what is really in the session bean
            // note that the templateStore will get deleted at the end of this page. the removal of this variable will
            // ensure that the template is just used the first time metaData.jsp is reached.
            // please note that the templateStore does not have information on the data file: that must ALWAYS
            // be supplied by the symbaFormSessionBean.
            if ( session.getAttribute( "templateStore" ) != null ) {
                info = ( DatafileSpecificMetadataStore ) session.getAttribute( "templateStore" );
            } else {
                info = symbaFormSessionBean.getDatafileSpecificMetadataStores().get( currentDataFile );
            }

            // Print headers
            buffer.append( printDataFileHeaders( info ) );

            // Print data portion of the form
            buffer.append(
                    DataTemplateParser.parse( info, currentDataFile, symbaFormSessionBean, personBean ) );

            // Print GPA portion of the form
            buffer.append(
                    GenericProtocolApplicationTemplateParser.parse( session, symbaFormSessionBean, info,
                            currentDataFile, personBean ) );

            // Print equipment portion of the form
            buffer.append(
                    GenericEquipmentTemplateParser.parse( info, currentDataFile, personBean ) );
        }
        if ( session.getAttribute( "templateStore" ) != null ) {
            session.removeAttribute( "templateStore" );
        }

        return buffer;
    }

    private static StringBuffer printDataFileHeaders( DatafileSpecificMetadataStore info ) {

        StringBuffer buffer = new StringBuffer();

        buffer.append( "<br/>" );
        buffer.append( "<hr/>" );
        // overriding templateStore, if present.
        buffer.append( "<h3>Information for " )
                .append( info.getFriendlyId() )
                .append( "</h3>" );
        buffer.append( "<h3>This data file is assigned to the following part of the experiment: " );
        // this won't print out anything where it's only one deep, but that doesn't happen with current templates
        for ( int iii = info.getNestedActions().getActionHierarchy().size() - 1; iii >= 0; iii-- ) {
            ActionInformation ai = info.getNestedActions().getActionHierarchy().get( iii );
            buffer.append( ai.getActionName() );
            // if this isn't the last time it's going to be run, then add a ", part of the " to the heading.
            if ( iii - 1 >= 0 ) {
                buffer.append( ", part of the " );
            }
        }
        buffer.append( "</h3>" );
        buffer.append( "<p>Original name: " )
                .append( info.getOldFilename() )
                .append( "</p>" );

        return buffer;
    }

}
