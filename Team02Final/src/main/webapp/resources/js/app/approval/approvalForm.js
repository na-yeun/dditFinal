document.addEventListener('DOMContentLoaded', async function () {


    // 1. 기본 요소 초기화
    initializeFormElements();
    // 2. 공통 모듈 초기화(이게 제일 먼저와야함)
    await ApprovalCommon.initialize();
    // 3. 조직도 트리 불러오기
    await OrgTreeManager.loadOrgTreeWithEmployees();
    // 4. 참조자 조직도 트리 불러오기
    await ReferenceOrgTree.loadOrgTreeWithEmployees();
    // 5. 결재선 관리자 초기화
    ApprovalLineManager.initialize();
    // 6. 첨부파일 초기화
    ApprovalAttachment.initialize();
    // 7. 이벤트 리스너 설정하기
    setDocumentTypeListeners()


});

function initializeFormElements() {
    //formSelect, container, button등의 전역 변수 초기화
    window.approvalElements = {
        formSelect: document.getElementById('formSelect'),
        vacationFormContainer: document.getElementById('vacationFormContainer'),
        expenseFormContainer: document.getElementById('expenseFormContainer'),
        freeFormContainer: document.getElementById('freeFormContainer'),
        previewContainer: document.getElementById('previewContainer'),
        buttonGroup: document.querySelector('.button-group')
    };
}

//문서 타입별 이벤트 리스너
function setDocumentTypeListeners() {
    approvalElements.formSelect.addEventListener('change', async function () {
        const selectedValue = this.value;
        const previousValue = ApprovalCommon.selectedFormId;
        // console.log('선택된 양식:', selectedValue);

        if (selectedValue) {
            // 양식 정보 저장
            ApprovalCommon.setSelectedForm(selectedValue);

            // 모든 폼 컨테이너 숨기기
            Object.keys(approvalElements).forEach(key => {
                if (key.toLowerCase().includes('container') && approvalElements[key]) {
                    approvalElements[key].style.display = 'none';
                }
            });

            // 결재선/참조자 버튼 숨기기
            const approvalButtonArea = document.querySelector('.approval-button-area');
            if (approvalButtonArea) {
                approvalButtonArea.style.display = 'none';
            }
            // 참조자 목록 영역 숨기기
            const referenceDisplayArea = document.querySelector('.reference-display-area');
            if (referenceDisplayArea) {
                referenceDisplayArea.style.display = 'none';
            }

            // 새로운 양식 초기화만 수행
            switch (selectedValue) {
                case 'v01':
                    ApprovalVacation.initialize();
                    break;
                case 'e01':
                    ApprovalExpense.initialize();
                    break;
                case 'f01':
                    ApprovalFree.initialize();
                    break;
            }
        }
    });
}
