<%-- Created by IntelliJ IDEA. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="ko">
<head>
  <title>회원가입 방법 선택</title>
  <link href="${pageContext.request.contextPath}/account/css/signupSelect.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</head>
<body>
<!-- 상단 네비게이션 바 -->
<jsp:include page="../header/header.jsp" flush="true"/>
<!-- 회원가입 안내 카드 -->
<div class="container mt-5">
  <div class="d-flex justify-content-center mb-4">
    <img src="${pageContext.request.contextPath}/images/bicycle.svg" alt="딸랑이" class="logo">
  </div>
  <div class="card text-center p-4">
    <h3 class="mb-3">회원가입</h3>
    <div class="info-text">
      <p class="mb-2">딸랑이는 <span class="over14">만 14세 이상</span>만 회원가입이 가능합니다.</p>
      <p class="mb-2">만 13세는 별도로 문의해 주시기 바랍니다.</p>
      <p class="mb-2">위의 사항에 동의할 경우 아래 가입하기 버튼을 눌러 진행해 주세요.</p>
    </div>
    <div class="d-flex justify-content-center m-0">
      <a href="/signup/form">
        <div class="btn-base btn-normal">
          일반 회원가입
        </div>
      </a>
      <div id="kakaoCert">
        <img class="btn-kakao" src="${pageContext.request.contextPath}/account/images/kakaoLogin.png" alt="카카오 아이콘">
      </div>
      <div id="naverCert">
        <img class="btn-naver" src="${pageContext.request.contextPath}/account/images/naverLogin.png" alt="네이버 아이콘">
      </div>
      <div id="paycoCert" class="btn-base btn-payco">PAYCO 로그인</div>
    </div>
  </div>
</div>
<!-- 하단 푸터 -->
<jsp:include page="../footer/footer.jsp" flush="true"/>
</body>
<script>
  const kakaoCert = document.querySelector("#kakaoCert");
  const naverCert = document.querySelector("#naverCert");
  const paycoCert = document.querySelector("#paycoCert");
  async function cert(uri) {
      const response = await fetch(uri);
      window.location.href = await response.text(); // SNS의 로그인 창을 연다.
  }
  kakaoCert.addEventListener("click",() => {
      cert("/api/oauth2/kakao");
  });
  naverCert.addEventListener("click",() => {
      cert("/api/oauth2/naver");
  });
  paycoCert.addEventListener("click",() => {
      cert("/api/oauth2/payco");
  });
</script>
</html>
