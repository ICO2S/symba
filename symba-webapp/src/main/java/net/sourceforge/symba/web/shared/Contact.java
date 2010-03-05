package net.sourceforge.symba.web.shared;

import com.google.gwt.user.client.Random;

import java.io.Serializable;

@SuppressWarnings( "serial" )
public class Contact implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String emailAddress;

    public Contact() {
        this.id = "0";
        this.firstName = "";
        this.lastName = "";
        this.emailAddress = "";

        // this way doesn't seem to work right - ends up with the string values being null!
//        new Contact( "0", "", "", "" );
    }

    public Contact( String id,
                    String firstName,
                    String lastName,
                    String emailAddress ) {
        this.id = id;
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.emailAddress = emailAddress.trim();
    }

    public String getId() { return id; }

    public void setId( String id ) { this.id = id; }

    public void createId() {
        // todo better ID creation
        setId( Integer.toString( Random.nextInt() ) );
    }

    public String getFirstName() { return firstName; }

    public void setFirstName( String firstName ) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName( String lastName ) { this.lastName = lastName; }

    public String getEmailAddress() { return emailAddress; }

    public void setEmailAddress( String emailAddress ) { this.emailAddress = emailAddress; }

    public String getFullName() { return (firstName + " " + lastName).trim(); }
}
