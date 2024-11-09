<%--
  Created by IntelliJ IDEA.
  User: mhd32
  Date: 24. 11. 8.
  Time: 오전 1:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="ko">
<head>
  <title>아이디/비밀번호 찾기</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/findSelect">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/signupForm.css">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
</head>
<!-- 상단 네비게이션 바 -->
<jsp:include page="../header/header.jsp" flush="true"/>
<body class="bg-light d-flex align-items-center" style="height: 100vh;">
<div class="container">
  <div class="row justify-content-center">
    <div class="col-lg-6">
      <div class="card shadow p-4">
        <h2 class="text-center mb-4">아이디/비밀번호 찾기</h2>
        <div class="d-grid gap-3">
          <a href="${pageContext.request.contextPath}/find/username" class="btn btn-primary">아이디 찾기</a>
          <a href="${pageContext.request.contextPath}/find/password" class="btn btn-primary">비밀번호 찾기</a>
        </div>
      </div>
    </div>
  </div>
</div>
<!-- 하단 푸터 -->
<jsp:include page="../footer/footer.jsp" flush="true"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>