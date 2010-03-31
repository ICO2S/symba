package net.sourceforge.symba.web.server.database;

import net.sourceforge.symba.database.controller.FugeDatabaseController;
import net.sourceforge.symba.web.server.StorageHelper;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;
import net.sourceforge.symba.web.shared.Material;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * allows a database version of SyMBA to run
 */
public class DatabaseStorageHelper extends StorageHelper {

    Interface2DatabaseController interface2db;
    FugeDatabaseController dbBasics;

    @Override
    public void setup( @NotNull ApplicationContext context ) {
        interface2db = context
                .getBean( "interface2db", Interface2DatabaseController.class );
        dbBasics = context.getBean( "dbBasics", FugeDatabaseController.class );
        System.err.println( "successfully created interface2db and dbBasics" );

    }

    /**
     * Retrieve all investigations from the database to initially populate the SyMBA UI. This method also clears
     * any currently-existing investigations prior to retrieving the up-to-date list from the database.
     *
     * @param addExampleIfEmpty If there are no entries at all in the database, if this value is true then an example
     *                          entry will be added.
     * @return the list of investigations to send to the client
     */
    @NotNull
    public HashMap<String, Investigation> fetchAll( boolean addExampleIfEmpty ) {
        // retrieve investigations from the database
        getInvestigations().clear();
        getInvestigations().putAll( interface2db.convertFugeToSymbaUI( dbBasics, addExampleIfEmpty ) );
        return getInvestigations();
    }

    /**
     * Retrieve all people from the database to initially populate the SyMBA UI. This method also clears
     * any currently-existing people prior to retrieving the up-to-date list from the database.
     *
     * @return the list of people (converted to UI Contacts) to send to the client
     */
    @NotNull
    public HashMap<String, Contact> fetchAllPeople() {
        getContacts().clear();
        getContacts().putAll( interface2db.convertPersonToSymbaUI( dbBasics ) );
        return getContacts();
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
