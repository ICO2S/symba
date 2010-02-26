package net.sourceforge.symba.web.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import net.sourceforge.symba.database.dao.SymbaDao;
import net.sourceforge.symba.web.client.InvestigationsService;
import net.sourceforge.symba.web.client.stepsorter.ExperimentStepHolder;
import net.sourceforge.symba.web.server.database.ServerDatabaseController;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings( "serial" )
public class InvestigationsServiceImpl extends RemoteServiceServlet implements
        InvestigationsService {

    @Autowired
    private SymbaDao symbaDao;

    @SuppressWarnings( { "UnusedDeclaration" } )
    public void setSymbaDao( SymbaDao symbaDao ) {
        this.symbaDao = symbaDao;
    }

    private final HashMap<String, Investigation> investigations;

    public InvestigationsServiceImpl() {
        // TODO: Create a real UID on-the-fly for each contact

        // retrieve investigations from the database
        ApplicationContext ctxt = new ClassPathXmlApplicationContext( "spring-config.xml" );

        ServerDatabaseController controller = ctxt
                .getBean( "serverDatabaseController", ServerDatabaseController.class );

        investigations = controller.convertFugeToGwt();
    }

    public Investigation addInvestigation( Investigation investigation ) {
        investigation.setId( String.valueOf( investigations.size() ) );
        investigations.put( investigation.getId(), investigation );
        return investigation;
    }

    public ArrayList<InvestigationDetail> updateInvestigation( Investigation investigation ) {
        // todo move the values in "current" to the value in "original"
        // todo copy files to new server
        // todo convert to FuGE and store in database
        investigations.remove( investigation.getId() );
        investigations.put( investigation.getId(), investigation );
        return getInvestigationDetails();
    }

    public InvestigationDetail copyInvestigation( String id ) {
        Investigation copy = new Investigation( investigations.get( id ) );
        copy.setId( "X" + copy.getId() ); //todo need a better way to make a new id
        copy.setInvestigationTitle( "Copy of " + copy.getInvestigationTitle() );

        // the original may be a template - unset the copy as a template
        copy.setTemplate( false );
        investigations.put( copy.getId(), copy );

        return copy.getLightWeightInvestigation();
    }

    public ArrayList<InvestigationDetail> setInvestigationAsTemplate( String id ) {
        Investigation template = investigations.get( id );
        template.setTemplate( true );
        for ( ExperimentStepHolder holder : template.getExperiments() ) {
            // remove file associations
            holder.clearFileAssociations();
            // set all currently-present parameters' subjects and objects as read only
            holder.setFullyWriteableParameters( false );
        }
        return getInvestigationDetails();
    }

    public Boolean deleteInvestigation( String id ) {
        investigations.remove( id );
        return true;
    }

    //todo this method will not be allowed in future, except perhaps by admins.
    public ArrayList<InvestigationDetail> deleteInvestigations( ArrayList<String> ids ) {

        for ( String id : ids ) {
            deleteInvestigation( id );
        }

        return getInvestigationDetails();
    }

    public ArrayList<InvestigationDetail> getInvestigationDetails() {
        ArrayList<InvestigationDetail> investigationDetails = new ArrayList<InvestigationDetail>();

        for ( String s : investigations.keySet() ) {
            Investigation investigation = investigations.get( s );
            investigationDetails.add( investigation.getLightWeightInvestigation() );
        }

        return investigationDetails;
    }

    public Investigation getInvestigation( String id ) {
        return investigations.get( id );
    }
}
