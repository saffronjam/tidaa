package model;

import com.mongodb.MongoException;

import java.io.IOException;
import com.mongodb.MongoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * This interface declares methods for querying a Books database.
 * Different implementations of this interface handles the connection and
 * queries to a specific DBMS and database, for example a MySQL or a MongoDB
 * database.
 * 
 * @author Elias Alyoussef & Emil Karlsson
 */
public interface BooksDbInterface {

    /**
     * Connect to the database.
     * @param database
     * @return true on successful connection.
     */
    public boolean connect(String database);

    /**
     * Disconnects from the database.
     */
    public void disconnect();

    /**
     * Search books by their title
     * @param title The title to search matches with
     * @return list of books with matching to argument given
     * @throws IOException when connection to database fails
     * @throws MongoException when database fails to process the query
     * @throws IllegalStateException when the database is not connected
     */
    public List<Book> searchBooksByTitle(String title) throws IOException, MongoException, IllegalStateException;

    /**
     * Search books by their ISBN
     * @param searchFor The ISBN to search matches with
     * @return list of books with matching to argument given
     * @throws IOException when connection to database fails
     * @throws MongoException when database fails to process the query
     * @throws IllegalStateException when the database is not connected
     */
    public List<Book> searchBooksByIsbn(String searchFor) throws IOException, MongoException, IllegalStateException;

    /**
     * Search books by their authors
     * @param searchFor One author name to search matches with
     * @return list of books with matching to argument given
     * @throws IOException when connection to database fails
     * @throws MongoException when database fails to process the query
     * @throws IllegalStateException when the database is not connected
     */
    public List<Book> searchBooksByAuthor(String searchFor) throws IOException, MongoException, IllegalStateException;

    /**
     * Search books by their rating
     * @param searchFor The rating to search matches with. This is value, and the
     *                  rating to match againstn is always rounded to be able to match.
     * @return list of books with matching to argument given
     * @throws IOException when connection to database fails
     * @throws MongoException when database fails to process the query
     * @throws IllegalStateException when the database is not connected
     */
    public List<Book> searchBooksByRating(String searchFor) throws IOException, MongoException, IllegalStateException;

    /**
     * Search books by their genres
     * @param searchFor One genre to search matches with.
     * @return list of books with matching to argument given
     * @throws IOException when connection to database fails
     * @throws MongoException when database fails to process the query
     * @throws IllegalStateException when the database is not connected
     */
    public List<Book> searchBooksByGenre(String searchFor) throws IOException, MongoException, IllegalStateException;

    /**
     * Search for a user with given username and password
     * @param username to match with in the database
     * @param password to match with in the database
     * @return User if match else null
     * @throws MongoException when database fails to process the query
     * @throws IllegalStateException when the database is not connected
     */
    public User getUserByCredentials(String username, String password) throws MongoException, IllegalStateException;

    /**
     * Search for a user with given userId
     * @param userId to match in the database
     * @return User if match is found, else null
     * @throws MongoException when database fails to process the query
     * @throws IllegalStateException when the database is not connected
     */
    public User getUserById(int userId) throws MongoException, IllegalStateException;

    /**
     * Push new book into database
     * @param book The book to put into the database
     * @throws IOException when connection to database fails
     * @throws MongoException when database fails to process the query
     * @throws IllegalStateException when the database is not connected
     */
    public void addNewBook(Book book) throws MongoException, IllegalStateException, IOException;

    /**
     * Update an existing book in the database. Any altering parameters that are null will be ignored
     * @param bookId The id of the book to update
     * @param newStoryline The new storyline. If this is null it will be ignored
     * @param newAuthors The new authors. If this is null it will be ignored
     * @param newGenres The new genres. If this is null it will be ignored
     * @throws MongoException when database fails to process the query
     * @throws IllegalStateException when the database is not connected
     */
    public void updateBook(int bookId, String newStoryline, ArrayList<Author> newAuthors, ArrayList<Genre> newGenres) throws MongoException, IllegalStateException;

    /**
     * remove a book from database
     * @param bookId
     * @throws MongoException when database fails to process the query
     * @throws IllegalStateException when the database is not connected
     */
    public void removeBook(int bookId) throws MongoException, IllegalStateException;

    /**
     * Give rating for a book in database
     * @param bookId
     * @param userId
     * @param date
     * @param description
     * @param rating
     * @throws MongoException when database fails to process the query
     * @throws IllegalStateException when the database is not connected
     */
    public void rateBook(int bookId, int userId, Date date, String description, int rating) throws MongoException, IllegalStateException;

    /**
     * add New Author to the database
     * @param author
     * @throws IOException when connection to database fails
     * @throws MongoException when database fails to process the query
     * @throws IllegalStateException when the database is not connected
     */
    public void addNewAuthor(Author author) throws MongoException, IllegalStateException, IOException;

    /**
     * get All Authors in the database
     * @return
     * @throws MongoException when database fails to process the query
     * @throws IllegalStateException when the database is not connected
     */
    public List<Author> getAllAuthors() throws MongoException, IllegalStateException;

}