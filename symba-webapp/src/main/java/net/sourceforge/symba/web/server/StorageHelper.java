package net.sourceforge.symba.web.server;

import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;
import org.jetbrains.annotations.NotNull;

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

    protected StorageHelper() {
        investigations = new HashMap<String, Investigation>();
        contacts = new HashMap<String, Contact>();
    }

    @NotNull
    public HashMap<String, Investigation> getInvestigations() {
        return investigations;
    }

    @NotNull
    public HashMap<String, Contact> getContacts() {
        return contacts;
    }

    /**
     * Get a list of the current investigations which are viewable and editable
     *
     * @return the list of current investigations
     */
    @NotNull
    public abstract HashMap<String, Investigation> fetchAll();

    @NotNull
    public abstract HashMap<String, Contact> fetchAllContacts();

    @NotNull
    public abstract HashMap<String, Contact> addContact( Contact contact );

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

}
