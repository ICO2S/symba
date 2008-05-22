package net.sourceforge.symba.lsid.webservices.service;

/**
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */

public class LsidDataServiceResponse {

    private String response;

    public LsidDataServiceResponse() {
        response = "Please see your subversion checkout of SyMBA in the LSID webservices module. Inside" +
                "src/main/java/net.sourceforge.symba/lsid.webservices.client there is a file called" +
                "client-beans.xml. Please use this, writing yourself a client based on LsidResolveAndRetrieveClient.java" +
                "in the same directory. In this way you will be able to access the LsidDataRetriever service.";
    }

    public String getResponse() {
        return response;
    }

    public void setResponse( String response ) {
        this.response = response;
    }
}
