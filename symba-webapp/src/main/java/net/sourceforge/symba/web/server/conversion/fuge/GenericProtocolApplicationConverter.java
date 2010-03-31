package net.sourceforge.symba.web.server.conversion.fuge;

import net.sourceforge.fuge.util.generated.DataCollection;
import net.sourceforge.fuge.util.generated.ExternalData;
import net.sourceforge.fuge.util.generated.GenericMaterial;
import net.sourceforge.fuge.util.generated.GenericProtocol;
import net.sourceforge.fuge.util.generated.GenericProtocolApplication;
import net.sourceforge.fuge.util.generated.InputCompleteMaterials;
import net.sourceforge.fuge.util.generated.MaterialCollection;
import net.sourceforge.fuge.util.generated.OutputData;
import net.sourceforge.fuge.util.generated.OutputMaterials;
import net.sourceforge.fuge.util.generated.Person;
import net.sourceforge.symba.web.shared.ExperimentStep;
import net.sourceforge.symba.web.shared.Material;

import javax.xml.bind.JAXBElement;
import java.util.HashMap;

/**
 * Helper class to ensure the conversions to and from FuGE are all in one place, to reduce error and confusion.
 */
public class GenericProtocolApplicationConverter {

    /**
     * A name is not passed, as it is copied from the associatedProtocol.
     * Do not attempt to parse any data, materials etc.
     *
     * @param identifier         the identifier for the GenericProtocolApplication
     * @param endurant           the endurant for the GenericProtocolApplication
     * @param associatedProtocol the protocol this GPA is referencing
     * @param person             the person who created this run of the protocol.
     * @return the new GenericProtocolApplication
     */
    public static GenericProtocolApplication toFuge( String identifier,
                                                     String endurant,
                                                     GenericProtocol associatedProtocol,
                                                     Person person ) {
        GenericProtocolApplication gpa = new GenericProtocolApplication();
        IdentifiableConverter.toFuge( gpa, associatedProtocol.getName(), identifier, endurant );
        gpa.setProtocolRef( associatedProtocol.getIdentifier() );
        IdentifiableConverter.addAuditTrail( gpa, person );
        return gpa;
    }

    /**
     * A name is not passed, as it is copied from the associatedProtocol.
     *
     * @param identifier         the identifier for the GenericProtocolApplication
     * @param endurant           the endurant for the GenericProtocolApplication
     * @param step               where to get the data, materials, etc. from
     * @param dataIdentifiers    the links to the actual identifiers in the database for each file name
     * @param associatedProtocol the protocol this GPA is referencing
     * @param person             the person who created this run of the protocol.
     * @return the new GenericProtocolApplication
     */
    public static GenericProtocolApplication toFuge( String identifier,
                                                     String endurant,
                                                     ExperimentStep step,
                                                     HashMap<String, String> dataIdentifiers,
                                                     GenericProtocol associatedProtocol,
                                                     Person person ) {
        GenericProtocolApplication gpa = toFuge( identifier, endurant, associatedProtocol, person );

        for ( String file : step.getFileNames() ) {
            OutputData od = new OutputData();
            od.setDataRef( dataIdentifiers.get( file ) );
            gpa.getOutputData().add( od );
        }

        for ( net.sourceforge.symba.web.shared.Material m : step.getInputMaterials() ) {
            InputCompleteMaterials icm = new InputCompleteMaterials();
            icm.setMaterialRef( m.getId() );
            gpa.getInputCompleteMaterials().add( icm );
        }

        for ( net.sourceforge.symba.web.shared.Material m : step.getOutputMaterials() ) {
            OutputMaterials om = new OutputMaterials();
            om.setMaterialRef( m.getId() );
            gpa.getOutputMaterials().add( om );
        }

        return gpa;
    }

    public static ExperimentStep toSymba( ExperimentStep step,
                                          GenericProtocolApplication gpa,
                                          DataCollection allData,
                                          MaterialCollection allMaterial ) {

        for ( OutputData od : gpa.getOutputData() ) {
            ExternalData ed = getExternalData( od.getDataRef(), allData );
            if ( ed != null ) {
                // todo sort out naming of name and location
                step.getFileNames().add( ed.getLocation() );
            }
        }
        for ( InputCompleteMaterials icm : gpa.getInputCompleteMaterials() ) {
            GenericMaterial gm = getGenericMaterial( icm.getMaterialRef(), allMaterial );
            if ( gm != null ) {
                net.sourceforge.symba.web.shared.Material uiMaterial = new Material();
                uiMaterial.setId( gm.getIdentifier() );
                uiMaterial.setName( gm.getName() );
                uiMaterial.setDescription( gm.getDescriptions().getDescription().get( 0 ).getText() );
                step.getInputMaterials().add( uiMaterial );
            }
        }
        for ( OutputMaterials om : gpa.getOutputMaterials() ) {
            GenericMaterial gm = getGenericMaterial( om.getMaterialRef(), allMaterial );
            if ( gm != null ) {
                net.sourceforge.symba.web.shared.Material uiMaterial = new Material();
                uiMaterial.setId( gm.getIdentifier() );
                uiMaterial.setName( gm.getName() );
                uiMaterial.setDescription( gm.getDescriptions().getDescription().get( 0 ).getText() );
                step.getOutputMaterials().add( uiMaterial );
            }
        }
        return step;
    }

    private static ExternalData getExternalData( String id,
                                                 DataCollection allData ) {
        for ( JAXBElement element : allData.getData() ) {
            if ( element.getValue() instanceof ExternalData ) {
                if ( ( ( ExternalData ) element.getValue() ).getIdentifier().equals( id ) ) {
                    return ( ExternalData ) element.getValue();
                }
            }
        }
        return null;
    }

    private static GenericMaterial getGenericMaterial( String id,
                                                       MaterialCollection allMaterial ) {
        for ( JAXBElement element : allMaterial.getMaterial() ) {
            if ( element.getValue() instanceof GenericMaterial ) {
                if ( ( ( GenericMaterial ) element.getValue() ).getIdentifier().equals( id ) ) {
                    return ( GenericMaterial ) element.getValue();
                }
            }
        }
        return null;
    }
}
