package net.sourceforge.symba.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.gui.panel.SymbaControllerPanel;

public class InvestigationManipulator implements EntryPoint {

    public void onModuleLoad() {
        final InvestigationsServiceAsync rpcService = GWT.create( InvestigationsService.class );
        SymbaControllerPanel symba = new SymbaControllerPanel( rpcService );
        RootPanel.get().add( symba );
    }
}
