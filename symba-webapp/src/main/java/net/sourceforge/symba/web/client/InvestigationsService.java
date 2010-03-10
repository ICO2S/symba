package net.sourceforge.symba.web.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;

import java.util.ArrayList;
import java.util.HashMap;

@RemoteServiceRelativePath( "investigationsService" )
public interface InvestigationsService extends RemoteService {

    Investigation addInvestigation( Investigation investigation );

    ArrayList<InvestigationDetail> deleteInvestigation( String id );

    ArrayList<InvestigationDetail> getInvestigationDetails();

    Investigation getInvestigation( String id );

    ArrayList<InvestigationDetail> updateInvestigation( Investigation investigation );

    InvestigationDetail copyInvestigation( String id );

    ArrayList<InvestigationDetail> setInvestigationAsTemplate( String id );

    HashMap<String, Contact> getAllContacts();

    HashMap<String, Contact> addContact( Contact contact );

    String getMetadata(String id);
}
