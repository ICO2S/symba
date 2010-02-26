package net.sourceforge.symba.web.client.stepsorter;

import com.google.gwt.user.client.Random;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The "component" interface of a whole-part design pattern, allowing changeable and arbitrarily-deep levels
 * of complexity.
 */
public class ExperimentStep implements Serializable {

    private String databaseId;
    private String title;
    private ArrayList<ExperimentParameter> parameters;
    private ArrayList<String> fileNames;

    // todo parameters etc. go here

    private ArrayList<ExperimentStepHolder> children;

    public ExperimentStep() {
        this.databaseId = "0"; // todo proper identification creation here or in createDatabaseId
        this.title = "Empty Experiment Step";
        this.fileNames = new ArrayList<String>();
        this.children = new ArrayList<ExperimentStepHolder>();
        this.parameters = new ArrayList<ExperimentParameter>();
    }

    public ExperimentStep( String databaseId,
                           String title,
                           ArrayList<String> fileNames,
                           ArrayList<ExperimentStepHolder> children,
                           ArrayList<ExperimentParameter> parameters ) {

        this.databaseId = databaseId;
        this.title = title;
        this.fileNames = fileNames;
        this.children = children;
        this.parameters = parameters;
    }

    /**
     * copy constructor
     * You get an UnsatisfiedLinkError if you try to run createDatabaseId() here, so if you wish to change the
     * databaseId you will need to do it outside this constructor.
     *
     * @param step the step to copy.
     */
    public ExperimentStep( ExperimentStep step ) {
        this( step.getDatabaseId(), step.getTitle(), new ArrayList<String>( step.getFileNames() ),
                new ArrayList<ExperimentStepHolder>( step.getChildren() ),
                new ArrayList<ExperimentParameter>( step.getParameters() ) );
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

    public ArrayList<String> getFileNames() {
        return fileNames;
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
