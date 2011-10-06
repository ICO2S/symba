package net.sourceforge.symba.web.shared;

import java.io.Serializable;

public class Contact implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String emailAddress;

    private static final String DEFAULT_ID = "0";

    public Contact() {
        this.id = DEFAULT_ID;
        this.firstName = "";
        this.lastName = "";
        this.emailAddress = "";

        // this way doesn't seem to work right - ends up with the string values being null!
//        new Contact( "0", "", "", "" );
    }

    public Contact( String id, String firstName, String lastName, String emailAddress ) {
        this.id = id;
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.emailAddress = emailAddress.trim();
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress( String emailAddress ) {
        this.emailAddress = emailAddress;
    }

    public String getFullName() {
        return ( firstName + " " + lastName ).trim();
    }

    public boolean hasValidId() {
        return ! id.equals( DEFAULT_ID ) && ! id.isEmpty();
    }
}
