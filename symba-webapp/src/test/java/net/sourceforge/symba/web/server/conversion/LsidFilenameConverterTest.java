package net.sourceforge.symba.web.server.conversion;

import org.junit.Test;

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
public class LsidFilenameConverterTest {

    // tests the convert method using the default colon replacement string
    @Test
    public void convertTest() {

        String lsid = "urn:lsid:cisban.cisbs.org:RawData:06ce5766-bb24-46e9-934a-ea68a56f4171";
        String colonReplacement = "__";

        String result = LsidFilenameConverter.convert( lsid, colonReplacement );

        assert ( result.equals(
                "urn__lsid__cisban.cisbs.org__RawData__06ce5766-bb24-46e9-934a-ea68a56f4171" ) ) : "The LSID has not been converted properly, and is instead parse to " + result;
    }

}
