# To run specific Main's from the mvn command line, use the following commands from within the backend sub-directory:

# Add official staff members to the SyMBA database
mvn exec:java -Dexec.mainClass="uk.ac.cisban.symba.backend.util.UnmarshalPeople" -Dexec.args="xml/samples/SamplePeople.xml"

# Add workflow(s) to the SyMBA database
mvn exec:java -Dexec.mainClass="uk.ac.cisban.symba.backend.util.UnmarshalWorkflow" -Dexec.args="out.txt xml/samples/SampleMicroarray.xml"

# Unload a FuGE experiment into FuGE XML
mvn exec:java -Dexec.mainClass="uk.ac.cisban.symba.backend.util.MarshalXML" -Dexec.args="../webapp/src/main/webapp/schemaFiles/FuGE_M3_test_13_07_2006.xsd your-identifier output-xml-file"

# These are simpler than trying to run them from the "java" command, as you inherit the classpath used in mvn.

# To add svn properties to a bunch of files (you may wish to change the extension)
find . \( -name '.svn' -prune \) -o -name '*.java' -exec svn propset svn:keywords "Date Rev Author HeadURL" {} \;
