/**
 * 
 */

gantt.plugins({
    marker: true
});

// 꼭 빈 객체로 저장 하기




    // 일감 이름, 일감 목록, 일감 데이터

    /*
        id: 작업 ID
        text: 작업 이름
        start_date: 시작 날짜
        duration: 작업 기간 (일 단위)
        parent: 상위 작업 ID (없으면 최상위 작업)
        color: 작업의 바 색상
        progress: 작업의 진행률
        status: 작업 상태 (Pending, In Progress 등)
        open: 하위 작업 열려있음, false => 하위 일감 닫혀있음.
    */


gantt.config.min_column_width = 20;


// 타임라인 구성 (월, 일)
gantt.config.scales = [
    { unit: "month", step: 1, format: "%Y년 %m월" }, // 월 단위 한글 포맷
    { unit: "day", step: 1, format: "%d일" } // 일 단위 한글 포맷
];

/*
    상태 데이터를 정의하고, 이를 Gantt 차트에 표시 및 편집할 수 있도록 구성합니다.
    Lightbox 설정:
    Lightbox는 작업을 편집할 수 있는 팝업 창입니다.
    description: 작업 이름을 편집하는 텍스트 영역.
    status: 상태를 선택하는 드롭다운.
    time: 작업 기간 설정.
*/
gantt.serverList("status", [
    { key: 0, label: "배정중" },
    { key: 1, label: "예정" },
    { key: 2, label: "진행중" },
    { key: 3, label: "완료" },
    { key: 4, label: "검토중" },
    { key: 5, label: "최종 완료" },
]);

gantt.locale.labels.section_status = "Status";

gantt.config.lightbox.sections = [
    { name: "description", height: 38, map_to: "text", type: "textarea", focus: true },
    {
        name: "status", height: 22, map_to: "status", type: "select",
        options: gantt.serverList("status")
    },
    { name: "time", type: "duration", map_to: "auto" }
];


function byId(list, id) {
    for (var i = 0; i < list.length; i++) {
        if (list[i].key == id) {
            return list[i].label || "";
        }
    }
    return "Pending";
}

const storyPointsEditor = { type: "number", map_to: "story_points", min: 0 };


/*
    Gantt 차트에 표시할 컬럼 구성:
    text: 작업 이름.
    start_date: 시작 날짜.
    items: 작업에 연결된 항목 수를 표시 (사용자 정의 템플릿).
    story_points: 작업의 스토리 포인트를 표시 (수정 가능).
    status: 작업 상태를 표시 (사용자 정의 템플릿).
*/
gantt.config.columns = [
        {
            name: "text",
            label: "일감", // 컬럼 헤더 이름
            tree: true, // 트리 구조 사용
            width: 330, // 컬럼 너비
            resize: true, // 크기 조정 가능
            template: function (task) {
                // 제목과 + 버튼을 하나의 셀로 합침
                return `
                <div id="workSell" style="display: flex; align-items: center; justify-content: space-between; padding-right: 5px;">
                    <!-- 왼쪽: 작업 제목 -->
                    <span style="flex-grow: 1; text-align: left;">${task.text}</span>
                    <!-- 오른쪽: "+"와 "..." 버튼 -->
                    <div class="hover-buttons" style="display: flex; align-items: center; margin-left: auto;">
                            <button class="btn btn-primary" style="cursor: pointer;" onclick="openModal('${task.id}', event)" data-bs-toggle="tooltip" data-bs-offset="0,4" data-bs-placement="bottom" data-bs-html="true" title="<span>일감 추가</span>">
                                +
                            </button>
                            <button class="btn btn-primary" style="cursor: pointer;" data-bs-toggle="tooltip" data-bs-offset="0,4" data-bs-placement="right" data-bs-html="true" title="<span>상세보기</span>">
                                <img id="workSellIcon" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAIdJREFUSEvtk7EJw0AMRd+fIkXAEDKQl0njDUwGyjjBBhcZIZ2MQWkScvIVV1kqPx89eEii8ajxfhIQGk5FR1RkZj0wAgbcJD02D7X5x93PFZnZApy98JR0dcALOO3NS4AZ6LwwSbo4oCovATZFd+ANDF+Kdud/AeHdVRbyk0NhqSgVhQbCwgojj0IZbzfplQAAAABJRU5ErkJggg==" style="width: 16px; height: 16px;" />
                            </button>
                        </div>
                </div>
                `;
            }
        }
];


