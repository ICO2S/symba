package net.sourceforge.symba.web.shared;

/**
 * This class stores information about the currently logged-in user. This is not necessarily the same thing
 * as the contacts within an experiment.
 */
public class User {
     public static enum UserType {
        ADMIN, EXPERT, STANDARD
    }

    private UserType userType;
    private String userId;
    private String userName;

    public User() {
        userType = UserType.STANDARD;
        userId = "";
        userName = "";
    }

    public UserType getUserType() {
        return userType;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
}
