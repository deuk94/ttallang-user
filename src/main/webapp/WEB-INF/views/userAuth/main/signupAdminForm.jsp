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
	<title>관리자 회원가입</title>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</head>
<body>
<form id="signupAdminForm">
	<div class="mb-3">
		<label for="userName" class="form-label">아이디</label>
		<input type="text" id="userName" name="userName" class="form-control" required>
		<button type="button" id="checkExist">중복 검사</button>
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
	<button type="submit" class="btn btn-primary">가입하기</button>
	<a href="${pageContext.request.contextPath}/login/form" type="button" class="btn btn-warning">취소</a>
</form>
</body>
<script>
    const userName = document.querySelector("#userName");
    const checkExistButton = document.querySelector("#checkExist");
    const userPassword = document.querySelector("#userPassword");
    const confirmPassword = document.querySelector("#confirmPassword");

    const form = document.querySelector("#signupAdminForm");

    // 폼 처리 관련.
    form.addEventListener("submit", handleSignupAdminForm);

    // 아이디 중복 검사 버튼.
    checkExistButton.addEventListener("click", handleCheckButton);

    let exist = false;

    function handleCheckButton(){
        const existId = document.querySelector("#existId");
        const notExistId = document.querySelector("#notExistId");
        const data = {
            username: userName.value
        }
        fetch("/api/signup/form/checkExisting", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        }).then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("확인 실패.");
            }
        }).then(data => {
            if (data.code === 204) { // 가능 (204는 성공적으로 실행됐는데 반환된 값이 없는 경우.)
                existId.classList.add("d-none");
                notExistId.classList.remove("d-none");
                exist = true;
            } else if (data.code === 200) { // 불가능. (성공적으로 실행되었고 반환값도 있음.)
                notExistId.classList.add("d-none");
                existId.classList.remove("d-none");
                exist = false;
            }
        }).catch((error) => {
            console.error('Error:', error);
            exist = false;
        });
    }

    // 유효성 검사 관련.
    userName.addEventListener("input", validateUserName);
    userPassword.addEventListener("input", validateUserPassword);
    confirmPassword.addEventListener("input", validateConfirmPassword);

    function handleSignupAdminForm (event) {
        event.preventDefault();

        // 유효한 경우.
        if (validateForm()) {
            const formData = new FormData(form);
            const data = Object.fromEntries(formData);

            fetch("/api/signup/admin", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(data)
            }).then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error("회원가입 실패.");
                }
            })
                .then(data => {
                    if (data.status === "success") {
                        console.log(data.status);
                        alert("회원가입 성공.")
                        window.location.href = "/login/form";
                    } else {
                        console.log(data.status)
                        console.log(data.message);
                    }
                })
                .catch((error) => {
                    console.error('Error:', error);
                });
        }
    }

    function validateForm() {
        const complete = validateUserName() && exist && validateUserPassword() && validateConfirmPassword();
        if (complete) {
            return true;
        }
        alert("아이디 중복 검사를 해주세요!");
        return false;
    }

    function validateUserName() {
        const userNameRegex = /^[A-Za-z0-9]{6,}$/;
        if (!userNameRegex.test(userName.value)) {
            userName.classList.add("is-invalid");
            return false;
        } else {
            userName.classList.remove("is-invalid");
            return true;
        }
    }

    function validateUserPassword() {
        const userPasswordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
        if (!userPasswordRegex.test(userPassword.value)) {
            userPassword.classList.add("is-invalid");
            return false;
        } else {
            userPassword.classList.remove("is-invalid");
            return true;
        }
    }

    function validateConfirmPassword() {
        if (userPassword.value !== confirmPassword.value) {
            confirmPassword.classList.add("is-invalid");
            return false;
        } else {
            confirmPassword.classList.remove("is-invalid");
            return true;
        }
    }
</script>
</html>
