package net.sourceforge.symba.webapp.util.forms;

import net.sourceforge.symba.webapp.util.DatafileSpecificMetadataStore;
import net.sourceforge.symba.webapp.util.GenericProtocolApplicationSummary;
import net.sourceforge.symba.webapp.util.MaterialFactorsStore;
import net.sourceforge.symba.webapp.util.SymbaFormSessionBean;
import net.sourceforge.symba.webapp.util.forms.schemes.BasicScheme;
import net.sourceforge.symba.webapp.util.forms.schemes.material.*;
import net.sourceforge.symba.webapp.util.forms.schemes.protocol.ActionHierarchyScheme;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Arrays;

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
public class MaterialFormValidator {

    // will validate the material information from a form generated by MaterialTemplateParser and using
    // the MaterialFormScheme classes.
    public static SymbaFormSessionBean validate( HttpServletRequest request,
                                                 SymbaFormSessionBean symbaFormSessionBean,
                                                 String toBeIgnoredParameterName,
                                                 boolean isPartOfDataFileForm ) {

        Enumeration enumeration = request.getParameterNames();

        // assume until discover otherwise that the material characteristics are complete
        symbaFormSessionBean.setMaterialCharacteristicsIncomplete( false );

        // create instances of all of the schemes we'll use
        MtOutputAsAssayInputScheme mtScheme = new MtOutputAsAssayInputScheme();

        MaterialNameScheme materialNameScheme = new MaterialNameScheme();
        MaterialNameScheme mMaterialNameScheme = new MaterialNameScheme();
        mMaterialNameScheme.setMeasuredMaterial( true );

        OntologyReplacementScheme ontologyReplacementScheme = new OntologyReplacementScheme();
        OntologyReplacementScheme mOntologyReplacementScheme = new OntologyReplacementScheme();
        mOntologyReplacementScheme.setMeasuredMaterial( true );

        TreatmentScheme completeMaterialTreatmentScheme = new TreatmentScheme();
        TreatmentScheme mTreatmentScheme = new TreatmentScheme();
        mTreatmentScheme.setMeasuredMaterial( true );

        MaterialTypeScheme materialTypeScheme = new MaterialTypeScheme();
        MaterialTypeScheme mMaterialTypeScheme = new MaterialTypeScheme();
        mMaterialTypeScheme.setMeasuredMaterial( true );

        CharacteristicScheme characteristicScheme = new CharacteristicScheme();
        CharacteristicScheme novelCharacteristicScheme = new CharacteristicScheme();
        novelCharacteristicScheme.setNovel( true );
        CharacteristicScheme mCharacteristicScheme = new CharacteristicScheme();
        mCharacteristicScheme.setMeasuredMaterial( true );
        CharacteristicScheme mNovelCharacteristicScheme = new CharacteristicScheme();
        mNovelCharacteristicScheme.setMeasuredMaterial( true );
        mNovelCharacteristicScheme.setNovel( true );

        ActionHierarchyScheme ahs = new ActionHierarchyScheme();
        ActionHierarchyScheme dummyAhs = new ActionHierarchyScheme();
        dummyAhs.setDummy( true );

        while ( enumeration.hasMoreElements() ) {

            String parameterName = ( String ) enumeration.nextElement();
            if ( parameterName.equals( ahs.getElementTitle() ) || parameterName.equals( dummyAhs.getElementTitle() ) ) {
                // this parameter is only used within material transformation forms and not assay forms
                // As it is such a simple parameter, we don't use getMfs.
                symbaFormSessionBean.setSpecimenActionHierarchy( request.getParameter( parameterName ) );

            } else if ( parameterName.startsWith( mtScheme.getElementTitle() ) &&
                        !parameterName.equals( toBeIgnoredParameterName ) ) {
                mtScheme.parse( parameterName );
                GenericProtocolApplicationSummary gpaSummary =
                        getGpaSummary( symbaFormSessionBean, isPartOfDataFileForm, mtScheme );
                gpaSummary.getInputIdentifiersFromMaterialTransformations()
                        .addAll( Arrays.asList( request.getParameterValues( parameterName ) ) );

                symbaFormSessionBean =
                        setGpaSummary( symbaFormSessionBean, isPartOfDataFileForm, mtScheme, gpaSummary );
            } else if ( ( parameterName.startsWith( ontologyReplacementScheme.getElementTitle() ) ||
                          parameterName.startsWith( mOntologyReplacementScheme.getElementTitle() ) )
                        && !parameterName.equals( toBeIgnoredParameterName ) ) {

                // once inside this section, no need to use the two versions of the scheme: the scheme
                // values will get set appropriately.
                ontologyReplacementScheme.parse( parameterName );

                MaterialFactorsStore mfs =
                        getMfs( symbaFormSessionBean, isPartOfDataFileForm, ontologyReplacementScheme );
                mfs.putOntologyReplacementsPair( ontologyReplacementScheme.getTitleOfReplacement(),
                        request.getParameter( parameterName ) );
                symbaFormSessionBean =
                        setMfs( symbaFormSessionBean, isPartOfDataFileForm, ontologyReplacementScheme, mfs );

                // reset scheme for the next parameter
                ontologyReplacementScheme.setMeasuredMaterial( false );

            } else if ( ( parameterName.startsWith( materialNameScheme.getElementTitle() ) ||
                          parameterName.startsWith( mMaterialNameScheme.getElementTitle() ) )
                        && !parameterName.equals( toBeIgnoredParameterName ) ) {
                if ( request.getParameter( parameterName ) != null &&
                     request.getParameter( parameterName ).length() > 0 ) {

                    // once inside this section, no need to use the two versions of the scheme: the scheme
                    // values will get set appropriately.
                    materialNameScheme.parse( parameterName );

                    MaterialFactorsStore mfs = getMfs( symbaFormSessionBean, isPartOfDataFileForm, materialNameScheme );
                    mfs.setMaterialName( request.getParameter( parameterName ) );
                    symbaFormSessionBean =
                            setMfs( symbaFormSessionBean, isPartOfDataFileForm, materialNameScheme, mfs );

                    // reset scheme for the next parameter
                    materialNameScheme.setMeasuredMaterial( false );
                }
            } else if ( ( parameterName.startsWith( completeMaterialTreatmentScheme.getElementTitle() ) ||
                          parameterName.startsWith( mTreatmentScheme.getElementTitle() ) )
                        && request.getParameter( parameterName ).length() > 0 &&
                        !parameterName.equals( toBeIgnoredParameterName ) ) {

                // once inside this section, no need to use the two versions of the scheme: the scheme
                // values will get set appropriately.
                completeMaterialTreatmentScheme.parse( parameterName );

                // will generate new array each time (unless there are *no* treatments at all,
                // to prevent old choices from being copied multiple times into the array.

                MaterialFactorsStore mfs =
                        getMfs( symbaFormSessionBean, isPartOfDataFileForm, completeMaterialTreatmentScheme );
                mfs.addTreatmentInfo( request.getParameter( parameterName ) );
                symbaFormSessionBean =
                        setMfs( symbaFormSessionBean, isPartOfDataFileForm, completeMaterialTreatmentScheme, mfs );

                // reset scheme for the next parameter
                completeMaterialTreatmentScheme.setMeasuredMaterial( false );

            } else if ( ( parameterName.startsWith( materialTypeScheme.getElementTitle() ) ||
                          parameterName.startsWith( mMaterialTypeScheme.getElementTitle() ) )
                        && !parameterName.equals( toBeIgnoredParameterName ) ) {

                // once inside this section, no need to use the two versions of the scheme: the scheme
                // values will get set appropriately.
                materialTypeScheme.parse( parameterName );

                MaterialFactorsStore mfs = getMfs( symbaFormSessionBean, isPartOfDataFileForm, materialTypeScheme );
                mfs.setMaterialType( request.getParameter( parameterName ) );
                symbaFormSessionBean = setMfs( symbaFormSessionBean, isPartOfDataFileForm, materialTypeScheme, mfs );

                // reset scheme for the next parameter
                materialTypeScheme.setMeasuredMaterial( false );

            } else if ( ( parameterName.startsWith( characteristicScheme.getElementTitle() ) ||
                          parameterName.startsWith( novelCharacteristicScheme.getElementTitle() ) ||
                          parameterName.startsWith( mCharacteristicScheme.getElementTitle() ) ||
                          parameterName.startsWith( mNovelCharacteristicScheme.getElementTitle() ) )
                        && !parameterName.equals( toBeIgnoredParameterName ) ) {
                // each characteristic cannot be empty (except a new one was created and it will redirect directly to metaData.jsp),
                // and might be multiple selections, which will be separated by commas
                if ( request.getParameter( parameterName ) == null ||
                     request.getParameter( parameterName ).length() == 0 ) {
                    symbaFormSessionBean.setMaterialCharacteristicsIncomplete( true );
                } else {

                    // once inside this section, no need to use the two versions of the scheme: the scheme
                    // values will get set appropriately.
                    characteristicScheme.parse( parameterName );

                    boolean multipleAllowed = false;
                    if ( parameterName.startsWith( characteristicScheme.getMultipleElementTitle() ) ) {
                        multipleAllowed = true;
                    }

                    MaterialFactorsStore mfs =
                            getMfs( symbaFormSessionBean, isPartOfDataFileForm, characteristicScheme );

                    if ( characteristicScheme.isNovel() ) {
                        mfs.setDescriptorOiEndurant( characteristicScheme.getDescriptorOiEndurant() );
                    }

                    mfs = parseFormCharacteristics( request.getParameterValues( parameterName ),
                            characteristicScheme.getSourceEndurant(), mfs, multipleAllowed,
                            characteristicScheme.isNovel() );

                    symbaFormSessionBean =
                            setMfs( symbaFormSessionBean, isPartOfDataFileForm, characteristicScheme, mfs );

                    // reset scheme for the next parameter
                    characteristicScheme.setMeasuredMaterial( false );
                    characteristicScheme.setNovel( false );
                }
            } else {
            }
        }
        return symbaFormSessionBean;
    }

