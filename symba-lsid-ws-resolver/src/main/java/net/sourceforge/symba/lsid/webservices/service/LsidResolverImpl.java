package net.sourceforge.symba.lsid.webservices.service;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;
import java.util.ArrayList;

/**
 *  
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
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
