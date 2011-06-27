package net.sourceforge.symba.web.server;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * This servlet is based on that found in the SWF examples here: http://code.google.com/p/swfupload-gwt/source/browse/
 */
@SuppressWarnings( "serial" )
public class UploadServlet extends HttpServlet {

    private long FILE_SIZE_LIMIT = 20 * 1024 * 1024; // 20 MiB
    private final Logger logger = Logger.getLogger( "UploadServlet" );

    @SuppressWarnings( "unchecked" )
    @Override
    protected void doPost( HttpServletRequest request,
                           HttpServletResponse response )
            throws ServletException, IOException {

        try {
            ServletFileUpload fileUpload = new ServletFileUpload( new DiskFileItemFactory() );
            fileUpload.setSizeMax( FILE_SIZE_LIMIT );

            List<FileItem> items = fileUpload.parseRequest( request );

            for ( FileItem item : items ) {
                if ( item.isFormField() ) {
                    logger.log( Level.INFO, "Received form field:" );
                    logger.log( Level.INFO, "Name: " + item.getFieldName() );
                    logger.log( Level.INFO, "Value: " + item.getString() );
                } else {
                    logger.log( Level.INFO, "Received file:" );
                    logger.log( Level.INFO, "Name: " + item.getName() );
                    logger.log( Level.INFO, "Size: " + item.getSize() );
                }

                if ( !item.isFormField() ) {
                    
                    storeFileLocally( item, response );
                    // todo copy file to remote server specified by Spring

                    if ( !item.isInMemory() ) {
                        item.delete();
                    }
                }
            }
        } catch ( Exception e ) {
            logger.log( Level.SEVERE,
                    "Throwing servlet exception for unhandled exception", e );
            throw new ServletException( e );
        }
    }

    private void storeFileLocally( FileItem item,
                                   HttpServletResponse response ) throws IOException {
        
        if ( item.getSize() > FILE_SIZE_LIMIT ) {
            response.sendError( HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
                    "File size exceeds limit" );

            return;
        }

        BufferedInputStream bufferedInputStream = new BufferedInputStream( item.getInputStream() );
        // todo temp location should be specified from Spring
        String filename = "/tmp/" + item.getName();
        logger.log( Level.INFO, "new filename is " + filename );

        FileOutputStream output = new FileOutputStream( filename );
        int i;
        while ( ( i = bufferedInputStream.read() ) != -1 ) {
            output.write( i );
        }
        bufferedInputStream.close();
        output.close();

    }

}
