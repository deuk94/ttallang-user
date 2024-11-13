<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>이용 안내</title>
    <style>
      body {
        font-family: Arial, sans-serif;
        background-color: #f5f5f5;
        margin: 0;
        padding: 57px;
        display: flex;
        flex-direction: column;
        min-height: 100vh;
        overflow: auto
      }

      .container {
        max-width: 700px; /* 창의 최대 너비 */
        width: 90%; /* 화면 크기에 따라 너비를 조정 */
        padding: 20px; /* 내부 여백을 충분히 설정 */
        background-color: #fff; /* 흰색 배경 */
        box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2); /* 더 진한 그림자 효과 */
        border-radius: 12px; /* 모서리를 더 둥글게 설정 */
        margin: 80px auto 130px; /* 상단과 하단 여백 추가 */
      }

      header, footer {
        width: 100%;
      }
      main {
        flex: 1;
        display: flex;
        justify-content: center;
        align-items: center;
      }
      h1 {
        text-align: center;
        color: #0d1a33;
        font-size: 24px;
        font-weight: bold;
      }
      .section {
        border-top: 1px solid #ddd;
        padding: 20px 0;
        display: flex;
        align-items: center;
      }
      .section:first-child {
        border-top: none;
      }
      .icon {
        flex: 0 0 60px;
        height: 60px;
        background-color: #e8f4ff;
        border-radius: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 24px;
        color: #0d1a33;
        margin-right: 20px;
      }
      .content {
        flex: 1;
      }
      .content h2 {
        color: #0d1a33;
        font-size: 18px;
        font-weight: bold;
        margin: 0 0 10px;
      }
      .content ul {
        margin: 0;
        padding: 0;
        list-style: none;
        color: #555;
      }
      .content ul li {
        margin-bottom: 5px;
      }
    </style>
</head>
<body>
<jsp:include page="../head/head.jsp" />
<div class="container">
    <h1>이용 안내</h1>

    <div class="section">
        <div class="icon">🔹</div>
        <div class="content">
            <h2>사용법</h2>
            <ul>
                <li>1. 웹사이트에서 가까운 대여소를 찾아 자전거를 선택합니다.</li>
                <li>2. 선택 된 자전거를 대여 버튼을 눌러 대여를 합니다.</li>
                <li>3. 안전 수칙에 맞게 운행을 하고 도착지의 가까운 대여소를 찾아 자전거를 반납합니다.</li>
                <li>4. 반납 완료 후 결제 페이지에서 결제 정보를 확인하고 결제를 진행합니다.</li>
            </ul>
        </div>
    </div>

    <div class="section">
        <div class="icon">🔹</div>
        <div class="content">
            <h2>주의 사항</h2>
            <ul>
                <li>1. 지정된 대여소 이외에서 반납할 경우 20,000원의 과태료가 부과됩니다.(고장인 경우 제외)</li>
                <li>2. 결제를 하지 않을 시 대여가 불가능 합니다.</li>
                <li>3. 무분별한 고장 신고는 제재를 당할 수 있습니다.</li>
            </ul>
        </div>
    </div>
</div>
<jsp:include page="../footer/footer.jsp" />
</body>
</html>
