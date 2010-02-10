package net.sourceforge.symba.web.shared;

import com.google.gwt.user.client.ui.HTML;

import java.io.Serializable;

/**
 * The JAXB-created XML code for FuGE cannot be used within the client-side GWT code. Therefore,
 * Investigation and InvestigationDetail (a summary of what's in Investigation) are used instead on
 * the client side.
 * <p/>
 * A limited version of Investigation, with just a minimal amount of data for a "summary" view without
 * having to pass huge objects around
 */
@SuppressWarnings( "serial" )
public class InvestigationDetail implements Serializable {
    private boolean template;
    private String id;
    private String investigationTitle;
    private ContactDetails providerDetails;

    public InvestigationDetail() {
        new InvestigationDetail( false, "0", investigationTitle, new ContactDetails() );
    }

    public InvestigationDetail( boolean isTemplate,
                                String id,
                                String investigationTitle,
                                ContactDetails contactDetails ) {
        this.template = isTemplate;
        this.id = id;
        this.investigationTitle = investigationTitle;
        this.providerDetails = contactDetails;
    }

    public String getId() { return id; }

    public String getInvestigationTitle() { return investigationTitle; }

    public ContactDetails getProviderDetails() {
        return providerDetails;
    }

    /**
     * Creates a simple view of the contents of the InvestigationDetail
     *
     * @return a piece of HTML that can be displayed
     */
    public HTML summarise() {
        if ( template ) {
            return new HTML(
                    "<em>" + getInvestigationTitle() + " (provided by " + getProviderDetails().getDisplayName() +
                            ")</em>" );
        } else {
            return new HTML( getInvestigationTitle() + " (provided by " + getProviderDetails().getDisplayName() + ")" );
        }
    }
}
