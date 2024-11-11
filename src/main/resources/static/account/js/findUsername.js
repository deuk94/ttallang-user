const phoneForm = document.querySelector("#phoneForm");
const helpPhoneNumber = document.querySelector("#helpPhoneNumber");
const helpAuthNumber = document.querySelector("#helpAuthNumber");
const customerPhoneInputGroup = document.querySelector("#customerPhoneInputGroup");
const customerPhone = document.querySelector("#customerPhone");
const authInputGroup = document.querySelector("#authInputGroup");

customerPhone.addEventListener("input", validateCustomerPhone);
phoneForm.addEventListener("submit", handlePhoneForm);
let isAuthenticated = false;
let isSubmitted = false;

function handlePhoneForm(event) {
    event.preventDefault();

    const formData = new FormData(phoneForm);
    const data = Object.fromEntries(formData);

    if (validateCustomerPhone()) {
        fetch(`/api/find/username`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("문자 보내기를 실패하였습니다.");
                }
                return response.json();
            })
            .then(data => {
                if (data.code !== 200) {
                    alert(data.message);
                } else { // 전송 성공.
                    isSubmitted = true;
                    // 인증번호 입력란 보여주기.
                    helpPhoneNumber.classList.add("d-none");
                    customerPhoneInputGroup.classList.add("d-none");
                    helpAuthNumber.classList.remove("d-none");
                    authInputGroup.classList.remove("d-none");
                }
            })
            .catch(error => {
                alert(error)
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
    data[customerPhone] = customerPhone.value;

    if (isSubmitted) {
        fetch(`/api/find/username/auth`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("인증을 실패하였습니다.");
                }
                return response.json();
            })
            .then(data => {
                if (data.code !== 200) {
                    alert(data.message);
                } else { // 인증 성공.
                    alert(`회원님의 아이디는 ${data.message} 입니다.`);
                    // 아이디 찾은 다음 아이디 알려주는 곳으로 이동.
                }
            })
            .catch(error => {
                alert(error)
            });
    } else {
        alert("핸드폰 번호 입력 후 전송 버튼을 눌러주세요!");
    }
}