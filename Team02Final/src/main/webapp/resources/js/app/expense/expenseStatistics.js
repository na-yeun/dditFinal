document.addEventListener('DOMContentLoaded', function () {
    // === 1. 전역 변수와 공통 설정 === //
    const contextPath = document.querySelector('#contextPath')?.value || '';
    const companyId = document.querySelector('#companyId')?.value || '';
    const charts = {};  // 요약 통계용 차트 객체 저장

    /**
     * 분석 차트 관련 상태 관리 객체
     * - currentView: 전체/부서별 보기 상태 ('total'/'department')
     * - currentType: 연도별/월별 보기 상태 ('year'/'month')
     * - selectedYear: 선택된 연도 (월별 보기시 사용)
     * - currentDepartment: 현재 선택된 부서명
     * - charts: 분석 관련 차트 객체들 저장
     */
    const analysisState = {
        currentView: 'total',
        currentType: 'year',
        selectedYear: new Date().getFullYear(),
        currentDepartment: null,
        charts: {
            main: null,
            detail: null
        }
    };

    // 차트 공통 옵션 설정
    const commonChartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            tooltip: {
                callbacks: {
                    label: function (context) {
                        return context.parsed.y.toLocaleString() + '원';
                    }
                }
            }
        }
    };

    // === 2. 유틸리티 함수들 === //

    /**
     * 에러 메시지를 사용자에게 표시하는 함수
     * @param {string} message - 표시할 에러 메시지
     */
    function showError(message) {
        Swal.fire({
            icon: 'error',
            title: '오류',
            text: message
        });
    }

    /**
     * 날짜를 YY.MM 형식으로 포맷팅하는 함수
     * @param {number} year - 연도 (YYYY 형식)
     * @param {number|string} month - 월 (1-12)
     * @returns {string} 포맷팅된 날짜 문자열 (예: '24.03')
     */
    function formatDate(year, month = null) {
        const shortYear = year.toString().slice(-2);  // 연도의 마지막 2자리만 사용
        if (month === null) {
            return shortYear;
        }
        const paddedMonth = month.toString().padStart(2, '0');
        return `${shortYear}.${paddedMonth}`;
    }

    /**
     * 연도 선택 UI 초기화 함수
     * @param {number} currentYear - 현재 연도
     */
    function setupYearOptions(currentYear) {
        const yearSelect = document.getElementById('yearSelect');
        yearSelect.innerHTML = '';
        for (let i = 0; i < 5; i++) {
            const year = currentYear - i;
            yearSelect.add(new Option(`${formatDate(year)}년`, year));
        }
        yearSelect.value = currentYear;  // 현재 연도를 기본값으로
    }

    /**
     * 분석용 연도 선택 UI 초기화 함수
     */
    function setupAnalysisYearSelect() {
        const yearSelect = document.getElementById('analysisYearSelect');
        const currentYear = new Date().getFullYear();

        yearSelect.innerHTML = '';
        // 최근 5년치 옵션 추가
        for (let i = 0; i < 5; i++) {
            const year = currentYear - i;
            yearSelect.add(new Option(`${formatDate(year)}년`, year));
        }
        yearSelect.value = currentYear;
        analysisState.selectedYear = currentYear;
    }

    /**
     * 월 선택 UI 초기화 함수
     * @param {number} currentMonth - 현재 월
     */
    function setupMonthOptions(currentMonth) {
        const monthSelect = document.getElementById('monthSelect');
        monthSelect.innerHTML = '';
        for (let i = 1; i <= 12; i++) {
            const month = i.toString().padStart(2, '0');
            monthSelect.add(new Option(`${month}월`, month));
        }
        monthSelect.value = currentMonth.toString().padStart(2, '0');  // 현재 월을 기본값으로
    }
    /**
     * 검색 필터 값을 가져오는 함수
     * @returns {Object} 연도, 월, 부서코드를 포함한 필터 객체
     */
    function getFilterValues() {
        return {
            year: document.getElementById('yearSelect').value,
            month: document.getElementById('monthSelect').value,
            departCode: document.getElementById('departSelect').value
        };
    }

    /**
     * 차트 색상을 순환하며 반환하는 함수
     * @param {number} index - 색상 배열의 인덱스
     * @returns {string} rgba 색상 문자열
     */
    function getChartColors(index) {
        const colors = [
            'rgba(75, 192, 192, 0.8)',
            'rgba(54, 162, 235, 0.8)',
            'rgba(153, 102, 255, 0.8)',
            'rgba(255, 99, 132, 0.8)',
            'rgba(255, 159, 64, 0.8)'
        ];
        return colors[index % colors.length];
    }