function addSubtask(parentId) {
    const newTask = {
        id: gantt.uid(), // 고유 ID 생성
        text: "새로운 작업", // 기본 작업 이름
        start_date: gantt.date.add(gantt.getTask(parentId).start_date, 1, "day"), // 부모 작업 시작일 + 1일
        duration: 1, // 기본 기간
        parent: parentId // 부모 ID 설정
    };
    gantt.addTask(newTask);
}

function closeOffCanvas() {
    const offCanvasElement = document.getElementById("offcanvasScroll");
    const offCanvasInstance = bootstrap.Offcanvas.getInstance(offCanvasElement);
    if (offCanvasInstance) {
        offCanvasInstance.hide(); // OffCanvas 닫기
    }
}

function openModal(taskId, event) {
    event.stopPropagation();
    closeOffCanvas(); // OffCanvas 닫기

    if(taskId.length === 0){
        // 모달의 내용을 업데이트
        document.getElementById("exampleModalLabel1").innerText = `신규 일감 등록`;
        document.getElementById("taskParentid").value = ``;

        // 모달 띄우기
        const modalElement = new bootstrap.Modal(document.getElementById("largeModal"));
        modalElement.show();
    }else{
        // Task 정보를 가져오기
        const task = gantt.getTask(taskId);

        // 모달의 내용을 업데이트
        document.getElementById("exampleModalLabel1").innerText = `${task.text} : 하위 일감 등록`;
        document.getElementById("taskParentid").value = `${taskId}`;

        // 모달 띄우기
        const modalElement = new bootstrap.Modal(document.getElementById("largeModal"));
        modalElement.show();
    }

}




gantt.attachEvent("onTaskClick", function (id, e) {
    const isTreeIcon = e.target.closest(".gantt_tree_icon");
    if (isTreeIcon) {
         // 트리 열기/닫기는 허용
         return true;
     }

    // OffCanvas 열기
    openOffCanvas(id);

    // 기본 클릭 동작을 방지 (옵션)
    return false;
});

