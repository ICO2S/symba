package net.sourceforge.symba.web.server.conversion.fuge;

import net.sourceforge.fuge.util.generated.Audit;
import net.sourceforge.fuge.util.generated.AuditTrail;
import net.sourceforge.fuge.util.generated.Identifiable;
import net.sourceforge.fuge.util.generated.Person;

import java.util.Date;

/**
 * Helper class to ensure the conversions to and from FuGE are all in one place, to reduce error and confusion.
 */
public class IdentifiableConverter {

    public static Identifiable toFuge( Identifiable identifiable, String name, String identifier, String endurant ) {
        identifiable.setName( name );
        identifiable.setIdentifier( identifier );
        identifiable.setEndurantRef( endurant );

        return identifiable;
    }

    public static void addAuditTrail( Identifiable identifiable, Person person ) {
        AuditTrail trail = new AuditTrail();
        Audit item = new Audit();
        item.setContactRef( person.getIdentifier() );
        item.setDateItem( new Date() );
        item.setAction( "CREATE" );
        trail.getAudit().add( item );

        identifiable.setAuditTrail( trail );
    }

    public static String createId() {
        return ( ( Double ) Math.random() ).toString();
    }

}
