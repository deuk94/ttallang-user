<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>따릉이 - KOSA BIKE</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/map/css/map.css">
</head>
<body>

<%@ include file="/WEB-INF/views/map/head/head.jsp" %>

<!-- 지도 영역 -->
<div id="map-container">
    <div id="map"></div>

    <!-- 대여소 정보 팝업 -->
    <div class="popup" id="branchInfoPopup">
        <button class="close-btn" onclick="closePopup('branchInfoPopup')">X</button>
        <div class="popup-header">
            <h3 id="branchName">선택된 지점 이름</h3>
        </div>
        <div class="popup-content">
            <p class="bike-info">
                현재 사용 가능한 자전거: <span id="availableBikes">0</span>대
            </p>
            <p class="pricing-info">잠금해제 500원, 분당 150원</p>
            <div class="bicycle-list" id="bicycleListContainer"></div>
        </div>
    </div>

    <!-- 신고하기 팝업 -->
    <div class="report-popup" id="dynamicReportPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('dynamicReportPopup')">X</button>
        <h3>신고하기</h3>
        <select id="reportCategorySelect1">
            <!-- 카테고리 옵션 로딩됨 -->
        </select>
        <textarea id="reportDetails1" placeholder="신고 내용을 입력해주세요..."></textarea>
        <button class="report-submit" onclick="submitReport()">신고하기</button>
    </div>

    <!-- 대여소 외 지역 반납 팝업 -->
    <div class="popup" id="customReturnPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('customReturnPopup')">X</button>
        <h3>대여소가 아닌 장소입니다.</h3>
        <p>반납 위치: <span id="customReturnLocation">기타</span></p>
        <p>현재 대여중인 자전거: <span id="customBicycleName"></span></p>
        <p>대여 지점: <span id="customRentalBranch"></span></p>
        <p>대여 시작 시간: <span id="customRentalStartDate"></span></p>
        <p>그래도 반납하시겠습니까?</p>
        <button onclick="returnBike(true)">반납하기</button>
        <button onclick="openReportAndReturnPopup(); closePopup('customReturnPopup');">신고하기</button>
    </div>

    <!-- 대여소 반납 팝업 -->
    <div class="popup" id="returnPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('returnPopup')">X</button>
        <h3>선택한 대여소로 자전거를 반납하시겠습니까?</h3>
        <p>반납 위치: <span id="returnBranchName"></span></p>
        <p>현재 대여중인 자전거: <span id="bicycleName"></span></p>
        <p>대여 지점: <span id="rentalBranch"></span></p>
        <p>대여 시작 시간: <span id="rentalStartDate"></span></p>
        <button onclick="returnBike(false)">반납하기</button>
        <button onclick="openReportAndReturnPopup(); closePopup('returnPopup');">신고하기</button>
    </div>

    <!-- 신고 및 반납 팝업 -->
    <div class="report-popup" id="reportAndReturnPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('reportAndReturnPopup')">X</button>
        <h3>신고 및 반납</h3>
        <select id="reportCategorySelect2">
            <!-- 카테고리 옵션 로딩됨 -->
        </select>
        <textarea id="reportDetails2" placeholder="신고 내용을 입력해주세요..."></textarea>
        <button class="report-submit" onclick="submitReportAndReturn()">신고 및 반납하기</button>
    </div>
</div>

<%@ include file="/WEB-INF/views/map/footer/footer.jsp" %>

<!-- 외부 및 map.js 파일 포함 -->
<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=b4a025b0f93d9847f6948dcc823656aa"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="${pageContext.request.contextPath}/map/js/map.js"></script>

</body>
</html>
