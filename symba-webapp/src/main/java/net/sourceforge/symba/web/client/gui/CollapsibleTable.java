package net.sourceforge.symba.web.client.gui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.ArrayList;

public abstract class CollapsibleTable extends FlexTable {
    // each row is plain text
    private ArrayList<String> list;
    private String listType;
    private final Label summaryLabel;
    HorizontalPanel summaryPanel;

    public CollapsibleTable( ArrayList list,
                             String listType ) {
        this( list, listType, null );
    }

    public CollapsibleTable( ArrayList list,
                             String listType,
                             final ClickHandler myEditableHandler ) {
        this.listType = listType;
        this.list = list;

        summaryPanel = new HorizontalPanel();
        summaryPanel.setSpacing( 5 );
        summaryLabel = new Label( displayCount() );
        final Label actionLabel = new Label( "(expand)" );
        actionLabel.addStyleName( "clickable-text" );
        actionLabel.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                if ( actionLabel.getText().equals( "(expand)" ) ) {
                    actionLabel.setText( "(collapse)" );
                    displayList( myEditableHandler );
                } else {
                    actionLabel.setText( "(expand)" );
                    hideList();
                }
            }
        } );
        if ( myEditableHandler != null ) {
            summaryLabel.addClickHandler( myEditableHandler );
            summaryLabel.addStyleName( "clickable-text" );
        }

        if ( list.size() > 0 ) {
            summaryPanel.add( summaryLabel );
            summaryPanel.add( actionLabel );
            setWidget( 0, 0, summaryPanel );
        }

    }

    protected abstract void displayList( ClickHandler myEditableHandler );

    protected void hideList() {
        removeAllRows();
        setWidget( 0, 0, summaryPanel );
    }

    protected String getListType() {
        return listType;
    }

    public String displayCount() {
        if ( getList().size() == 1 ) {
            return "1 " + getListType();
        } else if ( getList().size() > 1 ) {
            return getList().size() + " " + getListType() + "s";
        }
        return "";
    }

    public ArrayList getList() {
        return list;
    }

    public void updateSummary() {
        summaryLabel.setText( displayCount() );
    }
}
