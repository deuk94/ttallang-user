<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>의무 및 책임사항</title>
    <style>
      body {
        font-family: Arial, sans-serif;
        background-color: #f5f5f5;
        margin: 0;
        padding: 57px;
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
    <h1>의무 및 책임사항</h1>

    <div class="section">
        <div class="icon">🔹</div>
        <div class="content">
            <h2>사용 안내</h2>
            <ul>
                <li>보다 많은 사람들이 자전거 이용을 위해 기본 대여시간이 초과되면 요금이 징수됩니다.</li>
                <li>한 계정으로 하나의 자전거만 대여 가능합니다.(반납 및 요금 결제 후 대여가능)</li>
            </ul>
        </div>
    </div>

    <div class="section">
        <div class="icon">💲</div>
        <div class="content">
            <h2>손실 및 위약금</h2>
            <ul>
                <li>자전거 대여소 시설물의 고의적인 훼손 또는 공영자전거 분실 및 절도 시에는 관련 규정에 따라 정한 요금을 부과합니다.</li>
                <li>자전거 이용 중 위법, 부당한 행위로 발생한 손실 및 비용은 이용자가 부담해야 합니다.</li>
            </ul>
        </div>
    </div>

    <div class="section">
        <div class="icon">🚲</div>
        <div class="content">
            <h2>자전거 주행 시 주의사항</h2>
            <ul>
                <li>자전거는 일반 생활용 자전거로써 경주, 산악등반, 자전거 묘기 등의 특수 목적을 위해 사용할 수 없습니다.</li>
                <li>자전거 대여소 아닌 곳에 정차 및 주차 중일 때에는 자전거 도난에 주의해야 합니다.</li>
                <li>집합이 바구니에는 과다한 중량을 싣지 말아야 합니다.</li>
                <li>자전거를 대여하기 전 해당 자전거의 구동장치 및 안전장치를 확인해야 합니다.</li>
            </ul>
        </div>
    </div>
</div>
<jsp:include page="../footer/footer.jsp" />
</body>
</html>
