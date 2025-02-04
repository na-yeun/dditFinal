/**
 * 
 */

document.addEventListener("DOMContentLoaded", () => {
	// 현재 주소
		
	const submitForm = document.querySelector('#submit-form');
	const detailTable = document.querySelector('#detail-table');
	const boardNo = detailTable.dataset.boardNo;
	const multiOptions = document.querySelectorAll('.multi-options-container');
	const oneOption =document.querySelectorAll('.options-container');
	const textareaOption = document.querySelectorAll('.textarea-container');
	const editBtn = document.querySelector('#edit-btn');
	const deleteBtn = document.querySelector('#delete-btn');
	const beforeBtn = document.querySelector('#before-btn');
	
	// 목록 버튼 클릭시
	beforeBtn.addEventListener("click", () => {
		location.href=`${contextPath}/${companyId}/survey`
	});
	
	// 전송 요청이 들어왔을 때!(참여했을 때)
	submitForm.addEventListener("submit", async (e) => {
		e.preventDefault();
		
		Swal.fire({
			title: "참여하시겠습니까?",
			html: "한 번 참여한 설문은 취소하거나 수정할 수 없습니다.",
			icon: "warning",
			showCancelButton: true,
			// confirm 버튼 텍스트
		    confirmButtonText: '확인',
		    cancelButtonText: '취소',
			// confirm 버튼 색 지정(승인)
		    confirmButtonColor: 'blue',
		    cancelButtonColor: 'red',
		// resolve 함수 생각하면 good...
		}).then(async result => {
			if(result.isConfirmed){
				let resultList = [];
				
				
				// 다중선택 가능한 문항들 한 개씩 접근
				multiOptions.forEach((value)=>{
					// 다중선택 가능한 문항들의 체크된 응답들을 가지고 옴
					const checkedMultiOptions = value.querySelectorAll('div input:checked');
					
					checkedMultiOptions.forEach((checked) => {
						resultList.push({
			                surquesNo: checked.name,
			                suritemNo: checked.value,
			            });
					});
					
				});
				
				// 단일선택 가능한 문항들 한 개씩 접근
				oneOption.forEach((value)=>{
					// 단일 선택한 문항의 응답
					const checkedOneOptions = value.querySelectorAll('div input:checked');
					
					checkedOneOptions.forEach((checked) => {
						resultList.push({
			                surquesNo: checked.name,
			                suritemNo: checked.value,
			            });
					})
				});
				
				// 서술형 문항의 응답
				textareaOption.forEach((value)=>{
					let textareazone = value.querySelector('textarea');
					resultList.push({
			            suritemNo: textareazone.id,
			            surquesNo: textareazone.name,
			            resContent: textareazone.value,
			        });
				});
				
				let url = window.location.pathname;
				let resp = await fetch(url, {
					method : "post",
					headers: {
			            "Content-Type": "application/json",
			        },
					body : JSON.stringify(resultList)
				});
				
				if(resp.status==200){
					Swal.fire({
						title: "참여완료",
						text: "설문조사 참여가 완료되었습니다!",
						icon: "success",
						// confirm 버튼 텍스트
					    confirmButtonText: '확인',
						// confirm 버튼 색 지정(승인)
					    confirmButtonColor: 'blue',
					// resolve 함수 생각하면 good...
					}).then(result => {
						if (document.referrer && document.referrer !== window.location.href) {
			               window.location.href = document.referrer;
			            } else {
			               // referrer가 없으면 기본 뒤로 가기
			               window.history.back();
			             }
					})				
				} else if(resp.status==400){
					Swal.fire({
						title: "항목 누락으로 실패",
						html: "누락된 항목이 있습니다. <br> 누락된 항목을 확인 후 다시 시도해주세요.",
						icon: "error"
					})					
				} else {
					Swal.fire({
						title: "서버 오류",
						html: "서버 오류로 메일 발송에 실패했습니다. <br> 다시 시도해주세요.",
						icon: "error"
					});
				}
			}
		});
	});

	// 삭제 요청 들어왔을 때
	deleteBtn.addEventListener("click", () => {
		Swal.fire({
			title: "삭제",
			text: "정말로 삭제하시겠습니까?",
			icon: "warning",
			showCancelButton: true,
			// confirm 버튼 텍스트
		    confirmButtonText: '확인',
		    cancelButtonText: '취소',
			// confirm 버튼 색 지정(승인)
		    confirmButtonColor: 'blue',
		    cancelButtonColor: 'red',
		// resolve 함수 생각하면 good...
		}).then(async(result) => {
			if(result.isConfirmed){
				// 삭제 요청(비동기)
				
				let delUrl = `${contextPath}/${companyId}/survey/manage/${boardNo}`;
				let resp = await fetch(delUrl, {
					method: 'DELETE'
				});
				if(resp.status==200){
					Swal.fire({
						title: "삭제 완료",
						text: "삭제가 완료되었습니다.",
						icon: "success",
						// confirm 버튼 텍스트
						confirmButtonText: '확인',
						// confirm 버튼 색 지정(승인)
						confirmButtonColor: 'blue',
					}).then(() => {
						window.location.href = `${contextPath}/${companyId}/survey`;
					})
				} else if (resp.status==400) {
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

	// 수정 요청 들어왔을 때
	if(editBtn){
		editBtn.addEventListener("click", async () => {
			// 진행 중인지 진행 전인지 체크
			// 진행 전이면 수정이 가능하고 진행중이면 수정이 불가능함
			let progress = document.querySelector('#progress');
			if(progress){
				// 진행전
				let updateUrl = `${contextPath}/${companyId}/survey/manage/${boardNo}/edit`;
				location.href = updateUrl;
			} else {
				// 진행중
				Swal.fire({
					title: "수정 불가능",
					html: "이미 진행된 설문조사는 수정할 수 없습니다.",
					icon: "error",
					// confirm 버튼 텍스트
					confirmButtonText: '확인',
					// confirm 버튼 색 지정(승인)
					confirmButtonColor: 'blue'
				})
			}
	
	
		})
	}
});