package net.sourceforge.symba.web.server.conversion.fuge;

import net.sourceforge.fuge.util.generated.GenericAction;
import net.sourceforge.symba.web.client.stepsorter.ExperimentStep;

/**
 * Helper class to ensure the conversions to and from FuGE are all in one place, to reduce error and confusion.
 */
public class GenericActionConverter {

    public static GenericAction toFuge( String identifier,
                                        String endurant,
                                        String name,
                                        String protocolRef,
                                        int ordinal ) {
        GenericAction action = new GenericAction();
        action = ( GenericAction ) IdentifiableConverter.toFuge( action, identifier, endurant, name );
        action.setActionOrdinal( ordinal );
        action.setProtocolRef( protocolRef );

        return action;
    }

    public static ExperimentStep toSymba( ExperimentStep step,
                                          GenericAction action ) {

        // the only things we can add to the experiment steps from a GenericAction are the name of the step
        // and the protocol identifier associated with it.
        step.setDatabaseId( action.getProtocolRef() );
        step.setTitle( action.getName() );

        return step;
    }
}
