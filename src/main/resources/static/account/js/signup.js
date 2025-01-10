import { phoneAuthComplete } from './signupPhoneAuth.js';

const userName = document.querySelector("#userName");
const checkExistButton = document.querySelector("#checkExist");
const phoneAuthButton = document.querySelector("#phoneAuth");
const userPassword = document.querySelector("#userPassword");
const confirmPassword = document.querySelector("#confirmPassword");
const customerPhone = document.querySelector("#customerPhone");
const userEmail = document.querySelector("#email");
const userBirthday = document.querySelector("#birthday");
const existId = document.querySelector("#existId");
const notExistId = document.querySelector("#notExistId");
const existDiv = document.querySelector("#existDiv");

const form = document.querySelector("#signupForm");

let isExist = true;

// 폼 처리 관련.
form.addEventListener("submit", handleSignupForm);

// 아이디 중복 검사 버튼.
checkExistButton.addEventListener("click", handleCheckButton);

// 휴대폰 번호 인증 버튼.
phoneAuthButton.addEventListener("click", handlePhoneAuthButton);

function handlePhoneAuthButton(event) {
    event.preventDefault();

}

// 중복검사 버튼.
function handleCheckButton(event) {
    event.preventDefault();

    if (validateUserName(event)) {
        fetch(`/api/signup/form/checkExisting/${userName.value}`, {
            method: "GET",
        })
        .then(async response => {
            if (!response.ok) {
                const result = await response.json();
                throw new Error(result.message);
            }
            return response.json();
        })
        .then(result => {
            if (result.message === "가입 가능한 ID.") { // 아이디 사용 가능.
                existId.classList.add("d-none");
                notExistId.classList.remove("d-none");
                isExist = false;
            } else if (result.message === "이미 존재하는 ID.") { // 아이디 사용 불가능.
                notExistId.classList.add("d-none");
                existId.classList.remove("d-none");
                isExist = true;
            }
        })
        .catch(error => {
            alert(error.message);
            isExist = true;
        });
    } else {
        alert("형식에 맞게 입력해주세요!");
    }
}

// 유효성 검사 이벤트 등록.
userName.addEventListener("input", (event) => {
    validateUserName(event);
});
userPassword.addEventListener("input", validateUserPassword);
confirmPassword.addEventListener("input", validateConfirmPassword);
userEmail.addEventListener("input", validateEmail);
customerPhone.addEventListener("input", validateCustomerPhone);
userBirthday.addEventListener("input", validateBirthday);

function handleSignupForm(event) {
    event.preventDefault();

    // 폼 유효성 확인.
    if (validateForm(event)) {
        const formData = new FormData(form);
        const data = Object.fromEntries(formData);

        fetch("/api/signup", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        })
        .then(async response => {
            if (!response.ok) {
                const result = await response.json();
                throw new Error(result.message);
            }
            return response.json();
        })
        .then(result => {
            alert(result.message);
            window.location.href = "/login/form";
        })
        .catch(error => {
            alert(error.message);
        });
    }
}

function validateUserName(event) {
    // 이벤트가 있을 때 마다 중복검사 결과를 초기화 해야함.
    // 단, 폼 제출 이벤트가 아닌 경우에만.
    if (event !== undefined && event.type !== "submit") {
        isExist = true;
        existDiv.classList.add("d-none")
        if (!existId.classList.contains("d-none")) {
            existId.classList.add("d-none");
        }
        if (!notExistId.classList.contains("d-none")) {
            notExistId.classList.add("d-none");
        }
    }
    // 폼 제출이던 아니던 어떤 경우라도 유효성 검사는 진행함.
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

// 유효성 검사 조각 : 패스워드
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

// 유효성 검사 조각 : 패스워드 확인
function validateConfirmPassword() {
    if (userPassword.value !== confirmPassword.value) {
        confirmPassword.classList.add("is-invalid");
        return false;
    } else {
        confirmPassword.classList.remove("is-invalid");
        return true;
    }
}

// 유효성 검사 조각 : 이메일
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

// 유효성 검사 조각 : 생년월일
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

// 유효성 검사 조각 : 휴대폰번호
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

// 유효성 검사 모음.
function validateForm(event) {
    const complete =
        !isExist &&
        validateUserName(event) &&
        validateUserPassword() &&
        validateConfirmPassword() &&
        validateEmail() &&
        validateBirthday() &&
        validateCustomerPhone() &&
        phoneAuthComplete;
    if (complete) {
        return true;
    } else {
        if (isExist) {
            alert("아이디 중복 검사를 해주세요!");
        } else if (!phoneAuthComplete) {
            alert("휴대폰 인증을 완료해주세요!");
        } else {
            alert("올바른 정보를 입력해주세요!");
        }
    }
    return false;
}