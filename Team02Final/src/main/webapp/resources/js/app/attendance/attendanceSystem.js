
let allDepartments = []; // 부서 목록 담는 배열
let allPositions = [];   // 직급 목록 담는 배열 
let allAttendTimes = []; // 출근시간 담는 배열
let allLeaveTimes = [];  // 퇴근시간 담는 배열 

let allAttendStatus = []; // 출근 상태 담는 배열 
let allLeaveStatus = [];  // 퇴근 상태 담는 배열 

// 서버에서 출근상태코드 가져오는 function 
function fetchAttendStatus(){
    return fetch(`${contextPath}/${companyId}/hr/attendance/attendStatus`)
           .then(response => response.json())
           .then(data => {
                allAttendStatus = data.map(attStatus => ({ 
                    id : attStatus.code            // 출근 상태 코드
                  , name : attStatus.codeComment   // 출근 상태 이름 
                }));
                // 옵션 렌더링 
                modrenderOptions("attendStatus",allAttendStatus,'');
           })
}

// 서버에서 퇴근상태코드 가져오는 function 
// 퇴근을 안찍어서 퇴근상태가 없을경우엔 option 기본value 없음 출력
function fetchLeaveStatus(){
    return fetch(`${contextPath}/${companyId}/hr/attendance/leaveStatus`)
           .then(response => response.json())
           .then(data => {
            // 퇴근 상태 데이터 처리 
                allLeaveStatus = [
                    {id:"" , name : "없음"},    // 퇴근 상태 데이터 없을 경우에 없음 표시 
                    ...data.map(lstatus => ({
                       id : lstatus.code            // 퇴근 상태 코드
                       ,name : lstatus.codeComment  // 퇴근 상태 이름 
                    }))
                ]
                // 옵션 렌더링 
                modrenderOptions("leaveStatus",allLeaveStatus,'');
           })
}

// 서버에서 부서 목록 가져오는 function 
function fetchDepartments() {
    return fetch(`${contextPath}/${companyId}/hr/employee/departments`)
        .then(response => response.json())
        .then(data => {
            // 부서 목록 데이터 처리 
            allDepartments = data.map(dept => ({ code: dept.departCode, name: dept.departName })); // 모든 부서의 코드와 이름을 가져옴
            renderOptions('selectDepart', allDepartments, ''); // 가져온 부서리스트를 select 태그에 렌더링 
        })
        .catch(error => console.error("부서 목록 가져오기 실패: ", error));
}

// 서버에서 직급 목록 가져오는 function 
function fetchPositions() {
    return fetch(`${contextPath}/${companyId}/hr/employee/positions`)
        .then(response => response.json())
        .then(data => {
            // 직급 목록 데이터 처리 
            allPositions = data.map(posi => ({  // 모든 부서의 코드와 이름을 가져옴 
                id: posi.code, // 직급코드 
                name: posi.codeComment // 직급 이름 
            }));
            renderOptions('selectPosi', allPositions, ''); // 가져온 직급리스트를 select 태그에 렌더링 
        })
        .catch(error => console.error("직급 목록 가져오기 실패: ", error));
}

// 서버에서 출근시간 공통코드 가져오는 function 
function fetchAttendTimes(){
    return fetch(`${contextPath}/${companyId}/hr/attendance/allAttendTimeList`,{
            headers :{
                "Content-Type" : "application/json"
            }
    })
        .then(resp => {
            if(!resp.ok) throw new Error("출근 시간 목록 조회중 오류 발생 ");
            return resp.json();
        })
        .then(data => {
            console.log("출근시간 응답 데이터 : ",data);
            // 출근 시간 데이터 처리리
            allAttendTimes = data.map(attime => ({
               id : attime.code,            // 출근 시간 코드 
               name : attime.codeComment    // 출근 시간 설명 
            }));
            // select 옵션 렌더링 
            modrenderOptions('selectAttendTime',allAttendTimes,'');
        })
        .catch(error => console.error("출근시간 목록 가져오기 실패 " ,error));
}

