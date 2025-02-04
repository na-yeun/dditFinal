document.addEventListener("DOMContentLoaded", () => {
    fetchAllSalesData();             // 전년도, 올해 매출 데이터 가져오기
    fetchAllMiniCharts();            // 미니 차트 데이터 가져오기
});




// 모든 매출 데이터 로드
function fetchAllSalesData() {
    totayTotal();
    yearTotal();
}

// 모든 미니차트 데이터 로드
function fetchAllMiniCharts() {
    const statTypes = ["contType", "monthlyCont", "contScale", "contEmpcnt", "contStorage"];
    statTypes.forEach(statType => {
        fetch(`${contextPath}/prov/getStatData?statType=${statType}`)
            .then(resp => resp.json())
            .then(data => {
                console.log("응답 데이터 >>>", data);
                drawMiniChart(data, statType);  // ✅ 수정됨
            })
            .catch(error => console.error(`Error fetching ${statType}:`, error));
    });
}


// 전년도 매출 데이터 가져오기
function totayTotal(){
    fetch(`${contextPath}/prov/todayTotal`)
        .then(resp => resp.json())
        .then(data => {
            document.getElementById("todaySalesAmount").innerText = `${Number(data.payTotal).toLocaleString()} 원`;
        })
        .catch(error => {
            console.error("Error fetching todayTotal:", error);
        });
}

// 올해 매출 데이터 가져오기
function yearTotal(){
    fetch(`${contextPath}/prov/thisYearTotal`)
        .then(resp => resp.json())
        .then(data => {
            document.getElementById("yearSalesAmount").innerText = `${Number(data.payTotal).toLocaleString()} 원`;
        })
        .catch(error => {
            console.error("Error fetching thisYearTotal:", error);
        });
}


const colors = [
    'rgba(75, 192, 192, 0.7)',  // 청록색
    'rgba(255, 99, 132, 0.7)',  // 빨강
    'rgba(255, 206, 86, 0.7)',  // 노랑
    'rgba(54, 162, 235, 0.7)',  // 파랑
    'rgba(153, 102, 255, 0.7)', // 보라
    'rgba(255, 159, 64, 0.7)'   // 주황
];


function drawMiniChart(data, statType) {
    const canvas = document.getElementById(`${statType}Chart`);
    const ctx = canvas.getContext('2d');

    if (window[`${statType}ChartInstance`]) {
        window[`${statType}ChartInstance`].destroy();
    }

    let labels = [];
    let counts = [];
    let statTypeMappDate;
    // 데이터 매핑 (statType에 따른 속성 처리)
    switch (statType) {
        case "contType":
            labels = data.map(item => item.CONTRACT_TYPE);
            counts = data.map(item => item.CONTRACT_COUNT);
            statTypeMappDate = "업종"
            break;
        case "monthlyCont":
            labels = data.map(item => item.MONTH);
            counts = data.map(item => item.PAY_COUNT);
            statTypeMappDate = "월별 계약";
            break;
        case "contScale":
            labels = data.map(item => item.SCALE_SIZE);
            counts = data.map(item => item.SCALE_COUNT);
            statTypeMappDate = "업체 규모";
            break;
        case "contEmpcnt":
            labels = data.map(item => item.EMP_COUNT);
            counts = data.map(item => item.EMP_CNT);
            statTypeMappDate = "사용 인원";
            break;
        case "contStorage":
            labels = data.map(item => item.STORAGE_SIZE);
            counts = data.map(item => item.STORAGE_COUNT);
            statTypeMappDate = "스토리지 용량";
            break;
        default:
            console.warn("Unknown statType:", statType);
            labels = data.map(item => item.LABEL);
            counts = data.map(item => item.COUNT);
            break;
    }

    const chartOptions = {
        responsive: true,  // 반응형 활성화
        maintainAspectRatio: false,  // 비율 고정 해제
        scales: statType === "contType" || statType === "monthlyCont" ? {
            y: {
                beginAtZero: true,
                ticks : {
                    stepSize : 2
                }
            }
        } : {},
        plugins: {
            legend: {
                position: 'top'
            }
        },
        tooltip : {
            callbacks : {
                title : (tooltipItems) => {
                    return tooltipItems[0].label + " 계약";
                },
                label : (tooltipItem) => {
                    return `계약 수: ${tooltipItem.raw} 건`;  
                }
            }
        }
    };
    
    

    //  차트 렌더링
    window[`${statType}ChartInstance`] = new Chart(ctx, {
        type: statType === "monthlyCont" ? 'line' :
              statType === "contScale" || statType === "contStorage" ? 'doughnut' :
              statType === "contEmpcnt" ? 'pie' : 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: `${statTypeMappDate} 통계`,
                data: counts,
                backgroundColor: colors.slice(0, counts.length),
                borderColor: colors.slice(0, counts.length).map(color => color.replace('0.7', '1')),
                borderWidth: 1
            }]
        },
        options: chartOptions
    });
}
