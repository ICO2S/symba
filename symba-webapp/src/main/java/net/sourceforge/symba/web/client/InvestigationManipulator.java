package net.sourceforge.symba.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.gui.EditInvestigationTable;
import net.sourceforge.symba.web.client.gui.panel.SymbaControllerPanel;
import org.swfupload.client.File;
import org.swfupload.client.SWFUpload;
import org.swfupload.client.UploadBuilder;
import org.swfupload.client.event.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InvestigationManipulator implements EntryPoint {

//    SWFUpload uploadToExistingStep, uploadToNewStep;
    private List<File> files = new ArrayList<File>();
    private FlexTable southTable;
    private final String toNewStepImageUrl = "/images/toNewStep70w76h.png";
    private final String toExistingStepImageUrl = "/images/toExistingStep70w52h.png";
    private SymbaControllerPanel symba;

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
        symba = new SymbaControllerPanel( rpcService );
        southTable = ( FlexTable ) symba.getSouthWidget();
        fileIdToRow = new HashMap<String, Integer>();
        radioRowSelectedOnUpload = -1;

        RootPanel.get().add( symba );

        // todo disable all functions on entire page until file uploads are complete

    }


}
