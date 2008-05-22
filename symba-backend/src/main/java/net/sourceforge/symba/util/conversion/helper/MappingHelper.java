package net.sourceforge.symba.util.conversion.helper;

import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import net.sourceforge.symba.ServiceLocator;

/**
 * The interface for all Helpers, providing basic methods they must implement, which then can be used when retrieving
 * the objects via web services, as well as generally within SyMBA.
 * <p/>
 * P stands for the FuGE POJO object made by AndroMDA
 * <p/>
 * J stands for the FuGE JAXB object made by JAXB2
 * <p/>
 * $LastChangedDate$ $LastChangedRevision$ $Author$ $HeadURL$
 */

public interface MappingHelper<P, J> {

    static final RealizableEntityService reService = ServiceLocator.instance().getRealizableEntityService();

    /**
     * Marshal the contents of the POJO object into the JAXB object
     *
     * @param j the FuGE JAXB object to be filled
     * @param p the FuGE POJO to parse
     *
     * @return p p with the contents of j put into it
     */
    J marshal( J j, P p ) throws RealizableEntityServiceException;

    /**
     * Unmarshal the contents of the JAXB object into the POJO object
     *
     * @param j the FuGE JAXB object to parse
     * @param p the FuGE POJO to be filled
     *
     * @return p p with the contents of j put into it
     *
     * @throws RuntimeException if there is an error with the unmarshaling
     */
    P unmarshal( J j, P p ) throws RealizableEntityServiceException;

    /**
     * @param j the empty (or not!) object to fill and return
     * @return the randomly-generated JAXB FuGE object
     */
    J generateRandomXML(J j);

    P getLatestVersion( P p ) throws RealizableEntityServiceException;
}
