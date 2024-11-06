let name = "자전거 대여 요금"
let amount, email, userName, phoneNumber;

$(document).ready(function () {
  updatePaymentAmount()
  selectUser()

  // 결제 내역 조회
  $.ajax({
    url: '/api/pay/payment',
    method: 'GET',
    success: function (data) {
      $('#customerId').text(data.customerId);
      $('#paymentId').text(data.paymentId);
      $('#rentalBranch').text(data.rentalBranch);
      $('#rentalStartDate').text(data.rentalStartDate.replace("T", " "));
      $('#returnBranch').text(data.returnBranch);
      $('#rentalEndDate').text(data.rentalEndDate.replace("T", " "));
      $('#paymentAmount').text(formatCurrency(data.paymentAmount));
    },
    error: function () {
      alert('결제 정보를 가져오는 데 실패했습니다.');
    }
  });
});

function requestPay() {
  let IMP = window.IMP;
  IMP.init("imp56011821");

  let merchant_uid = "order_" + new Date().getTime();

  IMP.request_pay({
    pg: "html5_inicis",
    pay_method: "card",
    merchant_uid: merchant_uid,
    name: name,
    amount: amount,
    buyer_email: email,
    buyer_name: userName,
    buyer_tel: phoneNumber,
  }, function (rsp) {
    if (rsp.success) {
      $.ajax({
        url: '/api/pay/payment',
        method: 'PATCH',
        contentType: 'application/json',
        data: JSON.stringify({
          imp_uid: rsp.imp_uid,
          name: name,
          merchant_uid: merchant_uid,
          paymentAmount: amount,
          customerName: userName,
          customerPhone: phoneNumber,
          email: email,
        }),
        success: function () {
          alert("결제가 완료되었습니다.");
          window.location.href = "/map/main";
        },
        error: function () {
          alert("결제 검증에 실패했습니다.");
        }
      });
    } else {
      alert("결제에 실패하였습니다. \n" + rsp.error_msg);
    }
  });
}

// 결제 금액 수정
function updatePaymentAmount() {
  $.ajax({
    url: '/api/pay/updateAmount',
    method: 'PATCH',
    success: function (data) {
      amount = data.paymentAmount; // 업데이트된 금액 저장
      $('#paymentAmount').text(formatCurrency(amount));
    }
  });
}

// 결제 정보 조회
function selectUser() {
  $.ajax({
    url: '/api/pay/paymentInfo',
    method: 'GET',
    success: function (data) {
      email = data.email;
      userName = data.customerName;
      phoneNumber = data.customerPhone;
    }
  });
}

// 세자릿수 콤마, 금액 뒤에 원 붙이기
function formatCurrency(amount) {
  return amount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") + " 원";
}