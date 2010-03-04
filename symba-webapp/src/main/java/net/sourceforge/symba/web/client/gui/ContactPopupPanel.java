package net.sourceforge.symba.web.client.gui;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.InvestigationsServiceAsync;
import net.sourceforge.symba.web.shared.Contact;

import java.util.HashMap;

public class ContactPopupPanel extends PopupPanel {

    public ContactPopupPanel( HashMap<String, Contact> contacts,
                              final ReadWriteDetailsPanel callingPanel,
                              InvestigationsServiceAsync rpcService ) {
        super( true );

        setWidget( new AddContactPanel( contacts, callingPanel, rpcService ) );
    }

    private class AddContactPanel extends VerticalPanel {
        private static final String SAVE_TEXT = "Save Contact";
        private final InvestigationsServiceAsync rpcService;

        public AddContactPanel( final HashMap<String, Contact> contacts,
                                final ReadWriteDetailsPanel callingPanel,
                                InvestigationsServiceAsync rpcService ) {

            this.rpcService = rpcService;

            HorizontalPanel first = new HorizontalPanel();
            HorizontalPanel last = new HorizontalPanel();
            HorizontalPanel email = new HorizontalPanel();
            HorizontalPanel save = new HorizontalPanel();

            final TextBox firstBox = new TextBox();
            final TextBox lastBox = new TextBox();
            final TextBox emailBox = new TextBox();

            Button saveButton = new Button( SAVE_TEXT );

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
                    doSave( contacts, callingPanel, firstBox.getText(), lastBox.getText(), emailBox.getText() );
                }
            } );

        }

        private String doSave( final HashMap<String, Contact> contacts,
                               final ReadWriteDetailsPanel callingPanel,
                               String first,
                               String last,
                               String email ) {

            String emptyValues = "";
            if ( first == null || first.trim().length() == 0 ) {
                emptyValues += "First name\n";
            }
            if ( last == null || last.trim().length() == 0 ) {
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
            contact.createId();
            contact.setFirstName( first );
            contact.setLastName( last );
            contact.setEmailAddress( email );

            rpcService.addContact( contact, new AsyncCallback<HashMap<String, Contact>>() {
                public void onFailure( Throwable caught ) {
                    Window.alert( "Failed to store contact: " + contact.getFullName() );
                }

                public void onSuccess( HashMap<String, Contact> result ) {
                    contacts.clear();
                    contacts.putAll( result );
                    callingPanel.populateNameListBox();
                    callingPanel
                            .setupProviderNameDetailPanel( contact.getFullName(), contact.getEmailAddress(), false );
                }
            } );

            return contact.getId();
        }
    }
}
