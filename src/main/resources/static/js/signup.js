const userName = document.querySelector("#userName");
const checkExistButton = document.querySelector("#checkExist");
const userPassword = document.querySelector("#userPassword");
const confirmPassword = document.querySelector("#confirmPassword");
const customerPhone = document.querySelector("#customerPhone");
const userEmail = document.querySelector("#email");
const userBirthday = document.querySelector("#birthday");
const existId = document.querySelector("#existId");
const notExistId = document.querySelector("#notExistId");
const existDiv = document.querySelector("#existDiv");

const form = document.querySelector("#signupForm");

// 폼 처리 관련
form.addEventListener("submit", handleSignupForm);

// 아이디 중복 검사 버튼
checkExistButton.addEventListener("click", handleCheckButton);

let isExist = true;

// 중복검사 버튼.
function handleCheckButton(event) {
    event.preventDefault();

    if (validateUserName()) {
        fetch(`/api/signup/form/checkExisting/${userName.value}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            }
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("확인 실패.");
            }
        })
        .then(data => {
            if (data.code === 204) { // 아이디 사용 가능.
                existId.classList.add("d-none");
                notExistId.classList.remove("d-none");
                isExist = false;
            } else if (data.code === 200) { // 아이디 사용 불가능.
                notExistId.classList.add("d-none");
                existId.classList.remove("d-none");
                isExist = true;
            }
        })
        .catch(error => {
            console.error('Error:', error);
            isExist = true;
        });
    } else {
        alert("형식에 맞게 입력해주세요!");
    }
}

// 유효성 검사 이벤트 등록
userName.addEventListener("input", validateUserName);
userPassword.addEventListener("input", validateUserPassword);
confirmPassword.addEventListener("input", validateConfirmPassword);
userEmail.addEventListener("input", validateEmail);
customerPhone.addEventListener("input", validateCustomerPhone);
userBirthday.addEventListener("input", validateBirthday);

function handleSignupForm(event) {
    event.preventDefault();

    // 폼 유효성 확인
    if (validateForm()) {
        const formData = new FormData(form);
        console.log(formData);
        const data = Object.fromEntries(formData);

        fetch("/api/signup", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("회원가입 실패.");
            }
        })
        .then(data => {
            if (data.status === "success") {
                alert("회원가입 성공.");
                window.location.href = "/login/form";
            } else {
                console.log(data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
    }
}

function validateForm() {
    const complete =
        !isExist &&
        validateUserName() &&
        validateUserPassword() &&
        validateConfirmPassword() &&
        validateEmail() &&
        validateBirthday() &&
        validateCustomerPhone();
    if (complete) {
        return true;
    } else {
        if (isExist) {
            alert("아이디 중복 검사를 해주세요!");
        } else {
            alert("올바른 정보를 입력해주세요!");
        }
    }
    return false;
}

function validateUserName() {
    // 인풋이 새로 있을 때 마다 중복검사 결과를 초기화 해야함.
    isExist = true;
    existDiv.classList.add("d-none")
    if (!existId.classList.contains("d-none")) {
        existId.classList.add("d-none");
    }
    if (!notExistId.classList.contains("d-none")) {
        notExistId.classList.add("d-none");
    }
    const userNameRegex = /^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{6,}$/;
    if (!userNameRegex.test(userName.value)) {
        userName.classList.add("is-invalid");
        return false;
    } else {
        userName.classList.remove("is-invalid");
        existDiv.classList.remove("d-none")
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

function validateEmail() {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(userEmail.value)) {
        userEmail.classList.add("is-invalid");
        return false;
    } else {
        userEmail.classList.remove("is-invalid");
        return true;
    }
}


function validateBirthday() {
    const birthdayRegex = /^(19|20)\d\d(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])$/;
    if (!birthdayRegex.test(userBirthday.value)) {
        userBirthday.classList.add("is-invalid");
        return false;
    } else {
        userBirthday.classList.remove("is-invalid");
        return true;
    }
}

function validateCustomerPhone() {
    const customerPhoneRegex = /^01[0-9]{8,9}$/;
    if (!customerPhoneRegex.test(customerPhone.value)) {
        customerPhone.classList.add("is-invalid");
        return false;
    } else {
        customerPhone.classList.remove("is-invalid");
        return true;
    }
}
