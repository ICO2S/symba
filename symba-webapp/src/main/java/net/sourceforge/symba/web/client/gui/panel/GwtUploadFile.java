package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;
import gwtupload.client.PreloadedImage;
import net.sourceforge.symba.web.client.gui.CollapsibleTable;
import net.sourceforge.symba.web.client.gui.SetupTitledText;

import java.util.ArrayList;
import java.util.HashMap;

public class GwtUploadFile extends VerticalPanel {

    // A panel where the thumbnails of uploaded images will be shown
    private FlowPanel panelImages = new FlowPanel();

    // the key is the file name, and the value is the file description.
    private final HashMap<String, String> fileInfo;

    public GwtUploadFile( HashMap<String, String> fileInfo ) {

        //
        // Initial instantiation of all objects in this panel.
        //
        this.fileInfo = new HashMap<String, String>( fileInfo );
        final MultiUploader defaultUploader = new MultiUploader();
        OriginalFileDisplay originalFileDisplay = new OriginalFileDisplay( this.fileInfo );

        //
        // handlers
        //

        // Attach an image to the pictures viewer
        final PreloadedImage.OnLoadPreloadedImageHandler showImage = new PreloadedImage.OnLoadPreloadedImageHandler() {
            public void onLoad( PreloadedImage image ) {
                image.setWidth( "75px" );
                panelImages.add( image );
            }
        };

        // Add a finish handler which will load the image once the upload finishes
        IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
            public void onFinish( IUploader uploader ) {
                if ( uploader.getStatus() == IUploadStatus.Status.SUCCESS ) {
                    new PreloadedImage( uploader.fileUrl(), showImage );
                    GwtUploadFile.this.fileInfo.put( defaultUploader.getFileName(), "" );
                    HorizontalPanel hp = new HorizontalPanel();
                    final TextArea area = new TextArea();
                    area.setCharacterWidth( 40 );
                    area.setVisibleLines( 4 );
                    SetupTitledText.set( hp, area, defaultUploader.getFileName(), "", false );
                    add( hp );

                    area.addBlurHandler( new BlurHandler() {
                        public void onBlur( BlurEvent event ) {
                            // override original value in the HashMap
                            GwtUploadFile.this.fileInfo.put( defaultUploader.getFileName(), area.getText() );
                        }
                    } );
                }
            }
        };
        defaultUploader.addOnFinishUploadHandler( onFinishUploaderHandler );

        //
        // positioning
        //
        add( originalFileDisplay );
        add( defaultUploader );
        add( panelImages );
    }

    public HashMap<String, String> getFileInfo() {

        return fileInfo;
    }

    private class OriginalFileDisplay extends CollapsibleTable {
        private final HashMap<String, String> info;

        private OriginalFileDisplay( HashMap<String, String> info ) {
            super( new ArrayList<String>( info.keySet() ), "original file", new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    // deliberately leave this empty, as we are going to provide our own click handling in this class
                }
            } );
            this.info = info;
        }

        @Override
        protected void displayList( ClickHandler myEditableHandler ) {

            // myEditableHandler is ignored in this version of the method.
            // Instead, this is assumed to be editable, as this class is only used in an editable situation.

            for ( final Object item : getList() ) {
                final int currentRow = getRowCount();

                //
                // display object instantiation
                //

                final String fileName = ( String ) item;
                StringBuffer value = new StringBuffer( fileName );
                if ( info.get( fileName ).length() > 0 ) {
                    value.append( " (Description: " ).append( info.get( fileName ) ).append( ")" );
                }
                final Label label = new Label( value.toString() );
                final Button deleteButton = new Button( "X" );

                //
                // styles
                //
                label.addClickHandler( new ClickHandler() {
                    public void onClick( ClickEvent event ) {
                        remove( label );
                        // allow modification of the file description
                        HorizontalPanel hp = new HorizontalPanel();
                        final TextArea area = new TextArea();
                        area.setCharacterWidth( 40 );
                        area.setVisibleLines( 4 );
                        SetupTitledText.set( hp, area, fileName, info.get( fileName ), false );
                        setWidget( currentRow, 0, hp );

                        area.addBlurHandler( new BlurHandler() {
                            public void onBlur( BlurEvent event ) {
                                // override original value in the HashMap
                                GwtUploadFile.this.fileInfo.put( fileName, area.getText() );
                            }
                        } );
                    }
                } );
                label.addStyleName( "clickable-text" );

                //
                // positioning
                //
                setWidget( currentRow, 0, label );
                setWidget( currentRow, 1, deleteButton );

                //
                // handlers
                //
                deleteButton.addClickHandler( new ClickHandler() {
                    public void onClick( ClickEvent event ) {
                        getList().remove( item );
                        remove( label );
                        remove( deleteButton );
                        removeRow( currentRow );
                        updateSummary();
                    }
                } );

            }
        }

    }

}
