<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>안전 수칙</title>
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
    <h1>안전 수칙</h1>

    <div class="section">
        <div class="icon">🔹</div>
        <div class="content">
            <h2>출발 전</h2>
            <ul>
                <li>이용 전 브레이크, 타이어, 체인, 안장 조임을 꼭 확인하세요.</li>
                <li>반드시 안전모를 착용하세요.</li>
                <li>음주 후 대여 금지.</li>
            </ul>
        </div>
    </div>

    <div class="section">
        <div class="icon">🚲</div>
        <div class="content">
            <h2>주행 중</h2>
            <ul>
                <li>자전거 도로를 이용하고, 없는 경우 차도 우측 가장자리에 붙어서 통행합니다. 이때 2대 이상 나란히 통행하지 않습니다.</li>
                <li>횡단보도에서는 자전거를 끌고 보행해야 합니다.</li>
                <li>보행자의 통행에 방행가 될 경우 일시정지하고, 보행자 보호를 위해 과속하지 않습니다.</li>
                <li>주행시 핸들을 놓고나 이어폰, 핸드폰을 사용하지 않습니다.</li>
            </ul>
        </div>
    </div>
</div>
<jsp:include page="../footer/footer.jsp" />
</body>
</html>