// OffCanvas 제어 함수
function openOffCanvas(taskId) {
    // Task 정보를 가져오기
    const task = gantt.getTask(taskId);

    fetch(`${contextPath}/${companyId}/project/${projId}/task`,{
        method: "POST",
        headers: {
            "Content-Type": "application/json", // JSON 형식 명시
        },
        body: JSON.stringify({ taskId: taskId })
    })
    .then(resp => {
        if(!resp.ok){
            console.log("error");  
        }
        return resp.json();
    })
    .then(task => {
        // OffCanvas 내부 내용을 업데이트
        document.getElementById("offcanvasScrollLabel").innerHTML = `
            <div style="display: flex; align-items: center;">
                <div id="selectedColor" onclick="offCanvasColorClick();" style="width: 33px; height: 33px; background-color: ${task.taskColor}; margin-right: 8px; border: 1px solid #ccc; border-radius: 4px;">
                   
                </div>
                <strong id="taskName">${task.taskNm}</strong>
            </div>
                             <!-- 드롭다운 메뉴 -->
                    <div class="dropdown-menu dropdown-menu-color p-2" aria-labelledby="colorDropdown">
                        <div class="d-flex flex-wrap justify-content-between" style="gap: 6px;">
                            <button class="color-option offcanvas-color" style="background-color: #8c77e6;" data-color="#8c77e6" onclick="offCanvasColorChange('#8c77e6')"></button>
                            <button class="color-option offcanvas-color" style="background-color: #4787ff;" data-color="#4787ff" onclick="offCanvasColorChange('#4787ff')"></button>
                            <button class="color-option offcanvas-color" style="background-color: #41c685;" data-color="#41c685" onclick="offCanvasColorChange('#41c685')"></button>
                            <button class="color-option offcanvas-color" style="background-color: #5cb6d9;" data-color="#5cb6d9" onclick="offCanvasColorChange('#5cb6d9')"></button>
                            <button class="color-option offcanvas-color" style="background-color: #f2c338;" data-color="#f2c338" onclick="offCanvasColorChange('#f2c338')"></button>
                            <button class="color-option offcanvas-color" style="background-color: #f35955;" data-color="#f35955" onclick="offCanvasColorChange('#f35955')"></button>
                            <button class="color-option offcanvas-color" style="background-color: #727d90;" data-color="#727d90" onclick="offCanvasColorChange('#727d90')"></button>
                            <button class="color-option offcanvas-color" style="background-color: #5a45ba;" data-color="#5a45ba" onclick="offCanvasColorChange('#5a45ba')"></button>
                            <button class="color-option offcanvas-color" style="background-color: #0e4cdd;" data-color="#0e4cdd" onclick="offCanvasColorChange('#0e4cdd')"></button>
                            <button class="color-option offcanvas-color" style="background-color: #1d7348;" data-color="#1d7348" onclick="offCanvasColorChange('#1d7348')"></button>
                            <button class="color-option offcanvas-color" style="background-color: #1e6989;" data-color="#1e6989" onclick="offCanvasColorChange('#1e6989')"></button>
                            <button class="color-option offcanvas-color" style="background-color: #b23d05;" data-color="#b23d05" onclick="offCanvasColorChange('#b23d05')"></button>
                            <button class="color-option offcanvas-color" style="background-color: #ba2322;" data-color="#ba2322" onclick="offCanvasColorChange('#ba2322')"></button>
                            <button class="color-option offcanvas-color" style="background-color: #4f5c73;" data-color="#4f5c73" onclick="offCanvasColorChange('#4f5c73')"></button>
                        </div>
                    </div>
            `;
        
        // OffCanvas의 내용을 업데이트
        const offCanvasBody = document.querySelector("#offcanvasScroll .offcanvas-body");

        // 기존 내용을 지우고 새로 삽입
        offCanvasBody.innerHTML = ""; // 기존 내용을 제거
        offCanvasBody.innerHTML =
            // dropdown 선택시 이벤트 리스너 구현해야함. 
            `
            <form id="taskDetailForm" method="post">
            <div class="dropdown" style="display:none;">
                <button class="btn btn-primary dropdown-toggle" type="button" id="dropdownMenuButton" data-bs-toggle="dropdown" aria-expanded="false">
                    상태 선택
                </button>
                <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                    <li><a class="dropdown-item" href="#"><span class="badge bg-secondary">배정 대기중</span></a></li>
                    <li><a class="dropdown-item" href="#"><span class="badge bg-secondary">예정</span></a></li>
                    <li><a class="dropdown-item" href="#"><span class="badge bg-primary">진행중</span></a></li>
                    <li><a class="dropdown-item" href="#"><span class="badge bg-danger">검토중</span></a></li>
                    <li><a class="dropdown-item" href="#"><span class="badge bg-warning">수정중</span></a></li>
                    <li><a class="dropdown-item" href="#"><span class="badge bg-success">완료</span></a></li>
                </ul>
            </div>
            <br/>
            <div>
                <label for="taskContent" class="form-label">일감 설명</label>
                <textarea class="form-control" id="taskContent" name="taskContent" rows="3">${task.taskContent ? task.taskContent : ''}</textarea>
            </div>
            <br/>

            <div class="accordion accordion-header-primary" id="accordionStyle1">
                <div class="accordion-item card">
                    <h2 class="accordion-header">
                        <button type="button" class="accordion-button collapsed" data-bs-toggle="collapse" data-bs-target="#accordionStyle1-1" aria-expanded="true">
                            세부사항
                        </button>
                    </h2>
                    <div id="accordionStyle1-1" class="accordion-collapse collapse show" data-bs-parent="#accordionStyle1">
                            <div class="form-group row mb-3">
                                <label for="html5-week-input" class="col-md-3 col-form-label text-md-end ps-4">일감 상태</label>
                                <div class="col-md-8">
                                    <select class="form-select" id="taskStatus" name="taskStatus">
									    <option value="">상태 선택</option>
									    <option value="배정중">배정중</option>
									    <option value="예정">예정</option>
									    <option value="진행중">진행중</option>
									    <option value="검토중">검토중</option>
									    <option value="수정중">수정중</option>
									    <option value="완료">완료</option>
                                    </select>
                                </div>
                            </div>

                            <!-- Week Input -->
                            <div class="form-group row mb-3">
                                <label for="html5-week-input" class="col-md-3 col-form-label text-md-end ps-4">담당자</label>
                                <div class="col-md-4">
                                    <input class="form-control" type="text" value="${task.empName ? task.empName : ""}" id="projectMemberName" readonly style="background-color : white; text-align : center;"/>                                    
                                </div>
                                <div class="col-md-4">
                                    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#basicModal" style="width:100px;">변경</button>
                                </div>
                            </div>

                            <div class="form-group row mb-3" style="display:none;">
                                <div class="col-md-4">
                                    <input class="form-control" name="projectMemberid" id="projectMemberid" type="text" value="${task.projectMemberid ? task.projectMemberid : ""}" />
                                </div> 
                            </div>

                            <div class="form-group row mb-3">
                                <label for="html5-week-input" class="col-md-3 col-form-label text-md-end ps-4">일감 제목</label>
                                <div class="col-md-8">
                                    <input class="form-control" type="text" name="taskNm" value="${task.taskNm}" id="html5-week-input" required="required"  />
                                </div>
                            </div>


                            <div class="form-group row mb-3">
                                <label for="html5-week-input" class="col-md-3 col-form-label text-md-end ps-4">시작 날짜</label>
                                <div class="col-md-8">
                                    <input class="form-control" type="date" name="taskSdate" value="${task.taskSdate}" id="html5-week-input" />
                                </div>
                            </div>

                            <div class="form-group row mb-3">
                                <label for="html5-week-input" class="col-md-3 col-form-label text-md-end ps-4">종료 날짜</label>
                                <div class="col-md-8">
                                    <input class="form-control" type="date" name="taskEdate" value="${task.taskEdate}" id="html5-week-input" />
                                </div>
                            </div>


                            <div class="form-group row mb-3">
                                <label for="html5-week-input" class="col-md-3 col-form-label text-md-end ps-4">진행률</label>
                                <div class="col-md-8">
                                    <select class="form-select" id="taskProgress" name="taskProgress">
                                        <option value="1">0%</option>
                                        <option value="10">10%</option>
                                        <option value="20">20%</option>
                                        <option value="30">30%</option>
                                        <option value="40">40%</option>
                                        <option value="50">50%</option>
                                        <option value="60">60%</option>
                                        <option value="70">70%</option>
                                        <option value="80">80%</option>
                                        <option value="90">90%</option>
                                        <option value="100">100%</option>
                                    </select>
                                </div>
                            </div>

                            <!-- Color Input -->
                            <div class="form-group row mb-3" style="display: none;">
                                <div class="col-md-8">
                                    <input class="form-control" type="text" name="taskColor" value="${task.taskColor}" id="offcanvasTaskColor" />
                                </div>
                            </div>
                            <div class="form-group row mb-3" style="display: none;">
                                <div class="col-md-8">
                                    <input class="form-control" type="text" name="taskModifyMemId" value="${empId}" id="empId" />
                                </div>
                            </div>
                            <div class="form-group row mb-3" style="display: none;">
                                <div class="col-md-8">
                                    <input class="form-control" type="text" name="taskId" value="${task.taskId}" id="taskId" />
                                </div>
                            </div>
                    </div>
                </div>
            </div>


            <br/>
            <div style="display: flex; justify-content: flex-end;">
                <button type="submit" class="btn btn-primary mb-2">수정</button>
                <button type="button" class="btn btn-danger mb-2" onclick="deleteTask('${task.taskId}')">삭제</button>
                <button type="button" class="btn btn-secondary mb-2" data-bs-dismiss="offcanvas">취소</button>
            </div>
            <br/>
            <br/>
            </form>
        `
    ;

    document.getElementById("taskStatus").value = task.taskStatus || ""; // taskStatus 값을 설정
    document.getElementById("taskProgress").value = task.taskProgress || ""; // taskProgress 값을 설정
    
    const taskDetailForm = document.getElementById("taskDetailForm");

    taskDetailForm.addEventListener("submit", (e) => {
        e.preventDefault();
        
        const formData = new FormData(taskDetailForm); // FormData 객체 생성

        const result = validateDates(taskDetailForm.taskSdate.value, taskDetailForm.taskEdate.value);
        
        if(result == false){
            return;
        }

        if(taskDetailForm.taskNm.value.length === 0){
            Swal.fire({
                title: "오류",
                html: "일감 제목이 공백입니다.<br/>일감 제목을 입력한 후 다시 시도해주세요.",
                icon: "error",
            });
            return;
        }

        if(taskDetailForm.taskSdate.value.length === 0 || taskDetailForm.taskEdate.value.length === 0){
            Swal.fire({
                title: "오류",
                html: "일감 시작일 또는 일감 종료일이 공백입니다.<br/>다시 입력한 후 다시 시도해주세요.",
                icon: "error",
            });
            return;
        }

        fetch(`${contextPath}/${companyId}/project/${projId}/updateTask`, {
            method: "POST",
            body: formData, // FormData로 데이터 전송
        })
            .then((resp) => {
                if (!resp.ok) {
                    throw new Error("Request failed");
                }
                return resp.text(); // 서버에서 반환되는 텍스트 응답 처리
            })
            .then((response) => {
                console.log("서버 응답:", response);
                Swal.fire({
                    title: "성공",
                    text: "일감이 성공적으로 수정되었습니다.",
                    icon: "success",
                    timer: 2000,
                    showConfirmButton: true,
                });
                ganttInitFunction();
                closeOffCanvas(); // OffCanvas 닫기
                updateProjectLog();
            })
            .catch((error) => {
                console.error("Error:", error);
                Swal.fire({
                    title: "오류",
                    text: "일감 수정에 실패했습니다. 다시 시도해주세요.",
                    icon: "error",
                });
            });
    });

    const taskId = task.taskId;
    fetch(`${contextPath}/${companyId}/project/${projId}/taskHistory`, {
        method:"POST",
        headers : {
            "Content-type" : "application/json"
        },
        body: JSON.stringify({taskId})
    })
    .then(resp => {
        if(resp.ok){
           return resp.json(); 
        }
    })
    .then(resp => {
        let toastContext = `<div class="toast-container position-relative">`;
        resp.forEach(resp => {
            toastContext += `<div class="bs-toast toast fade show" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="toast-header">
                    <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAJpJREFUSEtjZKAxYKSx+Qz0tSAr93/I/38MLQyMDOpk+ew/w01GJoaaaZMZ18D0o/ggM+f/VQYGBi2yDEdoujZ9CqM2Lgv+U2g4WPv0KYxwh6P7gH4WwFyRmfMfbCmyq7D5EqaOaB/Q3AJK4mI0DrCGHsmRPBoHBIuK0XwwMMU1uML5z9DOwMCgQlYyJVThkGUoAU30rZNp4QMAyNmEGVJcLcMAAAAASUVORK5CYII="/>
                <div class="me-auto fw-medium">일감 로그</div>
                <small>${resp.taskDate}</small>
                <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
                <div class="toast-body">
                ${resp.empName}님이 "${resp.taskTitle}"을(를) ${resp.taskMethod}하였습니다.
                </div>
            </div>
            `;
        });
        toastContext += `</div>`

        document.querySelector(".offcanvas-body").insertAdjacentHTML("beforeend", toastContext);
    })
    .catch(error => {console.error(error)})
    

    // OffCanvas 표시
    const offCanvasElement = document.getElementById("offcanvasScroll");
    const offCanvasInstance = new bootstrap.Offcanvas(offCanvasElement);
    offCanvasInstance.show();
    })
    .catch(error => console.error(error));

}

