package net.sourceforge.symba.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.gui.EditInvestigationTable;
import net.sourceforge.symba.web.client.gui.SummariseInvestigationPanel;
import org.swfupload.client.File;
import org.swfupload.client.SWFUpload;
import org.swfupload.client.UploadBuilder;
import org.swfupload.client.event.*;

import java.util.ArrayList;
import java.util.List;

public class InvestigationManipulator implements EntryPoint {

    SWFUpload uploadToExistingStep, uploadToNewStep;
    private List<File> files = new ArrayList<File>();
    VerticalPanel centerPanel;
    HTML northHtml;
    HTML southHtml;
    HTML eastHtml;
    private final String toNewStepImageUrl = "/images/toNewStep70w76h.png";
    private final String toExistingStepImageUrl = "/images/toExistingStep70w52h.png";

    private void removeFile( String id ) {
        for ( File ff : files ) {
            if ( ff.getId().equals( id ) ) {
                files.remove( ff );
            }
        }
    }

    public void onModuleLoad() {

        final InvestigationsServiceAsync rpcService = GWT.create( InvestigationsService.class );

        DockPanel container = new DockPanel();
        northHtml = new HTML();
        southHtml = new HTML( wrapInFileQueueStyle( "", "" ) );
        eastHtml = new HTML();

        SummariseInvestigationPanel investigatePanel = new SummariseInvestigationPanel( rpcService );
        investigatePanel.fetchInvestigationDetails();

        container.setWidth( "100%" );

        container.add( northHtml, DockPanel.NORTH );
        northHtml.setHTML( "This is the NORTH panel 3" );

        container.add( southHtml, DockPanel.SOUTH );
        container.add( eastHtml, DockPanel.EAST );

//        container.add(head_, DockPanel.NORTH);
        container.add( investigatePanel, DockPanel.WEST );
//        container.setCellWidth(expTree_, TREE_WIDTH);
//        container.add(foot_, DockPanel.SOUTH);

        RootPanel.get().add( container );

        centerPanel = new VerticalPanel();
        container.add( centerPanel, DockPanel.CENTER );

        setupMultipleFileUploader();

        EditInvestigationTable editTable = new EditInvestigationTable( rpcService, investigatePanel,
                uploadToExistingStep, uploadToNewStep );
        investigatePanel.addDefaultHandlers( editTable );

        centerPanel.add( editTable );
    }

    private void setupMultipleFileUploader() {
        String moduleBase = GWT.getModuleBaseURL();
        String moduleName = GWT.getModuleName();
        String baseApp = moduleBase.substring( 0, moduleBase.lastIndexOf( moduleName ) );
        String url = baseApp + "upload";

        northHtml.setHTML( "SyMBA upload service is running on " + url );

        // Determine if the demo is being viewed in Hosted mode, if so display a
        // warning.
        // This is because Flash to JavaScript communications does not work
        // correctly in hosted mode.
        if ( !GWT.isScript() ) {
            HTML warning = new HTML();
            warning.addStyleName( "note" );
            warning.setHTML( "NIS: Not in scripting mode. You have to deploy the app to an application server! (" +
                    url + ")" );
            warning.setTitle( "Not in scripting mode. You have to deploy the app to an application server! (" +
                    url + ")" );
            centerPanel.add( warning );
            return;
        }

        HorizontalPanel tempPanel = new HorizontalPanel();
        HTML bt = new HTML( "<span id=\"uploadToExistingStep-button\" />" );
        tempPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
        tempPanel.setSpacing( 20 );
        tempPanel.add( bt );
        HTML bt2 = new HTML( "<span id=\"uploadToNewStep-button\" />" );
        tempPanel.add( bt2 );
        centerPanel.add( tempPanel );

        eastHtml.setHTML( GWT.getHostPageBaseURL() + "<br />" + GWT.getModuleBaseURL() +
                "<br />" + GWT.getModuleName() );

        setupExistingStepBuilder( baseApp, url );
        setupNewStepBuilder( baseApp, url );

    }