// === 3. API 호출 함수들 === //

    /**
     * 부서 목록을 불러오는 함수
     * @throws {Error} 부서 데이터 로드 실패시 발생
     */
    async function loadDepartments() {
        try {
            const response = await fetch(`${contextPath}/${companyId}/expense/stats/codes/department`);
            if (!response.ok) throw new Error('부서 데이터 로드 실패');

            const departments = await response.json();
            const select = document.getElementById('departSelect');
            select.innerHTML = '<option value="">전체 부서</option>';
            departments.forEach(dept => {
                select.add(new Option(dept.DEPART_NAME, dept.DEPART_CODE));
            });
        } catch (error) {
            console.error('부서 데이터 로드 중 오류:', error);
            throw error;
        }
    }

    /**
     * 요약 통계 데이터를 불러오는 함수
     * - 월별 비교와 부서별 지출 현황 데이터를 함께 로드
     */
    async function loadSummaryData() {
        try {
            const response = await fetch(`${contextPath}/${companyId}/expense/stats/summary`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(getFilterValues())
            });

            if (!response.ok) throw new Error('요약 데이터 로드 실패');
            const data = await response.json();

            updateMonthlyComparisonChart(data.monthlyComparison);
            updateDepartmentExpenseChart(data.departmentStats);
        } catch (error) {
            console.error('요약 데이터 로드 중 오류:', error);
            showError('데이터를 불러오는 중 오류가 발생했습니다.');
        }
    }

    /**
     * 분석 통계 데이터를 불러오는 함수
     * @returns {Promise<Object|null>} 분석 데이터 또는 실패시 null
     */
    async function loadExpenseAnalysisData() {
        try {
            const params = {
                type: analysisState.currentType,
                year: analysisState.selectedYear,
                viewType: analysisState.currentView
            };

            console.log('Analysis Request Params:', params);  // 디버깅용

            const response = await fetch(`${contextPath}/${companyId}/expense/stats/analysis`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(params)
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Server Error Response:', errorText);
                throw new Error('분석 데이터 로드 실패');
            }

            return await response.json();
        } catch (error) {
            console.error('분석 데이터 로드 중 오류:', error);
            showError('분석 데이터를 불러오는 중 오류가 발생했습니다.');
            return null;
        }
    }

    /**
     * 연도별 카테고리 상세 데이터를 불러오는 함수
     * @param {number} year - 조회할 연도
     * @param {string|null} departName - 부서명 (부서별 조회시)
     */
    async function loadYearCategoryDetail(year, departName = null) {
        try {
            console.log("[DEBUG] 전달받은 year 값:", year); // 디버깅 로그 추가
            console.log("[DEBUG] yearSelect 값:", document.getElementById('yearSelect').value); // 디버깅 로그 추가

            const params = {
                year: year,
                type: 'year',
                departName: departName
            };

            console.log("API 요청 시작 - 연도별 카테고리 상세");
            console.log("요청 파라미터:", params);

            const response = await fetch(`${contextPath}/${companyId}/expense/stats/detail`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(params)
            });

            console.log("API 응답 상태:", response.status);

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Server error response:', errorText);
                throw new Error('연간 카테고리 상세 데이터 로드 실패');
            }

            const data = await response.json();
            console.log("받아온 데이터:", data);
            return data;
        } catch (error) {
            console.error('연간 카테고리 상세 데이터 로드 중 오류:', error);
            showError('카테고리 상세 데이터를 불러오는 중 오류가 발생했습니다.');
            return null;
        }
    }

    /**
     * 월별 카테고리 상세 데이터를 불러오는 함수
     * @param {number} year - 조회할 연도
     * @param {number} month - 조회할 월
     */
    async function loadMonthCategoryDetail(year, month, departName) {
        try {
            const params = {
                year: year || null,  // 전체 연도인 경우 null 전달
                month: month,
                type: 'month',
                viewType: analysisState.currentView,
                departName: departName
            };

            console.log('Month Category Detail Request params:', params);

            const response = await fetch(`${contextPath}/${companyId}/expense/stats/detail`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(params)
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Server error response:', errorText);
                throw new Error('카테고리 상세 데이터 로드 실패');
            }

            return await response.json();
        } catch (error) {
            console.error('카테고리 상세 데이터 로드 중 오류:', error);
            showError('카테고리 상세 데이터를 불러오는 중 오류가 발생했습니다.');
            return null;
        }
    }

