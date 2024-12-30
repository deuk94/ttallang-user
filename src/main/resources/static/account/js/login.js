const loginForm = document.querySelector("#loginForm");
const userName = document.querySelector("#username");
const userPassword = document.querySelector("#password");
const errorMessage = document.querySelector("#errorMessage");

loginForm.addEventListener("submit", handleLoginForm);

function handleLoginForm(event) {
    event.preventDefault();

    if (checkValidation()) {
        errorMessage.classList.add("d-none");

        // Spring Security 는 application/x-www-form-urlencoded 타입의 값을 기대하고 있기 때문에 JSON 대신 아래 타입으로 보내야 함.
        const formData = new URLSearchParams();
        formData.append("username", userName.value);
        formData.append("password", userPassword.value);

        // 비동기 처리.
        fetch("/api/login", {
            credentials: "include",
            method: "POST",
            headers: {
                // application/json 으로 보내면 안됨.
                // 시큐리티에서 설정된 경로로 걸러지는 요청에 대해서 json 타입으로 하면 서버 측에서 못알아먹는다고 한다.
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: formData
        }).then(async response => {
            if (!response.ok) {
                if (response.status === 402) {
                    alert("결제를 먼저 진행해주세요!!!");
                    window.location.href = "/pay/payment";
                }
                const result = await response.json();
                throw new Error(result.message);
            }
            return response.json();
        })
            .then(result => { // 이 data는 response.json()의 실제값.
                window.location.href = "/main";
            })
            .catch(error => {
                errorMessage.textContent = error.message;
                errorMessage.classList.remove("d-none");
            });
    }
}

function checkValidation() {
    // 아이디 유효성 검사 (영문/숫자 조합 6자 이상).
    let isValidateId = false;
    let isValidatePw = false;
    const userNameRegex = /^[A-Za-z0-9]{6,}$/;
    if (!userNameRegex.test(userName.value)) {
        userName.classList.add("is-invalid");
        isValidateId = false;
    } else {
        userName.classList.remove("is-invalid");
        isValidateId = true;
    }

    // 비밀번호 유효성 검사 (영문 대소문자, 숫자, 특수문자 포함 8자 이상).
    const userPasswordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
    if (!userPasswordRegex.test(userPassword.value)) {
        userPassword.classList.add("is-invalid");
        isValidatePw = false;
    } else {
        userPassword.classList.remove("is-invalid");
        isValidatePw = true;
    }
    return isValidateId && isValidatePw;
}

// URL에 "error" 파라미터가 존재하는지 확인하여 alert 표시.
// 이것은 외부로부터 에러가 발생했을 때 로그인창으로 오면서 에러메세지를 전달하기 위함임.
function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

const alertMessage = getQueryParam("error");
const cancelMessage = getQueryParam("cancel");
if (alertMessage) {
    alert(alertMessage);
    window.location.href = "/login/form";
}
if (cancelMessage) {
    alert(cancelMessage);
    window.location.href = "/login/form";
}