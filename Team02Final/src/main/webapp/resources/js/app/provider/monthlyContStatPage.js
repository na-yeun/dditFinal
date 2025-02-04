let lineChartInstance = null;
let rejectionChartInstance = null;


document.addEventListener("DOMContentLoaded" , () => {
    // class collapse 부분을 처음에는 숨기기. 
    toggleCollapse('rejectionChartContainer', false);

    fetchContractTypeData();

    document.getElementById('selectYear').addEventListener('change', () => {
        toggleCollapse('rejectionChartContainer', false);  // ✅ 변경될 때 차트 숨김
        fetchContractTypeData();
    });

    document.getElementById('selectMonth').addEventListener('change', () => {
        toggleCollapse('rejectionChartContainer', false);  // ✅ 변경될 때 차트 숨김
        fetchContractTypeData();
    });

    


}); // DOM end 


async function fetchContractTypeData() {
    try {
        // 연월 가져와서 contractStart 만들기 (파라미터로 보내야댐)
        const year = document.getElementById('selectYear')?.value || '';
        const month = document.getElementById('selectMonth')?.value || '';
        const contractStart = year + month;

        const response = await fetch(`${contextPath}/prov/getMonthlyContStat?contractStart=${contractStart}`);
        const data = await response.json();
        console.log("초기 라인차트 응답데이터 >>" , data);

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
        renderlineChart(data);
    } catch (error) {
        console.error('데이터를 불러오는데 실패했습니다.', error);
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

    const contractTypeLabels = data.contractType.map(item => item.LABEL);
    const contractTypeCounts = data.contractType.map(item => Number(item.COUNT));

    

    // 라벨 및 데이터가 일치하도록 병합
    const labels = [...new Set([...scaleLabels, ...contractTypeLabels])];
    
    // 데이터 매핑 보장 및 시각화
    rejectionChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                
                {
                    label: '업종',
                    data: labels.map(label => contractTypeLabels.includes(label) ? contractTypeCounts[contractTypeLabels.indexOf(label)] : 0),
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
                        stepSize: 1,
                        precision: 0
                    }
                }
            }
        }
    });
}


function renderlineChart(data) {
    const canvas = document.getElementById("monthlyContractChart");
    const ctx = canvas.getContext('2d');

    canvas.width = 400;
    canvas.height = 650;

    // 기존 차트 제거
    if (lineChartInstance) {
        lineChartInstance.destroy();
    }

    // ✅ 연월 데이터 사용 (contractType 대신)
    const labels = data.map(item => item.MONTH);  // '2024-01' 같은 월 정보
    const counts = data.map(item => item.PAY_COUNT);

    lineChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: '월별 계약 건수',
                data: counts,
                fill: true,
                backgroundColor: 'rgba(54, 162, 235, 0.2)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 2,
                pointRadius: 5,
                pointBackgroundColor: 'rgba(54, 162, 235, 1)'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1,
                        precision: 0
                    }
                }
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: (tooltipItem) => `계약 건수: ${tooltipItem.raw}건`
                    }
                }
            },
            // ✅ 클릭 시 연월 데이터를 파라미터로 전송
            onClick: (event, elements) => {
                if (elements.length > 0) {
                    const index = elements[0].index;
                    const selectedMonth = labels[index]; // ✅ 연월 데이터 선택
                    console.log("선택한 연월:", selectedMonth);
                    
                   

                    // ✅ 선택한 연월을 contractStart로 전송
                    fetchAndRenderStats(selectedMonth);                
                }
            }
        }
    });
}



// ✅ 세부 데이터 가져오기 및 차트, 테이블 렌더링 
async function fetchAndRenderStats(month) {
    try {

        const year = document.getElementById('selectYear')?.value || '';
        const monthVal = document.getElementById('selectMonth')?.value || '';
        const contractStart = year + monthVal;
        // const dateValue = document.querySelector("#dateValue");

        // dateValue.innerHTML = '';

        const response = await fetch(`${contextPath}/prov/getMonthlyContCollapse?month=${month}&contractStart=${contractStart}`);
        const data = await response.json();
        console.log("라인차트 클릭시 세부 응답데이터 >>" , data);
        // ✅ 데이터 없을 경우 처리
        
        const isEmptyData = !data.scale.length && data.contractType.length;

        if (isEmptyData) {
            Swal.fire({
                title: "계약 건수 없음",
                text: `${month}에 계약건수가 없습니다.`,
                icon: "warning"
            });
            toggleCollapse('rejectionChartContainer', false);  // ✅ 데이터 없을 때 숨기기
            return; 
        }
        
        // dateValue.innerHTML = `<strong>${month} 통계</strong>`

        // ✅ 데이터가 있을 경우 차트와 테이블 표시
        toggleCollapse('rejectionChartContainer', true);

        // ✅ 차트 데이터 렌더링 (가로 바 차트)
        renderGroupedBarChart(data);

        // ✅ 테이블 데이터 렌더링 (JSP에 맞춤)
        renderTable(data.scale, 'scaleTable');           
        renderTable(data.contractType, 'contractTypeTable');      
               

    } catch (error) {
        console.error("업종 데이터를 가져오는데 실패했습니다:", error);
        Swal.fire({
            title: "데이터 오류",
            text: "데이터를 불러오는 도중 문제가 발생했습니다.",
            icon: "error"
        });
        toggleCollapse('rejectionChartContainer', false);
    }
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