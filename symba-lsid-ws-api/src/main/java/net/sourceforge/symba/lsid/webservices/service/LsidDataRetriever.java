package net.sourceforge.symba.lsid.webservices.service;

import javax.activation.DataHandler;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlMimeType;

/**
 * This implements the getData() and getMetadata() methods as required by the LSID spec:
 * http://lsids.sourceforge.net/quick-links/lsid-spec/ however the getDataByRange() and the getMetadataSubset() are yet
 * to be implemented. The spec says:
 * <p/>
 * "The data retrieval services implement the following methods:
 * <p/>
 * bytes getData (LSID lsid) bytes getDataByRange (LSID lsid, integer start, integer length) Metadata_response
 * getMetadata (LSID lsid, string[] accepted_formats) Metadata_response getMetadataSubset (LSID lsid, string[]
 * accepted_formats, string selector)
 * <p/>
 * The data retrieval services may implement all of the methods, or only methods for retrieving data, or only methods
 * for retrieving associated metadata.
 * <p/>
 * The same LSID named data object must be resolved always to the same set of bytes. Therefore, all of the data
 * retrieval services return the same results for the same LSID. The user has, however, the choice of which one of these
 * to utilize depending on its location, known quality of service and other attributes. With metadata, the situation is
 * different. Each data retrieval service can provide different metadata for the same LSID."
 * <p/>
 * The only way this fails the spec is that the argument is a String rather than an LSID object, but none of the other
 * parts of the LSID object are used within SyMBA.
 * <p/>
 * $LastChangedDate$ $LastChangedRevision$ $Author$
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-lsid-ws-api/src/main/java/net/sourceforge/symba/lsid/webservices/service/LsidDataRetriever.java
 * $
 */
@WebService
public interface LsidDataRetriever {

    /**
     * From the lsid spec:
     * <p/>
     * "This method is used to return document containing the metadata associated with a particular lsid at this
     * particular data retrieval service. Note that this means that calling getMetadata on two different data services
     * may yield different metadata since each service may contain different metadata about the same lsid.
     * <p/>
     * Metadata can be returned in multiple formats. The accepted_formats argument is an array of strings, each of them
     * contains a media type."
     * <p/>
     * SyMBA only accepts LsidMetadataResponse.LSID_RDF_FORMAT
     *
     * @param lsid            the lsid to get the metadata on
     * @param acceptedFormats the formats the user can accept
     * @return null if not passed an acceptable format, empty class if no information, otherwise the metadata from the
     *         given lsid
     */
    LsidMetadataResponse getMetadata( @WebParam( name = "lsid" ) String lsid,
                                      @WebParam( name = "acceptedFormats" ) String[] acceptedFormats );

    /**
     * From the lsid spec: "This method is used to return data associated with the given lsid. If a copy of the data
     * represented by an LSID cannot be returned for any reason, an exception should be raised.
     * <p/>
     * If the given lsid represents an abstract entity (a concept), this method returns an empty array of bytes. Note
     * that the semantics of the returned bytes is not defined by this specification. It is either known from an
     * external documentation, or (preferably) it is available by reading the metadata for this particular lsid."
     *
     * @param lsid the lsid to get the data on
     * @return the data, otherwise a RuntimeException is raised
     * @throws RuntimeException if a copy of the data represented by an LSID cannot be returned for any reason.
     */
    @XmlMimeType( "application/octet-stream" )
    DataHandler getData( @WebParam( name = "lsid" ) String lsid ) throws RuntimeException;

}