package net.sourceforge.symba.web.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import net.sourceforge.symba.web.client.InvestigationsService;
import net.sourceforge.symba.web.client.stepsorter.ExperimentStepHolder;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetails;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings( "serial" )
public class InvestigationsServiceImpl extends RemoteServiceServlet implements
        InvestigationsService {

    private static final String[] contactsFirstNameData = new String[]{
            "Hollie", "Emerson", "Healy" }; //,"Brigitte", "Elba", "Claudio",
//            "Dena", "Christina", "Gail", "Orville", "Rae", "Mildred",
//            "Candice", "Louise", "Emilio", "Geneva", "Heriberto", "Bulrush",
//            "Abigail", "Chad", "Terry", "Bell" };

    private final String[] contactsLastNameData = new String[]{
            "Voss", "Milton", "Colette" }; //,"Cobb", "Lockhart", "Engle",
//            "Pacheco", "Blake", "Horton", "Daniel", "Childers", "Starnes",
//            "Carson", "Kelchner", "Hutchinson", "Underwood", "Rush", "Bouchard",
//            "Louis", "Andrews", "English", "Snedden" };

    private final String[] contactsEmailData = new String[]{
            "mark@example.com", "hollie@example.com", "boticario@example.com" };
//            ,"emerson@example.com", "healy@example.com", "brigitte@example.com",
//            "elba@example.com", "claudio@example.com", "dena@example.com",
//            "brasilsp@example.com", "parker@example.com", "derbvktqsr@example.com",
//            "qetlyxxogg@example.com", "antenas_sul@example.com",
//            "cblake@example.com", "gailh@example.com", "orville@example.com",
//            "post_master@example.com", "rchilders@example.com", "buster@example.com",
//            "user31065@example.com", "ftsgeolbx@example.com" };

    private final HashMap<String, Investigation> investigations = new HashMap<String, Investigation>();
    private final String[] investigationIdData = new String[]{ "1", "2", "3" };
    private final String[] investigationTitleData = new String[]{ "Investigation 1", "Investigation 2", "Investigation 3" };

    public InvestigationsServiceImpl() {
        initInvestigations();
    }

    private void initInvestigations() {
        // TODO: Create a real UID on-the-fly for each contact
        //
        // this is cheating a little, as we're not testing the size of the contacts* variables, but OK for testing now.
        for ( int i = 0; i < investigationIdData.length && i < investigationTitleData.length; ++i ) {
            Investigation investigation = new Investigation( investigationIdData[i], investigationTitleData[i],
                    initProvider( i ), new ArrayList<ExperimentStepHolder>() );
            investigations.put( investigation.getId(), investigation );
        }
    }

    private Contact initProvider( int testValue ) {
        return new Contact( String.valueOf( testValue ), contactsFirstNameData[testValue],
                contactsLastNameData[testValue], contactsEmailData[testValue] );
    }

    public Investigation addInvestigation( Investigation investigation ) {
        investigation.setId( String.valueOf( investigations.size() ) );
        investigations.put( investigation.getId(), investigation );
        return investigation;
    }

    public ArrayList<InvestigationDetails> updateInvestigation( Investigation investigation ) {
        // todo move the values in "current" to the value in "original"
        investigations.remove( investigation.getId() );
        investigations.put( investigation.getId(), investigation );
        return getInvestigationDetails();
    }

    public InvestigationDetails copyInvestigation( String id ) {
        Investigation copy = new Investigation( investigations.get( id ) );
        copy.setId( "X" + copy.getId() ); //todo need a better way to make a new id
        copy.setInvestigationTitle( "Copy of " + copy.getInvestigationTitle() );
        investigations.put( copy.getId(), copy );

        return copy.getLightWeightInvestigation();
    }

    public Boolean deleteInvestigation( String id ) {
        investigations.remove( id );
        return true;
    }

    //todo this method will not be allowed in future, except perhaps by admins.
    public ArrayList<InvestigationDetails> deleteInvestigations( ArrayList<String> ids ) {

        for ( String id : ids ) {
            deleteInvestigation( id );
        }

        return getInvestigationDetails();
    }

    public ArrayList<InvestigationDetails> getInvestigationDetails() {
        ArrayList<InvestigationDetails> investigationDetails = new ArrayList<InvestigationDetails>();

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
