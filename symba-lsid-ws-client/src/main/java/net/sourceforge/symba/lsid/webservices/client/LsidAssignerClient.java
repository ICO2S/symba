package net.sourceforge.symba.lsid.webservices.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import net.sourceforge.symba.lsid.webservices.service.LsidAssigner;

/**
 * $LastChangedDate: $
 * $LastChangedRevision: $
 * $Author:  $
 * $HeadURL: $
 */

public class LsidAssignerClient {
    public static void main( String args[] ) throws Exception {

        // get the context of the web services
        ClassPathXmlApplicationContext context
                = new ClassPathXmlApplicationContext( "net/sourceforge/symba/lsid/webservices/client/client-beans.xml" );

        // get an instance of the assigner class
        LsidAssigner assignerClient = ( LsidAssigner ) context.getBean( "clientAssigner" );

        // go ahead and try to get an identifier
        System.out.println( "Response assignLSID: " + assignerClient.assignLSID( "TestClassName" ) );
    }

}
