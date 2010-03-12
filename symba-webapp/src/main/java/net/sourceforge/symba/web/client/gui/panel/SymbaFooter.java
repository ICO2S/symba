package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.HistoryToken;

/**
 * Class that represents the footer panel.
 *
 * @author Craig Smiles, Allyson Lister
 */
public class SymbaFooter extends VerticalPanel {

    /* Constants */

//    private static final PropertyServiceAsync PROPERTIES = PropertyService.App.getInstance();
    private static final String CISBAN_LOGO = "/images/logo-small.jpg";
    private static final String CISBAN_TEXT = "CISBAN homepage";
    private static final String CISBAN_URL = "http://www.cisban.ac.uk";

    private static final String WHOAMI_TEXT = "Logins are currently disabled.";
//    private static final String WHOAMI_TEXT = "You are currently logged in as ";

    /* Help constants */
    private static final String HELP_LINK = "View help";
    private static final String HELP_TEXT = "If you have any comments or questions please "
            + "<a href='mailto:helpdesk@cisban.ac.uk'>contact us</a>";

    /* This label shows who is currently logged in */
    private final Label whoAmI_;

    /**
     * Constructor for SymbaFooter class. This creates a footer that assumes the user hasn't logged in yet.
     * Not providing login services yet.
     */
    public SymbaFooter() {
        super();

        String moduleBase = GWT.getModuleBaseURL();
        String moduleName = GWT.getModuleName();
        String baseApp = moduleBase.substring( 0, moduleBase.lastIndexOf( moduleName ) );

        // you need slightly different URLs when in development mode (specifically, no prefix at all).
        String prefix = "";
        if ( GWT.isScript() ) {
            prefix = baseApp;
        }

        whoAmI_ = new Label( WHOAMI_TEXT );
        Label help = new Label( HELP_LINK );
//        final Hyperlink help = new Hyperlink( HELP_LINK, HistoryToken.HELP.toString() ); // todo help page
        final HTML contact = new HTML();
        final Anchor cisbanLogo = new Anchor( "<img border=\"0\" src=\"" + prefix + CISBAN_LOGO + "\">", true,
                CISBAN_URL );
        cisbanLogo.setTitle( CISBAN_TEXT );
        contact.setHTML( HELP_TEXT );

        addStyleName( "footer-style" );

        // It only applies to widgets added after this property is set.
        setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );

        add( help );
        add( whoAmI_ );
        add( contact );
        add( cisbanLogo );

    }

    /**
     * This method should be called when the user logs in. It modifies the display according to the user.
     *
     * @param user user that has logged in
     */
//    public void setLoginState(final PersonClient user) {
    /*
      Note that there is no symmetrical opposite of this method (setLogoutState). This is because using the same object
      with GWT's asynchronous service calls is risky and creating a completely new object is a simple solution and care free solution
    */

    /* Now we can set the whoAmI_ label's text */
//        user.getName(new AsyncCallback<String>() {
//            public void onFailure(Throwable throwable) {
//                TODO: Possibly warn server admin of failure
//            }
//
//            public void onSuccess(String result) {
//                whoAmI_.setText(WHOAMI_TEXT + result);
//            }
//        });
//    }
}