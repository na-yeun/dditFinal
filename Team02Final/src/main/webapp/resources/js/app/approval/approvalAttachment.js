//첨부파일 공통코드 부분
const ApprovalAttachment = {
    //첨부파일 정보 담아놓을 set 컬렉션 설정하기
    attachments: new Set(),

    //초기화하기
    initialize() {
        const attachmentInput = document.getElementById('attachmentInput');
        if (attachmentInput) {
            attachmentInput.addEventListener('change', (e) => {
                console.log('파일 선택됨:', e.target.files);  // 선택된 파일 로그
                this.handleFileSelect(e.target.files);
            });
        }
    },

    //파일선택했을때 컬렉션에담고 뷰를 업데이트 렌더링하기
    handleFileSelect(files) {
        console.log('새로 선택된 파일들:', files);

        // 기존 파일들을 유지하면서 새 파일 추가
        Array.from(files).forEach(file => {
            this.attachments.add(file);
        });

        console.log('업데이트된 전체 파일들:', this.attachments);
        this.updateDisplay();
    },

    //화면 업데이트하기
    updateDisplay() {
        //<div class="attachment-display-area" style="display: none;">
        const displayArea = document.querySelector('.attachment-display-area');
        //<ul id="attachmentDisplayList" class="attachment-list"></ul>
        const displayList = document.getElementById('attachmentDisplayList');

        console.log('updateDisplay 호출됨');
        console.log('현재 attachments:', this.attachments);
        console.log('displayArea:', displayArea);
        console.log('displayList:', displayList);

        //파일 있는지 검증해서 있다면
        if (this.attachments.size > 0) {
            //영역 노출시키기
            console.log('첨부파일 있음, 표시 시도');
            displayArea.style.display = 'block';
            displayList.innerHTML = Array.from(this.attachments)
                .map(file => `
                    <li class="attachment-item">
                        <!-- 파일 이름 출력시키기-->
                        ${file.name}
                        <!-- 동적으로 지울수도 있게한다 -->
                        <span class="remove-btn" onclick="ApprovalAttachment.removeFile('${file.name}')">×</span>
                    </li>
                `).join('');
            console.log('생성된 HTML:', displayList.innerHTML);
        } else {
            console.log('첨부파일 없음, 영역 숨김');
            displayArea.style.display = 'none';
            displayList.innerHTML = '';
        }
    },

    //파일 동적으로 지우고 업데이트하기
    removeFile(fileName) {
        this.attachments = new Set(
            Array.from(this.attachments).filter(file => file.name !== fileName)
        );
        this.updateDisplay();
    },

    // 첨부파일 싹다 지워버리기~~
    clearAttachments() {
        this.attachments.clear();
        this.updateDisplay();
        document.getElementById('attachmentInput').value = '';
    },

    // 전송할때 보낼 게터 메소드
    getAttachmentsForDraft() {
        return Array.from(this.attachments);
    }
};