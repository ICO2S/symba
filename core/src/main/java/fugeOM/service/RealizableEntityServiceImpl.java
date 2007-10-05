// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package fugeOM.service;

import fugeOM.Bio.Investigation.FactorValue;
import fugeOM.Bio.Material.GenericMaterial;
import fugeOM.Common.Audit.Security;
import fugeOM.Common.Audit.SecurityAccess;
import fugeOM.Common.Audit.SecurityGroup;
import fugeOM.Common.DescribableDao;
import fugeOM.Common.IdentifiableDao;
import fugeOM.Common.Protocol.GenericEquipment;
import fugeOM.Common.Protocol.GenericProtocol;
import fugeOM.Common.Protocol.GenericProtocolApplication;
import fugeOM.Common.Protocol.GenericSoftware;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
/*
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * This file was modified from an original created by the FuGE (http://fuge.sf.net) Team. Further
 * information on licensing for FuGE code is available from LICENSE.txt
 *
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 *
 */


/**
 * @see fugeOM.service.RealizableEntityService
 */
public class RealizableEntityServiceImpl
        extends fugeOM.service.RealizableEntityServiceBase {

    private boolean verbose = false;

    public void setVerbose( boolean verbose ) {
        this.verbose = verbose;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#getDescribableOb(java.lang.Long)
     */
    protected java.lang.Object handleGetDescribableOb( java.lang.Long id )
            throws java.lang.Exception {
        return getDescribableDao().load( DescribableDao.TRANSFORM_NONE, id );
    }

    /**
     * @see fugeOM.service.RealizableEntityService#getIdentifiableOb(java.lang.Long)
     */
    protected java.lang.Object handleGetIdentifiableOb( java.lang.Long id )
            throws java.lang.Exception {
        return getIdentifiableDao().load( IdentifiableDao.TRANSFORM_NONE, id );
    }

    /**
     * @see fugeOM.service.RealizableEntityService#createIdentifiableOb(java.lang.String,java.lang.String,java.lang.Object,java.lang.String)
     */
    protected java.lang.Object handleCreateIdentifiableOb( java.lang.String fugeIdentifier,
                                                           java.lang.String fugeName,
                                                           java.lang.Object fugeEndurant,
                                                           java.lang.String fullClassName )
            throws java.lang.Exception {
        Object createdIdentifiable = null;

        try {

            Method newInstanceMethod = Class.forName( fullClassName + "$Factory" ).getMethod( "newInstance", null );
            createdIdentifiable = newInstanceMethod.invoke( null, null );

            /*
              Class superclaz = this.getClass().getSuperclass();

              Method[] meths = superclaz.getMethods();

              for(int i =0; i<meths.length;i++){
                   System.out.println("Method: " + meths[i].getName());
              }
              */
            //Method test = Class.forName("fugeOM.service.RealizableEntityServiceBase").getMethod("getGenericProtocolDao",null);

            //Method daoMethod = Class.forName("fugeOM.service.RealizableEntityServiceBase").getMethod(getDao,null);

            //Class entityClass = Class.forName(fullClassName);


            Class[] args2 = {Class.forName( "java.lang.String" )};

            Class[] args6 = {Class.forName( "fugeOM.Common.Endurant" )};

            //Set the identifier...
            if ( fugeIdentifier != null ) {
                Object[] args3 = {fugeIdentifier};
                Method setIdentifierMethod = Class.forName( fullClassName ).getMethod( "setIdentifier", args2 );
                setIdentifierMethod.invoke(
                        createdIdentifiable,
                        args3 );    //should put some error checking in here... only try to do this if these are identifiable... I guess this will cause an exception if it is not...
            }

            if ( fugeName != null ) {
                Object[] args4 = {fugeName};
                Method setNameMethod = Class.forName( fullClassName ).getMethod( "setName", args2 );
                setNameMethod.invoke( createdIdentifiable, args4 );
            }

            if ( fugeEndurant != null ) {
                Object[] args5 = {fugeEndurant};
                Method setEndurantMethod = Class.forName( fullClassName ).getMethod( "setEndurant", args6 );
                setEndurantMethod.invoke( createdIdentifiable, args5 );
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return createdIdentifiable;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#createObInDB(java.lang.String,java.lang.Object)
     */
    protected java.lang.Long handleCreateObInDB( java.lang.String fullClassName, java.lang.Object describable )
            throws java.lang.Exception {
        Long dbID = null;

        Object[] args = {describable};
        Class[] args1 = {Class.forName( fullClassName )};
        String entityType = fullClassName.substring( fullClassName.lastIndexOf( "." ) + 1 );

        String getDao = "get" + entityType + "Dao";

        Method daoMethod = this.getClass().getSuperclass().getDeclaredMethod( getDao, null );
        Object daoObj = daoMethod.invoke( this, null );
        Method createMethod = Class.forName( fullClassName + "Dao" ).getMethod( "create", args1 );
        // Create the object in the database...
        createMethod.invoke( daoObj, args );

        Method getIDMethod = Class.forName( fullClassName ).getMethod( "getId", null );
        dbID = ( Long ) getIDMethod.invoke( describable, null );

        return dbID;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#createDescribableOb(java.lang.String)
     */
    protected java.lang.Object handleCreateDescribableOb( java.lang.String fullClassName )
            throws java.lang.Exception {
        Object createdDescribable = null;

        try {
            Method newInstanceMethod = Class.forName( fullClassName + "$Factory" ).getMethod( "newInstance", null );
            createdDescribable = ( Object ) newInstanceMethod.invoke( null, null );

        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return createdDescribable;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#updateObInDB(java.lang.String,java.lang.String,java.lang.Object)
     */
    protected void handleUpdateObInDB( java.lang.String entityType,
                                       java.lang.String fullClassName,
                                       java.lang.Object describable )
            throws java.lang.Exception {
        try {

            Object[] args = {describable};
            Class[] args1 = {Class.forName( fullClassName )};

            String getDao = "get" + entityType + "Dao";

            Method daoMethod = this.getClass().getSuperclass().getDeclaredMethod( getDao, null );
            Object daoObj = daoMethod.invoke( this, null );
            Method updateMethod = Class.forName( fullClassName + "Dao" ).getMethod( "update", args1 );
            //Create the Describable object in the database...
            updateMethod.invoke( daoObj, args );

        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * @see fugeOM.service.RealizableEntityService#findIdentifiable(java.lang.String)
     */
    protected java.lang.Object handleFindIdentifiable( java.lang.String fugeIdentifier )
            throws java.lang.Exception {
        Object obj = null;
        if ( verbose ) {
            System.err.println( "Searching for identifier = " + fugeIdentifier );
        }
        obj = getIdentifiableDao().getIdentifiable( fugeIdentifier );

        if ( obj == null ) {
            throw new java.lang.Exception( "No such identifier found: " + fugeIdentifier );
        }
        return obj;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#findEndurant(java.lang.String)
     */
    protected java.lang.Object handleFindEndurant( java.lang.String endurantId )
            throws java.lang.Exception {
        if ( verbose ) {
            System.err.println( "Searching for fuge endurant identifier = " + endurantId );
        }
        Object obj = getEndurantDao().getEndurant( endurantId );

        if ( obj == null ) {
            throw new java.lang.Exception( "No such fuge endurant identifier found: " + endurantId );
        }
        return obj;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#findLatestByEndurant(java.lang.String)
     */
    protected java.lang.Object handleFindLatestByEndurant( java.lang.String endurantId )
            throws java.lang.Exception {
        Date date = new Date();
        if ( verbose ) {
            System.err.println( "Searching for latest identifiable for fuge endurant = " + endurantId );
        }

        Object obj = getIdentifiableDao().findByEndurantAndDate( endurantId, date );

        if ( obj == null ) {
            throw new java.lang.Exception(
                    "No latest identifiable for the following fuge endurant found: " + endurantId );
        }
        return obj;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#findLatestIdentifierByIdentifiable(java.lang.String)
     */
    protected java.lang.String handleFindLatestIdentifierByIdentifiable( java.lang.String identifiableIdentifier )
            throws java.lang.Exception {
        String str = getIdentifiableDao().findLatestIdByIdentifier( identifiableIdentifier );

        if ( str == null ) {
            throw new java.lang.Exception( "No latest identifier for identifiable: " + identifiableIdentifier );
        }
        return str;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#findLatestIdentifierByEndurant(java.lang.String)
     */
    protected java.lang.String handleFindLatestIdentifierByEndurant( java.lang.String endurantIdentifier )
            throws java.lang.Exception {
        String str = getIdentifiableDao().findLatestIdByEndurant( endurantIdentifier );

        if ( str == null ) {
            throw new java.lang.Exception( "No latest identifier for endurant: " + endurantIdentifier );
        }
        return str;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#findLatestIdentifierByUnknown(java.lang.String)
     */
    protected java.lang.String handleFindLatestIdentifierByUnknown( java.lang.String unknownIdentifier )
            throws java.lang.Exception {
        String str = getIdentifiableDao().findLatestIdByEndurant( unknownIdentifier );

        if ( str != null ) {
            return str;
        }

        // try one more time by searching Identifiable identifiers.
        str = getIdentifiableDao().findLatestIdByIdentifier( unknownIdentifier );
        if ( str == null ) {
            throw new java.lang.Exception( "No latest identifier for unknown type: " + unknownIdentifier );
        }
        return str;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#findByIdAndDate(java.lang.String,java.util.Date)
     */
    protected java.lang.Object handleFindByIdAndDate( java.lang.String identifier, java.util.Date date )
            throws java.lang.Exception {
        if ( verbose ) {
            System.err.println(
                    "Searching for latest identifiable for fuge identifier = " + identifier + ", Using date = " +
                            date.toString() );
        }

        Object obj = getIdentifiableDao().FindByIdentifierAndDate( identifier, date );

        if ( obj == null ) {
            throw new java.lang.Exception(
                    "No identifiable for the following fuge identifier and date found: " + identifier + " " +
                            date.toString() );
        }
        return obj;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#getAllExperiments()
     */
    protected java.util.List handleGetAllExperiments()
            throws java.lang.Exception {
        Collection experiments = getFuGEDao().loadAll();
        return ( java.util.List ) experiments;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#getAllProtocols()
     */
    protected java.util.List handleGetAllProtocols()
            throws java.lang.Exception {
        return ( ArrayList ) getGenericProtocolDao().loadAll();
    }

    /**
     * @see fugeOM.service.RealizableEntityService#getAllEquipment()
     */
    protected java.util.List handleGetAllEquipment()
            throws java.lang.Exception {
        return ( ArrayList ) getGenericEquipmentDao().loadAll();
    }

    /**
     * @see fugeOM.service.RealizableEntityService#getAllExperimentsWithContact(java.lang.String)
     */
    protected java.util.List handleGetAllExperimentsWithContact( java.lang.String endurantId )
            throws java.lang.Exception {
        // Retrieves all versions of experiments containing a contact with the
        // endurant identifier "endurantId".
        List genericList = getFuGEDao().getAllWithContact( endurantId );

        if ( genericList == null ) {
            throw new java.lang.Exception(
                    "Error trying to retrieve all experiments related to Contact: " + endurantId );
        }
        return genericList;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#getAllLatestExperimentsWithContact(java.lang.String)
     */
    protected java.util.List handleGetAllLatestExperimentsWithContact( java.lang.String endurantId )
            throws java.lang.Exception {
        // Retrieves all experiments whose *latest* version contains a contact with the
        // endurant identifier "endurantId".
        List genericList = getFuGEDao().getAllLatestWithContact( endurantId );

        if ( genericList == null ) {
            throw new java.lang.Exception(
                    "Error trying to retrieve all current experiments related to Contact: " + endurantId );
        }
        return genericList;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#getAllLatestExpIdsWithContact(java.lang.String)
     */
    protected java.util.List handleGetAllLatestExpIdsWithContact( java.lang.String endurantId )
            throws java.lang.Exception {
        // Retrieves all experiment endurant identifiers whose *latest* version contains a contact with the
        // endurant identifier "endurantId".
        List genericList = getFuGEDao().getAllLatestIdsWithContact( endurantId );

        if ( genericList == null ) {
            throw new java.lang.Exception(
                    "Error trying to retrieve all current experiment endurant identifiers related to Contact: " +
                            endurantId );
        }
        return genericList;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#getAllLatestTermsWithSource(java.lang.String)
     */
    protected java.util.List handleGetAllLatestTermsWithSource( java.lang.String sourceEndurant )
            throws java.lang.Exception {
        // Retrieves the latest version of all ontology terms associated with the given ontology source.
        List genericList = getOntologyTermDao().getAllLatestWithSource( sourceEndurant );

        if ( genericList == null ) {
            throw new java.lang.Exception(
                    "Error trying to retrieve all current ontology terms related to source" + sourceEndurant + "." );
        }
        return genericList;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#getAllLatestPeople()
     */
    protected java.util.List handleGetAllLatestPeople()
            throws java.lang.Exception {
        // Retrieves the latest version of all people in the database, irrespective of experimental affiliation.
        List genericList = getPersonDao().getAllLatest();

        if ( genericList == null ) {
            throw new java.lang.Exception( "Error trying to retrieve all current people." );
        }
        return genericList;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#getAllLatestGenericProtocols()
     */
    protected java.util.List handleGetAllLatestGenericProtocols()
            throws java.lang.Exception {
        // Retrieves the latest version of all generic protocols in the database.
        List genericList = getGenericProtocolDao().getAllLatest();

        if ( genericList == null ) {
            throw new java.lang.Exception( "Error trying to retrieve all current generic protocols." );
        }
        return genericList;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#getAllLatestGenericMaterials()
     */
    protected java.util.List handleGetAllLatestGenericMaterials()
            throws java.lang.Exception {
        // Retrieves the latest version of all generic materials in the database.
        List genericList = getGenericMaterialDao().getAllLatest();

        if ( genericList == null ) {
            throw new java.lang.Exception( "Error trying to retrieve all current generic materials." );
        }
        return genericList;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#getAllPeople()
     */
    protected java.util.List handleGetAllPeople()
            throws java.lang.Exception {
        return ( List ) getPersonDao().loadAll();
    }

    /**
     * @see fugeOM.service.RealizableEntityService#greedyGet(java.lang.Object)
     */
    protected java.lang.Object handleGreedyGet( java.lang.Object lazyObj )
            throws java.lang.Exception {
        if ( lazyObj instanceof FactorValue ) {
            FactorValue fv = ( FactorValue ) lazyObj;
            fv = ( FactorValue ) getFactorValueDao().load( fv.getId() );
            // you have to actually do something with the collections to get them loaded. Not sure why the
            // tagged values to stop lazy loading aren't working, but this will do for now.
            fv.getDataPartitions().size();
            return fv;
        } else if ( lazyObj instanceof GenericProtocol ) {
            GenericProtocol gp = ( GenericProtocol ) lazyObj;
            gp = ( GenericProtocol ) getGenericProtocolDao().load( gp.getId() );
            // you have to actually do something with the collections to get them loaded. Not sure why the
            // tagged values to stop lazy loading aren't working, but this will do for now.
            gp.getInputTypes().size(); // abstract protocol attribute
            gp.getOutputTypes().size(); // abstract protocol attribute
            gp.getGenPrtclToEquip().size();
            gp.getGenSoftware().size();
            gp.getParameterizableTypes().size();
            return gp;
        } else if ( lazyObj instanceof GenericEquipment ) {
            GenericEquipment ge = ( GenericEquipment ) lazyObj;
            ge = ( GenericEquipment ) getGenericEquipmentDao().load( ge.getId() );
            // you have to actually do something with the collections to get them loaded. Not sure why the
            // tagged values to stop lazy loading aren't working, but this will do for now.
            ge.getGenEquipParts().size();
            ge.getSoftware().size();
            ge.getParameterizableTypes().size();
            return ge;
        } else if ( lazyObj instanceof GenericSoftware ) {
            GenericSoftware gs = ( GenericSoftware ) lazyObj;
            gs = ( GenericSoftware ) getGenericSoftwareDao().load( gs.getId() );
            // you have to actually do something with the collections to get them loaded. Not sure why the
            // tagged values to stop lazy loading aren't working, but this will do for now.
            gs.getParameterizableTypes().size();
            gs.getGenEquipment().size();
            return gs;
        } else if ( lazyObj instanceof GenericMaterial ) {
            GenericMaterial gm = ( GenericMaterial ) lazyObj;
            gm = ( GenericMaterial ) getGenericMaterialDao().load( gm.getId() );
            // you have to actually do something with the collections to get them loaded. Not sure why the
            // tagged values to stop lazy loading aren't working, but this will do for now.
            gm.getMaterialType();
//            gm.getMaterialType().getOntologySource().getEndurant().getIdentifier();
            gm.getCharacteristics().size();
            return gm;
        } else if ( lazyObj instanceof GenericProtocolApplication ) {
            GenericProtocolApplication gpa = ( GenericProtocolApplication ) lazyObj;
            gpa = ( GenericProtocolApplication ) getGenericProtocolApplicationDao().load( gpa.getId() );
            gpa.getGenericInputData().size();
            gpa.getGenericInputMaterials().size();
            gpa.getGenericInputCompleteMaterials().size();
            gpa.getGenericOutputData().size();
            gpa.getGenericOutputMaterials().size();
            return gpa;
        } else if ( lazyObj instanceof SecurityGroup ) {
            SecurityGroup sg = ( SecurityGroup ) lazyObj;
            sg = ( SecurityGroup ) getSecurityGroupDao().load( sg.getId() );
            // you have to actually do something with the collections to get them loaded. Not sure why the
            // tagged values to stop lazy loading aren't working, but this will do for now.
            sg.getMembers().size();
            return sg;
        } else if ( lazyObj instanceof Security ) {
            Security security = ( Security ) lazyObj;
            security = ( Security ) getSecurityDao().load( security.getId() );
            // you have to actually do something with the collections to get them loaded. Not sure why the
            // tagged values to stop lazy loading aren't working, but this will do for now.
            security.getOwner().size();
            return security;
        } else if ( lazyObj instanceof SecurityAccess ) {
            SecurityAccess access = ( SecurityAccess ) lazyObj;
            access = ( SecurityAccess ) getSecurityAccessDao().load( access.getId() );
            // you have to actually do something with the collections to get them loaded. Not sure why the
            // tagged values to stop lazy loading aren't working, but this will do for now.
            access.getAccessGroup().getIdentifier();
            return access;
        }
        return lazyObj; // if no greedy rules simply returns the lazyObject
    }

    /**
     * @see fugeOM.service.RealizableEntityService#createEndurantOb(java.lang.String,java.lang.String)
     */
    protected java.lang.Object handleCreateEndurantOb( java.lang.String fugeIdentifier, java.lang.String fullClassName )
            throws java.lang.Exception {
        Object createdEndurant = null;
        try {

            Method newInstanceMethod = Class.forName( fullClassName + "$Factory" ).getMethod( "newInstance", null );
            createdEndurant = ( Object ) newInstanceMethod.invoke( null, null );

            Class[] args2 = {Class.forName( "java.lang.String" )};
            Object[] args3 = {( Object ) fugeIdentifier};

            //Set the identifier...
            if ( fugeIdentifier != null ) {
                Method setIdentifierMethod = Class.forName( fullClassName ).getMethod( "setIdentifier", args2 );
                setIdentifierMethod.invoke(
                        createdEndurant,
                        args3 );    //should put some error checking in here... only try to do this if these are identifiable... I guess this will cause an exception if it is not...
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return createdEndurant;
    }

    /**
     * @see fugeOM.service.RealizableEntityService#createIdentifiableAndEndurantObs(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String)
     */
    protected java.lang.Object handleCreateIdentifiableAndEndurantObs( java.lang.String idenIdentifier,
                                                                       java.lang.String idenName,
                                                                       java.lang.String endurantIdentifier,
                                                                       java.lang.String idenClassName,
                                                                       java.lang.String endurantClassName )
            throws java.lang.Exception {
        if ( verbose ) {
            System.out.println( "Creating Endurant Object = " + endurantIdentifier );
        }
        Object createdEndurant = handleCreateEndurantOb( endurantIdentifier, endurantClassName );

        if ( verbose ) {
            System.out.println( "Creating Endurant Object In Database = " + endurantIdentifier );
        }
        handleCreateObInDB( endurantClassName, createdEndurant );

        if ( idenName != null )
            return handleCreateIdentifiableOb( idenIdentifier, idenName, createdEndurant, idenClassName );
        return handleCreateIdentifiableOb( idenIdentifier, "", createdEndurant, idenClassName );
    }

    /**
     * @see fugeOM.service.RealizableEntityService#countLatestExperiments()
     */
    protected int handleCountLatestExperiments()
            throws java.lang.Exception {
        return getFuGEEndurantDao().countAll();
    }

    /**
     * @see fugeOM.service.RealizableEntityService#countData()
     */
    protected int handleCountData()
            throws java.lang.Exception {
        // counts all ExternalDataEndurants.
        return getExternalDataEndurantDao().countAll();
    }

}