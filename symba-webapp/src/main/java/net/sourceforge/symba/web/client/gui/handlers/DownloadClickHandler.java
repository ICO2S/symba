package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.event.dom.client.ClickEvent;
import net.sourceforge.symba.web.client.gui.panel.InitialDownloadPopup;
import net.sourceforge.symba.web.client.gui.panel.SymbaController;

public class DownloadClickHandler extends ActivateableClickHandler {
    final SymbaController controller;

    public DownloadClickHandler( SymbaController controller ) {
        super();
        this.controller = controller;
    }

    @Override
    protected boolean runClickMethod( ClickEvent event ) {
        InitialDownloadPopup panel = new InitialDownloadPopup( controller );
        panel.show();
        return true;
    }

}
