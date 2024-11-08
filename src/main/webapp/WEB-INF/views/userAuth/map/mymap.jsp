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
<%--        <p>현재 대여중인 자전거 아이디: <span id="bicycleid"></span></p>--%>
        <p>현재 대여중인 자전거: <span id="bicycleName"></span></p> <!-- 자전거 이름 표시 -->
        <p>대여 지점: <span id="rentalBranch"></span></p>
        <p>대여 시작 시간: <span id="rentalStartDate"></span></p>
        <button onclick="returnBike(false)">반납하기</button>
        <button onclick="openReportAndReturnPopup(); closePopup('returnPopup');">신고하기</button> <!-- 새로운 신고 및 반납 팝업 열기 -->
    </div>


    <!-- 새로운 신고 및 반납 팝업 -->
    <div class="report-popup" id="reportAndReturnPopup" style="display: none;">
        <button class="close-btn" onclick="closePopup('reportAndReturnPopup')">X</button>
        <h3 class="report-title">신고 및 반납</h3>
        <textarea id="reportAndReturnDetails" placeholder="신고 내용을 입력해주세요..."></textarea>
        <button class="report-submit" onclick="submitReportAndReturn()">신고 및 반납하기</button> <!-- 신고 및 반납 버튼 -->
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
      var selectedBicycleId = 0;
      var selectedReturnLatitude = 0;
      var selectedReturnLongitude = 0;



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
            alert(response);
            closeAllPopups();
          },
          error: function(xhr) {
            if (xhr.status === 400) { // Bad Request
              alert(xhr.responseText);
              window.location.href = "/pay/payment";  // 결제 페이지로 리다이렉트
            } else {
              alert("대여에 실패했습니다: " + xhr.responseText);
            }
          }
        });
      }


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

      function openReportPopup() {
        closePopup('branchInfoPopup');

        checkRentalStatus().then(isRented => {
          const reportButton = document.getElementById("reportButton"); // 신고 버튼의 ID 사용 (가정)
          if (isRented) {
            reportButton.onclick = submitReportAndReturn; // 대여 중 신고 및 반납 함수 연결
          } else {
            reportButton.onclick = submitBrokenReport; // 기존 신고 함수 연결
          }
          document.getElementById("reportPopup").style.display = 'block';
        });
      }

      function openLocationReportPopup() {
        closePopup('reportPopup');
        document.getElementById("locationReportPopup").style.display = 'block';
      }

      function openBrokenReportPopup() {
        closePopup('branchInfoPopup');  // 다른 팝업이 열린 상태라면 닫기
        document.getElementById("brokenReportPopup").style.display = 'block'; // 고장 신고 팝업 열기
      }

      function handleReportClick() {
        openReportPopup(); // 신고 팝업을 먼저 열기
        closePopup('returnPopup'); // 이후 반납 팝업 닫기
      }

      function submitLocationReport() {
        const details = document.getElementById("locationReportDetails").value;
        submitReport(1, details); // categoryId = 1
      }

      function submitBrokenReport() {
        const details = document.getElementById("brokenReportDetails").value;
        submitReport(2, details); // categoryId = 2
      }


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
            // 서버에서 반환된 응답을 로그로 확인
            console.log(response);
            if (response.includes("신고가 접수되었습니다")) {
              alert(response);
            } else {
              alert("신고 접수에 실패했습니다."); 
            }
            closeAllPopups(); // 모든 팝업 닫기
          },
          error: function(xhr, status, error) {
            alert("신고 접수 중 오류가 발생했습니다: " + xhr.responseText);
            console.error("Error:", error);
          }
        });
      }


      // 반납 팝업의 신고 버튼 클릭 시 "신고 및 반납 팝업" 열기
      function openReportAndReturnPopup() {
        closePopup('returnPopup'); // 반납 팝업 닫기
        document.getElementById("reportAndReturnPopup").style.display = 'block'; // 새로운 신고 및 반납 팝업 열기
      }


      function submitReportAndReturn() {
        if (!selectedBicycleId) {
          alert("자전거 ID가 설정되지 않았습니다. 다시 시도해 주세요.");
          return;
        }

        const details = document.getElementById("brokenReportDetails").value;
        $.ajax({
          url: "/map/report-and-return",
          type: "POST",
          data: {
            customerId: customerId,
            bicycleId: selectedBicycleId,  // 자전거 ID를 bicycleId 파라미터로 전달
            categoryId: 2,  // 고장 신고로 설정
            reportDetails: details,
            returnBranchName: selectedBranchName,
            returnLatitude: selectedReturnLatitude,
            returnLongitude: selectedReturnLongitude
          },
          success: function(response) {
            alert(response);
            closeAllPopups();
          },
          error: function(xhr) {
            alert("신고 및 반납에 실패했습니다: " + xhr.responseText);
          }
        });
      }


      // 팝업 닫기
      function closePopup(popupId) {
        var popup = document.getElementById(popupId);
        if (popup) {
          popup.style.display = 'none';
        }
      }

      function closeAllPopups() {
        closePopup('branchInfoPopup');
        closePopup('reportPopup');
        closePopup('locationReportPopup');
        closePopup('brokenReportPopup');
        closePopup('customReturnPopup');
        closePopup('returnPopup');
      }

      function showCustomReturnPopup() {
        document.getElementById("customReturnPopup").style.display = 'block';
      }

      function showReturnPopup(isCustomLocation) {
        if (isCustomLocation) {
          document.getElementById("customReturnPopup").style.display = 'block';
        } else {
          document.getElementById("returnBranchName").innerText = selectedBranchName;

          // 대여 중인 자전거 정보 가져오기
          $.ajax({
            url: '/map/current-rentals',
            type: 'GET',
            data: { customerId: customerId },
            success: function(response) {
              console.log("Response data:", response); // 응답 데이터 전체 확인

              $('#bicycleid').text(response.bicycleId);
              $('#bicycleName').text(response.bicycleName);
              $('#rentalBranch').text(response.rentalBranch);
              $('#rentalStartDate').text(response.rentalStartDate.replace("T", " "));


                // 대여 중인 자전거 ID를 selectedBicycleId에 설정
                selectedBicycleId = response.bicycleId;
                console.error("대여 중인 자전거 정보가 없습니다.");
            },
            error: function(xhr) {
              console.error("대여 중인 자전거 정보 가져오기 실패:", xhr);
            }
          });

          // 팝업 표시
          document.getElementById("returnPopup").style.display = 'block';
        }
      }





      function returnBike(isCustomLocation) {
        var returnLatitude = isCustomLocation ? selectedReturnLatitude : selectedBranchLatitude;
        var returnLongitude = isCustomLocation ? selectedReturnLongitude : selectedBranchLongitude;
        var returnBranchName = isCustomLocation ? "기타" : selectedBranchName;  // 반납 대여소 이름 설정

        // 신고 내용을 확인하고 함께 전달
        var reportDetails = document.getElementById("locationReportDetails")?.value || '';
        var categoryId = reportDetails ? 1 : null; // 예시로 위치 신고로 설정

        $.ajax({
          url: '/map/return/bicycle',
          type: 'POST',
          data: {
            customerId: customerId,
            returnLatitude: returnLatitude,
            returnLongitude: returnLongitude,
            isCustomLocation: isCustomLocation,
            returnBranchName: returnBranchName,
            reportDetails: reportDetails,
            categoryId: categoryId,
            bicycleId: selectedBicycleId // 자전거 ID 추가
          },
          success: function() {
            alert("반납이 성공적으로 완료되었습니다.");
            closeAllPopups();
            window.location.href = "/pay/userPayment";
          },
          error: function(xhr) {
            alert("반납에 실패했습니다: " + xhr.responseText);
          }
        });
      }



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

              // 대여 버튼 추가
              var rentButton = document.createElement("button");
              rentButton.textContent = "대여";
              rentButton.style.marginRight = "10px";
              rentButton.onclick = function() {
                selectedBicycleId = bike.bicycleId; // 자전거 ID 설정
                rentBike(bike.bicycleId, customerId, selectedBranchName);
              };
              bikeElement.appendChild(rentButton);

              // 신고 버튼 추가
              var reportButton = document.createElement("button");
              reportButton.textContent = "신고";
              reportButton.onclick = function() {
                selectedBicycleId = bike.bicycleId; // 신고 시 자전거 ID 설정
                openBrokenReportPopup(); // 고장 신고 팝업 열기
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

      // 지도를 클릭할 때, 선택한 위치의 위도와 경도를 저장
      kakao.maps.event.addListener(map, 'click', async function(mouseEvent) {
        closeAllPopups();

        try {
          const isRented = await checkRentalStatus();
          if (isRented) {
            // 마우스로 클릭한 위치의 위도와 경도를 저장
            selectedReturnLatitude = mouseEvent.latLng.getLat();
            selectedReturnLongitude = mouseEvent.latLng.getLng();
            showCustomReturnPopup(); // 대여소 외부 반납 팝업 열기
          }
        } catch (error) {
          console.error("대여 상태 확인 중 오류 발생:", error);
        }
      });
    </script>
</div>
</body>
</html>