package uk.ac.cisban.symba.webapp.util;

import uk.org.carmen.security.WorkFlowWSLocator;
import uk.org.carmen.security.WorkFlowWSSEI;

import javax.xml.rpc.ServiceException;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * $LastChangedDate: 2007-09-09 09:25:30 +0100 (Sun, 09 Sep 2007) $
 * $LastChangedRevision: 4 $
 * $Author: allysonlister $
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/webapp/src/main/java/uk/ac/cisban/symba/webapp/util/ScpBean.java $
 */
public class SecurityEngineInterrogator {
    private WorkFlowWSLocator locator;
    private WorkFlowWSSEI secService;

    public SecurityEngineInterrogator() throws ServiceException {

        this.locator = new WorkFlowWSLocator();
        // WorkFlowWSSEI secService  = locator.getWorkFlowWSSEIPort (new URL ("http://localhost:8080/CARMENWS/WorkFlowWS"));
        this.secService = this.locator.getWorkFlowWSSEIPort();
    }

    public boolean hasPermission( String role, String resource, String action ) throws Exception {

        if ( true ) {
            return true;
        }

        String response_unparsed = this.secService.checkWorkFlowOperation( role, resource, action );

        if ( response_unparsed.contains( "<Decision>" ) ) {
            int r1 = response_unparsed.indexOf( "<Decision>" );
            int r2 = response_unparsed.indexOf( "</Decision>" );
            String response_partially_parsed = response_unparsed.substring( r1, r2 );
            int r4 = response_partially_parsed.indexOf( ">" );


            String response = response_partially_parsed.substring( r4 + 1 );
            if ( response.equals( "Permit" ) || ( response.equals( "Grant" ) ) ) {
                // Authorisation Granted
                return true;
            } else if ( response.equals( "Deny" ) ) {
                // Authorization Denied
                return false;
            } else if ( response.equals( "NotApplicable" ) ) {
                // Related Policies Not Found
                throw new Exception(
                        "Response is NotApplicable: Related Polices have not been found" + response_unparsed );
            } else if ( response.equals( "Intederminate" ) ) {
                // There is a conflict: perhaps
                throw new Exception(
                        "Response is indeterminate: Panos has messed up the database" + response_unparsed );
            } else {
                throw new Exception( "The program does not understand the response: " + response_unparsed );
            }
        } else {
            throw new Exception( "The program does not understand the response: " + response_unparsed );
        }
    }
}
