$(document).ready(function () {
  // 회원 정보 조회
  $.ajax({
    url: "/myPage/modify",
    type: "GET",
    dataType: "json",
    success: function (data) {
      $('#userName').val(data.userName);
      $('#customerName').val(data.customerName);
      $('#userPassword').val('');
      $('#customerPhone').val(data.customerPhone);
      $('#birthday').val(data.birthday);
      $('#email').val(data.email);
    },
    error: function () {
      alert("회원 정보를 불러오는 데 실패했습니다.");
    }
  });

  // 수정 버튼 클릭 시 데이터 전송
  $('#saveButton').click(function () {
    const updatedUser = {
      customerPhone: $('#customerPhone').val(),
      birthday: $('#birthday').val(),
      email: $('#email').val()
    };

    // 회원 정보 수정
    $.ajax({
      url: "/myPage/modify",
      type: "PUT",
      contentType: "application/json",
      data: JSON.stringify(updatedUser),
      success: function () {
        alert("회원 정보가 성공적으로 수정되었습니다.");
      },
      error: function (xhr) {
        if (xhr.status === 404) {
          alert("해당 사용자를 찾을 수 없습니다.");
        } else {
          alert("회원 정보 수정에 실패했습니다.");
        }
      }
    });
  });

  // 회원탈퇴 버튼 클릭 시
  $('#deleteButton').click(function () {
    if (confirm("정말로 탈퇴하시겠습니까?")) {
      $.ajax({
        url: "/myPage/modify",
        type: "patch",
        success: function () {
          alert("회원탈퇴가 완료되었습니다.");
          window.location.href = "/map/myMap.jsp";
        },
        error: function () {
          alert("회원탈퇴에 실패했습니다.");
        }
      });
    }
  });
});