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
    fetch("/api/login", {
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
            window.location.href = "/admin/branch/main"; // 어드민과 통신하는 부분.
        } else if (data.role === "user") {
            // window.location.href = "/user/main";
            if (data.code === 400) {
                alert("결제를 먼저 진행해주세요!!!");
                window.location.href = "/pay/payment";
            } else {
                window.location.href = "../main"; // -> /user/** 형식으로 바꿔야 권한 적용 가능함.
            }
        } else {
            alert(data.message);
            throw new Error("로그인 실패 (권한 정보를 확인할 수 없습니다.)");
        }
    })
    .catch(error => {
        errorMessage.textContent = error.message;
        errorMessage.classList.remove("d-none");
    });
}

// "error" 파라미터가 존재하는지 확인하여 alert 표시.
function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

const alertMessage = getQueryParam("error");
const cancelMessage = getQueryParam("cancel");
if (alertMessage) {
    alert(alertMessage);
}
if (cancelMessage) {
    alert(cancelMessage);
}