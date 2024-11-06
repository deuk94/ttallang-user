$(document).ready(function () {
  // 신고 내역 조회
  $.ajax({
    url: "/api/myPage/faultReport",
    type: "GET",
    success: function (data) {
      let rows = "";
      $.each(data, function (index, faultReport) {
        rows += "<tr id='row-" + faultReport.reportId + "'>";
        rows += "<td>" + faultReport.categoryName + "</td>";
        rows += "<td>" + faultReport.reportDetails + "</td>";
        let formattedDate = faultReport.reportDate.replace("T", " ");
        rows += "<td>" + formattedDate + "</td>";

        let statusText = faultReport.reportStatus === '0' ? "처리 중" : "처리 완료";
        rows += "<td>" + statusText + "</td>";
        rows += "<td><button class='delete-btn' onclick='deleteReport(" + faultReport.reportId + ")'>삭제</button></td>";
        rows += "</tr>";
      });
      $("#rentalTable tbody").html(rows);
    },
    error: function () {
      alert("데이터를 가져오는 데 실패했습니다.");
    }
  });
});

// 신고 삭제
function deleteReport(reportId) {
  if (confirm("정말 삭제하시겠습니까?")) {
    $.ajax({
      url: "/api/myPage/faultReport/" + reportId,
      type: "PATCH",
      cache: false,
      success: function () {
        alert("삭제되었습니다.");
        $("#row-" + reportId).remove();
      },
      error: function () {
        alert("삭제에 실패했습니다. 서버 응답을 확인하세요.");
      }
    });
  }
}
