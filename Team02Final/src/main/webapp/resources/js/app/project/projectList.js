/**
 * 
 */

const accordionActiveFunction = function(e) {
    if (e.type == 'show.bs.collapse') {
      e.target.closest('.accordion-item').classList.add('active');
    } else {
      e.target.closest('.accordion-item').classList.remove('active');
    }
  };

  // 날짜 유효성 검증 함수
function validateDates(startInput, endInput) {
    const startDate = new Date(startInput);
    const endDate = new Date(endInput);

    if (startDate && endDate) {
        // 시작일이 종료일 이후일 경우
        if (startDate > endDate) {
            Swal.fire({
                title: "날짜 선택 오류",
                html:`종료일은 시작일 보다 빠를 수 없습니다.<br/>날짜를 다시 선택해주세요.`,
                icon: "error",
            });
            endInput.value = ""; // 종료일 초기화
            return false;
        }
        return true;
    }
}

document.addEventListener("DOMContentLoaded", ()=>{
    
    const menuBtn = document.getElementById("menuBtn");
    const gridBtn = document.getElementById("gridBtn");
    const container = document.getElementById("container");
    const insertProjectForm = document.getElementById("insertProjectForm");
    const modifyProjectForm = document.getElementById("modifyProjectForm");

    const headerAccordion = document.getElementById('accordionStyle1');
    headerAccordion.addEventListener('show.bs.collapse', accordionActiveFunction);
    headerAccordion.addEventListener('hide.bs.collapse', accordionActiveFunction);
    
    const dataInputBtn = document.getElementById("dataInputBtn");

    dataInputBtn.addEventListener("click", ()=>{
        document.getElementById("projTitle").value = "그룹웨어 프로젝트";
        document.getElementById("projContent").value = "그룹웨어 솔루션 프로젝트";
        document.getElementById("projSdate").value = "2025-01-01";
        document.getElementById("projEdate").value = "2025-01-31";
    });

    modifyProjectForm.addEventListener("submit", (e)=>{
        e.preventDefault();

        const projId = document.getElementById("modifyprojId").value;

        const formData = new FormData(modifyProjectForm);
        const formObject = Object.fromEntries(formData.entries()); // FormData를 JSON으로 변환
        
        const projtitle = modifyProjectForm.projTitle.value ;
        const projcontent = modifyProjectForm.projContent.value ;
        const projsdate = modifyProjectForm.projSdate.value ;
        const projedate = modifyProjectForm.projEdate.value ;

        if(projtitle.length === 0 || projcontent.length === 0 || projsdate.length === 0 || projedate.length === 0){
            Swal.fire({
                title: "오류",
                html:`필수 입력항목이 비어있습니다.<br/> 다시 입력해 주세요.`,
                icon: "error",
            });
            return;
        }

        const result = validateDates(modifyProjectForm.projSdate.value, modifyProjectForm.projEdate.value);

        if(result == false){
            return;
        }

        fetch(`${contextPath}/${companyId}/project/${projId}/update`,{
            method:"POST",
            headers: {
                "Content-type" : "application/json"
            },
            body: JSON.stringify(formObject)
        })
        .then(resp => {
            if(resp.ok){
                return resp.json();
            }
        })
        .then(async data => {
            await Swal.fire({
                title: "성공",
                text: "프로젝트가 성공적으로 수정되었습니다.",
                icon: "success",
                timer: 2000,
                showConfirmButton: true,
            });

            location.reload();
        })
        .catch(error => {
            console.log(error);
        })

    });

    insertProjectForm.addEventListener("submit", (e) => {
        e.preventDefault();
    
        const formData = new FormData(insertProjectForm);
        const formObject = Object.fromEntries(formData.entries()); // FormData를 JSON으로 변환
        
        const projtitle = insertProjectForm.projTitle.value ;
        const projcontent = insertProjectForm.projContent.value ;
        const projsdate = insertProjectForm.projSdate.value ;
        const projedate = insertProjectForm.projEdate.value ;

        if(projtitle.length === 0 || projcontent.length === 0 || projsdate.length === 0 || projedate.length === 0){
            Swal.fire({
                title: "오류",
                html:`필수 입력항목이 비어있습니다.<br/> 다시 입력해 주세요.`,
                icon: "error",
            });
            return;
        }

        const result = validateDates(insertProjectForm.projSdate.value, insertProjectForm.projEdate.value);

        if(result == false){
            return;
        }

        fetch(`${contextPath}/${companyId}/project/new`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(formObject), // JSON 데이터로 변환하여 전송
        })
        .then(resp => {
            if (resp.ok) {
                return resp.json();
            }
        })
        .then( async data  => {
            await Swal.fire({
                title: "성공",
                text: "프로젝트가 성공적으로 생성되었습니다.",
                icon: "success",
                timer: 2000,
                showConfirmButton: true,
            });

            location.reload();
        })
        .catch((error) => {
            console.error(error);
        });
    });
    


    menuBtn.addEventListener("click", (e)=>{
        fetch(`${contextPath}/${companyId}/project/apps`,{
            method : "GET",
            headers : {
                "Content-type" : "application/json"
            }
        })
        .then(resp =>{
            if(resp.ok){
                return resp.json();
            }
        })
        .then((projects)=>{
            console.log(projects);
            container.innerHTML = ""; // 기존 내용을 초기화

                projects.forEach((project) => {
                    let badge = ""; // 각 프로젝트마다 초기화

                    if (project.projStatus === "예정") {
                        badge = `<span class="badge bg-secondary">${project.projStatus}</span>`;
                    } else if (project.projStatus === "진행중") {
                        badge = `<span class="badge bg-success">${project.projStatus}</span>`;
                    } else if (project.projStatus === "종료") {
                        badge = `<span class="badge bg-dark">${project.projStatus}</span>`;
                    }
                    // 카드 생성
                    const card = `
                       			<!-- CASE1 -->
                    <div class="row">
                    <div class="col-md">
                        <div class="card mb-4">
                        <div class="row g-0">
                            <div class="col-md-12">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-center">
                                <div class="d-flex align-items-center gap-2">
                                    <h5 class="card-title mb-0">
                                    <a href="${contextPath}/${companyId}/project/${project.projId}">${project.projTitle}</a>
                                    </h5>
                                    ${badge}
                                </div>
                                <div class="dropdown">
                                    <button type="button" class="btn p-0 dropdown-toggle hide-arrow" data-bs-toggle="dropdown">
                                    <i class="bx bx-dots-vertical-rounded"></i>
                                    </button>
                                    <div class="dropdown-menu">
                                    <a class="dropdown-item" href="javascript:void(0);" onclick="openModifyProjectModal('${project.projId}')">
                                        <i class="bx bx-edit-alt me-1"></i>수정
                                    </a>
                                    <a class="dropdown-item" href="javascript:void(0);" onclick="deleteProject('${project.projId}')">
                                        <i class="bx bx-trash me-1"></i>삭제
                                    </a>
                                    </div>
                                </div>
                                </div>
                                <br/>
                                <p class="card-text">${project.projContent}</p>
                                <div class="row align-items-center mb-2">
                                <!-- 프로젝트 진행기간 -->
                                <div class="col-auto">
                                    <label for="start-date" class="form-label">프로젝트 진행기간</label>
                                    <div class="d-flex align-items-center gap-2">
                                    <input class="form-control" type="date" value="${project.projSdate}" id="start-date" style="width: 150px; background-color: white;" readonly/>
                                    <span>~</span>
                                    <input class="form-control" type="date" value="${project.projEdate}" id="end-date" style="width: 150px; background-color: white;" readonly/>
                                    </div>
                                </div>
                                
                                <!-- 담당자 -->
                                <div class="col-auto ms-auto">
                                    <label for="projectManager" class="form-label">담당자</label>
                                    <input type="text" class="form-control" id="projectManager" value="${project.projectDto.empName}" readonly style="width: 100px; text-align:center; background-color: white;" />
                                </div>
                                </div>
                    
                                <label for="datePercent" class="form-label">기간별 진행도</label>
                                <div class="progress mb-2" style="height: 16px;">
                                <div class="progress-bar" id="datePercent" role="progressbar" style="width: ${project.calculateProgress}%;" aria-valuenow="${project.calculateProgress}" aria-valuemin="0" aria-valuemax="100">${project.calculateProgress}%</div>
                                </div>
                                <label for="workPercent" class="form-label">업무별 진행도</label>
                                <div class="progress mb-2" style="height: 16px;">
                                <div class="progress-bar" id="workPercent" role="progressbar" style="width: ${project.taskCalculateProgress}%;" aria-valuenow="${project.taskCalculateProgress}" aria-valuemin="0" aria-valuemax="100">${project.taskCalculateProgress}%</div>
                                </div>
                            </div>
                            </div>
                        </div>
                        </div>
                    </div>
                    </div>
                    `;
                    container.innerHTML += card;
                });
        })
        .catch(error => {
            // sweetAlert
        })
    });   


    gridBtn.addEventListener("click", (e)=>{

        fetch(`${contextPath}/${companyId}/project/apps`,{
            method : "GET",
            headers : {
                "Content-type" : "application/json"
            }
        })
        .then(resp =>{
            if(resp.ok){
                return resp.json();
            }
        })
        .then((projects)=>{
            container.innerHTML = ""; // 기존 내용을 초기화
                let row = null;

                projects.forEach((project, index) => {
                    if (index % 2 === 0) {
                        // 두 개씩 배치할 행 생성
                        row = document.createElement("div");
                        row.className = "row";
                        container.appendChild(row);
                    }

                    let badge = ""; // 각 프로젝트마다 초기화

                    if (project.projStatus === "예정") {
                        badge = `<span class="badge bg-secondary">${project.projStatus}</span>`;
                    } else if (project.projStatus === "진행중") {
                        badge = `<span class="badge bg-success">${project.projStatus}</span>`;
                    } else if (project.projStatus === "종료") {
                        badge = `<span class="badge bg-dark">${project.projStatus}</span>`;
                    }

                    // 카드 생성
                    const card = `
                        <div class="col-md-6">
                            <div class="card mb-4">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <div class="d-flex align-items-center gap-2">
                                            <h5 class="card-title mb-0"><a href="${contextPath}/${companyId}/project/${project.projId}">${project.projTitle}</a></h5>
                                            ${badge}
                                        </div>
                                        <div class="dropdown">
                                            <button type="button" class="btn p-0 dropdown-toggle hide-arrow" data-bs-toggle="dropdown">
                                                <i class="bx bx-dots-vertical-rounded"></i>
                                            </button>
                                            <div class="dropdown-menu">
                                                <a class="dropdown-item" href="javascript:void(0);" onclick="openModifyProjectModal('${project.projId}')"><i class="bx bx-edit-alt me-1"></i>수정</a>
                                                <a class="dropdown-item" href="javascript:void(0);" onclick="deleteProject('${project.projId}')"><i class="bx bx-trash me-1"></i>삭제</a>
                                            </div>
                                        </div>
                                    </div>
                                    <br />
                                    <p class="card-text">${project.projContent}</p>
                                    <div class="row align-items-center mb-2">
                                        <div class="col-auto">
                                            <label for="start-date" class="form-label">프로젝트 진행기간</label>
                                            <div class="d-flex align-items-center gap-2">
                                                <input class="form-control" type="date" value="${project.projSdate}" id="start-date" style="width: 150px; background-color: white;" readonly />
                                                <span>~</span>
                                                <input class="form-control" type="date" value="${project.projEdate}" id="end-date" style="width: 150px; background-color: white;" readonly />
                                            </div>
                                        </div>
                                        <div class="col-auto ms-auto">
                                            <label for="projectManager" class="form-label">담당자</label>
                                            <input type="text" class="form-control" id="projectManager" value="${project.projectDto.empName}" readonly style="width: 100px; background-color: white; text-align:center;" />
                                        </div>
                                    </div>
                                    <label for="datePercent" class="form-label">기간별 진행도</label>
                                    <div class="progress mb-2" style="height: 16px;">
                                        <div class="progress-bar" id="datePercent" role="progressbar" style="width: ${project.calculateProgress}%;" aria-valuenow="${project.calculateProgress}" aria-valuemin="0" aria-valuemax="100">${project.calculateProgress}%</div>
                                    </div>
                                    <label for="workPercent" class="form-label">업무별 진행도</label>
                                    <div class="progress mb-2" style="height: 16px;">
                                        <div class="progress-bar" id="workPercent" role="progressbar" style="width: ${project.taskCalculateProgress}%;" aria-valuenow="${project.taskCalculateProgress}" aria-valuemin="0" aria-valuemax="100">${project.taskCalculateProgress}%</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                    row.innerHTML += card;
                });
        })
        .catch(error => {
            // sweetAlert
        })
    });

});

function getForm(){
    location.href = `${contextPath}/${companyId}/project/form`;
}

function deleteProject(projectId){

    Swal.fire({
        title: "프로젝트 삭제",
        html: `프로젝트를 정말로 삭제하시겠습니까?<br>삭제시, 프로젝트의 모든 정보가 사라집니다.`,
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: '승인',
        cancelButtonText: '취소',
        confirmButtonColor: 'blue',
        cancelButtonColor: 'red',
        reverseButtons: false,
    }).then(result => {
        if (result.isConfirmed) {

            fetch(`${contextPath}/${companyId}/project/${projectId}`,{
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            })
            .then(resp =>{
                if(resp.ok){
                    Swal.fire({
                        title: "삭제 성공",
                        text: "프로젝트가 성공적으로 삭제 되었습니다.",
                        icon: "success",
                        timer:2000,
                        showConfirmButton : false
                    });
        
                // 성공 시 페이지 새로고침 또는 다른 작업 수행
                    setTimeout(() => {
                            location.reload();
                    }, 2000);
                }else{
                    Swal.fire({
                        title: "오류",
                        text: "프로젝트 삭제 실패.",
                        icon: "error"
                    });
                }
            })

        } else {
            Swal.fire("취소 완료", "취소 버튼을 눌렀습니다.", "error");
        }
    });
}

function openModifyProjectModal(projId){

    fetch(`${contextPath}/${companyId}/project/${projId}`,{
        method : "POST",
        headers : {
            "Content-type" : "application/json"
        }
    })
    .then(resp => {
        if(resp.ok){
            return resp.json();
        }
    })
    .then(data => {
        document.getElementById("modifyprojTitle").value = data.projTitle;
        document.getElementById("modifyprojContent").value = data.projContent;
        document.getElementById("modifyprojSdate").value = data.projSdate;
        document.getElementById("modifyprojEdate").value = data.projEdate;
        document.getElementById("modifyprojId").value = projId;

         // projStatus 값을 설정
        const projStatusElement = document.getElementById("projStatus");
        projStatusElement.value = data.projStatus;

        const modal = document.getElementById("modifyProjectModal");
        const bootstrapModal = new bootstrap.Modal(modal);
        bootstrapModal.show();
    })
    .catch(error => {
        console.error(error);
    })
}