package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.gui.InputValidator;
import net.sourceforge.symba.web.shared.Material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MaterialListPanel extends HorizontalPanel {
    private final ViewType viewType;

    public static enum ViewType {
        ASSIGN_TO_EXPERIMENT, CREATE_ONLY
    }

    private static final String SAVE_TEXT = "Save New Material";
    private final SelectorPanel selector;

    public MaterialListPanel( SymbaController controller,
                              String materialType,
                              ArrayList<Material> selectedMaterials,
                              ViewType viewType ) {
        this.viewType = viewType;

        CreatorPanel creator = new CreatorPanel( controller );
        selector = new SelectorPanel( creator, controller, selectedMaterials, materialType );

        // positioning
        add( selector );
        add( creator );

        creator.setVisible( false );
    }

    public ArrayList<String> getSelectedMaterialIds() {
        return selector.getSelectedMaterialIds();
    }

    public boolean hasVisibleList() {
        return selector.expandedMaterialBox.isVisible();
    }

    public ArrayList<Material> getOriginallySelectedMaterials() {
        return selector.selectedMaterials;
    }


    private class CreatorPanel extends VerticalPanel {

        private final TextBox nameBox;
        private final TextArea descriptionBox;
        private String originalId;

        public CreatorPanel( final SymbaController controller ) {

            originalId = "";

            HorizontalPanel name = new HorizontalPanel();
            HorizontalPanel description = new HorizontalPanel();

            nameBox = new TextBox();
            descriptionBox = new TextArea();
            descriptionBox.setCharacterWidth( 30 );
            descriptionBox.setVisibleLines( 5 );

            Button saveButton = new Button( SAVE_TEXT );

            name.add( new Label( "Material Name: " ) );
            description.add( new Label( "Description: " ) );

            name.add( nameBox );
            description.add( descriptionBox );

            // all handlers

            nameBox.addBlurHandler( new BlurHandler() {
                public void onBlur( BlurEvent event ) {
                    InputValidator.nonEmptyTextBoxStyle( nameBox );
                }
            } );

            saveButton.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    doSave( controller, originalId, nameBox, descriptionBox );
                }
            } );

            // positioning
            add( name );
            add( description );
            add( saveButton );

        }

        private void editMaterial( final Material material ) {
            originalId = material.getId();
            nameBox.setText( material.getName() );
            descriptionBox.setText( material.getDescription() );
        }

        private String doSave( final SymbaController controller,
                               String originalId,
                               final TextBox nameBox,
                               TextArea descriptionBox ) {

            String emptyValues = "";
            if ( nameBox.getText().trim().length() == 0 ) {
                emptyValues += "Material Name\n";
            }
            if ( emptyValues.length() > 1 ) {
                Window.alert( "The following fields should not be empty: " + emptyValues );
                return "";
            }

            // otherwise, it's OK to save the material
            final Material material = new Material();
            if ( originalId.length() == 0 ) {
                material.createId();
            } else {
                material.setId( originalId );
            }
            material.setName( nameBox.getText().trim() );
            if ( descriptionBox.getText().trim().length() > 0 ) {
                material.setDescription( descriptionBox.getText().trim() );
            }

            if ( isValidMaterial( material, originalId, controller ) ) {
                if ( originalId.length() == 0 ) {
                    controller.getRpcService()
                            .addOrUpdateMaterial( material, new AsyncCallback<HashMap<String, Material>>() {
                                public void onFailure( Throwable caught ) {
                                    Window.alert(
                                            "Failed to store material: " + material.getName() + "\n" +
                                                    caught.getMessage() );
                                }

                                public void onSuccess( HashMap<String, Material> result ) {
                                    controller.setStoredMaterials( result );
                                    selector.getSelectedMaterials().add( material );
                                    selector.showListBox();
                                    // clear any values
                                    nameBox.setText( "" );
                                    setVisible( false );
                                }
                            } );
                } else {
                    controller.getRpcService()
                            .addOrUpdateMaterial( material, new AsyncCallback<HashMap<String, Material>>() {
                                public void onFailure( Throwable caught ) {
                                    Window.alert( "Failed to update material: " + material.getName() + "\n" +
                                            caught.getMessage() );
                                }

                                public void onSuccess( HashMap<String, Material> result ) {
                                    controller.setStoredMaterials( result );
                                    // remove the original material from the selected list, if present
                                    for ( Material current : selector.getSelectedMaterials() ) {
                                        if ( current.getId().equals( material.getId() ) ) {
                                            selector.getSelectedMaterials().remove( current );
                                            break;
                                        }
                                    }
                                    selector.getSelectedMaterials().add( material );
                                    selector.showListBox();
                                    // clear any values
                                    nameBox.setText( "" );
                                    setVisible( false );
                                }
                            } );
                }
                return material.getId();
            } else {
                InputValidator.setWarning( nameBox );
                return "";
            }
        }

        private boolean isValidMaterial( Material material,
                                         String originalId,
                                         SymbaController controller ) {
            // basic validation: check that the name isn't already in the list
            for ( Material storedMaterial : controller.getStoredMaterials().values() ) {
                if ( storedMaterial.getName().equals( material.getName() ) &&
                        !storedMaterial.getId().equals( originalId ) ) {
                    Window.alert( "You may not use the name of an existing material to create a new material" );
                    return false;
                }
            }
            return true;
        }
    }

    private class SelectorPanel extends VerticalPanel {

        private static final String ADD_ICON = "/images/plus-noword.png";
        private static final String COPY_ICON = "/images/new_window.png";
        private static final String CLEAR_ICON = "/images/clear.png";

        private final Label countLabel;
        private final ListBox expandedMaterialBox;
        private final ArrayList<Material> selectedMaterials;
        private final String materialType;
        private final SymbaController controller;

        private SelectorPanel( final CreatorPanel creator,
                               final SymbaController controller,
                               ArrayList<Material> selectedMaterials,
                               String materialType ) {
            // by using the controller here rather than its materials, we'll catch any updates to its materials
            this.controller = controller;
            this.selectedMaterials = selectedMaterials;
            this.materialType = materialType;

            String moduleBase = GWT.getModuleBaseURL();
            String moduleName = GWT.getModuleName();
            String baseApp = moduleBase.substring( 0, moduleBase.lastIndexOf( moduleName ) );

            // you need slightly different URLs when in development mode (specifically, no prefix at all).
            String prefix = "";
            if ( GWT.isScript() ) {
                prefix = baseApp;
            }

            // start with a basic view where they can just see the number of materials
            HorizontalPanel hPanel = new HorizontalPanel();
            hPanel.setSpacing( 5 );
            countLabel = new Label( getMaterialsCount() );
            Image createImage = new Image( prefix + ADD_ICON );
            createImage.setTitle( "Create New Material" );
            Image copyImage = new Image( prefix + COPY_ICON );
            copyImage.setTitle( "Copy Material" );
            Image clearImage = new Image( prefix + CLEAR_ICON );
            clearImage.setTitle( "Clear Selected Materials" );
            hPanel.add( countLabel );
            hPanel.add( createImage );
            hPanel.add( copyImage );
            hPanel.add( clearImage );

            if ( viewType == ViewType.ASSIGN_TO_EXPERIMENT ) {
                expandedMaterialBox = new ListBox( true ); // set as a multiple select box
            } else {
                expandedMaterialBox = new ListBox( false ); // set as a single select box when creating new materials
            }
            expandedMaterialBox.setVisibleItemCount( 5 );

            // styles
            countLabel.addStyleName( "clickable-text" );
            createImage.addStyleName( "within-step-images" );
            copyImage.addStyleName( "within-step-images" );
            clearImage.addStyleName( "within-step-images" );

            // handlers
            countLabel.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    // when clicked, present an extended view where the materials can be chosen.
                    // Keep that view open for the duration.
                    showListBox();
                }
            } );

            createImage.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    creator.setVisible( true );
                }
            } );

            copyImage.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    // if the set of clickable materials isn't visible yet, make it visible.
                    if ( !expandedMaterialBox.isVisible() ) {
                        showListBox();
                    } else {
                        // If a single material has been chosen, then allow it to be copied and put into the
                        // creator panel.
                        if ( getSelectedMaterialIds().size() == 1 ) {
                            Material original = controller.getStoredMaterials()
                                    .get( getSelectedMaterialIds().get( 0 ) );
                            final Material copy = new Material( "",
                                    original.getName() + " " +
                                            ( Integer.toString( Random.nextInt() ).substring( 1, 4 ) ),
                                    original.getDescription() );
                            copy.createId();
                            controller.getRpcService()
                                    .addOrUpdateMaterial( copy, new AsyncCallback<HashMap<String, Material>>() {
                                        public void onFailure( Throwable caught ) {
                                            Window.alert( "Failed to store material: " + copy.getName() + "\n" +
                                                    caught.getMessage() );
                                        }

                                        public void onSuccess( HashMap<String, Material> result ) {
                                            controller.setStoredMaterials( result );
                                            selector.getSelectedMaterials().add( copy );
                                            selector.showListBox();
                                        }
                                    } );
                        } else {
                            // if >1 material (or no materials) has been chosen, force the user to just select one
                            Window.alert( "Please choose exactly one material to copy." );
                        }
                    }
                }
            } );

            clearImage.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    if ( getSelectedMaterialIds().isEmpty() ) {
                        return;
                    }
                    boolean result = true;
                    if ( viewType == ViewType.ASSIGN_TO_EXPERIMENT ) {
                        result = Window
                                .confirm(
                                        "Are you sure you wish to drop all materials from this experimental step?" );
                    }
                    if ( result ) {
                        selector.getSelectedMaterials().clear();
                        selector.showListBox();
                        creator.setVisible( false );
                    }
                }
            } );

            expandedMaterialBox.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    // if there is just one selected item, display the information about that item in the
                    // creator panel. This allows edits of existing items.
                    if ( getSelectedMaterialIds().size() == 1 ) {
                        creator.setVisible( true );
                        creator.editMaterial(
                                controller.getStoredMaterials().get( getSelectedMaterialIds().get( 0 ) ) );
                    }
                }
            } );

            // positioning
            add( hPanel );
            add( expandedMaterialBox );
            expandedMaterialBox.setVisible( false );

        }

        private void populateListBox() {
            expandedMaterialBox.clear();
            // in order to sort, you can't have the information in a HashMap
            for ( Material material : sortMaterials( controller.getStoredMaterials().values() ) ) {
                expandedMaterialBox.addItem( material.getName(), material.getId() );
                // ensure all values selected earlier are automatically selected here
                for ( Material selected : selectedMaterials ) {
                    if ( material.getId().equals( selected.getId() ) ) {
                        expandedMaterialBox.setItemSelected( expandedMaterialBox.getItemCount() - 1, true );
                        break;
                    }
                }
            }
        }

        private ArrayList<Material> sortMaterials( Collection<Material> collection ) {
            ArrayList<Material> asList = new ArrayList<Material>( collection );
            // sort by material name
            for ( int iii = 0; iii < asList.size(); iii++ ) {
                for ( int jjj = 0; jjj < asList.size() - 1; jjj++ ) {
                    if ( asList.get( jjj ).getName().compareToIgnoreCase( asList.get( jjj + 1 ).getName() ) >= 0 ) {
                        Material tmp = asList.get( jjj );
                        asList.set( jjj, asList.get( jjj + 1 ) );
                        asList.set( jjj + 1, tmp );
                    }
                }
            }
            return asList;
        }

        private String getMaterialsCount() {
            if ( selectedMaterials.size() == 0 ) {
                return "Start selecting " + materialType + " materials";
            } else if ( selectedMaterials.size() == 1 ) {
                return "1 " + materialType + " material";
            } else {
                return selectedMaterials.size() + " " + materialType + " materials";
            }
        }

        public ArrayList<Material> getSelectedMaterials() {
            return selectedMaterials;
        }

        public void showListBox() {
            populateListBox(); // refresh with any new data
            expandedMaterialBox.setVisible( true );
        }

        public ArrayList<String> getSelectedMaterialIds() {
            ArrayList<String> ids = new ArrayList<String>();

            for ( int iii = 0; iii < expandedMaterialBox.getItemCount(); iii++ ) {
                if ( expandedMaterialBox.isItemSelected( iii ) ) {
                    ids.add( expandedMaterialBox.getValue( iii ) );
                }
            }
            return ids;
        }
    }
}
