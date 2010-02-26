package net.sourceforge.symba.web.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;

import java.util.ArrayList;

public interface InvestigationsServiceAsync {

    public void addInvestigation( Investigation investigation,
                                  AsyncCallback<Investigation> callback );

    public void deleteInvestigation( String id,
                                     AsyncCallback<Boolean> callback );

    public void deleteInvestigations( ArrayList<String> ids,
                                      AsyncCallback<ArrayList<InvestigationDetail>> callback );

    public void getInvestigationDetails( AsyncCallback<ArrayList<InvestigationDetail>> callback );

    public void getInvestigation( String id,
                                  AsyncCallback<Investigation> callback );

    public void updateInvestigation( Investigation investigation,
                                     AsyncCallback<ArrayList<InvestigationDetail>> callback );

    public void copyInvestigation( String id,
                                   AsyncCallback<InvestigationDetail> asyncCallback );

    void setInvestigationAsTemplate( String id,
                                     AsyncCallback<ArrayList<InvestigationDetail>> async );
}

