package net.sourceforge.symba.web.client.gui;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.InvestigationsServiceAsync;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;

import java.util.HashMap;

public class ReadWriteDetailsPanel extends VerticalPanel {

    private final TextBox investigationIdBox;
    private final TextBox investigationTitleBox;
    private final TextBox providerIdBox;
    private final ListBox fullNameBox;

    private final HorizontalPanel investigationIdPanel;
    private final HorizontalPanel investigationTitlePanel;
    private final HorizontalPanel providerIdPanel;
    private final HorizontalPanel contactPanel;

    private final HashMap<String, Contact> contacts;
    private final InvestigationsServiceAsync rpcService;

    public ReadWriteDetailsPanel( HashMap<String, Contact> allContacts,
                                  InvestigationsServiceAsync rpcService ) {

        investigationIdBox = new TextBox();
        investigationTitleBox = new TextBox();
        providerIdBox = new TextBox();
        fullNameBox = new ListBox();

        investigationIdPanel = new HorizontalPanel();
        investigationTitlePanel = new HorizontalPanel();
        providerIdPanel = new HorizontalPanel();
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

        setupDetailPanel( investigationIdPanel, investigationIdBox, "Investigation ID (temp): ",
                investigation.getId(), investigation.isTemplate() );
        add( investigationIdPanel );

        setupDetailPanel( investigationTitlePanel, investigationTitleBox, "Investigation Title: ",
                investigation.getInvestigationTitle(), investigation.isTemplate() );
        add( investigationTitlePanel );

        setupDetailPanel( providerIdPanel, providerIdBox, "Provider ID (temp): ", investigation.getProvider().getId(),
                investigation.isTemplate() );
        add( providerIdPanel );

        setupProviderNameDetailPanel( investigation.getProvider().getFullName(),
                investigation.getProvider().getEmailAddress(), investigation.isTemplate() );
        add( contactPanel );

    }

    private void setupDetailPanel( final HorizontalPanel panel,
                                   final TextBox box,
                                   String legend,
                                   String value,
                                   boolean template ) {

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
            if ( !template ) {
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
                                              final String emailAddress,
                                              boolean template ) {

        // clear the contact panel.
        for ( int iii = contactPanel.getWidgetCount(); iii > 0; iii-- ) {
            contactPanel.remove( iii - 1 );
        }
        contactPanel.setSpacing( 5 );

        // if there is no contact at all yet for this investigation, just start with the list box. Otherwise, start
        // with a read-only string.
        if ( fullNameValue != null && fullNameValue.length() > 0 ) {
            // add the read-only text
            Label legendLabel = new Label( "Name: " );
            legendLabel.addStyleName( "textbox-legend" );
            contactPanel.add( legendLabel );

            // the read-only label for the full name
            final Label label = new Label();
            label.setText( fullNameValue );

            if ( !template ) {
                // add the behaviour to switch to a listbox when the read-only text is clicked on
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
        }

        if ( !template ) {
            // provide the ability to add new contacts
            Label addContact = new Label( "(add new contact)" );
            contactPanel.add( addContact );
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
        ContactPopupPanel panel = new ContactPopupPanel( contacts, this, rpcService );
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
        // the ContactPopupPanel. Check each one, returning if any are empty
        String emptyValues = "\n";
        if ( investigationIdPanel.getWidget( 1 ) instanceof TextBox &&
                investigationIdBox.getText().trim().length() == 0 ) {
            emptyValues += "identifier\n";
        }
        if ( investigationTitlePanel.getWidget( 1 ) instanceof TextBox &&
                investigationTitleBox.getText().length() == 0 ) {
            emptyValues += "title of investigation\n";
        }
        if ( providerIdPanel.getWidget( 1 ) instanceof TextBox && providerIdBox.getText().trim().length() == 0 ) {
            emptyValues += "provider identifier\n";
        }
        return emptyValues;
    }

    public void updateModifiedDetails( Investigation investigation ) {

        if ( investigationIdPanel.getWidget( 1 ) instanceof TextBox && investigationIdBox.getText().length() > 0 ) {
            investigation.setId( investigationIdBox.getText() );
        }
        if ( investigationTitlePanel.getWidget( 1 ) instanceof TextBox &&
                investigationTitleBox.getText().length() > 0 ) {
            investigation.setInvestigationTitle( investigationTitleBox.getText() );
        }
        if ( providerIdPanel.getWidget( 1 ) instanceof TextBox && providerIdBox.getText().length() > 0 ) {
            investigation.getProvider().setId( providerIdBox.getText() );
        }
        // the contact will be identical if it is still in read-only mode. However, it may be new if there is a
        // ListBox there. If so, assign the provider to the contact whose ID is in the getValue() of the ListBox's
        // getSelectedIndex().
        if ( contactPanel.getWidget( 1 ) instanceof ListBox ) {
            Contact chosen = contacts.get( fullNameBox.getValue( fullNameBox.getSelectedIndex() ) );
            investigation.getProvider().setId( chosen.getId() );
            investigation.getProvider().setFirstName( chosen.getFirstName() );
            investigation.getProvider().setLastName( chosen.getLastName() );
            investigation.getProvider().setEmailAddress( chosen.getEmailAddress() );
        }
    }
}