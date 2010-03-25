package net.sourceforge.symba.web.server.conversion.fuge;

import net.sourceforge.fuge.util.generated.ObjectFactory;
import net.sourceforge.fuge.util.generated.OntologyCollection;
import net.sourceforge.fuge.util.generated.OntologyIndividual;
import net.sourceforge.fuge.util.generated.OntologyTerm;

import javax.xml.bind.JAXBElement;


public class OntologyIndividualConverter {

    public static String toFuge( String identifier,
                                          String endurant,
                                          String name,
                                          OntologyCollection allOntology) {
        final ObjectFactory factory = new ObjectFactory();

        // find a matching term, if present, within this fuge object's ontology collection. If found,
        // use that rather than creating a new ontology term
        for ( JAXBElement<? extends OntologyTerm> jaxbElement : allOntology.getOntologyTerm() ) {
            OntologyTerm term = jaxbElement.getValue();
            if ( term instanceof OntologyIndividual && term.getTerm().equals( name ) ) {
                return term.getIdentifier();
            }
        }

        OntologyIndividual term = new OntologyIndividual();
        term = (OntologyIndividual) IdentifiableConverter.toFuge( term, identifier, endurant, name );

        // todo allow real ontology terms and have their term accession specified
        // todo change to create a "proper" tuple using OI dataProperty value and datatype for boolean and number
        // todo change to create a "proper" tuple using OI objectProperty value for words/phrases
        term.setTerm( name );
        allOntology.getOntologyTerm().add( factory.createOntologyIndividual( term ) );

        return term.getIdentifier();
    }


}
