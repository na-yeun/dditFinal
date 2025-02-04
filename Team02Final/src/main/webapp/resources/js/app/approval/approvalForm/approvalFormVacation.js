const ApprovalVacation = {

    elements: {},

    initialize() {
        //요소 초기화
        this.elements = {
            startDate: document.getElementById('startDate'),
            endDate: document.getElementById('endDate'),
            vacationType: document.getElementById('vacationType')
        };

        //에디터 함수 초기화
        this.initializeEditor();
        //휴가 현황
        this.vacationStatus();
        //휴가 종류
        this.fetchVacationTypes();
        //휴가 영역 가시화
        document.getElementById('vacationFormContainer').style.display = 'block';
        // 검증 이벤트 리스너 활성화
        this.setVacationEventListeners();
    },

    //에디터 초기화 함수 추가하기
    initializeEditor(){
        ApprovalEditor.initialize('docContent', 'editor');
    },

    setVacationEventListeners(){
        //날짜 검증 이벤트 등록
        // 날짜 유효성 검사
        const startDate = document.getElementById('startDate');
        const endDate = document.getElementById('endDate');

        // this.validateDates를 사용하여 객체의 메서드를 참조
        // bind(this)를 사용하여 this 컨텍스트를 유지한다
        startDate.addEventListener('change', this.validateDates.bind(this));
        endDate.addEventListener('change', this.validateDates.bind(this));

        // 미리보기 버튼 이벤트
        document.getElementById('previewBtn').addEventListener('click', this.handleVacationPreview.bind(this));
    },

    // 연차 현황 조회
    vacationStatus() {
        // Form에서 써둔거 갖고오기
        fetch(vacationStatusUrl)
            .then(resp => resp.json())
            .then(data => {
                this.updateVacationStatusTable(data);
            })
            .catch(error => console.error('Error:', error));
    },

    updateVacationStatusTable: function (data) {
        console.log("데이터 확인:", data); // 데이터가 제대로 왔는지 확인

        // 테이블 갖고오기
        // 자꾸까먹음 vacation-table은 클래스이름이므로 .을찍고찾아야함
        const table = document.querySelector('.vacation-table');

        // 데이터 행만 업데이트 (첫번째 tr는 냅두고 두 번째 tr만 적용)
        // <tr><th></th></tr> 이게 첫번째 <tr><td></td></tr> 이게 두번째
        const dataRow = table.rows[1];
        if (dataRow) {

            // 총 연차 = 기본부여 + 추가부여
            const totalVacation = data.vstaAllcount + data.vstaAppend;

            dataRow.innerHTML = `
            <td>${totalVacation}</td>
            <td>${data.vstaUse}</td>
            <td>${data.vstaNowcount}</td>
            <td>${data.vstaSickcount}</td>
        `;
        }
    },

    //휴가 종류 조회 로직
    fetchVacationTypes: function () {
        fetch(vacationTypeUrl)
            .then(response => response.json())
            .then(data => {
                this.updateVacationTypeSelect(data);
            })
            .catch(error => console.error('Error:', error));
    },

    updateVacationTypeSelect: function (data) {
        const select = document.getElementById('vacationType');
        select.innerHTML = '<option value="">선택하세요</option>';
        data.forEach(type => {
            // value에는 code를, 화면에 보여주는 텍스트는 name을 미리 지정해놔야한다
            select.innerHTML += `<option value="${type.VACCODE}" data-name="${type.VACNAME}">${type.VACNAME}</option>`;
        });
    },


    //날짜 검증 로직
    validateDates: function () {
        const startDate = document.getElementById('startDate');
        const endDate = document.getElementById('endDate');
        const start = new Date(startDate.value);
        const end = new Date(endDate.value);
        const vacTypeSelect = document.getElementById('vacationType');
        const selectedVacType = vacTypeSelect.value;

        if (end < start) {
            Swal.fire({
                icon: 'warning',
                title: '날짜 선택 오류',
                text: '종료일은 시작일보다 빠를 수 없습니다.'
            });
            endDate.value = startDate.value;
            return false;
        }

        //this를붙혀야 인식이됨
        // 주말 체크
        if (this.isWeekend(start) || this.isWeekend(end)) {
            Swal.fire({
                icon: 'warning',
                title: '날짜 선택 오류',
                text: '주말은 선택할 수 없습니다.'
            });
            return false;
        }

        // 연차/반차일 경우에만 잔여 연차 체크하기
        if (selectedVacType === 'V001' || selectedVacType === 'V002') {
            //remainingDays는 row1 cell2번을가르키고있는데 이것은 잔여 연차 부분을 나타낸다 <th>잔여 연차</th>
            // row0번은 th니까 잔여연차라는이름그대로고 1번이 실제값임 cell 2번은 0 1 2이렇게 3번째 순서값
            const remainingDays = parseFloat(document.querySelector('.vacation-table').rows[1].cells[2].textContent);
            console.log("remainingDays : ", remainingDays);
            const requestedDays = this.calculateRequestedDays(start, end, selectedVacType);
            console.log("requestDays : ", requestedDays);
            if (requestedDays > remainingDays) {
                Swal.fire({
                    icon: 'warning',
                    title: '연차 초과',
                    text: `신청한 휴가 일수(${requestedDays}일)가 잔여 연차(${remainingDays}일)를 초과합니다.`
                });
                return false;
            }
        }
        return true;
    },

    // 신청 일수 계산 (프론트엔드용)
    calculateRequestedDays: function(start, end, vacType) {
        let days = 0;
        let current = new Date(start);

        while (current <= end) {
            if (current.getDay() !== 0 && current.getDay() !== 6) {
                days++;
            }
            current.setDate(current.getDate() + 1);
        }

        return vacType === 'V002' ? days * 0.5 : days;
    },

    // 널 체킹
    validateVacationForm() {
        const vacationType = this.elements.vacationType.value;
        const startDate = this.elements.startDate.value;
        const endDate = this.elements.endDate.value;
        const errors = [];

        if (!vacationType || vacationType === "선택하세요") {
            errors.push("휴가 종류를 선택해주세요.");
        }

        if (!startDate) {
            errors.push("시작일을 입력해주세요.");
        }

        if (!endDate) {
            errors.push("종료일을 입력해주세요.");
        }

        return {
            isValid: errors.length === 0,
            errors: errors
        };
    },

    //날짜끝에거보는거임
    isWeekend: function (date) {
        const day = date.getDay();
        return day === 0 || day === 6;
    },

    //미리보기 문서로 보내기전에 담아서 보내기
    handleVacationPreview: async function () {
        const validation = this.validateVacationForm();

        if (!validation.isValid) {
            Swal.fire({
                icon: 'error',
                title: '입력 오류',
                html: validation.errors.join('<br>')
            });
            return;
        }

        if (!this.validateDates()) return;

        // 휴가 타입 셀렉트 엘리먼트
        const vacTypeSelect = document.getElementById('vacationType');
        const selectedOption = vacTypeSelect.options[vacTypeSelect.selectedIndex];

        const formData = {
            vacationType: selectedOption.text,        // 휴가 이름
            vacCode: selectedOption.value,            // 휴가 코드 (V001, V002 등) 추가
            startDate: document.getElementById('startDate').value,
            endDate: document.getElementById('endDate').value,
            docContent: ApprovalEditor.getContent()
        };

        try {
            await ApprovalPreview.handlePreview(formData, 'v01');

            //결재선 추가되면 approver도 함께 보내기
            const approvers = ApprovalLineView.collectApprovers();
            if (approvers.length > 0) {
                await ApprovalPreview.saveApprovalLine(approvers);
            }
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: '오류 발생',
                text: '미리보기 생성 중 오류가 발생했습니다.'
            });
        }
    },

    // 휴가신청서 resetForm
    resetForm() {
        try {
            // 에디터 완전 초기화
            ApprovalEditor.destroy();
            ApprovalEditor.initialize('docContent', 'editor');

            // 휴가 종류 초기화
            if (this.elements.vacationType) {
                this.elements.vacationType.value = '';
            }

            // 날짜 초기화
            if (this.elements.startDate) {
                this.elements.startDate.value = '';
            }
            if (this.elements.endDate) {
                this.elements.endDate.value = '';
            }

            // 휴가 현황 테이블도 다시 로드
            this.vacationStatus();

            // 휴가 종류 옵션도 다시 로드
            this.fetchVacationTypes();

        } catch (error) {
            console.error('휴가신청서 폼 초기화 중 오류 발생:', error);
            Swal.fire({
                icon: 'error',
                title: '초기화 오류',
                text: '휴가신청서 초기화 중 오류가 발생했습니다.'
            });
        }
    }
}

document.getElementById('vacationDataBtn')?.addEventListener('click', () => {
    // 날짜 설정 (오늘로부터 3일 후까지)
    const today = new Date();
    const endDate = new Date();
    endDate.setDate(today.getDate() + 2);

    document.getElementById('startDate').value = today.toISOString().split('T')[0];
    document.getElementById('endDate').value = endDate.toISOString().split('T')[0];

    // 에디터 내용 설정
    ApprovalEditor.setContent('<p>개인 사유로 인한 연차 휴가를 신청합니다.</p><p>기간 동안 업무 인수인계는 완료했습니다.</p>');
});
