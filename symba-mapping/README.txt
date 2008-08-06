# These are simpler than trying to run them from the "java" command, as you inherit the classpath used in mvn AND
# you are using the jars created with "mvn install", so you can be sure everything's packaged correctly.

# To run specific Main's from the mvn command line, use the following commands from within the symba-mapping
# sub-directory:

# Add official staff members to the SyMBA database
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.UnmarshalPeople" -Dexec.args="xml/samples/SamplePeople.xml"

# Add workflow(s) to the SyMBA database
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.UnmarshalWorkflow" -Dexec.args="out.html xml/samples/SampleMicroarray.xml"
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.UnmarshalWorkflow" -Dexec.args="out.html xml/samples/SampleMicroscopy.xml"
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.UnmarshalWorkflow" -Dexec.args="out.html xml/referenceTemplates/CarmenElectrophysiology.xml"
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.UnmarshalWorkflow" -Dexec.args="out.html xml/samples/SampleMicroarray.xml xml/samples/SampleMicroscopy.xml xml/referenceTemplates/CarmenElectrophysiology.xml"

# Unload a FuGE experiment into FuGE XML
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.MarshalXML" -Dexec.args="../symba-webapp/src/main/webapp/schemaFiles/FuGE_M3_test_13_07_2006.xsd your-identifier output-xml-file"

# Write out 5 LSIDs based on the namespace of your choice
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.lsid.RetrieveLsid" -Dexec.args="GenericProtocol"

# Create usernames and passwords for the security database based on information found in an XML file formatted
# as in xml/samples/SamplePeople.xml
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.webapp.util.security.CreateUserPassAndLoad" -Dexec.args="xml/samples/SamplePeople.xml"


# To add svn properties to a bunch of files (you may wish to change the extension)
find . \( -name '.svn' -prune \) -o -name '*.java' -exec svn propset svn:keywords "Date Rev Author HeadURL" {} \;
