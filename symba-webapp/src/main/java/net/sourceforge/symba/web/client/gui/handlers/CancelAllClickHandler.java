package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import net.sourceforge.symba.web.client.gui.panel.SymbaController;

public class CancelAllClickHandler implements ClickHandler {
    private final SymbaController dock;
    private final Widget replacement;

    public CancelAllClickHandler( SymbaController dock,
                                  Widget replacement ) {
        this.dock = dock;
        this.replacement = replacement;
    }

    public void onClick( ClickEvent event ) {
        // todo add "did not modify" statement at top of new page
        // return to home page, removing the help panel from view
        dock.setCenterWidget( replacement );
        dock.hideEastWidget();
    }
}
