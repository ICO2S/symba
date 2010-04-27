package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HomePanel extends HorizontalPanel {

    private final ContactView login;

    private static final String HOME_ICON = "/images/lion.jpg";
    private static final HTML HOME_TEXT = new HTML(
            "<p><strong>SyMBA</strong> is an <strong>archive</strong> that facilitates metadata " +
                    "integration and data storage. It <strong>stores knowledge about experimental data</strong> in a " +
                    "user-friendly and computationally-amenable way.</p>" +
                    "<p>It is designed to <strong>prevent loss</strong>, deletion or accidental modification of primary data " +
                    "and metadata, while providing convenient manual and computational access for " +
                    "<strong>standards-compliant publication, sharing and analysis</strong>.</p>" );

    public HomePanel( SymbaController controller ) {
        String moduleBase = GWT.getModuleBaseURL();
        String moduleName = GWT.getModuleName();
        String baseApp = moduleBase.substring( 0, moduleBase.lastIndexOf( moduleName ) );
        // you need slightly different URLs when in development mode (specifically, no prefix at all).
        String prefix = "";
        if ( GWT.isScript() ) {
            prefix = baseApp;
        }

        setSpacing( 10 );

        login = new ContactView( controller, ContactView.ViewType.LOGIN );
        login.populateNameListBox();

        VerticalPanel textPanel = new VerticalPanel();
        textPanel.add( login );
        textPanel.add( HOME_TEXT );

        add( new Image( prefix + HOME_ICON ) );
        add( textPanel );

    }

    public ContactView getLogin() {
        return login;
    }
}
