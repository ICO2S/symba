package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import net.sourceforge.symba.web.shared.Contact;

public class ContactView extends HorizontalPanel {
    public static enum ViewType {
        LOGIN, WITHIN_INVESTIGATION
    }

    public static final String ADD_TEXT = "(add new data owner)";
    public static final String ADD_TEXT_LOGIN = "(add new user and log in)";

    private final SymbaController controller;
    private final ViewType type;
    private final ListBox fullNameBox;
    private final Button loginButton;

    public ContactView( SymbaController controller,
                        ViewType type ) {
        this.controller = controller;
        this.type = type;
        fullNameBox = new ListBox();
        loginButton = new Button( "Login" );

        populateNameListBox();

        // immediately display the information if it is for LOGIN purposes
        if ( this.type == ViewType.LOGIN ) {
            setupNameDetailPanel( "", "", false );
        }

        // handlers
        loginButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                Contact newUser = ContactView.this.controller.getStoredContacts().get( getSelectedContactId() );
                ContactView.this.controller.setUser( newUser );
                populateNameListBox();
                setupNameDetailPanel( newUser.getFullName(), newUser.getEmailAddress(), true );
            }
        } );
    }

    public ViewType getType() {
        return type;
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

    public void setupNameDetailPanel( final String fullNameValue,
                                      final String emailAddress,
                                      boolean readOnly ) {

        // clear the contact panel.
        for ( int iii = getWidgetCount(); iii > 0; iii-- ) {
            remove( iii - 1 );
        }
        setSpacing( 5 );

        Label legendLabel = new Label( "Data Owner: " );
        legendLabel.addStyleName( "textbox-legend" );
        add( legendLabel );

        // if there is no contact at all yet for this investigation, just start with the list box. Otherwise, start
        // with a read-only string.
        if ( fullNameValue != null && fullNameValue.length() > 0 ) {
            if ( type == ViewType.LOGIN ) {
                legendLabel.setText( "Logged in as: " );
            }
            // the read-only label for the full name
            final Label label = new Label( contactDisplayView( fullNameValue, emailAddress ) );

            if ( !readOnly ) {
                // add the behaviour to switch to a ListBox when the read-only text is clicked on
                label.addStyleName( "clickable-text" );
                label.addClickHandler( new ClickHandler() {
                    public void onClick( ClickEvent clickEvent ) {
                        remove( 1 ); // remove read-only label
                        // start with a list box pre-filled with the existing full name.
                        insert( fullNameBox, 1 );
                        fullNameBox.setItemSelected( getIndexForItem( fullNameBox,
                                contactDisplayView( fullNameValue, emailAddress ) ), true );
                    }
                } );
            }
            add( label );
        } else {
            if ( type == ViewType.LOGIN ) {
                legendLabel.setText( "Login as: " );
            }
            add( fullNameBox );
            if ( type == ViewType.LOGIN ) {
                add( loginButton );
            }
        }

        // if we can change the value, and further if the login does not already have a user, allow
        // the addition of new contacts.
        if ( !readOnly ) {
            // provide the ability to add new contacts
            Label addContact = new Label( ADD_TEXT );
            if ( type == ViewType.LOGIN ) {
                addContact.setText( ADD_TEXT_LOGIN );
            }

            if ( type == ViewType.WITHIN_INVESTIGATION ||
                    ( type == ViewType.LOGIN && ( fullNameValue == null || fullNameValue.length() == 0 ) ) ) {

                add( addContact );
                addContact.addStyleName( "clickable-text" );
                addContact.addClickHandler( new ClickHandler() {
                    public void onClick( ClickEvent event ) {
                        startContactPopupPanel();
                    }
                } );
            }
        }
    }

    public String getSelectedContactId() {
        String selected = "";
        if ( getWidget( 1 ) instanceof ListBox ) {
            selected = fullNameBox.getValue( fullNameBox.getSelectedIndex() );
        } else if ( getWidget( 1 ) instanceof Label ) {
            // search the ListBox for a matching item value
            for ( int iii = 0; iii < fullNameBox.getItemCount(); iii++ ) {
                if ( fullNameBox.getItemText( iii ).equals( ( ( Label ) getWidget( 1 ) ).getText() ) ) {
                    selected = fullNameBox.getValue( iii );
                    break;
                }
            }
        }
        return selected;
    }

    private int getIndexForItem( ListBox fullNameBox,
                                 String fullNameValue ) {
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

    private String contactDisplayView( String fullName,
                                       String emailAddress ) {
        if ( emailAddress != null && emailAddress.length() > 0 && type != ViewType.LOGIN ) {
            return fullName + " (" + emailAddress + ")";
        } else {
            return fullName;
        }

    }


}
