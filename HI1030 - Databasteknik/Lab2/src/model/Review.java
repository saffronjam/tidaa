package model;

import java.util.Date;

/**
 * Immutable container of related data
 *
 * @author Elias Alyoussef & Emil Karlsson
 */
public class Review
{
    private final User user;
    private final Date date;
    private final String description;
    private final int rating;

    /**
     * Creates a review
     */
    public Review(User user, Date date, String description, int rating) {
        this.user = user;
        this.date = date;
        this.description = description;
        this.rating = rating;
    }

    public User getUser() {
        return user;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public int getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return rating > 0.0f ? String.valueOf(rating) : "No rating";
    }
}
