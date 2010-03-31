package net.sourceforge.symba.web.server.conversion.fuge;

import net.sourceforge.fuge.util.generated.ActionApplication;
import net.sourceforge.fuge.util.generated.FuGE;
import net.sourceforge.fuge.util.generated.GenericAction;
import net.sourceforge.fuge.util.generated.GenericProtocol;
import net.sourceforge.fuge.util.generated.GenericProtocolApplication;
import net.sourceforge.fuge.util.generated.Person;
import net.sourceforge.fuge.util.generated.Protocol;
import net.sourceforge.fuge.util.generated.ProtocolApplication;
import net.sourceforge.symba.web.client.gui.InputValidator;
import net.sourceforge.symba.web.shared.ExperimentStep;
import net.sourceforge.symba.web.shared.ExperimentStepHolder;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

public class SymbaInvestigationCreator {

    public Investigation toSymbaInvestigation( FuGE fuge ) {
        // discover if the investigation is a template
        boolean template = fuge.getName().contains( InputValidator.TEMPLATE );
        // discover if the investigation is considered completed
        boolean completed = fuge.getName().contains( InputValidator.COMPLETED_INVESTIGATION );

        String investigationId = fuge.getInvestigationCollection().getInvestigation().get( 0 ).getIdentifier();
        String investigationName = fuge.getInvestigationCollection().getInvestigation().get( 0 ).getName();
        return new Investigation( template, completed, investigationId, investigationName,
                initProvider( fuge ), initExperiments( fuge ) );

    }

    @NotNull
    private Contact initProvider( @NotNull FuGE fuge ) {
        for ( JAXBElement element : fuge.getAuditCollection().getContact() ) {
            if ( element.getValue() instanceof Person ) {
                Person contact = ( Person ) element.getValue();
                if ( contact.getIdentifier().equals( fuge.getProvider().getContactRole().getContactRef() ) ) {
                    // match found to the contact value
                    // todo allow people to have organisations.
                    return new Contact( contact.getIdentifier(), contact.getFirstName(), contact.getLastName(),
                            contact.getEmail() );
                }
            }
        }
        // no matching contact found in the AuditCollection. This shouldn't happen. Make an empty contact if it does.
        Contact c = new Contact();
        c.setId( createRandom() ); // don't use the built-in createId method, as it uses a GWT-specific library.
        return c;
    }

    private ArrayList<ExperimentStepHolder> initExperiments( FuGE fuge ) {

        ArrayList<ExperimentStepHolder> stepList = new ArrayList<ExperimentStepHolder>();

        // we don't need to store the software value - it is currently filled in with a hard-coded SyMBA value
        // only upon deposition in the database - the UI doesn't care about it.

        // first, find the top-level protocol
        GenericProtocol topProtocol = null;
        for ( JAXBElement element : fuge.getProtocolCollection().getProtocol() ) {
            if ( element.getValue() instanceof GenericProtocol ) {
                GenericProtocol gp = ( GenericProtocol ) element.getValue();
                if ( gp.getName().contains( InputValidator.TOP_PROTOCOL ) ) {
                    topProtocol = gp;
                    break;
                }
            }
        }
        if ( topProtocol == null ) {
            // error finding the top-level protocol. It could be the saved protocol simply doesn't have any steps yet.
            return new ArrayList<ExperimentStepHolder>();
        }

        // find the associated top-level GPA: there will only ever be one
        GenericProtocolApplication topGpa = getGpaByProtocolRef( topProtocol.getIdentifier(),
                fuge.getProtocolCollection().getProtocolApplication() );

        // we now have a top protocol. Start there, and build up the steps as we go.
        // the top protocol is a placeholder, and not created by the user at all, therefore it doesn't need
        // to get stored in the UI Investigation object. However, all of its children do need adding.
        // we need to read the actions according to their ordinals
        // As it is a placeholder, it will never have parameters/data/materials
        for ( int iii = 0; iii < topProtocol.getAction().size(); iii++ ) {
            for ( int jjj = 0; jjj < topProtocol.getAction().size(); jjj++ ) {
                if ( topProtocol.getAction().get( iii ).getValue() instanceof GenericAction &&
                        topProtocol.getAction().get( iii ).getValue().getActionOrdinal() == iii ) {
                    GenericAction childAction = ( GenericAction ) topProtocol.getAction().get( iii ).getValue();

                    ExperimentStep step = new ExperimentStep();
                    step = GenericActionConverter.toSymba( step, childAction );

                    // information (including parameters) stored in the GenericProtocol associated with this Action
                    GenericProtocol referencedChildProtocol = getProtocol( childAction.getProtocolRef(),
                            fuge.getProtocolCollection().getProtocol() );
                    if ( referencedChildProtocol != null ) {
                        step = GenericProtocolConverter
                                .toSymba( step, referencedChildProtocol, fuge.getOntologyCollection() );
                    }

                    // external data and materials: from the GenericProtocolApplication referenced by the
                    // ActionApplication which also references this Action. The AA must be within the parent protocol's
                    // GenericProtocolApplication. There may be no external data or materials at this level - there
                    // could just be further ActionApplications. Just check the immediate AAs, in the same way as we
                    // just are checking the immediate GAs
                    for ( ActionApplication childAa : topGpa.getActionApplication() ) {
                        // check to see if it is a match to the current Action
                        if ( childAa.getActionRef().equals( childAction.getIdentifier() ) ) {
                            // this tells us the next protocol/step to add as a child of the current step, as well
                            // as the name of the GPA referenced by this AA.
                            step = GenericProtocolApplicationConverter.toSymba( step,
                                    getGpa( childAa.getProtocolApplicationRef(),
                                            fuge.getProtocolCollection().getProtocolApplication() ),
                                    fuge.getDataCollection(),
                                    fuge.getMaterialCollection() );
                        }
                    }

                    ExperimentStepHolder holder = new ExperimentStepHolder( step );
                    stepList.add( holder );
                }
            }
        }
        return stepList;
    }

    private GenericProtocol getProtocol( String id,
                                         List<JAXBElement<? extends Protocol>> protocols ) {
        for ( JAXBElement element : protocols ) {
            if ( element.getValue() instanceof GenericProtocol ) {
                GenericProtocol gp = ( GenericProtocol ) element.getValue();
                if ( gp.getIdentifier().equals( id ) ) {
                    return gp;
                }
            }
        }
        return null;
    }

    private GenericProtocolApplication getGpaByProtocolRef( String protocolRef,
                                                            List<JAXBElement<? extends ProtocolApplication>> protocolApplications ) {
        for ( JAXBElement element : protocolApplications ) {
            if ( element.getValue() instanceof GenericProtocolApplication ) {
                GenericProtocolApplication gpa = ( GenericProtocolApplication ) element.getValue();
                if ( gpa.getProtocolRef().equals( protocolRef ) ) {
                    return gpa;
                }
            }
        }
        return null;
    }

    private GenericProtocolApplication getGpa( String id,
                                               List<JAXBElement<? extends ProtocolApplication>> protocolApplications ) {
        for ( JAXBElement element : protocolApplications ) {
            if ( element.getValue() instanceof GenericProtocolApplication ) {
                GenericProtocolApplication gpa = ( GenericProtocolApplication ) element.getValue();
                if ( gpa.getIdentifier().equals( id ) ) {
                    return gpa;
                }
            }
        }
        return null;
    }

    // todo replace with proper creation method, e.g. the connection to the LSID server
    private String createRandom() {
        return ( ( Double ) Math.random() ).toString();
    }
}
