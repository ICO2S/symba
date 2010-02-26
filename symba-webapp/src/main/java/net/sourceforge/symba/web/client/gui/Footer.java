package net.sourceforge.symba.web.client.gui;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import net.sourceforge.symba.web.client.HistoryToken;

/**
 * Class that represents the footer panel.
 *
 * @author Craig Smiles
 * @version 13-Aug-2009
 */
public class Footer extends VerticalPanel {

    /* Constants */

//    private static final PropertyServiceAsync PROPERTIES = PropertyService.App.getInstance();
    private static final String CISBAN_LOGO = "images/logo-small.jpg";
    private static final String CISBAN_TEXT = "Cisban homepage";
    private static final String CISBAN_URL = "http://www.cisban.ac.uk";

    /* CSS properties for this panel */
    private static final String STYLE_NAME = "footer";
    private static final String WIDTH = "100%";

    private static final String WHOAMI_TEXT = "You are currently logged in as ";

    /* Help constants */
    private static final String HELP_LINK = "View help";
    private static final String HELP_EMAIL_KEY = "net.sourceforge.symba.webapp.helpEmail";
    private static final String HELP_TEXT_START = "If you have any comments or questions please "
                                                  + "<a href='mailto:";
    private static final String HELP_TEXT_END = "'>contact us</a>";

    /* This label shows who is currently logged in */
    private final Label whoAmI_;

    /**
     * Constructor for Footer class. This creates a footer that assumes the user hasn't logged in yet.
     * Not providing login services yet.
     */
    public Footer() {
        /* The whoAmI_ label is given an empty text since the user is assumed not to be logged in */
        super();
        whoAmI_ = new Label();
        final Hyperlink help = new Hyperlink(HELP_LINK, HistoryToken.HELP.toString());
        final HTML contact = new HTML();
        final HtmlImage cisbanLogo = new HtmlImage(CISBAN_LOGO, CISBAN_TEXT, CISBAN_URL);


//        PROPERTIES.getString(HELP_EMAIL_KEY, new AsyncCallback<String>() {
//            public void onFailure(Throwable caught) {
//                TODO: Possibly warn server admin of failure
//                caught.printStackTrace();
//            }
//
//            public void onSuccess(String result) {
//                contact.setHTML(HELP_TEXT_START + result + HELP_TEXT_END);
//            }
//        });
//
        setStyleName(STYLE_NAME);

        /* Setting the width in the .css file doesn't work for some reason */
        setWidth(WIDTH);
        add(help);
        add(whoAmI_);
        add(contact);
        add(cisbanLogo);

        /* The below method calls must be called after the above add calls */
        setCellHorizontalAlignment(help, ALIGN_CENTER);
        setCellHorizontalAlignment(whoAmI_, ALIGN_CENTER);
        setCellHorizontalAlignment(contact, ALIGN_CENTER);
        setCellHorizontalAlignment(cisbanLogo, ALIGN_CENTER);
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