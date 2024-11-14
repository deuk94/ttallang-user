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
  <title>패스워드 찾기</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/account/css/findPassword.css">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
</head>
<body>
<!-- 상단 네비게이션 바 -->
<jsp:include page="../header/header.jsp" flush="true"/>
<div class="container">
  <div class="d-flex justify-content-center">
    <div class="card p-3 input-card">
      <h2 class="text-center my-4">패스워드 찾기</h2>
      <p id="helpText" class="mt-2 mb-4">회원가입 시 입력했던 <span>아이디</span>와 <span>휴대폰 번호</span>를 입력해주세요.</p>
      <p id="helpAuthNumberText" class="mb-2 d-none">인증 번호를 입력 후 <span>확인</span> 버튼을 눌러주세요.</p>
      <form id="userNameCustomerPhoneForm" class="m-0" novalidate>
        <div id="userNameInputGroup" class="mb-3">
          <label for="userName" class="form-label">아이디</label>
          <input
              type="text"
              id="userName"
              name="userName"
              class="form-control"
              pattern="^[A-Za-z0-9]{6,}$"
              placeholder="영문/숫자 조합 6자 이상."
              required
          />
          <div class="invalid-feedback">
            아이디는 영문/숫자 조합으로 6자 이상이어야 합니다.
          </div>
        </div>
        <div id="customerPhoneInputGroup" class="mb-3">
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
      </form>
      <form id="authForm" class="m-0" novalidate>
        <div id="authInputGroup" class="input-group mb-3 d-none">
          <label for="authNumber"></label>
          <input type="text" id="authNumber" name="authNumber" class="form-control" maxlength="4" pattern="\d{4}" required>
        </div>
      </form>
      <div class="d-flex justify-content-center">
        <button id="sendSMS" form="userNameCustomerPhoneForm" type="submit" class="btn btn-outline-secondary me-2">전송</button>
        <button id="checkAuthNumber" form="authForm" type="submit" class="btn btn-outline-secondary me-2 d-none">확인</button>
        <a href="${pageContext.request.contextPath}/login/form" class="btn-cancel">취소</a>
      </div>
    </div>
  </div>
</div>
<div class="container">
  <!-- 하단 푸터 -->
  <jsp:include page="../footer/footer.jsp" flush="true"/>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
<script src="${pageContext.request.contextPath}/account/js/findPassword.js"></script>
</html>