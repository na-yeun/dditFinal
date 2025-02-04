/**
 * 
 */

// 연필 표시 클릭시 수정 폼 open
async function editToDo(todoNo){
	let thisDiv = document.getElementById(todoNo);
	let thisPriority = thisDiv.dataset.priority;
	let thisContent = thisDiv.querySelector('span').dataset.content;
	openToDoListEditModal(todoNo, thisPriority, thisContent);
}



// x 표시 클릭시 todolist 삭제 함수
async function deleteToDo(todoNo){
	let delUrl = `${contextPath}/${companyId}/todo/${todoNo}`
	
	let todoArea = document.getElementById(todoNo);
	
	let resp = await fetch(delUrl,{
		method:'delete',
	});
	
	if(resp.status==200){
		todoArea.remove();
		
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
		    title: "삭제되었습니다.",
		    customClass: {
		        title: 'swal-title',
		        text: 'swal-text'
		    }
		})
	} else if(resp.status==400){
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
		    title: "할 일이 제대로 선택되지 않았습니다.",
		    customClass: {
		        title: 'swal-title',
		        text: 'swal-text'
		    }
		})
	} else if(resp.status==500){
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
	
}

document.addEventListener("DOMContentLoaded", () => {
	const todoPlusBtn = document.querySelector('#todo-plus-btn');
	const todoPlusForm = document.querySelector('#todo-plus-form');
	const todoChecks = document.querySelectorAll('.todo-check');
	
	const insertBtn = document.querySelector('#insert-todo-btn');
	
	insertBtn.addEventListener("click", () => {
		document.querySelector('#todoContent').value="인재 추천 제출하기";
		document.querySelector('#todoPriority').value="3";
	})
	
	// to do list 추가 모달
	const todolistModal = new bootstrap.Modal('#todolistModal');
	
	window.openToDoListEditModal = function (todoNo, thisPriority, thisContent) {
		let updateModal = document.querySelector('#todolistModal');
		updateModal.querySelector('#todoNo').value=todoNo;
		updateModal.querySelector('#todoPriority').value=thisPriority;
		updateModal.querySelector('#todoContent').value=thisContent;
        todolistModal.show();
    };
	
	// 추가 버튼 클릭시 모달 오픈
	todoPlusBtn.addEventListener("click", () => {
		let allInput = document.querySelectorAll('#todolistModal input');
		allInput.forEach(element=>{
			element.value='';
		})
		todolistModal.show();
	})
	
	// 추가 모달의 form submit 이벤트
	todoPlusForm.addEventListener("submit", async (e) => {
		e.preventDefault();
		let url;
		let formData = new FormData(todoPlusForm);
		let method='';
		let jsonData = {};
	    formData.forEach((value, key) => {
	        jsonData[key] = value;
	    });

		let options;
		let todoNo = todoPlusForm.querySelector('#todoNo').value;
		if(todoNo){
			method = 'put';
			
			url = `${todoPlusForm.action}/${todoNo}`;
		    
			
		} else {
			url = todoPlusForm.action;
			method = 'post';
		}
		
		
		let resp = await fetch(url,{
			method:method,
			headers : {
				'content-type':'application/json'
			},
			body : JSON.stringify(jsonData)
		});
		
		if(resp.status==200){
			Swal.fire({
				title: "완료",
				text: "완료되었습니다.",
				icon: "success",
	
				// confirm 버튼 텍스트
				confirmButtonText: '확인',
				// confirm 버튼 색 지정(승인)
				confirmButtonColor: 'blue',
	
				// resolve 함수 생각하면 good...
			}).then(() => {
				// result(결과)가 confirm이면(승인이면)
				location.reload();
			})
			
		} else if(resp.status==400){
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
			    title: "빈 값이 있거나 할 일의 데이터가 너무 깁니다.",
			    customClass: {
			        title: 'swal-title',
			        text: 'swal-text'
			    }
			})
		} else if(resp.status==500){
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
			    title: "서버 오류로 추가 실패",
			    customClass: {
			        title: 'swal-title',
			        text: 'swal-text'
			    }
			})
		}
			 
	}) // 추가 이벤트
	
	// 체크 이벤트
	todoChecks.forEach(element => {
		element.addEventListener("change", async (e) => {
			let mydiv = e.target.closest('div');
			let todoNo = mydiv.id;
			
			let checked = "";
			// 업데이트 요청 해야함ㅋ
			if (e.target.checked) {
	            // 체크박스가 체크
				checked = 'Y'

	        } else {
	            // 체크박스가 해제
				checked = 'N'
	        }

			let data = {
				"todoNo" : todoNo,
				"todoStatus" : checked
			}
			
			let updateUrl = `${contextPath}/${companyId}/todo/${todoNo}/check`
			
			let resp = await fetch(updateUrl, {
				method:'put',
				headers : { 
					'Content-Type': 'application/json' 
				},
				body : JSON.stringify(data)
			});
			
			if(resp.status==200){
				
				mydiv.classList.toggle("todo-done");
			}else if(resp.status==400){
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
				    title: "데이터 누락으로 추가 실패",
				    customClass: {
				        title: 'swal-title',
				        text: 'swal-text'
				    }
				})
			}else if(resp.status==500){
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
				    title: "서버오류로 추가 실패",
				    customClass: {
				        title: 'swal-title',
				        text: 'swal-text'
				    }
				})
			}
		})
	})
	
	
	
})