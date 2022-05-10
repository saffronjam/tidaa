package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * Query Book by ISBN
 * @implement QueryBookInterface
 * @author Elias Alyoussef & Emil Karlsson
 */
public class QueryBookByIsbn implements QueryBookInterface {

    private final String isbn;

    public QueryBookByIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public PreparedStatement getPreparedStatement(Connection connection) throws SQLException
    {
        String query = "SELECT * FROM " + Tables.book + " b WHERE b.isbn LIKE ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, "%" + isbn + "%");
        return stmt;
    }
}
