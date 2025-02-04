/**
 * 
 */
 
 function paging(page) {
	searchForm.page.value = page;
	searchForm.requestSubmit();
}

function getInsertForm(posiId){
	if(posiId==6 || posiId==7 || posiId==3 || posiId==4){
		location.href = `${window.location.pathname}/manage`;
	} else {
		Swal.fire({
			title: "권한이 없습니다.",
			html: "설문 등록 권한이 없습니다.",
			icon: "error"
		});
	}
}

document.addEventListener("DOMContentLoaded", () => {
	const surveyList = document.querySelectorAll('.survey-list');
	
	
	surveyList.forEach(listOne=>{
		listOne.addEventListener("click", async e =>{
			let parent = e.target.closest("tr");
			let surveyId = parent.dataset.surveyId;
			let surveyTarget = parent.dataset.surveyTarget;
			console.log(surveyTarget);
			if(surveyTarget=='true'){
				location.href = `${window.location.pathname}/${surveyId}`;
			} else {
				Swal.fire({
					title: "권한이 없습니다.",
					html: "해당 설문조사에 조회 및 참여 권한이 없습니다.",
					icon: "error"
				});
			}
			//
			
		})
	});
	
	
	
	// 검색 관련 스크립트
	let $searchForm = $("#searchForm");
	let $searchBtn = $("#search-btn");
	$searchBtn.on("click", function() {
		let $parent = $(this).parents(".search-area");
		$parent.find(":input[name]").each(function(index, ipt) {

			if (searchForm[ipt.name]) {
				searchForm[ipt.name].value = ipt.value.replace(/-/g, "");
			}

			$searchForm.submit();
		});
	});  // 검색 관련 스크립트
	
	
});