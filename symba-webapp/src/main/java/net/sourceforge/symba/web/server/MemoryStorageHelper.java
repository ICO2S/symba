package net.sourceforge.symba.web.server;

import net.sourceforge.symba.web.server.conversion.fuge.FugeCreator;
import net.sourceforge.symba.web.server.conversion.fuge.IdentifiableConverter;
import net.sourceforge.symba.web.shared.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * allows an in-memory only version of SyMBA to run.
 */
public class MemoryStorageHelper extends StorageHelper {

    private static final Contact  ALICE   = new Contact( "23456", "Alice", "Smith", "alice.jones@example.com" );
    private static final Contact  BOB     = new Contact( "013265", "Bob", "Reynolds", "bob.reynolds@example.com" );
    private static final Contact  ZACH    = new Contact( "9561046", "Zach", "Peters", "zach.peters@example.com" );
    private static final Material CULTURE = new Material( "ABC23456", "Cell Culture 17", "An example cell culture." );

    @Override
    public void setup( @NotNull ApplicationContext context ) {
        // no need to do anything for this method for the MemoryStorageHelper
    }

    /**
     * If addExampleIfEmpty is true, resets the in-memory investigations to just one example to start out with in SyMBA,
     * clearing any currently-existing investigations.
     *
     * @param addExampleIfEmpty If this value is true then example entries will be added.
     * @return the list of investigations to send to the client
     */
    @NotNull
    public HashMap<String, Investigation> fetchAll( boolean addExampleIfEmpty ) {
        if ( addExampleIfEmpty ) {
            getInvestigations().clear();
            Investigation investigation = new Investigation( false,
                                                             false,
                                                             "12345",
                                                             "My Example Investigation",
                                                             "Some hypothesis",
                                                             "Conclusion 1. Conclusion 2.",
                                                             ALICE,
                                                             new ArrayList<ExperimentStepHolder>() );
            add( investigation );

            Investigation basicMicroarrayTemplate = new Investigation( true,
                                                                       false,
                                                                       "1234567",
                                                                       "Example Multi-Strain Microarray Investigation",
                                                                       "Examination of the effect of certain conditions on yeast strains over time.",
                                                                       "",
                                                                       ALICE,
                                                                       makeTemplateMicroarraySteps() );

            add( basicMicroarrayTemplate );
        }
        return getInvestigations();
    }

    /**
     * Resets the in-memory Contacts to initially populate the SyMBA UI. This method also clears any currently-existing
     * Contacts.
     *
     * @return the list of Contacts
     */
    @NotNull
    public HashMap<String, Contact> fetchAllUsers() {
        getUsers().clear();
        getUsers().put( ALICE.getId(), ALICE );
        getUsers().put( BOB.getId(), BOB );
        getUsers().put( ZACH.getId(), ZACH );
        return getUsers();
    }

    @NotNull
    @Override
    public HashMap<String, Material> fetchAllMaterials() {
        getMaterials().clear();
        getMaterials().put( CULTURE.getId(), CULTURE );
        return getMaterials();
    }

    @NotNull
    @Override
    public HashSet<String> fetchAllParameterSubjects() {
        getParameterSubjects().clear();
        for ( String key : getInvestigations().keySet() ) {
            for ( ExperimentStepHolder holder : getInvestigations().get( key ).getExperiments() ) {
                addSubjects( holder );
            }
        }
        return getParameterSubjects();
    }

    @NotNull
    public HashMap<String, Contact> addContact( @NotNull Contact contact ) {
        getUsers().put( contact.getId(), contact );
        return getUsers();
    }

    @NotNull
    public HashMap<String, Material> addMaterial( @NotNull Material material ) {
        getMaterials().put( material.getId(), material );
        return getMaterials();
    }

    @NotNull
    public Investigation add( @NotNull Investigation investigation ) {

        if ( investigation.getId().trim().equals( "" ) ) {
            investigation.setId( String.valueOf( getInvestigations().size() ) );
        }
        getInvestigations().put( investigation.getId(), investigation );
        return investigation;

    }

