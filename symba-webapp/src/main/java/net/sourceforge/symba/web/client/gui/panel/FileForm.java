package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;

public class FileForm extends FormPanel {
    private final ArrayList<String> fileNames;

    public FileForm( ArrayList<String> fileNames ) {
        //
        // Initial instantiation of all objects in this panel.
        //
        this.fileNames = fileNames;
        final Label fileLabel = new Label( getFileNamesString() );
        final FileUpload upload = new FileUpload();
        HorizontalPanel panel = new HorizontalPanel();
        Image submitImage = new Image();

        // FileUpload requires the POST and multi-part MIME encoding.
        setEncoding( FormPanel.ENCODING_MULTIPART );
        setMethod( FormPanel.METHOD_POST );
        setAction( "/upload" );

        upload.setName( "uploadForm" );
        // todo styles

        //
        // sort out image paths: must be done before displayStepData() is called to ensure paths are correct for the
        // display itself.
        //
        String submitImageUrl;
        if ( GWT.isScript() ) {
            String baseApp = GWT.getModuleBaseURL()
                    .substring( 0, GWT.getModuleBaseURL().lastIndexOf( GWT.getModuleName() ) );
            submitImageUrl = baseApp + "/images/68-paperclip.png";
        } else {
            // you need slightly different URLs when in development mode.
            submitImageUrl = "/images/68-paperclip.png";
        }

        submitImage.setUrl( submitImageUrl );
        submitImage.setTitle( "Attach a file to this step" );
        // When clicking on the image, the file will be submitted to the Servlet.
        submitImage.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                submit();
            }
        } );

        //
        // handlers
        //
        addSubmitHandler( new FormPanel.SubmitHandler() {
            public void onSubmit( SubmitEvent event ) {
                if ( upload.getFilename().length() == 0 ) {
                    Window.alert( "You must provide a filename" );
                    // todo error style
                    event.cancel();
                }
            }
        } );

        addSubmitCompleteHandler( new FormPanel.SubmitCompleteHandler() {
            public void onSubmitComplete( SubmitCompleteEvent event ) {
                // When the form submission is successfully completed, this event is fired
                Window.alert( event.getResults() );
                FileForm.this.fileNames.add( upload.getFilename() );
                fileLabel.setText( getFileNamesString() );
            }
        } );

        //
        // positioning
        //
        panel.add( fileLabel );
        panel.add( upload );
        panel.add( submitImage );
        setWidget( panel );
    }

    private String getFileNamesString() {
        String value = "";
        for ( String file : fileNames ) {
            value = value + file + "; ";
        }
        return value;
    }

    public ArrayList<String> getFileNames() {
        return fileNames;
    }
}
