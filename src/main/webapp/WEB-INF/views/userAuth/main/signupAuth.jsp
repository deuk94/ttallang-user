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
<!-- 상단 네비게이션 바 -->
<jsp:include page="../header/header.jsp" flush="true"/>
<!-- 메인 콘텐츠 -->
<div class="container text-center my-5">
  <img src="${pageContext.request.contextPath}/images/자전거.png" alt="따릉이 로고" class="logo">
  <h1 class="mt-3">따릉이 KOSA BIKE</h1>
  <button id="paycoCert" class="btn btn-danger mt-4">PAYCO 간편인증</button>
  <div class="mt-3">
    <a href="/signup/form" class="btn btn-outline-secondary">일반 가입</a>
  </div>
</div>
<!-- 하단 푸터 -->
<jsp:include page="../footer/footer.jsp" flush="true"/>
<script>
  const paycoCert = document.querySelector("#paycoCert");
  paycoCert.addEventListener("click", async () => {
    const response = await fetch("/api/oauth2/payco");
    window.location.href = await response.text();
  });
</script>
</body>
</html>
