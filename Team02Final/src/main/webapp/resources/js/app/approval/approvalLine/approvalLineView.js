const ApprovalLineView = {

    //저장된 결재선 렌더링하기
    renderSavedLines(savedLines) {
        console.log('받은 데이터:', savedLines); // 데이터 구조 확인

        const container = document.querySelector('.saved-lines-container');
        container.innerHTML = '';

        //단일 객체인 경우에는 배열로 변환을 시킨다
        const lines = Array.isArray(savedLines) ? savedLines : [savedLines];

        lines.forEach(line => {
            console.log('각 라인:', line); // 개별 라인 데이터 확인

            // div를 만들어내는거임
            const lineDiv = document.createElement('div');
            // 새로만든 div에대한 클래스 이름은 saved line item이 되는거고
            lineDiv.className = 'saved-line-item';
            lineDiv.setAttribute('data-line-id', line.apprlineId);

            //라벨형식의 결재선리스트 정보를 담기 class설정해준 이유는 참조자검증하기위해 데이터담는게 편하게담으려고
            let approverContent = line.approvers.map(approver => `<span class="approver-info" data-emp-id="${approver.EMPID}" data-name="${approver.NAME}" data-position="${approver.POSITION}" data-department="${approver.DEPARTMENTNAME}" data-final-yn="${approver.APPROVERFINALYN}">(${approver.APPROVERNUM}차) ${approver.APPROVERFINALYN === 'Y' ? '<span class="final-mark">[전결]</span> ' : ''}${approver.POSITION} ${approver.NAME}</span>`).join(' → ');
            lineDiv.innerHTML = `
                <div class="line-title fw-bold">${line.apprlineTitle || '(제목없음)'}</div>
                <div class="line-content">${approverContent}</div>
                <div class="line-actions">
                    <button class="btn btn-sm btn-outline-danger delete-btn">삭제</button>
                </div>
                `;
            // 이벤트 리스너 추가
            this.setLineItemEvents(lineDiv, line.apprlineId);
            // 컨테이너에 추가하는 부분이 누락되어 있었음
            container.appendChild(lineDiv);
        });
    },


    //리스너
    setLineItemEvents(lineDiv, lineId) {
        if (!lineDiv) return;  // null check 추가

        lineDiv.addEventListener('click', (e) => {
            //삭제 버튼 클릭은 제외하기
            if (!e.target.closest('.delete-btn')) {
                // 이전 선택한것은 초기화해야함
                document.querySelectorAll('.saved-line-item').forEach(item => {
                    item.classList.remove('selected');
                });
                // 현재 항목 선택 표시하기
                lineDiv.classList.add('selected');

                // 결재선 관리자에게 선택된 lineId 전달
                ApprovalLineManager.selectLine(lineId);
            }
        })

        const deleteBtn = lineDiv.querySelector('.delete-btn');
        if (deleteBtn) {  // null check 추가
            deleteBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                ApprovalLineManager.deletedLine(lineId);
            });
        }
    },

    //결재선 저장 성공 후 처리
    handleSaveSuccess(savedLine) {
        //선택된 결재자 목록 초기화
        document.getElementById('approverList').innerHTML = '';

        // 새로운 결재선 생성
        const lineDiv = document.createElement('div');
        lineDiv.className = 'saved-line-item';
        lineDiv.setAttribute('data-line-id', savedLine.apprlineId);

        // 결재자 텍스트 생성
        let approverContent = savedLine.approvers.map(approver => `<span class="approver-info" data-emp-id="${approver.EMPID}" data-name="${approver.NAME}" data-position="${approver.POSITION}" data-department="${approver.DEPARTMENTNAME}" data-final-yn="${approver.APPROVERFINALYN}">(${approver.APPROVERNUM}차) ${approver.APPROVERFINALYN === 'Y' ? '<span class="final-mark">[전결]</span> ' : ''}${approver.POSITION} ${approver.NAME}</span>`).join(' → ');
        lineDiv.innerHTML = `
            <div class="line-title fw-bold">${savedLine.apprlineTitle || '(제목없음)'}</div>
            <div class="line-content">${approverContent}</div>
            <div class="line-actions">
                <button class="btn btn-sm btn-outline-danger delete-btn">삭제</button>
            </div>
        `;

        // 이벤트 리스너 추가
        this.setLineItemEvents(lineDiv, savedLine.apprlineId);


        // 기존 목록의 맨 앞에 추가
        const container = document.querySelector('.saved-lines-container');
        if (container.firstChild) {
            container.insertBefore(lineDiv, container.firstChild);
        } else {
            container.appendChild(lineDiv);
        }

        // 성공메세지 표시하기
        Swal.fire({
            icon: 'success',
            title: '저장 완료',
            text: '결재선이 저장되었습니다.'
        });
    },

    removeLineElement(lineId) {
        const element = document.querySelector(`[data-line-id="${lineId}"]`);
        element?.remove();
    },

    // 결재자 추가 함수
    addApprover(emp) {
        const tbody = document.getElementById('approverList');

        // 최대 결재차수 검증
        if (tbody.children.length >= 5) {
            Swal.fire({
                icon: 'warning',
                title: '결재차수 제한',
                text: '결재차수는 5차까지만 지정 가능합니다.'
            });
            return;
        }

        //결재자 정보 저장을 위한 데이터 속성 추가하기
        const row = document.createElement('tr');
        row.setAttribute('data-emp-id', emp.empId);

        // tbody가 비어있으면(첫 번째 결재자면) checked 속성 추가
        const isFirstApprover = tbody.children.length === 0;

        row.innerHTML = `
            <td>${tbody.children.length + 1}</td>
            <td>${emp.departmentName}</td>
            <td>${emp.position} ${emp.name}</td>
<!--            전결권 지정 라디오 버튼으로 운용-->
            <td>
                <input type="radio" name="finalApprover" class="final-approver-radio" ${isFirstApprover ? 'checked' : ''}> 
            </td>
            <td>
                <div class="btn-group" role="group">
                    <!-- 맨위에 결재자가 올라가면 위 화살표는 보이지 않아야한다 -->
                    <button type="button" class="btn btn-sm btn-outline-secondary up-btn" 
                            ${tbody.children.length === 0 ? 'style="display:none"' : ''}>
                        ↑
                    </button>
                    <!-- 맨 아래의 경우도 마찬가지 -->
                    <button type="button" class="btn btn-sm btn-outline-secondary down-btn">
                        ↓
                    </button>
                    <button type="button" class="btn btn-sm btn-danger delete-btn">삭제</button>
                </div>
            </td>
        `;

        //행클릭 이벤트
        row.addEventListener('click', (e) => {
            // 버튼 클릭은 제외한다
            if (!e.target.closest('button')) {
                //기존의 선택된 행의 스타일 제거한다
                document.querySelectorAll('#approverList tr.selected').forEach(tr => {
                    tr.classList.remove('selected');
                });
                //현재 행의 스타일 추가하기
                row.classList.add('selected');
            }
        });

        //위로 이동하는 버튼 이벤트
        row.querySelector('.up-btn').addEventListener('click', () => {
            const prevRow = row.previousElementSibling;
            if (prevRow) {
                tbody.insertBefore(row, prevRow);
                this.updateMoveButtons();
                this.reorderApprovers();
            }
        });

        //아래로 이동하는 버튼 이벤트
        row.querySelector('.down-btn').addEventListener('click', () => {
            const nextRow = row.nextElementSibling;
            if (nextRow) {
                tbody.insertBefore(nextRow, row);
                this.updateMoveButtons();
                this.reorderApprovers();
            }
        })

        //삭제 버튼 이벤트
        row.querySelector('.delete-btn').addEventListener('click', () => {
            row.remove();
            this.updateMoveButtons();
            this.reorderApprovers(); //순서 재정렬하기
        });

        tbody.appendChild(row);
        this.updateMoveButtons();

    },

    //결재자 정보를 수집하기 비동기로 보내야하니까
    collectApprovers() {
        const approvers = [];
        const rows = document.querySelectorAll('#approverList tr');

        rows.forEach((row, index) => {

            const tdContent = row.children[2].textContent.trim();
            const [position, ...nameParts] = tdContent.split(' ');
            const name = nameParts.join(' '); // 이름에 공백이 있을 경우를 대비

            const approver = ({
                empId: row.getAttribute('data-emp-id'),
                order: index + 1, //여기서 1차결재인지 2차결재인지 정해진다
                departmentName: row.children[1].textContent.trim(),
                position: position,
                name: name,
                approverFinalYn: row.querySelector('.final-approver-radio').checked ? 'Y' : 'N'
            });
            console.log('Collected approver:', approver);  // 각 결재자 정보 확인
            approvers.push(approver);
        });

        return approvers;
    },

    //이동 버튼 상태 업데이트 함수
    updateMoveButtons: function () {
        const rows = document.querySelectorAll('#approverList tr');
        rows.forEach((row, index) => {
            const upBtn = row.querySelector('.up-btn');
            const downBtn = row.querySelector('.down-btn');

            // 첫번째 행
            if (index === 0) {
                upBtn.style.display = 'none';
            } else {
                upBtn.style.display = 'inline-block';
            }

            // 마지막 행
            if (index === rows.length - 1) {
                downBtn.style.display = 'none';
            } else {
                downBtn.style.display = 'inline-block';
            }
        });
    },

    // 결재자 순서 재정렬 함수
    reorderApprovers: function () {
        const rows = document.querySelectorAll('#approverList tr');
        rows.forEach((row, index) => {
            row.querySelector('td:first-child').textContent = index + 1;
        });
    }
}