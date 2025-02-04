let barChartInstance = null;
let rejectionChartInstance = null;

document.addEventListener("DOMContentLoaded", () => {
    fetchContractTypeData();

    document.getElementById('selectYear').addEventListener('change', () => {
        toggleCollapse('rejectionChartContainer', false);  // ✅ 변경될 때 차트 숨김
        fetchContractTypeData();
    });

    document.getElementById('selectMonth').addEventListener('change', () => {
        toggleCollapse('rejectionChartContainer', false);  // ✅ 변경될 때 차트 숨김
        fetchContractTypeData();
    });

    
    // class collapse 부분을 처음에는 숨기기. 
    toggleCollapse('rejectionChartContainer', false);
});

async function fetchContractTypeData() {
    try {
        // 연월 가져와서 contractStart 만들기 (파라미터로 보내야댐)
        const year = document.getElementById('selectYear')?.value || '';
        const month = document.getElementById('selectMonth')?.value || '';
        const contractStart = year + month;

        const response = await fetch(`${contextPath}/prov/getContTypeStat?contractStart=${contractStart}`);
        const data = await response.json();

        if (data.length === 0) {
            Swal.fire({
                title : "계약 건수 없음",
                text : "해당 월은 계약건수가 없습니다",
                icon : "error"
            })
            toggleCollapse('statsChart', false);  // 데이터가 없을 때 숨김 처리
            return;  // 실행 중지
        }
        toggleCollapse('statsChart', true);
        // 데이터를 바 차트로 렌더링 
        renderBarChart(data);
    } catch (error) {
        console.error('데이터를 불러오는데 실패했습니다.', error);
    }
}

// 계약 유형 바 차트로 렌더링 
function renderBarChart(data) {
    const canvas = document.getElementById("contractTypeChart");
    const ctx = canvas.getContext('2d');

    // 캔버스 크기 설정 (근데 약간 지 멋대로인거같음.)
    canvas.width = 400;
    canvas.height = 650;

    // 기존 차트 있으면 제거
    if (barChartInstance) {
        barChartInstance.destroy();
    }

    // 데이터 변환 및 매핑 
    const labels = data.map(item => item.CONTRACT_TYPE || item.contractType);
    const counts = data.map(item => item.CONTRACT_COUNT ?? item.contractCount);

    barChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: '계약 건수',
                data: counts,
                backgroundColor: 'rgba(54, 162, 235, 0.7)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 2,   // 1씩 증가
                        precision: 0   // 소수점 제거 
                    }
                }
            }, 
            // 하나의 바 차트 클릭시 이벤트  
            onClick: (event, elements) => {
                if (elements.length > 0) {
                    const index = elements[0].index;
                    const selectedContractType = labels[index];
                    // 데이터 재요청 및 class collapse true로 설정해서 보이게 하기 
                    const year = document.getElementById('selectYear')?.value || '';
                    const month = document.getElementById('selectMonth')?.value || '';
                    const contractStart = year + month;
                    
                    toggleCollapse('rejectionChartContainer', true);
                    fetchAndRenderStats(selectedContractType, contractStart);                
                }
            }
        }
    });
}

// 세부 데이터 가져오기. (차트 , 테이블 렌더링 )
async function fetchAndRenderStats(contractType, contractStart) {
    try {
        const response = await fetch(`${contextPath}/prov/getContTypeCollapse?contractType=${contractType}&contractStart=${contractStart}`);
        const data = await response.json();

        if (
            data.scale.length === 0 &&
            data.storage.length === 0 &&
            data.empCount.length === 0

        ) {
            Swal.fire({
                title : "계약 건수 없음",
                text : "해당 월은 계약건수가 없습니다",
                icon : "error"
            })
            toggleCollapse('statsChart', false);  // 데이터가 없을 때 숨김 처리
            return;  // 실행 중지
        }
        toggleCollapse('statsChart', true);
        renderGroupedBarChart(data);
        renderTable(data.scale, 'scaleTable');
        renderTable(data.storage, 'storageTable');
        renderTable(data.empCount, 'empCountTable');
    } catch (error) {
        console.error("업종 데이터를 가져오는데 실패했습니다:", error);
    }
}

// 그룹화된 바 차트 생성
function renderGroupedBarChart(data) {
    const ctx = document.getElementById('statsChart').getContext('2d');

    // 기존 차트 제거
    if (rejectionChartInstance) {
        rejectionChartInstance.destroy();
    }

    // 데이터 매핑 
    const scaleLabels = data.scale.map(item => item.LABEL);
    const scaleCounts = data.scale.map(item => Number(item.COUNT) || 0);

    const empLabels = data.empCount.map(item => item.LABEL);
    const empCounts = data.empCount.map(item => Number(item.COUNT) || 0);

    const storageLabels = data.storage.map(item => item.LABEL);
    const storageCounts = data.storage.map(item => Number(item.COUNT) || 0);

    

    // 라벨 및 데이터가 일치하도록 병합
    const labels = [...new Set([...scaleLabels, ...empLabels, ...storageLabels])];
    
    // 데이터 매핑 보장 및 시각화
    rejectionChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: '사용 인원',
                    data: labels.map(label => empLabels.includes(label) ? empCounts[empLabels.indexOf(label)] : 0),
                    backgroundColor: 'rgba(54, 162, 235, 0.7)'
                },
                {
                    label: '용량',
                    data: labels.map(label => storageLabels.includes(label) ? storageCounts[storageLabels.indexOf(label)] : 0),
                    backgroundColor: 'rgba(255, 206, 86, 0.7)'
                },
                {
                    label: '업체 규모',
                    data: labels.map(label => scaleLabels.includes(label) ? scaleCounts[scaleLabels.indexOf(label)] : 0),
                    backgroundColor: 'rgba(75, 192, 192, 0.7)'
                }
            ]
        },
        options: {
            indexAxis: 'y',   // 가로 바 차트로 생성하기 위해 사용 
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                tooltip: {  
                    callbacks: {
                        title: (tooltipItems) => {
                            return tooltipItems[0].label;  // 툴팁의 타이틀을 라벨로
                        },
                        label: (tooltipItem) => {
                            let datasetLabel = tooltipItem.dataset.label || '';
                            let value = tooltipItem.raw;
                            return `계약건수: ${value} 건`;
                        }
                    },
                    backgroundColor: 'rgba(0, 0, 0, 0.8)', // 툴팁 배경색
                    titleFont: {
                        size: 14
                    },
                    bodyFont: {
                        size: 12
                    },
                    bodyColor: '#ffffff',  // 텍스트 색상
                    borderColor: '#ffffff',
                    borderWidth: 1
                }
            },
            scales: {
                x: {  // 카운트 표시 x축방향
                    beginAtZero: true,
                    ticks: {
                        stepSize: 2,
                        precision: 0
                    }
                }
            }
        }
    });
}



// 테이블 데이터 렌더링
function renderTable(data, tableId) {
    const tbody = document.querySelector(`#${tableId} tbody`);
    tbody.innerHTML = '';

    data.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${item.LABEL}</td>
            <td>${item.COUNT}</td>
        `;
        tbody.appendChild(row);
    });
}


// 특정 요소 토글 
function toggleCollapse(elementId, show) {
    const element = document.getElementById(elementId);
    if (show) {
        element.classList.add('show');
        element.style.display = 'flex';  
    } else {
        element.classList.remove('show');
        element.style.display = 'none';

        // 차트가 존재할 경우 파괴 (차트 초기화 방지)
        if (rejectionChartInstance) {
            rejectionChartInstance.destroy();
            rejectionChartInstance = null;
        }
    }
}

