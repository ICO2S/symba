package net.sourceforge.symba.web.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;

import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetails;

public interface InvestigationsServiceAsync {

    public void addInvestigation( Investigation investigation,
                            AsyncCallback<Investigation> callback );

    public void deleteInvestigation( String id,
                               AsyncCallback<Boolean> callback );

    public void deleteInvestigations( ArrayList<String> ids,
                                AsyncCallback<ArrayList<InvestigationDetails>> callback );

    public void getInvestigationDetails( AsyncCallback<ArrayList<InvestigationDetails>> callback );

    public void getInvestigation( String id,
                            AsyncCallback<Investigation> callback );

    public void updateInvestigation( Investigation investigation,
                               AsyncCallback<ArrayList<InvestigationDetails>> callback );

    public void copyInvestigation( String id,
                            AsyncCallback<InvestigationDetails> asyncCallback );
}

