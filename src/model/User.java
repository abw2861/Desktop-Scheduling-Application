package model;

/** This is the User class. */
public class User {
    int userId;
    String userName;

    public User(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    /** @return The user ID */
    public int getUserId() {
        return userId;
    }

    /** @param userId The user ID to set */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /** @return The username */
    public String getUserName() {
        return userName;
    }

    /** @param userName The username to set */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /** @return The username string */
    @Override
    public String toString() {
        return userName;
    }
}
