# These are simpler than trying to run them from the "java" command, as you inherit the classpath used in mvn AND
# you are using the jars created with "mvn install", so you can be sure everything's packaged correctly.

# To run specific Main's from the mvn command line, use the following commands from within the symba-lsid-ws-client
# sub-directory:

# Check the connection to the LSID Assigning WS by creating an example LSID
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.lsid.webservices.client.LsidAssignerClient"

# If you wish to turn off Crossfire (cxf) logging, you must first update the logging.properties file to the appropriate
# level of logging (you'll find the file in src/main/resources), and secondly tell the Java VM where the
# logging.properties file is via the following property: "-Djava.util.logging.config.file=logging.properties".
# If you use the mvn exec:java command above, it runs in the same VM as maven itself, and therefore you cannot
# successfully pass the logging property when executing the exec:java command. Therefore you have to set it in
# something like the MAVEN_OPTS variable first. For instance, you could instead run the following command (with
# the default WARNING values set in logging.properties):
MAVEN_OPTS="$MAVEN_OPTS -Djava.util.logging.config.file=logging.properties"; mvn exec:java -Dexec.mainClass="net.sourceforge.symba.lsid.webservices.client.LsidAssignerClient"

# Please note that the above command sets the MAVEN_OPTS variable for the length of the terminal session, and therefore
# subsequent exec:java commands will not require setting of MAVEN_OPTS prior to running.

