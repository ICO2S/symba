package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.InvestigationsServiceAsync;
import net.sourceforge.symba.web.client.gui.InputValidator;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;

import java.util.HashMap;

public class InvestigationDetailsPanel extends VerticalPanel {

    private final TextBox investigationIdBox;
    private final TextBox investigationTitleBox;
    private final TextBox providerIdBox;
    private final ListBox fullNameBox;

    private final HorizontalPanel investigationTitlePanel;
    private final HorizontalPanel contactPanel;

    private final HashMap<String, Contact> contacts;
    private final InvestigationsServiceAsync rpcService;

    public InvestigationDetailsPanel( HashMap<String, Contact> allContacts,
                                  InvestigationsServiceAsync rpcService ) {

        investigationIdBox = new TextBox();
        investigationTitleBox = new TextBox();
        providerIdBox = new TextBox();
        fullNameBox = new ListBox();

        investigationTitlePanel = new HorizontalPanel();
        contactPanel = new HorizontalPanel();

        this.rpcService = rpcService;

        contacts = allContacts;
        populateNameListBox();

        setWidth( "100%" );

        investigationIdBox.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                InputValidator.nonEmptyTextBoxStyle( investigationIdBox );
            }
        } );

        investigationTitleBox.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                InputValidator.nonEmptyTextBoxStyle( investigationTitleBox );
            }
        } );

        providerIdBox.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                InputValidator.nonEmptyTextBoxStyle( providerIdBox );
            }
        } );

        fullNameBox.addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                // the change handler will only be called if the ListBox is present, which only happens if it is
                // not a template investigation.

                // When the value changes, update the other contact details
                setLinkedProviderInformation(
                        contacts.get( fullNameBox.getValue( fullNameBox.getSelectedIndex() ) ).getEmailAddress() );
            }
        } );

    }

    public void populateNameListBox() {
        // clear existing list
        fullNameBox.clear();

        // populate the fullNameBox with initial set of contacts
        for ( String key : contacts.keySet() ) {
            // todo sort alphabetically
            fullNameBox.addItem( contacts.get( key ).getFullName(), key );
        }
    }

    /**
     * Although we store first and last name separately, we use a GWT Oracle to ensure that, if it's already in
     * the database, the correct contact is chosen. Therefore it's only when creating an editable display
     * that the first and last names are shown separately.
     * <p/>
     * todo middle initial
     *
     * @param investigation the investigation to display. May be empty, but will not be null
     */
    public void createReadableDisplay( Investigation investigation ) {

        setupDetailPanel( investigationTitlePanel, investigationTitleBox, "Investigation Title: ",
                investigation.getInvestigationTitle(), investigation.isReadOnly() );
        add( investigationTitlePanel );

        setupProviderNameDetailPanel( investigation.getProvider().getFullName(),
                investigation.getProvider().getEmailAddress(), investigation.isReadOnly() );
        add( contactPanel );

    }

    private void setupDetailPanel( final HorizontalPanel panel,
                                   final TextBox box,
                                   String legend,
                                   String value,
                                   boolean readOnly ) {

        // clear the panel.
        for ( int iii = panel.getWidgetCount(); iii > 0; iii-- ) {
            panel.remove( iii - 1 );
        }

        panel.setSpacing( 5 );
        Label legendLabel = new Label( legend );
        legendLabel.addStyleName( "textbox-legend" );
        panel.add( legendLabel );

        if ( value != null && value.length() > 0 ) {
            final Label label = new Label();
            label.setText( value );
            if ( !readOnly ) {
                label.addStyleName( "clickable-text" );
                label.addClickHandler( new ClickHandler() {
                    public void onClick( ClickEvent clickEvent ) {
                        panel.remove( 1 ); // remove existing label widget
                        box.setText( label.getText() );
                        panel.add( box ); // add the write widget
                        box.setFocus( true );
                    }
                } );
            }
            panel.add( label );
        } else {
            // there is no value at all yet for the investigation detail. Put in empty box
            box.setText( "" );
            panel.add( box ); // add the write widget
        }
    }

    public void setupProviderNameDetailPanel( final String fullNameValue,
                                              String emailAddress,
                                              boolean readOnly ) {

        // clear the contact panel.
        for ( int iii = contactPanel.getWidgetCount(); iii > 0; iii-- ) {
            contactPanel.remove( iii - 1 );
        }
        contactPanel.setSpacing( 5 );

        Label legendLabel = new Label( "Name: " );
        legendLabel.addStyleName( "textbox-legend" );
        contactPanel.add( legendLabel );

        // if there is no contact at all yet for this investigation, just start with the list box. Otherwise, start
        // with a read-only string.
        if ( fullNameValue != null && fullNameValue.length() > 0 ) {
            // add the read-only text
            // the read-only label for the full name
            final Label label = new Label();
            label.setText( fullNameValue );

            if ( !readOnly ) {
                // add the behaviour to switch to a listbox when the read-only text is clicked on
                label.addStyleName( "clickable-text" );
                label.addClickHandler( new ClickHandler() {
                    public void onClick( ClickEvent clickEvent ) {
                        contactPanel.remove( 1 ); // remove read-only label
                        // start with a list box pre-filled with the existing full name.
                        contactPanel.insert( fullNameBox, 1 );
                        fullNameBox.setItemSelected( getIndexForItem( fullNameValue ), true );
                    }
                } );
            }
            contactPanel.add( label );
        } else {
            contactPanel.add( fullNameBox );
            emailAddress = contacts.get( fullNameBox.getValue( 0 ) ).getEmailAddress();
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

        // add associated read-only email information last
        setLinkedProviderInformation( emailAddress );

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
        ContactPopup panel = new ContactPopup( contacts, this, rpcService );
        panel.show();
    }

    private void setLinkedProviderInformation( String emailAddress ) {

        String legend = "Email address: ";

        if ( contactPanel.getWidgetCount() >= 2 ) {
            int lastIndex = contactPanel.getWidgetCount() - 1;
            for ( int iii = lastIndex; iii >= 0; iii-- ) {
                Widget widget = contactPanel.getWidget( iii );
                if ( widget instanceof Label && ( ( Label ) widget ).getText().equals( legend ) ) {
                    contactPanel.remove( iii + 1 );
                    contactPanel.remove( iii );
                    break;

                }
            }
        }

        Label legendLabel = new Label( legend );
        legendLabel.addStyleName( "textbox-legend" );
        contactPanel.add( legendLabel );
        contactPanel.add( new Label( emailAddress ) );
    }

    public String makeErrorMessages() {

        // there must be a nonzero value in every non-contact field (the contact behaviour is dealt with elsewhere, in
        // the ContactPopup. Check each one, returning if any are empty
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
        Contact chosen = contacts.get( selected );
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