// 서버에서 퇴근시간 공통코드 가져오는 function  
function fetchLeaveTimes(){
    return fetch(`${contextPath}/${companyId}/hr/attendance/allLeaveTimeList`,{
        headers :{
            "Content-Type" : "application/json"
        }
    })
        .then(resp => {
            if(!resp.ok) throw new Error("퇴근 시간 목록 조회중 오류 발생 ");
            return resp.json();
        })
        .then(data => {
            console.log("퇴근시간 응답 데이터 : ",data);
            // 퇴근 시간 데이터 처리 
            allLeaveTimes = data.map(lvtime => ({
               code : lvtime.code,          // 퇴근 시간 코드
               name : lvtime.codeComment    // 퇴근 시간 설명 
            }));
            // select 태그에 옵션 렌더링 
            modrenderOptions('selectLeaveTime',allLeaveTimes,'');
        })
        .catch(error => console.error("퇴근시간 목록 가져오기 실패 " ,error));
}

// 근태 리스트를 불러오는 함수(페이지 기본 1 로 설정)
// function attendanceList(page=1){
//     const department = document.querySelector("#selectDepart")?.value || '';  // 부서 선택값
//     const position = document.querySelector("#selectPosi")?.value || '';      // 직급 선택값 
//     const startDate = document.querySelector("#startJoinDate")?.value || '';  // 조회 시작일
//     const endDate = document.querySelector("#endJoinDate")?.value || '';      // 조회 종료일
//     const searchWord = document.querySelector("#searchInput")?.value || '';   // 검색어 값 

//     fetch(`${contextPath}/${companyId}/hr/attendance/attendList?page=${page}&department=${department}&position=${position}&startDate=${startDate}&endDate=${endDate}&searchWord=${searchWord}`)
//         .then((resp) => {
//             if(!resp.ok) throw new Error("리스트 가져오는 중 오류 발생");
//             return resp.json();
//         })
//         .then((data) => {
//             console.log("리스트 응답데이터 : ", data);
//             const attendList = data.attendList || []; // 근태 목록 추출
//             const tbody = document.querySelector("#innerTbody");
//             tbody.innerHTML="" // 테이블 데이터 초기화식 

//             if(data.length === 0) {
//                 tbody.insertAdjacentHTML("beforeend",`<tr><td colspan="11">사원이 없습니다.</td></tr>`)

//             }

//             attendList.forEach((att) => {
//                 const positionText = att.posiName || "직급 정보 없음"; //직급 정보 설정 
//                 const causeText = (att.atthisCause && att.atthisCause.trim() !== "") 
//                     ? att.atthisCause 
//                     : "없음";

//                 const atthisOverText = att.atthisOver || "-";
//                 const hleaveText = att.hleaveTime || "-";
//                 const attstaIdOutText = att.attstaIdOut || "-";
//                 const earlyLeaveCauseText = att.earlyLeaveCause || "-";


//                 const row = `
//                     <tr class="rowOne" data-emp-id="${att.emp.empId}" data-atthis-id="${att.atthisId}">
//                         <td>${att.atthisId.substring(0, 4)}-${att.atthisId.substring(4, 6)}-${att.atthisId.substring(6, 8)}</td>
//                         <td>${att.emp.empName}</td>
//                         <td>${positionText}</td>
//                         <td>${att.departName}</td>
//                         <td>${att.hahisTime}</td>
//                         <td>${hleaveText}</td>
//                         <td>${att.atthisOverYn}</td>
//                         <td>${atthisOverText}</td>
//                         <td>${att.attstaIdIn}</td>
//                         <td>${attstaIdOutText}</td> 
//                         <td>${causeText}</td>
//                         <td>${earlyLeaveCauseText}</td>
//                     </tr>
//                     `;
//                 tbody.insertAdjacentHTML("beforeend",row);
//             });
//             renderPagination(data);
           
//         })
// }
// select 옵션 렌더링 함수 
//==========================================================================================================================

