package net.sourceforge.symba.web.server.database;

import net.sourceforge.symba.database.dao.SymbaDao;
import net.sourceforge.symba.web.server.StorageHelper;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;
import net.sourceforge.symba.web.shared.Material;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * allows a database version of SyMBA to run
 */
public class DatabaseStorageHelper extends StorageHelper {

    ServerDatabaseController databaseController;

    public DatabaseStorageHelper() {
        super();
    }

    @NotNull
    public HashMap<String, Investigation> fetchAll() {
        // retrieve investigations from the database
        return databaseController.convertFugeToGwt();
    }

    @NotNull
    public HashMap<String, Contact> fetchAllContacts() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public HashMap<String, Material> fetchAllMaterials() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    public HashMap<String, Contact> addContact( Contact contact ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    public HashMap<String, Material> addMaterial( Material material ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    public Investigation add( @NotNull Investigation investigation ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    public ArrayList<InvestigationDetail> update( @NotNull Investigation investigation ) {
        // todo move the values in "current" to the value in "original"
        // todo copy files to new server
        // todo convert to FuGE and store in database
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    public InvestigationDetail copy( @NotNull String id ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    public ArrayList<InvestigationDetail> delete( @NotNull String id ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public String getMetadataString( @NotNull String id ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
