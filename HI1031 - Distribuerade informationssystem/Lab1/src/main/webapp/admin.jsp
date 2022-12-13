<%--
  Created by IntelliJ IDEA.
  User: hello
  Date: 2021-09-30
  Time: 15:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="application.User" %>
<%@ page import="application.PermissionLevel" %>
<%@ page import="presentation.Controller" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    session.setAttribute("users", Controller.getAllUsers((User) session.getAttribute("user")));
%>
<html>
<head>
    <title>Admin</title>
</head>
<body>
<h1>Admin</h1>
<a href="index.jsp">&lt; Go back</a>
<table>
    <thead>
    <th>Name</th>
    <th>Permission level</th>
    </thead>

    <c:forEach items="${sessionScope.users}" var="user">
        <tr>
            <form action="${pageContext.request.contextPath}/controller?page=admin&action=editUser" method="POST">
                <input type="text" style="display:none" name="userId" value="${user.uid}">
                <td><input type="text" name="name" value="${user.name}"></td>
                <td><select name="permissionLevel">
                    <c:forEach items="${PermissionLevel.values()}" var="enumValue">
                        <option value="${enumValue}" ${enumValue == user.permissionLevel ? 'selected="selected"' : ''}> ${enumValue}</option>
                    </c:forEach>
                </select></td>
                <td><input type="submit" value="Save"/></td>
            </form>
        </tr>
    </c:forEach>
</table>
</body>
</html>
