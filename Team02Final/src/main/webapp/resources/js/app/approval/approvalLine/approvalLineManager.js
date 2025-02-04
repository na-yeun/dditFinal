const ApprovalLineManager = {

    //처음엔 널로
    selectedLineId: null,
    //현재 결재자 정보를 저장할 배열 추가하기 참조자선택할때 결재자랑 중복되면 막기위해서 생성함
    //얘는 static으로 유지되어야함
    currentApprovers: [],

    //초기화
    initialize(){
        this.loadSavedLines();
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
                    Swal.fire({
                        icon: 'error',
                        title: '오류 발생',
                        text: '결재선 적용 중 오류가 발생했습니다.'
                    });
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

    // 저장된 결재선 로드
    async loadSavedLines() {
        try{
            const resp = await fetch(getSavedApprlineUrl)
            if (!resp.ok) {
                console.error('서버응답:', await resp.text());
                throw new Error('결재선 로드 실패')};

            const savedLines = await resp.json();
            //List<Map<String, Object>> savedLines 이게 온다
            ApprovalLineView.renderSavedLines(savedLines);
        }catch (error){
            console.error('결재선 로드 중 오류:', error);
        }
    },

    //결재선 저장
    async saveLine(approvers){

        const titleInput = document.getElementById('approvalLineTitle');
        const title = titleInput.value.trim();

        // 제목 검증
        if (!title) {
            Swal.fire({
                icon: 'warning',
                title: '입력 오류',
                text: '결재선 제목이 누락되었습니다.'
            });
            return;
        }

        if (title.length > 30) {
            Swal.fire({
                icon: 'warning',
                title: '입력 오류',
                text: '결재선 제목은 30글자를 초과할 수 없습니다.'
            });
            return;
        }

        if (approvers.length === 0) {
            Swal.fire({
                icon: 'warning',
                title: '결재선 오류',
                text: '최소 한명 이상의 결재자를 지정해주세요'
            });
            return;
        }


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
            // console.log('저장된 결재선 정보:', savedLine);
            // console.log('결재자 목록:', savedLine.approvers);

            //여기서 현재 결재자 정보를 담는다
            this.setCurrentApprovers(savedLine.approvers);
            // console.log('담긴 후 현재 결재자들:', this.currentApprovers);
            ApprovalLineView.handleSaveSuccess(savedLine);
            // console.log('저장 후 현재 결재자들:', this.currentApprovers);

            // 저장 성공 후 제목 초기화
            titleInput.value = '';
        } catch (error){
            console.error('결재선 저장 중 오류:', error);
            Swal.fire({
                icon: 'error',
                title: '저장 발생',
                text: error.response?.data?.message || '결재선 저장 중 오류가 발생했습니다.'
            });
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

    // 결재선 삭제
    async deletedLine(lineId) {
        const { isConfirmed } = await Swal.fire({
            icon: 'info',
            title: '결재선 삭제',
            text: '결재선을 삭제하시겠습니까?',
            showCancelButton: true,
            confirmButtonText: '삭제',
            cancelButtonText: '취소',
            customClass: {
                container: 'position-absolute swal2-high-zindex'
            },
            confirmButtonColor: '#dc3545', // 삭제는 빨간색으로
            reverseButtons: false // 취소 버튼을 오른쪽으로

        });
        if (!isConfirmed) return;

        try {
            //주소만들기
            const finalDeleteUrl  = deleteApprovalLineUrl.replace('{lineId}', lineId);
            const resp = await fetch(finalDeleteUrl , {
                method: 'DELETE'
            });

            //값지우고 -> 선택한 모달 결재선 값 있는지 확인 -> 없으면 숨겨버려서 없애버리기
            if (resp.ok) {
                ApprovalLineView.removeLineElement(lineId);
                if (this.selectedLineId === lineId) {
                    this.selectedLineId = null;
                    document.getElementById('confirmLineBtn').disabled = true;
                }
            }
        } catch (error){
            console.error('결재선 삭제 중 오류 : ', error);
            Swal.fire({
                icon: 'error',
                title: '오류 발생',
                text: '결재선 삭제중 오류가 발생했습니다'
            });
        }
    },

}


