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
    

    $searchBtn.on("click", function () {
        // 모든 입력 필드 값을 폼에 업데이트
        $(":input[name]").each(function (index, ipt) {
            let $ipt = $(ipt);
            let value = $ipt.val().trim(); // 입력 값 공백 제거

            // 날짜 필드는 하이픈 제거, 나머지는 그대로 유지
            if ($ipt.attr("type") === "date") {
                value = value.replace(/-/g, "");
            }

            // 폼의 hidden 필드 업데이트
            if ($searchForm.find(`input[name="${ipt.name}"]`).length > 0) {
                $searchForm.find(`input[name="${ipt.name}"]`).val(value);
            }

            console.log(`Updated field: ${ipt.name}, Value: ${value}`);
        });

        // 검색어 유효성 검증 (예: 공백 확인)
        const searchWord = $("#searchWord").val().trim();
        console.log("검색어 :",searchWord);

        // 폼 제출
        $searchForm.submit();
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
    const reloadBtn = document.querySelector('#reload-btn');

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

    // 새로고침 버튼
	
    reloadBtn.addEventListener("click", () => {
        console.log("새로고침 버튼 클릭");
        startDateInput.value = "";
        endDateInput.value = "";
        $("#searchWord").val("");
    
        // 폼의 hidden 필드 초기화
        $(":input[type=hidden]").each(function() {
            $(this).val("");
        });
    
        $searchForm.submit();
    });
    
}); // DOM end