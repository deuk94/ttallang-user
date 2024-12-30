<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>따릉이 - KOSA BIKE</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/main/css/main.css">
</head>
<body>

<%@ include file="../head/head.jsp" %>

<!-- 지도 영역 -->
<div id="map-container">
    <div id="map"></div>
    <div class="popup" id="branchInfoPopup">
        <button class="close-btn" onclick="closePopup('branchInfoPopup')">X</button>
        <div class="popup-header">
            <h3 id="branchName">선택된 대여소 이름</h3>
        </div>
        <div class="popup-content">
            <p class="bike-info">
                현재 사용 가능한 자전거: <span id="availableBikes">0</span>대
            </p>
            <p class="pricing-info">잠금해제 500원, 분당 150원</p>
            <table class="bicycle-list">
                <tbody id="bicycleListContainer">
                <!-- 자전거 목록이 여기에 추가됩니다 -->
                </tbody>
            </table>
        </div>
    </div>

    <!-- 대여 현황판 팝업 -->
    <div class="popup" id="rentalStatusPopup" style="display: none;">
        <h3>자전거 대여 현황</h3>
        <p>자전거 이름: <span id="rentedBicycleName"></span></p>
        <p>대여 지점: <span id="rentalBranchName"></span></p>
        <p>대여 시작 시간: <span id="rentalStartTime"></span></p>
        <div class="button-container">
            <button class="return-button" onclick="returnBikeFromStatus()">반납하기</button>
            <button class="report-button">신고하기</button>
        </div>
    </div>

    <!-- 신고하기 팝업 -->
    <div class="report-popup" id="reportPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('reportPopup');">X</button>
        <h3>신고하기</h3>
        <select id="reportCategorySelect">
        </select>
        <textarea id="reportDetails" placeholder="신고 내용을 입력해주세요..."></textarea>
        <button class="report-submit">신고하기</button>
    </div>
    <!-- 내 위치 버튼 -->
        <img src="/images/gps.png" id="locateMeButton" onclick="moveToMyLocation()" alt="내 위치로 이동">
</div>

<%@ include file="../footer/footer.jsp" %>

<!-- 외부 및 main.js 파일 포함 -->
<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=1bfc76317e78b81b1a32b1be44269182"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="${pageContext.request.contextPath}/main/js/main.js"></script>

</body>
</html>