    private static MaterialFactorsStore parseFormCharacteristics( String[] parameterValues,
                                                                  String ontologySourceEndurantID,
                                                                  MaterialFactorsStore mfs,
                                                                  boolean multipleAllowed,
                                                                  boolean isNovel ) {

        // we clear the current values in the list, as we don't want to add to them, but instead replace them
        LinkedHashSet<String> multiples = new LinkedHashSet<String>();

        for ( String singleParameter : parameterValues ) {
            String[] parsedStrings = singleParameter.split( "::" );

            String toSearch = parsedStrings[0];
            if ( isNovel ) {
                toSearch = parsedStrings[0] + "::" + parsedStrings[1];
            }
            if ( multipleAllowed ) {
                multiples.add( toSearch );
            } else {
                if ( isNovel ) {
                    mfs.addNovelCharacteristic( ontologySourceEndurantID, toSearch );
                } else {
                    mfs.addCharacteristic( ontologySourceEndurantID, toSearch );
                }
            }
        }

        if ( multipleAllowed && isNovel ) {
            mfs.addNovelMultipleCharacteristics( ontologySourceEndurantID, multiples );
        } else if ( multipleAllowed ) {
            mfs.addMultipleCharacteristics( ontologySourceEndurantID, multiples );
        }

        return mfs;
    }

