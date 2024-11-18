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
function showCustomReturnPopup(latitude, longitude) {
  // 전역 변수 설정 (이미 설정된 좌표를 사용하도록 보장)
  selectedReturnLatitude = latitude;
  selectedReturnLongitude = longitude;

  // 팝업 내용 업데이트
  $('#customReturnLocation').text(`위도: ${latitude}, 경도: ${longitude}`);
  $('#customReturnLocation').hide();
  // 대여 중인 자전거 정보 가져오기
  $.ajax({
    url: '/api/map/current-rentals',
    type: 'GET',
    success: function (response) {
      $('#customBicycleName').text(response.bicycleName);
      $('#customRentalBranch').text(response.rentalBranch);
      $('#customRentalStartDate').text(response.rentalStartDate.replace("T", " "));

      // 팝업 열기
      document.getElementById("customReturnPopup").style.display = 'block';
      isCustomReturnPopupOpen = true;
    },
    error: function (xhr) {
      if (xhr.status === 404) {
        alert("대여 중인 자전거 정보를 찾을 수 없습니다.");
      } else {
        alert("대여 정보를 불러오는 데 실패했습니다.");
      }
    },
  });
}


function loadRentalStatus() {
  $.ajax({
    url: '/api/map/rental/status',
    type: 'GET',
    success: function(response) {
      if (response.code === 200) {
        const rentalBranchName = response.rentalBranch || "기타";

        $('#rentedBicycleName').text(response.bicycleName);
        $('#rentalStartTime').text(response.rentalStartDate.replace("T", " "));
        $('#rentalBranchName').text(rentalBranchName);

        if (navigator.geolocation) {
          navigator.geolocation.getCurrentPosition(function(position) {
            currentLatitude = position.coords.latitude;
            currentLongitude = position.coords.longitude;
          });
        }

        document.getElementById("rentalStatusPopup").style.display = 'block';
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
      const lat = position.coords.latitude;
      const lon = position.coords.longitude;

      currentLatitude = lat;
      currentLongitude = lon;

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
        loadRentalStatus();
      }
    },
    error: function(xhr) {
      alert("서버 에러가 발생했습니다.");
      console.log(xhr);
    }
  });
}
let isReturnInProgress = false;
function returnBikeFromBranch() {
  if (isReturnInProgress) return;

  isReturnInProgress = true;

  const returnLatitude = selectedBranchLatitude;
  const returnLongitude = selectedBranchLongitude;
  const returnBranchName = selectedBranchName;

  // 대여소에서 반납 처리
  processReturn(returnLatitude, returnLongitude, returnBranchName, false);
}

function returnBikeFromCustomLocation() {
  if (isReturnInProgress) return;

  isReturnInProgress = true;

  // 사용자 지정 위치 좌표 사용
  const returnLatitude = selectedReturnLatitude;
  const returnLongitude = selectedReturnLongitude;

  processReturn(returnLatitude, returnLongitude, "기타", true);
}


function processReturn(latitude, longitude, branchName, isCustomLocation) {
  console.log("Process Return - Input Parameters:");
  console.log("Latitude:", latitude, "Longitude:", longitude);
  console.log("Branch Name:", branchName);
  console.log("Is Custom Location:", isCustomLocation);

  $.ajax({
    url: "/api/map/nearby-branch",
    type: "GET",
    data: { latitude: latitude, longitude: longitude },
    success: function (branchResponse) {
      const finalBranchName = branchResponse || branchName;

      $.ajax({
        url: "/api/map/return/bicycle",
        type: "POST",
        data: {
          returnLatitude: latitude,
          returnLongitude: longitude,
          isCustomLocation: finalBranchName === "기타",
          returnBranchName: finalBranchName,
        },
        success: function () {
          alert("반납이 성공적으로 완료되었습니다.");
          closeAllPopups();
          window.location.href = "/pay/payment";
        },
        error: function (xhr) {
          alert("반납에 실패했습니다: " + xhr.responseText);
        },
        complete: function () {
          isReturnInProgress = false;
        },
      });
    },
    error: function () {
      alert("근처 대여소 정보를 확인하지 못했습니다. 기본 위치로 반납됩니다.");
      $.ajax({
        url: "/api/map/return/bicycle",
        type: "POST",
        data: {
          returnLatitude: latitude,
          returnLongitude: longitude,
          isCustomLocation: true,
          returnBranchName: "기타",
        },
        success: function () {
          alert("반납이 성공적으로 완료되었습니다.");
          closeAllPopups();
          window.location.href = "/pay/payment";
        },
        error: function (xhr) {
          alert("반납에 실패했습니다: " + xhr.responseText);
        },
        complete: function () {
          isReturnInProgress = false;
        },
      });
    },
  });
}
function returnBikeFromStatus() {
  if (isReturnInProgress) return;

  isReturnInProgress = true;

  const returnLatitude = currentLatitude;
  const returnLongitude = currentLongitude;

  processReturn(returnLatitude, returnLongitude, "기타", false);
}


