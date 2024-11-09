const phoneId = document.querySelector("#id_phone");
const getAuthBtn = document.getElementById("get-auth-btn");
const authPhone = document.getElementById("auth-phone");
const authPhoneSubmit = document.getElementById("auth-phone-submit");
const authTimer = document.getElementById("auth-timer");
const helpText = document.getElementById("help-text");
const csrftoken = document.querySelector('[name=csrfmiddlewaretoken]').value;
getAuthBtn.addEventListener("click", (event) => {
    event.preventDefault();
    if (phoneId.value.length === 11 && !(isNaN(phoneId.value))) {
        let formData = new FormData();
        formData.append("phone", phoneId.value);
        axios({
            method: 'post',
            url: `/accounts/${event.target.dataset.accountId}/update/check/`,
            headers: { 'X-CSRFToken': csrftoken },
            data: formData,
        })
            // 일일 허용 횟수 검사
            .then(response => {
                if (response.data.authCount == 5) {
                    phoneId.setAttribute("disabled", true);
                    getAuthBtn.setAttribute("disabled", true);
                    helpText.classList.remove("d-none");
                    helpText.textContent = "오늘은 더 이상 인증이 불가능합니다. (최대 인증횟수 5회)";
                } else {
                    // 인증번호 입력란 보여주기
                    if (document.querySelector("#already-auth-user") != null) {
                        const phoneDiv = document.getElementById("phone-div");
                        const alreadyAuthUser = document.querySelector("#already-auth-user");
                        phoneDiv.removeChild(alreadyAuthUser);
                    };
                    getAuthBtn.classList.add("d-none");
                    authPhone.classList.remove("d-none");
                    authPhoneSubmit.classList.remove("d-none");
                    authTimer.classList.remove("d-none");
                    helpText.classList.remove("d-none");
                    let formData = new FormData();
                    formData.append("phone", phoneId.value);
                    axios({
                        method: 'post',
                        url: `/accounts/${event.target.dataset.accountId}/update/phone-auth/`,
                        headers: { 'X-CSRFToken': csrftoken },
                        data: formData,
                    })
                        .then(response => {
                            let totalSec = 300;
                            let min = '';
                            let sec = '';
                            let authInterval = setInterval(function () {
                                let min = parseInt(totalSec / 60);
                                let sec = totalSec % 60;
                                if (sec < 10) {
                                    authTimer.textContent = min + " : 0" + sec + "\u00a0";
                                } else {
                                    authTimer.textContent = min + " : " + sec + "\u00a0";
                                }
                                totalSec--;
                                if (totalSec < 0) {
                                    clearInterval(authInterval);
                                    authTimer.textContent = "인증 시간이 만료되었습니다.";
                                };
                            }, 1000);
                        });
                };
            });
    } else {
        phoneId.focus();
    };
});