<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>보험항목 및 보장금액</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      background-color: #f5f5f5;
      margin: 0;
      padding: 70px;
      display: flex;
      flex-direction: column;
      min-height: 100vh;
      overflow: hidden;
    }

    .container {
      max-width: 700px; /* 창의 최대 너비 */
      width: 90%; /* 화면 크기에 따라 너비를 조정 */
      padding: 20px; /* 내부 여백을 충분히 설정 */
      background-color: #fff; /* 흰색 배경 */
      box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2); /* 더 진한 그림자 효과 */
      border-radius: 12px; /* 모서리를 더 둥글게 설정 */
      margin: 20px auto; /* 화면 중앙에 위치 */
    }
    h2 {
      font-size: 24px;
      color: #003399;
      margin-bottom: 10px;
      display: flex;
      align-items: center;
    }
    h2::before {
      content: "📋"; /* 아이콘 예시 */
      margin-right: 10px;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 10px;
    }
    th, td {
      border: 1px solid #ddd;
      padding: 10px;
      text-align: center;
      font-size: 14px;
    }
    th {
      background-color: #f0f0f0;
      font-weight: bold;
    }
    .section-title {
      font-weight: bold;
      font-size: 16px;
      margin-top: 20px;
    }
    .footer {
      margin-top: 20px;
      font-size: 14px;
      color: #333;
    }
    .footer p {
      margin: 5px 0;
    }
    .icon {
      color: #003399;
      margin-right: 5px;
    }
  </style>
</head>
<body>
<jsp:include page="../head/head.jsp" />
<div class="container">
  <h2>보험항목 및 보장금액</h2>
  <table>
    <thead>
    <tr>
      <th rowspan="2">보험항목</th>
      <th colspan="2">시민안전보험</th>
      <th colspan="2">자전거 보험</th>
    </tr>
    <tr>
      <th>보장 내용</th>
      <th>보장 금액</th>
      <th>일반 자전거 이용</th>
      <th>공영자전거(누비자) 이용</th>
    </tr>
    </thead>
    <tbody>
    <tr>
      <td rowspan="2">자연재해(익사, 열사병 포함) 사망</td>
      <td>20백만원</td>
      <td>사망</td>
      <td rowspan="2">10백만원</td>
    </tr>
    <tr>
      <td>사회재난(전염병 제외) 사망</td>
      <td>자전거 사고</td>
    </tr>
    <tr>
      <td rowspan="3">폭발, 화재, 붕괴, 산사태 사고 상해</td>
      <td>사망</td>
      <td>10백만원</td>
      <td>자전거 진단위로금 + 6일 이상 입원시 추가 15만원</td>
    </tr>
    <tr>
      <td>휴유장해 (3%~100%)</td>
      <td>최대 10백만원</td>
      <td>자전거 사고</td>
      <td>7백만원</td>
    </tr>
    <!-- 추가적인 행을 원하는 만큼 추가 -->
    </tbody>
  </table>
  <div class="footer">
    <p><span class="icon">🔹</span>보험금 청구 및 문의</p>
    <p>청구방법: 청구서 및 구비서류를 제출하여 보험사에 직접 청구(자세한 문의: 보험사 고객센터)</p>
    <p>고객센터: 농협손해보험(1644-9666)</p>
    <p>청구기간: 사고발생일로부터 3년 이내 청구 가능</p>
    <p>기타문의: 딸랑컴퍼니 고객 센터 <span class="icon">☎</span>02-1234-9876</p>
  </div>
</div>
<jsp:include page="../footer/footer.jsp" />
</body>
</html>