    private void setupNewStepBuilder( String baseApp,
                                      String url ) {

        final UploadBuilder builder = new UploadBuilder();
        // builder.setDebug(true);
        builder.setHTTPSuccessCodes( 200, 201 );
        builder.setFileTypes(
                "*.asf;*.wma;*.wmv;*.avi;*.flv;*.swf;*.mpg;*.mpeg;*.mp4;*.mov;*.m4v;*.aac;*.mp3;*.wav;*.png;*.jpg;*.jpeg;*.gif" );
        builder.setFileTypesDescription( "Images, Video & Sound" );

        builder.setButtonPlaceholderID( "uploadToNewStep-button" );
        builder.setButtonImageURL( baseApp + toNewStepImageUrl );
//        builder.setButtonText( "Each file is added to a copy of the selected step" );
//        builder.setButtonTextTopPadding( 5 );
        builder.setButtonDisabled( true );
        builder.setButtonCursor( SWFUpload.ButtonCursor.ARROW );
        builder.setButtonWidth( 70 );
        builder.setButtonHeight( 76 );
        builder.setButtonAction( SWFUpload.ButtonAction.SELECT_FILES );

        builder.setUploadProgressHandler( new UploadProgressHandler() {

            public void onUploadProgress( UploadProgressEvent e ) {
                File f = e.getFile();

                f.getName();
                southHtml.setHTML( wrapInFileQueueStyle( southHtml.getHTML(),
                        "<br />" + e.getBytesComplete() + "; " + f.getName() ) );
            }
        } );

        builder.setUploadSuccessHandler( new UploadSuccessHandler() {
            public void onUploadSuccess( UploadSuccessEvent e ) {
                southHtml.setHTML(
                        wrapInFileQueueStyle( southHtml.getHTML(), "<br />server data : " + e.getServerData() ) );
            }
        } );

        builder.setUploadErrorHandler( new UploadErrorHandler() {
            public void onUploadError( UploadErrorEvent e ) {
                File ff = e.getFile();
                String message = e.getMessage();
                if ( message == null || message.trim().length() == 0 ) {
                    message = "uploadToNewStep failed: " + e.getMessage();
                }
                southHtml.setHTML( wrapInFileQueueStyle( southHtml.getHTML(),
                        "<br />error: " + ff.getId() + ", " + ff.getName() + " / " + message ) );
                removeFile( ff.getId() );
                if ( files.size() > 0 ) {
                    ff = files.get( 0 );
                    String id = ff.getId();
                    southHtml.setHTML( wrapInFileQueueStyle( southHtml.getHTML(), "<br />start: " + id ) );
                    uploadToNewStep.startUpload( id );
                }
            }
        } );

        builder.setUploadURL( url );

        builder.setUploadCompleteHandler( new UploadCompleteHandler() {
            public void onUploadComplete( UploadCompleteEvent e ) {
                File f = e.getFile();
                southHtml.setHTML(
                        wrapInFileQueueStyle( southHtml.getHTML(), "<br />done : " + f.getId() + ", " + f.getName() ) );
                removeFile( f.getId() );
                if ( files.size() > 0 ) {
                    File ff = files.get( 0 );
                    String id = ff.getId();
                    southHtml.setHTML( wrapInFileQueueStyle( southHtml.getHTML(), "<br />start: " + id ) );
                    uploadToNewStep.startUpload( id );
                }
            }
        } );

        builder.setFileQueuedHandler( new FileQueuedHandler() {
            public void onFileQueued( FileQueuedEvent event ) {
                String t = eastHtml.getHTML();
                t += "<br />ofq: " + event.getFile().getId() + "; "
                        + event.getFile().getName();
                eastHtml.setHTML( t );
                files.add( event.getFile() );
            }
        } );

        builder.setFileDialogCompleteHandler( new FileDialogCompleteHandler() {
            public void onFileDialogComplete( FileDialogCompleteEvent e ) {
                southHtml.setHTML( wrapInFileQueueStyle( "", "files = " + files.size() ) );
                if ( files.size() > 0 ) {
                    File ff = files.get( 0 );
                    String id = ff.getId();
                    southHtml.setHTML( wrapInFileQueueStyle( southHtml.getHTML(), "<br />start: " + id ) );
                    uploadToNewStep.startUpload( id );
                }
            }
        } );
        uploadToNewStep = builder.build();

    }

    private String wrapInFileQueueStyle( String originalHtml,
                                         String appendedHtml ) {
        final String startDiv = "<div class=\"fieldset flash\"> <span class=\"legend\">Upload Queue</span>";
        final String endDiv = "</div>";

        // remove the div from the original string, if present
        if ( originalHtml.indexOf( startDiv ) != -1 ) {
            originalHtml = originalHtml.substring( startDiv.length() );
            originalHtml = originalHtml.substring( 0, originalHtml.length() - endDiv.length() );
        }

        return startDiv + originalHtml + appendedHtml + endDiv;
    }

