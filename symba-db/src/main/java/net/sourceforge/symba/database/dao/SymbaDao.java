package net.sourceforge.symba.database.dao;

import net.sourceforge.fuge.util.generated.FuGE;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The data access object for Symba datatypes (i.e. Fuge objects)
 */
public interface SymbaDao {

    /**
     * Look for a match in the identifier, and if there is no match with the identifier, create a new Fuge object
     * with the provided string as an identifier, and then persist that object.
     * <p/>
     * Warning: do not use the endurant as the parameter here: this deals specifically with the identifier.
     *
     * @param fugeId please note that this is the *fuge* identifier, not the database primary key (e.g. database "id")
     * @return the retrieved (or newly created and persisted, then retrieved) Fuge entry.
     */
    @NotNull
    public FuGE fetchOrMakeFuge( @NotNull String fugeId );

    /**
     * Checks to see if there is a Fuge object in the database with the provided identifier
     *
     * @param fugeId the identifier to check
     * @return true if present in the database, false otherwise
     */
    public boolean isFugePresent( @NotNull String fugeId );

    /**
     * Takes a filled Fuge object (fuge) and saves it in the database. Uses the isFugePresent(String fugeId) to
     * perform the initial check. If already present, NO CHANGE IS MADE TO THE DATABASE, as it would update an
     * existing entry and this method is only for adding a new entry. If the identifier is not present in the database,
     * the new fuge object will be loaded. Note that this a
     * basic/superficial check at the top-level identifier only: if the top-level identifier has changed, but none of
     * the underlying modified objects have had *their* identifiers reset, then those objects will be updated, and this
     * may not be the behaviour you're interested in. For instance, the SyMBA database setup packaged with this
     * project is version-based, and no updates to any objects already extant in the database should be allowed.
     * Reset your identifiers in a cascading fashion when updating or modifying an object to follow expected SyMBA
     * behaviour!
     * <p/>
     * Warning: It is assumed that all endurants and identifiers
     * have been set correctly; this means that any modified part of the Fuge object should be already set with a new
     * identifier, where appropriate. This allows for the simplest save in the database.
     *
     * @param fuge the object to store in the database
     * @return true if the fuge object was successfully added, false if there was some problem with the object and
     *         wasn't loaded
     */
    public abstract boolean addNewFugeEntry( @NotNull FuGE fuge );

    /**
     * Retrieve a list of all of the latest versions of the fuge objects in the database.
     *
     * @return all of the latest versions of the fuge objects currently in the database
     */
    @NotNull
    public List<FuGE> fetchAllFuge();

    /**
     * Retrieve a count of all of the latest versions of the fuge objects in the database
     *
     * @return the number of the latest versions of the fuge objects in the database
     */
    public int countAllFuge();

}
