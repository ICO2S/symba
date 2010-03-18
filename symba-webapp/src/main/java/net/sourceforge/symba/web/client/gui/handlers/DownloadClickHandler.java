package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import net.sourceforge.symba.web.client.gui.panel.InitialDownloadPopup;
import net.sourceforge.symba.web.client.gui.panel.SymbaController;

public class DownloadClickHandler implements ClickHandler {
    final SymbaController controller;

    public DownloadClickHandler( SymbaController controller ) {
        this.controller = controller;
    }

    public void onClick( ClickEvent event ) {
        InitialDownloadPopup panel = new InitialDownloadPopup( controller );
        panel.show();
    }
}
