package net.sourceforge.symba.web.shared;

import com.google.gwt.user.client.Random;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The "component" interface of a whole-part design pattern, allowing changeable and arbitrarily-deep levels
 * of complexity.
 */
public class ExperimentStep implements Serializable {

    private String databaseId;
    private String title;
    private ArrayList<ExperimentParameter> parameters;
    private ArrayList<Material> inputMaterials;
    private ArrayList<Material> outputMaterials;
    private HashMap<String, String> fileInfo;

    // todo parameters etc. go here

    private ArrayList<ExperimentStepHolder> children;

    public ExperimentStep() {
        this.databaseId = "0"; // todo proper identification creation here or in createDatabaseId
        this.title = "Empty Experiment Step";
        this.fileInfo = new HashMap<String, String>();
        this.children = new ArrayList<ExperimentStepHolder>();
        this.parameters = new ArrayList<ExperimentParameter>();
        this.inputMaterials = new ArrayList<Material>();
        this.outputMaterials = new ArrayList<Material>();
    }

    public ExperimentStep( String databaseId,
                           String title,
                           HashMap<String, String> fileInfo,
                           ArrayList<ExperimentStepHolder> children,
                           ArrayList<ExperimentParameter> parameters,
                           ArrayList<Material> inputMaterials,
                           ArrayList<Material> outputMaterials ) {

        this.databaseId = databaseId;
        this.title = title;
        this.fileInfo = fileInfo;
        this.children = children;
        this.parameters = parameters;
        this.inputMaterials = inputMaterials;
        this.outputMaterials = outputMaterials;
    }

    /**
     * copy constructor
     * You get an UnsatisfiedLinkError if you try to run createDatabaseId() here, so if you wish to change the
     * databaseId you will need to do it outside this constructor.
     *
     * @param step the step to copy.
     */
    public ExperimentStep( ExperimentStep step ) {
        this( step.getDatabaseId(), step.getTitle(), new HashMap<String, String>( step.getFileInfo() ),
                new ArrayList<ExperimentStepHolder>( step.getChildren() ),
                new ArrayList<ExperimentParameter>( step.getParameters() ),
                new ArrayList<Material>( step.getInputMaterials() ),
                new ArrayList<Material>( step.getOutputMaterials() ) );
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId( String databaseId ) {
        this.databaseId = databaseId;
    }

    public void createDatabaseId() {
        setDatabaseId( Integer.toString( Random.nextInt() ) );
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public ArrayList<ExperimentParameter> getParameters() {
        return parameters;
    }

    public ArrayList<Material> getInputMaterials() {
        return inputMaterials;
    }

    public ArrayList<Material> getOutputMaterials() {
        return outputMaterials;
    }

    public HashMap<String, String> getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo( HashMap<String, String> fileInfo ) {
        this.fileInfo = fileInfo;
    }

    public ArrayList<ExperimentStepHolder> getChildren() {
        return children;
    }

    /**
     * A method for discovering quickly if the object is a leaf node (i.e. contains no further steps)
     *
     * @return true if it is a leaf node, false otherwise.
     */
    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    /**
     * Adds the new step to the bottom of the list of children in this step
     *
     * @param step the new ExperimentalStep to add
     */
    public void addChild( ExperimentStep step ) {
        ExperimentStepHolder holder = new ExperimentStepHolder( step );
        holder.setModified( true );
        this.children.add( holder );
    }

    /**
     * Should remove all sub-steps as well. We assume the user knows what he's doing.
     *
     * @param index the ExperimentStepHolder id marking the particular ExperimentStep to remove
     */
    public void remove( int index ) {

    }

    /**
     * only copies the information stored in this step, not in its children
     */
    public void shallowCopy() {

    }

    /**
     * Copies the information stored in this step except for the databaseId. Therefore this copy does include the
     * ENTIRE child hierarchy. What this will NOT add is the files, as it is not expected that files should be copied
     * (unless the user wishes this). Instead, it is the structure of the experimental step, and its parameters,
     * which should be copied.
     *
     * @return the copy of the Experiment step
     */
    public ExperimentStep deepCopy() {

        ExperimentStep step = new ExperimentStep();
        step.createDatabaseId();
        step.setTitle( getTitle() );
        step.getParameters().addAll( getParameters() );
        step.getInputMaterials().addAll( getInputMaterials() );
        step.getOutputMaterials().addAll( getOutputMaterials() );
        for ( ExperimentStepHolder holder : getChildren() ) {
            ExperimentStep newHolderCurrent = new ExperimentStep( holder.getCurrent() );
            newHolderCurrent.createDatabaseId();
            step.addChild( newHolderCurrent );
            createDatabaseId();
            step.getChildren().get( step.getChildren().size() - 1 ).setModified( true );
        }
        return step;
    }

    /**
     * Moves the position of the child up one within the list, or nowhere if it is already in the top position.
     *
     * @param index the id of the step to rearrange
     */
    public void moveChildUp( int index ) {

    }

    /**
     * Moves the position of the child down one within the list, or nowhere if it is already in the top position.
     *
     * @param index the id of the step to rearrange
     */
    public void moveChildDown( int index ) {

    }

}
