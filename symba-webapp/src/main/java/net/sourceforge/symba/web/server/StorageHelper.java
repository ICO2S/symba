package net.sourceforge.symba.web.server;

import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;
import net.sourceforge.symba.web.shared.Material;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * interface to allow more than one way of storing data within SyMBA
 */
public abstract class StorageHelper {

    private HashMap<String, Investigation> investigations;

    // The Contacts are more than just those contacts listed in the investigations: there may well be contacts
    // in the database which are not yet part of any investigation.
    private HashMap<String, Contact> contacts;

    // The Materials are more than just those listed in the investigations: there may well be materials
    // in the database which are not yet part of any investigation.
    private HashMap<String, Material> materials;

    protected StorageHelper() {
        investigations = new HashMap<String, Investigation>();
        contacts = new HashMap<String, Contact>();
        materials = new HashMap<String, Material>();
    }

    public abstract void setup( @NotNull ApplicationContext context );

    @NotNull
    public HashMap<String, Investigation> getInvestigations() {
        return investigations;
    }

    @NotNull
    public HashMap<String, Contact> getContacts() {
        return contacts;
    }

    @NotNull
    public HashMap<String, Material> getMaterials() {
        return materials;
    }

    /**
     * Get a list of the current investigations which are viewable and editable
     *
     * @param addExampleIfEmpty If there are no entries at all in the database, if this value is true then an example
     *                          entry should be added.
     * @return the list of current investigations
     */
    @NotNull
    public abstract HashMap<String, Investigation> fetchAll( boolean addExampleIfEmpty );

    @NotNull
    public abstract HashMap<String, Contact> fetchAllPeople();

    @NotNull
    public abstract HashMap<String, Material> fetchAllMaterials();

    @NotNull
    public abstract HashMap<String, Contact> addContact( Contact contact );

    @NotNull
    public abstract HashMap<String, Material> addMaterial( Material material );

    @NotNull
    public abstract Investigation add( @NotNull Investigation investigation );

    @NotNull
    public abstract ArrayList<InvestigationDetail> update( @NotNull Investigation investigation );

    @NotNull
    public abstract InvestigationDetail copy( @NotNull String id );

    @NotNull
    public abstract ArrayList<InvestigationDetail> delete( @NotNull String id );

    @NotNull
    public ArrayList<InvestigationDetail> getInvestigationDetails() {
        ArrayList<InvestigationDetail> investigationDetails = new ArrayList<InvestigationDetail>();

        for ( String s : investigations.keySet() ) {
            Investigation investigation = investigations.get( s );
            investigationDetails.add( investigation.getLightWeightInvestigation() );
        }

        return investigationDetails;
    }

    @NotNull
    public abstract String getMetadataString( @NotNull String id );
}
