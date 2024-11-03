<%--
  Created by IntelliJ IDEA.
  User: mhd32
  Date: 24. 11. 2.
  Time: 오후 2:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>회원가입 방법 선택</title>
  <link href="css/signupSelect.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</head>
<body>
<a href="/signupAuth" type="button" class="btn btn-success">일반 회원가입</a>
<a href="/kakaoSignup"><img class="kakao-login" src="${pageContext.request.contextPath}/images/kakaoLogin.png"></a>
<a href="/naverSignup"><img class="naver-login" src="${pageContext.request.contextPath}/images/naverLogin.png"></a>
</body>
</html>
