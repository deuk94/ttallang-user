<%--
  Created by IntelliJ IDEA.
  User: mhd32
  Date: 24. 11. 2.
  Time: 오후 10:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Title</title>
</head>
<body>
  <button onclick="startPaycoAuth()">페이코 가입</button>
  <a href="/signupForm" type="button">일반 가입</a>
</body>
<script>
    async function startPaycoAuth() {
        const response = await fetch("/api/oauth2/payco");
        const authUrl = await response.text();
        window.location.href = authUrl;
    }
</script>
</html>
