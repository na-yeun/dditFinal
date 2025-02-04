/**
 * 
 */


// 연필 표시 클릭시 수정 폼 open
async function editMyComent(){
	openMyCommentEditModal();
}


document.addEventListener("DOMContentLoaded", ()=>{
	const myCommentForm = document.querySelector('#my-comment-form');
	const myCommentArea = myCommentForm.querySelector('#myComment');
	const myCommentModal = new bootstrap.Modal('#myCommentModal');
	let myCommentValueArea = document.querySelector('#my-comment-area');
	let myCommentValue = myCommentValueArea.innerHTML;
	
	window.openMyCommentEditModal = function () {
		myCommentArea.value=myCommentValue;
		
        myCommentModal.show();
    };

	myCommentForm.addEventListener("submit", async (e) => {
		e.preventDefault();
		let myComment = myCommentArea.value;
		let data = {
			"myComment" : myComment
		};
		
		let commentUrl = myCommentForm.action;
		
		
		let resp = await fetch(commentUrl, {
			method: 'put',
			headers : {
				'content-type' : 'application/json'
			},
			body : JSON.stringify(data)
		});
		
		if(resp.status==200){
			myCommentValueArea.innerHTML = myComment;
			myCommentModal.hide();
			Swal.mixin({
			    toast: true,
			    position: "center",
			    showConfirmButton: false,
			    timer: 3000,
			    timerProgressBar: true,
			    
			    // 알림 열렸을 때 실행되는 콜백함수
			    // toast 인자로 알림 DOM 요소 접근
			    didOpen: (toast) => {
			    	// 토스트에 마우스를 올렸을 때 타이머 멈추는 이벤트(알림이 안 닫힘)
			        toast.addEventListener('mouseenter', Swal.stopTimer)
			        // 토스트에 마우스 치우면 타이머 진행 이벤트
			        toast.addEventListener('mouseleave', Swal.resumeTimer)
			    }
			}).fire({
			    icon: "success",
			    title: "수정되었습니다.",
			    customClass: {
			        title: 'swal-title',
			        text: 'swal-text'
			    }
			})
		} else if(resp.status==400){
			myCommentArea.value = myComment;
			Swal.mixin({
			    toast: true,
			    position: "center",
			    showConfirmButton: false,
			    timer: 3000,
			    timerProgressBar: true,
			    
			    // 알림 열렸을 때 실행되는 콜백함수
			    // toast 인자로 알림 DOM 요소 접근
			    didOpen: (toast) => {
			    	// 토스트에 마우스를 올렸을 때 타이머 멈추는 이벤트(알림이 안 닫힘)
			        toast.addEventListener('mouseenter', Swal.stopTimer)
			        // 토스트에 마우스 치우면 타이머 진행 이벤트
			        toast.addEventListener('mouseleave', Swal.resumeTimer)
			    }
			}).fire({
			    icon: "error",
			    title: "코멘트가 제대로 작성되지 않았거나 너무 깁니다.",
			    customClass: {
			        title: 'swal-title',
			        text: 'swal-text'
			    }
			})
		} else if(resp.status==500){
			myCommentArea.value = myComment;
			Swal.mixin({
			    toast: true,
			    position: "center",
			    showConfirmButton: false,
			    timer: 3000,
			    timerProgressBar: true,
			    
			    // 알림 열렸을 때 실행되는 콜백함수
			    // toast 인자로 알림 DOM 요소 접근
			    didOpen: (toast) => {
			    	// 토스트에 마우스를 올렸을 때 타이머 멈추는 이벤트(알림이 안 닫힘)
			        toast.addEventListener('mouseenter', Swal.stopTimer)
			        // 토스트에 마우스 치우면 타이머 진행 이벤트
			        toast.addEventListener('mouseleave', Swal.resumeTimer)
			    }
			}).fire({
			    icon: "error",
			    title: "서버 오류 입니다. 다시 시도해주세요.",
			    customClass: {
			        title: 'swal-title',
			        text: 'swal-text'
			    }
			})
		}
		
	})

	


})