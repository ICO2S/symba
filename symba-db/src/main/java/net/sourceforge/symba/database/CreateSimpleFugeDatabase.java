package net.sourceforge.symba.database;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

/**
 * In order to have this class function properly, you need to pass the correct location of spring-instrument jarfile to
 * the java vm. For example, add an option similar to this one, but substituting the correct path:
 * -javaagent:/home/user/.m2/repository/org/springframework/spring-instrument/3.0.1.RELEASE/spring-instrument-3.0.1.RELEASE.jar
 */
public class CreateSimpleFugeDatabase {

    public static void main( final String[] args ) {
        SimpleFugeEntriesDatabaseFiller filler = new SimpleFugeEntriesDatabaseFiller();

        try {
            filler.create();
        } catch ( JAXBException e ) {
            e.printStackTrace();
        } catch ( SAXException e ) {
            e.printStackTrace();
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }
    }

}
