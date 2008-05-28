# These are simpler than trying to run them from the "java" command, as you inherit the classpath used in mvn AND
# you are using the jars created with "mvn install", so you can be sure everything's packaged correctly.

# To run specific Main's from the mvn command line, use the following commands from within the symba-lsid-ws-client
# sub-directory:

# Check the connection to the LSID Assigning WS by creating an example LSID
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.lsid.webservices.client.LsidAssignerClient"
