package net.sourceforge.symba.web.client.gui;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Craig Smiles
 * @version 13-Aug-2009
 */
public class HtmlImage extends HTML {
    private String src_;

    public HtmlImage(String src, String alt, String href) {
        super();
        src_ = src;
        final boolean hasHref = href != null;
        String htmlText = "";

        if(hasHref) {
            htmlText += "<a href='" + href + "'>";
        }

        htmlText += "<img src_='" + src + "' alt='" + alt +"' />";

        if(hasHref) {
            htmlText += "</a>";
        }

        setHTML(htmlText);
    }

    public void addTo(CellPanel panel) {
        final Image img = new Image(src_);
        img.addLoadHandler(new MyLoadHandler());
        panel.add(this);
    }


    private class MyLoadHandler implements LoadHandler {
        /**
         * Called when LoadEvent is fired.
         *
         * @param event the {@link com.google.gwt.event.dom.client.LoadEvent} that was fired
         */
        public void onLoad(LoadEvent event) {
            final Image img = (Image) event.getSource();
            final CellPanel panel = (CellPanel) img.getParent();

            panel.setCellHeight(HtmlImage.this, img.getHeight() + "px");
            panel.setCellWidth(HtmlImage.this, img.getWidth() + "px");
            HtmlImage.this.setPixelSize(img.getWidth(), img.getHeight());
        }
    }
}