// 옵션 렌더링 함수 (필터링을 위한 select 태그에 option 추가)
function renderOptions(elementId , optionList , selectedValue){
    const selectElement = document.querySelector(`#${elementId}`); // 대상 select 요소 가져오기 
    selectElement.innerHTML = '<option value="">전체</option>'; // 기본 옵션 전체 

    // 옵션 리스트 순회하면서 select 태그에 추가
    optionList.forEach(item => {
        selectElement.insertAdjacentHTML("beforeend",`
            <option value="${item.code || item.id}" ${selectedValue === (item.code || item.id) ? 'selected' : ''}>
                ${item.name}
            </option>
            `);
    });
}
// 옵션 렌더링 함수 (option 에 전체 없는 렌더링 함수.)
function modrenderOptions(elementId, optionList, selectedValue) {
    const selectElement = document.querySelector(`#${elementId}`);  // 대상 select 요소 가져오기 ( 부서 , 직급 , 상태 중에서)
    selectElement.innerHTML = ''; // 기존 옵션을 모두 제거 (전체 옵션 포함)

    // 옵션 리스트를 순회하면서 select 태그에 추가
    optionList.forEach(item => {
        selectElement.insertAdjacentHTML("beforeend", `
            <option value="${item.code || item.id}" ${selectedValue === (item.code || item.id) ? 'selected' : ''}>
                ${item.name}
            </option>
        `);
    });
}
//==========================================================================================================================

// 페이지 렌더링 함수
// function renderPagination(data) {
//     const pagination = document.querySelector('#pagination');
//     pagination.innerHTML = ""; // 기존 HTML 초기화
//     const { currentPage, totalPage, startPage, endPage } = data;

//     // 페이지네이션 숨기기 조건
//     if (!totalPage || totalPage <= 1) {
//         pagination.style.display = 'none';
//         return;
//     } else {
//         pagination.style.display = 'block';
//     }

//     let paginationHTML = "";

//     // 이전 버튼
//     if (currentPage > 1) {
//         paginationHTML += `<button data-page="${currentPage - 1}" class="pagination-button btn">&lt;</button>`;
//     }

//     // 페이지 번호 버튼
//     for (let i = startPage; i <= endPage; i++) {
//         paginationHTML += `<button data-page="${i}" class="pagination-button btn ${i === currentPage ? 'active' : ''}">${i}</button>`;
//     }

//     // 다음 버튼
//     if (currentPage < totalPage) {
//         paginationHTML += `<button data-page="${currentPage + 1}" class="pagination-button btn">&gt;</button>`;
//     }

//     // 페이지네이션 HTML 추가
//     pagination.insertAdjacentHTML("beforeend", paginationHTML);
// }

// // 이벤트 리스너 단 한 번 등록
// document.querySelector('#pagination').addEventListener("click", (e) => {
//     const target = e.target;
//     if (target.tagName === 'BUTTON' && target.dataset.page) {
//         const page = parseInt(target.dataset.page, 10);
//         attendanceList(page); // 클릭된 페이지 번호로 리스트 갱신
//     }
// });


function syncFormWithFilters() {
    // 필터 컨테이너의 값 가져오기
    const startDate = document.querySelector('#startJoinDate').value;
    const endDate = document.querySelector('#endJoinDate').value;
    const department = document.querySelector('#selectDepart').value;
    const position = document.querySelector('#selectPosi').value;
    const searchWord = document.querySelector('#searchInput').value;

    // form hidden 필드 업데이트
    document.querySelector('input[name="startDate"]').value = startDate;
    document.querySelector('input[name="endDate"]').value = endDate;
    document.querySelector('input[name="department"]').value = department;
    document.querySelector('input[name="position"]').value = position;
    document.querySelector('input[name="searchWord"]').value = searchWord;
}

