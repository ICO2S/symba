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

    public SymbaHeader( SymbaController controller,
                        HomePanel home ) {

        setStyleName( "header-style" );
        setSpacing( 10 );

        String moduleBase = GWT.getModuleBaseURL();
        String moduleName = GWT.getModuleName();
        String baseApp = moduleBase.substring( 0, moduleBase.lastIndexOf( moduleName ) );

        // you need slightly different URLs when in development mode (specifically, no prefix at all).
        String prefix = "";
        if ( GWT.isScript() ) {
            prefix = baseApp;
        }

        // It only applies to widgets added after this property is set.
        setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );

        final Image homeImage = new Image( prefix + HOME_ICON );
        homeImage.setTitle( "SyMBA Home Page (Cancel Current Operation)" );
        homeImage.addStyleName( "header-home-image" );
        homeImage.addClickHandler( new CancelAllClickHandler( controller, home ) );
        homeImage.addMouseOverHandler( new MouseOverHandler() {
            public void onMouseOver( MouseOverEvent event ) {
                homeImage.addStyleName( "pointer-select" );
            }
        } );
        add( homeImage );

        Label title = new Label( "SyMBA 2 Pre-Release 2010" );
        title.addStyleName( "header-title" );
        add( title );

        // It only applies to widgets added after this property is set.
        setHorizontalAlignment( HasHorizontalAlignment.ALIGN_RIGHT );

        // adding the other actions in their own HorizontalPanel ensures that there isn't a huge amount of space
        // between each image.
        HorizontalPanel rightSide = new HorizontalPanel();
        rightSide.setSpacing( 10 );

        final Image addExpImage = new Image( prefix + ADD_EXPERIMENT );
        addExpImage.setTitle( "Describe a New Investigation" );
        addExpImage.addStyleName( "header-images" );
        addExpImage.addClickHandler( new AddExperimentClickHandler( controller ) );
        addExpImage.addMouseOverHandler( new MouseOverHandler() {
            public void onMouseOver( MouseOverEvent event ) {
                addExpImage.addStyleName( "pointer-select" );
            }
        } );
        rightSide.add( addExpImage );


        final Image listExpImage = new Image( prefix + LIST_EXPERIMENT );
        listExpImage.setTitle( "List All Investigations" );
        listExpImage.addStyleName( "header-images" );
        listExpImage.addClickHandler( new ListExperimentsClickHandler( controller ) );
        listExpImage.addMouseOverHandler( new MouseOverHandler() {
            public void onMouseOver( MouseOverEvent event ) {
                listExpImage.addStyleName( "pointer-select" );
            }
        } );
        rightSide.add( listExpImage );

        final Image downloadFileImage = new Image( prefix + DOWNLOAD_FILE );
        downloadFileImage.setTitle( "Download a Data or Metadata File" );
        downloadFileImage.addStyleName( "header-images" );
        downloadFileImage.addClickHandler( new DownloadClickHandler( controller ) );
        downloadFileImage.addMouseOverHandler( new MouseOverHandler() {
            public void onMouseOver( MouseOverEvent event ) {
                downloadFileImage.addStyleName( "pointer-select" );
            }
        } );
        rightSide.add( downloadFileImage );

        final Image helpFileImage = new Image( prefix + GET_HELP );
        helpFileImage.setTitle( "Help" );
        helpFileImage.addStyleName( "header-images" );
        helpFileImage.addClickHandler( new HelpClickHandler( controller ) );
        helpFileImage.addMouseOverHandler( new MouseOverHandler() {
            public void onMouseOver( MouseOverEvent event ) {
                helpFileImage.addStyleName( "pointer-select" );
            }
        } );
        rightSide.add( helpFileImage );

        LoginStatusPanel login = new LoginStatusPanel();
        rightSide.add( login );

        add( rightSide );

    }

}
