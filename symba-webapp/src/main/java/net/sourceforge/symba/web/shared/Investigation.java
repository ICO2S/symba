package net.sourceforge.symba.web.shared;

import com.google.gwt.user.client.Random;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The JAXB-created XML code for FuGE cannot be used within the client-side GWT code. Therefore,
 * Investigation and InvestigationDetail (a summary of what's in Investigation) are used instead on
 * the client side.
 */
public class Investigation implements Serializable {

    private boolean template, completed;

    private String id;
    private String investigationTitle;
    private String hypothesis;
    private String conclusion;
    private Contact provider;

    // todo private ArrayList<Contact> otherContacts; 

    // the experiment steps used in this investigation
    private ArrayList<ExperimentStepHolder> experiments;

    public Investigation() {
        this.template = false;
        this.completed = false;
        this.id = "0";
        this.investigationTitle = "";
        this.hypothesis = "";
        this.conclusion = "";
        this.provider = new Contact();
        this.experiments = new ArrayList<ExperimentStepHolder>();
        // todo why doesn't the below work? It ends up with provider being null!
//        new Investigation( "00", "", new Contact("0", "", "", "") );
    }

    public Investigation( boolean template,
                          boolean completed,
                          String id,
                          String investigationTitle,
                          String hypothesis,
                          String conclusion,
                          Contact provider,
                          ArrayList<ExperimentStepHolder> experiments ) {
        this.template = template;
        this.completed = completed;
        this.id = id;
        this.investigationTitle = investigationTitle;
        this.hypothesis = hypothesis;
        this.conclusion = conclusion;
        this.provider = provider;
        this.experiments = experiments;
    }

    /**
     * Copy constructor.
     *
     * @param investigation the object to be copied
     */
    public Investigation( Investigation investigation ) {
        this( investigation.isTemplate(), investigation.isCompleted(), investigation.getId(),
                investigation.getInvestigationTitle(), investigation.getHypothesis(), investigation.getConclusion(),
                investigation.getProvider(), investigation.getExperiments() );
    }

    public void createId() {
        // todo better ID creation
        setId( Integer.toString( Random.nextInt() ) );
    }

    /**
     * Updates the experiment step info of the one marked with the specified row. If the title is empty or null, the
     * original title will be used.
     *
     * @param selectedRow the row in the view that is to get a new title - will match the holder's stepId.
     * @param title       the new title to set the step to (this may be empty, but will be dealt with appropriately)
     * @param parameters  the new parameters to set the step to (these parameters completely re-write existing ones)
     * @param inputs      the new input materials to set the step to (these materials completely re-write existing)
     * @param outputs     the new output materials to set the step to (these materials completely re-write existing)
     * @param fileInfo    the names and descriptions of the files for the step
     * @return the new title at [0] and presence of modification (true/false) at [1]. Will be empty array if no
     *         matches found
     */
    public Object[] setExperimentStepInfo( int selectedRow,
                                           String title,
                                           ArrayList<ExperimentParameter> parameters,
                                           ArrayList<Material> inputs,
                                           ArrayList<Material> outputs,
                                           HashMap<String, String> fileInfo ) {

        for ( ExperimentStepHolder holder : experiments ) {
            Object[] values = holder.setInfoAtStepId( selectedRow, title, parameters, inputs, outputs, fileInfo );
            if ( values.length == 2 && values[0] != null && values[1] != null &&
                    ( ( String ) values[0] ).length() > 0 ) {
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

    public boolean isTemplate() {
        return template;
    }

    public void setTemplate( boolean template ) {
        this.template = template;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted( boolean completed ) {
        this.completed = completed;
    }

    public boolean isReadOnly() {
        return template || completed;
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

    public String getHypothesis() {
        return hypothesis;
    }

    public void setHypothesis( String hypothesis ) {
        this.hypothesis = hypothesis;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion( String conclusion ) {
        this.conclusion = conclusion;
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

    public InvestigationDetail getLightWeightInvestigation() {
        return new InvestigationDetail( template, completed, id, investigationTitle, provider );
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
//            System.err.println( "copying at top level" );
            addExperimentStep( step );
        }
    }

    public void setAllModified( boolean value ) {
        for ( int i = 0, experimentsSize = getExperiments().size(); i < experimentsSize; i++ ) {
            ExperimentStepHolder holder = getExperiments().get( i );
            holder.setAllModified( value );
        }
    }

//    public int addExperimentFile( int selectedRow,
//                                  File file ) {
//        for ( ExperimentStepHolder holder : experiments ) {
//            int returnedDepth = holder.setFileAtStepId( selectedRow, 0, file.getName() );
//            if ( returnedDepth != -1 ) {
//                // the title was found and set appropriately.
//                return returnedDepth;
//            }
//        }
//        return -1;
//    }


}
