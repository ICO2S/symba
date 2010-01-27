package net.sourceforge.symba.web.client.stepsorter;

import com.google.gwt.user.client.Random;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Rather than storing the ExperimentSteps directly, store a Holder which contains the step, plus extra metadata
 * specific to its position in the list and to its original values (to show when something has changed).
 */
public class ExperimentStepHolder implements Serializable {

    private ExperimentStep original;
    private ExperimentStep current;
    private int stepId;
    private boolean isModified;

    public ExperimentStepHolder() {
        this.original = new ExperimentStep();
        this.current = new ExperimentStep( this.original );
        this.stepId = 0; // will be re-written later in the view
        this.isModified = false;
    }

    public ExperimentStepHolder( ExperimentStep current ) {
        this.current = current;
        this.original = new ExperimentStep( current );
        this.stepId = 0; // will be re-written later in the view
        this.isModified = false;
    }

    public ExperimentStep getOriginal() {
        return original;
    }

    public ExperimentStep getCurrent() {
        return current;
    }

    public int getStepId() {
        return stepId;
    }

    public void setStepId( int stepId ) {
        this.stepId = stepId;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified( boolean modified ) {
        isModified = modified;
    }

    /**
     * adds an additional experimental step as a child of the step specified by selectedRow
     *
     * @param selectedRow the row identifier used to identify which step to add a child to
     * @return true if a row was added, false if no matching row was found.
     */
    public boolean addSubStepAtStepId( int selectedRow ) {

        if ( stepId == selectedRow ) {
            ExperimentStep step = new ExperimentStep();
            step.createDatabaseId();
            current.addChild( step );
            setModified( true );
            System.err.println( "Step found - adding to child. Now has #children = " + current.getChildren().size() );
            return true;
        }

        if ( !current.isLeaf() ) {
            for ( ExperimentStepHolder holder : current.getChildren() ) {
                if ( holder.addSubStepAtStepId( selectedRow ) ) {
                    setModified( true );
                    return true;
                }
            }
        }
        return false; // if there was no match
    }

    /**
     * sets the value of the "current" object to the value in the string. If title is null or empty, the value of
     * the "current" object is reset to the value in the original object.
     *
     * @param selectedRow the row whose title is to be changed
     * @param title       the new title
     * @return the new title value - this is important, as it isn't necessarily the same as the title parameter
     */
    public String setTitleAtStepId( int selectedRow,
                                    String title ) {

        if ( stepId == selectedRow ) {
            if ( title == null || title.length() == 0 ) {
                current.setTitle( original.getTitle() );
            } else {
                current.setTitle( title );
            }
            setModified( true );
            System.err.println( "Step found - modifying title. Now has title = " + current.getTitle() );
            return current.getTitle();
        }

        if ( !current.isLeaf() ) {
            for ( ExperimentStepHolder holder : current.getChildren() ) {
                String returnedTitle = holder.setTitleAtStepId( selectedRow, title );
                if ( returnedTitle.length() > 0 ) {
                    setModified( true );
                    return returnedTitle;
                }
            }
        }
        return ""; // if there was no match
    }

    /**
     * Will add a copy of the step to the same array the selectedRow is in (i.e. a sibling)
     *
     * @param selectedRow the row to find a match with
     * @return the new ExperimentStep to add. If the ExperimentStep has already been added at the right place,
     *         then the return value will be null.
     */
    public ExperimentStep copyStepWithStepId( int selectedRow ) {

        if ( stepId == selectedRow ) {
            ExperimentStep step = current.deepCopy();

            // the deep copy changes the minimal things, which is just the databaseid. If we want to change other
            // things, we need to do it here.
            step.setTitle( "Copy of " + step.getTitle() );
            return step;
        }

        ExperimentStep step = null;
        ExperimentStepHolder holder = null;
        if ( !current.isLeaf() ) {
            for ( int i = 0, childrenSize = current.getChildren().size(); i < childrenSize; i++ ) {
                holder = current.getChildren().get( i );
                step = holder.copyStepWithStepId( selectedRow );
                if ( step != null ) {
                    // we found the appropriate match in the children of this holder, therefore add the step outside
                    // the loop so as to prevent a ConcurrentModificationException
                    break;
                }
            }
            if ( step != null ) {
                System.err.println( "copying at non-top level" );
                current.addChild( step );
                setModified( true );
                return null;
            }
        }

        return null;
    }
}
