<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>시작일까지 남은 일수</title>
<style>
    /* 페이지 전체 스타일 */
    body {
        margin: 0;
        padding: 0;
        display: flex;
        justify-content: center;
        align-items: center;
        flex-direction: column;
        height: 100vh;
        font-family: Arial, sans-serif;
        background-color: #f8f9fa; /* 부드러운 회색 배경 */
    }

    /* 타이머 컨테이너 */
    .countdown-container {
        text-align: center;
        background-color: #ffffff;
        border: 1px solid #dee2e6;
        border-radius: 8px;
        padding: 30px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        max-width: 400px;
        width: 100%;
    }

    h1 {
        font-size: 24px;
        font-weight: bold;
        color: #343a40;
    }

    h4 {
        font-size: 20px;
        color: #495057;
        margin-top: 20px;
    }

    /* 홈으로 버튼 */
    .home-button {
        margin-top: 30px;
        padding: 10px 20px;
        font-size: 16px;
        background-color: #6366f1; /* 버튼 배경색 */
        color: #ffffff; /* 버튼 텍스트 색상 */
        border: none;
        border-radius: 4px;
        cursor: pointer;
        text-align: center;
    }

    .home-button:hover {
        background-color: #4f46e5; /* 버튼 호버 배경색 */
    }
</style>
<script>
    // 서버에서 전달된 초기 값
    let remainingDays = ${remainingDays}; // 남은 일수
    let hours = ${hours}; // 남은 시간
    let minutes = ${minutes}; // 남은 분
    let seconds = ${seconds}; // 남은 초

    function updateCountdown() {
        if (seconds === 0) {
            if (minutes === 0) {
                if (hours === 0) {
                    if (remainingDays === 0) {
                        // 시간이 모두 0이 되면 종료 처리
                        clearInterval(countdownInterval);
                        document.getElementById("countdown").innerHTML = "계약이 시작되었습니다!";
                        return;
                    }
                    remainingDays--; // 남은 일수 감소
                    hours = 23; // 23시간으로 리셋
                    minutes = 59; // 59분으로 리셋
                    seconds = 59; // 59초로 리셋
                } else {
                    hours--; // 남은 시간 감소
                    minutes = 59; // 59분으로 리셋
                    seconds = 59; // 59초로 리셋
                }
            } else {
                minutes--; // 남은 분 감소
                seconds = 59; // 59초로 리셋
            }
        } else {
            seconds--; // 남은 초 감소
        }

        // HTML 업데이트
        document.getElementById("countdown").innerHTML =
            remainingDays + "일 : " +
            hours.toString().padStart(2, '0') + "시 : " +
            minutes.toString().padStart(2, '0') + "분 : " +
            seconds.toString().padStart(2, '0') + "초";
    }

    // 1초마다 updateCountdown 실행
    const countdownInterval = setInterval(updateCountdown, 1000);
</script>
</head>
<body>
    <div class="countdown-container">
        <h1>시작일까지 남은 일수</h1>
        <h4 id="countdown">${remainingDays}일 : ${hours}시 : ${minutes}분 : ${seconds}초</h4>
        <button class="home-button" onclick="history.back()">홈으로</button>
    </div>
</body>
</html>
