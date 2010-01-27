package net.sourceforge.symba.web.shared;

import java.io.Serializable;

/**
 * The JAXB-created XML code for FuGE cannot be used within the client-side GWT code. Therefore,
 * Investigation and InvestigationDetails (a summary of what's in Investigation) are used instead on
 * the client side.
 * <p/>
 * A limited version of Investigation, with just a minimal amount of data for a "summary" view without
 * having to pass huge objects around
 */
@SuppressWarnings( "serial" )
public class InvestigationDetails implements Serializable {
    private String id;
    private String investigationTitle;
    private ContactDetails providerDetails;

    public InvestigationDetails() {
        new InvestigationDetails( "0", investigationTitle, new ContactDetails() );
    }

    public InvestigationDetails( String id,
                                 String investigationTitle,
                                 ContactDetails contactDetails ) {
        this.id = id;
        this.investigationTitle = investigationTitle;
        this.providerDetails = contactDetails;
    }

    public String getId() { return id; }

    public void setId( String id ) { this.id = id; }

    public String getInvestigationTitle() { return investigationTitle; }

    public void setInvestigationTitle( String investigationTitle ) { this.investigationTitle = investigationTitle; }

    public ContactDetails getProviderDetails() {
        return providerDetails;
    }

    public void setProviderDetails( ContactDetails providerDetails ) {
        this.providerDetails = providerDetails;
    }

    public String summarise() {
        return getInvestigationTitle() + " (provided by " + getProviderDetails().getDisplayName() + ")";
    }
}
