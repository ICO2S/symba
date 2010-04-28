package net.sourceforge.symba.web.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;
import net.sourceforge.symba.web.shared.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public interface InvestigationsServiceAsync {

    public void addInvestigation( Investigation investigation,
                                  AsyncCallback<Investigation> callback );

    public void deleteInvestigation( String id,
                                     AsyncCallback<ArrayList<InvestigationDetail>> callback );

    public void getInvestigationDetails( AsyncCallback<ArrayList<InvestigationDetail>> callback );

    public void getInvestigation( String id,
                                  AsyncCallback<Investigation> callback );

    public void updateInvestigation( Investigation investigation,
                                     AsyncCallback<ArrayList<InvestigationDetail>> callback );

    public void copyInvestigation( String id, String contactId,
                                   AsyncCallback<InvestigationDetail> asyncCallback );

    void setInvestigationAsTemplate( String id,
                                     AsyncCallback<ArrayList<InvestigationDetail>> async );

    void getAllContacts( AsyncCallback<HashMap<String, Contact>> async );

    void addContact( Contact contact,
                     AsyncCallback<HashMap<String, Contact>> async );

    void addOrUpdateMaterial( Material material,
                      AsyncCallback<HashMap<String, Material>> async );

    void getMetadata( String id,
                      AsyncCallback<String> async );

    void getAllMaterials( AsyncCallback<HashMap<String, Material>> async );

    void getParameterSubjects( AsyncCallback<HashSet<String>> asyncCallback );
}

