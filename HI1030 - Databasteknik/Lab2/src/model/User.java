package model;

/**
 * Immutable container of related data
 *
 * @author Elias Alyoussef & Emil Karlsson
 */
public class User
{
    public enum PermissionLevel
    {
        RATE, CREATE, MODIFY, NONE
    }

    private final int userId;
    private final String username;
    private final String password;
    private final String socialSecNo;
    private final PermissionLevel permissionLevel;

    /*
        Creates a user with a userId known
     */
    public User(int userId, String username, String password, String socialSecNo, PermissionLevel permissionLevel) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.socialSecNo = socialSecNo;
        this.permissionLevel = permissionLevel;
    }

    /*
        Creates a user without a userId
     */
    public User(String username, String password, String socialSecNo, PermissionLevel permissionLevel) {
        this(-1, username, password, socialSecNo, permissionLevel);
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSocialSecNo() {
        return socialSecNo;
    }

    public PermissionLevel getPermissionLevel() {
        return permissionLevel;
    }

    @Override
    public String toString(){
        return username;
    }
}
