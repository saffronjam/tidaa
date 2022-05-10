package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * Query Book by Author
 * @implement QueryBookInterface
 * @author Elias Alyoussef & Emil Karlsson
 */
public class QueryBookByAuthor implements QueryBookInterface {
    private final String fullname;

    public QueryBookByAuthor(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public PreparedStatement getPreparedStatement(Connection connection) throws SQLException
    {
        String query =
                "SELECT *" +
                "FROM " + Tables.book + " b " +
                "WHERE b.bookId IN ( "+
                    "SELECT w.bookId " +
                    "FROM " + Tables.author + " a, " + Tables.write + " w " +
                    "WHERE a.authorId = w.authorId AND CONCAT(a.fname, \" \", a.lname) LIKE ?" +
                ")";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, "%" + fullname + "%");
        return stmt;
    }
}
