package application;

import database.DB_Order;
import database.DB_Product;
import database.DB_User;
import database.Database;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

/**
 * Communicates with database
 */
public class Model {

    static Database database;

    /**
     * Initializes database connection
     *
     * @return true if successfully initialized
     */
    public static boolean initialize() {
        database = new Database();
        try {
            return database.connect(Database.DefaultDatabase);
        } catch (SQLException se) {
            System.out.println("Failed to connect to database: " + se.getMessage());
            return false;
        }
    }

    /**
     * Shutdown database connection
     *
     * @return true if successfully shutdown
     */
    public static boolean shutdown() {
        try {
            database.disconnect();
        } catch (SQLException | IOException ex) {
            return false;
        }
        return true;
    }

    /**
     * Shutdown database connection
     *
     * @return true if successfully shutdown
     */
    public static User loginUser(String username, String password) {
        User user = null;
        try {
            user = DB_User.login(database.getConnection(), username, password);
        } catch (SQLException sqle) {
            System.out.println("");
        }
        return user;
    }

    public static User registerUser(String username, String password, PermissionLevel permissionLevel) {
        User user = null;
        try {
            user = DB_User.register(database.getConnection(), username, password, permissionLevel);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return user;
    }

    public static List<User> getAllUsers() {
        List<User> users = null;
        try {
            users = DB_User.getAll(database.getConnection());
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return users;
    }

    public static void updateUser(User user) {
        try {
            DB_User.update(database.getConnection(), user);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }

    public static Product getProduct(int productId, boolean includeDeleted) {
        Product product = null;
        try {
            product = DB_Product.get(database.getConnection(), productId, includeDeleted);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return product;
    }

    public static List<Product> getAllProducts(boolean includeDeleted) {
        List<Product> products = null;
        try {
            products = DB_Product.getAll(database.getConnection(), includeDeleted);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return products;
    }

    public static Product addProduct(String name, int stock, int price, Category category) {
        Product product = null;
        try {
            product = DB_Product.add(database.getConnection(), name, stock, price, category);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return product;
    }

    public static void updateProduct(int productId, Product product) {
        try {
            DB_Product.update(database.getConnection(), productId, product);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }

    public static void deleteProduct(int productId) {
        try {
            DB_Product.delete(database.getConnection(), productId);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }

    public static List<Order> getAllOrders() {
        List<Order> result = null;
        try {
            result = DB_Order.getAll(database.getConnection());
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return result;
    }

    public static List<Order> getAllOrders(int userId) {
        List<Order> result = null;
        try {
            result = DB_Order.getAll(database.getConnection(), userId);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return result;
    }

    public static boolean placeOrder(HashMap<Integer, Integer> cart, int userId) {
        try {
            DB_Order.add(database.getConnection(), cart, userId);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
            return false;
        }
        return true;
    }

    public static void packOrder(int orderId) {
        try {
            DB_Order.dateMark(database.getConnection(), "packtime", orderId);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }

    public static void shipOrder(int orderId) {
        try {
            DB_Order.dateMark(database.getConnection(), "shiptime", orderId);
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }

    public static boolean userAtLeast(HttpSession session, PermissionLevel permissionLevel) {
        return userAtLeast((User) session.getAttribute("user"), permissionLevel);
    }

    public static boolean userAtLeast(User user, PermissionLevel permissionLevel) {
        return user != null && user.getPermissionLevel().ordinal() <= permissionLevel.ordinal();
    }

    public static boolean loggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    public static void addToCart(HttpSession session, int productId) {
        HashMap<Integer, Integer> cart = (HashMap<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            cart.put(productId, 1);
        } else {
            if (cart.containsKey(productId)) {
                if (cart.get(productId) < getProduct(productId, true).getStock())
                    cart.put(productId, cart.get(productId) + 1);
            } else {
                cart.put(productId, 1);
            }
        }

        session.setAttribute("cart", cart);
    }

    public static void removeFromCart(HttpSession session, int productId) {
        HashMap<Integer, Integer> cart = (HashMap<Integer, Integer>) session.getAttribute("cart");
        if (cart != null) {
            cart.remove(productId);
        }
    }

}
