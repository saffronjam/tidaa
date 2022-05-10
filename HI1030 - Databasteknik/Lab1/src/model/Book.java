package model;

import java.sql.Date;
import java.util.ArrayList;

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
    private final String storyLine;
    private final ArrayList<Genre> genres;
    private final User createdBy;
    private final float rating;

    /**
     * Creates a book with bookId known
     * @param bookId
     * @param isbn
     * @param title
     * @param published
     * @param authors
     * @param storyLine
     * @param genres
     * @param createdBy
     * @param rating
     * @throws IllegalArgumentException
     */
    public Book(int bookId, String isbn, String title, Date published, ArrayList<Author> authors, String storyLine, ArrayList<Genre> genres, User createdBy, float rating) throws  IllegalArgumentException{
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.published = published;
        this.authors = authors;
        this.storyLine = storyLine;
        this.genres = genres;
        this.createdBy = createdBy;
        this.rating = rating;
    }

    /**
     * Creates book without bookId
     * Used to temporarily store information before it exists in the database
     * @param isbn
     * @param title
     * @param published
     * @param authors
     * @param storyLine
     * @param genres
     * @param createdBy
     * @param rating
     */
    public Book(String isbn, String title, Date published, ArrayList<Author> authors, String storyLine, ArrayList<Genre> genres, User createdBy, float rating) {
        this(-1, isbn, title, published, authors, storyLine, genres, createdBy, rating);
    }
    
    public int getBookId() { return bookId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public Date getPublished() { return published; }
    public String getStoryLine() { return storyLine; }
    public ArrayList<Author> getAuthors() {return this.authors;}
    public ArrayList<Genre> getGenres() {return this.genres;}
    public User getCreatedBy() {return createdBy;}
    public float getRating() { return rating;}
    public String getRatingAsString() {
        if(rating > 0.0f)
        {
            return String.valueOf(rating);
        }
        else{
            return "No rating";
        }
    }
    
    @Override
    public String toString() {
        return title + ", " + isbn + ", " + published.toString();
    }
}
