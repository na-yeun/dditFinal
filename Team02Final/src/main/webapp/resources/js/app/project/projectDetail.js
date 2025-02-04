/**
 * 
 */
function newvalidateDates(startInput, endInput) {
    const startDate = new Date(startInput);
    const endDate = new Date(endInput);

    if (startDate && endDate) {
        // 시작일이 종료일 이후일 경우
        if (startDate > endDate) {
            return false;
        }
        return true;
    }
}

function clearForm(){
    document.getElementById("projectMember-form").innerHTML = "";
    ReferenceOrgTree.tempReferences.clear(); // 임시 저장소 초기화
    ReferenceOrgTree.confirmedReferences.clear(); // 확정된 저장소도 초기화
}

function addInputData(){
    const taskName = "수행계획서 검토";
    const taskCon = "수행계획서 검토 및 피드백"
    const startDate = "2024-11-17";
    const endDate = "2024-11-20";

    document.getElementById("taskNm").value = taskName;
    document.getElementById("taskContentModal").value = taskCon;
    document.getElementById("taskSdate").value = startDate;
    document.getElementById("taskEdate").value = endDate;
}

document.addEventListener("DOMContentLoaded", async ()=>{
    
    new PerfectScrollbar(document.getElementById('vertical-example'), {
        wheelPropagation: false
      });

    // 4. 참조자 조직도 트리 불러오기
    await ReferenceOrgTree.loadOrgTreeWithEmployees();

    // 5. 결재선 관리자 초기화
    ApprovalLineManager.initialize();

    // 모달 이벤트 리스너 추가

    // 임시 저장소를 확정 데이터로 초기화
    ReferenceOrgTree.initializeTempReferences();
    ReferenceOrgTree.renderReferenceList();


    ReferenceOrgTree.clearTempReferences();
    

    //확인 버튼 이벤트 추가
    // 확인 버튼 이벤트 추가
if (confirmRefBtn) {
    confirmRefBtn.addEventListener('click', async () => {
        ReferenceOrgTree.confirmReferences(); // 임시 데이터를 확정 데이터로 저장

        // 멤버 데이터 수집
        const rows = document.querySelectorAll('#projectMember-form tr');
        const members = Array.from(rows).map(row => ({
            teamId: row.querySelector('[name*="teamId"]').value,
            projId: row.querySelector('[name*="projId"]').value,
            joinDate: row.querySelector('[name*="joinDate"]').value,
            leaveDate: row.querySelector('[name*="leaveDate"]').value,
            projectRolenm: row.querySelector('[name*="projectRolenm"]').value,
            empId: row.querySelector('[name*="empId"]').value
        }));

        // 멤버 데이터 검증
        for (const member of members) {
            // 날짜 유효성 검증
            const sdateEdateValid = newvalidateDates(member.joinDate, member.leaveDate);
            if (!sdateEdateValid) {
                await Swal.fire({
                    title: "날짜 선택 오류",
                    html: `종료일은 시작일보다 빠를 수 없습니다. 날짜를 다시 선택해주세요.`,
                    icon: "error",
                });
                return; // 검증 실패 시 함수 종료
            }

            // 필수 입력 값 검증
            if (
                member.joinDate.trim() === "" ||
                member.leaveDate.trim() === "" ||
                member.projectRolenm.trim() === ""
            ) {
                await Swal.fire({
                    title: "오류",
                    html: `필수 입력 칸이 비어있습니다.<br/> 다시 작성후 등록해주세요.`,
                    icon: "error",
                });
                return; // 검증 실패 시 함수 종료
            }
        }

        // 전송할 데이터 로깅
        console.log("전송할 데이터:", JSON.stringify({ members }));

        // 서버로 데이터 전송
        fetch(`${contextPath}/${companyId}/project/${projId}/addMembers`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ members }),
        })
            .then((resp) => {
                if (resp.ok) {
                    Swal.fire({
                        title: "성공",
                        text: "참가자가 성공적으로 추가되었습니다.",
                        icon: "success",
                        timer: 2000,
                        showConfirmButton: true,
                    });
                    updateProjectMemberTable();
                    document.getElementById("projectMember-form").innerHTML = "";
                    // 모달 닫기
                    const modal = bootstrap.Modal.getInstance(
                        document.getElementById("referenceModal")
                    );
                    if (modal) {
                        modal.hide();
                    }
                } else {
                    Swal.fire({
                        title: "오류",
                        html: `참가자 추가 중 오류가 발생했습니다.<br/> 다시 시도해주세요.`,
                        icon: "error",
                    });
                }
            })
            .catch((error) => {
                Swal.fire({
                    title: "오류",
                    html: `참가자 추가 중 오류가 발생했습니다.<br/> 다시 시도해주세요.`,
                    icon: "error",
                });
            });
    });
}


    const projectMemberModifyForm = document.getElementById("projectMemberModifyForm");
    
    projectMemberModifyForm.addEventListener("submit", (e)=>{
        e.preventDefault();
        const projectMemberid = document.getElementById('projectMemberid').value;

        const updatedData = {
            projectRolenm: document.getElementById('rolenm').value,
            joinDate: document.getElementById('joinDate').value,
            leaveDate: document.getElementById('leaveDate').value,
            projectMemberid:projectMemberid
        };

        const result = validateDates(document.getElementById('joinDate').value, document.getElementById('leaveDate').value);
        
        if(result == false){
            return;
        }

        fetch(`${contextPath}/${companyId}/project/projectMember/${projectMemberid}`, {
            method:"PUT",
            headers : {
                "Content-type" : "application/json"
            },
            body: JSON.stringify(updatedData)
        })
        .then(response => {
            if (response.ok) {
                Swal.fire({
                    title: "성공",
                    text: "참가자 정보가 성공적으로 수정되었습니다.",
                    icon: "success",
                    timer: 2000,
                    showConfirmButton: true
                });


                updateProjectMemberTable();

                const modalElement = document.getElementById("projectMemberModifyModal");
                const bootstrapModal = bootstrap.Modal.getInstance(modalElement);
                bootstrapModal.hide();
            } else {
                throw new Error('Failed to update project member.');
            }
        })
        .catch(error => {
            console.error('Error updating project member:', error);
            alert('수정에 실패했습니다.');
        });


    });

});