    private static SymbaFormSessionBean setMfs( SymbaFormSessionBean symbaFormSessionBean,
                                                boolean isPartOfDataFileForm,
                                                BasicMaterialScheme scheme, MaterialFactorsStore mfs ) {
        if ( isPartOfDataFileForm ) {
            GenericProtocolApplicationSummary summary = symbaFormSessionBean.getDatafileSpecificMetadataStores()
                    .get( scheme.getDatafileNumber() ).getGenericProtocolApplicationInfo()
                    .get( scheme.getParentOfGpaEndurant() );
            if ( summary == null ) {
                summary = new GenericProtocolApplicationSummary();
            }
            if ( scheme.isMeasuredMaterial() ) {
                summary.setInputMeasuredMaterialFactor( mfs, scheme.getMaterialCount() );
            } else {
                summary.setInputCompleteMaterialFactor( mfs, scheme.getMaterialCount() );
            }
            symbaFormSessionBean.getDatafileSpecificMetadataStores().get( scheme.getDatafileNumber() )
                    .putGenericProtocolApplicationInfoValue( scheme.getParentOfGpaEndurant(), summary );
        } else {
            symbaFormSessionBean.setSpecimenToBeUploaded( mfs );
        }
        return symbaFormSessionBean;
    }

    private static MaterialFactorsStore getMfs( SymbaFormSessionBean symbaFormSessionBean,
                                                boolean isPartOfDataFileForm,
                                                BasicMaterialScheme scheme ) {
        // take what is already there, and add only those fields that have not been made yet

        if ( isPartOfDataFileForm ) {

            DatafileSpecificMetadataStore temp =
                    symbaFormSessionBean.getDatafileSpecificMetadataStores().get( scheme.getDatafileNumber() );

            if ( temp.getGenericProtocolApplicationInfo().get( scheme.getParentOfGpaEndurant() ) == null ) {
                return new MaterialFactorsStore();
            }

            MaterialFactorsStore mfs;
            if ( scheme.isMeasuredMaterial() ) {
                if ( temp.getGenericProtocolApplicationInfo().get( scheme.getParentOfGpaEndurant() )
                        .getInputMeasuredMaterialFactors().size() > scheme.getMaterialCount() ) {
                    mfs = temp.getGenericProtocolApplicationInfo().get( scheme.getParentOfGpaEndurant() )
                            .getInputMeasuredMaterialFactors().get( scheme.getMaterialCount() );
                } else {
                    return new MaterialFactorsStore();
                }
            } else {
                if ( temp.getGenericProtocolApplicationInfo().get( scheme.getParentOfGpaEndurant() )
                        .getInputCompleteMaterialFactors().size() > scheme.getMaterialCount() ) {
                    mfs = temp.getGenericProtocolApplicationInfo().get( scheme.getParentOfGpaEndurant() )
                            .getInputCompleteMaterialFactors().get( scheme.getMaterialCount() );
                } else {
                    return new MaterialFactorsStore();
                }
            }
            if ( mfs == null ) {
                return new MaterialFactorsStore();
            }
            return mfs;
        } else {
            return symbaFormSessionBean.getSpecimenToBeUploaded();
        }
    }

