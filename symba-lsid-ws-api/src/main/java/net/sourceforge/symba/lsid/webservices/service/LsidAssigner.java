package net.sourceforge.symba.lsid.webservices.service;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * This implements the assignLsid() method as required by the LSID spec: http://lsids.sourceforge.net/quick-links/lsid-spec/
 * <p/>
 * however all other methods are yet to be implemented.
 * <p/>
 * $LastChangedDate$ $LastChangedRevision$ $Author$
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-lsid-ws-api/src/main/java/net/sourceforge/symba/lsid/webservices/service/LsidAssigner.java
 * $
 */
@WebService
public interface LsidAssigner {

    /**
     * It returns a full LSID for a data entity that has the authority and namespace as requested by caller. Service
     * returns null if it cannot or does not want to name the object.
     * <p/>
     * This fails the spec in that the argument is a String rather than an LSID object, but none of the other parts of
     * the LSID object are used within SyMBA. It also doesn't use the property elements mentioned in the spec.
     *
     * @param authority the location of the authority, e.g. cisban.cisbs.org
     * @param namespace the namespace of the identifier. In SyMBA, it is expected that this is the class name
     * @return null if it cannot or does not want to name the object, or the lsid string otherwise
     */
    String assignLSIDWithAuthority( @WebParam( name = "authority" ) String authority,
                                    @WebParam( name = "namespace" ) String namespace );

    /**
     * Same as assignLsidWithAuthority but uses a default prefix that can be set by the SyMBA developer.
     *
     * @param namespace the namespace of the identifier. In SyMBA, it is expected that this is the class name
     * @return null if it cannot or does not want to name the object, or the lsid string otherwise
     */
    String assignLSID( @WebParam( name = "namespace" ) String namespace );
}