gantt.attachEvent("onTaskDblClick", function (id, e) {
    // 클릭 타이머를 취소하여 단일 클릭 작업 방지
    // clearTimeout(clickTimeout);

    // 원하는 더블 클릭 동작 수행
    console.log("Double click event triggered for task ID:", id);

    return false; // 더블 클릭 이벤트도 기본 동작 중단
});



// Gantt 준비 이벤트
// task.items 데이터를 기반으로 작업 항목을 차트에 표시.

gantt.attachEvent("onGanttReady", function () {
    gantt.addTaskLayer(function (task) {
        if (task.items) {
            const mainEl = document.createElement('div');
            for (timestamp in task.items) {
                const itemDate = new Date(+timestamp);
                const value = task.items[timestamp];

                const sizes = gantt.getTaskPosition(task, itemDate, itemDate);

                const el = document.createElement('div');
                // el.className = 'work_item';
                el.innerHTML = "";
                el.setAttribute("data-taskId", task.id);


                let elWidth = 15;

                // position to the center of the timeline cell
                let cellWidth = gantt.getScale().col_width;

                if (elWidth > cellWidth / 2) {
                    elWidth = cellWidth / 2
                }
                let marginLeft = (cellWidth - elWidth) / 2;


                el.style.left = sizes.left + 'px';
                el.style.top = sizes.top + 'px';
                el.style.marginLeft = marginLeft + 'px';
                el.style.width = elWidth + "px"

                el.style.zIndex = 1;
                let background = "Gray";
                if (+itemDate < +task.start_date || +itemDate > +task.end_date) {
                    background = "DarkRed";
                }
                else {
                    if (task.parent) {
                        const parent = gantt.getTask(task.parent);
                        background = parent.color || background;
                    }
                    else {
                        background = task.color || background;
                    }
                }
                el.style.background = background;
                el.style.color = "#eee"


                mainEl.appendChild(el);
            }
            if (mainEl.childNodes) {
                return mainEl;
            }


        }
    });
});


