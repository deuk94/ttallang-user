<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>자전거 대여시간 초과요금 및 환불 안내</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      background-color: #f5f5f5;
      margin: 0;
      padding: 40px;
      display: flex;
      flex-direction: column;
      min-height: 90vh;
      overflow: auto
    }

    .container {
      max-width: 700px; /* 창의 최대 너비 */
      width: 100%; /* 화면 크기에 따라 너비를 조정 */
      padding: 1px 20px; /* 내부 여백을 충분히 설정 */
      background-color: #fff; /* 흰색 배경 */
      box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2); /* 더 진한 그림자 효과 */
      border-radius: 12px; /* 모서리를 더 둥글게 설정 */
      margin: 100px auto 50px; /* 상단과 하단 여백 추가 */
    }

    h2 {
      font-size: 20px;
      color: #333;
      margin-bottom: 10px;
      display: flex;
      align-items: center;
    }

    h2::before {
      content: "🟢"; /* 아이콘을 텍스트로 예시 */
      font-size: 20px;
      margin-right: 8px;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 10px;
      margin-bottom: 30px;
    }
    th, td {
      border: 1px solid #ddd;
      padding: 10px;
      text-align: center;
      font-size: 14px;
      color: #666;
    }
    th {
      background-color: #f0f0f0;
      font-weight: bold;
      color: #333;
    }
    .section-title {
      font-weight: bold;
      font-size: 16px;
      margin-top: 20px;
    }
  </style>
</head>
<body>
<jsp:include page="../head/head.jsp" />
<div class="container">
  <!-- 첫 번째 테이블: 자전거 대여시간 초과요금 -->
  <h2>자전거 대여시간 초과요금</h2>
  <table>
    <thead>
    <tr>
      <th>구분</th>
      <th>요금</th>
    </tr>
    </thead>
    <tbody>
    <tr>
      <td>잠금 해제</td>
      <td>추가 요금</td>
    </tr>
    <tr>
      <td>500원</td>
      <td>분당 150원</td>
    </tr>
    </tbody>
  </table>
<br>
  <!-- 두 번째 테이블: 누비자 가입비 환불안내 -->
  <h2>환불안내</h2>
  <table>
    <thead>
    <tr>
      <th>서비스 구분</th>
      <th>환불</th>
    </tr>
    </thead>
    <tbody>
    <tr>
      <td rowspan="2">5분 이내 고장 시</td>
    </tr>
    <tr>
      <td>결제 X</td>
    </tr>
    <tr>
      <td>5분 이상 이용 시</td>
      <td>고객센터 문의<br>(02-1234-9876)</td>
    </tr>
    </tbody>
  </table>
</div>
<jsp:include page="../footer/footer.jsp" />
</body>
</html>
