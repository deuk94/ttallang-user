<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>따릉이 - KOSA BIKE</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<header>
    <div>
        <img src="/images/자전거.png" alt="따릉이 로고" />
        <span>KOSA BIKE</span>
    </div>
    <nav>
        <a href="#">사업소개</a>
        <a href="#">대여소 안내</a>
        <a href="#">이용권 구매</a>
        <a href="#">문의/FAQ</a>
        <a href="#">공지사항</a>
        <a href="#">안전수칙</a>
    </nav>
    <div>
        <span>환영합니다 ${username}님!</span>
        <span>customerId : ${customerId}</span>
        | <a href="${pageContext.request.contextPath}/api/logout">로그아웃</a>
        | <a href="${pageContext.request.contextPath}/mypage">마이페이지</a>
        | <a href="${pageContext.request.contextPath}/info">이용안내</a>
    </div>
</header>

<!-- 지도 영역 -->
<div id="map-container">
    <div id="map"></div>

    <!-- 통합 팝업 -->
    <div class="popup" id="branchInfoPopup">
        <button class="close-btn" onclick="closePopup('branchInfoPopup')">X</button>
        <div class="popup-header">
            <h3 id="branchName">선택된 지점 이름</h3>
        </div>
        <div class="popup-content">
            <p class="bike-info">
                현재 사용 가능한 자전거: <span id="availableBikes">0</span>대
                <a href="#" onclick="openReportPopup(event)" class="report-link">신고하기</a>
            </p>
            <p class="pricing-info">잠금해제 1000원, 분당 100원</p>
            <div class="bicycle-list" id="bicycleListContainer"></div>
        </div>
    </div>

    <!-- 신고 팝업 -->
    <div class="report-popup" id="reportPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('reportPopup')">X</button>
        <h3>도움이 필요하신가요?</h3>
        <a href="#" onclick="openNoBikePopup(event)">이 위치에 기기가 없어요</a>
        <a href="#" onclick="openBrokenBikePopup(event)">기기가 고장났어요</a>
    </div>

    <!-- 위치 신고 팝업 -->
    <div class="popup" id="noBikeReportPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('noBikeReportPopup')">X</button>
        <h3>위치 신고</h3>
        <textarea id="noBikeDetails" placeholder="상세 내용을 입력하세요..."></textarea>
        <button onclick="submitNoBikeReport()">접수하기</button>
    </div>

    <!-- 고장 신고 팝업 -->
    <div class="popup" id="brokenBikeReportPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('brokenBikeReportPopup')">X</button>
        <h3>고장 신고</h3>
        <textarea id="brokenBikeDetails" placeholder="상세 내용을 입력하세요..."></textarea>
        <button onclick="submitBrokenBikeReport()">접수하기</button>
    </div>
</div>
</div>

<!-- 하단 정보 영역 -->
<div class="footer">
    <p>이용약관 | 위치정보관련 약관 | 개인정보처리방침 | 보험안내 | 도움주신 분</p>
    <p>서울특별시 코사 딸랑이 대표 김영윤</p>
    <p>COPYRIGHT ⓒ 2024 bike코사 ALL RIGHTS RESERVED.</p>
</div>