    private void setupExistingStepBuilder( String baseApp,
                                           String url ) {
        final UploadBuilder builder = new UploadBuilder();
        // builder.setDebug(true);
        builder.setHTTPSuccessCodes( 200, 201 );
        builder.setFileTypes(
                "*.asf;*.wma;*.wmv;*.avi;*.flv;*.swf;*.mpg;*.mpeg;*.mp4;*.mov;*.m4v;*.aac;*.mp3;*.wav;*.png;*.jpg;*.jpeg;*.gif" );
        builder.setFileTypesDescription( "Images, Video & Sound" );

        builder.setButtonPlaceholderID( "uploadToExistingStep-button" );
        builder.setButtonImageURL( baseApp + toExistingStepImageUrl );
//        builder.setButtonText( "All files are added to the selected step" );
        builder.setButtonDisabled( true );
        builder.setButtonCursor( SWFUpload.ButtonCursor.ARROW );
        builder.setButtonWidth( 70 );
        builder.setButtonHeight( 52 );
        builder.setButtonAction( SWFUpload.ButtonAction.SELECT_FILES );

        builder.setUploadProgressHandler( new UploadProgressHandler() {

            public void onUploadProgress( UploadProgressEvent e ) {
                File f = e.getFile();

                f.getName();
                southHtml.setHTML( wrapInFileQueueStyle( southHtml.getHTML(),
                        "<br />" + e.getBytesComplete() + "; " + f.getName() ) );
            }
        } );

        builder.setUploadSuccessHandler( new UploadSuccessHandler() {
            public void onUploadSuccess( UploadSuccessEvent e ) {
                southHtml.setHTML(
                        wrapInFileQueueStyle( southHtml.getHTML(), "<br />server data : " + e.getServerData() ) );
            }
        } );

        builder.setUploadErrorHandler( new UploadErrorHandler() {
            public void onUploadError( UploadErrorEvent e ) {
                File ff = e.getFile();
                String message = e.getMessage();
                if ( message == null || message.trim().length() == 0 ) {
                    message = "uploadToExistingStep failed: " + e.getMessage();
                }
                southHtml.setHTML( wrapInFileQueueStyle( southHtml.getHTML(),
                        "<br />error: " + ff.getId() + ", " + ff.getName() + " / " + message ) );
                removeFile( ff.getId() );
                if ( files.size() > 0 ) {
                    ff = files.get( 0 );
                    String id = ff.getId();
                    southHtml.setHTML( wrapInFileQueueStyle( southHtml.getHTML(), "<br />start: " + id ) );
                    uploadToExistingStep.startUpload( id );
                }
            }
        } );

        builder.setUploadURL( url );

        builder.setUploadCompleteHandler( new UploadCompleteHandler() {
            public void onUploadComplete( UploadCompleteEvent e ) {
                File f = e.getFile();
                southHtml.setHTML(
                        wrapInFileQueueStyle( southHtml.getHTML(), "<br />done : " + f.getId() + ", " + f.getName() ) );
                removeFile( f.getId() );
                if ( files.size() > 0 ) {
                    File ff = files.get( 0 );
                    String id = ff.getId();
                    southHtml.setHTML( wrapInFileQueueStyle( southHtml.getHTML(), "<br />start: " + id ) );
                    uploadToExistingStep.startUpload( id );
                }
            }
        } );

        builder.setFileQueuedHandler( new FileQueuedHandler() {
            public void onFileQueued( FileQueuedEvent event ) {
                String t = eastHtml.getHTML();
                t += "<br />ofq: " + event.getFile().getId() + "; "
                        + event.getFile().getName();
                eastHtml.setHTML( t );
                files.add( event.getFile() );
            }
        } );

        builder.setFileDialogCompleteHandler( new FileDialogCompleteHandler() {
            public void onFileDialogComplete( FileDialogCompleteEvent e ) {
                southHtml.setHTML( wrapInFileQueueStyle( "", "files = " + files.size() ) );
                if ( files.size() > 0 ) {
                    File ff = files.get( 0 );
                    String id = ff.getId();
                    southHtml.setHTML( wrapInFileQueueStyle( southHtml.getHTML(), "<br />start: " + id ) );
                    uploadToExistingStep.startUpload( id );
                }
            }
        } );
        uploadToExistingStep = builder.build();

    }
}
