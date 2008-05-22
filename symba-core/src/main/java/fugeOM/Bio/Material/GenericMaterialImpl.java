// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package fugeOM.Bio.Material;

import java.util.Collection;
import java.util.HashSet;

/**
 * @see fugeOM.Bio.Material.GenericMaterial
 */
public class GenericMaterialImpl
    extends fugeOM.Bio.Material.GenericMaterial
{
    /**
     * The serial version UID of this class. Needed for serialization.
     */
    private static final long serialVersionUID = -77381448226860042L;


    /**
     * Overriding of the default setComponents() method. See AndroMDA forum for unfixed bug that causes a need for this:
     * URL:
     * @param components
     */
    public void setComponents( Collection components ) {
        super.setComponents( new HashSet( components ) );
    }

    /**
     * @see fugeOM.Bio.Material.GenericMaterial#dummy()
     */
    public void dummy()
    {
        // @todo find a better solution than creation of a dummy method. See the UML for a complete reason why this was done. Forum post submitted to AndroMDA.
        throw new java.lang.UnsupportedOperationException("fugeOM.Bio.Material.GenericMaterial.dummy() Not implemented!");
    }

}