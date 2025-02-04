const ApprovalFree = {
    initialize() {
        // 요소 초기화
        this.elements = {
            freeFormContainer: document.getElementById('freeFormContainer'),
            docTitle: document.getElementById('docTitle'),
            purpose: document.getElementById('purpose'),
            startDate: document.getElementById('freeStartDate'),
            endDate: document.getElementById('freeEndDate'),
            singleDate: document.getElementById('singleDate'),
            freeFormContent: document.getElementById('freeFormContent'),
            freeFormPreviewBtn: document.getElementById('freeFormPreviewBtn')
        };

        // 에디터 초기화하기
        this.initializeEditor();

        // 자유양식서 영역 표시
        this.elements.freeFormContainer.style.display = 'block';

        // 이벤트 리스너 설정
        this.setFreeFormEventListeners();
    },

    // 에디터 초기화
    initializeEditor() {
        ApprovalEditor.initialize('freeFormContent', 'freeFormEditor');
    },

    // 이벤트 리스너 설정
    setFreeFormEventListeners() {
        // 미리보기 버튼 클릭 이벤트
        this.elements.freeFormPreviewBtn.addEventListener('click', this.handleFreeFormPreview.bind(this));

        // 제목 입력 이벤트
        this.elements.docTitle.addEventListener('input', this.validateForm.bind(this));

        // 목적/용도 입력 이벤트
        this.elements.purpose.addEventListener('input', this.validateForm.bind(this));

        // 날짜 타입 선택 이벤트 라디오버튼으로 토글형식
        document.querySelectorAll('input[name="dateType"]').forEach(radio => {
            radio.addEventListener('change', this.toggleDateSelection.bind(this));
        });

        // 날짜 검증 이벤트 날짜건들이면 이렇게됨
        ['startDate', 'endDate', 'singleDate'].forEach(id => {
            const element = document.getElementById(id);
            if (element) {
                element.addEventListener('change', this.validateDates.bind(this));
            }
        });
    },

    // 폼 유효성 검사
    validateForm() {
        const errors = [];

        if (!this.elements.docTitle.value.trim()) {
            errors.push("문서 제목을 입력해주세요.");
        }

        if (!this.elements.purpose.value.trim()) {
            errors.push("목적/용도를 입력해주세요.");
        }

        if (!ApprovalEditor.getContent().trim()) {
            errors.push("상세 내용을 입력해주세요.");
        }

        return {
            isValid: errors.length === 0,
            errors: errors
        };
    },

    //날짜 검증하기
    validateDates() {
        const dateType = document.querySelector('input[name="dateType"]:checked').value;

        if (dateType === 'period') {
            const startDate = document.getElementById('freeStartDate');
            const endDate = document.getElementById('freeEndDate');
            const start = new Date(startDate.value);
            const end = new Date(endDate.value);

            if (end < start) {
                Swal.fire({
                    icon: 'warning',
                    title: '날짜 선택 오류',
                    text: '종료일은 시작일보다 빠를 수 없습니다.'
                });
                endDate.value = startDate.value;
                return false;
            }
        } else {
            const singleDate = document.getElementById('singleDate');
            if (!singleDate.value) {
                Swal.fire({
                    icon: 'warning',
                    title: '날짜 선택 오류',
                    text: '날짜를 선택해주세요.'
                });
                return false;
            }
        }
        return true;
    },



    // 날짜 입력 UI 토글 함수 추가
    toggleDateSelection() {
        //데이터 갖고오기
        const periodSelection = document.getElementById('periodSelection');
        const singleDateSelection = document.getElementById('singleDateSelection');
        const dateType = document.querySelector('input[name="dateType"]:checked').value;

        //하나만 뜨게하기
        if (dateType === 'period') {
            periodSelection.classList.remove('d-none');
            singleDateSelection.classList.add('d-none');
        } else {
            periodSelection.classList.add('d-none');
            singleDateSelection.classList.remove('d-none');
        }
    },

    // 미리보기 처리
    async handleFreeFormPreview() {

        //먼저 검증때리기
        const validation = this.validateForm();

        if (!validation.isValid) {
            Swal.fire({
                icon: 'warning',
                title: '입력 오류',
                html: validation.errors.join('<br>')
            });
            return;
        }

        // 날짜 유효성 검사
        if (!this.validateDates()) {
            return;
        }

        const formData = this.collectFormData();

        try {
            await ApprovalPreview.handlePreview(formData, 'f01');
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: '오류 발생',
                text: '미리보기 생성 중 오류가 발생했습니다.'
            });
        }
    },

