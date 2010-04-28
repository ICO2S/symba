package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public abstract class ActivateableClickHandler implements ClickHandler {
    boolean isActive;
    private ActivateableClickHandler[] associatedHandlers;
    private Widget[] associatedWidgets;

    public ActivateableClickHandler() {
        isActive = true;
        associatedWidgets = null;
        associatedHandlers = null;
    }

    public void onClick( ClickEvent event ) {
        if ( isActive ) {
            if ( runClickMethod( event ) ) {
                // disable anything else that's been asked to be disabled
                if ( associatedWidgets != null ) {
                    for ( Widget widget : associatedWidgets ) {
                        widget.addStyleName( "images-opaque" );
                    }
                }
                if ( associatedHandlers != null ) {
                    for ( ActivateableClickHandler handler : associatedHandlers ) {
                        handler.disable();
                    }
                }
            }
        }
    }

    public void enable() {
        isActive = true;
    }

    public void disable() {
        isActive = false;
    }

    public void setAssociatedWidgets( Widget... widgets ) {
        this.associatedWidgets = widgets;
    }

    public void setAssociatedHandlers( ActivateableClickHandler... handlers ) {
        this.associatedHandlers = handlers;
    }

    /**
     * Put any implementation-specific code here for your particular implementation of this class.
     *
     * @param event the event passed from the onClick method
     * @return true if everything ran correctly, and it's OK to continue with the onClick method
     */
    protected abstract boolean runClickMethod( ClickEvent event );
}
