const logoutButton = document.querySelector("#logoutButton");

logoutButton.addEventListener("click", (event) => {
    event.preventDefault();
    fetch("/api/logout", {
        credentials: "include",
        method: "GET",
        headers: {
            // application/json 으로 보내면 안됨.
            // 시큐리티에서 설정된 경로로 걸러지는 요청에 대해서 json 타입으로 하면 서버 측에서 못알아먹는다고 한다.
            "Content-Type": "application/x-www-form-urlencoded",
        },
    }).then(async response => { // response.json()의 기대값.
        if (!response.ok) {
            const result = await response.json();
            throw new Error(result.message);
        }
        return response.json();
    }).then(result => { // response.json()의 실제값.
        alert(result.message);
        window.location.href = "/login/form";
    }).catch(error => {
        alert(error.message);
    });
})