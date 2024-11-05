<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Rental List</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <link rel="stylesheet" href="/myPage/css/rental.css">
    <script src="/myPage/js/rental.js"></script>
</head>
<body>
<h1>이용 내역</h1>
<div class="menu">
    <div><a href="/myPage/userModify">정보수정</a></div>
    <div class="active"> <a href="/myPage/userRental">이용내역</a></div>
    <div> <a href="/myPage/userFaultReport">신고내역</a></div>
</div>
<table id="rentalTable">
    <thead>
    <tr>
        <th>자전거</th>
        <th>대여소</th>
        <th>대여일시</th>
        <th>반납대여소</th>
        <th>반납일시</th>
    </tr>
    </thead>
    <tbody>
    </tbody>
</table>
</body>
</html>
