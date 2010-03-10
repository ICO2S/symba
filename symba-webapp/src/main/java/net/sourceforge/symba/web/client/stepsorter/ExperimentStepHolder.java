package net.sourceforge.symba.web.client.stepsorter;

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

    public ExperimentStep getCurrent() {
        return current;
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
//            System.err.println( "Step found - adding to child. Now has #children = " + current.getChildren().size() );
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
     * adds a parameter triple to the step specified by selectedRow
     *
     * @param selectedRow the row identifier used to identify which step to add the parameter to
     * @param subject     the parameter name
     * @param predicate   the connection between the subject and object, defaults to hasValue
     * @param objectValue the "object" of the triple, the value that fits with the subject and predicate
     * @param unit        the optional units of the objectValue
     * @return true if a triple was added, false if no matching row was found.
     */
    public boolean addParameterAtStepId( int selectedRow,
                                         String subject,
                                         String predicate,
                                         String objectValue,
                                         String unit ) {

        if ( stepId == selectedRow ) {
            current.getParameters().add( new ExperimentParameter( subject, predicate, objectValue, unit ) );
            setModified( true );
//            System.err.println( "Step found - adding parameter" );
            return true;
        }

        if ( !current.isLeaf() ) {
            for ( ExperimentStepHolder holder : current.getChildren() ) {
                if ( holder.addParameterAtStepId( selectedRow, subject, predicate, objectValue, unit ) ) {
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
     * @param parameters  the new parameters (existing ones will be overwritten)
     * @return the new title value in [0], and true if new, false if matches original, in [1] - this is important,
     *         as it isn't necessarily the same as the title parameter
     */
    public Object[] setInfoAtStepId( int selectedRow,
                                     String title,
                                     ArrayList<ExperimentParameter> parameters ) {
        Object[] values = new Object[2];

        if ( stepId == selectedRow ) {
            // title changes
            if ( title == null || title.length() == 0 ) {
                current.setTitle( original.getTitle() );
                setModified( false );
            } else if ( !current.getTitle().equals( title.trim() ) ) {
                // nothing needs to be done if the titles match already, including running setModified(): it should
                // remain at whatever it was before.
                current.setTitle( title.trim() );
                setModified( true );
            }

            // parameter changes: delete the row if all three elements have zero length
            if ( current.getParameters() != parameters ) {
                setModified( true );
                current.getParameters().clear();
                for ( ExperimentParameter parameter : parameters ) {
                    if ( parameter.getSubject().length() == 0 && parameter.getPredicate().length() == 0 &&
                            parameter.getObjectValue().length() == 0 && parameter.getUnit().length() == 0 ) {
                        continue;
                    }
                    current.getParameters().add( parameter );
                }
            }

            values[0] = current.getTitle();
            values[1] = isModified();
            return values;
        }

        if ( !current.isLeaf() ) {
            for ( ExperimentStepHolder holder : current.getChildren() ) {
                Object[] returnedValues = holder.setInfoAtStepId( selectedRow, title, parameters );
                if ( returnedValues != null ) {
                    setModified( ( Boolean ) returnedValues[1] );
                    return returnedValues;
                }
            }
        }
        return values; // if there was no match
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
        ExperimentStepHolder holder;
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

    public void setAllModified( boolean value ) {

        setModified( value );
        if ( !current.isLeaf() ) {
            for ( int i = 0, childrenSize = current.getChildren().size(); i < childrenSize; i++ ) {
                current.getChildren().get( i ).setAllModified( value );
            }
        }
    }

    public int setFileAtStepId( int selectedRow,
                                int depth,
                                String fileName ) {
        if ( stepId == selectedRow ) {
            if ( fileName != null && fileName.length() > 0 ) {
                current.getFileNames().add( fileName );
                setModified( true );
            }
            return depth;
        }

        if ( !current.isLeaf() ) {
            for ( ExperimentStepHolder holder : current.getChildren() ) {
                int returnedDepth = holder.setFileAtStepId( selectedRow, depth + 1, fileName );
                if ( returnedDepth != -1 ) {
                    setModified( true );
                    return returnedDepth;
                }
            }
        }
        return -1;
    }

    public void clearFileAssociations() {
        current.getFileNames().clear();
        original.getFileNames().clear();
        if ( !current.isLeaf() ) {
            for ( ExperimentStepHolder holder : current.getChildren() ) {
                holder.clearFileAssociations();
            }
        }
        if ( !original.isLeaf() ) {
            for ( ExperimentStepHolder holder : original.getChildren() ) {
                holder.clearFileAssociations();
            }
        }
    }

    public void setFullyWriteableParameters( boolean writeable ) {
        for ( ExperimentParameter parameter : current.getParameters() ) {
            parameter.setFullyWriteable( writeable );
        }
        for ( ExperimentParameter parameter : original.getParameters() ) {
            parameter.setFullyWriteable( writeable );
        }
        if ( !current.isLeaf() ) {
            for ( ExperimentStepHolder holder : current.getChildren() ) {
                holder.setFullyWriteableParameters( writeable );
            }
        }
        if ( !original.isLeaf() ) {
            for ( ExperimentStepHolder holder : original.getChildren() ) {
                holder.setFullyWriteableParameters( writeable );
            }
        }
    }
}
