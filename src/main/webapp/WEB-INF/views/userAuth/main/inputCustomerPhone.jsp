<%--
  Created by IntelliJ IDEA.
  User: mhd32
  Date: 24. 11. 8.
  Time: 오전 3:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="ko">
<head>
  <title>아이디 찾기</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/findSelect">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/signupForm.css">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
</head>
<body class="bg-light d-flex align-items-center" style="height: 100vh;">
<!-- 상단 네비게이션 바 -->
<jsp:include page="../header/header.jsp" flush="true"/>
<div class="container">
  <div class="row justify-content-center">
    <div class="col-lg-6">
      <div class="card shadow p-4">
        <h2 class="text-center mb-4">아이디 찾기</h2>
        <p class="text-center mb-4">회원가입 시 입력했던 휴대폰 번호를 입력해주세요.</p>
        <form id="phoneForm" novalidate>
          <div class="mb-3">
            <label for="customerPhone" class="form-label">휴대폰 번호</label>
            <input
                type="tel"
                id="customerPhone"
                name="customerPhone"
                class="form-control"
                pattern="^01[0-9]{8,9}$"
                placeholder="'-'는 제외하고 입력해주세요."
                maxlength="11"
                required
            />
            <div class="invalid-feedback">
              유효한 휴대폰 번호를 입력해주세요. (ex. 01011112222)
            </div>
          </div>
          <div class="d-grid gap-2">
            <button form="phoneForm" type="submit" class="btn btn-primary">아이디 찾기</button>
            <a href="${pageContext.request.contextPath}/login/form" class="btn btn-warning">취소</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>
<!-- 하단 푸터 -->
<jsp:include page="../footer/footer.jsp" flush="true"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
<script src="${pageContext.request.contextPath}/js/findUsername.js"></script>
</html>