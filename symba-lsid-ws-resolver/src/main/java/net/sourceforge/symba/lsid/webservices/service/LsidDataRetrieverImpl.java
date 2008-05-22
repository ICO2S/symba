package net.sourceforge.symba.lsid.webservices.service;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.DC;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import net.sourceforge.symba.ServiceLocator;
import net.sourceforge.symba.util.conversion.xml.XMLMarshaler;
import org.xml.sax.SAXException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * This implements the getData() and getMetadata() methods as required by the LSID spec:
 * http://lsids.sourceforge.net/quick-links/lsid-spec/
 * <p/>
 * see the interface for more information
 * <p/>
 * $LastChangedDate: 2008-04-24 14:35:51 +0100 (Thu, 24 Apr 2008) $
 * $LastChangedRevision: 129 $
 * $Author: allysonlister $
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/backend/src/main/java/uk/ac/cisban/symba/backend/util/CisbanHelper.java $
 */

@WebService(endpointInterface = "net.sourceforge.symba.lsid.webservices.service.LsidDataRetriever",
        serviceName = "LsidDataRetrieverService")
public class LsidDataRetrieverImpl implements LsidDataRetriever {

    private static final RealizableEntityService reService = ServiceLocator.instance().getRealizableEntityService();
    private static final URL SCHEMA_FILE = ClassLoader.getSystemClassLoader().getResource("//FuGE_M3_test_13_07_2006.xsd");

    public LsidMetadataResponse getMetadata(@WebParam(name = "lsid")String lsid, String[] acceptedFormats) {

//        System.out.println( "Was passed lsid: " + lsid );
//        System.out.println( "was passed acceptedFormats = " + Arrays.toString( acceptedFormats ) );

        boolean goodFormat = false;
        for (String format : acceptedFormats) {
            if (format.equals(LsidMetadataResponse.LSID_RDF_FORMAT)) {
                goodFormat = true;
                break;
            }
        }

        if (!goodFormat) {
            return null;
        }

        // the format is acceptable to all. Now get the metadata.

        LsidMetadataResponse response = new LsidMetadataResponse();

        // there will always be no expiration date on the metadata, as we have no expectation of when it will become
        // invalid.
        response.setTimestamp(null);
        Model model = ModelFactory.createDefaultModel();

        String title = "Latest Version of Requested LSID";
        model.add(model.createResource(lsid), DC.title, model.createTypedLiteral(title));
        String identifier;
        try {
            identifier = reService.findLatestIdentifierByUnknown(lsid);
        } catch (RealizableEntityServiceException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving data from database: " + lsid);
        }
        model.add(model.createResource(lsid), DC.identifier, model.createTypedLiteral(identifier));

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        model.write(byteStream, LsidMetadataResponse.JENA_RDF_FORMAT);
        response.setMetadata(byteStream.toString());

        return response;
    }

    @SuppressWarnings({"ValidExternallyBoundObject"})
    public DataHandler getData(@WebParam(name = "lsid")String lsid) throws RuntimeException {
        XMLMarshaler xmlMarshaler;
        try {
            xmlMarshaler = new XMLMarshaler(SCHEMA_FILE.toString());
        } catch (JAXBException e) {
            System.err.println();
            e.printStackTrace();
            throw new RuntimeException("Error creating the XML Marshaler inside getData()");
        } catch (SAXException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating the XML Marshaler inside getData()");
        }

        StringWriter writer;
        String[] sections = lsid.split(":");
        if (sections.length < 5) {
            throw new RuntimeException("Error parsing the lsid: " + lsid);
        }
        try {
            writer = xmlMarshaler.ObjToJaxb2(sections[2], lsid);
        } catch (RealizableEntityServiceException e) {
            e.printStackTrace();
            throw new RuntimeException("Error talking to the database to retrieve the data for: " + lsid);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating the XML for: " + lsid);
        }
        if (writer == null) {
            throw new RuntimeException("No data returned for: " + lsid);
        }
        DataSource source = new ByteArrayDataSource(writer.toString().getBytes(), "application/octet-stream");
//        DataSource source = new FileDataSource( new File( "my/file" ) );

        return new DataHandler(source);
    }

}