// === 4. 차트 렌더링 함수들 === //

    /**
     * 월별 비교 차트 업데이트 함수
     * @param {Object} data - 전월/당월 비교 데이터
     */
    function updateMonthlyComparisonChart(data) {
        const ctx = document.getElementById('monthlyComparisonChart').getContext('2d');

        if (charts.monthlyComparison) {
            charts.monthlyComparison.destroy();
        }

        charts.monthlyComparison = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['전월', '당월'],
                datasets: [{
                    label: '지출액',
                    data: [data.PREV_AMOUNT, data.CURRENT_AMOUNT],
                    backgroundColor: ['rgba(75, 192, 192, 0.5)', 'rgba(75, 192, 192, 0.8)'],
                    borderColor: 'rgb(75, 192, 192)',
                    borderWidth: 1
                }]
            },
            options: {
                ...commonChartOptions,
                plugins: {
                    tooltip: {
                        callbacks: {
                            afterLabel: function (context) {
                                if (context.dataIndex === 1) {
                                    return `전월대비: ${data.GROWTH_RATE}%`;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 부서별 지출 차트 업데이트 함수
     * @param {Array} departmentStats - 부서별 지출 통계 데이터
     */
    function updateDepartmentExpenseChart(departmentStats) {
        const ctx = document.getElementById('departmentExpenseChart').getContext('2d');

        if (charts.departmentExpense) {
            charts.departmentExpense.destroy();
        }

        const sortedStats = [...departmentStats].sort((a, b) => b.TOTAL_AMOUNT - a.TOTAL_AMOUNT);

        charts.departmentExpense = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: sortedStats.map(item => item.DEPART_NAME),
                datasets: [{
                    label: '부서별 지출액',
                    data: sortedStats.map(item => item.TOTAL_AMOUNT),
                    backgroundColor: 'rgba(75, 192, 192, 0.5)',
                    borderColor: 'rgb(75, 192, 192)',
                    borderWidth: 1
                }]
            },
            options: {
                ...commonChartOptions,
                onClick: async (event, elements) => {
                    if (elements.length > 0) {
                        const idx = elements[0].index;
                        const departName = sortedStats[idx].DEPART_NAME;

                        let selectedYear = document.getElementById('yearSelect').value;
                        let selectedMonth = document.getElementById('monthSelect').value;

                        if (selectedMonth) {
                            const detailData = await loadMonthCategoryDetail(
                                selectedYear,  // year가 없으면 null이 전달됨
                                selectedMonth,
                                departName
                            );
                            if (detailData) {
                                renderCategoryPieChart(detailData,
                                    `${selectedYear ? selectedYear + '년' : '전체 연도'} ${selectedMonth}월 ${departName} 지출 분류 현황`);
                            }
                        } else {
                            const detailData = await loadYearCategoryDetail(
                                selectedYear,
                                departName
                            );
                            if (detailData) {
                                renderCategoryPieChart(detailData,
                                    `${selectedYear ? selectedYear + '년' : '전체 연도'} ${departName} 지출 분류 현황`);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 분석 차트 렌더링 함수
     * @param {Array} data - 분석 데이터
     */
    function renderExpenseAnalysisChart(data) {
        const ctx = document.getElementById('analysisChart').getContext('2d');

        if (analysisState.charts.main) {
            analysisState.charts.main.destroy();
        }

        // 데이터 정렬
        const sortedData = [...data].sort((a, b) => {
            if (analysisState.currentType === 'year') {
                return a.YEAR - b.YEAR;
            } else {
                return parseInt(a.MONTH) - parseInt(b.MONTH);
            }
        });

        // 차트 데이터 준비
        let chartData = {
            labels: [],
            datasets: []
        };

        if (analysisState.currentView === 'total') {
            // 전체 보기일 때의 데이터 구조
            chartData = {
                labels: sortedData.map(item => {
                    if (analysisState.currentType === 'year') {
                        return `${formatDate(item.YEAR)}년`;
                    } else {
                        return formatDate(analysisState.selectedYear, item.MONTH);
                    }
                }),
                datasets: [{
                    label: '총 지출액',
                    data: sortedData.map(item => item.AMOUNT),
                    backgroundColor: 'rgba(75, 192, 192, 0.5)',
                    borderColor: 'rgb(75, 192, 192)',
                    borderWidth: 1
                }]
            };
        } else {
            // 부서별 보기일 때의 데이터 구조
            const departments = [...new Set(sortedData.map(item => item.DEPART_NAME))];
            const periods = [...new Set(sortedData.map(item =>
                analysisState.currentType === 'year' ? item.YEAR : item.MONTH))].sort((a, b) => a - b);

            chartData = {
                labels: periods.map(period =>
                    analysisState.currentType === 'year' ?
                        `${formatDate(period)}년` :
                        formatDate(analysisState.selectedYear, period)
                ),
                datasets: departments.map((dept, index) => ({
                    label: dept,
                    departName: dept,
                    data: periods.map(period => {
                        const match = sortedData.find(item =>
                            item.DEPART_NAME === dept &&
                            (analysisState.currentType === 'year' ?
                                item.YEAR === period : item.MONTH === period)
                        );
                        return match ? match.AMOUNT : 0;
                    }),
                    backgroundColor: getChartColors(index),
                    borderColor: getChartColors(index).replace('0.8', '1'),
                    borderWidth: 1
                }))
            };
        }

        // 차트 옵션 설정
        const options = {
            ...commonChartOptions,
            onClick: async (event, elements) => {
                if (elements.length > 0) {
                    const index = elements[0].index;
                    const datasetIndex = elements[0].datasetIndex;
                    const label = chartData.labels[index];
                    let period;

                    if (analysisState.currentType === 'year') {
                        // 연도 클릭시 ('YY년' 형식에서 연도만 추출하고 20XX 형식으로 변환)
                        const shortYear = parseInt(label.replace('년', ''));
                        period = 2000 + shortYear;  // 20XX 형식으로 변환
                    } else {
                        // 월 클릭시 ('YY.MM' 형식에서 월만 추출)
                        period = parseInt(label.split('.')[1]);
                    }

                    if (analysisState.currentType === 'year') {
                        // 연도 클릭시
                        const departName = analysisState.currentView === 'department' ?
                            chartData.datasets[datasetIndex].departName : null;

                        const detailData = await loadYearCategoryDetail(period, departName);
                        if (detailData) {
                            renderCategoryPieChart(detailData,
                                `${period}년 ${departName || '전체'} 지출 분류`)
                        }
                    } else {
                        // 월 클릭시
                        const departName = analysisState.currentView === 'department' ?
                            chartData.datasets[datasetIndex].departName : null;

                        const detailData = await loadMonthCategoryDetail(
                            analysisState.selectedYear,
                            period,
                            departName
                        );
                        if (detailData) {
                            renderCategoryPieChart(detailData,
                                `${formatDate(analysisState.selectedYear, period)} ${departName || '전체'} 지출 분류`);
                        }
                    }
                }
            }
        };

        // 차트 생성
        analysisState.charts.main = new Chart(ctx, {
            type: analysisState.currentType === 'year' ? 'bar' : 'line',
            data: chartData,
            options: options
        });
    }

    /**
     * 카테고리 파이 차트 렌더링 함수
     * @param {Object} data - 카테고리 상세 데이터
     * @param {string} title - 차트 제목
     */
    function renderCategoryPieChart(data, title) {

        console.log("renderCategoryPieChart 시작");
        console.log("데이터:", data);
        console.log("제목:", title);


        // 데이터 유효성 검사
        if (!data.categoryStats || data.categoryStats.length === 0) {
            console.log("카테고리 데이터가 없습니다");
            showError('해당 기간에 지출 분류 데이터가 없습니다.');

            // 상세 영역 숨기기
            const isAnalysisTab = document.querySelector('#analysis').classList.contains('active');
            const detailArea = document.getElementById(isAnalysisTab ? 'analysisDetail' : 'detailArea');
            detailArea.style.display = 'none';
            return;  // 함수 종료
        }

        // 현재 탭에 따라 다른 영역을 표시
        const isAnalysisTab = document.querySelector('#analysis').classList.contains('active');
        const detailArea = document.getElementById(isAnalysisTab ? 'analysisDetail' : 'detailArea');

        // collapse 클래스를 사용하는 경우에 대한 처리
        if (detailArea.classList.contains('collapse')) {
            detailArea.classList.add('show');
        }
        detailArea.style.display = 'block';

        // 현재 탭에 맞는 요소 ID 선택
        const titleElement = document.getElementById(isAnalysisTab ? 'analysisDetailTitle' : 'detailTitle');
        const chartElement = document.getElementById(isAnalysisTab ? 'analysisPieChart' : 'categoryPieChart');
        const tableBody = document.querySelector(isAnalysisTab ? '#analysisDetailTable tbody' : '#detailTable tbody');
        // 제목 업데이트
        titleElement.textContent = title;

        // 파이 차트 업데이트
        const ctx = chartElement.getContext('2d');

        // 이전 차트 제거
        if (analysisState.charts.detail) {
            analysisState.charts.detail.destroy();
        }

        const categoryStats = data.categoryStats;
        analysisState.charts.detail = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: categoryStats.map(item => item.CATEGORY_NAME),
                datasets: [{
                    data: categoryStats.map(item => item.AMOUNT),
                    backgroundColor: categoryStats.map((_, index) => getChartColors(index))
                }]
            },
            options: {
                ...commonChartOptions,
                plugins: {
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                const value = context.raw.toLocaleString() + '원';
                                const ratio = categoryStats[context.dataIndex].RATIO;
                                return `${context.label}: ${value} (${ratio}%)`;
                            }
                        }
                    }
                }
            }
        });

        // 테이블 업데이트
        tableBody.innerHTML = categoryStats.map(item => `
        <tr>
            <td>${item.CATEGORY_NAME}</td>
            <td class="text-end">${item.AMOUNT.toLocaleString()}원</td>
            <td class="text-end">${item.RATIO}%</td>
            <td class="text-end">${item.COUNT}</td>
        </tr>
    `).join('');
    }

    // === 5. 이벤트 리스너 등록 === //

    /**
     * 검색 버튼 클릭 이벤트 리스너
     * - 요약 통계 데이터 새로고침
     */
    document.getElementById('search-btn').addEventListener('click', () => {
        loadSummaryData();
    });

    /**
     * 분석 뷰 타입(전체/부서별) 변경 이벤트 리스너
     * - 선택된 뷰 타입에 따라 차트 데이터 갱신
     */
    document.querySelectorAll('input[name="analysisViewType"]').forEach(radio => {
        radio.addEventListener('change', async (e) => {
            analysisState.currentView = e.target.value;
            // 뷰 타입 변경시 부서 정보 초기화
            analysisState.currentDepartment = null;
            const data = await loadExpenseAnalysisData();
            if (data) renderExpenseAnalysisChart(data);
        });
    });

    /**
     * 분석 유형(연도별/월별) 변경 이벤트 리스너
     * - 선택된 분석 유형에 따라 UI와 데이터 갱신
     */
    document.getElementById('analysisType').addEventListener('change', async function () {
        analysisState.currentType = this.value;
        const yearSelect = document.getElementById('analysisYearSelect');
        const backButton = document.getElementById('backToYearlyBtn');

        if (this.value === 'month') {
            // 월별 선택시 연도 선택 UI 표시
            yearSelect.style.display = 'inline-block';
            backButton.style.display = 'inline-block';

            // 현재 연도를 기본값으로 설정
            if (!analysisState.selectedYear) {
                analysisState.selectedYear = new Date().getFullYear();
                yearSelect.value = analysisState.selectedYear;
            }

            if (!yearSelect.options.length) {
                setupAnalysisYearSelect();
            }
        } else {
            // 연도별 선택시 연도 선택 UI 숨김
            yearSelect.style.display = 'none';
            backButton.style.display = 'none';
            analysisState.selectedYear = null;
        }

        const data = await loadExpenseAnalysisData();
        if (data) renderExpenseAnalysisChart(data);
    });

    /**
     * 연도 선택 변경 이벤트 리스너
     * - 선택된 연도로 데이터 갱신
     */
    document.getElementById('analysisYearSelect').addEventListener('change', async function () {
        analysisState.selectedYear = parseInt(this.value);
        const data = await loadExpenseAnalysisData();
        if (data) renderExpenseAnalysisChart(data);
    });

    /**
     * 뒤로가기 버튼 클릭 이벤트 리스너
     * - 연도별 보기로 돌아가기
     */
    document.getElementById('backToYearlyBtn').addEventListener('click', async () => {
        analysisState.currentType = 'year';
        analysisState.selectedYear = null;
        document.getElementById('backToYearlyBtn').style.display = 'none';
        const data = await loadExpenseAnalysisData();
        if (data) renderExpenseAnalysisChart(data);
    });

    // === 6. 페이지 초기화 === //

    /**
     * 페이지 초기화 함수
     * - 날짜 선택기 초기화
     * - 부서 목록 로드
     * - 요약 통계 로드
     * - 분석 차트 초기 데이터 로드
     */
    async function initializePage() {
        try {
            const today = new Date();
            const currentYear = today.getFullYear();
            const currentMonth = today.getMonth() + 1;

            // UI 초기화
            setupYearOptions(currentYear);
            setupMonthOptions(currentMonth);
            setupAnalysisYearSelect();

            // 데이터 로드
            await loadDepartments();
            await loadSummaryData();

            // 분석 차트 초기 상태 설정
            analysisState.currentType = 'year';
            analysisState.currentView = 'total';
            analysisState.selectedYear = null;

            // 분석 차트 초기 데이터 로드
            const data = await loadExpenseAnalysisData();
            if (data) renderExpenseAnalysisChart(data);
        } catch (error) {
            console.error('초기화 중 오류:', error);
            showError('페이지 초기화 중 오류가 발생했습니다.');
        }
    }

    // 페이지 초기화 실행
    initializePage();
});