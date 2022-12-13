package database;

import application.Category;
import application.Order;
import application.Product;
import application.User;

import java.sql.*;
import java.util.*;

public class DB_Order {

    public static Order get(Connection connection, int id) throws SQLException {
        Order result = null;

        String query = "SELECT ordertime, packtime, shiptime, iduser FROM \"order\" WHERE idorder = ?";
        try (PreparedStatement userStmt = connection.prepareStatement(query)) {
            connection.setAutoCommit(true);
            userStmt.setInt(1, id);
            ResultSet resultSet = userStmt.executeQuery();
            if (resultSet.next()) {
                var ordertime = resultSet.getTimestamp("ordertime");
                var packtime = resultSet.getTimestamp("packtime");
                var shiptime = resultSet.getTimestamp("shiptime");
                var iduser = resultSet.getInt("iduser");
                var products = getProducts(connection, id);
                result = new Order(id, ordertime, packtime, shiptime, iduser, products);
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            connection.setAutoCommit(true);
        }
        return result;
    }

    public static List<Order> getAll(Connection connection) throws SQLException {
        List<Order> result;
        String query = "SELECT idorder, ordertime, packtime, shiptime, iduser FROM webshop.order ORDER BY idorder DESC";
        try (PreparedStatement userStmt = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            ResultSet resultSet = userStmt.executeQuery();
            result = new ArrayList<Order>();

            while (resultSet.next()) {
                var id = resultSet.getInt("idorder");
                var ordertime = resultSet.getTimestamp("ordertime");
                var packtime = resultSet.getTimestamp("packtime");
                var shiptime = resultSet.getTimestamp("shiptime");
                var iduser = resultSet.getInt("iduser");
                var products = getProducts(connection, id);
                var order = new Order(id, ordertime, packtime, shiptime, iduser, products);
                result.add(order);
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }

        return result;
    }


    public static List<Order> getAll(Connection connection, int userId) throws SQLException {
        List<Order> result;
        String query = "SELECT idorder, ordertime, packtime, shiptime, iduser FROM webshop.order WHERE iduser=? ORDER BY idorder DESC";
        try (PreparedStatement userStmt = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            userStmt.setInt(1, userId);

            ResultSet resultSet = userStmt.executeQuery();
            result = new ArrayList<Order>();

            while (resultSet.next()) {
                var id = resultSet.getInt("idorder");
                var ordertime = resultSet.getTimestamp("ordertime");
                var packtime = resultSet.getTimestamp("packtime");
                var shiptime = resultSet.getTimestamp("shiptime");
                var iduser = resultSet.getInt("iduser");
                var products = getProducts(connection, id);
                var order = new Order(id, ordertime, packtime, shiptime, iduser, products);
                result.add(order);
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }

        return result;
    }

    /**
     * Adjust and add order
     * <p>
     * Correct product amount so we don't go over stock and edit their stock
     * If amount is 0, the product will be removed from order
     * If product is deleted, it will be removed from order
     *
     * @param connection
     * @param cart
     * @param iduser
     * @throws SQLException
     */
    public static void add(Connection connection, HashMap<Integer, Integer> cart, int iduser) throws SQLException {
        String insertOrder = "INSERT INTO webshop.order (ordertime, iduser) VALUE (?, ?)";

        var builder = new StringBuilder();
        builder.append("INSERT INTO orderproduct (idorder, idproduct, amount) VALUES ");
        for (int i = 0; i < cart.size() - 1; i++) {
            builder.append("(?, ?, ?), ");
        }
        builder.append("(?, ?, ?);");
        var insertOrderProduct = builder.toString();

        try (PreparedStatement orderStmt = connection.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement productStmt = connection.prepareStatement(insertOrderProduct)) {

            connection.setAutoCommit(false);

            orderStmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            orderStmt.setInt(2, iduser);
            orderStmt.executeUpdate();

            // Fetch new order id
            int orderId;
            var keysResultSet = orderStmt.getGeneratedKeys();
            if (keysResultSet.next()) {
                orderId = keysResultSet.getInt(1);
            } else {
                throw new SQLException("Failed to fetch generated order id");
            }

            var actualProducts = new HashMap<Product, Integer>();

            // Correct product amount so we don't go over stock and edit their stock
            // If amount is 0, the product will be removed from order
            // If product is deleted, it will be removed from order
            for (var key : cart.keySet()) {
                var product = DB_Product.get(connection, key, true);
                if (product.getCategory() == Category.Deleted) {
                    continue;
                }

                // Correct stock vs take
                var take = cart.get(key);
                var stock = product.getStock();
                var newVal = Math.min(take, stock);
                if (newVal == 0) {
                    continue;
                }

                // Update stock in database
                var newProduct = new Product(product.getId(), product.getName(), product.getStock() - newVal, product.getPrice(), product.getCategory());
                DB_Product.update(connection, newProduct);

                actualProducts.put(newProduct, newVal);
            }

            if (actualProducts.isEmpty()) {
                connection.rollback();
                connection.setAutoCommit(true);
                return;
            }

            int index = 1;
            for (var product : actualProducts.entrySet()) {
                productStmt.setInt(index, orderId);
                productStmt.setInt(index + 1, product.getKey().getId());
                productStmt.setInt(index + 2, product.getValue());
                index += 3;
            }
            productStmt.executeUpdate();

            connection.commit();
        } catch (SQLException sqe) {
            connection.rollback();
            throw sqe;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public static HashMap<Product, Integer> getProducts(Connection connection, int orderId) throws SQLException {
        connection.setAutoCommit(false);

        HashMap<Product, Integer> result;
        String queryGetProductIds = "SELECT orderproduct.idproduct, amount, name, stock, price, category FROM orderproduct LEFT OUTER JOIN product ON orderproduct.idproduct = product.idproduct WHERE orderproduct.idorder = ?";

        try (PreparedStatement stmt = connection.prepareStatement(queryGetProductIds)) {
            stmt.setInt(1, orderId);
            var resultSet = stmt.executeQuery();
            result = new HashMap<>();
            while (resultSet.next()) {
                var productId = resultSet.getInt("idproduct");
                var amount = resultSet.getInt("amount");
                var name = resultSet.getString("name");
                var stock = resultSet.getInt("stock");
                var price = resultSet.getInt("price");
                var category = Category.valueOf(resultSet.getString("category"));
                var product = new Product(productId, name, stock, price, category);
                result.put(product, amount);
            }
        }
        return result;
    }

    public static void dateMark(Connection connection, String field, int orderId) throws SQLException {
        var dateUpt = "UPDATE webshop.order SET " + field + "=? WHERE idorder=?";
        try (var stmt = connection.prepareStatement(dateUpt)) {
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        }
    }
}
