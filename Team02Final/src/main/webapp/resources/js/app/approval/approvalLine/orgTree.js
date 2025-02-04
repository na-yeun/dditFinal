const OrgTreeManager = {

    // 원본 데이터를 저장할 변수 추가
    originalTreeData: null,

    // 조직도 데이터와 사원 데이터를 함께 로드
    async loadOrgTreeWithEmployees() {
        try {
            console.log('조직도 데이터 로딩 시작');
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

            this.originalTreeData = this.buildTreeDataWithEmployees(orgData, employeeByDept);
            const treeHtml = this.renderTreeWithEmployees(this.originalTreeData);

            const orgTreeElement = document.getElementById('orgTree');
            if (orgTreeElement) {
                orgTreeElement.innerHTML = treeHtml;
                // 트리 렌더링 후에 검색 기능 초기화
                this.initializeSearch();
                this.setTreeEventListeners(orgTreeElement);
            }
        } catch (error) {
            console.error('데이터 로딩 중 에러 발생:', error);
        }
    },

    // 검색 기능 초기화
    initializeSearch() {
        const searchInput = document.getElementById('employeeSearch');
        const searchBtn = document.getElementById('searchBtn');
        const clearButton = document.getElementById('clearSearch');

        if (!searchInput || !searchBtn || !clearButton) {
            console.log('검색 관련 요소를 찾을 수 없습니다.');
            return;
        }

        // 검색 버튼 클릭 시
        searchBtn.addEventListener('click', () => {
            const searchTerm = searchInput.value.trim();
            this.handleSearch(searchTerm);
        });

        // Enter 키 입력 시
        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                const searchTerm = searchInput.value.trim();
                this.handleSearch(searchTerm);
            }
        });

        // 초기화 버튼
        clearButton.addEventListener('click', () => {
            this.resetSearch();
        });
    },

    // 검색 처리
    handleSearch(searchTerm) {
        if (!searchTerm) {
            this.resetSearch();
            return;
        }

        const orgTreeElement = document.getElementById('orgTree');
        if (!orgTreeElement) return;

        try {
            const searchTermLower = searchTerm.toLowerCase();
            const employeeItems = orgTreeElement.querySelectorAll('li.employee, li.department-head');
            let matchCount = 0; // 검색 결과 수 카운트
            let matchedFolders = new Set(); // 매칭된 직원이 있는 폴더들 저장

            // 먼저 모든 요소 숨기기
            orgTreeElement.querySelectorAll('li').forEach(item => {
                item.style.display = 'none';
            });

            // 검색어와 일치하는 직원 찾기 및 처리
            employeeItems.forEach(item => {
                const nameElement = item.querySelector('span');
                if (!nameElement) return;

                const name = nameElement.textContent.toLowerCase();

                if (name.includes(searchTermLower)) {
                    matchCount++;
                    item.style.display = '';

                    // 상위 폴더들 찾아서 모두 표시 및 열기
                    let currentElement = item;
                    while (currentElement) {
                        if (currentElement.classList.contains('folder')) {
                            currentElement.style.display = '';
                            currentElement.classList.add('open');
                            matchedFolders.add(currentElement);
                        }
                        currentElement = currentElement.parentElement;
                        if (currentElement === orgTreeElement) break;
                    }
                }
            });

            // 매칭된 폴더의 모든 상위 폴더들도 표시 및 열기
            matchedFolders.forEach(folder => {
                let parent = folder.parentElement;
                while (parent && parent !== orgTreeElement) {
                    const parentFolder = parent.closest('li.folder');
                    if (parentFolder) {
                        parentFolder.style.display = '';
                        parentFolder.classList.add('open');
                    }
                    parent = parent.parentElement;
                }
            });

            // 루트 폴더 자동으로 열기
            const rootFolder = orgTreeElement.querySelector('li.folder');
            if (rootFolder) {
                rootFolder.style.display = '';
                rootFolder.classList.add('open');
            }

            // 검색 결과 표시
            const searchResult = document.getElementById('searchResult');
            if (searchResult) {
                searchResult.style.display = 'block';
                if (matchCount > 0) {
                    searchResult.innerHTML = `
                    <small class="text-muted">
                        "<span class="text-primary">${searchTerm}</span>" 검색 결과 
                        <span class="badge bg-secondary">${matchCount}건</span>
                    </small>
                `;
                } else {
                    searchResult.innerHTML = `
                    <small class="text-muted">
                        "<span class="text-primary">${searchTerm}</span>"에 대한 검색 결과가 없습니다.
                    </small>
                `;
                }
            }

        } catch (error) {
            console.error('검색 중 오류 발생:', error);
            Swal.fire({
                icon: 'error',
                title: '오류 발생',
                text: '검색 처리 중 오류가 발생했습니다.'
            });
        }
    },

    // 검색 초기화
    resetSearch() {
        const orgTreeElement = document.getElementById('orgTree');
        const searchResult = document.getElementById('searchResult');
        const searchInput = document.getElementById('employeeSearch');

        if (orgTreeElement) {
            // 숨겨진 요소들 모두 다시 표시
            const allElements = orgTreeElement.querySelectorAll('li');
            allElements.forEach(el => {
                el.style.display = '';
            });

            // 모든 폴더 닫기
            const folders = orgTreeElement.querySelectorAll('li.folder');
            folders.forEach(folder => {
                folder.classList.remove('open');
            });

            // 하이라이트 효과 제거
            const highlightedElements = orgTreeElement.querySelectorAll('.search-highlight');
            highlightedElements.forEach(el => {
                const parent = el.parentNode;
                parent.textContent = parent.textContent;
            });
        }

        // 검색 입력창 초기화
        if (searchInput) {
            searchInput.value = '';
        }

        // 검색 결과 메시지 숨기기
        if (searchResult) {
            searchResult.style.display = 'none';
        }
    },



    //이벤트 리스너
    setTreeEventListeners(treeElement) {
        treeElement.addEventListener('click', (e) => {
            const span = e.target.closest('span');
            if (!span) return;

            //closest 메서드는 DOM 트리에서 특정 요소로부터 가장 가까운 조상을 찾아주는 메서드.
            const li = span.closest('li');
            if (li && li.classList.contains('folder')) {
                li.classList.toggle('open');
                console.log('폴더 토글', li.classList.contains('open'));
            }
            //사원 선택 이벤트
            if (span.hasAttribute('data-emp-id')) {
                const employeeInfo = this.extractEmployeeInfo(span);
                if (this.validateEmployeeSelection(employeeInfo)) {
                    ApprovalLineView.addApprover(employeeInfo);
                }
            }
        });
    },

    // 선택한 사원 정보 추출하기
    extractEmployeeInfo(span) {
        const employeeLi = span.closest('li');
        const departmentLi = employeeLi.closest('li.folder');
        return {
            empId: span.getAttribute('data-emp-id'),
            departmentName: departmentLi ? departmentLi.querySelector('span').textContent.trim() : '부서없음',
            name: span.textContent.replace(/\([^)]*\)/g, '').trim(),  // 괄호 내용 제거
            position: span.textContent.split(' ')[0]  // 직위는 이름 앞에 있음
        };
    }
    ,

    // 사원 선택 유효성 검사하기
    validateEmployeeSelection(employeeInfo) {
        //본인이 선택한거 방지하기
        if (employeeInfo.empId === myEmpId) {
            Swal.fire({
                icon: 'warning',
                title: '선택 제한',
                text: '본인은 결재자로 지정할 수 없습니다'
            });
            return false;
        }

        //중복 선택 방지하기
        const existingApprovers = document.querySelectorAll('#approverList tr');
        for (let approver of existingApprovers) {
            if (approver.getAttribute('data-emp-id') === employeeInfo.empId) {
                Swal.fire({
                    icon: 'warning',
                    title: '중복 선택',
                    text: '이미 추가된 결재자입니다'
                });
                return false;
            }
        }

        return true;
    }
    ,

    // 조직도 디렉토리 빌드
    buildTreeDataWithEmployees(departments, employeesByDept) {
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
    }
    ,

    // 조직도 트리 렌더링
    renderTreeWithEmployees(node) {
        if (!Array.isArray(node) || node.length === 0) return '';

        let html = '<ul>';
        node.forEach(item => {
            if (!item) return;

            html += `<li class="folder">`;
            html += `<span data-code="${item.departCode}" data-head="${item.departHead || ''}">${item.departName}</span>`;

            if (item.employees && item.employees.length > 0) {
                //기본적으로 숨겨놓기
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
                html += this.renderTreeWithEmployees(item.children);
            }
            html += `</li>`;
        });
        html += '</ul>';
        return html;
    }
};