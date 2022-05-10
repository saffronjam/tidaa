package presentation;

import application.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@WebServlet(name = "Controller", value = "/controller")
public class Controller extends HttpServlet {

    @Override
    public void init() throws ServletException {

        System.out.println("INITIALIZE\n\n\n\n\n");

        Model.initialize();
        super.init();
    }

    @Override
    public void destroy() {
        super.destroy();
        Model.shutdown();

        System.out.println("SHUTDOWN");
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doRequest(request, response);
    }

    /**
     * Handles both GET and POST requests
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String page = request.getParameter("page");
        String action = request.getParameter("action");

        if (page == null) {
            page = "";
        }

        switch (page) {
            case "admin": {
                doAdminRequest(action, request, response);
                break;
            }
            case "orders": {
                doOrdersRequest(action, request, response);
                break;
            }
            default: {
                doActionRequest(action, request, response);
                break;
            }
        }


    }

    private void doActionRequest(String action, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();


        if (action == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        switch (action) {
            case "login": {
                User user = login(request.getParameter("username"), request.getParameter("password"));
                if (user != null) {
                    request.getSession().setAttribute("user", user);
                    response.sendRedirect("index.jsp");
                }
                break;
            }
            case "logout": {
                session.setAttribute("user", null);
                session.setAttribute("users", null);
                session.setAttribute("orders", null);
                session.setAttribute("products", null);
                response.sendRedirect("index.jsp");
                break;
            }
            case "register": {
                User user = register(request.getParameter("username"), request.getParameter("password"));
                if (user != null) {
                    request.getSession().setAttribute("user", user);
                    response.sendRedirect("index.jsp");
                }
                break;
            }
            case "checkout": {
                if (Model.loggedIn(session)) {
                    boolean orderSuccess = placeOrder((HashMap<Integer, Integer>) session.getAttribute("cart"), ((User) session.getAttribute("user")).getUid());
                    if (orderSuccess) {
                        session.setAttribute("cart", null);
                    }
                    response.sendRedirect("orders.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
            case "cartAdd": {
                if (Model.loggedIn(session)) {
                    int productId = Integer.parseInt(request.getParameter("productId"));
                    Model.addToCart(session, productId);
                    response.sendRedirect("index.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
            case "cartEmpty": {
                if (Model.loggedIn(session)) {
                    request.getSession().setAttribute("cart", null);
                    response.sendRedirect("index.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
            case "cartRemove": {
                if (Model.loggedIn(session)) {
                    int productId = Integer.parseInt(request.getParameter("productId"));
                    Model.removeFromCart(session, productId);
                } else {
                    response.sendError(403);
                }
                break;
            }
            case "productCreate": {
                if (Model.userAtLeast(request.getSession(), PermissionLevel.Admin)) {
                    int stock = Integer.parseInt(request.getParameter("stock"));
                    int price = Integer.parseInt(request.getParameter("price"));
                    Category category = Category.valueOf(request.getParameter("category"));
                    addProduct(request.getParameter("name"), stock, price, category);
                    response.sendRedirect("index.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
            case "productDelete": {
                if (Model.userAtLeast(request.getSession(), PermissionLevel.Admin)) {
                    int id = Integer.parseInt(request.getParameter("id"));
                    deleteProduct(id);
                    response.sendRedirect("index.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
            case "productEdit": {
                if (Model.userAtLeast(request.getSession(), PermissionLevel.Admin)) {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String name = request.getParameter("name");
                    int stock = Integer.parseInt(request.getParameter("stock"));
                    int price = Integer.parseInt(request.getParameter("price"));
                    Category category = Category.valueOf(request.getParameter("category"));
                    Product product = new Product(id, name, stock, price, category);
                    updateProduct(product);
                    response.sendRedirect("index.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
        }

    }

    private void doAdminRequest(String action, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Model.userAtLeast(request.getSession(), PermissionLevel.Admin)) {
            response.sendError(401);
            return;
        }

        if (action == null) {
            response.sendRedirect("admin.jsp");
            return;
        }

        HttpSession session = request.getSession();

        switch (action) {
            case "editUser": {
                if (Model.userAtLeast(session, PermissionLevel.Admin)) {
                    int userId = Integer.parseInt(request.getParameter("userId"));
                    String name = request.getParameter("name");
                    PermissionLevel permissionLevel = PermissionLevel.valueOf(request.getParameter("permissionLevel"));
                    Model.updateUser(new User(userId, name, permissionLevel));
                    response.sendRedirect("admin.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
        }
    }

    private void doOrdersRequest(String action, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Model.loggedIn(request.getSession())) {
            response.sendError(401);
            return;
        }

        HttpSession session = request.getSession();

        if (Model.userAtLeast(session, PermissionLevel.Worker)) {
            List<Order> orders = Model.getAllOrders();
            session.setAttribute("orders", orders);
        } else {
            User user = (User) session.getAttribute("user");
            List<Order> orders = Model.getAllOrders(user.getUid());
            session.setAttribute("orders", orders);
        }

        if (action == null) {
            response.sendRedirect("orders.jsp");
            return;
        }

        switch (action) {
            case "orderPack": {
                if (Model.userAtLeast(request.getSession(), PermissionLevel.Worker)) {
                    pack(Integer.parseInt(request.getParameter("orderId")));
                    response.sendRedirect("orders.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }
            case "orderShip": {
                if (Model.userAtLeast(request.getSession(), PermissionLevel.Worker)) {
                    ship(Integer.parseInt(request.getParameter("orderId")));
                    response.sendRedirect("orders.jsp");
                } else {
                    response.sendError(403);
                }
                break;
            }

        }
    }

    static public User login(String username, String password) {
        return Model.loginUser(username, password);
    }

    static public User register(String username, String password) {
        return Model.registerUser(username, password, PermissionLevel.Customer);
    }

    static public Product getProduct(int productId) {
        return Model.getProduct(productId, true);
    }

    static public List<Product> getAllProducts(User user) {
        if (user.getPermissionLevel().ordinal() <= PermissionLevel.Worker.ordinal()) {
            return Model.getAllProducts(true);
        } else {
            return Model.getAllProducts(false);
        }
    }

    static public Product addProduct(String name, int stock, int price, Category category) {
        return Model.addProduct(name, stock, price, category);
    }

    static public void updateProduct(Product product) {
        updateProduct(product.getId(), product);
    }

    static public void updateProduct(int productId, Product product) {
        Model.updateProduct(productId, product);
    }

    static public void deleteProduct(int productId) {
        Model.deleteProduct(productId);
    }

    static public boolean placeOrder(HashMap<Integer, Integer> cart, int uid) {
        return Model.placeOrder(cart, uid);
    }

    static public List<Order> getAllOrders(User user) {
        if (user.getPermissionLevel().ordinal() <= PermissionLevel.Worker.ordinal()) {
            return Model.getAllOrders();
        } else {
            return Model.getAllOrders(user.getUid());
        }
    }

    static public List<User> getAllUsers(User user) {
        if (user.getPermissionLevel().ordinal() <= PermissionLevel.Worker.ordinal()) {
            return Model.getAllUsers();
        } else {
            return new ArrayList<>();
        }
    }

    static public void pack(int orderId) {
        Model.packOrder(orderId);
    }

    static public void ship(int orderId) {
        Model.shipOrder(orderId);
    }

}
