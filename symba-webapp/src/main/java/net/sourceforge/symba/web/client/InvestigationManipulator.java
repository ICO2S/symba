package net.sourceforge.symba.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.gui.EditInvestigationTable;
import net.sourceforge.symba.web.client.gui.SummariseInvestigationPanel;
import net.sourceforge.symba.web.shared.Contact;
import org.swfupload.client.File;
import org.swfupload.client.SWFUpload;
import org.swfupload.client.UploadBuilder;
import org.swfupload.client.event.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InvestigationManipulator implements EntryPoint {

    SWFUpload uploadToExistingStep, uploadToNewStep;
    private List<File> files = new ArrayList<File>();
    private VerticalPanel centerPanel;
    private HTML northHtml;
    private FlexTable southTable;
    private EditInvestigationTable editTable;
    private HTML eastHtml;
    private final String toNewStepImageUrl = "/images/toNewStep70w76h.png";
    private final String toExistingStepImageUrl = "/images/toExistingStep70w52h.png";

    private HashMap<String, Integer> fileIdToRow;
    private int radioRowSelectedOnUpload;

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
        southTable = new FlexTable();
        southTable.addStyleName( "fieldset flash" );
//        southTable.setWidth( "40%" );
        eastHtml = new HTML( "<em>You can only upload files once you have selected an experimental step." +
                "Do not upload more files until the files you have selected have completed.</em>" );
//        eastHtml.setWidth( "25%" );
        fileIdToRow = new HashMap<String, Integer>();
        radioRowSelectedOnUpload = -1;

        final SummariseInvestigationPanel investigatePanel = new SummariseInvestigationPanel( rpcService );
        investigatePanel.fetchInvestigationDetails();
//        investigatePanel.setWidth( "35%" );

        container.setWidth( "100%" );

        container.add( northHtml, DockPanel.NORTH );
        northHtml.setHTML( "This is the NORTH panel 3" );

        container.add( southTable, DockPanel.SOUTH );
        container.add( eastHtml, DockPanel.EAST );

//        container.add(head_, DockPanel.NORTH);
        container.add( investigatePanel, DockPanel.WEST );
//        container.setCellWidth(expTree_, TREE_WIDTH);
//        container.add(foot_, DockPanel.SOUTH);

        RootPanel.get().add( container );

        centerPanel = new VerticalPanel();
        container.add( centerPanel, DockPanel.CENTER );

        // todo disable all functions on entire page until file uploads are complete
        setupMultipleFileUploader();

        rpcService.getAllContacts( new AsyncCallback<HashMap<String, Contact>>() {
            public void onFailure( Throwable caught ) {
                Window.alert( "Error fetching contacts list: " + caught.getMessage() );
            }

            public void onSuccess( HashMap<String, Contact> result ) {
                editTable = new EditInvestigationTable( rpcService, result, investigatePanel,
                        uploadToExistingStep, uploadToNewStep );
                investigatePanel.addDefaultHandlers( editTable );
                centerPanel.add( editTable );
            }
        } );
    }

    private void setupMultipleFileUploader() {
        String moduleBase = GWT.getModuleBaseURL();
        String moduleName = GWT.getModuleName();
        String baseApp = moduleBase.substring( 0, moduleBase.lastIndexOf( moduleName ) );
        String url = baseApp + "upload";

        northHtml.setHTML( "SyMBA upload service is running on " + url );

        // Determine if the demo is being viewed in Hosted mode, if so display a
        // warning. This is because Flash to JavaScript communications does not work
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

        setupExistingStepBuilder( baseApp, url );
        setupNewStepBuilder( baseApp, url );

    }

    private void setupNewStepBuilder( String baseApp,
                                      String url ) {

        final UploadBuilder builder = new UploadBuilder();
        // builder.setDebug(true);
        builder.setHTTPSuccessCodes( 200, 201 );
        builder.setFileTypes(
                "*.wma;*.wmv;*.avi;*.mpg;*.mpeg;*.mp4;*.mov;*.m4v;*.aac;*.mp3;*.wav;*.png;*.jpg;*.jpeg;*.gif;*.svg;*.txt;*.doc;*.xls;*.odt" );
        builder.setFileTypesDescription( "Images, Video & Text" );

        builder.setButtonPlaceholderID( "uploadToNewStep-button" );
        builder.setButtonImageURL( baseApp + toNewStepImageUrl );
//        builder.setButtonText( "Each file is added to a copy of the selected step" );
        builder.setButtonDisabled( true );
        builder.setButtonCursor( SWFUpload.ButtonCursor.ARROW );
        builder.setButtonWidth( 70 );
        builder.setButtonHeight( 76 );
        builder.setButtonAction( SWFUpload.ButtonAction.SELECT_FILES );

        builder.setUploadProgressHandler( new UploadProgressHandler() {

            public void onUploadProgress( UploadProgressEvent e ) {
                southTable.setWidget( fileIdToRow.get( e.getFile().getId() ), 0, new HTML(
                        e.getFile().getName() + ": " + ( ( e.getBytesComplete() / e.getBytesTotal() ) * 100 ) + "%" ) );
                southTable.getCellFormatter()
                        .addStyleName( fileIdToRow.get( e.getFile().getId() ), 0, "progressContainer yellow" );
            }
        } );

        builder.setUploadSuccessHandler( new UploadSuccessHandler() {
            public void onUploadSuccess( UploadSuccessEvent e ) {
                southTable.getCellFormatter()
                        .addStyleName( fileIdToRow.get( e.getFile().getId() ), 0, "progressContainer blue" );

            }
        } );

        builder.setUploadErrorHandler( new UploadErrorHandler() {
            public void onUploadError( UploadErrorEvent e ) {
                String message = e.getMessage();
                if ( message == null || message.trim().length() == 0 ) {
                    message = "uploadToExistingStep failed";
                }
                eastHtml.setHTML(
                        eastHtml.getText() + "<br />Upload error: " + e.getFile().getId() + ", " +
                                e.getFile().getName() + " / " +
                                message );
                removeFile( e.getFile().getId() );
                if ( files.size() > 0 ) {
                    String id = files.get( 0 ).getId();
                    southTable.setWidget( fileIdToRow.get( id ), 0, new HTML( files.get( 0 ).getName() ) );
                    southTable.getCellFormatter()
                            .addStyleName( fileIdToRow.get( id ), 0, "progressContainer yellow" );
                    uploadToNewStep.startUpload( id );
                }
            }
        } );

        builder.setUploadURL( url );

        builder.setUploadCompleteHandler( new UploadCompleteHandler() {
            public void onUploadComplete( UploadCompleteEvent e ) {
                File f = e.getFile();
                removeFile( f.getId() );
                if ( files.size() > 0 ) {
                    String id = files.get( 0 ).getId();
                    southTable.setWidget( fileIdToRow.get( id ), 0, new HTML( files.get( 0 ).getName() ) );
                    southTable.getCellFormatter()
                            .addStyleName( fileIdToRow.get( id ), 0, "progressContainer yellow" );
                    uploadToNewStep.startUpload( id );
                }
            }
        } );

        builder.setFileQueuedHandler( new FileQueuedHandler() {
            public void onFileQueued( FileQueuedEvent event ) {
                files.add( event.getFile() );
            }
        } );

        builder.setFileDialogCompleteHandler( new FileDialogCompleteHandler() {
            public void onFileDialogComplete( FileDialogCompleteEvent e ) {
//                southTable.setHTML( wrapInFileQueueStyle( "", "files = " + files.size() ) );
                if ( files.size() > 0 ) {
                    // reset table variables
                    southTable.removeAllRows();
                    fileIdToRow = new HashMap<String, Integer>();
                    int fileIdToRowCount = 0;
                    // fill in fileIdToRow
                    for ( File file : files ) {
                        fileIdToRow.put( file.getId(), fileIdToRowCount++ );
                    }
                    // next, set the value in the appropriate table row.
                    String id = files.get( 0 ).getId();
                    southTable.setWidget( fileIdToRow.get( id ), 0, new HTML( files.get( 0 ).getName() + ": 0%" ) );
                    southTable.getCellFormatter()
                            .addStyleName( fileIdToRow.get( id ), 0, "progressContainer yellow" );
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
                "*.wma;*.wmv;*.avi;*.mpg;*.mpeg;*.mp4;*.mov;*.m4v;*.aac;*.mp3;*.wav;*.png;*.jpg;*.jpeg;*.gif;*.svg;*.txt;*.doc;*.xls;*.odt" );
        builder.setFileTypesDescription( "Images, Video & Text" );

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
                southTable.setWidget( fileIdToRow.get( e.getFile().getId() ), 0, new HTML(
                        e.getFile().getName() + ": " + ( ( e.getBytesComplete() / e.getBytesTotal() ) * 100 ) + "%" ) );
                southTable.getCellFormatter()
                        .addStyleName( fileIdToRow.get( e.getFile().getId() ), 0, "progressContainer yellow" );
            }
        } );

        builder.setUploadSuccessHandler( new UploadSuccessHandler() {
            public void onUploadSuccess( UploadSuccessEvent e ) {
                southTable.getCellFormatter()
                        .addStyleName( fileIdToRow.get( e.getFile().getId() ), 0, "progressContainer blue" );

                // now assign this file to the appropriate experimental step
                editTable.assignFileToStep( e.getFile(), radioRowSelectedOnUpload );
            }
        } );

        builder.setUploadErrorHandler( new UploadErrorHandler() {
            public void onUploadError( UploadErrorEvent e ) {
                southTable.getCellFormatter()
                        .addStyleName( fileIdToRow.get( e.getFile().getId() ), 0, "progressContainer red" );
                String message = e.getMessage();
                if ( message == null || message.trim().length() == 0 ) {
                    message = "uploadToExistingStep failed";
                }
                eastHtml.setHTML(
                        eastHtml.getText() + "<br />Upload error: " + e.getFile().getId() + ", " +
                                e.getFile().getName() + " / " +
                                message );
                removeFile( e.getFile().getId() );
                if ( files.size() > 0 ) {
                    String id = files.get( 0 ).getId();
                    southTable.setWidget( fileIdToRow.get( id ), 0, new HTML( files.get( 0 ).getName() ) );
                    southTable.getCellFormatter()
                            .addStyleName( fileIdToRow.get( id ), 0, "progressContainer yellow" );
                    uploadToExistingStep.startUpload( id );
                }
            }
        } );

        builder.setUploadURL( url );

        builder.setUploadCompleteHandler( new UploadCompleteHandler() {
            public void onUploadComplete( UploadCompleteEvent e ) {
                File f = e.getFile();
                removeFile( f.getId() );
                if ( files.size() > 0 ) {
                    String id = files.get( 0 ).getId();
                    southTable.setWidget( fileIdToRow.get( id ), 0, new HTML( files.get( 0 ).getName() ) );
                    southTable.getCellFormatter()
                            .addStyleName( fileIdToRow.get( id ), 0, "progressContainer yellow" );
                    uploadToExistingStep.startUpload( id );
                }
            }
        } );

        builder.setFileQueuedHandler( new FileQueuedHandler() {
            public void onFileQueued( FileQueuedEvent event ) {
                files.add( event.getFile() );
            }
        } );

        builder.setDialogStartHandler( new DialogStartHandler() {
            public void onDialogStart() {
                radioRowSelectedOnUpload = editTable.getSelectedRadioRow();
            }
        } );

        builder.setFileDialogCompleteHandler( new FileDialogCompleteHandler() {
            public void onFileDialogComplete( FileDialogCompleteEvent e ) {
                if ( files.size() > 0 ) {
                    // reset table variables
                    southTable.removeAllRows();
                    fileIdToRow = new HashMap<String, Integer>();
                    int fileIdToRowCount = 0;
                    // fill in fileIdToRow
                    for ( File file : files ) {
                        fileIdToRow.put( file.getId(), fileIdToRowCount++ );
                    }
                    // next, set the value in the appropriate table row.
                    String id = files.get( 0 ).getId();
                    southTable.setWidget( fileIdToRow.get( id ), 0, new HTML( files.get( 0 ).getName() + ": 0%" ) );
                    southTable.getCellFormatter()
                            .addStyleName( fileIdToRow.get( id ), 0, "progressContainer yellow" );
                    uploadToExistingStep.startUpload( id );
                }
            }
        } );
        uploadToExistingStep = builder.build();

    }
}
