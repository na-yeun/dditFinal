let hahisData;   
let hleaveData;

// 미니프로필에 들어갈 오늘의 나의 출근 시간 조회 
function fetchMyHahisTime() {
    const startTime = document.querySelector("#startTime");    // 출근시간을 표시할 곳. 
    fetch(`${contextPath}/${companyId}/hr/attendance/selectMyHahisTime/${empId}`)
        .then(resp => {
            if (!resp.ok) throw new Error("내 출근시간 조회 도중 오류 발생");
            return resp.json();
        })
        .then(data => {
            console.log("내 출근시간 조회 데이터 :", data);
            hahisData = data.hahisTime || null;         // 조회된 출근시간
            startTime.innerText = hahisData || "-";     // 화면에 출근 시간 표시시

            // 버튼 상태 설정
            if (hahisData) {
                startWorkBtn.disabled = true;    // 출근 버튼 비활성화
                endWorkBtn.disabled = false;    // 퇴근 버튼 활성화
                endWorkLogoutBtn.disabled = false; // 퇴근 및 로그아웃 버튼 활성화
            } else {
                startWorkBtn.disabled = false;   // 출근 버튼 활성화
                endWorkBtn.disabled = true;      // 퇴근 버튼 비활성화
                endWorkLogoutBtn.disabled = true; // 퇴근 및 로그아웃 버튼 비활성화
            }
        })
        
}

// 미니프로필에 들어갈 오늘의 나의 퇴근 시간 조회 
function fetchMyHleaveTime() {
    const endTime = document.querySelector("#endTime");     // 퇴근시간을 표시할 곳.
    fetch(`${contextPath}/${companyId}/hr/attendance/selectMyHleaveTime/${empId}`)
        .then(resp => {
            if (!resp.ok) throw new Error("내 퇴근시간 조회 도중 오류 발생");
            return resp.json();
        })
        .then(data => {
            console.log("내 퇴근시간 응답 데이터 :", data);
            hleaveData = data.hleaveTime || null;       // 조회된 퇴근시간
            endTime.innerText = hleaveData || "-";      // 화면에 퇴근시간 표시

          
            return hleaveData;
        })
}



