const phoneForm = document.querySelector("#phoneForm");
const helpPhoneNumber = document.querySelector("#helpPhoneNumber");
const helpAuthNumber = document.querySelector("#helpAuthNumber");
const customerPhoneInputGroup = document.querySelector("#customerPhoneInputGroup");
const customerPhone = document.querySelector("#customerPhone");
const authInputGroup = document.querySelector("#authInputGroup");
const sendSMS = document.querySelector("#sendSMS");
const checkAuthNumber = document.querySelector("#checkAuthNumber");

customerPhone.addEventListener("input", validateCustomerPhone);
phoneForm.addEventListener("submit", handlePhoneForm);

let isSubmitted = false;

function handlePhoneForm(event) {
    event.preventDefault();

    const formData = new FormData(phoneForm);
    const data = Object.fromEntries(formData);

    if (validateCustomerPhone()) {
        fetch(`/api/find/userName`, {
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

const authForm = document.querySelector("#authForm");

authForm.addEventListener("submit", handleAuthForm);

function handleAuthForm(event) {
    event.preventDefault();

    const formData = new FormData(authForm);
    const data = Object.fromEntries(formData);
    data[customerPhone.name] = customerPhone.value;

    if (isSubmitted) {
        fetch(`/api/find/userName/auth`, {
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
                alert(`회원님의 아이디는 ${result.message} 입니다.`);
                window.location.href = "/login/form";
            })
            .catch(error => {
                alert(error.message);
            });
    } else {
        alert("전송 버튼을 눌러주세요!");
    }
}

function changeForm() {
    sendSMS.classList.add("d-none");
    helpPhoneNumber.classList.add("d-none");
    customerPhoneInputGroup.classList.add("d-none");
    helpAuthNumber.classList.remove("d-none");
    authInputGroup.classList.remove("d-none");
    checkAuthNumber.classList.remove("d-none");
}