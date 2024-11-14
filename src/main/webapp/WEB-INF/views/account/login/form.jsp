<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>로그인</title>
  <link href="${pageContext.request.contextPath}/account/css/loginForm.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</head>
<body>
<!-- 상단 네비게이션 바 -->
<jsp:include page="../header/header.jsp" flush="true"/>
<!-- 로그인 폼 -->
<div class="login-container">
  <div class="login-title">딸랑이 KOSA BIKE</div>
  <form id="loginForm">
    <div class="mb-3">
      <label for="username" class="form-label">아이디</label>
      <input type="text" id="username" name="username" class="form-control" required>
      <div class="invalid-feedback">
        아이디는 영문/숫자 조합으로 6자 이상이어야 합니다.
      </div>
    </div>
    <div class="mb-3">
      <label for="password" class="form-label">비밀번호</label>
      <input type="password" id="password" name="password" class="form-control" required>
      <div class="invalid-feedback">
        비밀번호는 영문 대소문자, 숫자, 특수문자를 포함하여 8자 이상이어야 합니다.
      </div>
    </div>
    <div id="errorMessage" class="alert alert-danger d-none" role="alert"></div>
    <button type="submit" class="btn-login">로그인</button>
    <a href="${pageContext.request.contextPath}/signup/select" type="button" class="btn-signup">회원가입</a>
  </form>
  <div class="find-auth text-center mt-3">
    <a href="${pageContext.request.contextPath}/find/select">아이디/비밀번호 찾기</a>
  </div>
</div>
<!-- 하단 푸터 -->
<jsp:include page="../footer/footer.jsp" flush="true"/>
</body>
<script src="${pageContext.request.contextPath}/account/js/login.js"></script>
</html>