function updateProjectMemberTable() {
    fetch(`${contextPath}/${companyId}/project/${projId}/projectMember`, {
        method: "GET",
        headers: {
            "Content-type": "application/json",
        },
    })
    .then((resp) => {
        if (resp.ok) {
            return resp.json();
        } else {
            throw new Error("Failed to fetch project member list.");
        }
    })
    .then((data) => {
        // 서버에서 받아온 데이터를 바탕으로 테이블 업데이트
        const tableBody = document.getElementById("projectMemberTable");
        tableBody.innerHTML = ""; // 기존 내용 제거

        data.forEach((member) => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td><span>${member.projectRolenm}</span></td>
                <td>${member.departName}</td>
                <td>${member.empName}</td>
                <td>${member.codeComment}</td>
                <td>${member.joinDate}</td>
                <td>${member.leaveDate}</td>
                <td>
                    <div class="dropdown">
                        <button type="button" class="btn p-0 dropdown-toggle hide-arrow" data-bs-toggle="dropdown"><i class="bx bx-dots-vertical-rounded"></i></button>
                        <div class="dropdown-menu">
                            <a class="dropdown-item" href="javascript:void(0);" onclick="modifyProjectMember('${member.projectMemberid}');"><i class="bx bx-edit-alt me-1"></i>수정</a>
                            <a class="dropdown-item" href="javascript:void(0);" onclick="deleteProjectMember('${member.projectMemberid}');"><i class="bx bx-trash me-1"></i>삭제</a>
                        </div>
                    </div>
                </td>
            `;

            tableBody.appendChild(row);
        });
    })
    .catch((error) => {
        console.error("Error updating project member table:", error);
    });
}


function modifyProjectMember(projectMemberId) {
    console.log("Received projectMemberId:", projectMemberId); // 값 확인
    fetch(`${contextPath}/${companyId}/project/projectMember/${projectMemberId}`)
    .then(resp => {
        if (resp.ok) {
            return resp.json();
        }
    })
    .then(data => {
        console.log("Fetched data:", data); // 데이터 확인
        document.getElementById('name').value = data.empName; // 이름
        document.getElementById('department').value = data.departName; // 부서
        document.getElementById('position').value = data.codeComment; // 직급
        document.getElementById('rolenm').value = data.projectRolenm; // 역할
        document.getElementById('joinDate').value = data.joinDate; // 투입일
        document.getElementById('leaveDate').value = data.leaveDate; // 투입 종료일
        document.getElementById('projectMemberid').value = projectMemberId;

        const modal = new bootstrap.Modal(document.getElementById('projectMemberModifyModal'));
        modal.show();
    })
    .catch(error => {
        console.error("Error fetching data:", error); // 오류 로그 확인
        Swal.fire({
            title: "오류",
            html: "참가자 정보를 불러오는데 실패했습니다.<br/> 다시 시도해주세요.",
            icon: "error",
        });
    });
}


function deleteProjectMember(projectMemberId){
    Swal.fire({
        title: "참가자 삭제",
        html: `해당 참여자를 삭제하시겠습니까 ?`,
        icon: "question", // Updated icon type to "question"
        showCancelButton: true,
        confirmButtonText: "삭제",
        cancelButtonText: "취소",
    }).then((result) => {
        if (result.isConfirmed) {
            fetch(`${contextPath}/${companyId}/project/projectMember/${projectMemberId}`, {
                method: "DELETE",
                headers:{
                    "Content-type" : "application/json"
                }
            })
            .then(resp => {
                if(resp.ok){
                    return resp.json();
                }
            })
            .then(data => {
                Swal.fire({
                    title: "성공",
                    text: "참가자 정보가 성공적으로 삭제되었습니다.",
                    icon: "success",
                    timer: 2000,
                    showConfirmButton: true
                });
                updateProjectMemberTable();
            })
            .catch(error => {
                console.error(error);
                Swal.fire({
                    title: "오류",
                    html: "참가자 정보를 삭제하던 도중 실패했습니다.<br/> 다시 시도해주세요.",
                    icon: "error",
                });
            })
        }
    });
}

const ReferenceOrgTree = {
    // 모달에서 임시로 사용할 저장소
    tempReferences: new Set(),
    // 확정된 참조자 저장소
    confirmedReferences: new Set(),

    //참조자용 조직도 디렉토리 로딩 원래는 합칠까했는데 충돌이 너무일어나서 그냥 분리하기로함
    async loadOrgTreeWithEmployees() {
        try {
            console.log('참조자용 조직도 데이터 로딩 시작');
            const [teamResp, empResp] = await Promise.all([
                fetch(teamHistoryUrl),
                fetch(empUrl)
            ]);

            const [teamData, empData] = await Promise.all([
                teamResp.json(),
                empResp.json()
            ]);

            const employeeByDept = {};
            empData.forEach(emp => {
                if (!employeeByDept[emp.departCode]) {
                    employeeByDept[emp.departCode] = [];
                }
                employeeByDept[emp.departCode].push(emp);
            });

            const treeData = this.buildTreeData(teamData, employeeByDept);
            const treeHtml = this.renderTree(treeData);

            const refOrgTreeElement = document.getElementById('refOrgTree');
            if (refOrgTreeElement) {
                refOrgTreeElement.innerHTML = treeHtml;
                this.setRefTreeEventListeners(refOrgTreeElement);
            }
        } catch (error) {
            console.error('참조자 조직도 로딩 중 에러:', error);
        }
    },

    //참조자 디렉토리 클릭 리스너
    setRefTreeEventListeners(treeElement) {
        treeElement.addEventListener('click', (e) => {
            const span = e.target.closest('span');
            if (!span) return;

            const li = span.closest('li');
            if (li && li.classList.contains('folder')) {
                li.classList.toggle('open');
                console.log('참조자 폴더 토글');
            }

            //검증통과하면 추가시키기
            if (span.hasAttribute('data-emp-id')) {
                const employeeInfo = this.extractEmployeeInfo(span);
                if (this.validateReferenceSelection(employeeInfo)) {
                    this.addReference(employeeInfo);
                }
            }
        });
    },


    // 나머지 메서드들은 OrgTreeManager와 유사하지만 이름을 다르게 지정
    buildTreeData(teamHistoryList) {
        const tree = [];
        const map = {};
    
        // teamId를 기준으로 직원 데이터를 그룹화
        const teams = teamHistoryList.reduce((acc, teamHistory) => {
            const { teamId, empId, teamHistoryDTO } = teamHistory;
    
            if (!acc[teamId]) {
                acc[teamId] = {
                    teamId,
                    departCode: teamHistoryDTO.departCode,
                    departName: teamHistoryDTO.departName,
                    codeComment: teamHistoryDTO.codeComment,
                    employees: []
                };
            }
    
            acc[teamId].employees.push({
                empId,
                empName: teamHistoryDTO.empName
            });
    
            return acc;
        }, {});
    
        // 트리 구조 생성
        Object.values(teams).forEach(team => {
            map[team.teamId] = {
                ...team,
                children: [] // 계층적 확장이 필요할 경우를 대비
            };
            tree.push(map[team.teamId]);
        });
    
        return tree;
    },

    //orgTree랑똑같음
    renderTree(node) {
        if (!Array.isArray(node) || node.length === 0) return '';
    
        let html = '<ul>';
        node.forEach(item => {
            if (!item) return;
    
            // teamId 출력
            html += `<li class="folder">`;
            html += `<span data-code="${item.teamId}" class="team-info">${item.teamId}</span>`;
    
            // 직원 목록 출력
            if (item.employees && item.employees.length > 0) {
                html += '<ul class="employees">';
                item.employees.forEach(emp => {
                    html += `<li class="employee">
                        <span data-emp-id="${emp.empId}">
                            ${item.codeComment} ${emp.empName}
                        </span>
                    </li>`;
                });
                html += '</ul>';
            }
    
            // 하위 팀(children) 출력
            if (Array.isArray(item.children) && item.children.length > 0) {
                html += this.renderTree(item.children);
            }
    
            html += `</li>`;
        });
        html += '</ul>';
    
        return html;
    },

    //참조자 리스트 렌더링하기
    renderReferenceList() {
        const projectMemberForm = document.getElementById('projectMember-form');
        if (!projectMemberForm) return;
    
        projectMemberForm.innerHTML = Array.from(this.tempReferences)
            .map((ref, index) => { // `index`를 동적으로 가져옴
                const [position, name] = ref.name.split(' ');
    
                return `
                    <tr>
                        <td>
                            <input type="text" readonly class="form-control-plaintext" name="members[${index}].teamId" value="${ref.departmentName}" />
                        </td>
                        <td>
                            <input type="text" readonly class="form-control-plaintext" name="members[${index}].empName" value="${name}" />
                        </td>
                        <td>
                            <input type="text" readonly class="form-control-plaintext" name="members[${index}].codeComment" value="${position}" />
                        </td>
                        <td>
                            <div class="mb-3 row">
                                <input class="form-control" type="date" name="members[${index}].joinDate" required="required"  />
                            </div>
                        </td>
                        <td>
                            <div class="mb-3 row">
                                <input class="form-control" type="date" name="members[${index}].leaveDate" required="required" />
                            </div>
                        </td>
                        <td>
                            <div class="mb-3 row">
                                <input type="text" class="form-control" name="members[${index}].projectRolenm" placeholder="역할" maxlength="20" required="required" />
                            </div>
                        </td>
                        <td style="display : none;">
                            <input type="text" name="members[${index}].empId" value="${ref.empId}" readonly> 
                        </td>
                        <td style="display : none;">
                            <input type="text" name="members[${index}].projId" value="${projId}" readonly> 
                        </td>
                        <td>
                            <button class="btn-delete" data-emp-id="${ref.empId}">×</button>
                        </td>
                    </tr>
                `;
            }).join('');
    
        // 삭제 버튼에 이벤트 리스너 추가
        this.setDeleteButtonListeners();
    },

    //삭제 버튼 리스너
    setDeleteButtonListeners() {
        const deleteButtons = document.querySelectorAll('#projectMember-form .btn-delete');
        deleteButtons.forEach(button => {
            button.addEventListener('click', (e) => {
                const empId = e.target.getAttribute('data-emp-id');
                this.removeReference(empId);
            });
        });
    },

    // 선택한 사원 정보 추출하기
    extractEmployeeInfo(span) {
        const employeeLi = span.closest('li');
        const departmentLi = employeeLi.closest('li.folder');
        return {
            empId: span.getAttribute('data-emp-id'),
            departmentName: departmentLi ? departmentLi.querySelector('span').textContent.trim() : '부서없음',
            name: span.textContent.replace(/\([^)]*\)/g, '').trim(),
            position: span.textContent.split(' ')[0]
        };
    },

    // 사원 선택 유효성 검사 (참조자용으로 수정)
    validateReferenceSelection(employeeInfo) {
        console.log('검증할 참조자 정보:', employeeInfo);
    
    
        // 중복 선택 방지 (empId로 확인)
        if (Array.from(this.tempReferences).some(ref => ref.empId === employeeInfo.empId)) {
            Swal.fire({
                title: "오류",
                html: "이미 등록되어있는 참가자입니다.<br/> 검토 후 다시 수정해 주세요.",
                icon: "error",
            });
            return false;
        }
    
        // Step 2: 문자열을 배열로 변환
        let projectMemberToString = projectMemberList
            .replace(/\[(.+)\]/, "$1") // 대괄호 제거
            .split("), ")              // 각 객체를 나누기
            .map(item => {
                // 객체로 변환
                const properties = item.replace(/ProjectMemberVO\(|\)/g, "").split(", ");
                const obj = {};
                properties.forEach(prop => {
                    const [key, value] = prop.split("=");
                    obj[key.trim()] = value === "null" ? null : value.trim();
                });
                return obj;
            });

        // Step 3: 중복 확인
        const isDuplicate = projectMemberToString.some(member => member.empId === employeeInfo.empId);

        if (isDuplicate) {
            Swal.fire({
                title: "오류",
                html: "이미 등록되어있는 참가자입니다.<br/> 검토 후 다시 수정해 주세요.",
                icon: "error",
            });
            return false;
        } else {

        }
            
        return true;
    },

    //참조자 추가 시에는 임시 저장소에 추가해놓기
    addReference(employeeInfo) {
        this.tempReferences.add(employeeInfo);
        this.renderReferenceList();
    },

    // 확인 버튼 클릭 시 임시 저장소의 데이터를 확정 저장소로 이동시키기
    confirmReferences() {
        this.confirmedReferences = new Set(this.tempReferences);
        // this.displayReferencesInDocument();
    },

    // 모달 닫거나 할 시에는 임시 저장소만 초기화하기
    clearTempReferences() {
        this.tempReferences.clear();
        this.renderReferenceList();
    },

    //참조자 삭제하기
    removeReference(empId) {
        this.tempReferences = new Set(
            Array.from(this.tempReferences).filter(ref => ref.empId !== empId)
        );

        // 확정 저장소에서도 삭제
        this.confirmedReferences = new Set(
            Array.from(this.confirmedReferences).filter(ref => ref.empId !== empId)
        );

        this.renderReferenceList();
        this.displayReferencesInDocument(); // 문서의 참조자 목록도 업데이트
    },

    // 모달 열릴 때 임시 저장소를 확정 데이터로 초기화하기
    initializeTempReferences() {
        this.tempReferences = new Set(this.confirmedReferences);
    },

    // 기안용 참조자 데이터 반환 시에는 확정 저장소 사용하기
    getReferencesForDraft() {
        return Array.from(this.confirmedReferences);
    }
};

const ApprovalLineManager = {

    //처음엔 널로
    selectedLineId: null,
    //현재 결재자 정보를 저장할 배열 추가하기 참조자선택할때 결재자랑 중복되면 막기위해서 생성함
    //얘는 static으로 유지되어야함
    currentApprovers: [],

    //초기화
    initialize(){
        this.setEventListeners();
        console.log('ApprovalLIneManager 초기화됨');
    },

    //모달 취소버튼을 눌렀을때
    handleCancel(){
        // 선택 상태를 미리보기에 적용된 결재선으로 복구하기
        this.selectedLineId = ApprovalPreview.appliedLineId;

        //모달 닫기
        const modal = bootstrap.Modal.getInstance(document.getElementById('approvalLineModal'));
        modal.hide;
    },

    setEventListeners() {
        const saveBtn = document.getElementById('saveApprovalLineBtn');
        const confirmBtn = document.getElementById('confirmLineBtn');
        const cancelLineBtn = document.getElementById('cancelLineBtn');

        //모달에서 결재선 저장버튼을 눌렀을때 실행되는 리스너
        saveBtn?.addEventListener('click', async () => {
            const approvers = ApprovalLineView.collectApprovers();
            await this.saveLine(approvers);
        });

        //결재자 모달에서 확인버튼을 눌렀을때 실행되는 리스너
        confirmBtn?.addEventListener('click', async () => {
            if (this.selectedLineId) {
                try {
                    // 결재선 적용하기
                    await ApprovalPreview.saveApprovalLine(this.selectedLineId);

                    // 참조자 한번 초기화하기(재지정한 결재자와 참조자가 겹칠수있기때문임)
                    ReferenceOrgTree.clearTempReferences();

                    // 참조자 목록 영역을 숨기기
                    const referenceDisplayArea = document.querySelector('.reference-display-area');
                    if (referenceDisplayArea) {
                        referenceDisplayArea.style.display = 'none';
                    }

                    // 참조자 버튼은 다시든 처음이든 노출시키기
                    ApprovalCommon.showReferenceButton();

                    // 모달 닫기
                    const modal = bootstrap.Modal.getInstance(document.getElementById('approvalLineModal'));
                    modal.hide();

                } catch (error) {
                    console.error('결재선 적용 중 오류:', error);
                    alert('결재선 적용 중 오류가 발생했습니다.');
                }
            }
        });

        //모달에서 취소버튼을 눌렀을때 실행되는 리스너
        cancelLineBtn?.addEventListener('click', () => {
            this.handleCancel();
        });
    },

    // 결재선이 완전히 지정되었는지 검증
    isLineFullySelected() {
        // selectedLineId 체크 (결재선 선택 여부)
        if (!this.selectedLineId) {
            return false;
        }
        // 현재 결재자 배열 체크
        if (!this.currentApprovers || this.currentApprovers.length === 0) {
            return false;
        }
        return true;
    },


    //기안버튼 눌렀을때 결재자가 최소1명이상인지 검증하기 위한 메소드
    hasSelectedApprovers() {
        return this.currentApprovers && this.currentApprovers.length > 0;
    },

    //결재선 저장
    async saveLine(approvers){
        if (approvers.length === 0) {
            alert('최소 한명 이상의 결재자를 지정해주세요');
            return;
        }

        const title = document.getElementById('approvalLineTitle').value.trim() || '(제목없음)';

        try {
            const resp = await fetch(saveApprovalLineUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    apprlineTitle: title,
                    approvers: approvers
                })
            });

            if (!resp.ok) {
                throw new Error('결재선 저장중에 오류가 발생한듯~');
            }

            const savedLine = await resp.json();
            console.log('저장된 결재선 정보:', savedLine);
            console.log('결재자 목록:', savedLine.approvers);

            //여기서 현재 결재자 정보를 담는다
            this.setCurrentApprovers(savedLine.approvers);
            console.log('담긴 후 현재 결재자들:', this.currentApprovers);
            ApprovalLineView.handleSaveSuccess(savedLine);
            console.log('저장 후 현재 결재자들:', this.currentApprovers);
        } catch (error){
            console.error('결재선 저장 중 오류:', error);
            alert('결재선 저장 중 오류가 발생했습니다');
        }
    },

    // 결재자 정보 설정 메서드 추가(오로지 참조자 결재자 중복체크하기위해)
    setCurrentApprovers(approvers) {
        this.currentApprovers = approvers;
        console.log('결재자 정보 업데이트됨:', this.currentApprovers);
    },

    // 결재자 체크하기
    isApprover(empId) {
        console.log('중복 체크 시작 - 현재 결재자들:', this.currentApprovers);
        return Array.isArray(this.currentApprovers) &&
            this.currentApprovers.length > 0 &&
            this.currentApprovers.some(approver => approver.EMPID === empId);
    },

    // 결재선 선택 시
    selectLine(lineId) {
        this.selectedLineId = lineId;
        // 선택된 결재선의 결재자 정보도 저장
        const selectedLine = document.querySelector(`[data-line-id="${lineId}"]`);
        if (selectedLine) {
            //renderSaveId에서 갖고온 span태그 클래스의 approver-info
            const approverInfos = selectedLine.querySelectorAll('.approver-info');
            const approvers = Array.from(approverInfos).map(info => ({
                EMPID: info.dataset.empId,
                NAME: info.dataset.name,
                POSITION: info.dataset.position,
                DEPARTMENTNAME: info.dataset.department
            }));

            console.log('선택된 결재선의 결재자들:', approvers);
            this.setCurrentApprovers(approvers);
        }
        //확인 버튼 활성화
        document.getElementById('confirmLineBtn').disabled= false;
    },


}



