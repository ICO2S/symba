package net.sourceforge.symba.web.server.conversion.fuge;

import net.sourceforge.fuge.util.generated.Audit;
import net.sourceforge.fuge.util.generated.AuditTrail;
import net.sourceforge.fuge.util.generated.Identifiable;
import net.sourceforge.fuge.util.generated.Person;
import net.sourceforge.symba.lsid.webservices.service.LsidAssigner;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.UUID;

/**
 * Helper class to ensure the conversions to and from FuGE are all in one place, to reduce error and confusion.
 */
public class IdentifiableConverter {

    // get the context of the web services
//        ClassPathXmlApplicationContext context
//                = new ClassPathXmlApplicationContext( "net/sourceforge/symba/lsid/webservices/client/client-beans.xml" );
    // use the one below if the above doesn't work
    private static final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
            "/lsid-client-beans.xml" );

    // get an instance of the assigner class
    private static final LsidAssigner assigner = ( LsidAssigner ) context.getBean( "clientAssigner" );


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

    /**
     * @param className the class name of the object to be identified. This will form the namespace of the LSID.
     * @return If the connection to the LSID web service fails, then create a completely random number. If it succeeds,
     *         pass back an LSID.
     */
    public static String createId( String className ) {
        // go ahead and try to get an identifier
        try {
            return assigner.assignLSID( className );
        } catch ( Exception e ) {
            return UUID.randomUUID().toString();
        }
    }

}
