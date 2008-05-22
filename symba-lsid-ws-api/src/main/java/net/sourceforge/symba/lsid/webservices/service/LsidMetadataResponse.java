package net.sourceforge.symba.lsid.webservices.service;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * This implements the data structure returned by the getMetadata() methods as required by the LSID spec:
 * http://lsids.sourceforge.net/quick-links/lsid-spec/
 * The spec says:
 *
 * "The Metadata_response data structure is returned by getMetadata method. If the data retrieval service does not have
 * metadata in one of the formats requested by the accepted_formats input argument, then an exception should be raised.
 * <p/>
 * The provided_format is given as a media type just as the ones used for the accepted_formats input argument.
 * Wildcards are not allowed in the return, however. It is also not a list but a single media type specifying the type of the
 * metadata that is being returned.
 * <p/>
 * The expiration_date specifies how long the metadata is expected to be valid. The Timestamp is a string encoded as
 * defined in a W3C NOTE "Date and Time Formats" [8]. This NOTE defines a profile of ISO8601 standard [9]. ISO8601
 * describes a large number of date/time formats and the NOTE reduces the scope and restricts the supported formats to a
 * small number. The profile offers a number of options from which this specification permits the following one:
 * <p/>
 * YYYY-MM-DD (e.g., 2000-12-31)
 * <p/>
 * The Metadata_document is (usually) a string containing the metadata itself. It is considered out of the scope of this
 * specification to restrict the number of formats that the metadata can be returned in. The most popular and expected
 * formats are, however, RDF and XMI."
 *
 * In our case, only RDF is implemented.
 *
 * $LastChangedDate: 2008-04-24 14:35:51 +0100 (Thu, 24 Apr 2008) $
 * $LastChangedRevision: 129 $
 * $Author: allysonlister $
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/backend/src/main/java/uk/ac/cisban/symba/backend/util/CisbanHelper.java $
 */

public class LsidMetadataResponse {
    public static final String LSID_RDF_FORMAT = "application/rdf+xml";
    public static final String JENA_RDF_FORMAT = "RDF/XML";
    public static final SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );

    private GregorianCalendar timestamp;
    private String metadata;

    /**
     * A timestamp of null means no expiration date
     *
     * @return the expiration date
     */
    public GregorianCalendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp( GregorianCalendar timestamp ) {
        this.timestamp = timestamp;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata( String metadata ) {
        this.metadata = metadata;
    }
}