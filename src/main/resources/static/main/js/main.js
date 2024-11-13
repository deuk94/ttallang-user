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
  closePopup('reportAndReturnPopup');
  isCustomReturnPopupOpen = false;
  isBranchInfoPopupOpen = false;
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
      if (xhr.status === 404) {
        alert("대여 중인 자전거 정보를 찾을 수 없습니다.");
      } else {
        alert("대여 정보를 불러오는 데 실패했습니다.");
      }
    }
  });
}
function loadRentalStatus() {
  $.ajax({
    url: '/api/map/rental/status',  // 대여 현황 정보 API
    type: 'GET',
    success: function(response) {
      if (response.code === 200) {
        const rentalBranchName = response.rentalBranch ? response.rentalBranch : "기타";

        $('#rentedBicycleName').text(response.bicycleName);
        $('#rentalStartTime').text(response.rentalStartDate.replace("T", " "));
        $('#rentalBranchName').text(rentalBranchName);

        if (rentalBranchName === "기타") {
          $('#customLocationText').show();
        } else {
          $('#customLocationText').hide();
        }

        document.getElementById("rentalStatusPopup").style.display = 'block';

        // 현황판 위치를 현재 위치로 업데이트
        updateCurrentLocationOnStatusPopup();
      } else {
        alert(response.msg);
      }
    },
    error: function(xhr) {
      alert("서버 오류가 발생했습니다. 대여 현황을 불러올 수 없습니다.");
      console.log(xhr);
    }
  });
}


function updateCurrentLocationOnStatusPopup() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(function(position) {
      const lat = position.coords.latitude; // 현재 위도
      const lon = position.coords.longitude; // 현재 경도

      // 전역 변수 업데이트 (필요 시)
      currentLatitude = lat;
      currentLongitude = lon;

      // 현황판 위치 정보 업데이트
      $('#currentLatitude').text(lat);
      $('#currentLongitude').text(lon);
    }, function(error) {
      console.error("위치 정보를 가져오는 데 실패했습니다:", error);
      alert("위치 정보를 가져올 수 없습니다. 위치 접근을 허용했는지 확인하세요.");
    });
  } else {
    alert("이 브라우저에서는 위치 정보를 사용할 수 없습니다.");
  }
}


// 자전거 대여 함수
function rentBike(bicycleId, rentalBranch) {
  $.ajax({
    url: '/api/map/rent/bicycle',
    type: 'POST',
    data: { bicycleId: bicycleId, rentalBranch: rentalBranch },
    success: function(response) {
      if (response.code !== 200) {
        alert(response.msg);
        window.location.href = "/pay/payment";
      } else {
        alert("자전거의 대여가 완료되었습니다.");
        closeAllPopups();

        // 대여 완료 후 대여 현황을 표시하기 위해 호출
        loadRentalStatus();
      }
    },
    error: function(xhr) {
      alert("서버 에러가 발생했습니다.");
      console.log(xhr);
    }
  });
}




let isReturnInProgress = false;  // 반납이 진행 중인지 확인하는 변수

// 자전거 반납 함수
function returnBike() {
  if (isReturnInProgress) {
    // 이미 반납이 진행 중이면 중복 실행 방지
    return;
  }

  isReturnInProgress = true;  // 반납 시작

  var returnLatitude = currentLatitude;
  var returnLongitude = currentLongitude;

  // 근처 대여소가 있는지 확인하는 로직 추가
  var returnBranchName = "기타";  // 기본값 "기타"로 설정
  $.ajax({
    url: "/api/map/branches/nearby", // 근처 대여소 확인 API 호출 (현재 위치 기준)
    method: "GET",
    data: {
      latitude: returnLatitude,
      longitude: returnLongitude,
      radius: 0.0009 // 100미터 반경 (위도, 경도 근사값)
    },
    success: function(branchResponse) {
      // 근처 대여소가 있으면 해당 대여소 이름을 설정
      if (branchResponse && branchResponse.name) {
        returnBranchName = branchResponse.name;
      }

      // 반납 요청 처리
      $.ajax({
        url: '/api/map/return/bicycle',
        type: 'POST',
        data: {
          returnLatitude: returnLatitude,
          returnLongitude: returnLongitude,
          isCustomLocation: (returnBranchName === "기타"),
          returnBranchName: returnBranchName
        },
        success: function(response) {
          alert("반납이 성공적으로 완료되었습니다.");
          closeAllPopups();
          window.location.href = "/pay/payment"; // 결제 페이지로 이동
        },
        error: function(xhr) {
          if (xhr.status === 400) {
            alert("반납할 위치 정보를 찾을 수 없습니다.");
          } else {
            alert("반납에 실패했습니다: " + xhr.responseText);
          }
        },
        complete: function() {
          isReturnInProgress = false;  // 반납 완료 후 상태 리셋
        }
      });
    },
    error: function(xhr) {
      console.error("근처 대여소를 찾는 데 실패했습니다:", xhr);
      // 근처 대여소 조회 실패 시 기본값 "기타"로 반납
      $.ajax({
        url: '/api/map/return/bicycle',
        type: 'POST',
        data: {
          returnLatitude: returnLatitude,
          returnLongitude: returnLongitude,
          isCustomLocation: true, // 기본적으로 "기타"로 설정
          returnBranchName: "기타"
        },
        success: function(response) {
          alert("반납이 성공적으로 완료되었습니다.");
          closeAllPopups();
          window.location.href = "/pay/payment"; // 결제 페이지로 이동
        },
        error: function(xhr) {
          if (xhr.status === 400) {
            alert("반납할 위치 정보를 찾을 수 없습니다.");
          } else {
            alert("반납에 실패했습니다: " + xhr.responseText);
          }
        },
        complete: function() {
          isReturnInProgress = false;  // 반납 완료 후 상태 리셋
        }
      });
    }
  });
}

