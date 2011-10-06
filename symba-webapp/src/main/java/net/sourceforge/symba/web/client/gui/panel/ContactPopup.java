package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.gui.InputValidator;
import net.sourceforge.symba.web.shared.Contact;

import java.util.HashMap;

public class ContactPopup extends PopupPanel {

    private final AddContactPanel addContactPanel;

    public ContactPopup( final SymbaController controller, final ContactView callingPanel ) {
        super( true ); // set auto-hide property

        addContactPanel = new AddContactPanel( controller, callingPanel );
        setWidget( addContactPanel );

        // set the position to the center of the window
        setPopupPositionAndShow( new PopupPanel.PositionCallback() {
            public void setPosition( int offsetWidth, int offsetHeight ) {
                int left = ( Window.getClientWidth() - offsetWidth ) / 2;
                int top = ( Window.getClientHeight() - offsetHeight ) / 2;
                setPopupPosition( left, top );
            }
        } );

    }

    /**
     * Populate the form with the information from this user. This makes the action an update rather than a new contact
     *
     * @param toUpdate the contact to update
     */
    public void populate( Contact toUpdate ) {
        addContactPanel.populate( toUpdate );
    }

    private class AddContactPanel extends VerticalPanel {
        private static final String SAVE_TEXT   = "Save Contact";
        private static final String UPDATE_TEXT = "Update Contact";
        private final TextBox firstBox, lastBox, emailBox;
        private final Button saveButton;
        private       String existingId;

        public AddContactPanel( final SymbaController controller, final ContactView callingPanel ) {

            firstBox = new TextBox();
            lastBox = new TextBox();
            emailBox = new TextBox();
            saveButton = new Button( SAVE_TEXT );
            existingId = "";

            HorizontalPanel first = new HorizontalPanel();
            HorizontalPanel last = new HorizontalPanel();
            HorizontalPanel email = new HorizontalPanel();
            HorizontalPanel save = new HorizontalPanel();

            first.add( new Label( "First Name: " ) );
            last.add( new Label( "Last Name: " ) );
            email.add( new Label( "Email Address: " ) );
            save.add( new Label(
                    "By clicking on the \"" + SAVE_TEXT + "\" button, you will save this contact in the database." ) );

            first.add( firstBox );
            last.add( lastBox );
            email.add( emailBox );
            save.add( saveButton );

            add( first );
            add( last );
            add( email );
            add( save );

            add( new Label( "(Click outside the box to cancel and close.)" ) );

            // all handlers

            firstBox.addBlurHandler( new BlurHandler() {
                public void onBlur( BlurEvent event ) {
                    InputValidator.nonEmptyTextBoxStyle( firstBox );
                }
            } );

            lastBox.addBlurHandler( new BlurHandler() {
                public void onBlur( BlurEvent event ) {
                    InputValidator.nonEmptyTextBoxStyle( lastBox );
                }
            } );

            emailBox.addBlurHandler( new BlurHandler() {
                public void onBlur( BlurEvent event ) {
                    InputValidator.nonEmptyTextBoxStyle( emailBox );
                }
            } );

            saveButton.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    doSave( controller, callingPanel, firstBox, lastBox, emailBox.getText() );
                }
            } );

        }

        private String doSave( final SymbaController controller,
                               final ContactView callingPanel,
                               TextBox first,
                               TextBox last,
                               String email ) {

            String emptyValues = "";
            if ( first.getText().trim().length() == 0 ) {
                emptyValues += "First name\n";
            }
            if ( last.getText().trim().length() == 0 ) {
                emptyValues += "Last name\n";
            }
            if ( email == null || email.trim().length() == 0 ) {
                emptyValues += "Email address\n";
            }
            if ( emptyValues.length() > 1 ) {
                Window.alert( "The following fields should not be empty: " + emptyValues );
                return "";
            }

            // otherwise, it's OK to save the contact
            final Contact contact = new Contact();
            // do not create a new id - that's up to the implementation as to how to deal with it. Keeping
            // the existing id is a marker that an earlier version existed.
            if ( existingId.length() > 0 ) {
                contact.setId( existingId );
            }
            contact.setFirstName( first.getText().trim() );
            contact.setLastName( last.getText().trim() );
            contact.setEmailAddress( email );

            if ( existingId.length() == 0 ) {
                // basic validation: check that the full name isn't already in the contact list
                for ( Contact storedContact : controller.getStoredContacts().values() ) {
                    if ( storedContact.getFullName().equals( contact.getFullName() ) ) {
                        Window.alert( "You may not use the name of an existing contact to create a new contact" );
                        InputValidator.setWarning( first );
                        InputValidator.setWarning( last );
                        return "";
                    }
                }
            }

            controller.getRpcService().addOrUpdateContact( contact, new AsyncCallback<HashMap<String, Contact>>() {
                public void onFailure( Throwable caught ) {
                    Window.alert( "Failed to store contact: " + contact.getFullName() + "\n" + caught.getMessage() );
                }

                public void onSuccess( HashMap<String, Contact> result ) {
                    controller.setStoredContacts( result );
                    if ( callingPanel.getType() == ContactView.ViewType.LOGIN ) {
                        // also set the user for the session
                        controller.setUser( result.get( contact.getId() ) );
                        callingPanel.populateNameListBox();
                        callingPanel.setupNameDetailPanel( contact.getFullName(), contact.getEmailAddress(), true );
                    } else {
                        callingPanel.populateNameListBox();
                        callingPanel.setupNameDetailPanel( contact.getFullName(), contact.getEmailAddress(), false );
                    }
                    hide();
                }
            } );

            return contact.getId();
        }

        public void populate( Contact toUpdate ) {
            existingId = toUpdate.getId();
            firstBox.setText( toUpdate.getFirstName() );
            lastBox.setText( toUpdate.getLastName() );
            emailBox.setText( toUpdate.getEmailAddress() );
            saveButton.setText( UPDATE_TEXT );
        }
    }
}
