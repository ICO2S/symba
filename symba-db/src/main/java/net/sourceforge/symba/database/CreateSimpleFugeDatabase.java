package net.sourceforge.symba.database;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

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
