package net.sourceforge.symba.lsid.webservices.client;

import net.sourceforge.symba.lsid.webservices.service.LsidDataRetriever;
import net.sourceforge.symba.lsid.webservices.service.LsidDataServiceResponse;
import net.sourceforge.symba.lsid.webservices.service.LsidMetadataResponse;
import net.sourceforge.symba.lsid.webservices.service.LsidResolver;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.activation.DataHandler;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.util.List;
/**
 * $LastChangedDate: $
 * $LastChangedRevision: $
 * $Author:  $
 * $HeadURL: $
 */

public final class LsidResolveAndRetrieveClient {

    public static void main( String args[] ) throws Exception {

        // get the context of the web services
        ClassPathXmlApplicationContext context
                = new ClassPathXmlApplicationContext( new String[]{"net/sourceforge/symba/lsid/webservices/client/client-beans.xml"} );

        String lsid = "urn:lsid:cisban.cisbs.org:FuGE:09e7fb62-f01b-4cf7-9181-57f41ddd46ee";

        // check that we can retrieve our data from this resolution service.
        LsidResolver resolverClient = ( LsidResolver ) context.getBean( "clientResolver" );

        List<LsidDataServiceResponse> serviceResponses = resolverClient.getAvailableServices( lsid );

        // we currently don't care what the response is, as long as it isn't null. This is because we already know
        // how to connect to our data retrieval service.
        if ( serviceResponses == null ) {
            System.out.println( "Service cannot process our LSID: " + lsid );
            return;
        }

        // go ahead and try to get the data and metadata
        LsidDataRetriever retrieverClient = ( LsidDataRetriever ) context.getBean( "clientRetriever" );

        LsidMetadataResponse metadataResponse = retrieverClient.getMetadata(
                lsid, new String[]{LsidMetadataResponse.LSID_RDF_FORMAT} );
        System.out.println( "Response getMetadata: " + metadataResponse.getMetadata() );
        System.out.println( "Response getMetadata timestamp: " + metadataResponse.getTimestamp() );
        System.out.println();

        DataHandler dataResponse = retrieverClient.getData( lsid );
        BufferedInputStream input = new BufferedInputStream( dataResponse.getInputStream() );

        // do whatever you want with the data: the two examples shown here write to either System.out or to a file in
        // the current working directory. You will need to be aware of the type
        // of data coming from SyMBA and handle it properly. This could be XML, or it could be the raw data file.
        BufferedOutputStream output = new BufferedOutputStream( System.out );
//        BufferedOutputStream output = new BufferedOutputStream( new FileOutputStream("test-getData") );

        byte[] buffer = new byte[32 * 1024];
        int bytesRead;
        while ( ( bytesRead = input.read( buffer ) ) != -1 ) {
            output.write( buffer, 0, bytesRead );
        }
        output.flush();
    }
}