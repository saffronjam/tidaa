package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Immutable container of related data
 *
 * @author Elias Alyoussef & Emil Karlsson
 */
public class Book {

    private final int bookId;
    private final String isbn; // should check format
    private final String title;
    private final Date published;
    private final ArrayList<Author> authors;
    private final String storyline;
    private final ArrayList<Genre> genres;
    private final User createdBy;
    private final float rating;
    private final ArrayList<Review> reviews;

    /**
     * Creates a book with bookId known
     *
     * @param bookId
     * @param isbn
     * @param title
     * @param published
     * @param authors
     * @param storyline
     * @param genres
     * @param createdBy
     * @param rating
     * @throws IllegalArgumentException
     */
    public Book(int bookId, String isbn, String title, Date published, ArrayList<Author> authors, String storyline, ArrayList<Genre> genres, User createdBy, float rating, ArrayList<Review> reviews) throws IllegalArgumentException {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.published = published;
        this.authors = authors;
        this.storyline = storyline;
        this.genres = genres;
        this.createdBy = createdBy;
        this.rating = rating;
        this.reviews = reviews;
    }

    /**
     * Creates book without bookId
     * Used to temporarily store information before it exists in the database
     *
     * @param isbn
     * @param title
     * @param published
     * @param authors
     * @param storyline
     * @param genres
     * @param createdBy
     * @param rating
     */
    public Book(String isbn, String title, Date published, ArrayList<Author> authors, String storyline, ArrayList<Genre> genres, User createdBy, float rating, ArrayList<Review> reviews) {
        this(-1, isbn, title, published, authors, storyline, genres, createdBy, rating, reviews);
    }

    public int getBookId() {
        return bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public Date getPublished() {
        return published;
    }

    public String getStoryLine() {
        return storyline;
    }

    public ArrayList<Author> getAuthors() {
        return this.authors;
    }

    public ArrayList<Genre> getGenres() {
        return this.genres;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public float getRating() {
        return rating;
    }

    public String getRatingAsString() {
        if (rating > 0.0f) {
            return String.valueOf(rating);
        } else {
            return "No rating";
        }
    }

    public List<Review> getReviews() {
        return reviews;
    }

    @Override
    public String toString() {
        return title + ", " + isbn + ", " + published.toString();
    }
}
