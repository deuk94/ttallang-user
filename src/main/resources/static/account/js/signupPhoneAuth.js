const customerPhone = document.querySelector("#customerPhone");
const phoneAuthButton = document.querySelector("#phoneAuth");
const helpAuthNumber = document.querySelector("#helpAuthNumber");
const authNumber = document.querySelector("#authNumber");
const checkAuthNumberButton = document.querySelector("#checkAuthNumberButton");
let isSubmitted = false;

export let phoneAuthComplete = false; // 메인 signup.js 에서 써야 함.
phoneAuthButton.addEventListener("click", handleStartAuth);

// 버튼 누르면 인증번호 칸 생기게 하는 함수.
function changeForm() {
    helpAuthNumber.classList.remove("d-none");
    authNumber.classList.remove("d-none");
    checkAuthNumberButton.classList.remove("d-none");
    phoneAuthButton.remove(); // 인증번호 받기 버튼 없애기.
    customerPhone.classList.add("readonly-input"); // 휴대폰번호 입력칸 비활성화 처리.
    customerPhone.readOnly = true;
}

// 유효성 검사 : 휴대폰번호
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

// 휴대폰 인증 버튼 클릭했을 때 인증번호 요청하는 함수.
function handleStartAuth(event) {
    event.preventDefault();

    if (validateCustomerPhone()) {
        fetch(`/api/phoneAuth`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                "customerPhone": customerPhone.value,
                "authNumber": authNumber.value,
            }),
        })
            .then(async response => {
                if (!response.ok) {
                    const result = await response.json();
                    throw new Error(result.message);
                }
                return response.json();
            })
            .then(() => {
                // 전송 성공.
                isSubmitted = true;
                // 인증번호 입력란 보여주기.
                changeForm();
            })
            .catch(error => {
                if (error.message === "이미 인증이 진행중입니다.\n인증 번호를 다시 확인해주세요.") {
                    // 이미 전송함.
                    isSubmitted = true;
                    changeForm();
                }
                alert(error.message);
            });
    } else {
        alert("형식에 맞게 입력해주세요!");
    }
}

checkAuthNumberButton.addEventListener("click", handleCheckAuthNumber);

function handleCheckAuthNumber(event) {
    event.preventDefault();

    if (isSubmitted) {
        fetch(`/api/phoneAuth/result`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                "customerPhone": customerPhone.value,
                "authNumber": authNumber.value,
            }),
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
                authNumber.disabled = true; // 인증번호 입력칸 비활성화 처리.
                checkAuthNumberButton.remove(); // 확인버튼 없애기.
                phoneAuthComplete = true;
            })
            .catch(error => {
                alert(error.message);
            });
    } else {
        alert("전송 버튼을 눌러주세요!");
    }
}