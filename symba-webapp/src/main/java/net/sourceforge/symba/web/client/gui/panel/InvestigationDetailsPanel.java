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
    private final ListBox fullNameBox;

    private final HorizontalPanel investigationTitlePanel;
    private final HorizontalPanel hypothesisPanel;
    private final HorizontalPanel conclusionPanel;
    private final HorizontalPanel contactPanel;

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
        fullNameBox = new ListBox();

        investigationTitlePanel = new HorizontalPanel();
        hypothesisPanel = new HorizontalPanel();
        conclusionPanel = new HorizontalPanel();
        contactPanel = new HorizontalPanel();

        populateNameListBox();

        setWidth( "100%" );

        investigationTitleBox.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                InputValidator.nonEmptyTextBoxStyle( investigationTitleBox );
            }
        } );

    }

    public void populateNameListBox() {
        // clear existing list
        fullNameBox.clear();

        // populate the fullNameBox with initial set of contacts
        for ( String key : controller.getStoredContacts().keySet() ) {
            // todo sort alphabetically
            fullNameBox.addItem( contactDisplayView( controller.getStoredContacts().get( key ).getFullName(),
                    controller.getStoredContacts().get( key ).getEmailAddress() ), key );
        }
    }

    private String contactDisplayView( String fullName,
                                       String emailAddress ) {
        if ( emailAddress != null && emailAddress.length() > 0 ) {
            return fullName + " (" + emailAddress + ")";
        } else {
            return fullName;
        }

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

        setupProviderNameDetailPanel( investigation.getProvider().getFullName(),
                investigation.getProvider().getEmailAddress(), investigation.isReadOnly() );
        add( contactPanel );

    }

    public void setupProviderNameDetailPanel( final String fullNameValue,
                                              final String emailAddress,
                                              boolean readOnly ) {

        // clear the contact panel.
        for ( int iii = contactPanel.getWidgetCount(); iii > 0; iii-- ) {
            contactPanel.remove( iii - 1 );
        }
        contactPanel.setSpacing( 5 );

        Label legendLabel = new Label( "Data Owner: " );
        legendLabel.addStyleName( "textbox-legend" );
        contactPanel.add( legendLabel );

        // if there is no contact at all yet for this investigation, just start with the list box. Otherwise, start
        // with a read-only string.
        if ( fullNameValue != null && fullNameValue.length() > 0 ) {
            // the read-only label for the full name
            final Label label = new Label( contactDisplayView( fullNameValue, emailAddress ) );

            if ( !readOnly ) {
                // add the behaviour to switch to a ListBox when the read-only text is clicked on
                label.addStyleName( "clickable-text" );
                label.addClickHandler( new ClickHandler() {
                    public void onClick( ClickEvent clickEvent ) {
                        contactPanel.remove( 1 ); // remove read-only label
                        // start with a list box pre-filled with the existing full name.
                        contactPanel.insert( fullNameBox, 1 );
                        fullNameBox
                                .setItemSelected( getIndexForItem( contactDisplayView( fullNameValue, emailAddress ) ),
                                        true );
                    }
                } );
            }
            contactPanel.add( label );
        } else {
            contactPanel.add( fullNameBox );
        }

        if ( !readOnly ) {
            // provide the ability to add new contacts
            Label addContact = new Label( "(add new contact)" );
            contactPanel.add( addContact );
            addContact.addStyleName( "clickable-text" );
            addContact.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    startContactPopupPanel();
                }
            } );
        }
    }

    private int getIndexForItem( String fullNameValue ) {
        for ( int iii = 0; iii < fullNameBox.getItemCount(); iii++ ) {
            if ( fullNameBox.getItemText( iii ).equals( fullNameValue ) ) {
                return iii;
            }
        }
        return 0;
    }

    private void startContactPopupPanel() {
        ContactPopup panel = new ContactPopup( controller, this );
        panel.show();
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

    public void updateModifiedDetails( Investigation investigation ) {

        if ( investigationTitlePanel.getWidget( 1 ) instanceof TextBox &&
                investigationTitleBox.getText().length() > 0 ) {
            investigation.setInvestigationTitle( investigationTitleBox.getText() );
        }
        if ( hypothesisPanel.getWidget( 1 ) instanceof TextBox && hypothesisBox.getText().length() > 0 ) {
            investigation.setHypothesis( hypothesisBox.getText() );
        }
        if ( conclusionPanel.getWidget( 1 ) instanceof TextBox && conclusionBox.getText().length() > 0 ) {
            investigation.setConclusion( conclusionBox.getText() );
        }
        // Assign the provider to the contact whose ID is in the getValue() of the ListBox's getSelectedIndex().
        // Otherwise, it is a read-only label. Unless it is a new contact, this label will already have been stored
        // in the investigation. Store the contact either way. The information on the read-only contact
        // will be present in the fullNameBox anyway.
        String selected = "";
        if ( contactPanel.getWidget( 1 ) instanceof ListBox ) {
            selected = fullNameBox.getValue( fullNameBox.getSelectedIndex() );
        } else if ( contactPanel.getWidget( 1 ) instanceof Label ) {
            // search the ListBox for a matching item value
            for ( int iii = 0; iii < fullNameBox.getItemCount(); iii++ ) {
                if ( fullNameBox.getItemText( iii ).equals( ( ( Label ) contactPanel.getWidget( 1 ) ).getText() ) ) {
                    selected = fullNameBox.getValue( iii );
                    break;
                }
            }
        }
        Contact chosen = controller.getStoredContacts().get( selected );
        if ( chosen != null ) {
            investigation.getProvider().setId( chosen.getId() );
            investigation.getProvider().setFirstName( chosen.getFirstName() );
            investigation.getProvider().setLastName( chosen.getLastName() );
            investigation.getProvider().setEmailAddress( chosen.getEmailAddress() );
        } else {
            Window.alert( "Error updating the provider information for this investigation." );
        }
    }
}