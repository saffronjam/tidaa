package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Query Book by Genre
 * @implement QueryBookInterface
 * @author Elias Alyoussef & Emil Karlsson
 */
public class QueryBookByGenre implements QueryBookInterface {
    private final String bookGenre;

    public QueryBookByGenre(String bookGenre) {
        this.bookGenre = bookGenre;
    }

    @Override
    public PreparedStatement getPreparedStatement(Connection connection) throws SQLException
    {
        String query =
                "SELECT *" +
                "FROM " + Tables.book + " b " +
                "WHERE b.bookId IN ( "+
                    "SELECT bg.bookId " +
                    "FROM " + Tables.bookGenre + " bg " +
                    "WHERE bg.genre LIKE ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, "%" + bookGenre + "%");
        return stmt;
    }
}
