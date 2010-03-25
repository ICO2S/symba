package net.sourceforge.symba.web.server.database;

import net.sourceforge.fuge.util.generated.FuGE;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * allows a database version of SyMBA to run, but first populates the database with some test data.
 */
public class ExampleDatabaseStorageHelper extends DatabaseStorageHelper {

    private static final String FUGE_ID = "0.7624791808235739";
    private static final String FUGE_FILE = "/SimpleExampleInvestigation.xml";

    public ExampleDatabaseStorageHelper() {
        super();
        FuGE fuge = new FuGE();

        // Load an example FuGE investigation into the database, and all accompanying contacts, materials, etc.
        // (if not already present)
        if ( !databaseController.isFugePresent( FUGE_ID ) ) {
            // parse the example file into the FuGE object
            try {
                JAXBContext jc = JAXBContext.newInstance( "net.sourceforge.fuge.util.generated" );

                // create an unmarshaller
                Unmarshaller u = jc.createUnmarshaller();
                fuge = ( FuGE ) u.unmarshal( new FileInputStream( ExampleDatabaseStorageHelper.class.getClassLoader()
                        .getResource( FUGE_FILE ).toString() ) );
            } catch ( JAXBException e ) {
                e.printStackTrace();
            } catch ( FileNotFoundException e ) {
                e.printStackTrace();
            }

            // add the new FuGE entry
            databaseController.createFuge( fuge );
        }
    }
}