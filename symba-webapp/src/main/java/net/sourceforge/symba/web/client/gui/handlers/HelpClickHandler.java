package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.ui.HTML;
import net.sourceforge.symba.web.client.gui.panel.SymbaController;


public class HelpClickHandler implements ClickHandler {
    private final SymbaController controller;

    public HelpClickHandler( SymbaController controller ) {

        this.controller = controller;
    }

    public void onClick( ClickEvent event ) {

        final HTML html = new HTML( "There is extensive programmer documentation available at " +
                                    "<a href=\"http://symba.sourceforge.net/symba-books/general-information/index.html\">" +
                                    "the Sourceforge project pages</a>." );

        String moduleBase = GWT.getModuleBaseURL();
        String moduleName = GWT.getModuleName();
        String baseApp = moduleBase.substring( 0, moduleBase.lastIndexOf( moduleName ) );
        // you need slightly different URLs when in development mode (specifically, no prefix at all).
        String prefix = "";
        if ( GWT.isScript() ) {
            prefix = baseApp;
        }

        try {
            final String finalPrefix = prefix;
            new RequestBuilder( RequestBuilder.GET, "/MainHelp.html" ).sendRequest( "", new RequestCallback() {
                @Override
                public void onResponseReceived( Request req, Response resp ) {
                    String text = resp.getText();
                    text = text.replaceAll( "img src=\"images", "img src=\"" + finalPrefix + "/images" );
                    html.setHTML( text );
                }

                @Override
                public void onError( Request res, Throwable throwable ) {
                    // do nothing - there is already a default help message prepared.
                }
            } );
        } catch ( RequestException e ) {
            // couldn't connect to server. Use default text already supplied instead.
            e.printStackTrace();
        }
        controller.setCenterWidget( html );
    }
}
