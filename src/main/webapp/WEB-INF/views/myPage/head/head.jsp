<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<style>
  header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 20px;
    background-color: #f7f7f7;
    border-bottom: 1px solid #ddd;
    position: fixed; /* 고정 위치 설정 */
    top: 0; /* 화면 상단에 고정 */
    left: 0;
    width: 98%; /* 화면 너비에 맞춤 */
    z-index: 1000; /* 다른 요소들 위에 표시되도록 설정 */
  }

  header img {
    height: 40px;
  }

  header nav a {
    margin: 0 15px;
    text-decoration: none;
    color: #333;
  }
</style>
<header>
    <div>
        <a href="/map/main">
            <img src="/images/자전거.png" alt="따릉이 로고" />
            <span>KOSA BIKE</span>
        </a>
    </div>
    <nav>
        <a href="javascript:void(0);">사업소개</a>
        <a href="javascript:void(0);">대여소 안내</a>
        <a href="javascript:void(0);">이용권 구매</a>
        <a href="javascript:void(0);">문의/FAQ</a>
        <a href="javascript:void(0);">공지사항</a>
        <a href="javascript:void(0);">안전수칙</a>
    </nav>
    <div>
        <span>환영합니다 ${username}님!</span>
        | <a href="${pageContext.request.contextPath}/api/logout">로그아웃</a>
        | <a href="${pageContext.request.contextPath}/myPage/userModify">마이페이지</a>
        | <a href="${pageContext.request.contextPath}/info">이용안내</a>
    </div>
</header>