    @NotNull
    public ArrayList<InvestigationDetail> update( @NotNull Investigation investigation ) {
        // todo move the values in "current" to the value in "original"
        getInvestigations().remove( investigation.getId() );
        getInvestigations().put( investigation.getId(), investigation );
        return getInvestigationDetails();
    }

    @NotNull
    public InvestigationDetail copy( @NotNull String id, @NotNull String contactId ) {
        Investigation copy = new Investigation( getInvestigations().get( id ) );
        copy.setId( IdentifiableConverter.createId( "Investigation" ) );
        copy.setInvestigationTitle( copy.getInvestigationTitle() + " " + ( int ) ( Math.random() * 1000 ) );
        copy.setProvider( getUsers().get( contactId ) );
        // the original may be a template - unset the copy as a template
        copy.setTemplate( false );
        getInvestigations().put( copy.getId(), copy );

        return copy.getLightWeightInvestigation();
    }

    //todo this method will not be allowed in future, except perhaps by admins.

    @NotNull
    public ArrayList<InvestigationDetail> delete( @NotNull String id ) {
        getInvestigations().remove( id );
        return getInvestigationDetails();
    }

    @NotNull
    @Override
    public String getMetadataString( @NotNull String id ) {
        FugeCreator creator = new FugeCreator();
        return creator.toFugeString( getInvestigations().get( id ) );
    }

    private void addSubjects( ExperimentStepHolder holder ) {
        for ( ExperimentParameter parameter : holder.getCurrent().getParameters() ) {
            getParameterSubjects().add( parameter.getSubject() );
        }
        if ( ! holder.getCurrent().isLeaf() ) {
            for ( ExperimentStepHolder innerHolder : holder.getCurrent().getChildren() ) {
                addSubjects( innerHolder );
            }
        }
    }

    private ArrayList<ExperimentStepHolder> makeTemplateMicroarraySteps() {
        ArrayList<ExperimentStepHolder> templateSteps = new ArrayList<ExperimentStepHolder>();

        ExperimentStepHolder top = new ExperimentStepHolder();
        top.getCurrent().setTitle( "Experiment Run for Strain X" );
        ExperimentParameter strain = new ExperimentParameter();
        strain.setFullyWriteable( false );
        strain.setSubject( "Strain" );
        strain.setPredicate( "hasIdentifier" );
        top.getCurrent().getParameters().add( strain );

        ExperimentStepHolder middle = new ExperimentStepHolder();
        middle.getCurrent().setTitle( "Microarray Time Series Point X" );
        ExperimentParameter time = new ExperimentParameter();
        time.setFullyWriteable( false );
        time.setSubject( "TimeAfterInoculation" );
        time.setPredicate( "is" );
        time.setUnit( "minutes" );
        ExperimentParameter temp = new ExperimentParameter();
        temp.setFullyWriteable( false );
        temp.setSubject( "InoculationTemperature" );
        temp.setPredicate( "is" );
        temp.setUnit( "Celsius" );
        middle.getCurrent().getParameters().add( time );
        middle.getCurrent().getParameters().add( temp );

        ExperimentStepHolder last = new ExperimentStepHolder();
        last.getCurrent().setTitle( "Microarray Assay Repeat X" );
        ExperimentParameter repeat = new ExperimentParameter();
        repeat.setFullyWriteable( false );
        repeat.setSubject( "RepeatNumber" );
        repeat.setPredicate( "is" );
        ExperimentParameter brand = new ExperimentParameter();
        brand.setFullyWriteable( false );
        brand.setSubject( "MicroarrayChip" );
        brand.setPredicate( "hasBrand" );
        ExperimentParameter model = new ExperimentParameter();
        model.setFullyWriteable( false );
        model.setSubject( "MicroarrayChip" );
        model.setPredicate( "hasModel" );
        last.getCurrent().getParameters().add( repeat );
        last.getCurrent().getParameters().add( brand );
        last.getCurrent().getParameters().add( model );

        middle.getCurrent().getChildren().add( last );
        top.getCurrent().getChildren().add( middle );
        templateSteps.add( top );

        return templateSteps;
    }
}
