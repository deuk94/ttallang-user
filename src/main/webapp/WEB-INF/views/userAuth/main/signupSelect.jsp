<%-- Created by IntelliJ IDEA. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="ko">
<head>
  <title>회원가입 방법 선택</title>
  <link href="${pageContext.request.contextPath}/css/signupSelect.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</head>
<body>

<!-- 상단 메뉴 -->
<nav class="navbar navbar-expand-lg navbar-light bg-light">
  <div class="container">
    <a class="navbar-brand" href="#">
      <img src="${pageContext.request.contextPath}/images/logo.png" alt="딜랑이 로고" height="30">
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

<!-- 회원가입 안내 카드 -->
<div class="container mt-5 d-flex justify-content-center">
  <div class="card text-center p-4" style="width: 600px;">
    <h3>회원가입</h3>
    <p class="info-text">딜랑이는 만 14세 이상(일반회원)만 회원가입이 가능합니다.<br>만 13세는 별도로 문의해 주시기 바랍니다.<br>위의 사항에 동의할 경우 아래 가입하기 버튼을 눌러 진행해 주세요.</p>

    <div class="d-flex justify-content-around">
      <a href="/signup/auth" class="btn btn-custom btn-general">만 14세 이상 회원가입하기</a>
      <a href="/kakaoSignup" class="btn btn-custom btn-kakao">
        <img class="icon" src="${pageContext.request.contextPath}/images/kakaoLogin.png" alt="카카오 아이콘"> 카카오톡으로 회원가입하기
      </a>
      <a href="/naverSignup" class="btn btn-custom btn-naver">
        <img class="icon" src="${pageContext.request.contextPath}/images/naverLogin.png" alt="네이버 아이콘"> 네이버로 회원가입하기
      </a>
    </div>
  </div>
</div>

<!-- 하단 정보 -->
<footer class="bg-light text-center mt-5 py-4">
  <p>서울특별시 종로구 창경궁로 254 동원빌딩 5층, 우편번호 03077</p>
  <p>COPYRIGHT © 2024 Ddallangi All RIGHTS RESERVED.</p>
  <div class="social-icons">
    <a href="#"><img src="${pageContext.request.contextPath}/images/instagram-icon.png" alt="Instagram" class="social-icon"></a>
    <a href="#"><img src="${pageContext.request.contextPath}/images/facebook-icon.png" alt="Facebook" class="social-icon"></a>
    <a href="#"><img src="${pageContext.request.contextPath}/images/blog-icon.png" alt="Blog" class="social-icon"></a>
    <a href="#"><img src="${pageContext.request.contextPath}/images/github-icon.png" alt="GitHub" class="social-icon"></a>
  </div>
</footer>

</body>
</html>
