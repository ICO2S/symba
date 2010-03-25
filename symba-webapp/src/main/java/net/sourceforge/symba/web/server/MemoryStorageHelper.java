package net.sourceforge.symba.web.server;

import net.sourceforge.symba.web.client.stepsorter.ExperimentStepHolder;
import net.sourceforge.symba.web.server.conversion.fuge.FugeCreator;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;
import net.sourceforge.symba.web.shared.Material;
import org.jetbrains.annotations.NotNull;

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


    /**
     * Create just one example in-memory investigation to start out with in SyMBA.
     *
     * @return the list of investigations to send to the client
     */
    @NotNull
    public HashMap<String, Investigation> fetchAll() {
        Investigation investigation = new Investigation( false, false, "12345", "My Example Investigation", ALICE,
                new ArrayList<ExperimentStepHolder>() );
        getInvestigations().put( investigation.getId(), investigation );
        return getInvestigations();
    }

    @NotNull
    public HashMap<String, Contact> fetchAllContacts() {
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

        investigation.setId( String.valueOf( getInvestigations().size() ) );
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
