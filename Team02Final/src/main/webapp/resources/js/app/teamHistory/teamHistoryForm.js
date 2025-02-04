/**
 * 
 */

document.addEventListener("DOMContentLoaded", async ()=>{
    
    new PerfectScrollbar(document.getElementById('vertical-example'), {
        wheelPropagation: false
      });

    // 4. 참조자 조직도 트리 불러오기
    await ReferenceOrgTree.loadOrgTreeWithEmployees();

    // 5. 결재선 관리자 초기화
    ApprovalLineManager.initialize();

    const memberAddBtn = document.getElementById("memberAddBtn");
    // 모달 이벤트 리스너 추가

    const referenceModal = document.getElementById('referenceModal');
    const confirmRefBtn = document.getElementById('confirmRefBtn');
    if (referenceModal) {
        referenceModal.addEventListener('show.bs.modal', async () => {
            // 임시 저장소를 확정 데이터로 초기화
            ReferenceOrgTree.initializeTempReferences();
            ReferenceOrgTree.renderReferenceList();
        });

        referenceModal.addEventListener('hidden.bs.modal', () => {
            ReferenceOrgTree.clearTempReferences();
        });
    }

    //확인 버튼 이벤트 추가
    if (confirmRefBtn) {
        confirmRefBtn.addEventListener('click', () => {
            ReferenceOrgTree.confirmReferences();  // 임시 데이터를 확정 데이터로 저장
        const teamId = document.getElementById('teamId').value;
            // 멤버 데이터 수집
        const rows = document.querySelectorAll('#projectMember-form tr');
        const members = Array.from(rows).map(row => ({
            teamId: teamId,
            empId: row.querySelector('[name*="empId"]').value
        }));

        if(teamId.length === 0){
            Swal.fire({
                title: "팀등록 실패",
                html: "팀이름을 작성하지 않았습니다.<br/> 작성 후 다시 등록해주세요.",
                icon: "error"
            });
            return;
        }
        if(members.length === 0){
            Swal.fire({
                title: "팀등록 실패",
                html: "팀원을 추가하지 않았습니다.<br/> 추가 후 다시 등록해주세요.",
                icon: "error"
            });
            return;            
        }

        console.log('전송할 데이터:', JSON.stringify({ members }));

        // 서버로 데이터 전송
        fetch(`${contextPath}/${companyId}/teamHistory/addMembers`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ members })
        })
        .then(resp=>{
            if (resp.ok) {
                Swal.fire({
                    title: "성공",
                    text: "팀이 성공적으로 등록되었습니다.",
                    icon: "success",
                    timer: 2000,
                    showConfirmButton: true
                });
                teamUpdate();
                // 모달 닫기
                const modal = bootstrap.Modal.getInstance(document.getElementById('referenceModal'));
                if (modal) {
                    modal.hide();
                }
            } else {
                alert('데이터 전송 중 오류가 발생했습니다.');
            }
        })
        .catch(error =>{
            alert('데이터 전송 중 오류가 발생했습니다.', error);
        })



        })
    }

});

function teamUpdate() {
    fetch(`${contextPath}/${companyId}/project`, {
        method: "GET",
        headers: {
            "Content-type": "application/json"
        }
    })
    .then(resp => {
        if (resp.ok) {
            // 서버에서 응답으로 HTML이 반환되므로, JSON으로 파싱하지 않음
            return resp.text(); // HTML 텍스트 반환
        } else {
            throw new Error("Failed to fetch team data.");
        }
    })
    .then(htmlString => {
        // 서버에서 가져온 HTML 문자열로 DOM 업데이트
        const parser = new DOMParser();
        const doc = parser.parseFromString(htmlString, "text/html");

        // 새로운 팀 데이터를 기존 DOM에 업데이트
        const newAccordion = doc.querySelector("#accordionStyle1");
        const oldAccordion = document.querySelector("#accordionStyle1");

        if (newAccordion && oldAccordion) {
            oldAccordion.innerHTML = newAccordion.innerHTML;
        }
    })
    .catch(error => {
        console.error("Error fetching team data:", error);
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
            const [orgResp, empResp] = await Promise.all([
                fetch(orgTreeUrl),
                fetch(empUrl)
            ]);

            const [orgData, empData] = await Promise.all([
                orgResp.json(),
                empResp.json()
            ]);

            const employeeByDept = {};
            empData.forEach(emp => {
                if (!employeeByDept[emp.departCode]) {
                    employeeByDept[emp.departCode] = [];
                }
                employeeByDept[emp.departCode].push(emp);
            });

            const treeData = this.buildTreeData(orgData, employeeByDept);
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
    buildTreeData(departments, employeesByDept) {
        const tree = [];
        const map = {};

        // 활성화된 부서만 필터링
        const activeDepts = departments.filter(dept => dept.departStatus === 'Y');

        // 부서 노드 생성
        activeDepts.forEach(dept => {
            map[dept.departCode] = {
                ...dept,
                children: [],
                employees: employeesByDept[dept.departCode] || []
            };
        });

        // 부모-자식 관계 설정
        activeDepts.forEach(dept => {
            if (dept.departParentcode && map[dept.departParentcode]) {
                map[dept.departParentcode].children.push(map[dept.departCode]);
            } else if (!dept.departParentcode) {
                tree.push(map[dept.departCode]);
            }
        });

        return tree;
    },

    //orgTree랑똑같음
    renderTree(node) {
        if (!Array.isArray(node) || node.length === 0) return '';

        let html = '<ul>';
        node.forEach(item => {
            if (!item) return;

            html += `<li class="folder">`;
            html += `<span data-code="${item.departCode}" data-head="${item.departHead || ''}">${item.departName}</span>`;

            if (item.employees && item.employees.length > 0) {
                html += '<ul class="employees">';

                const head = item.employees.find(emp => emp.empId === item.departHead);
                if (head) {
                    html += `<li class="department-head">
                        <span data-emp-id="${head.empId}">
                            ${head.posiName} ${head.empName} (부서장)
                        </span>
                    </li>`;
                }

                item.employees
                    .filter(emp => emp.empId !== item.departHead)
                    .forEach(emp => {
                        html += `<li class="employee">
                            <span data-emp-id="${emp.empId}">
                                ${emp.posiName} ${emp.empName}
                            </span>
                        </li>`;
                    });
                html += '</ul>';
            }

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
                            <input type="text" readonly class="form-control-plaintext" name="members[${index}].departName" value="${ref.departmentName}" />
                        </td>
                        <td>
                            <input type="text" readonly class="form-control-plaintext" name="members[${index}].empName" value="${name}" />
                        </td>
                        <td>
                            <input type="text" readonly class="form-control-plaintext" name="members[${index}].codeComment" value="${position}" />
                        </td>
                        <td style="display : none;">
                            <input type="text" name="members[${index}].empId" value="${ref.empId}" readonly> 
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
                title: "중복 등록",
                html: "이미 추가된 임직원입니다",
                icon: "error"
            });
            return false;
        }
    
        // 결재자 중복 체크
        if (ApprovalLineManager.isApprover(employeeInfo.empId)) {
            alert('이미 결재자로 지정된 사용자입니다.');
            return false;
        }
    
        console.log('검증 통과');
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



