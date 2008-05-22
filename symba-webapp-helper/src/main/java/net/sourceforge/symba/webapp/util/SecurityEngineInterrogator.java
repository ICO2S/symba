package net.sourceforge.symba.webapp.util;

//import uk.org.carmen.security.PolicyCreationLocator;
//import uk.org.carmen.security.PolicyCreationSEI;
//import uk.org.carmen.security.WorkFlowWSLocator;
//import uk.org.carmen.security.WorkFlowWSSEI;
//
//import javax.xml.rpc.ServiceException;
//import java.rmi.RemoteException;

/**
 * This file is part of SyMBA. SyMBA is covered under the GNU Lesser General Public License (LGPL). Copyright (C) 2007
 * jointly held by Allyson Lister, Olly Shaw, and their employers. To view the full licensing information for this
 * software and ALL other files contained in this distribution, please see LICENSE.txt
 * <p/>
 * $LastChangedDate$ $LastChangedRevision$ $Author$
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/webapp-helper/src/main/java/uk/ac/cisban/symba/webapp/util/SecurityEngineInterrogator.java
 * $
 */
public class SecurityEngineInterrogator {
//    private WorkFlowWSSEI checkService;
//    private PolicyCreationSEI createService;

//    public SecurityEngineInterrogator() throws ServiceException {
//
//        WorkFlowWSLocator checkLocator = new WorkFlowWSLocator();
//        this.checkService = checkLocator.getWorkFlowWSSEIPort();
//
//        PolicyCreationLocator creationLocator = new PolicyCreationLocator();
//        this.createService = creationLocator.getPolicyCreationSEIPort();
//    }

    /**
     * creates a policy in the security database.
     *
     * @param role     The role, such as "admin" or "allUsers", that this policy will be attached to
     * @param resource The identifier, which for SyMBA is the LSID
     * @param action   Choose one of the following: Read, Update
     *
     * @return true if there is an explicit permission, or false if there is either explicit denial or no associated
     *         policy
     *
     * @throws Exception if there is a problem with getting the permission value.
     */
    public boolean hasPermission( String role, String resource, String action ) throws Exception {

        // comment this out if you have a connection to the security engine.
        return true;

//        String response_unparsed = checkService.checkWorkFlowOperation( role, resource, action );
//
//        if ( response_unparsed.contains( "<Decision>" ) ) {
//            int r1 = response_unparsed.indexOf( "<Decision>" );
//            int r2 = response_unparsed.indexOf( "</Decision>" );
//            String response_partially_parsed = response_unparsed.substring( r1, r2 );
//            int r4 = response_partially_parsed.indexOf( ">" );
//
//
//            String response = response_partially_parsed.substring( r4 + 1 );
//            if ( response.equals( "Permit" ) || ( response.equals( "Grant" ) ) ) {
//                // Authorisation Granted
//                return true;
//            } else if ( response.equals( "Deny" ) ) {
//                // Authorization Denied
//                return false;
//            } else if ( response.equals( "NotApplicable" ) ) {
//                // Related Policies Not Found. For instance, if there is no explicit permission or denial.
//                return false;
//            } else if ( response.equals( "Indeterminate" ) ) {
//                // There is a conflict: perhaps
//                throw new Exception(
//                        "Response is indeterminate: Panos has messed up the database" + response_unparsed );
//            } else {
//                throw new Exception( "The program does not understand the response: " + response_unparsed );
//            }
//        } else {
//            throw new Exception( "The program does not understand the response: " + response_unparsed );
//        }
    }

    /**
     * creates a policy in the security database.
     *
     * @param role     The role, such as "admin" or "allUsers", that this policy will be attached to
     * @param resource The identifier, which for SyMBA is the LSID
     * @param action   Choose one of the following: Read, Update
     * @param decision : "P" for Permit, or "D" to explicitly Deny
     *
     * @return true if everything's gone well in the database, and false if it hasn't updated the database
     *
     * @throws java.rmi.RemoteException if there is a problem with the creation of the policy
     */
//    public boolean createPolicy( String role, String resource, String action, String decision ) throws RemoteException {
    public boolean createPolicy( String role, String resource, String action, String decision ) {

        // comment this out if you have a connection to the security engine.
        return true;

//        return createService.updatePolicy( role, resource, action, decision );
    }

}
