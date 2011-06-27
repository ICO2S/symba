package net.sourceforge.symba.database.controller;

import net.sourceforge.fuge.util.generated.FuGE;
import net.sourceforge.fuge.util.generated.Person;
import net.sourceforge.symba.database.dao.SymbaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This class provides the business logic for accessing the SymbaDao and all implementations of that class.
 * This is where annotations such as @Transactional will occur. In order for @Transactional to be effective,
 * the method that calls the transactional method MUST be external to this class.
 * <p/>
 * As objects which are lazy loading will only be populated within the transaction they were queried from, this is where
 * all setup, processing and modification of database objects for other classes in this module will occur.
 */
public class FugeDatabaseController {

    @Autowired
    protected SymbaDao symbaDao;

    @Transactional( readOnly = false )
    public boolean createFuge( FuGE fuge ) {
        return symbaDao.addNewFugeEntry( fuge );
    }

    @Transactional( readOnly = true )
    public boolean isFugePresent( String fugeIdentifier ) {
        return symbaDao.isFugePresent( fugeIdentifier );
    }

    @Transactional( readOnly = true )
    public List<FuGE> fetchAll() {
        return symbaDao.fetchAllFuge();
    }

    @Transactional( readOnly = true )
    public List<Person> fetchAllPeople() {
        return symbaDao.fetchAllPeople();
    }
}
