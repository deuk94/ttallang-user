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

    <!-- 대여소 정보 팝업 -->
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
            <div class="bicycle-list" id="bicycleListContainer"></div>
        </div>
    </div>

    <!-- 대여 현황판 팝업 -->
    <div class="popup" id="rentalStatusPopup" style="display: none;">
        <h3>자전거 대여 현황</h3>
        <p>자전거 이름: <span id="rentedBicycleName"></span></p>
        <p>대여 지점: <span id="rentalBranchName"></span></p>
        <p>대여 시작 시간: <span id="rentalStartTime"></span></p>
        <div style="display: none;">
            <p>위도: <span id="currentLatitude"></span></p>
            <p>경도: <span id="currentLongitude"></span></p>
        </div>
        <div class="button-container">
            <button class="return-button" onclick="returnBikeFromStatus()">반납하기</button>
            <button class="report-button" onclick="openReportAndReturnPopup(); closePopup('customReturnPopup');">신고하기</button>
        </div>
    </div>

    <!-- 신고하기 팝업 -->
    <div class="report-popup" id="dynamicReportPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('dynamicReportPopup')">X</button>
        <h3>신고하기</h3>
        <select id="reportCategorySelect1">
        </select>
        <textarea id="reportDetails1" placeholder="신고 내용을 입력해주세요..."></textarea>
        <button class="report-submit" onclick="submitReport()">신고하기</button>
    </div>

    <!-- 대여소 외 지역 반납 팝업 -->
    <div class="popup" id="customReturnPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('customReturnPopup')">X</button>
        <h3>대여소가 아닌 장소입니다.</h3>
        <p id="customReturnLocation" style="display: none;">반납 위치: <span>기타</span></p>
        <p>현재 대여중인 자전거: <span id="customBicycleName"></span></p>
        <p>대여 지점: <span id="customRentalBranch"></span></p>
        <p>대여 시작 시간: <span id="customRentalStartDate"></span></p>
        <p>그래도 반납하시겠습니까?</p>
        <div class="button-container">
            <button class="return-button" onclick="returnBikeFromCustomLocation()">반납하기</button>
            <button class="report-button" onclick="openReportAndReturnPopup(); closePopup('customReturnPopup');">신고하기</button>
        </div>
    </div>

    <!-- 대여소 반납 팝업 -->
    <div class="popup" id="returnPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('returnPopup')">X</button>
        <h3>선택한 대여소로 자전거를 반납하시겠습니까?</h3>
        <p>반납 위치: <span id="returnBranchName"></span></p>
        <p>현재 대여중인 자전거: <span id="bicycleName"></span></p>
        <p>대여 지점: <span id="rentalBranch"></span></p>
        <p>대여 시작 시간: <span id="rentalStartDate"></span></p>
        <div class="button-container">
            <button class="return-button" onclick="returnBikeFromBranch()">반납하기</button>
            <button class="report-button" onclick="openReportAndReturnPopup(); closePopup('customReturnPopup');">신고하기</button>
        </div>
    </div>


    <!-- 신고 및 반납 팝업 -->
    <div class="report-popup" id="reportAndReturnPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('reportAndReturnPopup')">X</button>
        <h3>신고하기</h3>
        <select id="reportCategorySelect2">
            <!-- 카테고리 옵션 로딩됨 -->
        </select>
        <textarea id="reportDetails2" placeholder="신고 내용을 입력해주세요..."></textarea>
        <button class="report-submit" onclick="submitReportAndReturn()">신고하기</button>
    </div>
</div>

<%@ include file="../footer/footer.jsp" %>

<!-- 외부 및 main.js 파일 포함 -->
<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=40dcc4dae6de01cd9ed29123fcc4d775"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="${pageContext.request.contextPath}/main/js/main.js"></script>

</body>
</html>
