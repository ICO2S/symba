package net.sourceforge.symba.querying;

import java.util.List;
import java.util.Date;
import java.io.PrintStream;

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
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-core/src/main/java/fugeOM/Common/IdentifiableDaoImpl.java $
 */
public class ExperimentSummary {

    private String fugeIdentifier;
    private String fugeName;
    private Date dateCreated;

    /**
     * Pass the results from one of the getSummaries* methods living in SymbaEntityService and they will be
     * properly stored and processed by this class.
     *
     * @param results List of Objects that is the result of any of the SymbaEntityService.getSummaries* methods
     */
    public ExperimentSummary( List results ) {

        // first value is the fuge identifier
        this.fugeIdentifier = ( String ) results.get( 0 );

        // second value is the fuge name
        this.fugeName = ( String ) results.get( 1 );

        // third value is the date created
        this.dateCreated = ( Date ) results.get( 2 );
    }

    /**
     * This version of the constructor is a way to create an instance of EquipmentSummary without having database
     * results
     * @param fugeIdentifier the FuGE.identifier value
     * @param fugeName the FuGE.name value
     * @param dateCreated the date the FuGE object was created, as defined in the FuGE.auditTrail
     */
    public ExperimentSummary( String fugeIdentifier, String fugeName, Date dateCreated ) {
        this.fugeIdentifier = fugeIdentifier;
        this.fugeName = fugeName;
        this.dateCreated = dateCreated;
    }

    public String getFugeIdentifier() {
        return fugeIdentifier;
    }

    public String getFugeName() {
        return fugeName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        ExperimentSummary that = ( ExperimentSummary ) o;

        if ( dateCreated != null ? !dateCreated.equals( that.dateCreated ) : that.dateCreated != null ) return false;
        if ( !fugeIdentifier.equals( that.fugeIdentifier ) ) return false;
        if ( fugeName != null ? !fugeName.equals( that.fugeName ) : that.fugeName != null ) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = fugeIdentifier.hashCode();
        result = 31 * result + ( fugeName != null ? fugeName.hashCode() : 0 );
        result = 31 * result + ( dateCreated != null ? dateCreated.hashCode() : 0 );
        return result;
    }

    public void write( PrintStream out ) {
        out.println( "fugeIdentifier = " + fugeIdentifier );
        out.println( "fugeName = " + fugeName );
        out.println( "dateCreated = " + dateCreated );
    }
}
