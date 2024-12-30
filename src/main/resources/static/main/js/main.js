let mapContainer = document.getElementById('map'), // 지도를 표시할 div
    mapOption = {
      center: new kakao.maps.LatLng(37.1998620452348, 127.095491383746), // 지도의 중심좌표
      level: 3 // 지도의 확대 레벨
    };
let map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성합니다
//내 위치 이미지 함수
function showMyLocationOnMap(lat, lon) {
  let myLocationMarker = null;
  const myLocationImageSrc = '/images/mylocation.png';
  const myLocationImageSize = new kakao.maps.Size(40, 40);
  const myLocationImageOption = { offset: new kakao.maps.Point(12, 24) };

  const myLocationImage = new kakao.maps.MarkerImage(myLocationImageSrc, myLocationImageSize, myLocationImageOption);

  myLocationMarker = new kakao.maps.Marker({
    position: new kakao.maps.LatLng(lat, lon),
    map: map,
    image: myLocationImage,
    zIndex: 2,
    clickable: false
  });

  map.setCenter(new kakao.maps.LatLng(lat, lon));
}
//팝업 닫기 함수
function closePopup(popupId) {
  let popup = document.getElementById(popupId);
  if (popup) {
    popup.style.display = 'none';
  }
}
// 모든 팝업 닫기 함수
function closeAllPopups() {
  closePopup('branchInfoPopup');
  closePopup('returnPopup');
  closePopup('reportPopup');
}
//대여소 조회
function loadBranches() {
  // 대여소 마커 관리 배열
  let branchMarkers = [];
  $.ajax({
    url: "/api/map/branches",
    method: "GET",
    success: function(data) {
      data.forEach(function(branch) {
        const markerPosition = new kakao.maps.LatLng(branch.latitude, branch.longitude);
        const markerImage = new kakao.maps.MarkerImage('/images/bicycling.png', new kakao.maps.Size(40, 40), { offset: new kakao.maps.Point(25, 25) });
        const marker = new kakao.maps.Marker({
          position: markerPosition,
          map: map,
          image: markerImage,
          zIndex: 1
        });
        branchMarkers.push(marker);
        kakao.maps.event.addListener(marker, 'click', function() {
          selectedBranchName = branch.branchName;
          selectedBranchLatitude = branch.latitude;
          selectedBranchLongitude = branch.longitude;
          document.getElementById("branchName").innerText = selectedBranchName;
          branchClick(branch.latitude, branch.longitude);
        });
        checkRentalStatus(function (result) {
          if (result) {
            loadRentalStatus();
          }
        });
      });
    },
    error: function(xhr) {
      console.error("대여소 데이터 불러오기 실패:", xhr);
    }
  });
}
//대여중인지 확인 하는 함수
function checkRentalStatus(callback) {
  $.ajax({
    url: "/api/map/checkRentalStatus",
    method: "GET",
    success: function (data) {
      if (data === -1) {
        callback(false); // 대여중 x true 전달
      } else {
        callback(true); // 대여중 false 전달
      }
    },
    error: function (xhr) {
      alert("서버 오류");
    },
  });
}
//대여소 클릭했을 때 함수
function branchClick(latitude, longitude){
  checkRentalStatus(function (result) {
    if (result) {
      alert("현재 대여중입니다. 반납 후 이용하세요.");
    } else {
      showAvailableBicycles(latitude, longitude);
      document.getElementById("branchInfoPopup").style.display = 'block';
    }
  });
}
//대여소 안의 대여 가능한 자전거 보여주기
function showAvailableBicycles(latitude, longitude) {
  closeAllPopups();
  $.ajax({
    url: "/api/map/available/bicycle",
    method: "POST",
    contentType: "application/json",
    data: JSON.stringify({ latitude: latitude, longitude: longitude }),
    success: function (bicycles) {
      let bicyclelist = $("#bicycleListContainer");
      bicyclelist.empty();
      let count = 0;

      bicycles.forEach(function (bicycle) {
        const lat = latitude;
        const lon = longitude;

        bicyclelist.append(`
          <tr>
              <td>${bicycle.bicycleName}</td>
              <td>
                  <button onclick="rentBicycle('${bicycle.bicycleId}', selectedBranchName)">대여</button>
              </td>
              <td>
                  <button onclick="(function() { openReportPopup('${bicycle.bicycleId}', ${lat}, ${lon}); })()">신고</button>
              </td>
          </tr>
        `);
        count++;
      });
      $("#availableBikes").text(count);
    },
    error: function (xhr) {
      console.error("자전거 목록 불러오기 실패:", xhr);
    },
  });
}
//자건저 대여
function rentBicycle(bicycleId, rentalBranch) {
  $.ajax({
    url: '/api/map/rent/bicycle',
    type: 'POST',
    contentType: "application/json", // JSON 형태로 전송
    data: JSON.stringify({ bicycleId: bicycleId, rentalBranch: rentalBranch }),
    success: function(response) {
      alert("자전거의 대여가 완료되었습니다.");
      closePopup('branchInfoPopup');
      loadRentalStatus();
    },
    error: function(xhr) {
      if(xhr.status === 400){
        if(xhr.responseText === "NoPay"){
          alert("미결제 금액이 있습니다. 결제 페이지로 넘어갑니다.");
          window.location.href = "/pay/payment";
        }else if(xhr.responseText === "renting"){
          alert("이미 대여 중입니다. 대여 중에는 다른 자전거를 대여하지 못합니다.");
        }
      }else{
        alert("서버 에러가 발생했습니다.");
      }
    }
  });
}
//현황판 정보
function loadRentalStatus() {
  $.ajax({
    url: '/api/map/rental/status',
    type: 'GET',
    success: function(response) {
      const bicycleId = response.bicycleId;
      const rentalBranchName = response.rentalBranch;
      $('#rentedBicycleName').text(response.bicycleName);
      $('#rentalStartTime').text(response.rentalStartDate.replace("T", " "));
      $('#rentalBranchName').text(rentalBranchName);
      document.getElementById("rentalStatusPopup").style.display = 'block';
      $(document).on("click", ".report-button", function () {
        ReportAndReturn(bicycleId);
      });
    },
    error: function(xhr) {
      if(xhr.status === 400){
        if(xhr.responseText === "NoBicycle"){
          alert("자전가 정보 조회 실패");
        }else if(xhr.responseText === "NoRent"){
          alert("대여중인 자전거가 없습니다.")
        }
      }
      alert(xhr);
    }
  });
}

