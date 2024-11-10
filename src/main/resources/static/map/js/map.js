var selectedBranchName = '';
var selectedBranchLatitude = 0;
var selectedBranchLongitude = 0;
var selectedBicycleId = 0;
var selectedReturnLatitude = 0;
var selectedReturnLongitude = 0;
let isCustomReturnPopupOpen = false;
let isBranchInfoPopupOpen = false;

// 팝업 닫기 함수
function closePopup(popupId) {
  var popup = document.getElementById(popupId);
  if (popup) {
    popup.style.display = 'none';
  }
  if (popupId === 'customReturnPopup') {
    isCustomReturnPopupOpen = false;
  }
  if (popupId === 'branchInfoPopup') {
    isBranchInfoPopupOpen = false;
  }
}

// 모든 팝업 닫기 함수
function closeAllPopups() {
  closePopup('branchInfoPopup');
  closePopup('dynamicReportPopup');
  closePopup('customReturnPopup');
  closePopup('returnPopup');
  isCustomReturnPopupOpen = false;
  isBranchInfoPopupOpen = false;
}

// 대여소 팝업 표시 함수
function showBranchInfoPopup() {
  document.getElementById("branchInfoPopup").style.display = 'block';
  isBranchInfoPopupOpen = true;
}

// 대여소 외 지역 반납 팝업 표시 함수
function showCustomReturnPopup() {
  $.ajax({
    url: '/api/map/current-rentals',
    type: 'GET',
    success: function(response) {
      $('#customBicycleName').text(response.bicycleName);
      $('#customRentalBranch').text(response.rentalBranch);
      $('#customRentalStartDate').text(response.rentalStartDate.replace("T", " "));
      document.getElementById("customReturnPopup").style.display = 'block';
      isCustomReturnPopupOpen = true;
    },
    error: function(xhr) {
      console.error("대여 중인 자전거 정보 가져오기 실패:", xhr);
      alert("대여 정보를 불러오는 데 실패했습니다.");
    }
  });
}

// 자전거 대여 함수
function rentBike(bicycleId, rentalBranch) {
  $.ajax({
    url: '/api/map/rent/bicycle',
    type: 'POST',
    data: { bicycleId: bicycleId, rentalBranch: rentalBranch },
    success: function() {
      alert("자전거의 대여가 완료되었습니다.");
      closeAllPopups();
    },
    error: function(xhr) {
      const errorMessage = xhr.responseText || xhr.statusText;
      if (errorMessage.includes("결제되지 않은 자전거가 있습니다")) {
        alert(errorMessage);
        window.location.href = "/pay/payment";
      } else if (errorMessage.includes("5분 이내에는 신고가 가능합니다")) {
        alert(errorMessage);
      } else {
        alert("대여에 실패했습니다: " + errorMessage);
      }
    }
  });
}

// 자전거 반납 함수
function returnBike(isCustomLocation) {
  var returnLatitude = isCustomLocation ? selectedReturnLatitude : selectedBranchLatitude;
  var returnLongitude = isCustomLocation ? selectedReturnLongitude : selectedBranchLongitude;
  var returnBranchName = isCustomLocation ? "기타" : selectedBranchName;

  $.ajax({
    url: '/api/map/return/bicycle',
    type: 'POST',
    data: {
      returnLatitude: returnLatitude,
      returnLongitude: returnLongitude,
      isCustomLocation: isCustomLocation,
      returnBranchName: returnBranchName
    },
    success: function() {
      alert("반납이 성공적으로 완료되었습니다.");
      closeAllPopups();
      window.location.href = "/pay/payment";
    },
    error: function(xhr) {
      alert("반납에 실패했습니다: " + xhr.responseText);
    }
  });
}
// 대여소 반납 팝업 표시 함수
function showReturnPopup(isCustomLocation) {
  if (isCustomLocation) {
    showCustomReturnPopup(); // 대여소 외 위치에서의 반납 팝업
  } else {
    document.getElementById("returnBranchName").innerText = selectedBranchName;

    $.ajax({
      url: '/api/map/current-rentals',
      type: 'GET',
      success: function(response) {
        $('#bicycleid').text(response.bicycleId);
        $('#bicycleName').text(response.bicycleName);
        $('#rentalBranch').text(response.rentalBranch);
        $('#rentalStartDate').text(response.rentalStartDate.replace("T", " "));
        selectedBicycleId = response.bicycleId;
        document.getElementById("returnPopup").style.display = 'block'; // 반납 팝업 표시
      },
      error: function(xhr) {
        console.error("대여 중인 자전거 정보 가져오기 실패:", xhr);
        alert("대여 정보를 불러오는 데 실패했습니다.");
      }
    });
  }
}



