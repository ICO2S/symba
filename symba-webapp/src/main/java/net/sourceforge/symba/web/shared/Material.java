package net.sourceforge.symba.web.shared;

import java.io.Serializable;

public class Material implements Serializable {

    private String id;
    private String name;
    private String description;
    //todo add measurement, which can be used for input materials.

    private static final String DEFAULT_ID = "0";

    public Material() {
        id = DEFAULT_ID;
        name = "";
        description = "";
    }

    public Material( String id, String name, String description ) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public boolean hasValidId() {
        return ! id.equals( DEFAULT_ID ) && ! id.isEmpty();
    }
}