// 검색 버튼 클릭 시 form 동기화 후 제출
document.querySelector('.search-container button').addEventListener('click', (e) => {
    e.preventDefault(); // 기본 제출 방지
    // 선택된 값 가져오기
    const startDate = document.querySelector('#startJoinDate').value;
    const endDate = document.querySelector('#endJoinDate').value;
    const department = document.querySelector('#selectDepart').value;
    const position = document.querySelector('#selectPosi').value;
    const searchWord = document.querySelector('#searchInput').value;

    // 로컬 스토리지에 저장 (비동기 방식일때 이렇게 사용용)
    localStorage.setItem('filterOptions', JSON.stringify({
        startDate,
        endDate,
        department,
        position,
        searchWord
    }));
    syncFormWithFilters(); // hidden 필드 동기화
    document.querySelector('#searchForm').submit(); // 폼 제출
});

function paging(page) {
    const searchForm = document.querySelector("#searchForm");
	console.log(page);
	searchForm.page.value = page;
	searchForm.requestSubmit();
}


document.addEventListener("DOMContentLoaded" , () => {
    fetchDepartments();
    fetchPositions();
    // attendanceList(1); 
    
    const startDateInput = document.getElementById("startJoinDate");
    const endDateInput = document.getElementById("endJoinDate");
    const department = document.querySelector('#selectDepart');
    const position = document.querySelector('#selectPosi');
    const searchWord = document.querySelector("#searchInput");
    const searchForm = document.querySelector("#searchForm");
    
    // 초기화 버튼. (필드 초기화)
    const reloadBtn = document.querySelector("#reload-btn");
    reloadBtn.addEventListener("click", () => {
        console.log("새로고침 버튼 클릭");
        startDateInput.value = "";
        endDateInput.value = "";
        department.value="";
        position.value="";
        searchWord.value="";
        
        // localStorage의 필터 값 초기화
        // (필드에 선택한 옵션값이 남아있어서 직접 localStorage에 set한걸 remove로 삭제해줌)
        localStorage.removeItem('filterOptions');

        // 폼의 hidden 필드 초기화
        $(":input[type=hidden]").each(function() {
            $(this).val("");
        });
        // document.querySelectorAll("input[type='hidden']").forEach(function(){
        //     this.value="";
        // })
        searchForm.submit();
    });

      // LocalStorage에서 저장된 값 가져오기
      const savedFilters = JSON.parse(localStorage.getItem('filterOptions')) || {};

      // 필터 값 복원
      document.querySelector('#startJoinDate').value = savedFilters.startDate || '';
      document.querySelector('#endJoinDate').value = savedFilters.endDate || '';
      document.querySelector('#selectDepart').value = savedFilters.department || '';
      document.querySelector('#selectPosi').value = savedFilters.position || '';
      document.querySelector('#searchInput').value = savedFilters.searchWord || '';
  
      // 비동기로 옵션 로드 후, 저장된 값으로 선택 설정
      fetchDepartments().then(() => {
          document.querySelector('#selectDepart').value = savedFilters.department || '';
      });
  
      fetchPositions().then(() => {
          document.querySelector('#selectPosi').value = savedFilters.position || '';
      });
  
    
    

    // 필터 선택 시 직원 목록 갱신 (검색어는 input 으로 받음)
    // document.querySelector('#selectDepart')?.addEventListener("change", () => attendanceList(1));
    // document.querySelector('#selectPosi')?.addEventListener("change", () => attendanceList(1));
    // document.querySelector('#startJoinDate')?.addEventListener("change", () => attendanceList(1));
    // document.querySelector('#endJoinDate')?.addEventListener("change", () => attendanceList(1));
    // document.querySelector('#searchInput')?.addEventListener("input", () => attendanceList(1));    

    
    // 시작일 변경 시 종료일의 최소 날짜를 설정
    startDateInput.addEventListener("change", function () {
        const startDate = this.value;
        endDateInput.min = startDate; // 종료일 최소값을 시작일로 설정
    });

    // 종료일 변경 시 유효성 검증
    endDateInput.addEventListener("change", function () {
        const startDate = startDateInput.value;
        const endDate = this.value;

        if (endDate < startDate) {
            alert("종료일은 시작일보다 이전일 수 없습니다.");
            this.value = ""; // 잘못된 입력값 초기화
        }
    });


    // 테이블 정렬 이벤트 설정 (화살표 오름 내림 차순) 
    const headers = document.querySelectorAll("th");

    headers.forEach((header, index) => {
      header.addEventListener("click", () => {
        const order = header.dataset.order || "asc"; // 초기 정렬 상태  (asc 또는 desc)
        const tbody = document.getElementById("innerTbody"); 
        const rows = Array.from(tbody.querySelectorAll("tr"));  // tbody내의 모든 행 가져오기 
  
        // 행 정렬 수행
        const sortedRows = rows.sort((a, b) => {
          const aValue = a.children[index].innerText.trim();    // a 행의 index번째 셀 값    
          const bValue = b.children[index].innerText.trim();    // b 행의 index번째 셀 값 
  
          return order === "asc"    
            ? aValue.localeCompare(bValue)  // 오름 차순 정렬
            : bValue.localeCompare(aValue); // 내림 차순 정렬 
        });
  
        // 정렬된 행으로 테이블 업데이트
        tbody.innerHTML = "";
        sortedRows.forEach((row) => tbody.appendChild(row));
  
        // 화살표 방향 토글
        header.dataset.order = order === "asc" ? "desc" : "asc";
        updateArrowDirection(header, order);
      });
    });
  
    // 화살표 방향 업데이트 함수
    function updateArrowDirection(header, order) {
      document.querySelectorAll("th .arrow").forEach((arrow) => {
        arrow.innerText = "▼"; // 모든 화살표 초기화
      });
      const arrow = header.querySelector(".arrow");
      arrow.innerText = order === "asc" ? "▲" : "▼"; // 선택된 정렬 방향 반영 
    }
  // 화살표 오름 내림 차순 끝 

    // 근태 다운로드 버튼 클릭시 이벤트리스너 (필터링 옵션 설정 가능)
    const downloadBtn = document.querySelector("#downloadBtn");
    downloadBtn.addEventListener("click" , (e) => {
        e.preventDefault();
    
        
        // 필터 옵션 파라미터로 넘겨서 필터링 된 근태 기록 다운로드 가능 
        const department = document.querySelector("#selectDepart")?.value || '';
        const position = document.querySelector("#selectPosi")?.value || '';
        const startDate = document.querySelector("#startJoinDate")?.value || '';
        const endDate = document.querySelector("#endJoinDate")?.value || '';
        const searchWord = document.querySelector("#searchInput")?.value || '';
        
        // 옵션 파라미터 넘기고, 해당되는 근태 기록 다운로드 요청 
        window.location.href=`${contextPath}/${companyId}/hr/attendance/downloadAttendanceExcel?position=${position}&department=${department}&startDate=${startDate}&endDate=${endDate}&searchWord=${searchWord}`;

        }) // downloadBtn end 

    // 출퇴근시간 변경 클릭 이벤트
    const changeTimeBtn = document.querySelector('#changeTimeBtn');   
    let modAttendTimeModal = document.querySelector('#modAttendTimeModal');
    let modATInstanceModal = null;
    

    changeTimeBtn.addEventListener("click", (e)=> {
        console.log("출퇴근시간 변경 버튼 클릭 ");
        
        
        let attendId;   // 출퇴근 시간 ID 저장 변수 
        fetch(`${contextPath}/${companyId}/hr/attendance/selectAttendTime`)
            .then(resp => {
                if(!resp.ok) throw new Error("출퇴근 시간 조회 도중 오류 발생");
                return resp.json();
            })
            .then(data => {
                console.log("출퇴근 시간 조회 응답데이터 : " , data);
                attendId = data.attendId;   // 응답데이터에서 attendId 저장 
                console.log("attendId 값 : " , attendId);
                modATInstanceModal = new bootstrap.Modal(modAttendTimeModal);
                modATInstanceModal.show();  // 모달 표시
                fetchAttendTimes(); // 출근 시간 옵션 가져오기
                fetchLeaveTimes();  // 퇴근 시간 옵션 가져오기 
                const modalBody = document.querySelector('#modAttendTimeModalBody');
                modalBody.innerHTML = ""; // 모달 내용용 초기화 

                const row = `
                     <div class="current-time">
                        <label for="attendTime">
                            현재 설정된 출근 시간
                            <input type="text" class="time-input" value="${data.attendTime}" readonly />
                        </label>
                        <label for="leaveTime">
                            현재 설정된 퇴근 시간
                            <input type="text" class="time-input" value="${data.leaveTime}" readonly />
                        </label>
                    </div><br>

                   <div class="select-time">
                        <label for="selectAttendTime">
                            출근 시간 설정<br>
                            <select id="selectAttendTime" class="form-select"></select>
                        </label><br>
                        <label for="selectLeaveTime">
                            퇴근 시간 설정<br>
                            <select id="selectLeaveTime" class="form-select"></select>
                        </label>
                    </div>

                    `
                modalBody.insertAdjacentHTML("beforeend" ,row);

            })

            // 수정 버튼 클릭 이벤트 리스너 
            const modATBtn = document.querySelector('#modATBtn');
            modATBtn.addEventListener("click" , (e)=>{
                console.log("출퇴근 수정 버튼 클릭 ");
                e.preventDefault();

                const selectAttendTime = document.querySelector('#selectAttendTime')?.value || '';
                const selectLeaveTime = document.querySelector('#selectLeaveTime')?.value || '';
                const body = {
                    attendId : attendId  
                  , attendTime : selectAttendTime
                  , leaveTime : selectLeaveTime
                };
                console.log("내가 설정한 출퇴근 시간 :",body);

                Swal.fire({
                    title : "출퇴근 시간을 수정하시겠습니까?"
                  , icon : "warning"
                  , showCancelButton: true
                  , confirmButtonText: "수정"
                  , cancelButtonText: "취소"
                  , customClass: {              // Swal이 Modal 뒤에 뜨는 현상 발생할 때 사용 
                    popup: 'custom-swal-popup'  // css에서 custom-swal-popup에 z-index를 모달보다 더많이줌 > 1050 
                 }
                }).then((result)=> {
                    if(result.isConfirmed){

                        fetch(`${contextPath}/${companyId}/hr/attendance/modAttendTime`,{
                            method : 'PUT'
                          , headers : {
                            "Content-Type" : "application/json"
                          },
                            body : JSON.stringify(body)
                        })
                        .then(resp => {
                            if(!resp.ok) throw new Error("수정 도중 오류 발생 ");
                            return resp.json();
                        })
                        .then(data => {
                            console.log("수정 응답 데이터 : ", data);
                            Swal.fire({
                                title : "수정 성공"
                              , text : "출퇴근 시간 수정에 성공했습니다."
                              , icon : "success"
                            }).then(()=>{
                                modATInstanceModal.hide();
                                // attendanceList(1); // 초기 근태페이지로 (1페이지)
                                paging(1);
                            })
                            .catch(error => {
                                console.log(error);
                                Swal.fire({
                                    title : "오류 발생"
                                  , text : "수정 도중 오류가 발생했습니다."
                                  , icon : "error"
                                });
                            }) // catch end 
                        }) // .then(data) end 

                    } // isConfirmed end 
                }) // .then(result) end 

            })  // 시간 설정하고 수정 버튼 클릭 이벤트 end 


        }) // 출퇴근시간 수정 버튼 클릭 이벤트 end 

        
        // 사원 한명 클릭 근태 상세 조회 이벤트 
        let attendEmpModal = document.querySelector('#attendEmpModal');
        let attendInstanceModal = null;
        const modAttEmpBtn = document.querySelector("#modAttEmpBtn");
        const attendEmpModalBody = document.querySelector("#attendEmpModalBody");
        const tbody = document.querySelector("#innerTbody");
        let empId = null;
        let atthisId = null;
        tbody.addEventListener("click", async (e) => {
            e.preventDefault();
            const clickRow = e.target.closest("tr.rowOne");  
            if (!clickRow) return;

            console.log("사원 클릭");
            empId = clickRow.dataset.empId;        
            atthisId = clickRow.dataset.atthisId; 


            attendInstanceModal = new bootstrap.Modal(attendEmpModal);
            attendInstanceModal.show();
            
            try {
                // 상세 정보 조회 (await 적용)
                const resp = await fetch(`${contextPath}/${companyId}/hr/attendance/attendDetail/${empId}/${atthisId}`, {
                    headers: { "Content-Type": "application/json" }
                });
                if (!resp.ok) throw new Error("상세정보 조회중 오류 발생");
                const data = await resp.json();
                
                // 모달 데이터 바인딩
                const hlText = data.hleaveTime || '-';
                const aioText = data.attstaIdOut || '-';
                

                
                const atCause = data.atthisCause ? `<textarea class="form-control">${data.atthisCause}</textarea>` : "-";
                const elCause = data.earlyLeaveCause ? `<textarea class="form-control">${data.earlyLeaveCause}</textarea>` : "-";


                attendEmpModalBody.innerHTML = `
                    <div class="table-responsive"> 
                        <table class="table">
                            <tr><th>날짜</th><td>${data.atthisId.substring(0, 4)}-${data.atthisId.substring(4, 6)}-${data.atthisId.substring(6, 8)}</td></tr>
                            <tr><th>사원명</th><td>${data.emp.empName}</td></tr>
                            <tr><th>직급</th><td>${data.posiName}</td></tr>
                            <tr><th>부서</th><td>${data.departName}</td></tr>
                            <tr><th>출근시간</th><td>${data.hahisTime}</td></tr>
                            <tr><th>출근상태</th><td><select id="attendStatus" class="form-select"></select></td></tr>
                            <tr><th>지각사유</th><td>${atCause}</td></tr>
                            <tr><th>퇴근시간</th><td>${hlText}</td></tr>
                            <tr><th>퇴근상태</th><td><select id="leaveStatus" class="form-select"></select></td></tr>
                            <tr><th>조퇴사유</th><td>${elCause}</td></tr>
                            <tr><th>초과근무여부</th><td>${data.atthisOverYn}</td></tr>
                            <tr><th>초과근무시간(분)</th><td>${data.atthisOver}</td></tr>
                        </table>
                    </div>
                `;

                // 출근상태 / 퇴근상태 select 박스에 옵션 추가 (await 사용)
                await fetchAttendStatus();
                const selectedASId = allAttendStatus.find(item => item.name === data.attstaIdIn)?.id || '';
                modrenderOptions("attendStatus", allAttendStatus, selectedASId);

                await fetchLeaveStatus();
                const selectedLVId = allLeaveStatus.find(item => item.name === data.attstaIdOut)?.id || '';
                modrenderOptions("leaveStatus", allLeaveStatus, selectedLVId);
                
            } catch (error) {
                console.error("데이터 조회 오류:", error);
                Swal.fire("오류 발생", "데이터를 불러오는데 실패했습니다.", "error");
            }
        });

        modAttEmpBtn.addEventListener("click", async function(e) {
            e.preventDefault();
            console.log("수정버튼 클릭");
        
            const selectedAt = document.querySelector("#attendStatus")?.value || '';
            const selectedLv = document.querySelector("#leaveStatus")?.value || '';
        
            const reqData = {
                empId: empId,
                atthisId: atthisId,
                attstaIdIn: selectedAt,
                attstaIdOut: selectedLv
            };
        
            try {
                const resp = await fetch(`${contextPath}/${companyId}/hr/attendance/modAttendLeaveStatus`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(reqData)
                });
                if (!resp.ok) throw new Error("수정 실패");
                const data = await resp.json();
        
                console.log("수정 완료:", data);
                Swal.fire({
                    title: "수정 완료",
                    text: "근태 상태 수정이 완료되었습니다.",
                    icon: "success"
                }).then(() => {
                    attendInstanceModal.hide();
                    paging(1);
                });
            } catch (error) {
                console.error("수정 오류:", error);
                Swal.fire("수정 실패", "다시 시도해주세요.", "error");
            }
        });



    }) // DOM end 

        





