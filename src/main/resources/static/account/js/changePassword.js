const changePasswordForm = document.querySelector("#changePasswordForm");

const userPassword = document.querySelector("#userPassword");
const confirmPassword = document.querySelector("#confirmPassword");

changePasswordForm.addEventListener("submit", handleChangePasswordForm);

userPassword.addEventListener("input", validateUserPassword);
confirmPassword.addEventListener("input", validateConfirmPassword);

function handleChangePasswordForm(event) {
    event.preventDefault();

    // 폼 유효성 확인.
    if (validateUserPassword() && validateConfirmPassword()) {
        const formData = new FormData(changePasswordForm);
        const data = Object.fromEntries(formData);

        fetch("/api/find/changePassword", {
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
                alert("변경이 취소되었습니다.");
                window.location.href = "/login/form";
            });
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