// 현황판의 반납하기 버튼 클릭 이벤트
document.getElementById("rentalStatusPopup").querySelector(".return-button").addEventListener("click", returnBike);



// 대여소 반납 팝업 표시 함수
function showReturnPopup(isCustomLocation) {
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
      document.getElementById("returnPopup").style.display = 'block';
    },
    error: function(xhr) {
      if (xhr.status === 404) {
        alert("대여 중인 자전거를 찾을 수 없습니다.");
      } else {
        alert("대여 정보를 불러오는 데 실패했습니다.");
      }
    }
  });
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

function submitReport() {
  const categoryId = document.getElementById("reportCategorySelect1").value;
  const reportDetails = document.getElementById("reportDetails1").value;

  $.ajax({
    url: '/api/map/report-issue',
    type: 'POST',
    data: { bicycleId: selectedBicycleId, categoryId: categoryId, reportDetails: reportDetails },
    success: function(response) {
      if (response.code === 403) {  // 미결제 상태로 신고 불가
        alert(response.msg);  // 결제 필요 메시지 표시
        window.location.href = "/pay/payment";  // 결제 페이지로 이동
      } else {
        alert(response.msg);
        closePopup('dynamicReportPopup');
        if (response.redirectToPayment) {
          window.location.href = "/pay/payment";
        }
      }
    },
    error: function(xhr) {
      alert("서버 에러가 발생했습니다. 신고 접수에 실패했습니다.");
      closePopup('dynamicReportPopup');
    }
  });
}



