package net.sourceforge.symba.lsid;

import net.sourceforge.fuge.util.identification.FuGEIdentifier;
import net.sourceforge.symba.lsid.webservices.service.LsidAssigner;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * Stored within the mapping module to ensure that the Lsid Retrieval works correctly within it.
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-mapping/src/main/java/net/sourceforge/symba/lsid/RetrieveLsid.java $
 */
public class LSIDFuGEIdentifier extends FuGEIdentifier {

    private static LsidAssigner assigner;

    private static String location;

    public LSIDFuGEIdentifier( String domainName ) {
        super( domainName );
    }

    public String getLocation() {
        return location;

    }

    public void setLocation( String providedLocation ) {
        location = providedLocation;

        // use the one below if not using within the web interface / tomcat
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext( location );
        if ( context.getBean( "clientAssigner" ) == null ) {
            // use the one below if using within the web interface / tomcat
            context = new ClassPathXmlApplicationContext(
                    ClassLoader.getSystemClassLoader().getResource( location ).toString() );
        }
        // get the assigner
        assigner = ( LsidAssigner ) context.getBean( "clientAssigner" );
    }

    /**
     * This method returns a String that is a new FuGE identifier, according to the rules
     * of the concrete subclass used.
     *
     * @param className
     * @return a FuGE identifier suitable for filling in an Identifiable class' identifier attribute
     */
    public String create( String className ) {
        String entityType = className;
        if ( className.contains( "." ) ) {
            entityType = entityType.substring( entityType.lastIndexOf( "." ) + 1 );
        }
        return assigner.assignLSID( entityType );
    }
}
