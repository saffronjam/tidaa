package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * Query Book by Rating
 *
 * @author Elias Alyoussef & Emil Karlsson
 */
public class QueryBookByRating implements QueryBookInterface {
    private final String rating;

    public QueryBookByRating(String rating) {
        this.rating = rating;
    }

    @Override
    public PreparedStatement getPreparedStatement(Connection connection) throws SQLException
    {
        boolean invalidRating = rating == null || rating.isEmpty() || !rating.matches("[0-9]+");
        String query = "";
        if(invalidRating)
        {
            query = "SELECT * " +
                    "FROM " + Tables.book;
        }
        else
        {
            query = "SELECT * " +
                    "FROM " + Tables.book + " b " +
                    "WHERE b.bookId IN ( " +
                        "SELECT r.bookId " +
                        "FROM " + Tables.review + " r " +
                        "GROUP BY r.bookId " +
                        "HAVING ROUND(AVG(r.rating), 0) = ROUND(?,0) )";
        }
        PreparedStatement stmt = connection.prepareStatement(query);
        if(!invalidRating)
        {
            stmt.setFloat(1, Float.parseFloat(rating));
        }
        return stmt;
    }
}
