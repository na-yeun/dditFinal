document.addEventListener("DOMContentLoaded", function() {
    let currentPage = 1;
    let currentTab = 'draft';

    const contextPath = document.querySelector('#contextPath')?.value || '';
    const companyId = document.querySelector('#companyId')?.value || '';

    // 초기 데이터 로드
    loadTabContent('draft', 1);

    // 탭 클릭 이벤트
    document.querySelectorAll('.nav-link').forEach(tab => {
        tab.addEventListener('click', function(e) {
            e.preventDefault();
            const tabId = this.getAttribute('href').substring(1);
            currentTab = tabId;
            loadTabContent(tabId, 1);
        });
    });

    // 검색 버튼 이벤트
    document.getElementById('search-btn').addEventListener('click', function() {
        loadTabContent(currentTab, 1);
    });

    // 탭 컨텐츠 로드 함수
    function loadTabContent(tabId, page) {
        const tabContent = document.querySelector(`#${tabId} .document-list`);
        tabContent.innerHTML = '<div class="text-center">로딩중...</div>';

        const searchType = document.getElementById('searchType').value;
        const searchWord = document.getElementById('searchWord').value;

        // approvalList.js
        fetch(`${contextPath}/${companyId}/approval/list/${tabId}?page=${page}&searchType=${searchType}&searchWord=${searchWord}`)
            .then(response => {
                if (!response.ok) throw new Error('서버 오류 인 것 같습니다');
                return response.json();
            })
            .then(data => {
                // 테이블 렌더링 하기
                const table = renderDocumentTable(data.list);
                tabContent.innerHTML = table;

                // 페이징 렌더링 하기
                const pagingArea = document.querySelector(`#${tabId} .paging-area`);
                pagingArea.innerHTML = renderPagination(data.paging);

                // 문서 클릭 이벤트 바인딩 하는것
                bindDocumentClickEvents(tabContent);
            })
            .catch(error => {
                console.error('Error:', error);
                tabContent.innerHTML = '<div class="alert alert-danger">데이터 로드 중 오류가 발생했습니다.</div>';
            });
    }

    function renderDocumentTable(documents) {
        // 데이터가 없을때는 기본값
        if (!documents || documents.length === 0) {
            return `
            <table class="table">
                <thead>
                    <tr>
                        <th>순번</th>
                        <th>문서번호</th>
                        <th>제목</th>
                        <th>기안자</th>
                        <th>기안일자</th>
                        <th>결재상태</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td colspan="6">문서가 없습니다.</td>
                    </tr>
                </tbody>
            </table>`;
        }
        // 데이터가 있을때는 이렇게
        return `
        <table class="table">
            <thead>
                <tr>
                    <th>순번</th>
                    <th>문서번호</th>
                    <th>제목</th>
                    <th>기안자</th>
                    <th>기안일자</th>
                    <th>결재상태</th>
                </tr>
            </thead>
            <!--            여러개의 데이터가 오니까-->
            <tbody>
                ${documents.map(doc => `
                    <tr class="document-list" data-doc-id="${doc.docId}">
                        <td>${doc.rnum}</td>
                        <td>${doc.docId}</td>
                        <td class="text-left">${doc.docTitle}</td>
                        <td>${doc.empName}</td>
                        <td>${doc.createdDate}</td>
                        <td>
                            <span class="badge rounded-pill ${getStatusClass(doc.docStatus)}">
                                ${doc.docStatusName}
                            </span>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>`;
    }

    // 페이징 렌더링 함수
    function renderPagination(paging) {
        // 데이터가 없거나 totalPages가 0이면 빈 문자열 반환시켜주기
        if (!paging || paging.totalPages === 0) return '';

        let html = '<ul class="pagination justify-content-center">';

        // 이전 페이지 그룹 버튼
        if (paging.startPage > 1) {
            html += `
            <li class="page-item">
                <a class="page-link" href="#" data-page="${paging.startPage - 1}" aria-label="Previous">
                    <span aria-hidden="true">&laquo;</span>
                </a>
            </li>`;
        }

        // 페이지 번호 버튼
        // totalRecord를 screenSize로 나눈 값을 올림하여 실제 필요한 페이지 수 계산
        const realEndPage = Math.min(
            paging.endPage,
            Math.ceil(paging.totalRecord / 10)  // screenSize는 PaginationInfo에서 기본값 10
        );

        for (let i = paging.startPage; i <= realEndPage; i++) {
            html += `
        <li class="page-item ${i === paging.currentPage ? 'active' : ''}">
            <a class="page-link" href="#" data-page="${i}">${i}</a>
        </li>`;
        }

        // 다음 페이지 그룹 버튼
        if (paging.endPage < paging.totalPages) {
            html += `
            <li class="page-item">
                <a class="page-link" href="#" data-page="${paging.endPage + 1}" aria-label="Next">
                    <span aria-hidden="true">&raquo;</span>
                </a>
            </li>`;
        }

        html += '</ul>';
        return html;
    }

    // 문서 상태에 따른 뱃지 클래스 반환 함수
    function getStatusClass(status) {
        switch(status) {
            case '1': return 'bg-secondary';  // 미열람
            case '2': return 'bg-primary';    // 결재 대기
            case '3': return 'bg-info';       // 진행중
            case '4': return 'bg-danger';     // 반려
            case '5': return 'bg-success';    // 승인
            default: return 'bg-secondary';
        }
    }

    // 문서 클릭 이벤트 바인딩 함수
    function bindDocumentClickEvents(tabContent) {
        // 문서 행 클릭 이벤트
        tabContent.querySelectorAll('tr[data-doc-id]').forEach(row => {
            row.addEventListener('click', function(e) {
                const docId = this.dataset.docId;
                // 새 창에서 문서 상세 보기
                location.href = `${contextPath}/${companyId}/approval/detail/${docId}`;
            });
        });

        // 페이징 클릭 이벤트
        const pagingArea = tabContent.closest('.tab-pane').querySelector('.paging-area');
        if (pagingArea) {
            pagingArea.querySelectorAll('a.page-link').forEach(link => {
                link.addEventListener('click', function(e) {
                    e.preventDefault();
                    const page = this.dataset.page;
                    const tabId = tabContent.closest('.tab-pane').id;
                    loadTabContent(tabId, page);
                });
            });
        }
    }
});