<!-- JavaScript -->
<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=b4a025b0f93d9847f6948dcc823656aa"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
  // 전역 변수 설정
  var selectedBranchName = ''; // 선택된 대여소 이름을 저장하는 변수
  var selectedBicycleId = null; // 선택된 자전거 ID를 저장하는 변수
  var selectedBranchLatitude = 0; // 선택된 대여소 위도
  var selectedBranchLongitude = 0; // 선택된 대여소 경도


  // 모델에서 customerId 가져오기
  var customerId = ${customerId};
  // 팝업 닫기 함수
  function closePopup(popupId) {
    var popup = document.getElementById(popupId);
    if (popup) {
      popup.style.display = 'none';
    }
  }

  // 신고 팝업 열기 함수
  function openReportPopup(event) {
    event.preventDefault();
    closePopup('branchInfoPopup');
    document.getElementById('reportPopup').style.display = 'block';
  }

  // 위치 신고 팝업 열기 함수
  function openNoBikePopup(event) {
    event.preventDefault();
    closePopup('reportPopup');
    document.getElementById('noBikeReportPopup').style.display = 'block';
  }

  // 고장 신고 팝업 열기 함수
  function openBrokenBikePopup(event) {
    event.preventDefault();
    closePopup('reportPopup');
    document.getElementById('brokenBikeReportPopup').style.display = 'block';
  }

  // 위치 신고 제출 함수
  function submitNoBikeReport() {
    alert("위치 신고가 접수되었습니다.");
    closePopup('noBikeReportPopup');
  }

  // 고장 신고 제출 함수
  function submitBrokenBikeReport() {
    alert("고장 신고가 접수되었습니다.");
    closePopup('brokenBikeReportPopup');
  }

  // 지도와 마커 설정
  var imageSrc = '/images/자전거.png',
      imageSize = new kakao.maps.Size(40, 40),
      imageOption = {offset: new kakao.maps.Point(20, 20)};
  var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);
  var container = document.getElementById('map');
  var options = {
    center: new kakao.maps.LatLng(37.583883601891, 126.9999880311),
    level: 3
  };
  var map = new kakao.maps.Map(container, options);

  // Ajax를 통해 대여소 정보 불러오기
  $.ajax({
    url: "/map/branches",
    method: "GET",
    success: function(data) {
      data.forEach(function(branch) {
        var marker = new kakao.maps.Marker({
          map: map,
          position: new kakao.maps.LatLng(branch.latitude, branch.longitude),
          image: markerImage
        });

        // 대여소 클릭 시 정보 표시 팝업
        kakao.maps.event.addListener(marker, 'click', function() {
          selectedBranchName = branch.branchName;
          selectedBranchLatitude = branch.latitude;
          selectedBranchLongitude = branch.longitude;

          document.getElementById("branchName").innerText = selectedBranchName;
          getAvailableBikesAtLocation(branch.latitude, branch.longitude); // 자전거 개수 조회
          showAvailableBicycles(branch.latitude, branch.longitude); // 자전거 리스트 조회
          document.getElementById("branchInfoPopup").style.display = 'block';
        });
      });
    },
    error: function(xhr, status, error) {
      console.error("대여소 데이터 불러오기 실패:", error);
    }
  });

  // 마커 클릭 시 대여 가능한 자전거 개수 조회
  function getAvailableBikesAtLocation(latitude, longitude) {
    $.ajax({
      url: "/map/available/bikes/location",
      method: "GET",
      data: { latitude: latitude, longitude: longitude },
      success: function(count) {
        document.getElementById("availableBikes").innerText = count;
      },
      error: function(xhr, status, error) {
        console.error("자전거 개수 불러오기 실패:", error);
      }
    });
  }

  // 대여소 위치 근처의 대여 가능한 자전거 목록 조회 및 표시
  function showAvailableBicycles(latitude, longitude) {
    $.ajax({
      url: "/map/available/bikes",
      method: "GET",
      data: { latitude: latitude, longitude: longitude },
      success: function(bicycles) {
        var container = document.getElementById("bicycleListContainer");
        container.innerHTML = ''; // 기존 내용을 지움

        bicycles.forEach(function(bike) {
          var bikeElement = document.createElement("div");
          bikeElement.className = "bicycle-item";

          var bikeInfo = document.createElement("span");
          bikeInfo.textContent = bike.bicycleName;
          bikeElement.appendChild(bikeInfo);

          var rentButton = document.createElement("button");
          rentButton.textContent = "대여";
          rentButton.onclick = function() {
            rentBike(bike.bicycleId, customerId, selectedBranchName);
          };
          bikeElement.appendChild(rentButton);

          container.appendChild(bikeElement);
        });
      },
      error: function(xhr, status, error) {
        console.error("자전거 목록 불러오기 실패:", error);
      }
    });
  }

  // 대여 기능 함수
  function rentBike(bicycleId, customerId, rentalBranch) {
    $.ajax({
      url: '/map/rent/bicycle',
      type: 'POST',
      data: {
        bicycleId: bicycleId,
        customerId: customerId,
        rentalBranch: rentalBranch
      },
      success: function(response) {
        alert(response); // 대여 성공 메시지 출력
        // 대여 상태 업데이트 UI
        document.getElementById("availableBikes").innerText = parseInt(document.getElementById("availableBikes").innerText) - 1;
        closePopup('branchInfoPopup'); // 대여 성공 시 팝업 닫기
      },
      error: function(xhr) {
        alert("대여에 실패했습니다: " + xhr.responseText);
      }
    });
  }

  // 지도 클릭 시 팝업 닫기
  kakao.maps.event.addListener(map, 'click', function() {
    closePopup('branchInfoPopup');
    closePopup('reportPopup');
    closePopup('noBikeReportPopup');
    closePopup('brokenBikeReportPopup');
  });
</script>

</body>
</html>
