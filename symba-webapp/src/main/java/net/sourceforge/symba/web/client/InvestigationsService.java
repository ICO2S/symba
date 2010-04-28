package net.sourceforge.symba.web.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;
import net.sourceforge.symba.web.shared.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@RemoteServiceRelativePath( "investigationsService" )
public interface InvestigationsService extends RemoteService {

    Investigation addInvestigation( Investigation investigation );

    ArrayList<InvestigationDetail> deleteInvestigation( String id );

    ArrayList<InvestigationDetail> getInvestigationDetails();

    Investigation getInvestigation( String id );

    ArrayList<InvestigationDetail> updateInvestigation( Investigation investigation );

    InvestigationDetail copyInvestigation( String id, String contactId );

    ArrayList<InvestigationDetail> setInvestigationAsTemplate( String id );

    HashMap<String, Contact> getAllContacts();

    HashMap<String, Contact> addOrUpdateContact( Contact contact );

    HashMap<String, Material> getAllMaterials();

    HashMap<String, Material> addOrUpdateMaterial( Material material );

    String getMetadata( String id );

    HashSet<String> getParameterSubjects();
}
