const userNameCustomerPhoneForm = document.querySelector("#userNameCustomerPhoneForm");

const helpText = document.querySelector("#helpText");
const helpAuthNumberText = document.querySelector("#helpAuthNumberText");

const userName = document.querySelector("#userName");
const customerPhone = document.querySelector("#customerPhone");

const authInputGroup = document.querySelector("#authInputGroup");

const sendSMS = document.querySelector("#sendSMS");
const checkAuthNumber = document.querySelector("#checkAuthNumber");

userName.addEventListener("input", validateUserName);
customerPhone.addEventListener("input", validateCustomerPhone);
userNameCustomerPhoneForm.addEventListener("submit", handleUserNameCustomerPhoneForm);

let isSubmitted = false;

function handleUserNameCustomerPhoneForm(event) {
    event.preventDefault();

    const formData = new FormData(userNameCustomerPhoneForm);
    const data = Object.fromEntries(formData);

    if (validateUserName() && validateCustomerPhone()) {
        fetch(`/api/find/password`, {
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
                if (error.message === "이미 인증이 진행중입니다.") {
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

function validateUserName() {
    const userNameRegex = /^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{6,}$/;
    if (!userNameRegex.test(userName.value)) {
        userName.classList.add("is-invalid");
        return false;
    } else {
        userName.classList.remove("is-invalid");
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

const authForm = document.querySelector("#authForm");

authForm.addEventListener("submit", handleAuthForm);

function handleAuthForm(event) {
    event.preventDefault();

    const formData = new FormData(authForm);
    const data = Object.fromEntries(formData);
    data[customerPhone.name] = customerPhone.value;

    if (isSubmitted) {
        fetch(`/api/find/username/auth`, {
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
                window.location.href = `/api/find/username/${result.message}/changePassword`;
            })
            .catch(error => {
                alert(error.message);
            });
    } else {
        alert("전송 버튼을 눌러주세요!");
    }
}

function changeForm() {
    helpText.classList.add("d-none");
    helpAuthNumberText.classList.remove("d-none");

    userNameCustomerPhoneForm.classList.add("d-none");
    authInputGroup.classList.remove("d-none");

    sendSMS.classList.add("d-none");
    checkAuthNumber.classList.remove("d-none");
}