// 카테고리 옵션 로딩 함수
function loadReportCategories(selectId) {
  $.ajax({
    url: '/api/map/report-categories',
    method: 'GET',
    success: function(categories) {
      const categorySelect = document.getElementById(selectId);
      categorySelect.innerHTML = '';
      categories.forEach(category => {
        const option = document.createElement("option");
        option.value = category.categoryId;
        option.textContent = category.categoryName;
        categorySelect.appendChild(option);
      });
    },
    error: function() {
      alert("신고 카테고리 로딩에 실패했습니다.");
    }
  });
}

// 신고 팝업 열기 함수
function openReportPopup() {
  loadReportCategories('reportCategorySelect1');
  document.getElementById("reportDetails1").value = "";
  document.getElementById("dynamicReportPopup").style.display = 'block';
}

// 신고 및 반납 팝업 열기 함수
function openReportAndReturnPopup() {
  loadReportCategories('reportCategorySelect2');
  document.getElementById("reportDetails2").value = "";
  document.getElementById("reportAndReturnPopup").style.display = 'block';
}

// 신고 제출 함수
function submitReport() {
  const categoryId = document.getElementById("reportCategorySelect1").value;
  const reportDetails = document.getElementById("reportDetails1").value;

  $.ajax({
    url: '/api/map/report-issue',
    type: 'POST',
    data: { bicycleId: selectedBicycleId, categoryId: categoryId, reportDetails: reportDetails },
    success: function() {
      alert("신고가 성공적으로 접수되었습니다.");
      closePopup('dynamicReportPopup');
    },
    error: function(xhr) {
      alert("신고 처리에 실패했습니다: " + xhr.responseText);
      closePopup('dynamicReportPopup');
    }
  });
}

// 신고 및 반납 제출 함수
function submitReportAndReturn() {
  const categoryId = document.getElementById("reportCategorySelect2").value;
  const reportDetails = document.getElementById("reportDetails2").value;
  const returnBranchName = isCustomReturnPopupOpen ? "기타" : selectedBranchName;

  console.log("Submitting Report and Return:");
  console.log("Category ID:", categoryId);
  console.log("Report Details:", reportDetails);
  console.log("Return Branch Name:", returnBranchName);
  console.log("Return Latitude:", selectedBranchLatitude);
  console.log("Return Longitude:", selectedBranchLongitude);

  $.ajax({
    url: '/api/map/report-and-return',
    type: 'POST',
    data: {
      bicycleId: selectedBicycleId,
      categoryId: categoryId,
      reportDetails: reportDetails,
      returnBranchName: returnBranchName,
      returnLatitude: selectedBranchLatitude,
      returnLongitude: selectedBranchLongitude
    },
    success: function() {
      alert("신고 및 반납이 성공적으로 처리되었습니다.");
      closePopup('reportAndReturnPopup');
    },
    error: function(xhr) {
      alert("신고 및 반납 처리에 실패했습니다: " + xhr.responseText);
    }
  });
}



// 지도 외부 클릭 시 대여소 외 위치 반납 팝업 표시
async function handleMapClickOutsideBranch(latitude, longitude) {
  selectedReturnLatitude = latitude;
  selectedReturnLongitude = longitude;

  // 대여소 정보 팝업이 열려 있으면 닫음
  if (isBranchInfoPopupOpen) {
    closePopup('branchInfoPopup');
  }

  // 대여 중인지 확인하고, 대여 중일 때만 반납 팝업을 표시
  const isRented = await checkRentalStatus(); // 대여 상태 확인
  if (isRented) {
    showCustomReturnPopup(); // 대여 중일 때만 반납 팝업 호출
  } else {
    console.log("대여 중이 아닙니다."); // 대여 중이 아닐 때는 다른 동작 수행 가능
  }
}


// 대여소 클릭 시 이벤트 처리
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

// 대여소에서 사용 가능한 자전거 개수 로딩
function getAvailableBikesAtLocation(latitude, longitude) {
  $.ajax({
    url: "/api/map/available/bikes/location",
    method: "GET",
    data: { latitude: latitude, longitude: longitude },
    success: function(count) {
      document.getElementById("availableBikes").innerText = count;
    },
    error: function(xhr) {
      console.error("자전거 개수 불러오기 실패:", xhr);
    }
  });
}

