<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>신고 내역</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <link rel="stylesheet" href="/myPage/css/faultReport.css">
    <script src="/myPage/js/faultReport.js"></script>
</head>
<body>
<jsp:include page="head/head.jsp" />
<h1>신고 내역</h1>
<div class="menu">
    <div><a href="/myPage/userModify">정보수정</a></div>
    <div><a href="/myPage/userRental">이용내역</a></div>
    <div class="active"><a href="/myPage/userFaultReport">신고내역</a></div>
</div>
<table id="rentalTable">
    <thead>
    <tr>
        <th>신고 내역</th>
        <th>신고 내용</th>
        <th>신고 날짜</th>
        <th>처리 상태</th>
        <th>삭제</th>
    </tr>
    </thead>
    <tbody></tbody>
</table>
<jsp:include page="footer/footer.jsp" />
</body>
</html>