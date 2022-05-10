package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * Query Book by title
 * @implements QueryBookInterface
 * @author Elias Alyoussef & Emil Karlsson
 */
public class QueryBookByTitle implements QueryBookInterface {

    private final String title;

    public QueryBookByTitle(String title) {
        this.title = title;
    }

    @Override
    public PreparedStatement getPreparedStatement(Connection connection)  throws SQLException
    {
        String query = "SELECT * FROM " + Tables.book + " b WHERE b.title LIKE ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, "%" + title + "%");
        return stmt;
    }
}
