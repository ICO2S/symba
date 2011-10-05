package net.sourceforge.symba.web.server.conversion.fuge;

import net.sourceforge.fuge.util.generated.GenericParameter;
import net.sourceforge.fuge.util.generated.GenericProtocol;
import net.sourceforge.fuge.util.generated.OntologyCollection;
import net.sourceforge.symba.web.shared.ExperimentParameter;
import net.sourceforge.symba.web.shared.ExperimentStep;

import java.util.ArrayList;

/**
 * Helper class to ensure the conversions to and from FuGE are all in one place, to reduce error and confusion.
 */
public class GenericProtocolConverter {

    /**
     * Do not attempt to parse any parameters
     *
     * @param identifier the identifier for the GenericProtocol
     * @param endurant   the endurant for the GenericProtocol
     * @param name       the name for the GenericProtocol
     * @return the new GenericProtocol
     */
    public static GenericProtocol toFuge( String identifier, String endurant, String name ) {
        GenericProtocol protocol = new GenericProtocol();
        protocol = ( GenericProtocol ) IdentifiableConverter.toFuge( protocol, name, identifier, endurant );
        return protocol;
    }

    /**
     * convert a SyMBA User Interface object into a FuGE GenericProtocol
     *
     * @param identifier   the identifier for the GenericProtocol
     * @param endurant     the endurant for the GenericProtocol
     * @param name         the name for the GenericProtocol
     * @param uiParameters the parameters to parse
     * @param allOntology  the ontology collection to add new ontology terms to
     * @return the new GenericProtocol
     */
    public static GenericProtocol toFuge( String identifier,
                                          String endurant,
                                          String name,
                                          ArrayList<ExperimentParameter> uiParameters,
                                          OntologyCollection allOntology ) {
        GenericProtocol protocol = toFuge( identifier, endurant, name );
        // add any parameters
        for ( ExperimentParameter uiParameter : uiParameters ) {
            protocol.getGenericParameter().add( GenericParameterConverter.toFuge( IdentifiableConverter.createId(
                    "GenericProtocol" ),
                                                                                  IdentifiableConverter.createId(
                                                                                          "GenericProtocolEndurant" ),
                                                                                  uiParameter,
                                                                                  allOntology ) );
        }
        return protocol;
    }

    public static ExperimentStep toSymba( ExperimentStep step,
                                          GenericProtocol protocol,
                                          final OntologyCollection allOntology ) {
        // the only thing we get directly from a GenericProtocol at the moment is the GenericParameters.
        // ignore the child actions, as that is not of direct relevance to this particular step.
        for ( GenericParameter parameter : protocol.getGenericParameter() ) {
            step.getParameters().add( GenericParameterConverter.toSymba( parameter, allOntology ) );
        }
        return step;
    }
}