//현황판에서 반납하기 버튼을 눌렀을때
function returnBikeFromStatus() {
  alert("현황판 반납.")
  let returnLatitude = -1;
  let returnLongitude = -1;
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
        function (position) {
          returnLatitude = position.coords.latitude;
          returnLongitude = position.coords.longitude;
          returnBicycle(returnLatitude, returnLongitude);
        },
        function (error) {
          console.error("Geolocation 에러: ", error.message);
          alert("위치 정보를 가져오지 못했습니다.");
        }
    );
  } else {
    alert("이 브라우저는 Geolocation을 지원하지 않습니다.");
  }
}
//자전거 반납 함수
function returnBicycle(latitude, longitude) {

  $.ajax({
    url: "/api/map/nearBranch",
    type: "GET",
    data: { latitude: latitude, longitude: longitude },
    success: function (returnBranch) {
      if(returnBranch === "기타"){
        if(confirm("현재 장소가 반납 구역 밖입니다. 이 장소는 추가 비용 20000원이 부과됩니다."
            + "진짜 반납 하시겠습니까?") == true){
        }else{
          alert("취소되었습니다");
          loadRentalStatus();
          return;
        }
      }
      else{
        if(confirm("현재 반납 구역은 " + returnBranch + " 입니다. 반납하시겠습니까?") == true){
        }else{
          alert("취소되었습니다");
          loadRentalStatus();
          return;
        }
      }
      const finalReturnBranchName = returnBranch;
      $.ajax({
        url: "/api/map/return/bicycle",
        type: "POST",
        data: {
          returnLatitude: latitude,
          returnLongitude: longitude,
          returnBranchName: finalReturnBranchName,
        },
        success: function () {
          alert("반납이 성공적으로 완료되었습니다.");
          closePopup('rentalStatusPopup');
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
function ReportAndReturn(bicycleId) {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
        function (position) {
          returnLatitude = position.coords.latitude;
          returnLongitude = position.coords.longitude;
          console.log("신고 반납");
          openReportPopup(bicycleId, returnLatitude, returnLongitude);
        },
        function (error) {
          console.error("Geolocation 에러: ", error.message);
          alert("위치 정보를 가져오지 못했습니다.");
        }
    );
  } else {
    alert("이 브라우저는 Geolocation을 지원하지 않습니다.");
  }
}
//신고 카테고리 옵션 로딩 함수
function loadReportCategories(selectId) {
  $.ajax({
    url: '/api/map/reportCategories',
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
function openReportPopup(bicycleId, latitude, longitude) {
  closePopup('branchInfoPopup');
  closePopup('rentalStatusPopup');
  loadReportCategories('reportCategorySelect');
  document.getElementById("reportDetails").value = "";
  document.getElementById("reportPopup").style.display = 'block';
  $(document).on("click", ".report-submit", function () {
    submitReport(bicycleId, latitude, longitude);
  });

}
function submitReport(bicycleId ,latitude, longitude) {
  const categoryId = document.getElementById("reportCategorySelect").value;
  const reportDetails = document.getElementById("reportDetails").value;
  $.ajax({
    url: '/api/map/report',
    type: 'POST',
    data: { bicycleId: bicycleId, categoryId: categoryId, reportDetails: reportDetails, latitude : latitude, longitude : longitude},
    success: function(response) {
      closePopup('reportPopup');
      if(response === "After5"){
        alert("신고 후 반납 완료. 만약 기타 장소에 반납이면 결제 후 고객센터를 이용해주세요!");
        window.location.href = "/pay/payment";
      }else if(response === "Before5"){
        console.log(response);
        alert("신고 후 반납 완료. 5분 이내 반납으로 이용 요금이 전혀 발생하지 않습니다.")
      }else{
        alert("신고 성공");
      }
    },
    error: function(xhr) {
      if(xhr.status === 400){
        if(xhr.responseText === "NoPay"){
          alert("미결제 내역이 있습니다. 결제 페이지로 넘어갑니다.");
          window.location.href = "/pay/payment";
        }else{
          alert(xhr.responseText);
        }
      }
      closePopup('reportPopup');
    }
  });
}
//내 위치 이동 함수
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
$(document).ready(function (){
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
        function (position) {
          let lat = position.coords.latitude;
          let lon = position.coords.longitude;
          showMyLocationOnMap(lat, lon); // 현재 위치를 지도에 표시
        },
        function (error) {
          console.error("Geolocation 에러: ", error.message);
          alert("위치 정보를 가져오지 못했습니다.");
        }
    );
  } else {
    alert("이 브라우저는 Geolocation을 지원하지 않습니다.");
  }
  loadBranches(); // 대여소 불러오기
});
