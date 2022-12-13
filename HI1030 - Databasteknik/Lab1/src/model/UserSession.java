package model;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
/**
 * User Session is responsible that a user can be able to log in with a
 *
 * @author Elias Alyoussef & Emil Karlsson
 */
public class UserSession {

    private final User user;
    private final LocalTime loggedInSince;

    public UserSession(User user)
    {
        this.user = user;
        this.loggedInSince = LocalTime.now();
    }
    /**
     *  @Return the user
     */
    public User getUser()
    {
        return user;
    }

    @Override
    public String toString() {
        return "[ " + user.getUsername() + " ]" +
                " Permission level: [ " + user.getPermissionLevel() + " ]" +
                " Login time: [ " + loggedInSince.truncatedTo(ChronoUnit.MINUTES) + " ]";
    }
}
