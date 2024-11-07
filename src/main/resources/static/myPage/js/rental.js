// 렌탈 내역 조회
$(document).ready(function () {
  $.ajax({
    url: "/api/myPage/rental",
    type: "GET",
    success: function (data) {
      let rows = "";
      if (data.length === 0) {
        rows = "<tr><td colspan='5'>이용 내역이 없습니다.</td></tr>";
      } else {
        $.each(data, function (index, rental) {
          rows += "<tr>";
          rows += "<td>" + rental.bicycleName + "</td>";
          rows += "<td>" + rental.rentalBranch + "</td>";

          let startData = rental.rentalStartDate.replace("T", " ");
          rows += "<td>" + startData + "</td>";

          let returnBranch = rental.returnBranch === null ? "대여중" : rental.returnBranch;
          rows += "<td>" + returnBranch + "</td>";

          let endData = rental.rentalEndDate === null ? "대여중" : rental.rentalEndDate.replace("T", " ");
          rows += "<td>" + endData + "</td>";
          rows += "</tr>";
        });
      }
      $("#rentalTable tbody").html(rows);
    },
    error: function (xhr, status, error) {
      alert("데이터를 가져오는 데 실패했습니다.");
    }
  });
});