function submitReportAndReturn() {
  const categoryId = document.getElementById("reportCategorySelect2").value;
  const reportDetails = document.getElementById("reportDetails2").value;

  // 현 위치를 사용하여 반납 처리
  const returnLatitude = currentLatitude;
  const returnLongitude = currentLongitude;

  // 두 위치가 근사치로 일치하면 대여소 위치를 사용, 그렇지 않으면 "기타"로 설정
  const isSameLocation = Math.abs(selectedBranchLatitude - returnLatitude) < 0.0009 && Math.abs(selectedBranchLongitude - returnLongitude) < 0.0009;

  const returnBranchName = isSameLocation ? selectedBranchName : "기타";

  console.log("bicycleId :", selectedBicycleId);
  console.log("categoryId :", categoryId);
  console.log("reportDetails :", reportDetails);
  console.log("returnBranchName :", returnBranchName);
  console.log("returnLatitude :", returnLatitude);
  console.log("returnLongitude :", returnLongitude);

  // 100미터 반경 내 대여소 이름을 가져옴
  $.ajax({
    url: '/api/map/nearby-branch',
    type: 'GET',
    data: {
      latitude: returnLatitude,
      longitude: returnLongitude
    },
    success: function(response) {
      // 대여소 이름이 있으면 사용하고 없으면 "기타" 사용
      const nearbyBranchName = response || "기타";

      $.ajax({
        url: '/api/map/report-and-return',
        type: 'POST',
        data: {
          bicycleId: selectedBicycleId,
          categoryId: categoryId,
          reportDetails: reportDetails,
          returnBranchName: nearbyBranchName,
          returnLatitude: returnLatitude,
          returnLongitude: returnLongitude
        },
        success: function(response) {
          alert(response.msg);
          if (response.code === 200) {
            closePopup('reportAndReturnPopup'); // 신고 및 반납 팝업 닫기
            closePopup('rentalStatusPopup'); // 현황판 팝업 닫기
            if (response.redirectToPayment) {
              window.location.href = "/pay/payment";
            }
          }
        },
        error: function(xhr) {
          closePopup('reportAndReturnPopup');
          if (xhr.status === 500) {
            alert("서버 오류가 발생했습니다. 다시 시도해 주세요.");
          } else {
            alert("서버 오류가 발생했습니다. 다시 시도해 주세요.");
          }
          console.log(xhr);
        }
      });
    },
    error: function(xhr) {
      alert("서버 오류가 발생했습니다. 대여소 정보를 가져올 수 없습니다.");
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


  currentLatitude = latitude;
  currentLongitude = longitude;
  updateRentalStatusLocation(); // 현황판의 위치 정보 업데이트

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
        rentButton.style.backgroundColor = "#001f54";
        rentButton.style.color = "white";
        rentButton.style.marginRight = "10px";
        rentButton.onclick = function() {
          selectedBicycleId = bike.bicycleId;
          rentBike(bike.bicycleId, selectedBranchName);
        };
        bikeElement.appendChild(rentButton);

        var reportButton = document.createElement("button");
        reportButton.textContent = "신고";
        reportButton.style.backgroundColor = "#001f54";
        reportButton.style.color = "white";
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
    if (response && response.rentalStatus === "0") {
      selectedBicycleId = response.bicycleId; // 자전거 ID 설정
      return true;
    }
    return false;
  } catch (error) {
    console.error("Error checking rental status:", error);
    return false;
  }
}

$(document).ready(async function() {
  const isRented = await checkRentalStatus();
  if (isRented) {
    loadRentalStatus(); // 대여 중인 상태라면 대여 현황 팝업 표시
  }
});



// 전역 변수로 현재 위치(위도, 경도)를 관리
let currentLatitude = 0;
let currentLongitude = 0;

// 카카오 지도 초기화
var container = document.getElementById('map');
var options = { center: new kakao.maps.LatLng(37.583883601891, 126.9999880311), level: 3 };
var main = new kakao.maps.Map(container, options);

// 마커 이미지 설정
var imageSrc = '/images/bicycle.svg',
    imageSize = new kakao.maps.Size(50, 50),
    imageOption = { offset: new kakao.maps.Point(20, 20) };
var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);

// 사용자의 현재 위치를 중심으로 지도 설정
if (navigator.geolocation) {
  navigator.geolocation.getCurrentPosition(function(position) {
    var lat = position.coords.latitude, // 위도
        lon = position.coords.longitude; // 경도

    currentLatitude = lat; // 초기 위치 설정
    currentLongitude = lon;

    var locPosition = new kakao.maps.LatLng(lat, lon);
    displayMarker(locPosition, '<div style="padding:5px;">현재 위치입니다.</div>'); // 사용자 위치에 마커 표시
    updateRentalStatusLocation(); // 현황판의 위치 업데이트
  });
} else {
  var locPosition = new kakao.maps.LatLng(37.583883601891, 126.9999880311); // 기본 위치 설정 (혜화)
  displayMarker(locPosition, 'geolocation을 사용할 수 없어요..');
}

// 지도 클릭 이벤트로 모든 팝업 닫기 및 위치 업데이트
kakao.maps.event.addListener(main, 'click', function(mouseEvent) {
  closeAllPopups();
  const clickedLatLng = mouseEvent.latLng;
  currentLatitude = clickedLatLng.getLat();
  currentLongitude = clickedLatLng.getLng();

  handleMapClickOutsideBranch(currentLatitude, currentLongitude);
  updateRentalStatusLocation(); // 현황판의 위치 업데이트
});

// 현황판의 위도, 경도 업데이트 함수
function updateRentalStatusLocation() {
  const rentalLatitudeElement = document.getElementById("currentLatitude");
  const rentalLongitudeElement = document.getElementById("currentLongitude");

  if (rentalLatitudeElement && rentalLongitudeElement) {
    rentalLatitudeElement.innerText = currentLatitude;
    rentalLongitudeElement.innerText = currentLongitude;
  }
}

// 대여소 마커 로드 함수
function loadBranches() {
  $.ajax({
    url: "/api/map/branches",
    method: "GET",
    success: function(data) {
      data.forEach(function(branch) {
        var marker = new kakao.maps.Marker({
          map: main,
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

// 초기 로드 시 현황판 업데이트
$(document).ready(function() {
  updateRentalStatusLocation(); // 페이지 로드 시 현황판에 초기 위치 정보 표시
});


// 마커와 정보창 표시 함수
function displayMarker(locPosition, message) {
  if (message !== '<div style="padding:5px;">현재 위치입니다.</div>') {
    var marker = new kakao.maps.Marker({
      map: main,
      position: locPosition
    });

    var infowindow = new kakao.maps.InfoWindow({
      content: message,
      removable: true
    });

    infowindow.open(main, marker);
    main.setCenter(locPosition);
  } else {
    // 사용자 위치만 센터로 설정
    main.setCenter(locPosition);
  }
}
$(document).ready(function() {
  // 페이지 로드 시 현황판의 위치를 현재 위치로 설정
  updateCurrentLocationOnStatusPopup();
});

// 페이지 로드 시 대여소 로드
loadBranches();