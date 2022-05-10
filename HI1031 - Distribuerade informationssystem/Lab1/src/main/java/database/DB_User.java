package database;

import application.Category;
import application.PermissionLevel;
import application.Product;
import application.User;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB_User {

    public static User login(Connection connection, String username, String password) throws SQLException {
        User result = null;

        String query = "SELECT iduser, permissionLevel FROM user WHERE name = ? AND password = ?";
        try (PreparedStatement userStmt = connection.prepareStatement(query)) {
            userStmt.setString(1, username);
            userStmt.setString(2, password);
            ResultSet resultSet = userStmt.executeQuery();
            if (resultSet.next()) {
                int userId = resultSet.getInt("iduser");
                PermissionLevel permissionLevel = PermissionLevel.valueOf(resultSet.getString("permissionLevel"));
                result = new User(userId, username, permissionLevel);
            }
        }

        return result;
    }

    public static User register(Connection connection, String username, String password, PermissionLevel permissionLevel) throws SQLException {
        User result;

        String query = "INSERT INTO user (name, password, permissionLevel) VALUE (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, permissionLevel.toString());
            stmt.executeUpdate();

            int userId;
            var keysResultSet = stmt.getGeneratedKeys();
            if (keysResultSet.next()) {
                userId = keysResultSet.getInt(1);
            } else {
                throw new SQLException("Failed to fetch generated user id");
            }
            result = new User(userId, username, permissionLevel);
            connection.commit();
        } catch (SQLException sqle) {
            connection.rollback();
            throw new SQLException(sqle.getMessage());
        } finally {
            connection.setAutoCommit(true);
        }

        return result;
    }

    public static void update(Connection connection, User user) throws SQLException {
        String uptProduct = "UPDATE user SET name=?, permissionlevel=? WHERE iduser=?";
        try (PreparedStatement stmt = connection.prepareStatement(uptProduct)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPermissionLevel().toString());
            stmt.setInt(3, user.getUid());
            stmt.executeUpdate();
        }
    }

    public static List<User> getAll(Connection connection) throws SQLException {
        List<User> result;

        String query = "SELECT iduser, name, permissionlevel FROM user";
        try (PreparedStatement userStmt = connection.prepareStatement(query)) {

            ResultSet resultSet = userStmt.executeQuery();
            result = new ArrayList<>();

            while (resultSet.next()) {
                var id = resultSet.getInt("iduser");
                var name = resultSet.getString("name");
                var permissionlevel = PermissionLevel.valueOf(resultSet.getString("permissionlevel"));
                var user = new User(id, name, permissionlevel);

                result.add(user);
            }
        }

        return result;
    }

}
