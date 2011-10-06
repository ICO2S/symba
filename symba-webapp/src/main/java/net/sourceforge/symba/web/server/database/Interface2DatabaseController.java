package net.sourceforge.symba.web.server.database;

import net.sourceforge.fuge.util.generated.FuGE;
import net.sourceforge.fuge.util.generated.Person;
import net.sourceforge.symba.database.controller.FugeDatabaseController;
import net.sourceforge.symba.web.server.StorageHelper;
import net.sourceforge.symba.web.server.conversion.fuge.SymbaInvestigationCreator;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

/**
 * This class provides additional business logic for accessing the SymbaDao and all implementations of that class. This
 * is where annotations such as @Transactional will occur. In order for @Transactional to be effective, the method that
 * calls the transactional method MUST be external to this class.
 * <p/>
 * As objects which are lazily loaded will only be populated within the transaction they were queried from, this is
 * where all setup, processing and modification of database objects to GWT server objects will occur.
 */
public class Interface2DatabaseController {
    private static final String FUGE_ID   = "urn:lsid:cisban.cisbs.org:FuGE:843e7567-9e30-438f-a7a9-bf73ec9de597";
    private static final String FUGE_FILE = "SimpleExampleInvestigation.xml";

    @NotNull
    @Transactional( readOnly = true )
    public HashMap<String, Investigation> convertFugeToSymbaUI( @NotNull FugeDatabaseController dbBasics,
                                                                boolean addExampleIfEmpty ) {

        HashMap<String, Investigation> investigations = new HashMap<String, Investigation>();
        SymbaInvestigationCreator creator = new SymbaInvestigationCreator();

        List<FuGE> fugeList = dbBasics.fetchAll();

        if ( fugeList.isEmpty() && addExampleIfEmpty ) {
            System.err.println( "FuGE list is empty" );
            // Load an example FuGE investigation into the database, and all accompanying contacts, materials, etc.
            // (if not already present)
            if ( ! dbBasics.isFugePresent( FUGE_ID ) ) {
                // parse the example file into the FuGE object
                try {
                    JAXBContext jc = JAXBContext.newInstance( "net.sourceforge.fuge.util.generated" );

                    // create an unmarshaller
                    Unmarshaller u = jc.createUnmarshaller();
                    JAXBElement<?> genericTopLevelElement
                            = ( JAXBElement<?> ) u.unmarshal( new FileInputStream( new File( StorageHelper.class.getClassLoader()
                                                                                                                .getResource(
                                                                                                                        FUGE_FILE )
                                                                                                                .toURI() ) ) );
                    FuGE fuge = ( FuGE ) genericTopLevelElement.getValue();

                    // add the new FuGE entry
                    dbBasics.createFuge( fuge );
                    fugeList.add( fuge );
                } catch ( JAXBException e ) {
                    e.printStackTrace();
                } catch ( FileNotFoundException e ) {
                    e.printStackTrace();
                } catch ( URISyntaxException e ) {
                    e.printStackTrace();
                }
            }
        }

        for ( FuGE fugeEntry : fugeList ) {
            final Investigation uiInvestigation = creator.toSymbaInvestigation( fugeEntry );
            investigations.put( uiInvestigation.getId(), uiInvestigation );
        }

        return investigations;
    }

    @NotNull
    @Transactional( readOnly = true )
    public HashMap<String, Contact> convertPersonToSymbaUI( @NotNull FugeDatabaseController dbBasics ) {
        HashMap<String, Contact> uiPeople = new HashMap<String, Contact>();
        List<Person> dbPeople = dbBasics.fetchAllPeople();
        for ( Person dbPerson : dbPeople ) {
            Contact uiPerson = new Contact( dbPerson.getIdentifier(),
                                            dbPerson.getFirstName(),
                                            dbPerson.getLastName(),
                                            dbPerson.getEmail() );
            uiPeople.put( uiPerson.getId(), uiPerson );
        }
        return uiPeople;
    }
}