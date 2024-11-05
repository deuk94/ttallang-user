<%-- Created by IntelliJ IDEA. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="ko">
<head>
	<title>회원가입</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/signupForm.css">
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
</head>
<body class="bg-light d-flex align-items-center">
<div class="container">
	<div class="row justify-content-center">
		<div class="col-lg-6">
			<div class="card shadow p-4">
				<h2 class="text-center mb-4">회원가입</h2>
				<form id="signupForm" novalidate>
					<div class="mb-3 position-relative">
						<label for="userName" class="form-label">아이디</label>
						<div class="input-group">
							<input type="text" id="userName" name="userName" class="form-control" required pattern="^[A-Za-z0-9]{6,}$">
							<button id="checkExist" class="btn btn-outline-secondary ms-2">중복 검사</button>
							<div class="invalid-feedback">
								아이디는 영문/숫자 조합으로 6자 이상이어야 합니다.
							</div>
						</div>
						<div id="existDiv">
							<div id="existId" class="id-exist text-danger d-none mt-1">
								이미 존재하는 아이디입니다.
							</div>
							<div id="notExistId" class="id-not-exist text-success d-none mt-1">
								사용 가능한 아이디입니다.
							</div>
						</div>
					</div>
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
					<div class="mb-3">
						<label for="customerName" class="form-label">이름</label>
						<input
								type="text"
								id="customerName"
								name="customerName"
								class="form-control ${not empty customerName ? 'readonly-input' : ''}"
								value="${not empty customerName ? customerName : ''}" ${not empty customerName ? 'readonly' : ''}
								required
						/>
						<div class="invalid-feedback">
							이름은 필수 항목입니다.
						</div>
					</div>
					<div class="mb-3">
						<label for="customerPhone" class="form-label">휴대폰 번호</label>
						<input
								type="tel"
								id="customerPhone"
								name="customerPhone"
								class="form-control ${not empty customerPhone ? 'readonly-input' : ''}"
								value="${not empty customerPhone ? customerPhone : ''}" ${not empty customerPhone ? 'readonly' : ''}
								pattern="^01[0-9]{8,9}$"
								placeholder="'-'는 제외하고 입력해주세요."
								maxlength="11"
								required
						/>
						<div class="invalid-feedback">
							유효한 휴대폰 번호를 입력해주세요. (ex. 01011112222)
						</div>
					</div>
					<div class="mb-3">
						<label for="email" class="form-label">이메일 주소</label>
						<input
								type="email"
								id="email"
								name="email"
								class="form-control ${not empty email ? 'readonly-input' : ''}"
								value="${not empty email ? email : ''}" ${not empty email ? 'readonly' : ''}
								required
						/>
						<div class="invalid-feedback">
							유효한 이메일 주소를 입력해주세요.
						</div>
					</div>
					<div class="mb-3">
						<label for="birthday" class="form-label">생년월일</label>
						<input
								type="text"
								id="birthday"
								name="birthday"
								class="form-control ${not empty birthday ? 'readonly-input' : ''}"
								value="${not empty birthday ? birthday : ''}" ${not empty birthday ? 'readonly' : ''}
								pattern="^(19|20)\d\d(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])$"
								placeholder="19990101"
								maxlength="8"
								required
						/>
						<div class="invalid-feedback">
							유효한 생년월일을 입력해주세요. (ex. 19990101)
						</div>
					</div>
					<div class="d-grid gap-2">
						<button form="signupForm" type="submit" class="btn btn-primary">가입하기</button>
						<a href="${pageContext.request.contextPath}/login/form" class="btn btn-warning">취소</a>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
<script src="${pageContext.request.contextPath}/js/signup.js"></script>
</body>
</html>
