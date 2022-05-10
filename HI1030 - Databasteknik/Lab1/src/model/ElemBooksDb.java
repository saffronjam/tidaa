package model;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementments BookDBInterface with MySQL queries
 * @implements BooksDbInterface
 * @author Elias Alyoussef & Emil Karlsson
 */
public class ElemBooksDb implements BooksDbInterface
{
    private Connection dbConnection;

    @Override
    public boolean connect(String database) throws IOException, SQLException
    {
        String server = "jdbc:mysql://localhost:3306/" + database +"?UseClientEnc=UTF8";
        String user = "user";
        String password = "password";

        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dbConnection = DriverManager.getConnection(server, user, password);
        }
        catch (ClassNotFoundException cnfe)
        {
            System.out.println(cnfe.getMessage());
            dbConnection.close();
            return false;
        }
        return true;
    }

    @Override
    public void disconnect() throws IOException, SQLException {
        dbConnection.close();
    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws IOException, SQLException, IllegalStateException
    {
        ensureDatabaseConnection();
        return getBooks(new QueryBookByTitle(searchTitle));
    }

    @Override
    public List<Book> searchBooksByIsbn(String isbn) throws IOException, SQLException, IllegalStateException
    {
        ensureDatabaseConnection();
        searchBooksByAuthor("test");
        return getBooks(new QueryBookByIsbn(isbn));
    }

    @Override
    public List<Book> searchBooksByAuthor(String fullname) throws IOException, SQLException, IllegalStateException
    {
        ensureDatabaseConnection();
        return getBooks(new QueryBookByAuthor(fullname));
    }

    public List<Book> searchBooksByRating(String rating) throws IOException, SQLException, IllegalStateException
    {
        ensureDatabaseConnection();
        return getBooks(new QueryBookByRating(rating));
    }

    public List<Book> searchBooksByGenre(String genre) throws IOException, SQLException, IllegalStateException
    {
        ensureDatabaseConnection();
        return getBooks(new QueryBookByGenre(genre));
    }

    @Override
    public User getUserByCredentials(String username, String password) throws SQLException, IllegalStateException
    {
        ensureDatabaseConnection();

        User result = null;
        String query = "SELECT userId, socialSecNo, permissionLevel FROM " + Tables.user + " WHERE username = ? AND password = ?";
        try (PreparedStatement userStmt = dbConnection.prepareStatement(query)) {
            userStmt.setString(1, username);
            userStmt.setString(2, password);
            ResultSet resultSet = userStmt.executeQuery();
            if (resultSet.next()) {
                int userId = resultSet.getInt("userId");
                String socialSecNo = resultSet.getString("socialSecNo");
                User.PermissionLevel permissionLevel = User.PermissionLevel.valueOf(resultSet.getString("permissionLevel"));

                result = new User(userId, username, password, socialSecNo, permissionLevel);
            }
        }

        return result;
    }

    @Override
    public User getUserById(int userId) throws SQLException, IllegalStateException
    {
        ensureDatabaseConnection();

        if(userId == 0)
        {
            return new User(0, "Deleted", "", "", User.PermissionLevel.NONE);
        }

        User result = null;
        String query = "SELECT * FROM " + Tables.user + " WHERE userId = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next())
            {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String socialSecNo = resultSet.getString("socialSecNo");
                User.PermissionLevel permissionLevel = User.PermissionLevel.valueOf(resultSet.getString("permissionLevel"));

                result = new User(userId, username, password, socialSecNo, permissionLevel);
            }
        }

        return result;
    }

    @Override
    public void addNewBook(Book book) throws SQLException, IllegalStateException, IOException
    {
        ensureDatabaseConnection();

        String query = "INSERT INTO " + Tables.book + " (isbn, title, published, storyLine, createdBy) VALUE (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
        {
            dbConnection.setAutoCommit(false);
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setDate(3, book.getPublished());
            stmt.setString(4, book.getStoryLine());
            stmt.setInt(5, book.getCreatedBy().getUserId());
            stmt.executeUpdate();

            var keysResultSet = stmt.getGeneratedKeys();
            if (keysResultSet.next()) {
                int bookId = keysResultSet.getInt(1);
                setAuthors(bookId, book.getAuthors());
                setGenres(bookId, book.getGenres());
            } else {
                throw new IOException("Failed to fetch generated bookId");
            }
            dbConnection.commit();
        }
        catch (SQLException sqle)
        {
            dbConnection.rollback();
            throw new SQLException(sqle.getMessage());
        }
        finally
        {
            dbConnection.setAutoCommit(true);
        }
    }

    @Override
    public void updateBook(int bookId, String newStoryline, ArrayList<Author> newAuthors, ArrayList<Genre> newGenres) throws SQLException, IllegalStateException
    {
        ensureDatabaseConnection();

        try
        {
            dbConnection.setAutoCommit(false);
            if (newStoryline != null && !newStoryline.isEmpty()) {
                String queryUpdateBook = "UPDATE " + Tables.book + " SET storyLine=? WHERE bookId=?";
                try(PreparedStatement stmt = dbConnection.prepareStatement(queryUpdateBook))
                {
                    stmt.setString(1, newStoryline);
                    stmt.setInt(2, bookId);
                    stmt.executeUpdate();
                }
            }
            if (newAuthors != null && !newAuthors.isEmpty()) {
                String removeOldAuthors = "DELETE FROM " + Tables.write + " WHERE bookId=?";
                try(PreparedStatement stmt = dbConnection.prepareStatement(removeOldAuthors))
                {
                    stmt.setInt(1, bookId);
                    stmt.executeUpdate();
                    setAuthors(bookId, newAuthors);
                }
            }
            if (newGenres != null && !newGenres.isEmpty()) {
                String removeOldGenres = "DELETE FROM " + Tables.bookGenre + " WHERE bookId=?";
                try(PreparedStatement stmt = dbConnection.prepareStatement(removeOldGenres))
                {
                    stmt.setInt(1, bookId);
                    stmt.executeUpdate();
                    setGenres(bookId, newGenres);
                }
            }
            dbConnection.commit();
        }
        catch (SQLException sqle)
        {
            dbConnection.rollback();
            throw new SQLException(sqle.getMessage());
        }
        finally
        {
            dbConnection.setAutoCommit(true);
        }
    }

    @Override
    public void removeBook(int bookId) throws SQLException, IllegalStateException
    {
        ensureDatabaseConnection();

        String queryBook = "DELETE FROM " + Tables.book + " WHERE bookId=?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(queryBook)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        }
    }

    @Override
    public void rateBook(int bookId, int userId, Date date, String description, int rating) throws SQLException, IllegalStateException
    {
        ensureDatabaseConnection();

        String queryCheckDuplicate = "SELECT * FROM " + Tables.review + " r WHERE r.bookId=? AND r.userId=?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(queryCheckDuplicate)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, userId);
            var resultSet = stmt.executeQuery();
            if(resultSet.next())
            {
                throw new SQLException("User have already reviewed book");
            }
        }

        String queryInsertRating = "INSERT INTO " + Tables.review + " (userId, bookId, date, description, rating) VALUE (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.prepareStatement(queryInsertRating)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.setDate(3, date);
            stmt.setString(4, description);
            stmt.setInt(5, rating);
            stmt.executeUpdate();
        }
    }

    @Override
    public void addNewAuthor(Author newAuthor) throws SQLException, IllegalStateException, IOException
    {
        ensureDatabaseConnection();

        String queryInsertAuthor = "INSERT INTO " + Tables.author + " (fName, lName, dateOfBirth, createdBy) VALUE (?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.prepareStatement(queryInsertAuthor)) {
            stmt.setString(1, newAuthor.getFirstName());
            stmt.setString(2, newAuthor.getLastName());
            stmt.setDate(3, newAuthor.getDateOfBirth());
            stmt.setInt(4, newAuthor.getCreatedBy().getUserId());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Author> getAllAuthors() throws SQLException, IllegalStateException
    {
        ensureDatabaseConnection();

        var result = new ArrayList<Author>();
        String query = "SELECT * FROM " + Tables.author;
        try (PreparedStatement stmt = dbConnection.prepareStatement(query))
        {
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int authorId = resultSet.getInt("authorId");
                String fName = resultSet.getString("fName");
                String lName = resultSet.getString("lName");
                Date dateOfBirth = resultSet.getDate("dateOfBirth");
                int createdById = resultSet.getInt("createdBy");
                User createdBy = getUserById(createdById);

                result.add(new Author(authorId, fName, lName, dateOfBirth, createdBy));
            }
        }
        return result;
    }

    @Override
    public List<Review> getReviewsByBook(Book book) throws SQLException, IllegalStateException
    {
        ensureDatabaseConnection();

        var result = new ArrayList<Review>();
        String query = "SELECT * FROM " + Tables.review + " r WHERE r.bookId = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query))
        {
            stmt.setInt(1, book.getBookId());
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int userId = resultSet.getInt("userId");
                User user = getUserById(userId);
                Date date = resultSet.getDate("date");
                String description = resultSet.getString("description");
                int rating = resultSet.getInt("rating");
                result.add(new Review(user, book, date, description, rating));
            }
        }
        return result;
    }

    private List<Book> getBooks(QueryBookInterface bookQuery) throws SQLException
    {
        var result = new ArrayList<Book>();
        try ( PreparedStatement stmt = bookQuery.getPreparedStatement(dbConnection))
        {
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int bookId = resultSet.getInt("bookId");
                String isbn = resultSet.getString("isbn");
                String title = resultSet.getString("title");
                Date published = resultSet.getDate("published");
                String storyline = resultSet.getString("storyline");
                int createdById = resultSet.getInt("createdBy");
                User createdBy = getUserById(createdById);
                float rating = getRatingFromBookId(bookId);

                var authors = getAuthorsFromBookId(bookId);
                var genres = getGenresFromBookId(bookId);

                result.add(new Book(bookId, isbn, title, published, authors, storyline, genres, createdBy, rating));
            }
        }
        return result;
    }

    private ArrayList<Author> getAuthorsFromBookId(int bookId) throws SQLException
    {
        var authors = new ArrayList<Author>();
        String queryAuthors = "SELECT * FROM " + Tables.author + " a, " + Tables.write + " w WHERE a.authorId = w.authorId AND w.bookId = ?";
        try( PreparedStatement stmt = dbConnection.prepareStatement(queryAuthors))
        {
            stmt.setInt(1, bookId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int authorId = resultSet.getInt("authorId");
                String fName = resultSet.getString("fName");
                String lName = resultSet.getString("lName");
                Date dateOfBirth = resultSet.getDate("dateOfBirth");
                int createdById = resultSet.getInt("createdBy");
                User user = getUserById(createdById);

                authors.add(new Author(authorId, fName, lName, dateOfBirth, user));
            }
        }
        return authors;
    }

    private ArrayList<Genre> getGenresFromBookId(int bookId) throws SQLException
    {
        var genres = new ArrayList<Genre>();
        String queryGenres = "SELECT g.genre FROM " + Tables.bookGenre + " g, " + Tables.book + " b WHERE g.bookId = b.bookId AND g.bookId = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(queryGenres))
        {
            stmt.setInt(1, bookId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                Genre genre = Genre.valueOf(resultSet.getString("genre"));
                genres.add(genre);
            }
        }
        return genres;
    }

    private float getRatingFromBookId(int bookId) throws SQLException
    {
        float rating = 0.0f;
        String queryRating = "SELECT AVG(t.rating) as averageRating FROM " + Tables.review + " t WHERE bookId = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(queryRating))
        {
            stmt.setInt(1, bookId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next())
            {
                rating = resultSet.getFloat("averageRating");
            }
        }
        return rating;
    }

    private void setAuthors(int bookId, ArrayList<Author> authors) throws SQLException
    {
        String queryBookAuthor = "INSERT INTO " + Tables.write + " (bookId, authorId) VALUE (?, ?)";
        try (PreparedStatement stmt = dbConnection.prepareStatement(queryBookAuthor))
        {
            stmt.setInt(1, bookId);
            for (var author : authors) {
                stmt.setInt(2, author.getAuthorId());
                stmt.executeUpdate();
            }
        }
    }

    private void setGenres(int bookId, ArrayList<Genre> genres) throws SQLException
    {
        String queryBookGenre = "INSERT INTO " + Tables.bookGenre + " (bookId, genre) VALUE (?, ?)";
        try (PreparedStatement stmt = dbConnection.prepareStatement(queryBookGenre))
        {
            stmt.setInt(1, bookId);
            for (var genre : genres) {
                stmt.setString(2, genre.toString());
                stmt.executeUpdate();
            }
        }
    }

    private void ensureDatabaseConnection() throws IllegalStateException
    {
        if ( dbConnection == null )
        {
            throw new IllegalStateException("Not connected to database");
        }
    }
}
