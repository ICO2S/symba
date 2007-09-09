package uk.ac.cisban.symba.backend.util.conversion.xml;

import com.ibm.lsid.LSIDException;
import fugeOM.Collection.AuditCollection;
import fugeOM.ServiceLocator;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionAuditCollectionType;
import uk.ac.cisban.symba.backend.util.conversion.helper.CisbanAuditCollectionHelper;
import uk.ac.cisban.symba.backend.util.conversion.helper.CisbanDescribableHelper;
import uk.ac.cisban.symba.backend.util.conversion.helper.CisbanIdentifiableHelper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

/*
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 *
 */

public class PeopleUnmarshaler {
    private final String XMLFilename;
    private final RealizableEntityService reService;

    public PeopleUnmarshaler( String xf ) {
        ServiceLocator serviceLocator = ServiceLocator.instance();
        this.reService = serviceLocator.getRealizableEntityService();
        this.XMLFilename = xf;
    }

    public void Jaxb2ToFuGE() throws JAXBException, FileNotFoundException, RealizableEntityServiceException, LSIDException, URISyntaxException {

        // create a JAXBContext capable of handling classes generated into
        // the fugeOM.util.generatedJAXB2 package
        JAXBContext jc = JAXBContext.newInstance( "fugeOM.util.generatedJAXB2" );

        // create an Unmarshaller
        Unmarshaller u = jc.createUnmarshaller();

        // unmarshal JUST what is normally available within an AuditCollection
        JAXBElement<?> genericTopLevelElement = ( JAXBElement<?> ) u.unmarshal( new FileInputStream( XMLFilename ) );

        // Get the AuditCollection root object. REMEMBER that we will not be loading the Collection, JUST its contents.
        FugeOMCollectionAuditCollectionType collectionType = ( FugeOMCollectionAuditCollectionType ) genericTopLevelElement
                .getValue();

        CisbanDescribableHelper cd = new CisbanDescribableHelper( reService );
        CisbanIdentifiableHelper ci = new CisbanIdentifiableHelper( reService, cd );
        // get and store all information in the database
        CisbanAuditCollectionHelper cac = new CisbanAuditCollectionHelper( reService, ci );

        // unmarshall the jaxb object without loading the collection into the database
        AuditCollection auditCollection = ( AuditCollection ) reService
                .createDescribableOb( "fugeOM.Collection.AuditCollection" );
        cac.unmarshalCollectionContents( collectionType, auditCollection );
    }
}
