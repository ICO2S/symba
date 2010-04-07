package net.sourceforge.symba.web.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import net.sourceforge.symba.web.client.InvestigationsService;
import net.sourceforge.symba.web.shared.ExperimentStepHolder;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;
import net.sourceforge.symba.web.shared.Material;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings( "serial" )
public class InvestigationsServiceImpl extends RemoteServiceServlet implements
        InvestigationsService {

    private final StorageHelper helper;

    public InvestigationsServiceImpl() {
        // TODO: Create a real UID on-the-fly for each contact

        // retrieve investigations from the database
        ApplicationContext context = new ClassPathXmlApplicationContext( "spring-config-web.xml" );

        // the spring config tells us which kind of helper to use.
        helper = context.getBean( "storageImplementation", StorageHelper.class );

        if ( !( helper instanceof MemoryStorageHelper ) ) {
            // we need the extra database information if it is not a MemoryStorageHelper. This is a bit of a hack
            // as the spring/jpa stuff doesn't work right in development mode.
            context = new ClassPathXmlApplicationContext( "spring-config.xml", "spring-config-web.xml" );
        }

        // todo make the fetching smarter as might not be great retrieving the entire database here!
        helper.setup( context );
        helper.fetchAll( true );
        helper.fetchAllPeople();
        helper.fetchAllMaterials();
    }

    public Investigation addInvestigation( Investigation investigation ) {
        return helper.add( investigation );
    }

    public ArrayList<InvestigationDetail> deleteInvestigation( String id ) {
        return helper.delete( id );
    }

    public ArrayList<InvestigationDetail> getInvestigationDetails() {
        return helper.getInvestigationDetails();
    }

    public Investigation getInvestigation( String id ) {
        return helper.getInvestigations().get( id );
    }

    public ArrayList<InvestigationDetail> updateInvestigation( Investigation investigation ) {
        return helper.update( investigation );
    }

    public InvestigationDetail copyInvestigation( String id ) {
        return helper.copy( id );
    }

    public ArrayList<InvestigationDetail> setInvestigationAsTemplate( String id ) {
        Investigation template = getInvestigation( id );
        template.setTemplate( true );
        for ( ExperimentStepHolder holder : template.getExperiments() ) {
            // remove file associations
            holder.clearFileAssociations();
            // set all currently-present parameters' subjects and objects as read only
            holder.setFullyWriteableParameters( false );
        }
        return getInvestigationDetails();
    }

    public HashMap<String, Contact> getAllContacts() {
        return helper.getContacts();
    }

    public HashMap<String, Contact> addContact( Contact contact ) {
        return helper.addContact( contact );
    }

    public HashMap<String, Material> getAllMaterials() {
        return helper.getMaterials();
    }

    public HashMap<String, Material> addOrUpdateMaterial( Material material ) {
        return helper.addMaterial( material );
    }

    public String getMetadata( String id ) {
        return helper.getMetadataString( id );
    }
}