gantt.config.row_height = 45;
gantt.config.bar_height = 30;

let resizeElement = false;
window.addEventListener('mousedown', function (e) {
    const element = e.target.closest(".left_resizer") || e.target.closest(".right_resizer")
    if (element) {
        resizeElement = element;
    }
});

window.addEventListener('mouseup', function (e) {
    resizeElement = false;
});

window.addEventListener('mousemove', function (e) {
    if (resizeElement) {
        const resizerWidth = 8;
        const margin = 10;

        const totalRange = document.querySelector(".total_range");
        const leftResizer = document.querySelector(".left_resizer");
        const rightResizer = document.querySelector(".right_resizer");
        const rangeIndicator = document.querySelector(".range_indicator");

        const totalRect = totalRange.getBoundingClientRect();
        const newPosition = e.pageX - totalRect.x; // 부모 요소 기준 좌표

        if (resizeElement.classList.contains("left_resizer")) {
            if (newPosition >= 0 && newPosition + resizerWidth < rightResizer.offsetLeft) {
                resizeElement.style.left = newPosition + "px";
            }
        } else if (resizeElement.classList.contains("right_resizer")) {
            if (newPosition > leftResizer.offsetLeft + resizerWidth && newPosition <= totalRect.width) {
                resizeElement.style.left = newPosition + "px";
            }
        }

        // range_indicator 위치 및 너비 업데이트
        rangeIndicator.style.left = leftResizer.offsetLeft + "px";
        rangeIndicator.style.width = (rightResizer.offsetLeft - leftResizer.offsetLeft) + "px";

        // Gantt 차트의 날짜 범위 업데이트
        const range = gantt.getSubtaskDates();
        const rangeDuration = gantt.calculateDuration(range.start_date, range.end_date);

        const leftPos = Math.round((leftResizer.offsetLeft / totalRect.width) * rangeDuration);
        const rightPos = Math.round((rightResizer.offsetLeft / totalRect.width) * rangeDuration);

        gantt.config.start_date = gantt.date.add(range.start_date, leftPos, "day");
        gantt.config.end_date = gantt.date.add(range.start_date, rightPos, "day");

        gantt.render();
    }
});




