package net.sourceforge.symba.web.shared;

import net.sourceforge.symba.web.client.stepsorter.ExperimentStep;
import net.sourceforge.symba.web.client.stepsorter.ExperimentStepHolder;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The JAXB-created XML code for FuGE cannot be used within the client-side GWT code. Therefore,
 * Investigation and InvestigationDetails (a summary of what's in Investigation) are used instead on
 * the client side.
 */
@SuppressWarnings( "serial" )
public class Investigation implements Serializable {

    private String id;
    private String investigationTitle;
    private Contact provider;

    // the experiment steps used in this investigation
    private ArrayList<ExperimentStepHolder> experiments;

    public Investigation() {
        this.id = "0";
        this.investigationTitle = "";
        this.provider = new Contact();
        this.experiments = new ArrayList<ExperimentStepHolder>();
        // todo why doesn't the below work? It ends up with provider being null!
//        new Investigation( "00", "", new Contact("0", "", "", "") );
    }

    public Investigation( String id,
                          String investigationTitle,
                          Contact provider,
                          ArrayList<ExperimentStepHolder> experiments ) {
        this.id = id;
        this.investigationTitle = investigationTitle;
        this.provider = provider;
        this.experiments = experiments;

    }

    /**
     * Copy constructor.
     *
     * @param investigation the object to be copied
     */
    public Investigation( Investigation investigation ) {
        this( investigation.getId(), investigation.getInvestigationTitle(), investigation.getProvider(),
                investigation.getExperiments() );
    }


    /**
     * Updates the experiment step title of the one marked with the specified row. If the title is empty or null, the
     * original title will be used.
     *
     * @param selectedRow the row in the view that is to get a new title - will match the holder's stepId.
     * @param title       the new title to set the step to (this may be empty, but will be dealt with appropriately)
     * @return the new title, which may not be the same as the input parameter, or "" if no match in any experiments
     *         were found
     */
    public Object[] setExperimentStepTitle( int selectedRow,
                                            String title ) {

        for ( ExperimentStepHolder holder : experiments ) {
            Object[] values = holder.setTitleAtStepId( selectedRow, title );
            if ( values[0] != null && ( ( String ) values[0] ).length() > 0 ) {
                // the title was found and set appropriately.
                return values;
            }
        }
        return new Object[2]; // no match in any of the experiments were found. Nothing was changed
    }

    /**
     * Adds a new experiment step to the top level experiment if no parent step is selected.
     */
    public void addExperimentStep() {

        System.err.println( "Adding at top level" );
        ExperimentStep step = new ExperimentStep();
        step.createDatabaseId();
        addExperimentStep( step );
    }

    /**
     * Adds the provided step to the top-level experiment
     *
     * @param step the ExperimentStep to add
     */
    public void addExperimentStep( ExperimentStep step ) {

        ExperimentStepHolder toAdd = new ExperimentStepHolder( step );
        toAdd.setModified( true );

        experiments.add( toAdd );
    }

    /**
     * Adds the experiment step as a child of the one marked with the specified row.
     *
     * @param selectedRow the row in the view that is to get a new sub-step - will match the holder's stepId.
     */
    public void addExperimentStep( int selectedRow ) {

//        System.err.println( "Adding at level " + selectedRow );
        for ( ExperimentStepHolder holder : experiments ) {
            if ( holder.addSubStepAtStepId( selectedRow ) ) {
                return; // matching parent step found and added to.
            }
        }
        // if we get here, there was no matching stepId. Add to the top-level.
        addExperimentStep();
    }


    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getInvestigationTitle() {
        return investigationTitle;
    }

    public void setInvestigationTitle( String investigationTitle ) {
        this.investigationTitle = investigationTitle;
    }

    public Contact getProvider() {
        return provider;
    }

    public void setProvider( Contact provider ) {
        this.provider = provider;
    }

    public ArrayList<ExperimentStepHolder> getExperiments() {
        return experiments;
    }

    public InvestigationDetails getLightWeightInvestigation() {
        return new InvestigationDetails( id, investigationTitle, provider.getLightWeight() );
    }

    /**
     * Performs a deep copy of the experiment represented with the given selected row. As the null value for the step
     * is used to show whether or not the step has already been added, it cannot definitely also be used to determine
     * if anything has been added to the investigation at all. Therefore it makes sense to only return void.
     *
     * @param selectedRow the row to match to a step id
     */
    public void deepExperimentCopy( int selectedRow ) {

        ExperimentStep step = null;
        ExperimentStepHolder holder;

        for ( int i = 0, experimentsSize = getExperiments().size(); i < experimentsSize; i++ ) {
            holder = getExperiments().get( i );
            step = holder.copyStepWithStepId( selectedRow );
            if ( step != null ) {
                // we need to add the step to the top-level ArrayList once we're out of the loop to prevent
                // a ConcurrentModificationException
                break;
            }
        }
        if ( step != null ) {
            System.err.println( "copying at top level" );
            addExperimentStep( step );
        }
    }

    public void setAllModified( boolean value ) {
        for ( int i = 0, experimentsSize = getExperiments().size(); i < experimentsSize; i++ ) {
            ExperimentStepHolder holder = getExperiments().get( i );
            holder.setAllModified( value );
        }
    }
}
