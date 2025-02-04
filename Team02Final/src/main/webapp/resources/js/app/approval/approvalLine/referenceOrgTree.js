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
        const container = document.getElementById('referenceList');
        if (!container) return;

        container.innerHTML = Array.from(this.tempReferences)
            .map(ref => `
                <div class="reference-item">
                    ${ref.departmentName} ${ref.position} ${ref.name}
                    <button class="btn-delete" data-emp-id="${ref.empId}">×</button>
                </div>
            `).join('');

        // 삭제 버튼에 이벤트 리스너 추가
        this.setDeleteButtonListeners();
    },

    //삭제 버튼 리스너
    setDeleteButtonListeners() {
        const deleteButtons = document.querySelectorAll('.reference-item .btn-delete');
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

        //본인 선택 방지
        if (employeeInfo.empId === myEmpId) {
            Swal.fire({
                icon: 'warning',
                title: '선택 제한',
                text: '본인은 참조자로 지정할 수 없습니다'
            });
            return false;
        }

        //중복 선택 방지
        const existingReferences = document.querySelectorAll('.reference-item');
        console.log('기존 참조자 수:', existingReferences.length);

        for (let ref of existingReferences) {
            if (ref.querySelector('.btn-delete').getAttribute('data-emp-id') === employeeInfo.empId) {
                Swal.fire({
                    icon: 'warning',
                    title: '중복 선택',
                    text: '이미 추가된 참조자입니다'
                });
                return false;
            }
        }

        console.log('결재자 중복 체크 시작');
        // 결재자 중복 체크
        if (ApprovalLineManager.isApprover(employeeInfo.empId)) {
            Swal.fire({
                icon: 'warning',
                title: '중복 선택',
                text: '이미 결재자로 지정된 사용자입니다.'
            });
            return false;
        }

        console.log('검증 통과');
        return true;
    },

    //참조자 목록을 문서에 표시하기
    displayReferencesInDocument() {
        const displayArea = document.querySelector('.reference-display-area');
        const displayList = document.getElementById('referenceDisplayList');

        if (!displayList) return;

        const selectedReferences = Array.from(this.confirmedReferences);

        //노출시키고 렌더링하기
        if (selectedReferences.length > 0) {
            displayArea.style.display = 'block';
            displayList.innerHTML = selectedReferences
                .map(ref => `
                    <div class="reference-display-item">
                        ${ref.departmentName} ${ref.position} ${ref.name}
                    </div>
                `).join('');
        }else {
            displayArea.style.display = 'none';
            displayList.innerHTML = '';
        }
    },

    //참조자 추가 시에는 임시 저장소에 추가해놓기
    addReference(employeeInfo) {
        this.tempReferences.add(employeeInfo);
        this.renderReferenceList();
    },

    // 확인 버튼 클릭 시 임시 저장소의 데이터를 확정 저장소로 이동시키기
    confirmReferences() {
        this.confirmedReferences = new Set(this.tempReferences);
        this.displayReferencesInDocument();
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

// 모달 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', () => {
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

            // 모달 닫기
            const modal = bootstrap.Modal.getInstance(document.getElementById('referenceModal'));
            if (modal) {
                modal.hide();
            }

            // 참조자 목록 표시
            ReferenceOrgTree.displayReferencesInDocument();
        })
    }
});