document.addEventListener("DOMContentLoaded", () => {
  
    // 버튼 및 초기 상태 설정
    const startWorkBtn = document.querySelector('#startWorkBtn');
    const endWorkBtn = document.querySelector('#endWorkBtn');
    const endWorkLogoutBtn = document.querySelector('#endWorkLogoutBtn');
    startWorkBtn.disabled = false;
    endWorkBtn.disabled = true;
    endWorkLogoutBtn.disabled = true;
    
    fetchMyHahisTime();  // 출근시간 렌더링 
    fetchMyHleaveTime(); // 퇴근시간 렌더링 

    let attendTime;
    let leaveTime;

    // 출퇴근 시간 정보 가져오기
    fetch(`${contextPath}/${companyId}/hr/attendance/selectAttendTime`)
        .then(resp => {
            if (!resp.ok) throw new Error("출퇴근 시간정보 가져오는 도중 오류발생");
            return resp.json();
        })
        .then(data => {
            console.log("출퇴근 시간정보 응답 데이터 :", data);
            attendTime = data.attendTime;   // 출근 시간 저장
            leaveTime = data.leaveTime;     // 퇴근 시간 저장 
        });

    // 출근 버튼 클릭 이벤트 
    startWorkBtn.addEventListener("click", async (e) => {
        e.preventDefault();
        console.log("출근 버튼 클릭");

        const now = new Date();       // 현재시간 생성  ex) 2024-12-24T09:30:00
        const formatAttendTime = `${attendTime}:00`;    // 출근 시간 형식화 (DB에서 불러오는 attendTime은 09:00 형식이여서 뒤에 :00 추가)
        const [hours, minutes] = formatAttendTime.split(":"); // : 을 기준으로 나눔 ["09" ,"00","00"] ==> 개별적으로 사용하기 위해 
        const attendTimeObj = new Date();
        // setHours : Date 객체의 시간을 설정하는 메소드. 
        attendTimeObj.setHours(parseInt(hours), parseInt(minutes), 0, 0);   // 출근 시간 객체 생성 

        let isLate = now > attendTimeObj; // 지각 여부 판단
        let atthisCause = null;

        if (isLate) {   // 지각시 사유 입력 
            const { value: cause } = await Swal.fire({
                title: "지각입니다",
                text: "지각사유를 입력해주세요.",
                input: 'text',
                inputPlaceholder: "50자 이내로 입력해주세요.",
                confirmButtonText: "확인",
                inputValidator: (value) => {
                    if(!value) {
                        return "사유를 입력해주세요";
                    }else if(value.length > 50){
                        return "50자 이내로 입력해주세요."
                    }
                }
            });


            if (!cause) return Swal.fire("지각 사유를 입력해주세요.", "", "warning");
            atthisCause = cause;    // 입력받은 사유 저장 
        }

        const reqData = { empId, atthisCause, attendTime };  // 요청할 데이터 생성 

        try {
            const resp = await fetch(`${contextPath}/${companyId}/hr/attendance/addMyHahisTime`, {
                method: 'POST',
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(reqData)
            });

            if (resp.ok) {
                Swal.fire("출근 완료", "", "success");
                fetchMyHahisTime();
            } else {
                Swal.fire("이미 출근 처리 되었습니다.", "", "error");
            }
        } catch (error) {
            console.error(error);
            Swal.fire("오류 발생", "네트워크 오류", "error");
        }
    }); // 출근버튼 이벤트 끝 

    // 퇴근 버튼 클릭 이벤트 
    endWorkBtn.addEventListener("click", async (e) => {
        e.preventDefault();
        console.log("퇴근 버튼 클릭");

        /*
            출근시간과 퇴근시간 insert 매퍼에서 
            출근시간은 CURRENT_TIMESTAMP , 퇴근시간은 TimeZone을 사용해서 
            TimeZone을 사용하면 UCT 기준 시간으로 맞춰지기 때문에 KST 기준 시간으로 설정 해주어야함.
        */
        const now = new Date();     // 현재시간 
        const kstOffset = 9 * 60 * 60 * 1000;   // KST 기준 시간 
        const kstTime = new Date(now.getTime() + kstOffset)
            .toISOString()
            .replace("Z", "+09:00");

        const formatLeaveTime = `${leaveTime}:00`;
        const [hours, minutes] = formatLeaveTime.split(":");
        const leaveTimeObj = new Date();
        leaveTimeObj.setHours(parseInt(hours), parseInt(minutes), 0, 0);

        let reqData = {
              empId
            , hleaveTime: kstTime    // 현재시간 
            , atthisOverYn: 'N'      // 연장근무 여부 기본 N
            , atthisOver: 0          // 연장근무시간 기본 : 0
            , attstaIdOut: '8'       // 기본값 : 정상 퇴근
            , earlyLeaveCause : null //조퇴 사유 
        };

        if (now < leaveTimeObj) {   // 조퇴 확인 
            const { isConfirmed } = await Swal.fire({
                title: "퇴근시간 이전입니다.",
                text: "정말 퇴근하시겠습니까?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonText: "예",
                cancelButtonText: "아니오"
            });
            if(!isConfirmed) return;

            // 조퇴사유 입력
            const {value : cause} = await Swal.fire({
                title: "조퇴 사유",
                text: "사유를 입력하세요.",
                input: "text",
                inputPlaceholder: "50자 이내로 입력해주세요.",
                confirmButtonText: "확인",
                inputValidator: (value) => {
                    if(!value) {
                        return "사유를 입력해주세요";
                    }else if(value.length > 50){
                        return "50자 이내로 입력해주세요."
                    }
                }
                
            });

            reqData.attstaIdOut = "7";      // 상태코드 : 조퇴
            reqData.earlyLeaveCause = cause; // 입력받은 사유 추가

            
        } else {
            const { isConfirmed } = await Swal.fire({
                title: "연장 근무 여부",
                text: "연장근무로 처리하시겠습니까?",
                icon: "question",
                showCancelButton: true,
                confirmButtonText: "예",
                cancelButtonText: "아니오"
            });
            if (isConfirmed) {
                const overtimeMinutes = Math.round((now - leaveTimeObj) / 60000);
                reqData.atthisOver = overtimeMinutes;
                reqData.atthisOverYn = 'Y';
                reqData.attstaIdOut = '9'; // 연장근무
            }
        }

        try {
            const resp = await fetch(`${contextPath}/${companyId}/hr/attendance/addMyHleaveTime`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(reqData)
            });

            if (resp.ok) {
                Swal.fire("퇴근 처리 완료", "", "success");
                fetchMyHleaveTime();
            } else {
                Swal.fire("퇴근 처리 실패", "", "error");
            }
        } catch (error) {
            console.error(error);
            Swal.fire("오류 발생", "서버 통신 오류", "error");
        }
    }); // 퇴근버튼 end 


    // 퇴근 및 로그아웃 버튼 이벤트 
    endWorkLogoutBtn.addEventListener("click", async (e) => {
        e.preventDefault();
        console.log("퇴근 및 로그아웃 버튼 클릭!");
    
        const now = new Date();
        const kstOffset = 9 * 60 * 60 * 1000;
        const kstTime = new Date(now.getTime() + kstOffset)
            .toISOString()
            .replace("Z", "+09:00");
    
        const formatLeaveTime = `${leaveTime}:00`;
        const [hours, minutes] = formatLeaveTime.split(":");
        const leaveTimeObj = new Date();
        leaveTimeObj.setHours(parseInt(hours), parseInt(minutes), 0, 0);
    
        let reqData = {
            empId,
            hleaveTime: kstTime,
            atthisOverYn: "N",
            atthisOver: 0,
            attstaIdOut: "8",
            earlyLeaveCause : null
        };
    
        if (now < leaveTimeObj) {
            const { isConfirmed } = await Swal.fire({
                title: "퇴근시간 이전입니다.",
                text: "정말 퇴근하시겠습니까?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonText: "예",
                cancelButtonText: "아니오",
            });
            if(!isConfirmed) return;

            // 조퇴사유 입력
            const {value : cause} = await Swal.fire({
                title: "조퇴 사유",
                text: "사유를 입력하세요.",
                input: "text",
                inputPlaceholder: "사유를 입력하세요.",
                confirmButtonText: "확인"
            });

            if(!cause){
                Swal.fire("조퇴사유를 입력해주세요.","","warning");
                return;
            }
            reqData.attstaIdOut = "7";      // 상태코드 : 조퇴
            reqData.earlyLeaveCause = cause; // 입력받은 사유 추가

        } else {
            const { isConfirmed } = await Swal.fire({
                title: "연장 근무 여부",
                text: "연장근무로 처리하시겠습니까?",
                icon: "question",
                showCancelButton: true,
                confirmButtonText: "예",
                cancelButtonText: "아니오",
            });
            if (isConfirmed) {
                const overtimeMinutes = Math.round((now - leaveTimeObj) / 60000);
                reqData.atthisOver = overtimeMinutes;
                reqData.atthisOverYn = "Y";
                reqData.attstaIdOut = "9"; // 연장근무
            }
        }
    
        try {
            const resp = await fetch(`${contextPath}/${companyId}/hr/attendance/addMyHleaveTime`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(reqData),
            });
    
            if (resp.ok) {
                await fetchMyHleaveTime();
                Swal.fire({
                    title: "퇴근 처리 완료",
                    text: "잠시 후 로그아웃됩니다.",
                    icon: "success",
                    timer: 2000, // 2초 후 자동으로 닫힘
                    timerProgressBar: true, // 타이머 진행 상태를 표시
                    willClose: () => {
                        // 타이머가 끝난 후 로그아웃 처리
                        window.location.href = `${contextPath}/${companyId}/logout`;
                    },
                });
            } else {
                Swal.fire("퇴근 처리 실패", "", "error");
            }
        } catch (error) {
            console.error(error);
            Swal.fire("오류 발생", "서버 통신 오류", "error");
        }
    });
    





});  // DOM end
