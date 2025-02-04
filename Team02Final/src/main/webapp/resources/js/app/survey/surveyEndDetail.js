/**
 * 
 */
async function drawChart(sboardNo, type) {
	let resultUrl = `/work2gether/${companyId}/survey/${sboardNo}/result`;

	let resultResp = await fetch(resultUrl, {
		method: 'get',
		header: {
			'Content-Type': 'application/json'
		}
	});

	if (resultResp.ok) {
		let jsonObj = await resultResp.json();


		let countData = jsonObj['countData'];
		const totalParticipants = countData['totalCount'];
		const actualParticipants = countData['participatedCount'];
		const percentage = ((actualParticipants / totalParticipants) * 100).toFixed(1);

		const centerTextPlugin = {
			id: 'centerText',
			beforeDraw(chart) {
				const { width } = chart;
				const { height } = chart;
				const ctx = chart.ctx;
				ctx.restore();

				// 텍스트 스타일 설정
				const fontSize = (height / 10).toFixed(2);
				ctx.font = `bold ${fontSize}px sans-serif`;
				ctx.textBaseline = 'middle';
				ctx.textAlign = 'center';
				ctx.fillStyle = 'black';

				// 중앙에 퍼센트 값 표시
				const text = `${percentage}%`;
				const textX = width / 2;
				const textY = height / 1.5;
				ctx.fillText(text, textX, textY);

				ctx.save();
			}
		};

		// 참여도 차트 생성
		const totalChart = document.querySelector('#participation');
		new Chart(totalChart, {
			type: 'doughnut',
			data: {
				labels: ['참여', '불참'],
				datasets: [{
					data: [actualParticipants, totalParticipants - actualParticipants], // 참여와 미참여 데이터
					backgroundColor: ['#00bfff', '#e0e0e0'], // 색상
					borderWidth: 0
				}]
			},
			options: {
				rotation: -90,
				circumference: 180,
				cutout: '80%',
				plugins: {
					legend: {
						display: true, // 범례 활성화
						position: 'top'
					},
				}
			},
			plugins: [centerTextPlugin]
		});


		for (let squesNo in jsonObj) {
			if (squesNo != 'countData') {
				let squesNoId = `sub_${squesNo}`
				let squesNoArea = document.querySelector(`#${squesNoId}`);
				
				if (squesNoArea) {
					let itemValues = jsonObj[squesNo];
					const keys = Object.keys(itemValues);

					keys.forEach((key) => {
						const totalResult = itemValues[key].totalResult;
						let code = `<ul>`;
						for (let i = 0; i < totalResult.length; i++) {
							code += "<li>" + totalResult[i] + "</li>";
						}

						code += `</ul>`;
						squesNoArea.innerHTML = code;
					})

				} else {
					// labels와 data 배열 초기화
					let labels = [];
					let data = [];
					let itemValues = jsonObj[squesNo];

					for (let itemNo in itemValues) {
						let itemDetails = itemValues[itemNo]; // 각 항목의 데이터 객체
						let suritemContent = itemDetails.suritemContent; // 항목 이름
						let totalResult = itemDetails.totalResult; // 항목의 결과 값

						// labels와 data에 추가
						labels.push(suritemContent); // 항목 이름 추가
						data.push(totalResult); // 결과 값 추가
					}

					// 차트를 그릴 canvas 선택
					let mychart = document.querySelector(`#mychart_${squesNo}`);

					// 차트 생성
					if (mychart) {
						if (type == 'line') {

						}
						new Chart(mychart, {
							type: type,
							data: {
								labels: labels, // 항목의 내용 (suritemContent)
								datasets: [{
									data: data, // totalResult 값을 데이터로 사용
									borderWidth: 1,
									label: '응답'
								}]
							},
							options: {
								scales: {
									y: {
										beginAtZero: true
									}
								}
							}
						});
					}
				}
			}
		}
	} else {
		console.error();
	} // else 
}

document.addEventListener("DOMContentLoaded", async () => {

	let mytable = document.querySelector('#main-table')
	let sboardNo = mytable.dataset.sboardNo;
	const beforeBtn = document.querySelector('#before-btn');
	
	// 목록 버튼 클릭시
	beforeBtn.addEventListener("click", () => {
		location.href=`${contextPath}/${companyId}/survey`
	});

	// 화면 로딩시 Chart 새로 그리는 함수
	drawChart(sboardNo, "doughnut");


	// 차트 모양 버튼 클릭시
	const chartTypeBtns = document.querySelectorAll('.chart-type');
	chartTypeBtns.forEach(element => {
		element.addEventListener("click", (e) => {
			// 기존 차트 전체 삭제
			let mychartArea = document.querySelectorAll('.chart-area');
			mychartArea.forEach(chartOne => Chart.getChart(chartOne).destroy());
			let sboardNo = mytable.dataset.sboardNo;
			let targetValue = e.target.id;
			drawChart(sboardNo, targetValue);
		});
	});

	// 삭제 버튼 클릭시 삭제
	let delBtn = document.querySelector('#delete-btn');
	if(delBtn){
		delBtn.addEventListener("click", () => {
			Swal.fire({
				title: "삭제",
				html: "정말로 삭제하시겠습니까?<br>삭제한 게시글은 다시 복원할 수 없습니다.",
				icon: "warning",
				showCancelButton: true,
				// confirm 버튼 텍스트
				confirmButtonText: '확인',
				cancelButtonText: '취소',
				// confirm 버튼 색 지정(승인)
				confirmButtonColor: 'blue',
				cancelButtonColor: 'red',
				// resolve 함수 생각하면 good...
			}).then(async (result) => {
				if (result.isConfirmed) {
					// 삭제 요청(비동기)
	
					let delUrl = `/work2gether/${companyId}/survey/manage/${sboardNo}`;
					let resp = await fetch(delUrl, {
						method: 'DELETE'
					});
					if (resp.status == 200) {
						Swal.fire({
							title: "삭제 완료",
							text: "삭제가 완료되었습니다.",
							icon: "success",
							// confirm 버튼 텍스트
							confirmButtonText: '확인',
							// confirm 버튼 색 지정(승인)
							confirmButtonColor: 'blue',
						}).then(() => {
							window.location.href = `/work2gether/${companyId}/survey`;
						})
					} else if (resp.status == 400) {
						Swal.fire({
							title: "오류",
							text: "설문조사 게시물의 번호가 전달되지 않았습니다.",
							icon: "error",
							showCancelButton: true,
							// confirm 버튼 텍스트
							confirmButtonText: '확인',
							cancelButtonText: '취소',
							// confirm 버튼 색 지정(승인)
							confirmButtonColor: 'blue',
							cancelButtonColor: 'red',
							// resolve 함수 생각하면 good...
						})
					} else {
						Swal.fire({
							title: "서버오류",
							html: "서버에 오류가 생겨 정상적으로 처리되지 않았습니다. <br> 다시 시도해주세요.",
							icon: "error",
							showCancelButton: true,
							// confirm 버튼 텍스트
							confirmButtonText: '확인',
							cancelButtonText: '취소',
							// confirm 버튼 색 지정(승인)
							confirmButtonColor: 'blue',
							cancelButtonColor: 'red'
						})
					}
				}
			})
		})
	}


});






