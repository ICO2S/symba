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
    private Contact provider;

    @SuppressWarnings( "unused" )
    public InvestigationDetail() {
        Contact contact = new Contact();
        contact.createId();
        new InvestigationDetail( false, "0", investigationTitle, contact );
    }

    public InvestigationDetail( boolean isTemplate,
                                String id,
                                String investigationTitle,
                                Contact contactDetails ) {
        this.template = isTemplate;
        this.id = id;
        this.investigationTitle = investigationTitle;
        this.provider = contactDetails;
    }

    public boolean isTemplate() {
        return template;
    }

    public String getId() { return id; }

    public String getInvestigationTitle() { return investigationTitle; }

    public Contact getProvider() {
        return provider;
    }

    /**
     * Creates a simple view of the contents of the InvestigationDetail
     *
     * @return a piece of HTML that can be displayed
     */
    public HTML summarise() {
        HTML summary = new HTML( getInvestigationTitle() + " (provided by " + getProvider().getFullName() + ")" );
        if ( template ) {
            // todo create proper css to describe templated text
            summary.setHTML( "<em>" + summary + "</em>" );
        }
        summary.addStyleName( "clickable-text" );
        return summary;
    }
}
