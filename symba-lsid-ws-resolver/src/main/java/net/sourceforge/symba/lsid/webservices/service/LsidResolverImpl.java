package net.sourceforge.symba.lsid.webservices.service;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;
import java.util.ArrayList;

/**
 *  
 * $LastChangedDate: 2008-04-24 14:35:51 +0100 (Thu, 24 Apr 2008) $
 * $LastChangedRevision: 129 $
 * $Author: allysonlister $
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/backend/src/main/java/uk/ac/cisban/symba/backend/util/CisbanHelper.java $
 */
@WebService( endpointInterface = "net.sourceforge.symba.lsid.webservices.service.LsidResolver",
        serviceName = "LsidResolverService" )
public class LsidResolverImpl implements LsidResolver {

    public List<LsidDataServiceResponse> getAvailableServices( @WebParam( name = "lsid" )String lsid ) {

        if ( lsid.startsWith( "urn:lsid:cisban.cisbs.org" ) ) {
            // we know about this lsid. send the list of available services, which in this case is just the default.
            LsidDataServiceResponse response = new LsidDataServiceResponse();
            List<LsidDataServiceResponse> responses = new ArrayList<LsidDataServiceResponse>();
            responses.add( response );
            return responses;
        }

        return null;
    }
}