gantt.config.show_tasks_outside_timescale = true;

function ganttInitFunction(){
    gantt.init("gantt_here");
    fetch(`${contextPath}/${companyId}/project/${projId}/dhtmlx`)
        .then(resp => resp.json())
        .then(resp => {
            gantt.clearAll();
            const today = new Date();

            gantt.addMarker({
                start_date: today,
                css: "today",
                text: "Today"
            });
            gantt.parse(resp);
        })
        .catch(error => {console.error(error);});
}

ganttInitFunction();

document.addEventListener("DOMContentLoaded", ()=>{
    const addTaskForm = document.getElementById("addTaskForm");


    new PerfectScrollbar(document.getElementById('vertical-ex2'), {
        wheelPropagation: false
      });


    addTaskForm.addEventListener("submit", (e)=>{
        e.preventDefault();
        
        const formData = new FormData(addTaskForm);

        const result = validateDates(addTaskForm.taskSdate.value, addTaskForm.taskEdate.value);
        
        if(result == false){
            return;
        }
        
        if(addTaskForm.taskNm.value.length === 0){
            Swal.fire({
                title: "오류",
                html: "일감 제목이 공백입니다.<br/>일감 제목을 입력한 후 다시 시도해주세요.",
                icon: "error",
            });
            return;
        }

        if(addTaskForm.taskSdate.value.length === 0 || addTaskForm.taskEdate.value.length === 0){
            Swal.fire({
                title: "오류",
                html: "일감 시작일 또는 일감 종료일이 공백입니다.<br/>다시 입력한 후 다시 시도해주세요.",
                icon: "error",
            });
            return;
        }

        fetch(`${contextPath}/${companyId}/project/${projId}/addTask`, {
            method : "POST",
            body : formData
        })
        .then(resp => {
            if(resp.ok){
                Swal.fire({
                    title: "성공",
                    text: "일감이 성공적으로 등록되었습니다.",
                    icon: "success",
                    timer: 2000,
                    showConfirmButton: true
                });
                resetModalInputs();
                updateProjectLog();
                ganttInitFunction();
                // 모달 닫기
                const modalElement = document.getElementById("largeModal");
                const bootstrapModal = bootstrap.Modal.getInstance(modalElement);
                bootstrapModal.hide();
            }else{
                Swal.fire({
                    title: "오류",
                    text: "일감 등록에 실패했습니다. 다시 시도해주세요.",
                    icon: "error",
                });
            }
        })
        .catch((error) => {
            console.error("Error adding task:", error);
            Swal.fire({
                title: "오류",
                text: "일감 등록에 실패했습니다. 다시 시도해주세요.",
                icon: "error",
            });
        });

    });

    // modal-color 버튼 클릭 이벤트 설정
    document.querySelectorAll('.modal-color').forEach(button => {
        button.addEventListener('click', function () {
            // 선택한 버튼의 data-color 속성 값 가져오기
            const selectedColor = this.getAttribute('data-color');
            
            // id가 newTaskColor인 요소의 배경색 변경
            const newTaskColorElement = document.getElementById('newTaskColor');
            if (newTaskColorElement) {
                newTaskColorElement.style.backgroundColor = selectedColor;
            }

            // id가 modalColor인 input의 값 변경
            const modalColorInput = document.getElementById('taskColor');
            if (modalColorInput) {
                modalColorInput.value = selectedColor;
            }
        });
    });

    // 날짜를 YYYY-MM-DD 형식으로 포맷팅하는 함수
    const formatDate = (date) => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    };

    // 모든 <input type="date"> 요소를 찾음
    const dateInputs = document.querySelectorAll('input[type="date"]');

    // 날짜 변경 이벤트 리스너 추가
    dateInputs.forEach(input => {
        input.addEventListener("change", (event) => {
            const selectedDate = new Date(event.target.value);

            // 선택된 날짜가 범위를 벗어난 경우
            if (selectedDate < projSdate || selectedDate > projEdate) {
                Swal.fire({
                    title: "날짜 선택 오류",
                    html:`선택 가능한 날짜는 <br/> <strong>${formatDate(projSdate)}부터 ${formatDate(projEdate)}</strong> <br/>까지입니다.`,
                    icon: "error",
                });
                event.target.value = ""; // 선택된 날짜를 초기화
            }
        });
    });

    document.body.addEventListener("change", (event) => {
        // 이벤트가 발생한 대상이 input[type="date"]인지 확인
        if (event.target.matches('input[type="date"]')) {
            const selectedDate = new Date(event.target.value);

            // 선택된 날짜가 범위를 벗어나면 경고 메시지 출력
            if (selectedDate < projSdate || selectedDate > projEdate) {
                Swal.fire({
                    title: "날짜 선택 오류",
                    html:`프로젝트 기간 내의 날짜를 선택해 주세요.<br/> <strong>"${formatDate(projSdate)}" 부터 "${formatDate(projEdate)}"</strong> <br/>사이의 날짜로 다시 선택해주세요.`,
                    icon: "error",
                });
                event.target.value = ""; // 날짜 입력 초기화
            }
        }
    });

});

