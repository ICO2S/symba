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

    private boolean disabled = false;
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
        southHtml = new HTML();
        eastHtml = new HTML();

        SummariseInvestigationPanel investigatePanel = new SummariseInvestigationPanel( rpcService );
        investigatePanel.fetchInvestigationDetails();

        EditInvestigationTable editTable = new EditInvestigationTable( rpcService, investigatePanel );
        investigatePanel.addDefaultHandlers( editTable );

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

//        vp.setSpacing( 5 );
//        RootPanel.get().add( container );
        HorizontalPanel tempPanel = new HorizontalPanel();
        HTML bt = new HTML( "<span id=\"uploadToExistingStep-button\" />" );
        tempPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
        tempPanel.setSpacing( 20 );
        tempPanel.add( bt );
        HTML bt2 = new HTML( "<span id=\"uploadToNewStep-button\" />" );
        tempPanel.add( bt2 );
        centerPanel.add( tempPanel );
//        button.addClickHandler( new ClickHandler() {
//            public void onClick( ClickEvent event ) {
//                if ( disabled ) {
//                    uploadToExistingStep.setButtonDisabled( false );
//                    uploadToExistingStep.setButtonCursor( SWFUpload.ButtonCursor.HAND.getValue() );
//                    disabled = false;
//                } else {
//                    uploadToExistingStep.setButtonDisabled( true );
//                    uploadToExistingStep.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );
//                    disabled = true;
//                }
//            }
//        } );

//        final HTML html = new HTML( "----" );
//        HorizontalPanel hp = new HorizontalPanel();
//        hp.setSpacing( 5 );
//        RootPanel.get().add( hp );
//        hp.setWidth( "100%" );
//        hp.add( html );
        southHtml.setHTML( "----" );
//        final HTML html2 = new HTML( GWT.getHostPageBaseURL() + "<br />" + GWT.getModuleBaseURL() +
//                "<br />" + GWT.getModuleName() );
//        hp.add( html2 );
        eastHtml.setHTML( GWT.getHostPageBaseURL() + "<br />" + GWT.getModuleBaseURL() +
                "<br />" + GWT.getModuleName() );
        // -----------------

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
        builder.setButtonDisabled( false );
        builder.setButtonCursor( SWFUpload.ButtonCursor.HAND );
        builder.setButtonWidth( 70 );
        builder.setButtonHeight( 76 );
        builder.setButtonAction( SWFUpload.ButtonAction.SELECT_FILES );

        builder.setUploadProgressHandler( new UploadProgressHandler() {

            public void onUploadProgress( UploadProgressEvent e ) {
                File f = e.getFile();

                f.getName();
                String text = southHtml.getHTML();
                text += "<br />" + e.getBytesComplete() + "; " + f.getName();
                southHtml.setHTML( text );
            }
        } );

        builder.setUploadSuccessHandler( new UploadSuccessHandler() {
            public void onUploadSuccess( UploadSuccessEvent e ) {
                String t = southHtml.getHTML();
                t += "<br />server data : " + e.getServerData();
                southHtml.setHTML( t );
            }
        } );

        builder.setUploadErrorHandler( new UploadErrorHandler() {
            public void onUploadError( UploadErrorEvent e ) {
                File ff = e.getFile();
                String message = e.getMessage();
                if ( message == null || message.trim().length() == 0 ) {
                    message = "uploadToNewStep failed";
                }
                String t = southHtml.getHTML();
                t += "<br />error: " + ff.getId() + ", " + ff.getName() + " / " + message;
                southHtml.setHTML( t );
                removeFile( ff.getId() );
                if ( files.size() > 0 ) {
                    ff = files.get( 0 );
                    String id = ff.getId();
                    String tt = southHtml.getHTML();
                    tt += "<br />start: " + id;
                    southHtml.setHTML( tt );
                    uploadToNewStep.startUpload( id );
                }
            }
        } );

        builder.setUploadURL( url );

        builder.setUploadCompleteHandler( new UploadCompleteHandler() {
            public void onUploadComplete( UploadCompleteEvent e ) {
                File f = e.getFile();
                String t = southHtml.getHTML();
                t += "<br />done : " + f.getId() + ", " + f.getName();
                southHtml.setHTML( t );
                removeFile( f.getId() );
                if ( files.size() > 0 ) {
                    File ff = files.get( 0 );
                    String id = ff.getId();
                    String tt = southHtml.getHTML();
                    tt += "<br />start: " + id;
                    southHtml.setHTML( tt );
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
                southHtml.setHTML( "files = " + files.size() );
                if ( files.size() > 0 ) {
                    File ff = files.get( 0 );
                    String id = ff.getId();
                    String t = southHtml.getHTML();
                    t += "<br />start: " + id;
                    southHtml.setHTML( t );
                    uploadToNewStep.startUpload( id );
                }
            }
        } );
        uploadToNewStep = builder.build();

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
        builder.setButtonDisabled( false );
        builder.setButtonCursor( SWFUpload.ButtonCursor.HAND );
        builder.setButtonWidth( 70 );
        builder.setButtonHeight( 52 );
        builder.setButtonAction( SWFUpload.ButtonAction.SELECT_FILES );

        builder.setUploadProgressHandler( new UploadProgressHandler() {

            public void onUploadProgress( UploadProgressEvent e ) {
                File f = e.getFile();

                f.getName();
                String text = southHtml.getHTML();
                text += "<br />" + e.getBytesComplete() + "; " + f.getName();
                southHtml.setHTML( text );
            }
        } );

        builder.setUploadSuccessHandler( new UploadSuccessHandler() {
            public void onUploadSuccess( UploadSuccessEvent e ) {
                String t = southHtml.getHTML();
                t += "<br />server data : " + e.getServerData();
                southHtml.setHTML( t );
            }
        } );

        builder.setUploadErrorHandler( new UploadErrorHandler() {
            public void onUploadError( UploadErrorEvent e ) {
                File ff = e.getFile();
                String message = e.getMessage();
                if ( message == null || message.trim().length() == 0 ) {
                    message = "uploadToExistingStep failed";
                }
                String t = southHtml.getHTML();
                t += "<br />error: " + ff.getId() + ", " + ff.getName() + " / " + message;
                southHtml.setHTML( t );
                removeFile( ff.getId() );
                if ( files.size() > 0 ) {
                    ff = files.get( 0 );
                    String id = ff.getId();
                    String tt = southHtml.getHTML();
                    tt += "<br />start: " + id;
                    southHtml.setHTML( tt );
                    uploadToExistingStep.startUpload( id );
                }
            }
        } );

        builder.setUploadURL( url );

        builder.setUploadCompleteHandler( new UploadCompleteHandler() {
            public void onUploadComplete( UploadCompleteEvent e ) {
                File f = e.getFile();
                String t = southHtml.getHTML();
                t += "<br />done : " + f.getId() + ", " + f.getName();
                southHtml.setHTML( t );
                removeFile( f.getId() );
                if ( files.size() > 0 ) {
                    File ff = files.get( 0 );
                    String id = ff.getId();
                    String tt = southHtml.getHTML();
                    tt += "<br />start: " + id;
                    southHtml.setHTML( tt );
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
                southHtml.setHTML( "files = " + files.size() );
                if ( files.size() > 0 ) {
                    File ff = files.get( 0 );
                    String id = ff.getId();
                    String t = southHtml.getHTML();
                    t += "<br />start: " + id;
                    southHtml.setHTML( t );
                    uploadToExistingStep.startUpload( id );
                }
            }
        } );
        uploadToExistingStep = builder.build();

    }
}
