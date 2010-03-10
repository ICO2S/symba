package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import net.sourceforge.symba.web.client.gui.panel.InitialDownloadPopup;
import net.sourceforge.symba.web.client.gui.panel.SymbaController;

public class DownloadClickHandler implements ClickHandler {
    final SymbaController dock;

    public DownloadClickHandler( SymbaController dock ) {
        this.dock = dock;
    }

    public void onClick( ClickEvent event ) {
        InitialDownloadPopup panel = new InitialDownloadPopup( dock );
        panel.show();
    }
}