// 대여소에서 사용 가능한 자전거 목록 로딩 및 표시
function showAvailableBicycles(latitude, longitude) {
  $.ajax({
    url: "/api/map/available/bikes",
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
        var rentButton = document.createElement("button");
        rentButton.textContent = "대여";
        rentButton.style.marginRight = "10px";
        rentButton.onclick = function() {
          selectedBicycleId = bike.bicycleId;
          rentBike(bike.bicycleId, selectedBranchName);
        };
        bikeElement.appendChild(rentButton);
        var reportButton = document.createElement("button");
        reportButton.textContent = "신고";
        reportButton.onclick = function() {
          selectedBicycleId = bike.bicycleId;
          closePopup('branchInfoPopup');
          openReportPopup();
        };
        bikeElement.appendChild(reportButton);
        container.appendChild(bikeElement);
      });
    },
    error: function(xhr) {
      console.error("자전거 목록 불러오기 실패:", xhr);
    }
  });
}

// 대여 상태 확인 함수
async function checkRentalStatus() {
  try {
    const response = await $.ajax({ url: "/api/map/check-rental-status", method: "GET" });
    return response && response.rentalStatus === "0";
  } catch (error) {
    console.error("Error checking rental status:", error);
    return false;
  }
}

// 카카오 지도 초기화
var container = document.getElementById('map');
var options = { center: new kakao.maps.LatLng(37.583883601891, 126.9999880311), level: 3 };
var map = new kakao.maps.Map(container, options);

// 일반 지도와 스카이뷰로 지도 타입을 전환할 수 있는 지도타입 컨트롤을 생성합니다
var mapTypeControl = new kakao.maps.MapTypeControl();

// 지도에 컨트롤을 추가해야 지도위에 표시됩니다
// kakao.maps.ControlPosition은 컨트롤이 표시될 위치를 정의하는데 TOPRIGHT는 오른쪽 위를 의미합니다
map.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT);

// 지도 확대 축소를 제어할 수 있는  줌 컨트롤을 생성합니다
var zoomControl = new kakao.maps.ZoomControl();
map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);

// 마커 이미지 설정
var imageSrc = '/images/자전거.png',
    imageSize = new kakao.maps.Size(40, 40),
    imageOption = { offset: new kakao.maps.Point(20, 20) };
var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);

// 사용자의 현재 위치를 중심으로 지도 설정
if (navigator.geolocation) {
  navigator.geolocation.getCurrentPosition(function(position) {
    var lat = position.coords.latitude, // 위도
        lon = position.coords.longitude; // 경도

    var locPosition = new kakao.maps.LatLng(lat, lon);
    displayMarker(locPosition, '<div style="padding:5px;">현재 위치입니다.</div>'); // 사용자 위치에 마커 표시
  });
} else {
  var locPosition = new kakao.maps.LatLng(37.583883601891, 126.9999880311); // 기본 위치 설정 (혜화)
  displayMarker(locPosition, 'geolocation을 사용할 수 없어요..');
}

// 클릭 이벤트로 모든 팝업 닫기
kakao.maps.event.addListener(map, 'click', function(mouseEvent) {
  closeAllPopups();
  const clickedLatLng = mouseEvent.latLng;
  handleMapClickOutsideBranch(clickedLatLng.getLat(), clickedLatLng.getLng());
});

// 대여소 마커 로드 함수
function loadBranches() {
  $.ajax({
    url: "/api/map/branches",
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

          // 선택된 대여소 정보 확인
          console.log("Selected Branch Name:", selectedBranchName);
          console.log("Selected Branch Latitude:", selectedBranchLatitude);
          console.log("Selected Branch Longitude:", selectedBranchLongitude);

          document.getElementById("branchName").innerText = selectedBranchName;
          handleBranchClick(branch.latitude, branch.longitude);
        });
      });
    },
    error: function(xhr) {
      console.error("대여소 데이터 불러오기 실패:", xhr);
    }
  });
}

// 마커와 정보창 표시 함수
function displayMarker(locPosition, message) {
  var marker = new kakao.maps.Marker({
    map: map,
    position: locPosition
  });

  var infowindow = new kakao.maps.InfoWindow({
    content: message,
    removable: true
  });

  infowindow.open(map, marker);
  map.setCenter(locPosition);
}

// 페이지 로드 시 대여소 로드
loadBranches();