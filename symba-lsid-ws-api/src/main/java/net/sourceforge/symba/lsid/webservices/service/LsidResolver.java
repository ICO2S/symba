package net.sourceforge.symba.lsid.webservices.service;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

/**
 * This implements the getAvailableServices() method as required by the LSID spec:
 * http://lsids.sourceforge.net/quick-links/lsid-spec/
 * <p/>
 * The only way this fails the spec is that the argument is a String rather than an LSID object, but none of the
 * other parts of the LSID object are used within SyMBA.
 *
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */
@WebService
public interface LsidResolver {

    /**
     * This implements the getAvailableServices() method as required by the LSID spec:
     * http://lsids.sourceforge.net/quick-links/lsid-spec/
     * but it only sends you the client-beans.xml file appropriate to the current service setup, which you can get from this
     * package. In other words, you shouldn't really need it. The spec says:
     * <p/>
     * "A fault is returned if the LSID Resolution service does not know anything about the given lsid.
     * The method returns a list in which each element represents a data retrieval service. "
     * <p/>
     * The only way this fails the spec is that the argument is a String rather than an LSID object, but none of the
     * other parts of the LSID object are used within SyMBA.
     *
     * @param lsid the lsid you wish to retrieve the data about
     * @return the set of data retrieval services that know about the data/metadata, and null if it knows nothing
     *         about the lsid
     */
    public List<LsidDataServiceResponse> getAvailableServices( @WebParam( name = "lsid" )String lsid );

}