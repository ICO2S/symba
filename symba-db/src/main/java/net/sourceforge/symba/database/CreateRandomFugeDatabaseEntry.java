package net.sourceforge.symba.database;

import net.sourceforge.fuge.util.RandomXmlGenerator;
import net.sourceforge.fuge.util.generated.FuGE;
import net.sourceforge.symba.database.controller.FugeDatabaseController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

/**
 * In order to have this class function properly, you need to pass the correct location of spring-instrument jarfile to
 * the java vm. For example, add an option similar to this one, but substituting the correct path:
 * -javaagent:/home/user/.m2/repository/org/springframework/spring-instrument/3.0.1.RELEASE/spring-instrument-3.0.1.RELEASE.jar
 */
public class CreateRandomFugeDatabaseEntry {
    public static void main( String[] args ) throws JAXBException, SAXException, FileNotFoundException {

        ApplicationContext ctxt = new ClassPathXmlApplicationContext( "spring-config.xml" );

        FugeDatabaseController controller = ctxt.getBean( "dbBasics", FugeDatabaseController.class );

        String xmlFilename = "/tmp/fugeExample" + ( ( Double ) ( Math.random() * 100 ) ).intValue() + ".xml";

        System.out.println( "Filename for this run: " + xmlFilename );

        FuGE fuge = RandomXmlGenerator.generate( xmlFilename );

        boolean response = controller.createFuge( fuge );

        if ( response ) {
            System.out.println( "Fuge Entry " + fuge.getName() + " added to database" );
        } else {
            System.out.println( "Fuge Entry " + fuge.getName() + " NOT added to database" );
        }

    }

}