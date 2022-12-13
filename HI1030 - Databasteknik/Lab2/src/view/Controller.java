package view;

import javafx.scene.control.Alert;
import model.*;

import com.mongodb.*;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

import static javafx.scene.control.Alert.AlertType.*;

/**
 * The controller is responsible for handling user requests and update the view
 * (and in some cases the model).
 *
 * @author Elias Alyoussef & Emil Karlsson
 */
public class Controller {

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model
    private final ArrayList<Book> emptyList;
    private UserSession userSession;
    private Book selectedBook;
    private final String isbnPattern = "[0-9]{13}";
    private final String databaseName = "db_books";

    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
        this.emptyList = new ArrayList<>();
        onConnect();
    }

    /**
     * Queries the database to fetch every author
     *
     * @return A list of every author in the database
     */
    public List<Author> getAllAuthors() {
        List<Author> result = null;
        try {
            result = booksDb.getAllAuthors();
        } catch (MongoException me) {
            booksView.showAlertAndWait("Failed to get authors from datebase\nReason: " + me.getMessage() + "\n\nPlease try again", ERROR);
        }
        return result;
    }

    /**
     * Creating a new author in the database
     *
     * @param author Containing all the information about the new author
     */
    protected void onCreateAuthor(Author author) {
        Thread thread = new Thread(() -> {
            try {
                booksDb.addNewAuthor(author);
            } catch (IOException ioe) {
                showAlertAndWaitLater("Failed to communicate with database\nReason: " + ioe.getMessage(), ERROR);
            } catch (IllegalStateException ise) {
                showAlertAndWaitLater("Failed to create book\nReason: " + ise.getMessage(), ERROR);
            } catch (MongoException me) {
                showAlertAndWaitLater("Failed to add book to database!\nReason: " + me.getMessage() + "\n\nPlease try again", ERROR);
            }
        });

        thread.start();
    }

    protected void setSelectedBook(Book book) {
        selectedBook = book;
    }

    protected Book getSelectedBook() {
        return selectedBook;
    }

    protected UserSession getUserSession() {
        return userSession;
    }

    /**
     * Updating selected book in the database
     *
     * @param newStoryline
     * @param newAuthors
     * @param newGenres
     */
    protected void onUpdateSelectedBook(String newStoryline, ArrayList<Author> newAuthors, ArrayList<Genre> newGenres) {
        if (!ensureSelectedBook()) {
            return;
        }

        Thread thread = new Thread(() -> {
            try {
                booksDb.updateBook(selectedBook.getBookId(), newStoryline, newAuthors, newGenres);
            } catch (IllegalStateException ise) {
                showAlertAndWaitLater("Failed to update book.\nReason: " + ise.getMessage(), ERROR);
            } catch (MongoException me) {
                showAlertAndWaitLater("Failed to update book in database.\nReason: " + me.getMessage(), ERROR);
            } catch (Exception e) {
                showAlertAndWaitLater("Unknown database error.\n" + e.getMessage(), ERROR);
            }
        });

        thread.start();
    }

    /**
     * Removing selected book in the database
     */
    protected void onRemoveSelectedBook() {
        if (!ensureSelectedBook()) {
            return;
        }

        Thread thread = new Thread(() -> {
            try {
                booksDb.removeBook(selectedBook.getBookId());
            } catch (IllegalStateException ise) {
                showAlertAndWaitLater("Failed to remove book.\nReason: " + ise.getMessage(), ERROR);
            } catch (MongoException me) {
                showAlertAndWaitLater("Failed to remove book from database.\nReason: " + me.getMessage(), ERROR);
            } catch (Exception e) {
                showAlertAndWaitLater("Unknown database error.\n" + e.getMessage(), ERROR);
            }
        });

        thread.start();
    }

    /**
     * Searches for books
     *
     * @param searchFor
     * @param mode
     */
    protected void onSearchSelected(String searchFor, SearchMode mode) {
        Thread thread = new Thread(() -> {
            try {
                if (searchFor != null) {
                    List<Book> result = null;
                    switch (mode) {
                        case Title:
                            result = booksDb.searchBooksByTitle(searchFor);
                            break;
                        case ISBN:
                            result = booksDb.searchBooksByIsbn(searchFor);
                            break;
                        case Author:
                            result = booksDb.searchBooksByAuthor(searchFor);
                            break;
                        case Rating:
                            result = booksDb.searchBooksByRating(searchFor);
                            break;
                        case Genre:
                            result = booksDb.searchBooksByGenre(searchFor);
                            break;
                        default:
                    }
                    if (result != null) {
                        List<Book> finalResult = result;
                        Platform.runLater(() -> {
                            booksView.setSearchStatusInfo(finalResult.size() + " results");
                            booksView.displayBooks(finalResult);
                        });
                    }
                }
            } catch (IllegalStateException ise) {
                showAlertAndWaitLater("Failed to search for " + mode.toString() + "\nReason: " + ise.getMessage(), ERROR);
            } catch (MongoException me) {
                showAlertAndWaitLater("Failed to search for book in database.\nReason: " + me.getMessage(), ERROR);
            } catch (Exception e) {
                showAlertAndWaitLater("Unknown database error.\n" + e.getMessage(), ERROR);
            }
        });

        thread.start();
    }

    /**
     * Creating a review for a book in the database
     *
     * @param description
     * @param rating
     */
    protected void onRateSelectedBook(String description, int rating) {
        if (!ensureSelectedBook()) {
            return;
        }

        Thread thread = new Thread(() -> {
            try {
                var convertedDate = Date.valueOf(LocalDate.now());
                booksDb.rateBook(selectedBook.getBookId(), userSession.getUser().getUserId(), convertedDate, description, rating);
            } catch (IllegalStateException ise) {
                showAlertAndWaitLater("Failed to rate book.\nReason: " + ise.getMessage(), ERROR);
            } catch (MongoException me) {
                showAlertAndWaitLater("Failed to add rating to database.\nReason: " + me.getMessage(), ERROR);
            } catch (Exception e) {
                showAlertAndWaitLater("Unknown database error.\n" + e.getMessage(), ERROR);
            }
        });

        thread.start();
    }

    /**
     * Connecting to database
     */
    protected void onConnect() {
        booksDb.connect(databaseName);
        booksView.showAlertAndWait("Successfully connected to database", INFORMATION);
    }

    /**
     * Disconnects from the database
     */
    protected void onDisconnect() {
        booksDb.disconnect();
        booksView.displayBooks(emptyList);
    }

    /**
     * Creates a new book in the database
     *
     * @param book
     */
    protected void onCreateBook(Book book) {
        Thread thread = new Thread(() -> {
            try {
                booksDb.addNewBook(book);
            } catch (IOException ioe) {
                showAlertAndWaitLater("Failed to communicate with database\nReason: " + ioe.getMessage(), ERROR);
            } catch (IllegalStateException ise) {
                showAlertAndWaitLater("Failed to create book\nReason: " + ise.getMessage(), ERROR);
            } catch (MongoException me) {
                showAlertAndWaitLater("Failed to add book to database.\nReason: " + me.getMessage() + "\n\nPlease try again", ERROR);
            } catch (Exception e) {
                showAlertAndWaitLater("Failed to create book.\nReason: " + e.getMessage() + "\n\nPlease try again", ERROR);
            }
        });

        thread.start();
    }

    /**
     * Attempts login to database with user
     *
     * @param username
     * @param password
     */
    protected void onLogin(String username, String password) {
        Thread thread = new Thread(() -> {
            try {
                User result = booksDb.getUserByCredentials(username, password);
                if (result == null) {
                    showAlertAndWaitLater("Bad credentials", ERROR);
                } else {
                    userSession = new UserSession(result);
                    Platform.runLater(() -> {
                        booksView.setLoginStatusInfo("Logged in as: " + userSession.toString());
                    });
                }
            } catch (IllegalStateException ise) {
                showAlertAndWaitLater("Failed to login\nReason: " + ise.getMessage(), ERROR);
            } catch (MongoException me) {
                showAlertAndWaitLater("Failed to login to database!\nReason: " + me.getMessage() + "\n\nPlease try again", ERROR);
            }
        });

        thread.start();
    }

    /**
     * Logs out from database
     */
    protected void onLogout() {
        if (userSession == null) {
            return;
        }
        userSession = null;
        booksView.setLoginStatusInfo("Not logged in");
    }

    protected boolean hasUserRight(User.PermissionLevel permissionLevel) {
        return userSession != null && userSession.getUser().getPermissionLevel().ordinal() >= permissionLevel.ordinal();
    }

    private boolean ensureSelectedBook() {
        if (selectedBook == null) {
            booksView.showAlertAndWait("No selected book", WARNING);
            return false;
        }
        return true;
    }

    private void showAlertAndWaitLater(String message, Alert.AlertType alertType) {
        Platform.runLater(() -> {
            booksView.showAlertAndWait(message, alertType);
        });
    }
}
