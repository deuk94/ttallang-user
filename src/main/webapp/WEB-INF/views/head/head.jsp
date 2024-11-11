<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<style>
  header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 1px 20px;
    background-color: #f7f7f7;
    border-bottom: 1px solid #ddd;
    position: fixed; /* 고정 위치 설정 */
    top: 0; /* 화면 상단에 고정 */
    left: 0;
    width: 98%; /* 화면 너비에 맞춤 */
    z-index: 1000; /* 다른 요소들 위에 표시되도록 설정 */
  }

  header img {
    height: 70px;
    width: auto;
  }

  header nav a {
    margin: 0 15px;
    text-decoration: none;
    color: #333;
  }

  a {
    text-decoration: none;
    color: black;
  }

  .brand-name {
    font-size: 35px; /* 글자 크기를 원하는 크기로 설정 */
    font-weight: bold; /* 굵은 글씨 */
    color: rgba(2, 2, 2, 0.97);
  }
</style>
<header>
    <div>
        <a href="/map/main">
            <img src="/images/logo.png" alt="따릉이 로고" />
        </a>
    </div>
    <nav>
        <a href="/myPage/information">이용 안내</a>
        <a href="/myPage/charge">이용 요금</a>
        <a href="/myPage/responsibility">책임 사항</a>
        <a href="/myPage/insurance">보험 안내</a>
        <a href="/myPage/safety">안전수칙</a>
    </nav>
    <div>
        <span>환영합니다 ${username}님!</span>
        | <a href="${pageContext.request.contextPath}/api/logout">로그아웃</a>
        | <a href="${pageContext.request.contextPath}/myPage/userModify">마이페이지</a>
    </div>
</header>
