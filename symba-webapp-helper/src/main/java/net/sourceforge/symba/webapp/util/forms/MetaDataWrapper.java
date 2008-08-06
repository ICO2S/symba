package net.sourceforge.symba.webapp.util.forms;

import net.sourceforge.symba.webapp.util.DatafileSpecificMetadataStore;
import net.sourceforge.symba.webapp.util.SymbaFormSessionBean;
import net.sourceforge.symba.webapp.util.PersonBean;

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
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/webapp-helper/src/main/java/uk/ac/cisban/symba/webapp/util/LoadFuge.java $
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

            // Print material portion of the form
            buffer.append(
                    MaterialTemplateParser.parse( info, currentDataFile, symbaFormSessionBean, personBean, session ) );

            // Print data portion of the form
            buffer.append(
                    DataTemplateParser.parse( info, currentDataFile, symbaFormSessionBean, personBean ) );

            // Print GPA portion of the form
            buffer.append(
                    GenericProtocolApplicationTemplateParser.parse( info, currentDataFile, personBean ) );

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
        buffer.append( "<p>Original name: " )
                .append( info.getOldFilename() )
                .append( "</p>" );

        return buffer;
    }

}
