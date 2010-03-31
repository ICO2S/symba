package net.sourceforge.symba.web.server;

import net.sourceforge.symba.web.shared.ExperimentStepHolder;
import net.sourceforge.symba.web.server.conversion.fuge.FugeCreator;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;
import net.sourceforge.symba.web.shared.Material;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * allows an in-memory only version of SyMBA to run.
 */
public class MemoryStorageHelper extends StorageHelper {

    private static final Contact ALICE = new Contact( "23456", "Alice", "Smith", "alice.jones@example.com" );
    private static final Contact BOB = new Contact( "013265", "Bob", "Reynolds", "bob.reynolds@example.com" );
    private static final Contact ZACH = new Contact( "9561046", "Zach", "Peters", "zach.peters@example.com" );
    private static final Material CULTURE = new Material( "ABC23456", "Cell Culture 17", "An example cell culture." );

    @Override
    public void setup( @NotNull ApplicationContext context ) {
        // no need to do anything for this method for the MemoryStorageHelper
    }

    /**
     * Resets the in-memory investigations to just one example to start out with in SyMBA, clearing any
     * currently-existing investigations.
     *
     * @param addExampleIfEmpty If there are no entries at all in the database, if this value is true then an example
     *                          entry will be added.
     * @return the list of investigations to send to the client
     */
    @NotNull
    public HashMap<String, Investigation> fetchAll( boolean addExampleIfEmpty ) {
        getInvestigations().clear();
        if ( getInvestigations().isEmpty() && addExampleIfEmpty ) {
            Investigation investigation = new Investigation( false, false, "12345", "My Example Investigation", ALICE,
                    new ArrayList<ExperimentStepHolder>() );
            add( investigation );
        }
        return getInvestigations();
    }

    /**
     * Resets the in-memory Contacts to initially populate the SyMBA UI. This method also clears
     * any currently-existing Contacts.
     *
     * @return the list of Contacts
     */
    @NotNull
    public HashMap<String, Contact> fetchAllPeople() {
        getContacts().clear();
        getContacts().put( ALICE.getId(), ALICE );
        getContacts().put( BOB.getId(), BOB );
        getContacts().put( ZACH.getId(), ZACH );
        return getContacts();
    }

    @NotNull
    @Override
    public HashMap<String, Material> fetchAllMaterials() {
        getMaterials().put( CULTURE.getId(), CULTURE );
        return getMaterials();
    }

    @NotNull
    public HashMap<String, Contact> addContact( @NotNull Contact contact ) {
        getContacts().put( contact.getId(), contact );
        return getContacts();
    }

    @NotNull
    public HashMap<String, Material> addMaterial( @NotNull Material material ) {
        getMaterials().put( material.getId(), material );
        return getMaterials();
    }

    @NotNull
    public Investigation add( @NotNull Investigation investigation ) {

        if ( investigation.getId().trim().equals( "" ) ) {
            investigation.setId( String.valueOf( getInvestigations().size() ) );
        }
        getInvestigations().put( investigation.getId(), investigation );
        return investigation;

    }

    @NotNull
    public ArrayList<InvestigationDetail> update( @NotNull Investigation investigation ) {
        // todo move the values in "current" to the value in "original"
        getInvestigations().remove( investigation.getId() );
        getInvestigations().put( investigation.getId(), investigation );
        return getInvestigationDetails();
    }

    @NotNull
    public InvestigationDetail copy( @NotNull String id ) {
        Investigation copy = new Investigation( getInvestigations().get( id ) );
        copy.setId( ( int ) ( Math.random() * 1000 ) + copy.getId() ); //todo need a better way to make a new id
        copy.setInvestigationTitle( copy.getInvestigationTitle() + " " + ( int ) ( Math.random() * 1000 ) );

        // the original may be a template - unset the copy as a template
        copy.setTemplate( false );
        getInvestigations().put( copy.getId(), copy );

        return copy.getLightWeightInvestigation();
    }

    //todo this method will not be allowed in future, except perhaps by admins.

    @NotNull
    public ArrayList<InvestigationDetail> delete( @NotNull String id ) {
        getInvestigations().remove( id );
        return getInvestigationDetails();
    }

    @NotNull
    @Override
    public String getMetadataString( @NotNull String id ) {
        FugeCreator creator = new FugeCreator();
        return creator.toFugeString( getInvestigations().get( id ) );
    }

}
