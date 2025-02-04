const subscribedPaths = new Set(); // 구독된 경로 저장
let notificationHistory = new Set(); // 최근 알림 저장 (중복 방지)

// 특정 경로에 대해 구독
const subscribeOnce = (path, callback) => {
    if (subscribedPaths.has(path)) {
        return; // 이미 구독된 경로는 다시 구독하지 않음
    }
    client.subscribe(path, callback);
    subscribedPaths.add(path);
};

// 알림 표시 함수 - 쪽지(DM)
const showDMToast = (payload) => {
    const notificationKey = `${payload.sender}-${payload.title}-${payload.content}`;
    if (notificationHistory.has(notificationKey)) return; // 중복 방지

    notificationHistory.add(notificationKey);
    setTimeout(() => notificationHistory.delete(notificationKey), 15000);

    Toastify({
        text: `📬 ${payload.sender}님에게서 새로운 쪽지가 도착했습니다!`,
        duration: 10000,
        close: true,
        gravity: "bottom",
        position: "right",
        backgroundColor: "linear-gradient(to right, rgb(173, 216, 230), rgb(179, 158, 255), rgb(138, 43, 226))",
        className: "toastify-big", // 커스터마이즈된 클래스 추가
        onClick: () => window.location.href = `${contextPathApproval}/${companyId}/message/receive`,
    }).showToast();
};

// 알림 표시 함수 - 전자결재 FA
const showApprovalToast = (payload) => {
    const notificationKey = `FA-${payload.documentId}-${Date.now()}`;
    if (notificationHistory.has(notificationKey)) return; // 중복 방지

    notificationHistory.add(notificationKey);
    setTimeout(() => notificationHistory.delete(notificationKey), 15000);

    Toastify({
        text: `📋 ${payload.sender}님의 결재 문서가 도착했습니다.`,
        duration: 10000,
        close: true,
        gravity: "bottom",
        position: "right",
        backgroundColor: "linear-gradient(to right, #FFD200, #FF8C00, #FF4E00)", // 주황색-노란색 계열
        className: "toastify-big", // 커스터마이즈된 클래스 추가
        onClick: () => window.location.href = `${contextPathApproval}/${companyId}/approval/detail/${payload.documentId}`,
    }).showToast();
};

// 알림 표시 함수 - 전자결재 NA
const showNextApproverToast = (payload) => {
    console.log("showNextApproverToast 호출됨:", payload); // 디버깅
    const notificationKey = `NA-${payload.documentId}-${Date.now()}`;
    if (notificationHistory.has(notificationKey)) return; // 중복 방지

    notificationHistory.add(notificationKey);
    setTimeout(() => notificationHistory.delete(notificationKey), 15000);

    Toastify({
        text: `🔔 새로운 결재 문서가 도착했습니다.`,//\n${payload.content}
        duration: 10000,
        close: true,
        gravity: "bottom",
        position: "right",
        backgroundColor: "linear-gradient(to right, #f7e907, #a8e063, #56ab2f)", // 노랑색-초록색 계열
        className: "toastify-big", // 커스터마이즈된 클래스 추가
        onClick: () => window.location.href = `${contextPathApproval}/${companyId}/approval/detail/${payload.documentId}`,
    }).showToast();
};

// 알림 표시 함수 - 전자결재 LA
const showLastApproverToast = (payload) => {
    console.log("showLastApproverToast 호출됨:", payload); // 디버깅
    const notificationKey = `NA-${payload.documentId}-${Date.now()}`;
    if (notificationHistory.has(notificationKey)) return; // 중복 방지

    notificationHistory.add(notificationKey);
    setTimeout(() => notificationHistory.delete(notificationKey), 15000);

    Toastify({
        text: `🔔 결재 결과가 도착했습니다.`,//\n${payload.content}
        duration: 10000,
        close: true,
        gravity: "bottom",
        position: "right",
        backgroundColor: "linear-gradient(to right,rgb(83, 202, 253),rgb(159, 217, 241),  #ff9fcb )", // 노랑색-초록색 계열
        className: "toastify-big", // 커스터마이즈된 클래스 추가
        onClick: () => window.location.href = `${contextPathApproval}/${companyId}/approval/detail/${payload.documentId}`,
    }).showToast();
};

