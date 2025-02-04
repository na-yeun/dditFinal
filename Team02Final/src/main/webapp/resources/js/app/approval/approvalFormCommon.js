const ApprovalCommon = {

    // 선택된 양식 ID를 저장할 변수
    selectedFormId: null,
    selectedFormTitle: null,

    //초기화 함수 정하기
    initialize(){
        //요소들 초기화
        this.initializeElements();
        this.setCommonEventListeners();
        // 참조자 버튼은 초기에는 숨기기
        this.hideReferenceButton();
    },

    initializeElements() {
        this.elements = {
            // 요소들 나열하기
            modifyBtn: document.getElementById('modifyBtn'),
            nextBtn: document.getElementById('nextBtn'),
            cancelBtn: document.getElementById('cancelBtn'),
            buttonGroup: document.querySelector('.button-group'),
            allowFinalApprovalCheckbox: document.getElementById('allowFinalApprovalCheckbox'),
            allowFinalApprovalHidden: document.getElementById('allowFinalApprovalHidden')
        };
    },

    // 양식 선택값 (ID 제목) 을 저장하는 메소드임
    setSelectedForm(formId) {
        this.selectedFormId = formId;

        // select 엘리먼트에서 선택된 option의 텍스트 저장하기
        const selectElement = document.getElementById('formSelect');
        this.selectedFormTitle = selectElement.options[selectElement.selectedIndex].text;

        console.log('선택된 양식:', this.selectedFormId, this.selectedFormTitle);
    },

    //공통 이벤트 리스너
    setCommonEventListeners() {

        //취소했을때
        this.elements.cancelBtn?.addEventListener('click', async () => {
            const { isConfirmed } = await Swal.fire({
                icon: 'warning',
                title: '취소',
                text: '그동안에 입력했던 데이터가 사라집니다. 취소하시겠습니까?',
                showCancelButton: true,
                confirmButtonText: '확인',
                cancelButtonText: '취소',
                reverseButtons: false // 취소 버튼을 오른쪽으로
            });
            if (!isConfirmed) return;

            window.location.href = documentListUrl;
        });

        //수정 버튼을 클릭했을때 연출바꾸기
        this.elements.modifyBtn?.addEventListener('click', async () => {
            // 확인창 띄우기
            const { isConfirmed } = await Swal.fire({
                icon: 'warning',
                title: '수정 확인',
                text: '참조 등의 일부 정보가 초기화됩니다. 수정하시겠습니까?',
                showCancelButton: true,
                confirmButtonText: '수정',
                cancelButtonText: '취소',
                reverseButtons: false // 취소 버튼을 오른쪽으로
            });

            if (!isConfirmed) return;

            // 셀렉트박스 다시 활성화
            approvalElements.formSelect.disabled = false;

            // 결재선/참조자 버튼 영역 숨기기
            const approvalButtonArea = document.querySelector('.approval-button-area');
            if (approvalButtonArea) {
                approvalButtonArea.style.display = 'none';
            }

            // 미리보기 숨기기
            approvalElements.previewContainer.style.display = 'none';
            this.elements.buttonGroup.classList.remove('show');  // display: none !important가 적용되도록 수정

            // 결재선 데이터 초기화하기
            if (ApprovalLineManager) {
                ApprovalLineManager.selectedLineId = null;
                ApprovalLineManager.currentApprovers = [];
            }

            //참조자 관련 초기화 하기
            //참조자 목록 영역 숨기기
            const referenceDisplayArea = document.querySelector('.reference-display-area');
            if (referenceDisplayArea) {
                referenceDisplayArea.style.display = 'none';
            }

            // 참조자 버튼 숨기기
            const referenceBtn = document.getElementById('referenceBtn');
            if (referenceBtn) {
                referenceBtn.style.display = 'none';
            }

            // 참조자 데이터 완전 초기화 추가
            if (ReferenceOrgTree) {
                ReferenceOrgTree.tempReferences.clear();    // 임시 저장소 초기화
                ReferenceOrgTree.confirmedReferences.clear(); // 확정 저장소 초기화
                ReferenceOrgTree.renderReferenceList();      // 참조자 목록 UI 갱신
                ReferenceOrgTree.displayReferencesInDocument(); // 문서 상의 참조자 목록 갱신
            }

            //참조자 버튼도 숨기기
            this.hideReferenceButton();

            // 첨부파일 관련 초기화하기위해 불러세우기 호출시키는것
            const attachmentDisplayArea = document.querySelector('.attachment-display-area');
            const attachmentLabel = document.getElementById('attachmentLabel');

            // 첨부파일 목록 영역 숨기기
            if (attachmentDisplayArea) {
                attachmentDisplayArea.style.display = 'none';
            }

            // 첨부파일 버튼 숨기기
            if (attachmentLabel) {
                attachmentLabel.style.display = 'none';
            }

            // 첨부파일 데이터 초기화
            if (ApprovalAttachment) {
                ApprovalAttachment.clearAttachments();
            }

            // 문서 타입별 폼 보여주기
            switch (this.selectedFormId) {
                case 'v01':
                    approvalElements.vacationFormContainer.style.display = 'block';
                    approvalElements.expenseFormContainer.style.display = 'none';
                    approvalElements.freeFormContainer.style.display = 'none';
                    ApprovalVacation.resetForm();
                    break;
                case 'e01':
                    approvalElements.expenseFormContainer.style.display = 'block';
                    approvalElements.vacationFormContainer.style.display = 'none';
                    approvalElements.freeFormContainer.style.display = 'none';
                    ApprovalExpense.resetForm();
                    break;
                case 'f01':
                    approvalElements.freeFormContainer.style.display = 'block';
                    approvalElements.vacationFormContainer.style.display = 'none';
                    approvalElements.expenseFormContainer.style.display = 'none';
                    ApprovalFree.resetForm();
                    break;
            }


        });

        this.elements.nextBtn?.addEventListener('click', this.handleDraft.bind(this));

        // 전결 허용 여부 체크박스 이벤트
        this.elements.allowFinalApprovalCheckbox.addEventListener('change', () => {
            this.elements.allowFinalApprovalHidden.value =
                this.elements.allowFinalApprovalCheckbox.checked ? 'Y' : 'N';
        });
    },



    //참조자 지정 버튼 처음에는 숨기기 설정
    hideReferenceButton() {
        const referenceBtn = document.getElementById('referenceBtn');
        if (referenceBtn) {
            referenceBtn.style.display = 'none';
        }
    },

    //결재자 지정이 완료되면 참조자 버튼을 표시하게할거임
    //첨부파일 버튼도 이때 나타나게할거임
    showReferenceButton() {
        const referenceBtn = document.getElementById('referenceBtn');
        const attachmentLabel = document.getElementById('attachmentLabel');
        if (referenceBtn) {
            referenceBtn.style.display = 'block';
        }
        if (attachmentLabel) {
            attachmentLabel.style.display = 'block';
        }
    },

    //기안 버튼을 눌렀을때
    async handleDraft(){

        // 결재선 지정 여부 검증 강화
        if (!ApprovalLineManager.isLineFullySelected()) {
            Swal.fire({
                icon: 'warning',
                title: '결재선 미지정',
                text: '결재선을 지정해주세요.'
            });
            return;
        }

        // 결재자 검증 추가
        if (!ApprovalLineManager.hasSelectedApprovers()) {
            Swal.fire({
                icon: 'warning',
                title: '결재자 미지정',
                text: '최소 한 명 이상의 결재자를 지정해야 합니다.'
            });
            return;
        }

        const { isConfirmed } = await Swal.fire({
            icon: 'question',
            title: '기안 확인',
            text: '기안하시겠습니까?',
            showCancelButton: true,
            confirmButtonText: '기안',
            cancelButtonText: '취소',
            reverseButtons: false // 취소 버튼을 오른쪽으로
        });
        if (!isConfirmed) return;

        try{

            //선택된 참조자 정보 갖고와서 우려 표시를 한번해주기
            const selectedReferences = ReferenceOrgTree.getReferencesForDraft();
            console.log("선택된 참조자:", selectedReferences); // 디버깅용 로그

            if (selectedReferences && selectedReferences.length === 0) {
                const { isConfirmed: proceedConfirmed } = await Swal.fire({
                    icon: 'warning',
                    title: '참조자 미지정',
                    text: '선택된 참조자가 없습니다. 이대로 진행하시겠습니까?',
                    showCancelButton: true,
                    confirmButtonText: '진행',
                    cancelButtonText: '취소',
                    reverseButtons: false // 취소 버튼을 오른쪽으로
                });
                if (!proceedConfirmed) return;
            }

            const formId = this.selectedFormId;
            console.log(formId);
            if (!formId) {
                Swal.fire({
                    icon: 'warning',
                    title: '양식 미선택',
                    text: '결재 양식을 선택해주세요.'
                });
                return;
            }

            const formData = this.collectDraftData(formId);
            if (!formData) {
                Swal.fire({
                    icon: 'warning',
                    title: '데이터 누락',
                    text: '필요한 데이터가 누락되었습니다.'
                });
                return;
            }

            // FormData 내용 확인용 유틸리티
            for (let [key, value] of formData.entries()) {
                console.log(`${key}: ${value}`);
            }

            const resp = await fetch(draftUrl, {
                method: 'POST',
                body: formData
            });

            if (!resp.ok) {
                throw new Error('문서 저장하다가 서버 오류가 발생했습니다');
            }

            const documentId = (await resp.text()).replace(/"/g, '');  // 응답에서 documentId 받기 따옴표 제거
            await Swal.fire({
                icon: 'success',
                title: '기안 완료',
                text: '기안이 완료되었습니다. 문서 리스트로 이동합니다.'
            });
            window.location.replace(`${detailUrl}/${documentId}`);

        } catch (error){
            console.error('기안 중 오류 발생 : ', error);
            Swal.fire({
                icon: 'error',
                title: '오류 발생',
                text: '기안 중 오류가 발생했습니다.'
            });
        }
    },

    //기안할때 대리고가는 요소들 모아두기
    collectDraftData(){

        //폼데이터 담을거하나만들기
        const formData = new FormData();

        // JSON 데이터를 문자열로 변환하여 FormData에 추가하기
        const jsonData = {
            // 현재 문서 HTML
            documentHtml: approvalElements.previewContainer.innerHTML,
            //전결 허용 여부
            allowDelegation: this.elements.allowFinalApprovalHidden.value,
            // 선택된 결재선 ID
            apprlineId: ApprovalLineManager.selectedLineId,
            //결재자 정보 수집
            approvers: ApprovalLineManager.currentApprovers,
            //참조자 정보 수집
            references: ReferenceOrgTree.getReferencesForDraft(),
            //결재 문서 유형 코드(v01, e01...)
            formId: this.selectedFormId,
            //밑에거 오버로딩 호출
            formData: this.collectFormData(this.selectedFormId)
        };

        //이렇게보내니까 오류가발생함
        // formData.append("jsonParam", JSON.stringify(jsonData));

        // JSON을 Blob으로 변환 여기서 컨텐츠 타입을 명시적으로 설정함
        const jsonBlob = new Blob([JSON.stringify(jsonData)], {
            type: 'application/json'
        });
        formData.append("jsonParam", jsonBlob);

        // 이제 첨부파일 차례
        const files = ApprovalAttachment.getAttachmentsForDraft();
        files.forEach(file => {
            formData.append('attachments', file);
        })

        return formData;
    },

    collectFormData(formId){
        switch (formId) {
            case 'v01':
                const vacationSelect = document.getElementById('vacationType');
                console.log("vacationSelect 고른거 : ", vacationSelect);
                //내가 셀렉트박스에서 선택한 휴가 종류 코드와 이름담겨있는거
                const selectedOption = vacationSelect.options[vacationSelect.selectedIndex];
                console.log("selectedOption 선택한거 : ", selectedOption);

                return{
                    formType: 'vacation',
                    formTitle: this.selectedFormTitle,
                    data: {
                        vacationType: selectedOption.text,
                        vacCode: selectedOption.value,  // VACCODE 값
                        startDate: document.getElementById('startDate').value,
                        endDate: document.getElementById('endDate').value,
                        docContent: ApprovalEditor.getContent()
                    }
                };
            case 'e01':
                // 제목과 지출 구분 데이터 수집
                const expenseName = document.getElementById('expenseName').value;
                const expenseTypeSelect = document.getElementById('expenseType');
                const selectedExpenseType = expenseTypeSelect.options[expenseTypeSelect.selectedIndex];

                //지출 내역 아이템들 수집하기
                const expenseItems = Array.from(document.getElementById('expenseItemList').getElementsByTagName('tr'))
                    .map(row => {
                        const categorySelect = row.querySelector('.expense-category');
                        const categoryOption = categorySelect ? categorySelect.options[categorySelect.selectedIndex] : null;

                        return {
                            id: row.dataset.itemId,
                            date: row.querySelector('.expense-date')?.value || '',
                            categoryCode: categorySelect?.value || '',
                            categoryName: categoryOption ? categoryOption.textContent.trim() : '',
                            paymentMethod: row.querySelector('.payment-method')?.value || '',
                            quantity: row.querySelector('.expense-quantity')?.value || '',
                            price: row.querySelector('.expense-price')?.value || '',
                            amount: row.querySelector('.item-amount')?.textContent || '',
                            detail: row.querySelector('.expense-detail')?.value || ''
                        };
                    });
                return {
                    formType: 'expense',
                    formTitle: `${expenseName}`,  // 사용자가 입력한 제목 사용
                    data: {
                        expenseName: expenseName,  // 제목 추가
                        expenseType: selectedExpenseType.text,
                        expTypeCode: selectedExpenseType.value,
                        expenseItems: expenseItems,
                        totalAmount: document.getElementById('totalExpenseAmount').textContent,
                        docContent: ApprovalEditor.getContent()
                    }
                };
                case 'f01':

                    return {
                        //백단에서 서비스에서 꺼낼때 네이밍은 free
                        formType: 'free',
                        formTitle: document.getElementById('docTitle').value, // 사용자가 입력한 제목 사용하도록
                        data: ApprovalFree.collectFormData() //위랑 다르게 이번엔 다담아서 옴
                    };
            default:
                throw new Error('지원하지않는 양식 문서임');
        }
    },

};
