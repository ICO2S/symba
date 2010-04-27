package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.gui.InputValidator;
import net.sourceforge.symba.web.client.gui.SetupTitledText;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;

public class InvestigationDetailsPanel extends VerticalPanel {

    private final TextBox investigationTitleBox;
    private final TextArea hypothesisBox;
    private final TextArea conclusionBox;

    private final HorizontalPanel investigationTitlePanel;
    private final HorizontalPanel hypothesisPanel;
    private final HorizontalPanel conclusionPanel;
    private final ContactView contactView;

    private final SymbaController controller;

    public InvestigationDetailsPanel( SymbaController controller ) {

        this.controller = controller;

        investigationTitleBox = new TextBox();
        hypothesisBox = new TextArea();
        hypothesisBox.setCharacterWidth( 40 );
        hypothesisBox.setVisibleLines( 4 );
        conclusionBox = new TextArea();
        conclusionBox.setCharacterWidth( 40 );
        conclusionBox.setVisibleLines( 4 );

        investigationTitlePanel = new HorizontalPanel();
        hypothesisPanel = new HorizontalPanel();
        conclusionPanel = new HorizontalPanel();
        contactView = new ContactView( controller, ContactView.ViewType.WITHIN_INVESTIGATION );

        setWidth( "100%" );

        investigationTitleBox.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                InputValidator.nonEmptyTextBoxStyle( investigationTitleBox );
            }
        } );

    }

    /**
     * todo middle initial
     *
     * @param investigation the investigation to display. May be empty, but will not be null
     */
    public void createReadableDisplay( Investigation investigation ) {

        SetupTitledText.set( investigationTitlePanel, investigationTitleBox, "Investigation Title: ",
                investigation.getInvestigationTitle(), investigation.isReadOnly() );
        add( investigationTitlePanel );
        SetupTitledText.set( hypothesisPanel, hypothesisBox, "Investigation Hypothesis: ",
                investigation.getHypothesis(), investigation.isReadOnly() );
        add( hypothesisPanel );
        SetupTitledText.set( conclusionPanel, conclusionBox, "Investigation Conclusion(s): ",
                investigation.getConclusion(), investigation.isReadOnly() );
        add( conclusionPanel );

        if ( investigation.getProvider().getFullName().length() > 0 ) {
            contactView.setupNameDetailPanel( investigation.getProvider().getFullName(),
                    investigation.getProvider().getEmailAddress(), investigation.isReadOnly() );
        } else {
            // there is no provider yet - default the value to the current user
            contactView.setupNameDetailPanel( controller.getUser().getFullName(),
                    controller.getUser().getEmailAddress(), investigation.isReadOnly() );
        }
        add( contactView );

    }

    public String makeErrorMessages() {

        // there must be a nonzero value in the title (the contact behaviour is dealt with elsewhere, in
        // the ContactPopup. Return a message if empty
        String emptyValues = "\n";
        if ( investigationTitlePanel.getWidget( 1 ) instanceof TextBox &&
                investigationTitleBox.getText().length() == 0 ) {
            emptyValues += "title of investigation\n";
        }
        return emptyValues;
    }

    public boolean updateModifiedDetails( Investigation investigation ) {

        if ( investigationTitlePanel.getWidget( 1 ) instanceof TextBox &&
                investigationTitleBox.getText().length() > 0 ) {
            investigation.setInvestigationTitle( investigationTitleBox.getText() );
        }
        if ( hypothesisPanel.getWidget( 1 ) instanceof TextArea && hypothesisBox.getText().length() > 0 ) {
            investigation.setHypothesis( hypothesisBox.getText() );
        }
        if ( conclusionPanel.getWidget( 1 ) instanceof TextArea && conclusionBox.getText().length() > 0 ) {
            investigation.setConclusion( conclusionBox.getText() );
        }
        // Assign the provider to the contact whose ID is in the getValue() of the ListBox's getSelectedIndex().
        // Otherwise, it is a read-only label. Unless it is a new contact, this label will already have been stored
        // in the investigation. Store the contact either way. The information on the read-only contact
        // will be present in the fullNameBox anyway.
        String selected = contactView.getSelectedContactId();

        Contact chosen = controller.getStoredContacts().get( selected );
        if ( chosen != null ) {
            investigation.getProvider().setId( chosen.getId() );
            investigation.getProvider().setFirstName( chosen.getFirstName() );
            investigation.getProvider().setLastName( chosen.getLastName() );
            investigation.getProvider().setEmailAddress( chosen.getEmailAddress() );
            return true;
        } else {
            Window.alert( "Error updating the provider information for this investigation." );
            return false;
        }
    }
}