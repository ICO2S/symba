package net.sourceforge.symba.web.server;

import net.sourceforge.symba.web.shared.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * interface to allow more than one way of storing data within SyMBA
 */
public abstract class StorageHelper {

    private User currentUser;

    private HashMap<String, Investigation> investigations;

    // These are all possible users, which is not the same as all contacts listed in the investigations: there may
    // well be users in the database which are not yet part of any investigation.
    private HashMap<String, Contact> users;

    // The Materials are more than just those listed in the investigations: there may well be materials
    // in the database which are not yet part of any investigation.
    private HashMap<String, Material> materials;

    // The current subjects used within parameters - used to do things like populate SuggestBoxes.
    private HashSet<String> parameterSubjects;

    protected StorageHelper() {
        currentUser = new User();
        investigations = new HashMap<String, Investigation>();
        users = new HashMap<String, Contact>();
        materials = new HashMap<String, Material>();
        parameterSubjects = new HashSet<String>();
    }

    public abstract void setup( @NotNull ApplicationContext context );

    public User getCurrentUser() {
        return currentUser;
    }

    @NotNull
    public HashMap<String, Investigation> getInvestigations() {
        return investigations;
    }

    @NotNull
    public HashMap<String, Contact> getUsers() {
        return users;
    }

    @NotNull
    public HashMap<String, Material> getMaterials() {
        return materials;
    }

    @NotNull
    public HashSet<String> getParameterSubjects() {
        return parameterSubjects;
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
    public abstract HashMap<String, Contact> fetchAllUsers();

    @NotNull
    public abstract HashMap<String, Material> fetchAllMaterials();

    @NotNull
    public abstract HashSet<String> fetchAllParameterSubjects();

    @NotNull
    public abstract HashMap<String, Contact> addContact( Contact contact );

    /**
     * This method must be able to deal with instances where the incoming material ID is identical to one of the
     * already-present IDs. In such cases, it should update the existing material in a way appropriate to that
     * implementation of the StorageHelper. In the case of a versioned database, for example, it must create a new
     * version of the existing material.
     *
     * @param material the material to add or update
     * @return the new set of materials
     */
    @NotNull
    public abstract HashMap<String, Material> addMaterial( Material material );

    @NotNull
    public abstract Investigation add( @NotNull Investigation investigation );

    @NotNull
    public abstract ArrayList<InvestigationDetail> update( @NotNull Investigation investigation );

    @NotNull
    public abstract InvestigationDetail copy( @NotNull String id,
                                              @NotNull String contactId );

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