// 날짜 유효성 검증 함수
function validateDates(startInput, endInput) {
    const startDate = new Date(startInput);
    const endDate = new Date(endInput);

    if (startDate && endDate) {
        // 시작일이 종료일 이후일 경우
        if (startDate > endDate) {
            Swal.fire({
                title: "날짜 선택 오류",
                html:`종료일은 시작일 보다 빠를 수 없습니다. 날짜를 다시 선택해주세요.`,
                icon: "error",
            });
            endInput.value = ""; // 종료일 초기화
            return false;
        }
        return true;
    }
}

// 색상 버튼 이벤트 리스너 추가
function offCanvasColorClick() {
    const dropdownMenu = document.querySelector(".dropdown-menu-color");
    console.log("Dropdown menu:", dropdownMenu); // 선택된 요소 확인
    if (dropdownMenu) {
        if (dropdownMenu.style.display === "none" || !dropdownMenu.style.display) {
            dropdownMenu.style.display = "block";
        } else {
            dropdownMenu.style.display = "none";
        }
    } else {
        console.error("Dropdown menu not found.");
    }
}


function ModalColorClick() {
    const dropdownMenuModal = document.querySelector(".dropdown-menu-color-modal");
    console.log("Dropdown menu:", dropdownMenuModal); // 선택된 요소 확인
    if (dropdownMenuModal) {
        if (dropdownMenuModal.style.display === "none" || !dropdownMenuModal.style.display) {
            dropdownMenuModal.style.display = "block";
        } else {
            dropdownMenuModal.style.display = "none";
        }
    } else {
        console.error("Dropdown menu not found.");
    }
}

function offCanvasColorChange(color){
            
    // id가 selectedColor인 요소의 배경색 변경
    const selectedColorElement = document.getElementById('selectedColor');
    if (selectedColorElement) {
        selectedColorElement.style.backgroundColor = color;
    }

    // id가 offcanvasTaskColor인 input의 값 변경
    const offcanvasTaskColorInput = document.getElementById('offcanvasTaskColor');
    if (offcanvasTaskColorInput) {
        offcanvasTaskColorInput.value = color;
    }
}

