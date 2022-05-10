<%--
Register
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register</title>
</head>
<body>
<h1>Register</h1>
<form action="${pageContext.request.contextPath}/controller?action=register" method="POST">
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
                <input type="submit" value="Register" />
            </td>
        </tr>
    </table>
</form>
</table>
</body>
</html>
