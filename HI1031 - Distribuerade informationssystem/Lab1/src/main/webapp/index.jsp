<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="application.PermissionLevel" %>
<%@ page import="application.User" %>
<%@ page import="application.Product" %>
<%@ page import="application.Category" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="presentation.Controller" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser != null)
        session.setAttribute("products", Controller.getAllProducts(currentUser));
%>
<!DOCTYPE html>
<html>
<head>
    <title>Webshop</title>
</head>
<body>
<h1><%= "Webshop" %>
</h1>
<nav>
    <%
        if (currentUser == null) {
    %>

    <form action="${pageContext.request.contextPath}/controller?action=login" method="POST">
        <table border="1">
            <tr>
                <td>
                    <label>
                        Username
                        <input type="text" name="username"/>
                    </label>
                </td>
                <td>
                    <label>
                        Password
                        <input type="password" name="password"/>
                    </label>
                </td>
                <td>
                    <input type="submit" value="Login"/>
                </td>
            </tr>
        </table>
    </form>
    <span>Please login above or</span>
    <a href="register.jsp">Register</a>
    <%
    } else {
    %>
    <span>Logged in as: <b><%= currentUser.getName() %> (<%= currentUser.getPermissionLevel()%>)</b></span>
    <a href="${pageContext.request.contextPath}/controller?action=logout">Logout</a>
    <%
        }
    %>
    <br>
    <span>Navigation: </span>

    <c:if test="${sessionScope.user.permissionLevel.ordinal() == 0}">
        <a href="${pageContext.request.contextPath}/controller?page=admin">Admin</a>
    </c:if>
    <a href="${pageContext.request.contextPath}/controller?page=orders">Orders</a>

    <br>
    <br>

    <c:if test="${sessionScope.user.permissionLevel.ordinal() != 1}">
        <c:if test="${sessionScope.user != null}">
            <h3>Products</h3>
            <table border="1">
                <thead>
                <th>Category</th>
                <th>Product</th>
                <th>Price</th>
                <th>Stock</th>
                </thead>

                <c:if test="${sessionScope.user.permissionLevel.ordinal() > 1}">
                    <c:forEach items="${sessionScope.products}" var="product">
                        <tr>
                            <td>${product.category.toString()}</td>
                            <td>${product.name}</td>
                            <td>${product.price}</td>
                            <td>${product.stock}</td>

                            <c:if test="${sessionScope.user != null && product.stock > 0}">
                                <td>
                                    <form action="${pageContext.request.contextPath}/controller?action=cartAdd"
                                          method="POST">
                                        <input type="text" style="display:none" name="productId" value="${product.id}">
                                        <input type="submit" value="Add to cart"/>
                                    </form>
                                </td>
                            </c:if>
                            <c:if test="${sessionScope.user != null && product.stock == 0}">
                                <td>
                                    <span style="color: red">Out of stock</span>
                                </td>
                            </c:if>
                        </tr>
                    </c:forEach>
                </c:if>

                <c:if test="${sessionScope.user.permissionLevel.ordinal() < 1}">
                    <c:forEach items="${sessionScope.products}" var="product">
                        <tr>
                            <form action="${pageContext.request.contextPath}/controller?action=productEdit"
                                  method="POST">
                                <input type="text" style="display:none" name="id" value="${product.id}">
                                <td><select name="category">
                                    <c:forEach items="${Category.values()}" var="enumValue">
                                        <option value="${enumValue}"
                                                <c:if test="${product.category == enumValue}">selected</c:if>>${enumValue}</option>
                                    </c:forEach>
                                </select></td>
                                <td><input type="text" name="name" value="${product.name}"></td>
                                <td><input type="text" name="price" value="${product.price}"></td>
                                <td><input type="text" name="stock" value="${product.stock}"></td>
                                <td><input type="submit" value="Save"/></td>
                            </form>
                            <td>
                                <form action="${pageContext.request.contextPath}/controller?action=productDelete"
                                      method="POST">
                                    <input type="text" style="display:none" value="${product.id}">
                                    <input type="submit" value="Delete"/>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <form action="${pageContext.request.contextPath}/controller?action=productCreate" method="POST">
                        <tr>
                            <td><select name="category">
                                <c:forEach items="${Category.values()}" var="enumValue">
                                    <option value="${enumValue}">${enumValue}</option>
                                </c:forEach>
                            </select></td>
                            <td><input type="text" name="name" placeholder="name"></td>
                            <td><input type="text" name="price" placeholder="price"></td>
                            <td><input type="text" name="stock" placeholder="stock"></td>
                            <td><input type="submit" value="Create"/></td>
                        </tr>
                    </form>
                </c:if>
            </table>
        </c:if>
    </c:if>

    <c:if test="${sessionScope.user != null && sessionScope.user.permissionLevel.ordinal() > 1 }">
        <h3>Cart</h3>
        <form action="${pageContext.request.contextPath}/controller?action=cartEmpty" method="POST">
            <input type="submit" value="Empty cart"/>
        </form>
        <form action="${pageContext.request.contextPath}/controller?action=checkout" method="POST">
            <input type="submit" value="Place order"/>
        </form>
        <table border="1">
            <row>
                <th>Category</th>
                <th>Product</th>
                <th>Price</th>
                <th>Amount</th>
                <th>Row total</th>
            </row>

            <c:forEach items="${sessionScope.cart}" var="cartPair">
                <tr>
                    <td>${sessionScope.products.stream()
                            .filter(product -> product.id == cartPair.key)
                            .findFirst()
                            .orElse("").category}</td>
                    <td>${sessionScope.products.stream()
                            .filter(product -> product.id == cartPair.key)
                            .findFirst()
                            .orElse("Refresh site").name}</td>
                    <td>${sessionScope.products.stream()
                            .filter(product -> product.id == cartPair.key)
                            .findFirst()
                            .orElse("").price}</td>
                    <td>${cartPair.value}</td>
                    <td>${cartPair.value * sessionScope.products.stream()
                            .filter(product -> product.id == cartPair.key)
                            .findFirst()
                            .orElse("").price}</td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</nav>


</body>
</html>