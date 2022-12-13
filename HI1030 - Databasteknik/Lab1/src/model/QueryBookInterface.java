package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * interface for Query Book
 *
 * @author Elias Alyoussef & Emil Karlsson
 */
public interface QueryBookInterface {
    /**
     * get Prepared Statement depending on searching type
     * @param connection
     * @return a prepared statement
     * @throw SQLException
     */
    public PreparedStatement getPreparedStatement(Connection connection) throws SQLException;
}
