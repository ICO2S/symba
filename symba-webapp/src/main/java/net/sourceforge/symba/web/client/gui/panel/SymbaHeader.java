package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.gui.handlers.*;

public class SymbaHeader extends HorizontalPanel {

    private static final String HOME_ICON = "/images/lion-plus-text.jpg";
    private static final String ADD_EXPERIMENT = "/images/plus.png";
    private static final String LIST_EXPERIMENT = "/images/article.png";
    private static final String DOWNLOAD_FILE = "/images/arrow_down.png";
    private static final String GET_HELP = "/images/help.png";
    private static final String NO_USER = "Not logged in";

    private final Image addExpImage, listExpImage, downloadFileImage, helpFileImage;
    private final AddExperimentClickHandler addExpHandler;
    private final ListExperimentsClickHandler listExpHandler;
    private final DownloadClickHandler downloadHandler;
    private final HorizontalPanel loginStatus;
    private SymbaController controller;
    private HomePanel home;

    public SymbaHeader( SymbaController controller,
                        HomePanel home ) {
        this.controller = controller;
        this.home = home;
        //
        // file setup
        //
        String moduleBase = GWT.getModuleBaseURL();
        String moduleName = GWT.getModuleName();
        String baseApp = moduleBase.substring( 0, moduleBase.lastIndexOf( moduleName ) );

        // you need slightly different URLs when in development mode (specifically, no prefix at all).
        String prefix = "";
        if ( GWT.isScript() ) {
            prefix = baseApp;
        }
        //
        // initialisation
        //
        final Image homeImage = new Image( prefix + HOME_ICON );
        final Label title = new Label( "SyMBA 2" );

        // adding the other actions in their own HorizontalPanel ensures that there isn't a huge amount of space
        // between each image.
        final HorizontalPanel rightSide = new HorizontalPanel();

        addExpImage = new Image( prefix + ADD_EXPERIMENT );
        listExpImage = new Image( prefix + LIST_EXPERIMENT );
        downloadFileImage = new Image( prefix + DOWNLOAD_FILE );
        helpFileImage = new Image( prefix + GET_HELP );

        loginStatus = new HorizontalPanel();
        loginStatus.setSpacing( 5 );
        Label nameStatus = new Label( NO_USER );
        loginStatus.add( nameStatus );

        //
        // styles
        //
        setStyleName( "header-style" );
        setSpacing( 10 );
        homeImage.setTitle( "SyMBA Home Page (Cancel Current Operation)" );
        homeImage.addStyleName( "header-home-image" );
        title.addStyleName( "header-title" );
        rightSide.setSpacing( 10 );

        addExpImage.setTitle( "Describe a New Investigation" );
        listExpImage.setTitle( "List All Investigations" );
        downloadFileImage.setTitle( "Download a Data or Metadata File" );
        helpFileImage.addStyleName( "header-images" );
        helpFileImage.setTitle( "Help" );


        //
        // handlers
        //
        homeImage.addClickHandler( new CancelAllClickHandler( controller, home ) );
        homeImage.addMouseOverHandler( new MouseOverHandler() {
            public void onMouseOver( MouseOverEvent event ) {
                homeImage.addStyleName( "pointer-select" );
            }
        } );

        addExpHandler = new AddExperimentClickHandler( controller );
        addExpImage.addClickHandler( addExpHandler );
        addExpImage.addMouseOverHandler( new MouseOverHandler() {
            public void onMouseOver( MouseOverEvent event ) {
                addExpImage.addStyleName( "pointer-select" );
            }
        } );

        listExpHandler = new ListExperimentsClickHandler( controller );
        listExpImage.addClickHandler( listExpHandler );
        listExpImage.addMouseOverHandler( new MouseOverHandler() {
            public void onMouseOver( MouseOverEvent event ) {
                listExpImage.addStyleName( "pointer-select" );
            }
        } );

        downloadHandler = new DownloadClickHandler( controller );
        downloadFileImage.addClickHandler( downloadHandler );
        downloadFileImage.addMouseOverHandler( new MouseOverHandler() {
            public void onMouseOver( MouseOverEvent event ) {
                downloadFileImage.addStyleName( "pointer-select" );
            }
        } );

        helpFileImage.addClickHandler( new HelpClickHandler( controller ) );
        helpFileImage.addMouseOverHandler( new MouseOverHandler() {
            public void onMouseOver( MouseOverEvent event ) {
                helpFileImage.addStyleName( "pointer-select" );
            }
        } );

        //
        // positioning
        //

        // It only applies to widgets added after this property is set.
        setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );

        add( homeImage );
        add( title );

        // It only applies to widgets added after this property is set.
        setHorizontalAlignment( HasHorizontalAlignment.ALIGN_RIGHT );

        rightSide.add( addExpImage );
        rightSide.add( listExpImage );
        rightSide.add( downloadFileImage );
        rightSide.add( helpFileImage );

        rightSide.add( loginStatus );

        add( rightSide );

        // start with all actions disabled, until the user logs in
        disableActions();
    }

    public void disableActions() {

        addExpImage.addStyleName( "header-images-opaque" );
        addExpHandler.disable();
        listExpImage.addStyleName( "header-images-opaque" );
        listExpHandler.disable();
        downloadFileImage.addStyleName( "header-images-opaque" );
        downloadHandler.disable();
        ( ( Label ) loginStatus.getWidget( 0 ) ).setText( NO_USER );
        if ( loginStatus.getWidgetCount() > 1 ) {
            loginStatus.remove( 1 );
        }
    }

    public void enableActions() {

        if ( controller.getUser().getFullName().length() > 0 ) {
            ( ( Label ) loginStatus.getWidget( 0 ) ).setText( controller.getUser().getFullName() );
            Label logoutLabel = new Label( "(Logout)" );
            loginStatus.add( logoutLabel );
            logoutLabel.addStyleName( "clickable-text" );
            logoutLabel.addClickHandler( new LogoutClickHandler( controller ) );
        } else {
            // there must be a user to enable the actions
            return;
        }

        addExpImage.removeStyleName( "header-images-opaque" ); // if present
        addExpImage.addStyleName( "header-images" );
        addExpHandler.enable();

        listExpImage.removeStyleName( "header-images-opaque" ); // if present
        listExpImage.addStyleName( "header-images" );
        listExpHandler.enable();

        downloadFileImage.removeStyleName( "header-images-opaque" ); // if present
        downloadFileImage.addStyleName( "header-images" );
        downloadHandler.enable();

    }
}
