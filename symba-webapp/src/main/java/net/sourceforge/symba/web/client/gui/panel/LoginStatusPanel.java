package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class LoginStatusPanel extends SimplePanel {
    private static final String LOGIN_TEXT = "Log In";
    //    private static final String LOGGED_IN_TEXT = user + " (Log Out)";
    private final Label whoAmI_;

    public LoginStatusPanel() {
        super();
        whoAmI_ = new Label( LOGIN_TEXT );
        add( whoAmI_ );
    }
}
