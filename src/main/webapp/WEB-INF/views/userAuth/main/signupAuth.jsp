<%-- Created by IntelliJ IDEA. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="ko">
<head>
  <title>회원가입 인증</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/authPage.css">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</head>
<body>

<!-- 상단 메뉴 -->
<nav class="navbar navbar-expand-lg navbar-light bg-light">
  <div class="container">
    <a class="navbar-brand" href="#">
      <img src="${pageContext.request.contextPath}/images/자전거.png" alt="따릉이 로고" height="30">
      따릉이 KOSA BIKE
    </a>
    <div class="collapse navbar-collapse">
      <ul class="navbar-nav ms-auto">
        <li class="nav-item"><a class="nav-link" href="#">대여소 안내</a></li>
        <li class="nav-item"><a class="nav-link" href="#">이용권 구매</a></li>
        <li class="nav-item"><a class="nav-link" href="#">문의/FAQ</a></li>
        <li class="nav-item"><a class="nav-link" href="#">공지사항</a></li>
        <li class="nav-item"><a class="nav-link" href="#">안전수칙</a></li>
        <li class="nav-item"><a class="nav-link" href="#">로그인</a></li>
        <li class="nav-item"><a class="nav-link" href="#">회원가입</a></li>
        <li class="nav-item"><a class="nav-link" href="#">이용안내</a></li>
      </ul>
    </div>
  </div>
</nav>

<!-- 메인 콘텐츠 -->
<div class="container text-center my-5">
  <img src="${pageContext.request.contextPath}/images/자전거.png" alt="따릉이 로고" class="logo">
  <h1 class="mt-3">따릉이 KOSA BIKE</h1>
  <button onclick="startPaycoAuth()" class="btn btn-danger mt-4">PASS 간편인증</button>
  <div class="mt-3">
    <a href="/signup/form" class="btn btn-outline-secondary">일반 가입</a>
  </div>
</div>

<!-- 하단 정보 -->
<footer class="bg-light text-center py-4">
  <p>서울특별시 종로구 창경궁로 254 동원빌딩 5층, 우편번호 03077</p>
  <p>COPYRIGHT © 2024 Ddallangi All RIGHTS RESERVED.</p>
  <div class="social-icons">
    <a href="#"><img src="${pageContext.request.contextPath}/images/instagram-icon.png" alt="Instagram" class="social-icon"></a>
    <a href="#"><img src="${pageContext.request.contextPath}/images/facebook-icon.png" alt="Facebook" class="social-icon"></a>
    <a href="#"><img src="${pageContext.request.contextPath}/images/blog-icon.png" alt="Blog" class="social-icon"></a>
    <a href="#"><img src="${pageContext.request.contextPath}/images/github-icon.png" alt="GitHub" class="social-icon"></a>
  </div>
</footer>

<script>
  async function startPaycoAuth() {
    const response = await fetch("/api/oauth2/payco");
    const authUrl = await response.text();
    window.location.href = authUrl;
  }
</script>
</body>
</html>
