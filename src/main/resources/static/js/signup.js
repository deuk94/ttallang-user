const userName = document.querySelector("#userName");
const checkExistButton = document.querySelector("#checkExist");
const userPassword = document.querySelector("#userPassword");
const confirmPassword = document.querySelector("#confirmPassword");
const userEmail = document.querySelector("#email");

const form = document.querySelector("#signupForm");

// 폼 처리 관련.
form.addEventListener("submit", handleSignupForm);

// 아이디 중복 검사 버튼.
checkExistButton.addEventListener("click", handleCheckButton);

let exist = false;

function handleCheckButton(){
    const existId = document.querySelector("#existId");
    const notExistId = document.querySelector("#notExistId");
    const data = {
        username: userName.value
    }
    fetch("/api/signupForm/checkExisting", {
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
        if (data.code === 204) { // 가능.
            existId.classList.add("d-none");
            notExistId.classList.remove("d-none");
            exist = true;
        } else if (data.code === 200) { // 불가능.
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
userEmail.addEventListener("input", validateUserEmail);

function handleSignupForm (event) {
    event.preventDefault();

    // 유효한 경우.
    if (validateForm()) {
        const formData = new FormData(form);
        const data = Object.fromEntries(formData);

        fetch("/api/signup", {
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
                window.location.href = "/loginForm";
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
    const complete = validateUserName() && exist && validateUserPassword() && validateConfirmPassword() && validateUserEmail();
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

function validateUserEmail() {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(userEmail.value)) {
        userEmail.classList.add("is-invalid");
        return false;
    } else {
        userEmail.classList.remove("is-invalid");
        return true;
    }
}