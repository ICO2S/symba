package net.sourceforge.symba.web.server.database;

import net.sourceforge.fuge.util.generated.FuGE;
import net.sourceforge.fuge.util.generated.Person;
import net.sourceforge.fuge.util.generated.Protocol;
import net.sourceforge.fuge.util.generated.ProtocolCollection;
import net.sourceforge.fuge.util.generated.ProtocolCollectionProtocolItem;
import net.sourceforge.symba.database.controller.FugeDatabaseController;
import net.sourceforge.symba.database.dao.SymbaDao;
import net.sourceforge.symba.web.client.stepsorter.ExperimentStep;
import net.sourceforge.symba.web.client.stepsorter.ExperimentStepHolder;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class provides the business logic for accessing the SymbaDao and all implementations of that class.
 * This is where annotations such as @Transactional will occur. In order for @Transactional to be effective,
 * the method that calls the transactional method MUST be external to this class.
 * <p/>
 * As objects which are lazy loading will only be populated within the transaction they were queried from, this is where
 * all setup, processing and modification of database objects to GWT server objects will occur.
 */
public class ServerDatabaseController extends FugeDatabaseController {

    @Autowired
    private SymbaDao symbaDao;

    @SuppressWarnings( { "UnusedDeclaration" } )
    public void setSymbaDao( SymbaDao symbaDao ) {
        this.symbaDao = symbaDao;
        System.err.println( "Symba connector set." );
    }

    @Transactional
    @NotNull
    public HashMap<String, Investigation> convertFugeToGwt() {

        HashMap<String, Investigation> investigations = new HashMap<String, Investigation>( );

        List<FuGE> fugeList = symbaDao.fetchAllFuge();
        for ( FuGE fuge : fugeList ) {
            Investigation investigation = new Investigation( false, fuge.getIdentifier(), fuge.getName(),
                    initProvider( fuge ), new ArrayList<ExperimentStepHolder>( ) );
//                    initProvider( fuge ), initExperiments( fuge.getProtocolCollection() ) );
            investigations.put( investigation.getId(), investigation );
        }

        return investigations;
    }

    @NotNull
    private Contact initProvider( @NotNull FuGE fuge ) {
        for ( net.sourceforge.fuge.util.generated.AuditCollectionContactItem item : fuge
                .getAuditCollection().getContactItems() ) {
            if ( item.getItemValue() instanceof Person ) {
                Person contact = ( Person ) item.getItemValue();
                if ( contact.getIdentifier().equals( fuge.getProvider().getContactRole().getContactRef() ) ) {
                    // match found to real contact value
                    // todo "Name" is the value in FuGE, so change that within the Symba UI to match
                    // todo allow people to have organisations.
                    return new Contact( contact.getIdentifier(), contact.getFirstName(), contact.getLastName(),
                            contact.getEmail() );
                }
            }
        }
        return new Contact();
        // todo create server-side ID
    }

    private ArrayList<ExperimentStepHolder> initExperiments( ProtocolCollection protocolCollection ) {

        ArrayList<ExperimentStepHolder> stepList = new ArrayList<ExperimentStepHolder>();

        buildExperimentHierarchy( stepList, protocolCollection.getProtocolItems() );

        return stepList;
    }

    private void buildExperimentHierarchy( ArrayList<ExperimentStepHolder> stepList,
                                           List<ProtocolCollectionProtocolItem> pItems ) {
        for ( ProtocolCollectionProtocolItem item : pItems ) {
            Protocol protocol = item.getItemValue();
            ExperimentStep step = new ExperimentStep();
            ExperimentStepHolder holder = new ExperimentStepHolder( step );
            // todo filenames via the associated protocol applications. Also, store pa identifier/name instead
            // todo loading parameters from the protocol.
            step.setDatabaseId( protocol.getIdentifier() );
            step.setTitle( protocol.getName() );
            // todo call recursively, adding child protocols
            stepList.add( holder );
        }
    }

}
