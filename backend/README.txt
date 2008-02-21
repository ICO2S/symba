# To run specific Main's from the mvn command line, use the following commands from within the backend sub-directory:

# Add official staff members to the SyMBA database
mvn exec:java -Dexec.mainClass="uk.ac.cisban.symba.backend.util.UnmarshalPeople" -Dexec.args="xml/samples/SamplePeople.xml"

# Add workflow(s) to the SyMBA database
mvn exec:java -Dexec.mainClass="uk.ac.cisban.symba.backend.util.UnmarshalWorkflow" -Dexec.args="out.html xml/samples/SampleMicroarray.xml"
mvn exec:java -Dexec.mainClass="uk.ac.cisban.symba.backend.util.UnmarshalWorkflow" -Dexec.args="out.html xml/samples/SampleMicrscopy.xml"
mvn exec:java -Dexec.mainClass="uk.ac.cisban.symba.backend.util.UnmarshalWorkflow" -Dexec.args="out.html xml/referenceTemplates/CarmenElectrophysiology.xml"
 
# Unload a FuGE experiment into FuGE XML
mvn exec:java -Dexec.mainClass="uk.ac.cisban.symba.backend.util.MarshalXML" -Dexec.args="../webapp/src/main/webapp/schemaFiles/FuGE_M3_test_13_07_2006.xsd your-identifier output-xml-file"

# Write out 5 LSIDs based on the namespace of your choice
mvn exec:java -Dexec.mainClass="uk.ac.cisban.symba.backend.util.RetrieveLsid" -Dexec.args="GenericProtocol"

# These are simpler than trying to run them from the "java" command, as you inherit the classpath used in mvn.

# To add svn properties to a bunch of files (you may wish to change the extension)
find . \( -name '.svn' -prune \) -o -name '*.java' -exec svn propset svn:keywords "Date Rev Author HeadURL" {} \;
