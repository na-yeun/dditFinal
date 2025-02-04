const ApprovalExpense = {
    currentItems: [], // 현재 추가된 지출 항목들을 관리

    initialize() {
        //이전상태 초기화


        // 요소 초기화
        this.elements = {
            expenseType: document.getElementById('expenseType'),
            expenseCategory: document.getElementById('expenseCategory'),
            paymentMethod: document.getElementById('paymentMethod'),
            addItemBtn: document.getElementById('addExpenseItem'),
            itemList: document.getElementById('expenseItemList'),
            totalAmount: document.getElementById('totalExpenseAmount')
        };

        // 여기에 제목 길이 제한 코드 추가
        const expenseNameInput = document.getElementById('expenseName');
        expenseNameInput.setAttribute('maxlength', '30');
        expenseNameInput.addEventListener('input', (e) => {
            if (e.target.value.length > 30) {
                e.target.value = e.target.value.slice(0, 30);
                Swal.fire({
                    icon: 'warning',
                    title: '입력 제한',
                    text: '제목은 30글자를 초과할 수 없습니다.'
                });
            }
        });

        this.resetForm(); //먼저 초기화하기

        // 에디터 초기화
        this.initializeEditor();
        // 지출 구분, 분류 카테고리 로딩하기
        this.fetchExpenseCategories();
        this.fetchExpenseTypes();
        // 지출결의서 영역 표시하기
        document.getElementById('expenseFormContainer').style.display = 'block';
        // 이벤트 리스너 설정
        this.setExpenseEventListeners();
    },

    //에디터 초기화
    initializeEditor() {
        ApprovalEditor.initialize('expenseDocContent', 'expenseEditor');
    },

    // 지출 구분 코드 로드
    async fetchExpenseTypes() {
        try {
            const expenseTypeSelect = this.elements.expenseType;
            const response = await fetch(expenseTypeUrl);
            const types = await response.json();

            // 셀렉트박스 초기화
            expenseTypeSelect.innerHTML = '<option value="">선택하세요</option>';

            types.forEach(type => {
                expenseTypeSelect.innerHTML += `
                <option value="${type.EXPTYPECODE}" data-name="${type.EXPTYPENAME}">
                    ${type.EXPTYPENAME}
                </option>`;
            });
        } catch (error) {
            console.error('지출 구분 로드 중 오류:', error);
        }
    },

    // 지출 분류 코드 로드
    async fetchExpenseCategories() {
        try {
            const response = await fetch(expenseCategoriesUrl);
            this.categoryData = await response.json(); // 데이터를 객체에 저장
        } catch (error) {
            console.error('지출 분류 로드 중 오류:', error);
        }
    },

    // 이벤트 리스너 설정
    setExpenseEventListeners() {
        // 항목 추가 버튼 이벤트 리스너
        if (this.elements.addItemBtn) {
            // 기존 이벤트 리스너 제거 및 새로운 버튼 생성
            const oldBtn = this.elements.addItemBtn;
            const newBtn = oldBtn.cloneNode(true);
            if (oldBtn.parentNode) {  // 부모 노드 존재 확인
                oldBtn.parentNode.replaceChild(newBtn, oldBtn);
                this.elements.addItemBtn = newBtn;  // elements 객체 업데이트
            }
            // 새 버튼에 이벤트 리스너 추가
            this.elements.addItemBtn.addEventListener('click', () => {

                // 만약 expenseType이 아직 선택되지 않았다면, 사전승인 / 사후정산 중 하나 고르도록 유도해야함
                if (!this.elements.expenseType.value) {
                    Swal.fire({
                        icon: 'warning',
                        title: '지출 구분 선택',
                        text: '항목을 추가하기 전에 지출 구분을 먼저 선택하세요.'
                    });
                    return;
                }

                // 이미 항목 추가가 시작됐다면, 지출 구분 드롭다운 비활성화
                this.elements.expenseType.disabled = true;
                // 지출 항목 추가 로직
                this.addExpenseItem();
            });

            // 지출 구분 이벤트 리스너
            if (this.elements.expenseType) {
                // PS 선택 시 첨부파일 필수 체크
                this.elements.expenseType.addEventListener('change', (e) => {
                    if (e.target.value === 'PS') {
                        Swal.fire({
                            icon: 'info',
                            title: '증빙자료 필수',
                            text: '사후정산의 경우 반드시 증빙자료(영수증 등)를 첨부해야 합니다.'
                        });
                    }
                    // 항목 있을 때 변경 경고
                    if (this.currentItems.length > 0) {
                        Swal.fire({
                            icon: 'warning',
                            title: '지출 구분 변경',
                            text: '이미 항목이 추가되었습니다. 항목을 모두 삭제하거나 초기화해야 지출 구분을 다시 변경할 수 있습니다.'
                        });
                    }
                });
            }

            // 초기화 버튼 이벤트 리스너
            const resetAllBtn = document.getElementById('resetAllBtn');
            if (resetAllBtn) {
                resetAllBtn.addEventListener('click', () => {
                    // 항목 테이블 초기화하기
                    // 1) currentItems 지출내역 배열 초기화하기
                    if (this.elements.itemList) {
                        this.elements.itemList.innerHTML = '';
                    }
                    // 2) 총계 0원으로 초기화하기
                    this.currentItems = [];
                    if (this.elements.totalAmount) {
                        this.elements.totalAmount.textContent = '0원';
                    }
                    // 3) 지출 분류 초기화
                    if (this.elements.expenseType) {
                        this.elements.expenseType.disabled = false;
                        this.elements.expenseType.value = '';
                    }
                    // 4) 제목 초기화
                    const expenseName = document.getElementById('expenseName');
                    if (expenseName) {
                        expenseName.value = '';
                    }
                });
            }
            // 미리보기 버튼 이벤트 리스너
            const previewBtn = document.getElementById('expensePreviewBtn');
            if (previewBtn) {
                previewBtn.addEventListener('click', this.handleExpensePreview.bind(this));
            }
        }




        // 미리보기 버튼 이벤트
        document.getElementById('expensePreviewBtn').addEventListener('click', this.handleExpensePreview.bind(this));
    },

    //결과: "ITEM-1234-123456-001" 유일성 스크립트에서 최대한 만들어보기
    generateItemId() {
    const prefix = 'ITEM';
    const empId = window.myEmpId.slice(-4); // 사원번호 뒤 4자리만
    const timestamp = new Date().getTime().toString().slice(-6); // 밀리초 뒤 6자리
    const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0');
    return `${prefix}-${empId}-${timestamp}-${random}`;
    },

    // 지출 항목 추가
    addExpenseItem() {
        const itemId = this.generateItemId(); // 고유 ID 생성
        const newRow = document.createElement('tr');
        newRow.dataset.itemId = itemId;

        // 현재 선택된 지출 구분하기(PA, PS 등)
        const expenseTypeValue = this.elements.expenseType.value;
        // 오늘 날짜저장하기 (YYYY-MM-DD 형태로 변환)
        const todayString = new Date().toISOString().split('T')[0];

        // min, max를 동적으로 설정하기 위해 변수 준비해두기
        let minDateAttr = '';
        let maxDateAttr = '';

        // 사전승인(PA): 오늘부터 미래만
        if (expenseTypeValue === 'PA') {
            minDateAttr = `min="${todayString}"`;
        }
        // 사후정산(PS): 과거 ~ 오늘만
        else if (expenseTypeValue === 'PS') {
            maxDateAttr = `max="${todayString}"`;
        }

        newRow.innerHTML = `
        <td>
            <input type="date" class="form-control expense-date" 
                ${minDateAttr} ${maxDateAttr} required>
        </td>
        <td>
            <select class="form-control expense-category" required>
                <option value="">선택하세요</option>
                ${this.getCategoryOptions()}
            </select>
        </td>
        <td>
            <select class="form-control payment-method" required>
                <option value="">선택하세요</option>
                <option value="법인카드">법인카드</option>
                <option value="개인카드">개인카드</option>
                <option value="현금">현금</option>
                <option value="계좌이체">계좌이체</option>
            </select>
        </td>
        <td>
            <input type="number" class="form-control expense-quantity" min="1" value="1" required>
        </td>
        <td>
            <input type="number" class="form-control expense-price" min="0" required>
        </td>
        <td class="text-end item-amount">0원</td>
        <td>
            <input type="text" class="form-control expense-detail" 
                   maxlength="40" required>
        </td>
        <td class="text-center">
            <button type="button" class="btn btn-danger btn-sm delete-item">
                <i class="bi bi-trash"></i>
            </button>
        </td>
    `;

        this.elements.itemList.appendChild(newRow);
        // 이벤트 리스너 추가
        this.addItemEventListeners(newRow);

        // 항목 데이터 추가
        this.currentItems.push({
            id: itemId,
            date: '',
            category: '',
            paymentMethod: '',
            quantity: 1,
            price: 0,
            amount: 0,
            detail: ''
        });
    },

    // 지출 분류 옵션 생성 함수
    getCategoryOptions() {
        const categories = this.categoryData || [];
        return categories.map(category =>
            `<option value="${category.EXPCATEGORIESCODE}" 
                 data-name="${category.EXPCATEGORIESNAME}">
            ${category.EXPCATEGORIESNAME}
         </option>`
        ).join('');
    },

    // 항목별 이벤트 리스너
    addItemEventListeners(row) {
        const itemId = row.dataset.itemId;

        // 수량, 단가 변경 시 금액 계산 실시간으로하기
        ['expense-quantity', 'expense-price'].forEach(className => {
            row.querySelector(`.${className}`).addEventListener('input', () => {
                this.calculateItemAmount(row);
            });
        });

        // 삭제 버튼
        row.querySelector('.delete-item').addEventListener('click', () => {
            this.deleteExpenseItem(itemId);
        });

        // 내용 입력 필드 이벤트 리스너 추가
        const detailInput = row.querySelector('.expense-detail');
        detailInput.addEventListener('input', (e) => {
            if (e.target.value.length > 40) {
                e.target.value = e.target.value.slice(0, 40);
                Swal.fire({
                    icon: 'warning',
                    title: '입력 제한',
                    text: '내용은 40글자를 초과할 수 없습니다.'
                });
            }
        });
    },

    // 항목 금액 계산
    calculateItemAmount(row) {
        const quantity = Number(row.querySelector('.expense-quantity').value) || 0;
        const price = Number(row.querySelector('.expense-price').value) || 0;
        const amount = quantity * price;

        row.querySelector('.item-amount').textContent = amount.toLocaleString() + '원';

        this.updateTotalAmount();
    },

    // 총액 업데이트
    updateTotalAmount() {
        const total = Array.from(this.elements.itemList.querySelectorAll('.item-amount'))
            .reduce((sum, td) => {
                return sum + Number(td.textContent.replace(/[^0-9]/g, ''));
            }, 0);

        this.elements.totalAmount.textContent = total.toLocaleString() + '원';
    },

    // 항목 삭제
    deleteExpenseItem(itemId) {
        const row = this.elements.itemList.querySelector(`tr[data-item-id="${itemId}"]`);
        if (row) {
            row.remove();
            this.currentItems = this.currentItems.filter(item => item.id !== itemId);
            this.updateTotalAmount();
        }
    },

    // 유효성 검사
    validateExpenseForm() {
        const expenseName = document.getElementById('expenseName').value;
        const type = this.elements.expenseType.value;
        const rows = this.elements.itemList.getElementsByTagName('tr');

        const errors = [];

        if (!expenseName) errors.push("지출결의서 제목을 입력해주세요.");
        if (!type) errors.push("지출 구분을 선택해주세요.");
        if (rows.length === 0) errors.push("최소 하나 이상의 지출 내역을 추가해주세요.");

        // 각 행의 필수 입력값 검증
        Array.from(rows).forEach((row, index) => {
            const rowNum = index + 1;

            const date = row.querySelector('.expense-date')?.value;
            const category = row.querySelector('.expense-category')?.value;
            const paymentMethod = row.querySelector('.payment-method')?.value;
            const quantity = row.querySelector('.expense-quantity')?.value;
            const price = row.querySelector('.expense-price')?.value;
            const detail = row.querySelector('.expense-detail')?.value;

            if (!date) errors.push(`${rowNum}번 항목의 지출일자를 입력해주세요.`);
            if (!category) errors.push(`${rowNum}번 항목의 지출분류를 선택해주세요.`);
            if (!paymentMethod) errors.push(`${rowNum}번 항목의 결제수단을 선택해주세요.`);
            if (!quantity || quantity < 1) errors.push(`${rowNum}번 항목의 수량을 확인해주세요.`);
            if (!price || price < 0) errors.push(`${rowNum}번 항목의 단가를 확인해주세요.`);
            if (!detail) errors.push(`${rowNum}번 항목의 내용을 입력해주세요.`);
        });

        return {
            isValid: errors.length === 0,
            errors: errors
        };
    },

    // 미리보기 처리
    async handleExpensePreview() {
        const validation = this.validateExpenseForm();

        if (!validation.isValid) {
            Swal.fire({
                icon: 'warning',
                title: '입력 오류',
                html: validation.errors.join('<br>')
            });
            return;
        }

        // 지출 내역 데이터 수집
        const items = Array.from(this.elements.itemList.getElementsByTagName('tr'))
            .map(row => {
                const categorySelect = row.querySelector('.expense-category');
                const categoryOption = categorySelect ? categorySelect.options[categorySelect.selectedIndex] : null;

                return {
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

        const formData = {
            expenseName: document.getElementById('expenseName').value, // 제목
            expenseType: this.elements.expenseType.options[this.elements.expenseType.selectedIndex].text,
            expenseItems: items,
            totalAmount: this.elements.totalAmount.textContent,
            docContent: ApprovalEditor.getContent()
        };

        try {
            await ApprovalPreview.handlePreview(formData, 'e01');
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: '오류 발생',
                text: '미리보기 생성 중 오류가 발생했습니다.'
            });
        }
    },

    //초기화함수
    resetForm() {
        try {
            // 에디터 완전 초기화
            ApprovalEditor.destroy();
            ApprovalEditor.initialize('expenseDocContent', 'expenseEditor');

            // 기존 리스너 제거 방식 수정
            const addItemBtn = document.getElementById('addExpenseItem');
            if (addItemBtn) {
                // 모든 이벤트 리스너 제거
                const oldBtn = addItemBtn;
                const newBtn = oldBtn.cloneNode(true);
                oldBtn.parentNode.replaceChild(newBtn, oldBtn);

                // 새로운 버튼에 이벤트 리스너 직접 추가
                newBtn.addEventListener('click', () => {
                    if (!this.elements.expenseType.value) {
                        Swal.fire({
                            icon: 'warning',
                            title: '지출 구분 선택',
                            text: '항목을 추가하기 전에 지출 구분을 먼저 선택하세요.'
                        });
                        return;
                    }
                    this.elements.expenseType.disabled = true;
                    this.addExpenseItem();
                });
            }

            // 제목 초기화
            document.getElementById('expenseName').value = '';

            // 지출 구분 초기화
            if (this.elements.expenseType) {
                this.elements.expenseType.value = '';
                this.elements.expenseType.disabled = false;
            }

            // 지출 내역 테이블 초기화
            if (this.elements.itemList) {
                this.elements.itemList.innerHTML = '';
            }

            // 총계 초기화
            if (this.elements.totalAmount) {
                this.elements.totalAmount.textContent = '0원';
            }

            // 현재 항목 배열 초기화
            this.currentItems = [];

            // 지출 구분 옵션 다시 로드
            this.fetchExpenseTypes();

        } catch (error) {
            console.error('지출결의서 폼 초기화 중 오류 발생:', error);
            Swal.fire({
                icon: 'error',
                title: '초기화 오류',
                text: '지출결의서 초기화 중 오류가 발생했습니다.'
            });
        }
    },
};

// 지출결의서 (ApprovalExpense.js)
document.getElementById('expenseDataBtn')?.addEventListener('click', () => {
    // 제목 설정
    document.getElementById('expenseName').value = '1월 회식비 지출결의서';

    // 에디터 내용 설정
    ApprovalEditor.setContent('<p>1월 회식 비용에 대한 지출결의서입니다.</p><p>상세 내역은 아래와 같습니다.</p>');
});
