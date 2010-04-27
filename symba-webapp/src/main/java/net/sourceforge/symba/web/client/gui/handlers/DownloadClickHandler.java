package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import net.sourceforge.symba.web.client.gui.panel.InitialDownloadPopup;
import net.sourceforge.symba.web.client.gui.panel.SymbaController;

public class DownloadClickHandler implements ClickHandler {
    final SymbaController controller;
    private boolean isActive;

    public DownloadClickHandler( SymbaController controller ) {
        this.controller = controller;
        isActive = true;
    }

    public void onClick( ClickEvent event ) {
        if ( isActive ) {
            InitialDownloadPopup panel = new InitialDownloadPopup( controller );
            panel.show();
        }
    }

    public void disable() {
        isActive = false;
    }

    public void enable() {
        isActive = true;
    }

}
