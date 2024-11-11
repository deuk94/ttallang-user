let name = "자전거 대여 요금"
let amount, email, userName, phoneNumber;

$(document).ready(function () {
  updatePaymentAmount();
  selectUser();

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

      let { minutes, seconds } = RentalCalculate(data.rentalStartDate, data.rentalEndDate);
      $('#rentalDuration').text(`${minutes} 분 ${seconds} 초`);

      $('#paymentAmount').text(formatCurrency(data.paymentAmount));
    },
    error: function () {
      alert('결제 정보를 가져오는 데 실패했습니다.');
    }
  });

  // 결제 버튼 클릭 이벤트
  $('#paymentButton').on('click', function () {
    validatePayment();
  });
});

// 결제 전 검증
function validatePayment() {
  $.ajax({
    url: '/api/pay/paymentValidation',
    method: 'GET',
    success: function (isValid) {
      if (isValid) {
        requestPay();
      } else {
        alert("결제 정보가 일치하지 않습니다.");
      }
    },
    error: function () {
      alert('결제 정보 검증에 실패했습니다.');
    }
  });
}

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
      alert("결제에 실패하였습니다.");
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

// 총 대여 시간 계산
function RentalCalculate(startDate, endDate) {
  const start = new Date(startDate);
  const end = new Date(endDate);
  const diffInMs = end - start;
  const diffInSeconds = Math.floor(diffInMs / 1000);

  const minutes = Math.floor(diffInSeconds / 60);
  const seconds = diffInSeconds % 60;

  return {minutes, seconds};
}