    private static SymbaFormSessionBean setGpaSummary( SymbaFormSessionBean symbaFormSessionBean,
                                                       boolean isPartOfDataFileForm,
                                                       BasicScheme scheme,
                                                       GenericProtocolApplicationSummary gpaSummary ) {
        if ( isPartOfDataFileForm ) {
            symbaFormSessionBean.getDatafileSpecificMetadataStores().get( scheme.getDatafileNumber() )
                    .putGenericProtocolApplicationInfoValue( scheme.getParentOfGpaEndurant(), gpaSummary );
        }
        return symbaFormSessionBean;
    }

    private static GenericProtocolApplicationSummary getGpaSummary( SymbaFormSessionBean symbaFormSessionBean,
                                                                    boolean isPartOfDataFileForm, BasicScheme scheme ) {
        // take what is already there, and add only those fields that have not been made yet

        if ( isPartOfDataFileForm ) {

            DatafileSpecificMetadataStore temp =
                    symbaFormSessionBean.getDatafileSpecificMetadataStores().get( scheme.getDatafileNumber() );
            GenericProtocolApplicationSummary gpaSummary =
                    temp.getGenericProtocolApplicationInfo().get( scheme.getParentOfGpaEndurant() );
            if ( gpaSummary == null ) {
                gpaSummary = new GenericProtocolApplicationSummary();
            }
            return gpaSummary;
        }
        return null;
    }
}