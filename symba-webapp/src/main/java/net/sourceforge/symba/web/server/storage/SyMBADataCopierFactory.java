package net.sourceforge.symba.web.server.storage;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */
public class SyMBADataCopierFactory {
    /**
     * Creates an instance of the appropriate subclass of SyMBADataCopier. The subclass is selected based on information
     * received from the arguments.
     * <p/>
     * This was created to provide users with the ability to use their own method of copying data files
     * to their own types of file systems via means other than the default (scp, or secure copy).
     * <p/>
     * You can also create your own implementation of SyMBADataCopier. Create your class that extends SyMBADataCopier, then
     * update createSyMBADataCopier accordingly, keeping the parameters that go into it identical to their original state.
     *
     * @param copyType The domain name you want your identifier to include. If null, unrecognized, or "scp", will create
     *                 a ScpSyMBADataCopier
     * @param remoteDataStoreOs MUST be either "dos" or "unix".
     * @return the appropriate subclass of SyMBADataCopier
     */
    public static SyMBADataCopier createSyMBADataCopier( String copyType, String hostname, String username, String password, String directory, String remoteDataStoreOs ) {
        if ( copyType.equals( "scp" ) ) {
            return new ScpSyMBADataCopier( hostname, username, password, directory, "dos" );
        }
        // When you create your own SyMBADataCopier, put in an if-statement to divert people to that
        // class where necessary.
        // else if ( something == true ) {
        //     return new OtherTypeOfSyMBADataCopier( null );
        // }

        // default return type is ScpSyMBADataCopier. 
        return new ScpSyMBADataCopier( hostname, username, password, directory, "dos" );
    }
}
