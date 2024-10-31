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
	<title>Signup</title>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</head>
<body>
<form id="signupForm">
	<div class="mb-3">
		<label for="userName" class="form-label">아이디</label>
		<input type="text" id="userName" name="userName" class="form-control" required>
		<button id="checkExist">중복 검사</button>
		<div class="invalid-feedback">
			아이디는 영문/숫자 조합으로 6자 이상이어야 합니다.
		</div>
		<div id="existId" class="id-exist d-none">
			이미 존재하는 아이디입니다.
		</div>
		<div id="notExistId" class="id-not-exist d-none">
			사용 가능한 아이디입니다.
		</div>
	</div>
	<div class="mb-3">
		<label for="userPassword" class="form-label">비밀번호</label>
		<input type="password" id="userPassword" name="userPassword" class="form-control" required>
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
	<div class="mb-3">
		<label for="customerName" class="form-label">이름</label>
		<input type="text" id="customerName" name="customerName" class="form-control" required>
		<div class="invalid-feedback">
			이름은 필수 항목입니다.
		</div>
	</div>
	<div class="mb-3">
		<label for="email" class="form-label">이메일 주소</label>
		<input type="email" id="email" name="email" class="form-control" required>
		<div class="invalid-feedback">
			유효한 이메일 주소를 입력해주세요.
		</div>
	</div>
	<button type="submit" class="btn btn-primary">가입하기</button>
	<button type="reset" class="btn btn-danger">초기화</button>
	<a href="/intro" type="button" class="btn btn-warning">취소</a>
</form>
</body>
<script src="js/signup.js"></script>
</html>
