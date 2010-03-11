package net.sourceforge.symba.database.dao;

import net.sourceforge.fuge.util.generated.FuGE;
import org.jetbrains.annotations.NotNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the SymbaDao using JPA
 */

public class PostgresSymbaDaoImpl implements SymbaDao {

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager( EntityManager entityManager ) {
        this.entityManager = entityManager;
        //entityManager.setFlushMode(FlushModeType.COMMIT);
    }

    private Map<String, FuGE> fugeByFugeIdCache = new HashMap<String, FuGE>();

    @NotNull
    public FuGE fetchOrMakeFuge( @NotNull String fugeId ) {

        FuGE fuge = fugeByFugeIdCache.get( fugeId );

        if ( fuge == null ) {
            System.out.print( "-" );
            fuge = ( FuGE ) entityManager.createNamedQuery( "fugeByIdentifier" )
                    .setParameter( "fugeId", fugeId ).getSingleResult();
            if ( fuge == null ) {
                fuge = new FuGE();
                fuge.setIdentifier( fugeId );
                System.err.println( "Persisting a new Fuge object with id " + fugeId );
                // todo set other default values, as you do elsewhere: audit, endurant, etc.
                entityManager.persist( fuge );
//            tick();
            }

            fugeByFugeIdCache.put( fugeId, fuge );
        } else {
            System.out.print( "+" );
        }

        return fuge;
    }

    public boolean isFugePresent( @NotNull String fugeId ) {

        List<String> endurant = ( List<String> ) entityManager.createNamedQuery( "fugeEndurantByIdentifier" )
                .setParameter( "fugeId", fugeId ).getResultList();
        return !( endurant.isEmpty() );
    }

    public boolean addNewFugeEntry( @NotNull FuGE fuge ) {
        if ( fuge.getIdentifier() == null || fuge.getIdentifier().length() == 0 ) {
            return false;
        }

        if ( isFugePresent( fuge.getIdentifier() ) ) {
            return false;
        }
        entityManager.persist( fuge );
        fugeByFugeIdCache.put( fuge.getIdentifier(), fuge );

        return true;
    }

    @NotNull
    public List<FuGE> fetchAllFuge() {
        return entityManager.createNamedQuery( "allFuge" ).getResultList();
    }

    public int countAllFuge() {
        return ( Integer ) entityManager.createNamedQuery( "countAllFuge" ).getSingleResult();
    }

}
