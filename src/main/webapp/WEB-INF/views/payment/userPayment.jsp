<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>결제 정보</title>
    <script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
    <script src="https://cdn.iamport.kr/js/iamport.payment-1.2.0.js"></script>
    <link rel="stylesheet" href="/payment/css/payment.css">
    <script src="/payment/js/payment.js"></script>
</head>
<body>
<h1>결제</h1>
<div class="table-container">
    <table>
        <tr>
            <th>결제 번호</th>
            <td id="paymentId"></td>
        </tr>
        <tr>
            <th>대여 장소</th>
            <td id="rentalBranch"></td>
        </tr>
        <tr>
            <th>대여 시작 시간</th>
            <td id="rentalStartDate"></td>
        </tr>
        <tr>
            <th>반납 장소</th>
            <td id="returnBranch"></td>
        </tr>
        <tr>
            <th>반납 시간</th>
            <td id="rentalEndDate"></td>
        </tr>
        <tr>
            <th>총 대여 시간</th>
            <td id="rentalDuration"></td>
        </tr>
        <tr>
            <th>총 금액</th>
            <td id="paymentAmount"></td>
        </tr>
    </table>
</div>
<div class="button-container">
    <button class="button cancel" onclick="alert('결제를 진행하세요.\n결제가 완료 되어야 홈페이지로 이동할 수 있습니다');">취소</button>
    <button class="button" id="paymentButton">결제</button>
</div>
<jsp:include page="../footer/footer.jsp" />
</body>
</html>
