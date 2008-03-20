package uk.org.carmen.security;

public class SecurityCheck {

    public SecurityCheck() {

    }

    public static void main( String[] args ) {
        SecurityCheck work = new SecurityCheck();
        work.checkResource();
        //work.addAccess ();
    }

    public void addAccess() {
        try {
            // Create the locator for the webservice interface.
            //
            PolicyCreationLocator locator = new PolicyCreationLocator();
            PolicyCreationSEI service = locator.getPolicyCreationSEIPort();

            // Update a policy
            //
            String role = "demo";
            String resource = "urn:lsid:carmen.org:file:1";
            String verb = "Read";
            String access = "Grant";
            boolean answer = service.updatePolicy( role, resource, verb, access );

            System.out.println( "UpdatePolicy: " + answer );
        }
        catch ( Exception e ) {

        }
    }

    public void checkResource() {
        try {
            // Create the locator to get the webservice interface.
            //
            WorkFlowWSLocator locator = new WorkFlowWSLocator();
            //WorkFlowWSSEI secService  = locator.getWorkFlowWSSEIPort (new URL ("http://localhost:8080/CARMENWS/WorkFlowWS"));
            WorkFlowWSSEI secService = locator.getWorkFlowWSSEIPort();

            // Chcek the role, action, resource
            //
            String role = "demo";
            String action = "Read";
            String resource = "urn:lsid:carmen.org:file:1";

            // response_unparsed="<response>Permit</response>"; //replace this string with the web service response
            String response_unparsed = secService.checkWorkFlowOperation( role, resource, action );
            //boolean answer = secService.checkWorkFlow (role, action, resource);

            if ( response_unparsed.contains( "<Decision>" ) ) {
                int r1 = response_unparsed.indexOf( "<Decision>" );
                int r2 = response_unparsed.indexOf( "</Decision>" );
                String response_partially_parsed = response_unparsed.substring( r1, r2 );
                int r4 = response_partially_parsed.indexOf( ">" );


                String response = response_partially_parsed.substring( r4 + 1 );
                if ( response.equals( "Permit" ) || ( response.equals( "Grant" ) ) ) {
                    System.out.println( "Authorisation Granted" );
                } else if ( response.equals( "Deny" ) ) {
                    System.out.println( "Authorisation Denied" );
                } else if ( response.equals( "NotApplicable" ) ) {
                    System.out.println( "Related Policies Not Found" );
                } else if ( response.equals( "Intederminate" ) ) {
                    System.out.println( "Panos has messed up the database" );
                } else {
                    System.out.println( "The program does not understand the response: " + response_unparsed );
                }
            } else {
                System.out.println( "The program does not understand the response: " + response_unparsed );
            }

        }
        catch ( Exception e ) {
            System.out.println( e );
            e.printStackTrace();
        }
    }
}