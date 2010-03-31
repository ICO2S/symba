package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;
import gwtupload.client.PreloadedImage;
import net.sourceforge.symba.web.client.gui.CollapsibleTable;

import java.util.ArrayList;

public class GwtUploadFile extends VerticalPanel {

    // A panel where the thumbnails of uploaded images will be shown
    private FlowPanel panelImages = new FlowPanel();

    private final ArrayList<String> fileNames;

    public GwtUploadFile( ArrayList<String> fileNames ) {

        //
        // Initial instantiation of all objects in this panel.
        //
        this.fileNames = fileNames;
        final MultiUploader defaultUploader = new MultiUploader();
        OriginalFileDisplay originalFileDisplay = new OriginalFileDisplay( this.fileNames, new ClickHandler() {
            public void onClick( ClickEvent event ) {
                // do nothing - we just need to pass something to ensure that the display is changeable.
            }
        } );

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
                    GwtUploadFile.this.fileNames.add( defaultUploader.getFileName() );
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


    public ArrayList<String> getFileNames() {
        return fileNames;
    }

    private class OriginalFileDisplay extends CollapsibleTable {
        private OriginalFileDisplay( ArrayList list ) {
            super( list, "original file" );
        }

        private OriginalFileDisplay( ArrayList list,
                                     final ClickHandler myEditableHandler ) {
            super( list, "original file", myEditableHandler );
        }

        @Override
        protected void displayList( ClickHandler myEditableHandler ) {

            for ( final Object item : getList() ) {
                final int currentRow = getRowCount();

                //
                // display object instantiation
                //

                final Label label = new Label( ( String ) item );
                final Button deleteButton = new Button( "X" );

                //
                // styles
                //
                if ( myEditableHandler != null ) {
                    label.addClickHandler( myEditableHandler );
                    label.addStyleName( "clickable-text" );
                }

                //
                // positioning
                //
                setWidget( currentRow, 0, label );
                if ( myEditableHandler != null ) {
                    setWidget( currentRow, 1, deleteButton );
                }

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
