/**
 * 
 */
 
function paging(page){
	console.log(page);
	searchForm.page.value = page;
	searchForm.requestSubmit();
}

function checkAccess(quSecretyn, quNo, myEmpId, questionId) {
    if (quSecretyn === 'Y') {
        // 비밀글일 경우 본인 확인
        if (myEmpId === questionId) {
            // 본인이라면 이동
            window.location.href = `${contextPath}/${companyId}/question/${quNo}`;
        } else {
            // 본인이 아니라면 경고 메시지
			Swal.fire({
				title: "조회 불가",
				text: "비밀글은 작성자만 확인할 수 있습니다.",
				icon: "error"
			});
        }
    } else {
        // 비밀글이 아닐 경우 바로 이동
        window.location.href = `${contextPath}/${companyId}/question/${quNo}`;
    }
}


document.addEventListener("DOMContentLoaded", ()=>{
	const questionWrite = document.getElementById("questionWrite");
	

	questionWrite.addEventListener("click", () => {
	    const dataLcAdd = questionWrite.getAttribute("data-lc-add"); // data-lc-add 속성 값 가져오기
	        window.location.href = dataLcAdd; // 해당 URL로 이동
	});

	// 검색 버튼 클릭 이벤트 처리
	const searchBtn = document.getElementById("search-btn");
	const searchForm = document.getElementById("searchForm"); // 폼 ID 확인
	if (searchBtn && searchForm) {
		searchBtn.addEventListener("click", () => {
			const searchType = document.querySelector("[name='searchType']");
			const searchWord = document.querySelector("[name='searchWord']");
			if (searchType && searchWord) {
				searchForm.querySelector("[name='searchType']").value = searchType.value;
				searchForm.querySelector("[name='searchWord']").value = searchWord.value;
				searchForm.submit(); // 폼 제출
			}
		});
	}
	
});