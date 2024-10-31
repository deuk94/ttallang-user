const loginForm = document.querySelector("#loginForm");
const userName = document.querySelector("#username");
const userPassword = document.querySelector("#password");
const errorMessage = document.querySelector("#errorMessage");

loginForm.addEventListener("submit", handleLoginForm);

function handleLoginForm(event) {
    event.preventDefault();
    errorMessage.classList.add("d-none");

    // 아이디 유효성 검사 (영문/숫자 조합 6자 이상).
    const userNameRegex = /^[A-Za-z0-9]{6,}$/;
    if (!userNameRegex.test(userName.value)) {
        userName.classList.add("is-invalid");
    } else {
        userName.classList.remove("is-invalid");
    }

    // 비밀번호 유효성 검사 (영문 대소문자, 숫자, 특수문자 포함 8자 이상).
    const userPasswordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
    if (!userPasswordRegex.test(userPassword.value)) {
        userPassword.classList.add("is-invalid");
    } else {
        userPassword.classList.remove("is-invalid");
    }

    // Spring Security 는 application/x-www-form-urlencoded 타입의 값을 기대하고 있기 때문에 JSON 대신 아래 타입으로 보내야 함.
    const formData = new URLSearchParams();
    formData.append("username", userName.value);
    formData.append("password", userPassword.value);

    // 비공디 처리.
    fetch("/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded" // application/json 으로 보내면 안됨.
        },
        body: formData
    }).then(response => {
        if (!response.ok) {
            throw new Error("로그인 실패.");
        }
        return response.json();
    })
    .then(data => { // 이 data는 response.json()의 실제값.
        console.log(data)
        if (data.role === "admin") {
            window.location.href = "/admin/adminMain";
        } else if (data.role === "user") {
            window.location.href = "/user/userMain";
        } else {
            throw new Error("로그인 실패.");
        }
    })
    .catch(error => {
        errorMessage.textContent = error.message;
        errorMessage.classList.remove("d-none");
    });
}