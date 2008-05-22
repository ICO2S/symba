package net.sourceforge.symba.lsid.webservices.service;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.UUID;

/**
 * This implements the assignLsid method as required by the LSID spec:
 * http://lsids.sourceforge.net/quick-links/lsid-spec/
 * <p/>
 * see the interface for more information
 *
 * $LastChangedDate: 2008-04-24 14:35:51 +0100 (Thu, 24 Apr 2008) $
 * $LastChangedRevision: 129 $
 * $Author: allysonlister $
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/backend/src/main/java/uk/ac/cisban/symba/backend/util/CisbanHelper.java $
 */

@WebService( endpointInterface = "net.sourceforge.symba.lsid.webservices.service.LsidAssigner",
        serviceName = "LsidAssignerService" )
public class LsidAssignerImpl implements LsidAssigner {

    private static final String DEFAULT_AUTHORITY = "cisban.cisbs.org";

    private String getAccession() {
        return UUID.randomUUID().toString();
    }
    
    public String assignLSIDWithAuthority( @WebParam( name = "authority" )String authority,
                              @WebParam( name = "namespace" )String namespace ) {

        return "urn:lsid:" + authority + ":" + namespace + ":" + getAccession();
    }

    public String assignLSID( @WebParam( name = "namespace" )String namespace ) {
        return assignLSIDWithAuthority( DEFAULT_AUTHORITY, namespace );
    }
}
