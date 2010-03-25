package net.sourceforge.symba.web.server.database;

import net.sourceforge.fuge.util.generated.FuGE;
import net.sourceforge.fuge.util.generated.Person;
import net.sourceforge.fuge.util.generated.Protocol;
import net.sourceforge.fuge.util.generated.ProtocolCollection;
import net.sourceforge.fuge.util.generated.ProtocolCollectionProtocolItem;
import net.sourceforge.symba.database.controller.FugeDatabaseController;
import net.sourceforge.symba.web.client.gui.InputValidator;
import net.sourceforge.symba.web.client.stepsorter.ExperimentStep;
import net.sourceforge.symba.web.client.stepsorter.ExperimentStepHolder;
import net.sourceforge.symba.web.server.conversion.fuge.SymbaInvestigationCreator;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class provides the business logic for accessing the SymbaDao and all implementations of that class.
 * This is where annotations such as @Transactional will occur. In order for @Transactional to be effective,
 * the method that calls the transactional method MUST be external to this class.
 * <p/>
 * As objects which are lazily loaded will only be populated within the transaction they were queried from, this is
 * where all setup, processing and modification of database objects to GWT server objects will occur.
 */
public class ServerDatabaseController extends FugeDatabaseController {

    @Transactional
    @NotNull
    public HashMap<String, Investigation> convertFugeToGwt() {

        HashMap<String, Investigation> investigations = new HashMap<String, Investigation>();
        SymbaInvestigationCreator creator = new SymbaInvestigationCreator();

        List<FuGE> fugeList = symbaDao.fetchAllFuge();
        for ( FuGE fuge : fugeList ) {
            final Investigation uiInvestigation = creator.toSymbaInvestigation( fuge );
            investigations.put( uiInvestigation.getId(), uiInvestigation );
        }

        return investigations;
    }
}
