<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>로그인</title>
  <link href="${pageContext.request.contextPath}/css/loginForm.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</head>
<body>
<!-- 상단 네비게이션 바 -->
<nav class="navbar navbar-expand-lg navbar-light bg-light">
  <div class="container">
    <a class="navbar-brand" href="#">
      <img src="${pageContext.request.contextPath}/images/자전거.png" alt="따릉이 KOSA BIKE" style="width: 50px; vertical-align: middle;">
      따릉이 KOSA BIKE
    </a>
    <div class="navbar-nav ms-auto">
      <a class="nav-link" href="#">대여소 안내</a>
      <a class="nav-link" href="#">이용권 구매</a>
      <a class="nav-link" href="#">문의/FAQ</a>
      <a class="nav-link" href="#">공지사항</a>
      <a class="nav-link" href="#">안전수칙</a>
    </div>
  </div>
</nav>

<!-- 로그인 폼 -->
<div class="login-container">
  <div class="login-title">따릉이 KOSA BIKE</div>
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
    <button type="submit" class="btn btn-primary">로그인</button>
    <a href="${pageContext.request.contextPath}/signup/select" type="button" class="btn btn-primary">회원가입</a>
  </form>
  <div class="text-center mt-3">
    <a href="#">아이디/비밀번호 찾기</a>
    <div class="social-login text-center">
      <button type="button" class="btn btn-outline-secondary">N</button>
      <button type="button" class="btn btn-outline-secondary">K</button>
    </div>
  </div>
</div>

<!-- 하단 푸터 -->
<footer class="footer">
  <div class="container">
    <a href="#">이용약관</a> |
    <a href="#">위치정보관련 약관</a> |
    <a href="#">개인정보처리방침</a> |
    <a href="#">보험안내</a> |
    <a href="#">도움주신 분</a>
    <div class="mt-2">
      <span>서울특별시 중구 항공길 254 통합빌딩 5층, 우편번호 03077</span><br>
      <span>COPYRIGHT © 2024 Ttalangi All RIGHTS RESERVED.</span>
    </div>
    <div class="social-icons mt-2">
      <i class="bi bi-instagram"></i>
      <i class="bi bi-facebook"></i>
      <i class="bi bi-blog"></i>
      <i class="bi bi-youtube"></i>
      <i class="bi bi-github"></i>
    </div>
  </div>
</footer>
</body>
<script src="${pageContext.request.contextPath}/js/login.js"></script>
</html>