function changeOffcanvasInput(empId, empName) {
    const taskNm = document.getElementById("taskName");

    Swal.fire({
        title: "일감 담당자 배정",
        html: `'${taskNm.innerText}' 일감의 담당자로<br/>"${empName}"님 으로 선택하시겠습니까?`,
        icon: "question", // Updated icon type to "question"
        showCancelButton: true,
        confirmButtonText: "선택",
        cancelButtonText: "취소",
    }).then((result) => {
        if (result.isConfirmed) {
            const projectMemberid = document.getElementById("projectMemberid");
            const projectMemberName = document.getElementById("projectMemberName");
            
            projectMemberid.value = empId;
            projectMemberName.value = empName;

            // Close the modal
            const modal = document.getElementById("basicModal");
            const bootstrapModal = bootstrap.Modal.getInstance(modal);
            bootstrapModal.hide(); // Close the modal
        }
    });
}

function deleteTask(taskId){
    Swal.fire({
        title: "일감 삭제",
        html: `일감을 삭제하시겠습니까?<br/>삭제시, 하위 일감까지 모두 삭제됩니다.`,
        icon: "question", // Updated icon type to "question"
        showCancelButton: true,
        confirmButtonText: "삭제",
        cancelButtonText: "취소",
    }).then((result) => {
        if (result.isConfirmed) {
            fetch(`${contextPath}/${companyId}/project/${projId}/${taskId}`,{
                method:"DELETE",
            })
            .then(resp => {
                if(!resp.ok){
                    Swal.fire({
                        title: "오류",
                        text: "일감 삭제에 실패했습니다. 다시 시도해주세요.",
                        icon: "error",
                    });
                }
            })
            .then(() => {
                // Close the OffCanvas
                const offCanvasElement = document.getElementById("offcanvasScroll");
                const offCanvasInstance = bootstrap.Offcanvas.getInstance(offCanvasElement);
                if (offCanvasInstance) {
                    offCanvasInstance.hide();
                }

                closeOffCanvas(); // OffCanvas 닫기
                // Reinitialize the Gantt chart
                ganttInitFunction();
                updateProjectLog();
                // Show success alert
                Swal.fire({
                    title: "성공",
                    text: "일감이 성공적으로 삭제되었습니다.",
                    icon: "success",
                    timer: 2000,
                    showConfirmButton: true,
                });
            })
            .catch(error =>{

            });
        }
    });
}

function resetModalInputs() {
    document.getElementById("taskNm").value = ""; 
    document.getElementById("taskContentModal").value = "";
    document.getElementById("taskSdate").value = ""; 
    document.getElementById("taskEdate").value = ""; 
    document.getElementById("taskParentid").value = "";
    document.getElementById("taskModifyMemId").value = empId; 
    document.getElementById("taskColor").value = "#8c77e6"; 

    // Reset the color preview
    const newTaskColorElement = document.getElementById("newTaskColor");
    if (newTaskColorElement) {
        newTaskColorElement.style.backgroundColor = "#8c77e6";
    }
}

function updateProjectLog() {
    fetch(`${contextPath}/${companyId}/project/${projId}/taskList`, {
        method: "GET",
        headers: {
            "Content-type": "application/json"
        }
    })
    .then(resp => {
        if (resp.ok) {
            return resp.json(); // 서버에서 JSON 데이터 반환
        } else {
            throw new Error('Failed to fetch task history.');
        }
    })
    .then(data => {
        const taskHistoryLog = document.getElementById("taskHistoryLog"); // 갱신할 대상 요소
        taskHistoryLog.innerHTML = ""; // 기존 내용을 초기화

        // 새로운 데이터를 기반으로 HTML 갱신
        data.forEach(task => {
            const taskItem = `
                <div class="vertical-timeline-item vertical-timeline-element">
                    <div>
                        <span class="vertical-timeline-element-icon bounce-in">
                            <i class="badge badge-dot badge-dot-xl badge-success"></i>
                        </span>
                        <div class="vertical-timeline-element-content bounce-in">
                            <br/>
                            <h4 class="timeline-title">${task.taskMethod}</h4>
                            <p>${task.empName}님께서 <strong>"${task.taskTitle}"</strong>을(를) <strong>'${task.taskMethod}'</strong>하셨습니다.</p>
                            <span class="vertical-timeline-element-date">${task.taskDate}</span>
                        </div>
                    </div>
                </div>
            `;
            taskHistoryLog.innerHTML += taskItem; // 새 HTML 요소 추가
        });
    })
    .catch(error => {
        console.error("Error fetching task history:", error);
    });
}

const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map((tooltipTriggerEl) => {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });