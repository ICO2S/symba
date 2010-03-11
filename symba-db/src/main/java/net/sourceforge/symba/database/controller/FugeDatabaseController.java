package net.sourceforge.symba.database.controller;

import net.sourceforge.fuge.util.generated.FuGE;
import net.sourceforge.symba.database.dao.SymbaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
    private SymbaDao symbaDao;

    @SuppressWarnings( { "UnusedDeclaration" } )
    public void setSymbaDao( SymbaDao symbaDao ) {
        this.symbaDao = symbaDao;
        System.err.println( "Symba connector set." );
    }

    @Transactional( readOnly = false )
    public boolean createOrAddFugeVersion( FuGE fuge ) {
        return symbaDao.addNewFugeEntry( fuge );
    }

}
