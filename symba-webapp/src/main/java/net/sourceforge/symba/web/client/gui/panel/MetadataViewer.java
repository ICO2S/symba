package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MetadataViewer extends VerticalPanel {
    private SymbaController symba;

    public MetadataViewer( SymbaController symba ) {
        super();
        this.symba = symba;
    }

    public void display( String investigationId ) {
        symba.getRpcService().getMetadata( investigationId, new AsyncCallback<String>() {
            public void onFailure( Throwable caught ) {
                Window.alert( "Failed to retrieve metadata for Investigation: " + caught.getMessage() );
            }

            public void onSuccess( String result ) {
                String filtered = result.replaceAll(">", "&gt;" ).replaceAll("<", "&lt;");
                HTML metadata = new HTML( filtered );
                metadata.addStyleName( "metadata-style" );
                symba.setCenterWidget( metadata );
                System.err.println( "result: " + result );
            }
        } );

    }
}
