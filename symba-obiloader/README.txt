# To generate a FuGE-ML OntologyCollection based around specific groupings of OBI (http://obi.sf.net) terms, use
# the following command. (obi.properties lives within src/main/resources)
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.obiloader.examples.CreateFuGEOntologyCollection" -Dexec.args="src/main/resources/obi.properties"

# you can then load the resulting XML into the datbase using the net.sourceforge.symba.util.UnmarshalOntologyCollection
# class, as you can see below. Please note that if using mvn exec:java, you need to run the following command from
# within the symba-backend directory:
mvn exec:java -Dexec.mainClass="net.sourceforge.symba.util.UnmarshalOntologyCollection" -Dexec.args="path/to/your/ontologycollectionfile.xml"
