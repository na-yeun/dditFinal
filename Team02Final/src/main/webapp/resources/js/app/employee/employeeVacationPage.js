/**
 * 
 */

function paging(page) {
	console.log(page);
	searchForm.page.value = page;
	searchForm.requestSubmit();
}



//날짜 객체를 받아서 yyyy-mm-dd(input에 찍힐 모양)
function changeDateFormat(date) {
	let yyyy = date.getFullYear();
	let mm = String(date.getMonth() + 1).padStart(2, '0');
	let dd = String(date.getDate()).padStart(2, '0');
	return yyyy + '-' + mm + '-' + dd;
}

document.addEventListener("DOMContentLoaded", () => {
	let $searchForm = $("#searchForm");
	let $searchBtn = $("#search-btn");	
	$searchBtn.on("click", function(){
		
		
		
		
		let $parent = $(this).parents(".search-area");
		$parent.find(":input[name]").each(function(index, ipt){
			if(searchForm[ipt.name]){
				searchForm[ipt.name].value=ipt.value.replace(/-/g, "");
			}	
			$searchForm.submit(); 	
		});
	});
	
	
	
	// 오늘 날짜
	const todayDate = new Date();
	// 오늘날짜
	const today = changeDateFormat(todayDate);

	const weeklyBtn = document.querySelector('#weekly-btn');
	const monthlyBtn = document.querySelector('#monthly-btn');
	const yearBtn = document.querySelector('#year-btn');
	const startDateInput = document.querySelector('#start-date');
	const endDateInput = document.querySelector('#end-date');

	
	endDateInput.addEventListener("change", ()=>{
		let std = startDateInput.value;
		let ed = endDateInput.value;
		if(std>ed){
			Swal.fire({
				title: "오류",
				html: "조회하려는 종료날짜가 시작날짜보다 이전일 수 없습니다.",
				icon: "error",
				showCancelButton: true,
				// confirm 버튼 텍스트
			    confirmButtonText: '확인',
			    cancelButtonText: '취소',
				// confirm 버튼 색 지정(승인)
			    confirmButtonColor: 'blue',
			    cancelButtonColor: 'red',
			// resolve 함수 생각하면 good...
			}).then(()=>{
				endDateInput.value="";
			})
		}
	});

	// 주간 검색 했을 때
	weeklyBtn.addEventListener("click", () => {
		const weekAgo = new Date(todayDate); // 오늘 날짜 복사
		weekAgo.setDate(todayDate.getDate() - 7);

		let start = changeDateFormat(weekAgo);
		startDateInput.value = start;
		endDateInput.value = today;
	});

	// 월간 검색 했을 때
	monthlyBtn.addEventListener("click", () => {
		const monthAgo = new Date(todayDate); // 오늘 날짜 복사
		monthAgo.setMonth(todayDate.getMonth() - 1);

		let start = changeDateFormat(monthAgo);
		startDateInput.value = start;
		endDateInput.value = today;
	});

	// 주간 검색 했을 때
	yearBtn.addEventListener("click", () => {
		const oneYearAgo = new Date(todayDate); // 오늘 날짜 복사
		oneYearAgo.setFullYear(todayDate.getFullYear() - 1);

		let start = changeDateFormat(oneYearAgo);
		startDateInput.value = start;
		endDateInput.value = today;
	});
	
	


})