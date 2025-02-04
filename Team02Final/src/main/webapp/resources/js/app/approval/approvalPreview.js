const ApprovalPreview = {

    //실제 적용된 결재선 ID를 저장함
    appliedLineId: null,

    //기안시에 쓸 메소드
    getAppliedLineId() {
        return this.appliedLineId;
    },

    initialize() {
        // 필요한 DOM 요소 초기화 하기
        this.elements = {
            previewContainer: document.getElementById('previewContainer'),
            vacationFormContainer: document.getElementById('vacationFormContainer'),
            buttonGroup: document.querySelector('.button-group')
        };

        // 초기 상태 설정
        if (this.elements.previewContainer) {
            this.elements.previewContainer.style.display = 'none';
        }
        if (this.elements.vacationFormContainer) {
            this.elements.vacationFormContainer.style.display = 'none';
        }
        if (this.elements.buttonGroup) {
            this.elements.buttonGroup.classList.remove('show');
        }
    },

    // 문서 미리보기 공통 처리
    async handlePreview(formData, formId) {
        try{
            // console.log('미리보기 요청 데이터:', formData);

            //여기서 뭘받을지정해져버림 v01이면 이제 컨트롤러에서 휴가 문서 템플릿을꺼내오든지할거임
            const finalPreviewUrl = preViewTypeUrl.replace('{formId}', formId);

            const resp = await fetch(finalPreviewUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });
            // console.log('적용전의 문서 상태:', approvalElements.previewContainer.innerHTML);

            // 1) resp.ok 확인
            if (!resp.ok) {
                // 2) 서버에서 보낸 에러 메시지를 받기
                const serverMessage = await resp.text();
                //    (만약 JSON 구조라면 resp.json()으로 읽고 serverMessage = obj.message 식으로 추출)

                // 3) 해당 메시지가 있으면 그대로 Error를 throw
                throw new Error(serverMessage || `HTTP 서버 에러가 발생했습니다. status : ${resp.status}`);
            }

            const html = await resp.text();
            this.updatePreview(html);

        }catch (error){
            console.error('미리보기 생성 중 오류 발생 : ', error);
            const message = error.message.replace(/^"|"$/g, '').replace(/\\n/g, '\n');
            Swal.fire({
                icon: 'error',
                title: '오류 발생',
                html: `<div style="white-space: pre-line; text-align: center;">${message}</div>`
            });
        }
    },

    //미리보기 업뎃하기
    updatePreview(html){
        //approvalForm 초기화 세팅쪽에 배치되어있음
        approvalElements.previewContainer.innerHTML = html;
        // console.log('현재 문서 상태:', approvalElements.previewContainer.innerHTML);
        this.showPreview();
    },

    // 문서와 버튼 표시
    showPreview() {
        //미리보기 컨테이너 활성화(표시)
        approvalElements.previewContainer.style.display = 'block';

        // 문서 폼 컨테이너 숨기기 (문서 타입별)
        switch (ApprovalCommon.selectedFormId) {
            case 'v01':
                approvalElements.vacationFormContainer.style.display = 'none';
                break;
            case 'e01':
                approvalElements.expenseFormContainer.style.display = 'none';
                break;
            case 'f01':
                approvalElements.freeFormContainer.style.display = 'none';
                break;
        }
        //그룹 버튼 영역 활성화
        approvalElements.buttonGroup.classList.add('show');

        // 결재선/참조자 버튼 영역 표시하기
        const approvalButtonArea = document.querySelector('.approval-button-area');
        if (approvalButtonArea) {
            approvalButtonArea.style.display = 'block';
        }

        // 셀렉트박스 비활성화 (추가)
        approvalElements.formSelect.disabled = true;
    },

    //결재선 추가시켜서 html문서 다시 렌더링해서 갖고오기
    async saveApprovalLine(lineId){
        try {
            // 현재 미리보기 문서 HTML 가져오기

            console.log('lineId : ', lineId)
            const previewContainer = document.getElementById('previewContainer');
            console.log('Preview container : ', previewContainer)

            if (!previewContainer) {
                throw new Error('미리보기 컨테이너를 찾을 수 없습니다~');
            }
            const appendApproversFormUrl = appendApproversUrl.replace('{lineId}', lineId);
            const resp = await fetch(appendApproversFormUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    documentHtml: previewContainer.innerHTML
                })
            });

            if (!resp.ok) {
                throw new Error('결재선 저장 중 오류가 발생했습니다');
            }

            const updatedHtml = await resp.text();
            //결재선 적용 성공시에 결재선 ID를 저장해서 이걸갖고 기안할때 쓰기로
            this.appliedLineId = lineId;
            //렌더링 업데이트
            this.updatePreview(updatedHtml);
        } catch(error) {
            console.error('결재선 저장 중 오류', error);
            throw error;
        }
    }
};