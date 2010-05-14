package net.sourceforge.symba.web.client.gui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.ArrayList;

public abstract class CollapsibleTable extends FlexTable {

    public static final String ACTION_EXPAND = "(expand)";
    public static final String ACTION_COLLAPSE = "(collapse)";
    // each row is plain text
    private ArrayList<String> list;
    private String listType;
    private final Label summaryLabel, actionLabel;
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
        actionLabel = new Label( ACTION_EXPAND );
        summaryLabel = new Label( displayCount() );
        actionLabel.addStyleName( "clickable-text" );
        actionLabel.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                if ( actionLabel.getText().equals( ACTION_EXPAND ) ) {
                    actionLabel.setText( ACTION_COLLAPSE );
                    displayList( myEditableHandler );
                } else {
                    actionLabel.setText( ACTION_EXPAND );
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
        String result;
        if ( getList().size() == 1 ) {
            result = "1 " + getListType();
            if ( actionLabel.getText().length() == 0 ) {
                actionLabel.setText( ACTION_EXPAND );
            }
        } else if ( getList().size() > 1 ) {
            result = getList().size() + " " + getListType() + "s";
            if ( actionLabel.getText().length() == 0 ) {
                actionLabel.setText( ACTION_EXPAND );
            }
        } else {
            // clear the collapse/expand action label
            result = "";
            actionLabel.setText( "" );
        }
        return result;
    }

    public ArrayList getList() {
        return list;
    }

    public void updateSummary() {
        summaryLabel.setText( displayCount() );
    }
}
