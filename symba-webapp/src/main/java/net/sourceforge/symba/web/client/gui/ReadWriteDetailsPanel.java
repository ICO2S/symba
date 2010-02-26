package net.sourceforge.symba.web.client.gui;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import net.sourceforge.symba.web.shared.Investigation;

public class ReadWriteDetailsPanel extends VerticalPanel {

    private final TextBox investigationIdBox;
    private final TextBox investigationTitleBox;
    private final TextBox providerIdBox;
    private final TextBox firstNameBox;
    private final TextBox lastNameBox;
    private final TextBox emailAddressBox;

    private final HorizontalPanel investigationIdPanel;
    private final HorizontalPanel investigationTitlePanel;
    private final HorizontalPanel providerIdPanel;
    private final HorizontalPanel firstNamePanel;
    private final HorizontalPanel lastNamePanel;
    private final HorizontalPanel emailAddressPanel;

    public ReadWriteDetailsPanel() {

        investigationIdBox = new TextBox();
        investigationTitleBox = new TextBox();
        providerIdBox = new TextBox();
        firstNameBox = new TextBox();
        lastNameBox = new TextBox();
        emailAddressBox = new TextBox();

        investigationIdPanel = new HorizontalPanel();
        investigationTitlePanel = new HorizontalPanel();
        providerIdPanel = new HorizontalPanel();
        firstNamePanel = new HorizontalPanel();
        lastNamePanel = new HorizontalPanel();
        emailAddressPanel = new HorizontalPanel();

        setWidth( "100%" );

        investigationIdBox.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                addTextBoxHelperStyle( investigationIdBox );
            }
        } );

        investigationTitleBox.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                addTextBoxHelperStyle( investigationTitleBox );
            }
        } );

        providerIdBox.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                addTextBoxHelperStyle( providerIdBox );
            }
        } );

        firstNameBox.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                addTextBoxHelperStyle( firstNameBox );
            }
        } );

        lastNameBox.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                addTextBoxHelperStyle( lastNameBox );
            }
        } );

        emailAddressBox.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                addTextBoxHelperStyle( emailAddressBox );
            }
        } );

    }

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

        setupDetailPanel( firstNamePanel, firstNameBox, "First Name: ", investigation.getProvider().getFirstName(),
                investigation.isTemplate() );
        add( firstNamePanel );

        setupDetailPanel( lastNamePanel, lastNameBox, "Last Name: ", investigation.getProvider().getLastName(),
                investigation.isTemplate() );
        add( lastNamePanel );

        setupDetailPanel( emailAddressPanel, emailAddressBox, "Email Address: ",
                investigation.getProvider().getEmailAddress(), investigation.isTemplate() );
        add( emailAddressPanel );

    }

    private void setupDetailPanel( final HorizontalPanel panel,
                                   final TextBox box,
                                   String legend,
                                   String value,
                                   boolean template ) {

        panel.setSpacing( 5 );
        Label legendLabel = new Label( legend );
        legendLabel.addStyleName( "textbox-legend" );
        panel.add( legendLabel );

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
    }

    private void addTextBoxHelperStyle( TextBox box ) {
        if ( box.getText().trim().length() == 0 ) {
            box.removeStyleName( "textbox-accepted" );
            box.addStyleName( "textbox-warning" );
        } else {
            box.removeStyleName( "textbox-warning" );
            box.addStyleName( "textbox-accepted" );
        }
    }


    public String makeErrorMessages() {

        // there must be a nonzero value in every field: check each one, returning if any are empty
        String emptyValues = "\n";
        if ( investigationIdPanel.getWidget( 1 ) instanceof TextBox && investigationIdBox.getText().length() == 0 ) {
            emptyValues += "identifier\n";
        }
        if ( investigationTitlePanel.getWidget( 1 ) instanceof TextBox &&
                investigationTitleBox.getText().length() == 0 ) {
            emptyValues += "title of investigation\n";
        }
        if ( providerIdPanel.getWidget( 1 ) instanceof TextBox && providerIdBox.getText().length() == 0 ) {
            emptyValues += "provider identifier\n";
        }
        if ( firstNamePanel.getWidget( 1 ) instanceof TextBox && firstNameBox.getText().length() == 0 ) {
            emptyValues += "provider first name\n";
        }
        if ( lastNamePanel.getWidget( 1 ) instanceof TextBox && lastNameBox.getText().length() == 0 ) {
            emptyValues += "provider last name\n";
        }
        if ( emailAddressPanel.getWidget( 1 ) instanceof TextBox && emailAddressBox.getText().length() == 0 ) {
            emptyValues += "provider email address\n";
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
        if ( firstNamePanel.getWidget( 1 ) instanceof TextBox && firstNameBox.getText().length() > 0 ) {
            investigation.getProvider().setFirstName( firstNameBox.getText() );
        }
        if ( lastNamePanel.getWidget( 1 ) instanceof TextBox && lastNameBox.getText().length() > 0 ) {
            investigation.getProvider().setLastName( lastNameBox.getText() );
        }
        if ( emailAddressPanel.getWidget( 1 ) instanceof TextBox && emailAddressBox.getText().length() > 0 ) {
            investigation.getProvider().setEmailAddress( emailAddressBox.getText() );
        }
    }
}