package net.sourceforge.symba.web.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.ArrayList;

import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetails;

@RemoteServiceRelativePath( "investigationsService" )
public interface InvestigationsService extends RemoteService {

    Investigation addInvestigation( Investigation investigation );

    Boolean deleteInvestigation( String id );

    ArrayList<InvestigationDetails> deleteInvestigations( ArrayList<String> ids );

    ArrayList<InvestigationDetails> getInvestigationDetails();

    Investigation getInvestigation( String id );

    ArrayList<InvestigationDetails> updateInvestigation( Investigation investigation );

    InvestigationDetails copyInvestigation( String id );
}
