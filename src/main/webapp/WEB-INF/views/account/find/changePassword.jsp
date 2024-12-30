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
  <title>패스워드 변경</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/account/css/changePassword.css">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
</head>
<body>
<!-- 상단 네비게이션 바 -->
<jsp:include page="../header/header.jsp" flush="true"/>
<div class="container">
  <div class="d-flex justify-content-center">
    <div class="card p-3 input-card">
      <h2 class="text-center my-4">패스워드 변경</h2>
      <p id="helpText" class="mt-2 mb-4">비밀번호를 새롭게 설정해주세요.</p>
      <form id="changePasswordForm" class="m-0" novalidate>
        <input type="hidden" id="userName" name="userName" value="${userName}">
        <input type="hidden" id="state" name="state" value="${state}">
        <div class="mb-3">
          <label for="userPassword" class="form-label">비밀번호</label>
          <input type="password" id="userPassword" name="userPassword" class="form-control" required pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$">
          <div class="invalid-feedback">
            비밀번호는 영문 대소문자, 숫자, 특수문자를 포함하여 8자 이상이어야 합니다.
          </div>
        </div>
        <div class="mb-3">
          <label for="confirmPassword" class="form-label">비밀번호 확인</label>
          <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required>
          <div class="invalid-feedback">
            비밀번호가 일치하지 않습니다.
          </div>
        </div>
      </form>
      <div class="d-flex justify-content-center m-0">
        <button id="formSubmit" form="changePasswordForm" type="submit" class="btn-submit me-3">변경</button>
        <a id="formSubmitCancel" href="${pageContext.request.contextPath}/login/form" class="btn-cancel">취소</a>
      </div>
    </div>
  </div>
</div>
<div class="container">
  <!-- 하단 푸터 -->
  <jsp:include page="../footer/footer.jsp" flush="true"/>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
<script src="${pageContext.request.contextPath}/account/js/changePassword.js"></script>
</html>