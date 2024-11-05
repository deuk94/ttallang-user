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
    <div class="report-popup" id="reportPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('reportPopup')">X</button>
        <h3>도움이 필요하신가요?</h3>
        <div class="report-options">
            <a href="javascript:void(0);" onclick="openLocationReportPopup()">이 위치에 기기가 없어요</a>
            <a href="javascript:void(0);" onclick="openBrokenReportPopup()">기기가 고장났어요</a>
        </div>
    </div>

    <!-- 위치 신고 팝업 -->
    <div class="report-popup" id="locationReportPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('locationReportPopup')">X</button>
        <h3 class="report-title">위치 신고</h3>
        <textarea id="locationReportDetails" placeholder="문제가 발생한 위치를 입력해주세요..."></textarea>
        <button class="report-submit" onclick="submitLocationReport()">신고하기</button>
    </div>

    <!-- 고장 신고 팝업 -->
    <div class="report-popup" id="brokenReportPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('brokenReportPopup')">X</button>
        <h3 class="report-title">고장 신고</h3>
        <textarea id="brokenReportDetails" placeholder="고장 내용을 입력해주세요..."></textarea>
        <button class="report-submit" onclick="submitBrokenReport()">신고하기</button>
    </div>

    <!-- 대여소 외 지역 반납 팝업 -->
    <div class="popup" id="customReturnPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('customReturnPopup')">X</button>
        <h3>대여소가 아닌 장소입니다.</h3>
        <p>그래도 반납하시겠습니까?</p>
        <button onclick="returnBike(true)">반납하기</button>
    </div>

    <!-- 대여소 반납 팝업 -->
    <div class="popup" id="returnPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('returnPopup')">X</button>
        <h3>선택한 대여소로 자전거를 반납하시겠습니까?</h3>
        <p>반납 위치: <span id="returnBranchName"></span></p>
        <button onclick="returnBike(false)">반납하기</button>
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
  var selectedBranchName = '';
  var selectedBranchLatitude = 0;
  var selectedBranchLongitude = 0;
  var customerId = ${customerId};
  var selectedBicycleId = 0; // 선택된 자전거 ID 저장

  // 대여 상태 확인 함수
  async function checkRentalStatus() {
    try {
      const response = await $.ajax({
        url: "/map/check-rental-status",
        method: "GET",
        data: { customerId: customerId }
      });
      return response && response.rentalStatus === "0"; // 대여 중 상태
    } catch (error) {
      console.error("Error checking rental status:", error);
      return false;
    }
  }

  // 신고하기 팝업 열기 함수
  function openReportPopup() {
    closePopup('branchInfoPopup');
    document.getElementById("reportPopup").style.display = 'block';
  }

  // 위치 신고 팝업 열기 함수
  function openLocationReportPopup() {
    closePopup('reportPopup');
    document.getElementById("locationReportPopup").style.display = 'block';
  }

  // 고장 신고 팝업 열기 함수
  function openBrokenReportPopup() {
    closePopup('reportPopup');
    document.getElementById("brokenReportPopup").style.display = 'block';
  }

  // 위치 신고 전송 함수
  function submitLocationReport() {
    const details = document.getElementById("locationReportDetails").value;
    submitReport(1, details); // categoryId = 1
  }

  // 고장 신고 전송 함수
  function submitBrokenReport() {
    const details = document.getElementById("brokenReportDetails").value;
    submitReport(2, details); // categoryId = 2
  }

  // 신고 전송 공통 함수
  function submitReport(categoryId, details) {
    $.ajax({
      url: "/map/report-issue",
      type: "POST",
      data: {
        customerId: customerId,
        bicycleId: selectedBicycleId,
        categoryId: categoryId,
        reportDetails: details
      },
      success: function(response) {
        alert("신고가 접수되었습니다.");
        closeAllPopups();
      },
      error: function(xhr, status, error) {
        alert("신고 접수 중 오류가 발생했습니다.");
        console.error("Error:", error);
      }
    });
  }

  // 특정 팝업 닫기 함수
  function closePopup(popupId) {
    var popup = document.getElementById(popupId);
    if (popup) {
      popup.style.display = 'none';
    }
  }

  // 모든 팝업 닫기 함수
  function closeAllPopups() {
    closePopup('branchInfoPopup');
    closePopup('reportPopup');
    closePopup('locationReportPopup');
    closePopup('brokenReportPopup');
    closePopup('customReturnPopup');
    closePopup('returnPopup');
  }

  // 대여소 외 지역 반납 팝업 표시 함수
  function showCustomReturnPopup() {
    document.getElementById("customReturnPopup").style.display = 'block';
  }

  // 반납 팝업 열기 함수
  function showReturnPopup(isCustomLocation) {
    if (isCustomLocation) {
      document.getElementById("customReturnPopup").style.display = 'block';
    } else {
      document.getElementById("returnBranchName").innerText = selectedBranchName;
      document.getElementById("returnPopup").style.display = 'block';
    }
  }

  // 대여 기능 함수 (반납 성공 시 결제 페이지로 이동)
  function returnBike(isCustomLocation) {
    var returnLatitude = selectedBranchLatitude;
    var returnLongitude = selectedBranchLongitude;

    $.ajax({
      url: '/map/return/bicycle',
      type: 'POST',
      data: {
        customerId: customerId,
        returnLatitude: returnLatitude,
        returnLongitude: returnLongitude,
        isCustomLocation: isCustomLocation
      },
      success: function(response) {
        alert(response);
        window.location.href = "/pay/userPayment";
      },
      error: function(xhr) {
        alert("반납에 실패했습니다: " + xhr.responseText);
      }
    });
  }

  // 대여소 클릭 시 대여 상태 확인 후 반납 팝업 표시
  async function handleBranchClick(latitude, longitude) {
    closePopup('customReturnPopup');

    const isRented = await checkRentalStatus();
    if (isRented) {
      selectedBranchLatitude = latitude;
      selectedBranchLongitude = longitude;
      showReturnPopup(false);
    } else {
      getAvailableBikesAtLocation(latitude, longitude);
      showAvailableBicycles(latitude, longitude);
      document.getElementById("branchInfoPopup").style.display = 'block';
    }
  }

  // 지도 상 자전거 대여소 표시 및 이벤트 설정
  function loadBranches() {
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

          kakao.maps.event.addListener(marker, 'click', function() {
            selectedBranchName = branch.branchName;
            selectedBranchLatitude = branch.latitude;
            selectedBranchLongitude = branch.longitude;
            document.getElementById("branchName").innerText = selectedBranchName;
            handleBranchClick(branch.latitude, branch.longitude);
          });
        });
      },
      error: function(xhr, status, error) {
        console.error("대여소 데이터 불러오기 실패:", error);
      }
    });
  }

  // 자전거 대여 가능 수 조회
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
        container.innerHTML = '';

        bicycles.forEach(function(bike) {
          var bikeElement = document.createElement("div");
          bikeElement.className = "bicycle-item";

          var bikeInfo = document.createElement("span");
          bikeInfo.textContent = bike.bicycleName;
          bikeElement.appendChild(bikeInfo);

          // 대여 버튼 추가 (왼쪽 정렬)
          var rentButton = document.createElement("button");
          rentButton.textContent = "대여";
          rentButton.style.marginRight = "10px";
          rentButton.onclick = function() {
            selectedBicycleId = bike.bicycleId;
            rentBike(bike.bicycleId, customerId, selectedBranchName);
          };
          bikeElement.appendChild(rentButton);

          // 신고 버튼 추가 (오른쪽 정렬)
          var reportButton = document.createElement("button");
          reportButton.textContent = "신고";
          reportButton.onclick = function() {
            selectedBicycleId = bike.bicycleId;
            openReportPopup(); // 신고 팝업 열기
          };
          bikeElement.appendChild(reportButton);

          container.appendChild(bikeElement);
        });
      },
      error: function(xhr, status, error) {
        console.error("자전거 목록 불러오기 실패:", error);
      }
    });
  }

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
  map.setDraggable(true);

  loadBranches();

  // 지도 빈 공간 클릭 시 모든 팝업 닫기 및 대여 상태 확인 후 대여소 외 반납 팝업 표시
  kakao.maps.event.addListener(map, 'click', async function() {
    closeAllPopups();

    try {
      const isRented = await checkRentalStatus();
      if (isRented) {
        showCustomReturnPopup();
      }
    } catch (error) {
      console.error("대여 상태 확인 중 오류 발생:", error);
    }
  });
</script>

</body>
</html>
