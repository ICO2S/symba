/**
 * Copyright Notice
 *
 * The MIT License
 *
 * Copyright (c) 2008 2007-8 Proteomics Standards Initiative / Microarray and Gene Expression Data Society
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * Acknowledgements
 *  The authors wish to thank the Proteomics Standards Initiative for
 *  the provision of infrastructure and expertise in the form of the PSI
 *  Document Process that has been used to formalise this document.
 */
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package net.sourceforge.symba.service;

import java.util.Date;

/**
 * @see net.sourceforge.symba.service.SymbaEntityService
 */
public class SymbaEntityServiceImpl
        extends net.sourceforge.symba.service.SymbaEntityServiceBase {

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getEndurant(java.lang.String)
     */
    protected net.sourceforge.symba.versioning.Endurant handleGetEndurant( java.lang.String endurantIdentifier )
            throws java.lang.Exception {

        return getEndurantDao().get( endurantIdentifier );
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getLatest(java.lang.String)
     */
    protected net.sourceforge.fuge.common.Identifiable handleGetLatest( java.lang.String identifierOrEndurant )
            throws java.lang.Exception {

        if ( identifierOrEndurant.contains( "Endurant" ) ) {
            return getIdentifiableDao().getWithEndurantForDate( identifierOrEndurant, new Date() );
        } else {
            return getIdentifiableDao().getForDate( identifierOrEndurant, new Date() );
        }
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getLatest(java.lang.String, java.util.Date)
     */
    protected net.sourceforge.fuge.common.Identifiable handleGetLatest( java.lang.String identifierOrEndurant,
                                                                        java.util.Date date )
            throws java.lang.Exception {

        if ( identifierOrEndurant.contains( "Endurant" ) ) {
            return getIdentifiableDao().getWithEndurantForDate( identifierOrEndurant, date );
        } else {
            return getIdentifiableDao().getForDate( identifierOrEndurant, date );
        }
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getLatestByEndurant(java.lang.String)
     */
    protected net.sourceforge.fuge.common.Identifiable handleGetLatestByEndurant( java.lang.String endurantIdentifier )
            throws java.lang.Exception {

        return getIdentifiableDao().getWithEndurantForDate( endurantIdentifier, new Date() );
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getLatestByEndurant(java.lang.String, java.util.Date)
     */
    protected net.sourceforge.fuge.common.Identifiable handleGetLatestByEndurant( java.lang.String endurantIdentifier,
                                                                                  java.util.Date date )
            throws java.lang.Exception {

        return getIdentifiableDao().getWithEndurantForDate( endurantIdentifier, date );
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getLatestByIdentifier(java.lang.String)
     */
    protected net.sourceforge.fuge.common.Identifiable handleGetLatestByIdentifier( java.lang.String identifier )
            throws java.lang.Exception {

        return getIdentifiableDao().getForDate( identifier, new Date() );
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getLatestByIdentifier(java.lang.String, java.util.Date)
     */
    protected net.sourceforge.fuge.common.Identifiable handleGetLatestByIdentifier( java.lang.String identifier,
                                                                                    java.util.Date date )
            throws java.lang.Exception {

        return getIdentifiableDao().getForDate( identifier, date );
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#countExperiments()
     */
    protected long handleCountExperiments()
            throws java.lang.Exception {
        return ( Long ) getFuGEDao().countLatest().iterator().next();
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#countData()
     */
    protected long handleCountData()
            throws java.lang.Exception {
        return ( Long ) getExternalDataDao().countRealData().iterator().next();
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getLatestOntologySources()
     */
    protected java.util.List handleGetLatestOntologySources()
            throws java.lang.Exception {

        return getOntologySourceDao().getLatest();
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getLatestUnsourcedTerms()
     */
    protected java.util.List handleGetLatestUnsourcedTerms()
            throws java.lang.Exception {

        return getOntologyTermDao().getLatestUnsourced();
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getLatestTermsWithSource(java.lang.String)
     */
    protected java.util.List handleGetLatestTermsWithSource( java.lang.String sourceEndurantIdentifier )
            throws java.lang.Exception {

        return getOntologyTermDao().getLatestWithSource( sourceEndurantIdentifier );
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getLatestGenericMaterials(boolean)
     */
    protected java.util.List handleGetLatestGenericMaterials( boolean dummiesOnly )
            throws java.lang.Exception {

        if ( dummiesOnly ) {
            return getGenericMaterialDao().getLatestDummies( "% Dummy%" );
        } else {
            return getGenericMaterialDao().getLatest();
        }
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getLatestExternalData(boolean)
     */
    protected java.util.List handleGetLatestExternalData( boolean dummiesOnly )
            throws java.lang.Exception {

        if ( dummiesOnly ) {
            return getExternalDataDao().getLatestDummies( "% Dummy%" );
        } else {
            return getExternalDataDao().getLatest();
        }
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getLatestGenericProtocolApplications(boolean)
     */
    protected java.util.List handleGetLatestGenericProtocolApplications( boolean dummiesOnly )
            throws java.lang.Exception {

        if ( dummiesOnly ) {
            return getGenericProtocolApplicationDao().getLatestDummies( "% Dummy%" );
        } else {
            return getGenericProtocolApplicationDao().getLatest();
        }
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getLatestGenericProtocols()
     */
    protected java.util.List handleGetLatestGenericProtocols()
            throws java.lang.Exception {

        return getGenericProtocolDao().getLatest();
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getSummaries()
     */
    protected java.util.List handleGetSummaries()
            throws java.lang.Exception {

        return getFuGEDao().getSummaries();
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getSummariesWithPartialName(java.lang.String)
     */
    protected java.util.List handleGetSummariesWithPartialName( java.lang.String query )
            throws java.lang.Exception {

        return getFuGEDao().getSummariesWithPartialName( "%" + query + "%" );
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getSummariesWithContact(java.lang.String)
     */
    protected java.util.List handleGetSummariesWithContact( java.lang.String contactEndurantIdentifier )
            throws java.lang.Exception {

        return getFuGEDao().getSummariesWithContact( contactEndurantIdentifier );
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getSummariesWithOntologyTerm(java.lang.String)
     */
    protected java.util.List handleGetSummariesWithOntologyTerm( java.lang.String ontologyTermEndurantIdentifier )
            throws java.lang.Exception {

        return getFuGEDao().getSummariesWithOntologyTerm( ontologyTermEndurantIdentifier );
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getOntologyIndividualByAccession(java.lang.String)
     */
    protected net.sourceforge.fuge.common.ontology.OntologyIndividual handleGetOntologyIndividualByAccession( java.lang.String termAccession )
            throws java.lang.Exception {

        return getOntologyIndividualDao().getByAccession( termAccession );
    }

    /**
     * @see net.sourceforge.symba.service.SymbaEntityService#getGenericSoftwareByNameAndVersion(java.lang.String, java.lang.String)
     */
    protected net.sourceforge.fuge.common.protocol.GenericSoftware handleGetGenericSoftwareByNameAndVersion( java.lang.String name, java.lang.String version )
            throws java.lang.Exception {

        return getGenericSoftwareDao().getByNameAndVersion( name, version );
    }
}