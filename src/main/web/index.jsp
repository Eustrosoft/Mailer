<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Mailer</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<%
    long enter_time = System.currentTimeMillis();
%>
<div class="mainContainer">
    <div class="containerHeader">
        <h1>Mailer</h1>
    </div>
    There is no content!
    <hr>
    <i>timing : <%= ((System.currentTimeMillis() - enter_time) + " ms") %>
    </i>
    <br> Your web-server is <%= application.getServerInfo() %><br>
</div>
</body>
</html>