<%--
  Created by IntelliJ IDEA.
  User: KOSA
  Date: 2024-10-30
  Time: 오후 12:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
	<title>Login</title>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</head>
<body>
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
</form>
</body>
<script src="js/login.js"></script>
</html>
