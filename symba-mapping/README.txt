# These are simpler than trying to run them from the "java" command, as you inherit the classpath used in mvn AND
# you are using the jars created with "mvn install", so you can be sure everything's packaged correctly.

# To run specific Main's from the mvn command line, use the following commands from within the symba-mapping
# sub-directory:

# Add official staff members to the SyMBA database
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.UnmarshalPeople" -Dexec.args="xml/samples/SamplePeople.xml"

# Add a set of ontology terms to the SyMBA database
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.UnmarshalOntologyCollection" -Dexec.args="xml/referenceTemplates/MiMage-Terms.xml"

# Add workflow(s) to the SyMBA database
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.UnmarshalWorkflow" -Dexec.args="xml/samples/SampleMicroarray.xml"
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.UnmarshalWorkflow" -Dexec.args="xml/samples/SampleMicroscopy.xml"
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.UnmarshalWorkflow" -Dexec.args="xml/referenceTemplates/CarmenElectrophysiology.xml"
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.UnmarshalWorkflow" -Dexec.args="xml/samples/SampleMicroarray.xml xml/samples/SampleMicroscopy.xml xml/samples/SampleMicroarray.xml xml/referenceTemplates/CarmenElectrophysiology.xml xml/referenceTemplates/MiMage-PCR.xml"

# Unload a FuGE experiment into FuGE XML
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.MarshalXML" -Dexec.args="../symba-jaxb2/src/main/resources/xmlSchema.xsd your-identifier output-xml-file"

# Write out 5 LSIDs based on the namespace of your choice
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.lsid.RetrieveLsid" -Dexec.args="GenericProtocol"

# Create usernames and passwords for the security database based on information found in an XML file formatted
# as in symba-mapping/xml/samples/SamplePeople.xml. Run from symba-webapp-helper
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.webapp.util.security.CreateUserPassAndLoad" -Dexec.args="../symba-mapping/xml/samples/SamplePeople.xml"

# Create usernames and passwords for the security database based on information found in an username/password file
# in format "username	password	endurant". Run from symba-webapp-helper
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.webapp.util.security.LoadUserPass" -Dexec.args="input.txt"

# Create an OntologyCollection in FuGE-ML, without modifying the database, using an input file that's much simpler in
# structure to the XML. Run from the symba-mapping subdirectory
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.GenerateOntologyIndividuals" -Dexec.args="../symba-jaxb2/src/main/resources/xmlSchema.xsd input-list outputOntologyCollection.xml OntologySourceName"

# Create an OntologyCollection in FuGE-ML, without modifying the database, using an input file that's much simpler in
# structure to the XML. Run from the symba-mapping subdirectory. This version allows the creation of LSIDs as well as
# provides the ability to pass both term label and name.
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.GenerateLsidOntologyIndividuals" -Dexec.args="../symba-jaxb2/src/main/resources/xmlSchema.xsd input-list outputOntologyCollection.xml OntologySourceName"

# To add svn properties to a bunch of files (you may wish to change the extension)
find . \( -name '.svn' -prune \) -o -name '*.java' -exec svn propset svn:keywords "Date Rev Author HeadURL" {} \;