// 메시지 처리 함수 - FA
const handleFA = (messageFrame) => {
    try {
        const payload = JSON.parse(messageFrame.body);
        const currentUserId = document.getElementById('webEmpId').value;

        // 수신자 확인
        if (!payload.receiverId || payload.receiverId !== currentUserId) {
            return; // 메시지가 현재 사용자에게 해당되지 않으면 무시
        }
        console.log("FA 알림 메시지 처리:", payload);
        showApprovalToast(payload);
    } catch (error) {
        console.error("FA 메시지 처리 중 오류 발생:", error);
    }
};

// 메시지 처리 함수 - NA
const handleNA = (messageFrame) => {
    try {
        const payload = JSON.parse(messageFrame.body);
        const currentUserId = document.getElementById('webEmpId').value;

        // 수신자 확인
        if (!payload.receiverId || payload.receiverId !== currentUserId) {
            return; // 메시지가 현재 사용자에게 해당되지 않으면 무시
        }
        console.log("NA 알림 메시지 처리:", payload);
        showNextApproverToast(payload);
    } catch (error) {
        console.error("NA 메시지 처리 중 오류 발생:", error);
    }
};

// 메시지 처리 함수 - LA
const handleLA = (messageFrame) => {
    try {
        const payload = JSON.parse(messageFrame.body);
        const currentUserId = document.getElementById('webEmpId').value;

        // 수신자 확인
        if (!payload.receiverId || payload.receiverId !== currentUserId) {
            return; // 메시지가 현재 사용자에게 해당되지 않으면 무시
        }
        console.log("LA 알림 메시지 처리:", payload);
        showLastApproverToast(payload);
    } catch (error) {
        console.error("LA 메시지 처리 중 오류 발생:", error);
    }
};

// STOMP 연결 성공 시 호출
const fnConnect = function (connFrame) {
    console.log("STOMP 연결 성공:", connFrame);
    console.log("서버 정보:", connFrame.headers);

    // 브로드캐스트 경로 구독
    subscribeOnce("/topic/noti", (message) => {
        console.log("브로드캐스트 메시지 수신:", message.body);
    });

    // 개인 DM 경로 구독
    subscribeOnce("/user/queue/DM", (messageFrame) => {
        try {
            const payload = JSON.parse(messageFrame.body);
            const currentUserId = document.getElementById('webEmpId').value;

            // 수신자 확인
            if (!payload.receiverId || payload.receiverId !== currentUserId) return;

            console.log("DM 알림 메시지 처리:", payload);
            showDMToast(payload);
        } catch (error) {
            console.error("DM 메시지 처리 중 오류 발생:", error);
        }
    });

    // 전자결재 FA 경로 구독
    subscribeOnce("/user/queue/FA", handleFA);

    // 전자결재 NA 경로 구독
    subscribeOnce("/user/queue/NA", handleNA);

    // 전자결재 NA 경로 구독
    subscribeOnce("/user/queue/LA", handleLA);
};

// STOMP 연결 실패 시 호출
const fnStompError = (error) => {
    console.error("STOMP 연결 실패:", error);
};

// 로그인 후 자동 연결
document.addEventListener("DOMContentLoaded", () => {
    const inputElement = document.getElementById("start-btn-websocket");
    const url = inputElement.dataset.url;

    if (url) {
        sockJS = new SockJS(url);

        sockJS.onopen = () => console.log("SockJS 연결 성공");
        sockJS.onclose = () => console.log("SockJS 연결 종료");
        sockJS.onerror = (error) => console.error("SockJS 에러 발생:", error);

        client = Stomp.over(sockJS);
        client.connect({}, fnConnect.bind(client), fnStompError);
    }
});