// 대여소 반납 팝업 표시 함수
function showReturnPopup() {
  document.getElementById("returnBranchName").innerText = selectedBranchName;

  $.ajax({
    url: '/api/map/current-rentals',
    type: 'GET',
    success: function (response) {
      $('#bicycleid').text(response.bicycleId);
      $('#bicycleName').text(response.bicycleName);
      $('#rentalBranch').text(response.rentalBranch);
      $('#rentalStartDate').text(response.rentalStartDate.replace("T", " "));
      selectedBicycleId = response.bicycleId;

      document.getElementById("returnPopup").style.display = 'block';
    },
    error: function (xhr) {
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
  closePopup('customReturnPopup');
  closePopup('returnPopup');
  document.getElementById("reportAndReturnPopup").style.display = 'block';
}

// 대여소 외 반납 팝업에서 "신고하기" 버튼 클릭 시 호출되는 함수
document.getElementById("customReturnPopup").querySelector(".report-button").addEventListener("click", openReportAndReturnPopup);

// 대여소 반납 팝업에서 "신고하기" 버튼 클릭 시 호출되는 함수
document.getElementById("returnPopup").querySelector(".report-button").addEventListener("click", openReportAndReturnPopup);

function submitReport() {
  const categoryId = document.getElementById("reportCategorySelect1").value;
  const reportDetails = document.getElementById("reportDetails1").value;

  $.ajax({
    url: '/api/map/report-issue',
    type: 'POST',
    data: { bicycleId: selectedBicycleId, categoryId: categoryId, reportDetails: reportDetails },
    success: function(response) {
      if (response.code === 403) {
        alert(response.msg);
        window.location.href = "/pay/payment";
      } else {
        alert(response.msg);
        closePopup('dynamicReportPopup');
        if (response.redirectToPayment) {
          window.location.href = "/pay/payment";
        }
      }
    },
    error: function() {
      alert("서버 에러가 발생했습니다. 신고 접수에 실패했습니다.");
      closePopup('dynamicReportPopup');
    }
  });
}

// 신고 및 반납 처리 함수
function submitReportAndReturn(isCustomLocation = false) {
  const categoryId = document.getElementById("reportCategorySelect2").value || null;
  const reportDetails = document.getElementById("reportDetails2").value || "";

  if (!categoryId && !isCustomLocation) {
    alert("신고할 내용이 없으면 반납만 진행됩니다.");
  }

  const returnLatitude = isCustomLocation ? selectedReturnLatitude : currentLatitude;
  const returnLongitude = isCustomLocation ? selectedReturnLongitude : currentLongitude;

  $.ajax({
    url: "/api/map/nearby-branch",
    type: "GET",
    data: { latitude: returnLatitude, longitude: returnLongitude },
    success: function (branchResponse) {
      const nearbyBranchName = branchResponse || "기타";

      const requestData = {
        bicycleId: selectedBicycleId,
        categoryId: categoryId,
        reportDetails: reportDetails,
        returnBranchName: nearbyBranchName,
        returnLatitude: returnLatitude,
        returnLongitude: returnLongitude,
        isCustomLocation: (nearbyBranchName === "기타"),
      };

      $.ajax({
        url: "/api/map/report-and-return",
        type: "POST",
        data: requestData,
        success: function (response) {
          alert(response.msg || "신고 및 반납이 완료되었습니다.");
          closeAllPopups(); // 모든 팝업 닫기
          closePopup('rentalStatusPopup'); // 현황판 팝업 닫기
          if (response.redirectToPayment) {
            window.location.href = "/pay/payment";
          }
        },
        error: function (xhr) {
          alert("반납 또는 신고 처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
        },
      });
    },
    error: function () {
      alert("근처 대여소 정보를 가져올 수 없습니다. 기본 위치로 반납됩니다.");

      const fallbackRequestData = {
        bicycleId: selectedBicycleId,
        categoryId: categoryId,
        reportDetails: reportDetails,
        returnBranchName: "기타",
        returnLatitude: returnLatitude,
        returnLongitude: returnLongitude,
        isCustomLocation: true,
      };

      $.ajax({
        url: "/api/map/report-and-return",
        type: "POST",
        data: fallbackRequestData,
        success: function (response) {
          alert(response.msg || "신고 및 반납이 완료되었습니다.");
          closeAllPopups(); // 모든 팝업 닫기
          closePopup('rentalStatusPopup'); // 현황판 팝업 닫기
          if (response.redirectToPayment) {
            window.location.href = "/pay/payment";
          }
        },
        error: function (xhr) {
          alert("반납 또는 신고 처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
        },
      });
    },
  });
}

// 현황판 반납 및 신고 이벤트 리스너
document.getElementById("rentalStatusPopup").querySelector(".report-button").addEventListener("click", function () {
  document.getElementById("currentLatitude").innerText = initialLatitude;
  document.getElementById("currentLongitude").innerText = initialLongitude;

  openReportPopup();

  document.getElementById("reportAndReturnPopup").querySelector(".report-submit").onclick = function () {
    submitReportAndReturn(false);
  };
});

// 대여소 외 위치에서 반납 및 신고 이벤트 리스너
document.getElementById("customReturnPopup").querySelector(".report-button").addEventListener("click", function () {
  openReportAndReturnPopup();

  document.getElementById("reportAndReturnPopup").querySelector(".report-submit").onclick = function () {
    submitReportAndReturn(true);
  };
});

let initialLatitude = 0;
let initialLongitude = 0;

if (navigator.geolocation) {
  navigator.geolocation.getCurrentPosition(function (position) {
    initialLatitude = position.coords.latitude;
    initialLongitude = position.coords.longitude;
  }, function (error) {
    console.error("위치 정보를 가져오는 데 실패했습니다:", error);
  });
} else {
  console.warn("이 브라우저에서는 위치 정보를 사용할 수 없습니다.");
}

// 지도 외부 클릭 시 대여소 외 위치 반납 팝업 표시
async function handleMapClickOutsideBranch(latitude, longitude) {
  selectedReturnLatitude = latitude;
  selectedReturnLongitude = longitude;

  if (isBranchInfoPopupOpen) {
    closePopup('branchInfoPopup');
  }

  const isRented = await checkRentalStatus();
  if (isRented) {
    showCustomReturnPopup(latitude, longitude);
  } else {
    console.log("대여 중이 아닙니다.");
  }
}

// 대여소 클릭 이벤트 처리
async function handleBranchClick(latitude, longitude) {
  closePopup('customReturnPopup'); // 대여소 외 팝업 닫기

  // 대여소 위치 업데이트
  selectedBranchLatitude = latitude;
  selectedBranchLongitude = longitude;

  const isRented = await checkRentalStatus();
  if (isRented) {
    showReturnPopup(); // 대여소 반납 팝업 열기
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
      selectedBicycleId = response.bicycleId;
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
    loadRentalStatus();
  }
});

// 지도 초기화 및 내 위치 표시 관련 코드
let myLocationMarker = null;

const container = document.getElementById('map');
const options = { center: new kakao.maps.LatLng(37.583883601891, 126.9999880311), level: 3 };
const main = new kakao.maps.Map(container, options);

function showMyLocationOnMap(lat, lon) {
  if (myLocationMarker) {
    myLocationMarker.setMap(null);
  }

  const myLocationImageSrc = '/images/mylocation.png';
  const myLocationImageSize = new kakao.maps.Size(30, 30);
  const myLocationImageOption = { offset: new kakao.maps.Point(12, 24) };

  const myLocationImage = new kakao.maps.MarkerImage(myLocationImageSrc, myLocationImageSize, myLocationImageOption);

  myLocationMarker = new kakao.maps.Marker({
    position: new kakao.maps.LatLng(lat, lon),
    map: main,
    image: myLocationImage,
    zIndex: 2,
    clickable: false
  });

  main.setCenter(new kakao.maps.LatLng(lat, lon));
}

function moveToMyLocation() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(function(position) {
      currentLatitude = position.coords.latitude;
      currentLongitude = position.coords.longitude;
      showMyLocationOnMap(currentLatitude, currentLongitude);
    }, function() {
      alert("위치 정보를 가져올 수 없습니다.");
    });
  } else {
    alert("이 브라우저에서는 위치 정보를 사용할 수 없습니다.");
  }
}

if (navigator.geolocation) {
  navigator.geolocation.getCurrentPosition(function (position) {
    initialLatitude = position.coords.latitude;
    initialLongitude = position.coords.longitude;

    currentLatitude = initialLatitude;
    currentLongitude = initialLongitude;

    showMyLocationOnMap(initialLatitude, initialLongitude);
  }, function () {
    alert("위치 정보를 가져올 수 없습니다.");
  });
} else {
  alert("이 브라우저에서는 위치 정보를 사용할 수 없습니다.");
}

// 대여소 마커 관리 배열
let branchMarkers = [];

function loadBranches() {
  $.ajax({
    url: "/api/map/branches",
    method: "GET",
    success: function(data) {
      data.forEach(function(branch) {
        const markerPosition = new kakao.maps.LatLng(branch.latitude, branch.longitude);
        const markerImage = new kakao.maps.MarkerImage('/images/bicycling.png', new kakao.maps.Size(50, 50), { offset: new kakao.maps.Point(25, 25) });

        const marker = new kakao.maps.Marker({
          position: markerPosition,
          map: main,
          image: markerImage,
          zIndex: 1
        });

        branchMarkers.push(marker);

        kakao.maps.event.addListener(marker, 'click', function() {
          selectedBranchName = branch.branchName;
          selectedBranchLatitude = branch.latitude;
          selectedBranchLongitude = branch.longitude;
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

// 지도 클릭 이벤트로 대여소 외 반납 팝업 표시
kakao.maps.event.addListener(main, 'click', function (mouseEvent) {
  closeAllPopups();

  const clickedLatLng = mouseEvent.latLng;
  const clickedLatitude = clickedLatLng.getLat();
  const clickedLongitude = clickedLatLng.getLng();

  selectedReturnLatitude = clickedLatitude;
  selectedReturnLongitude = clickedLongitude;

  checkRentalStatus().then((isRented) => {
    if (isRented) {
      showCustomReturnPopup(clickedLatitude, clickedLongitude);
    }
  });
});


function updateRentalStatusLocation() {
  const rentalLatitudeElement = document.getElementById("currentLatitude");
  const rentalLongitudeElement = document.getElementById("currentLongitude");

  if (rentalLatitudeElement && rentalLongitudeElement) {
    rentalLatitudeElement.innerText = currentLatitude;
    rentalLongitudeElement.innerText = currentLongitude;
  }
}

$(document).ready(function() {
  updateRentalStatusLocation();

  const locateMeButton = document.createElement('button');
  locateMeButton.id = 'locateMeButton';
  locateMeButton.onclick = moveToMyLocation;

  const img = document.createElement('img');
  img.src = '/images/location.png';
  img.alt = '내 위치로 이동';
  img.style.width = '100%';
  img.style.height = '100%';
  locateMeButton.appendChild(img);

  document.getElementById('map').appendChild(locateMeButton);
});

loadBranches();