// 자유양식서 resetForm
    resetForm() {
        try {
            // 에디터 완전 초기화
            ApprovalEditor.destroy();
            ApprovalEditor.initialize('freeFormContent', 'freeFormEditor');

            // 기본 필드 초기화
            this.elements.docTitle.value = '';
            this.elements.purpose.value = '';

            // 날짜 필드 초기화
            this.elements.startDate.value = '';
            this.elements.endDate.value = '';
            this.elements.singleDate.value = '';

            // 날짜 타입 라디오 버튼 초기화 (기간 선택으로)
            const periodRadio = document.getElementById('periodType');
            if (periodRadio) {
                periodRadio.checked = true;
                this.toggleDateSelection(); // UI 상태 업데이트
            }

        } catch (error) {
            console.error('자유양식서 폼 초기화 중 오류 발생:', error);
            Swal.fire({
                icon: 'error',
                title: '초기화 오류',
                text: '폼 초기화 중 오류가 발생했습니다.'
            });
        }
    },

    // 폼 데이터 수집 (기안용 여러가지 방법으로 보내보기위해서 이번엔 여기다가 선언함)
    collectFormData() {
        const dateType = document.querySelector('input[name="dateType"]:checked').value;
        let periodInfo = '';

        if (dateType === 'period') {
            const startDate = this.elements.startDate.value;
            const endDate = this.elements.endDate.value;
            if (startDate && endDate) {
                periodInfo = `${startDate} ~ ${endDate}`;
            }
        } else {
            const singleDate = this.elements.singleDate.value;
            if (singleDate) {
                periodInfo = singleDate;
            }
        }

        return {
            docTitle: this.elements.docTitle.value,
            purpose: this.elements.purpose.value,
            dateType: dateType,  // dateType 추가
            startDate: this.elements.startDate.value || '',  // 날짜 값들도 개별적으로 전송
            endDate: this.elements.endDate.value || '',
            singleDate: this.elements.singleDate.value || '',
            period: periodInfo || '-',
            docContent: ApprovalEditor.getContent()
        };
    }
};

// 자유양식서 (ApprovalFree.js)
document.getElementById('freeFormDataBtn')?.addEventListener('click', () => {
    // 제목 설정
    document.getElementById('docTitle').value = '프로젝트 리팩토링 계획서';

    // 목적/용도 설정
    document.getElementById('purpose').value = '프로젝트 리팩토링 계획 보고';

    // 기간 설정 (1개월)
    const today = new Date();
    const endDate = new Date();
    endDate.setMonth(today.getMonth() + 1);

    document.getElementById('freeStartDate').value = today.toISOString().split('T')[0];
    document.getElementById('freeEndDate').value = endDate.toISOString().split('T')[0];

    // 라디오 버튼 설정 (기간 지정으로)
    document.getElementById('periodType').checked = true;
    document.getElementById('singleType').checked = false;

    // 에디터 내용 설정
    ApprovalEditor.setContent(`
        <h3>1. 프로젝트 개요</h3>
        <p>현재 진행중인 프로젝트 및 기능 개선을 위한 전면 개편</p>
        <br>
        <h3>2. 주요 개선사항</h3>
        <ul>
            <li>반응형 웹 디자인 적용</li>
            <li>사용자 경험 개선</li>
            <li>보안 강화</li>
        </ul>
        <br>
        <h3>3. 소요 예산</h3>
        <p>총 3,000만원 (상세 내역 별첨)</p>
    `);
});