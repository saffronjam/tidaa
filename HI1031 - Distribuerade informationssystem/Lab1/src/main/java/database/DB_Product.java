package database;

import application.Category;
import application.PermissionLevel;
import application.Product;
import application.User;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.*;

public class DB_Product {

    public static Product get(Connection connection, int id, boolean includeDeleted) throws SQLException {
        Product result = null;

        String query = "SELECT name, price, stock, category FROM product WHERE idproduct = ?";
        if (!includeDeleted) {
            query += " AND NOT STRCMP(category, \"Deleted\") = 0";
        }

        try (PreparedStatement userStmt = connection.prepareStatement(query)) {
            userStmt.setInt(1, id);
            ResultSet resultSet = userStmt.executeQuery();
            if (resultSet.next()) {
                var name = resultSet.getString("name");
                var price = resultSet.getInt("price");
                var stock = resultSet.getInt("stock");
                var category = Category.valueOf(resultSet.getString("category"));
                result = new Product(id, name, stock, price, category);
            }
        }

        return result;
    }

    public static List<Product> getAll(Connection connection, boolean includeDeleted) throws SQLException {
        List<Product> result;
        String query = "SELECT idproduct, name, price, stock, category FROM product";
        if (!includeDeleted) {
            query += " WHERE NOT STRCMP(category, \"Deleted\") = 0";
        }

        try (PreparedStatement userStmt = connection.prepareStatement(query)) {

            ResultSet resultSet = userStmt.executeQuery();
            result = new ArrayList<Product>();

            while (resultSet.next()) {
                var id = resultSet.getInt("idproduct");
                var name = resultSet.getString("name");
                var price = resultSet.getInt("price");
                var stock = resultSet.getInt("stock");
                var category = Category.valueOf(resultSet.getString("category"));
                var product = new Product(id, name, stock, price, category);

                result.add(product);
            }
        }

        return result;
    }

    public static void update(Connection connection, Product product) throws SQLException {
        update(connection, product.getId(), product);
    }

    public static void update(Connection connection, int id, Product product) throws SQLException {
        String uptProduct = "UPDATE product SET name=?, stock=?, price=?, category=? WHERE idproduct=?";
        try (PreparedStatement stmt = connection.prepareStatement(uptProduct)) {
            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getStock());
            stmt.setInt(3, product.getPrice());
            stmt.setString(4, product.getCategory().toString());
            stmt.setInt(5, id);
            stmt.executeUpdate();
        }
    }

    public static Product add(Connection connection, String name, int stock, int price, Category category) throws SQLException {
        Product result;

        String query = "INSERT INTO product (name, stock, price, category) VALUE (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            stmt.setString(1, name);
            stmt.setInt(2, stock);
            stmt.setInt(3, price);
            stmt.setString(4, category.toString());
            stmt.executeUpdate();

            int productId;
            var keysResultSet = stmt.getGeneratedKeys();
            if (keysResultSet.next()) {
                productId = keysResultSet.getInt(1);
            } else {
                throw new SQLException("Failed to fetch generated product id");
            }
            result = new Product(productId, name, stock, price, category);
            connection.commit();
        } catch (SQLException sqle) {
            connection.rollback();
            throw new SQLException(sqle.getMessage());
        } finally {
            connection.setAutoCommit(true);
        }

        return result;
    }

    public static void delete(Connection connection, int productId) throws SQLException {
        String uptProduct = "UPDATE product SET category=? WHERE idproduct=?";
        try (PreparedStatement stmt = connection.prepareStatement(uptProduct)) {
            stmt.setString(1, Category.Deleted.toString());
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }
}
