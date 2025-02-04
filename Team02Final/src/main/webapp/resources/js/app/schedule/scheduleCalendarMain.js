document.addEventListener("DOMContentLoaded", async function () {
    const calendarEl = document.querySelector("#calendarMain");

    if (!calendarEl || calendarEl.getAttribute("data-calendar-initialized")) {
        return;
    }

    // 색상 데이터 기본값
    let companyBackgroundColor = "#000000";
    let companyTextColor = "#FFFFFF";
    let deptBackgroundColor = "#000000";
    let deptTextColor = "#FFFFFF";

    // 색상 데이터를 서버에서 가져오기
    try {
        const colorResponse = await fetch(`${contextPath}/${companyId}/schedule/getColors`, {
            method: "GET",
            headers: { "Content-Type": "application/json" },
        });

        if (!colorResponse.ok) throw new Error("색상 데이터를 불러오는 데 실패했습니다.");

        const colorData = await colorResponse.json();
        companyBackgroundColor = colorData.companyBackgroundColor || companyBackgroundColor;
        companyTextColor = colorData.companyTextColor || companyTextColor;
        deptBackgroundColor = colorData.deptBackgroundColor || deptBackgroundColor;
        deptTextColor = colorData.deptTextColor || deptTextColor;
    } catch (error) {
        console.error("색상 데이터 로드 오류:", error);
    }


    if (typeof tippy === "undefined") {
        console.error("Tippy.js가 로드되지 않았습니다.");
        return;
    }

    // FullCalendar 초기화
    const calendar = new FullCalendar.Calendar(calendarEl, {
        height: "630px",
        expandRows: true,
        initialView: "dayGridMonth",
        locale: "kr",
        headerToolbar: {
            left: "",
            center: "title",
            right: "",
        },
        editable: false,
        selectable: false,
        eventSources: [
            {
                url: `${contextPath}/${companyId}/schedule`,
                method: "GET",
                success: function (events) {
                    const validEvents = events.filter(event => event.extendedProps?.schetypeId);
                    validEvents.forEach(event => {
                        const schetypeId = event.extendedProps.schetypeId;
                        if (schetypeId === "1") {
                            event.backgroundColor = companyBackgroundColor;
                            event.textColor = companyTextColor;
                        } else if (schetypeId === "2") {
                            event.backgroundColor = deptBackgroundColor;
                            event.textColor = deptTextColor;
                        }
                    });
                },
                failure: function () {
                    console.error("이벤트를 불러오지 못했습니다.");
                },
            },
        ],
        eventDidMount: function (info) {
            // `title` 속성을 설정하여 기본 툴팁 표시
            //info.el.setAttribute("title", info.event.title || "일정 없음");

            // Tippy.js로 툴팁 적용
            tippy(info.el, {
                content: info.event.title || "일정 없음", // 툴팁 내용
                placement: "top", // 툴팁 위치
                theme: "light", // 툴팁 테마
                animation: "scale", // 애니메이션
                duration: 200, // 애니메이션 지속 시간
                arrow: true, // 화살표 표시
            });
        },
    });

    calendar.render();
    calendarEl.setAttribute("data-calendar-initialized", "true");
});
