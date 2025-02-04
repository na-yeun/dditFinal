function paging(page) {
	searchForm.page.value = page;
	searchForm.requestSubmit();
}


// 첨부파일 id, 실제 메세지 id를 받아서 비동기로 다운로드 요청
async function getAttach(messageId, mailattachmentId) {
	const thisUrl = window.location.pathname;
	let downUrl = `${thisUrl}/download?messageId=${messageId}&mailattachmentId=${mailattachmentId}`;

	fetch(downUrl, {
		method: "GET",
		headers: {
			"Accept": "application/json", // 서버에서 JSON 응답을 기대
		}
	})
		.then(response => {
			if (!response.ok) {
				throw new Error("파일 다운로드에 실패했습니다.");
			}

			// Content-Disposition 헤더에서 파일 이름 추출
			const disposition = response.headers.get('Content-Disposition');
			let filename = "attachment"; // 기본 파일명 설정
			if (disposition) {
				// filename* 우선 처리 (UTF-8 인코딩 지원)
				const utf8FilenameMatches = /filename\*=UTF-8''(.+)/.exec(disposition);
				if (utf8FilenameMatches != null && utf8FilenameMatches[1]) {
					filename = decodeURIComponent(utf8FilenameMatches[1]); // URL 디코딩
				} else {
					// fallback: filename 처리 (ASCII 인코딩)
					const asciiFilenameMatches = /filename="(.+)"/.exec(disposition);
					if (asciiFilenameMatches != null && asciiFilenameMatches[1]) {
						filename = asciiFilenameMatches[1];
					}
				}
			}

			// 응답을 Blob 형태로 받기
			return response.blob().then(blob => ({ filename, blob }));
		})
		.then(({ filename, blob }) => {
			// Blob을 다운로드 링크로 변환
			var downloadLink = document.createElement("a");
			var url = window.URL.createObjectURL(blob);
			downloadLink.href = url;
			downloadLink.download = filename;  // 서버에서 받은 파일명으로 다운로드 설정
			downloadLink.click();  // 자동으로 다운로드가 시작됩니다.

			// 다운로드 링크 제거
			window.URL.revokeObjectURL(url);
		})
		.catch(error => {
			alert(error.message); // 오류 메시지 출력
		});
}

// 토스트 알람 설정정
const Toast = Swal.mixin({
	// 토스트 알람 설정
	toast: true,
	// 알림 위치
	position: 'center',
	// 확인 버튼 숨김(toast에서는 확인버튼 필요없음)
	showConfirmButton: false,
	// toast 알림 팝업 시간
	timer: 2500,
	// 타이머 진행바, 기본값을 false이고 false로 설정하면 안 보임
	timerProgressBar: true,


	// 알림 열렸을 때 실행되는 콜백함수
	// toast 인자로 알림 DOM 요소 접근
	didOpen: (toast) => {
		// 토스트에 마우스를 올렸을 때 타이머 멈추는 이벤트(알림이 안 닫힘)
		toast.addEventListener('mouseenter', Swal.stopTimer)
		// 토스트에 마우스 치우면 타이머 진행 이벤트
		toast.addEventListener('mouseleave', Swal.resumeTimer)
	}
});

// 메일 삭제 함수
async function trashmail(checkedIds) {
	// 아이디들이 담긴 checkedIds를 controller에 post 방식으로 보내기.. 비동기로
	// 배열에 담긴 message id를 통해 삭제메일함 insert, 해당 메일함 delete처리 + api trash
	let url = window.location.pathname + '/trash';
	let resp = await fetch(url, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'  // JSON 형식으로 보내기
		},
		body: JSON.stringify(checkedIds)  // 배열을 JSON 문자열로 변환하여 body에 담기
	});
	if (resp.ok) {
		// 성공!
		// 화면 새로고침
		Swal.fire({
			title: "메일 삭제가 완료되었습니다.",
			icon: "success",

			// confirm 버튼 텍스트
			confirmButtonText: '확인',
			// confirm 버튼 색 지정(승인)
			confirmButtonColor: 'blue'

			// resolve 함수 생각하면 good...
		}).then(async (result) => {
			// result(결과)가 confirm이면(승인이면)
			if (result.isConfirmed) {
				location.reload();
			} else {

			}
		})
	} else {
		// 실패!
		Toast.fire({
			icon: 'error',
			title: '삭제에 실패했습니다.'
		})
	}

} // 메일 삭제 함수

// 메일 중요 설정 함수
async function importantMail(checkedIds) {
	// 아이디들이 담긴 checkedIds를 controller에 post 방식으로 보내기.. 비동기로
	// 배열에 담긴 message id를 통해 삭제메일함 insert, 해당 메일함 delete처리 + api trash
	let url = window.location.pathname + '/important';
	let resp = await fetch(url, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'  // JSON 형식으로 보내기
		},
		body: JSON.stringify(checkedIds)  // 배열을 JSON 문자열로 변환하여 body에 담기
	});
	if (resp.ok) {
		// 성공!
		// 화면 새로고침
		Swal.fire({
			title: "중요메일 설정이 완료되었습니다.",
			icon: "success",

			// confirm 버튼 텍스트
			confirmButtonText: '확인',
			// confirm 버튼 색 지정(승인)
			confirmButtonColor: 'blue'

			// resolve 함수 생각하면 good...
		}).then(async (result) => {
			// result(결과)가 confirm이면(승인이면)
			if (result.isConfirmed) {
				location.reload();
			} else {

			}
		})
	} else {
		// 실패!
		Toast.fire({
			icon: 'error',
			title: '중요 메일 설정에 실패했습니다.'
		})
	}
} // 메일 중요 설정 함수

document.addEventListener("DOMContentLoaded", function() {
	// 모달창
	const myModal = new bootstrap.Modal(document.querySelector('#mail-detail'));
	// 메일의 tr 태그(클릭시 메일 보여야 함)
	const mailSelector = document.querySelectorAll('.mail-list-table tbody tr .clickTitle');
	// 메일 새로 고침 버튼
	const mailRefreshBtn = document.querySelector('#mail-refresh');
	// 나의 메일 주소 data 속성으로 저장되어있는 곳
	const myMailArea = document.querySelector('#my-mail-area');
	// 중요메일 설정 버튼
	const mailImportantBtn = document.querySelector('#important-btn');
	// 메일 삭제 버튼
	const maildeleteBtn = document.querySelector('#delete-btn');
	// 전체 선택 체크박스
	const totalCheckBox = document.querySelector('#total-checkbox');
	// 낱개 선택 체크박스
	const checkBoxList = document.querySelectorAll('.one-checkBox');
	
	// 모달창 답장버튼
	const modalReplyBtn = document.querySelector('#modal-re-btn');
	// 모달창 전달버튼
	const modalForwardBtn = document.querySelector('#modal-for-btn');
	// 전달하기 폼
	const forwardForm = document.querySelector('#forwardForm');
	// 모달창 중요버튼
	const modalImportantBtn = document.querySelector('#modal-important-btn');
	// 모달창 삭제버튼
	const modalDeleteBtn = document.querySelector('#modal-del-btn');


	// 한 명 이상일 경우 
	// 모든 class="to" 요소 선택
	const cells = document.querySelectorAll(".from"); 
	
	cells.forEach(cell => {
		// 해당 cell 안에 있는 span 태그들의 길이 확인
		let spanTag = cell.querySelectorAll('span');
		let spanLength = spanTag.length;
		if (spanLength > 1) {
			// 한 명 이상일 경우
			let htmlcnt = spanLength-1;
			cell.innerHTML = `${spanTag[0].outerHTML} 외 ${htmlcnt}명`;
		}
	}); // 글자수 초과시 ... 으로 표시
	

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

	// 메일 클릭시 데이터 가지고 와서 모달 열리는 이벤트
	mailSelector.forEach(function(myMail) {
		myMail.addEventListener("click", async function(e) {
			// 이벤트를 부른 객체
			let target = e.target;
			// 그 객체의 부모 객체(tr 태그)
			let daddy = target.parentNode;
			let messageId = daddy.dataset.messageid;

			// 비동기 양식으로 messageId를 통해 하나의 메세지 가지고 오기
			// 가지고 온 하나의 메세지의 값을, 모달창에 뿌리고 모달 열기

			let url = window.location.pathname + '/' + messageId;
			let resp = await fetch(url);
			if (resp.ok) {
				let { mailMessageId, empMail, rmailFromList, rmailCc, rmailDate, rmailSubject, mailAttachmentList, rmailContent } = await resp.json();
				document.querySelector('#mail-id').value = mailMessageId;
				
				let fromCode = ``;
				rmailFromList.forEach(fromOne => {
					fromCode += `<span class="mail-span badge bg-label-info">${fromOne}</span>, `;
				})
				if(fromCode.endsWith(", ")){
					fromCode = fromCode.slice(0, -2);
				}
				document.querySelector('#mail-from').innerHTML = fromCode;
				
				document.querySelector('#mail-to').innerHTML = `나 &lt;${empMail}&gt;`;
				document.querySelector('#mail-cc').innerHTML = rmailCc;
				document.querySelector('#mail-date').innerHTML = rmailDate;
				document.querySelector('#mail-title').innerHTML = rmailSubject;
				document.querySelector('#mail-content').innerHTML = rmailContent;

				//     				document.querySelector('#attach-area').innerHTML = mailAttachmentList[0].mailattachmentName;

				let code = '';

				mailAttachmentList.forEach((mailAttachment, i) => {
					if (mailAttachment.mailattachmentName) {
						code += `<a href='javascript:void(0)' onclick="getAttach('${mailAttachment.mailmessageId}','${mailAttachment.mailattachmentId}')">${mailAttachment.mailattachmentName}</a><br/>`;
					}

				})

				document.querySelector('#attach-area').innerHTML = code;


				myModal.show();

			} else {
				console.log("실패")
			}
		});
	}) // 메일 클릭시 데이터 가지고 와서 모달 열리는 이벤트

	// 메일 새로 고침 버튼 클릭시, 이벤트 호출
	mailRefreshBtn.addEventListener("click", () => {
		Swal.fire({
			title: "메일을 불러옵니다.",
			html: "메일을 불러오는 동안 시간이 소요될 수 있습니다.<br>실행하시겠습니까?",
			icon: "warning",

			// candel 버튼
			showCancelButton: true,
			// confirm 버튼 텍스트
			confirmButtonText: '승인',
			// cancel 버튼 텍스트
			cancelButtonText: '취소',
			// confirm 버튼 색 지정(승인)
			confirmButtonColor: 'blue',
			// cancel 버튼 색 지정(취소)
			cancelButtonColor: 'red',

			// resolve 함수 생각하면 good...
		}).then(async (result) => {
			// result(결과)가 confirm이면(승인이면)
			if (result.isConfirmed) {
				let url = window.location.pathname + '/refresh';
				
				Swal.fire({
					title: "처리 중입니다...",
					html: "잠시만 기다려주세요.",
					allowOutsideClick: false,
					didOpen: () => {
						Swal.showLoading(); // 로딩 표시
					},
				});
				
				let resp = await fetch(url);
				if (resp.ok) {
					Swal.close();
					Swal.fire({
						title: "완료되었습니다.",
						icon: "success",
			
						// confirm 버튼 텍스트
						confirmButtonText: '확인',
						// confirm 버튼 색 지정(승인)
						confirmButtonColor: 'blue',
			
						// resolve 함수 생각하면 good...
					}).then(async (result) => {
						// result(결과)가 confirm이면(승인이면)
						if (result.isConfirmed) {
							location.reload();
						} else {
			
						}
					})

				} else {
					Swal.close();
					Swal.fire({
						title: "서버 오류로 실패했습니다.",
						icon: "error",
						// confirm 버튼 텍스트
						confirmButtonText: '확인',
						// confirm 버튼 색 지정(승인)
						confirmButtonColor: 'blue',
			
						// resolve 함수 생각하면 good...
					}).then(async (result) => {
						// result(결과)가 confirm이면(승인이면)
						if (result.isConfirmed) {
							location.reload();
						} else {
			
						}
					})
				}
			} else {

			}
		})
	}); // 메일 새로고침
	

	// 체크버튼 토글
	totalCheckBox.addEventListener("click", () => {
		// 지금 전체 체크박스의 상태
		const isChecked = totalCheckBox.checked;
		checkBoxList.forEach(cb => {
			// 상태에 따라 모든 체크박스를 선택/해제
			cb.checked = isChecked;
		});
	});

	// 목록 삭제버튼 클릭시
	maildeleteBtn.addEventListener("click", async () => {

		let checkedCheckBoxes = [];
		checkBoxList.forEach(cb => {
			if (cb.checked) {
				// 체크되어있으면 checkedCheckBoxes 배열에 넣기기
				checkedCheckBoxes.push(cb);
			}
		});

		if (checkedCheckBoxes.length != 0) {
			Swal.fire({
				title: "메일을 삭제하시겠습니까?",
				icon: "warning",

				// candel 버튼
				showCancelButton: true,
				// confirm 버튼 텍스트
				confirmButtonText: '승인',
				// cancel 버튼 텍스트
				cancelButtonText: '취소',
				// confirm 버튼 색 지정(승인)
				confirmButtonColor: 'blue',
				// cancel 버튼 색 지정(취소)
				cancelButtonColor: 'red',

				// resolve 함수 생각하면 good...
			}).then(async (result) => {
				// result(결과)가 confirm이면(승인이면)
				if (result.isConfirmed) {
					// 체크 박스에 담긴 애들을 통해 걔네들의 message id 가져와서 배열에 담기
					let checkedIds = [];
					checkedCheckBoxes.forEach(ccb => {
						let itsMessageId = ccb.parentNode.parentNode.dataset.messageid;
						checkedIds.push(itsMessageId);
					});
					trashmail(checkedIds);

				} else {

				}
			})
		}

	});

	// 모달창 안의 삭제 버튼 클릭시
	modalDeleteBtn.addEventListener("click", async () => {
		// 모달창에 해당하는 메세지의 아이디
		let thisMessageId = document.querySelector('#mail-id').value;
		Swal.fire({
			title: "메일을 삭제하시겠습니까?",
			icon: "warning",
			// candel 버튼
			showCancelButton: true,
			// confirm 버튼 텍스트
			confirmButtonText: '승인',
			// cancel 버튼 텍스트
			cancelButtonText: '취소',
			// confirm 버튼 색 지정(승인)
			confirmButtonColor: 'blue',
			// cancel 버튼 색 지정(취소)
			cancelButtonColor: 'red',
			customClass: {
				popup: 'swal2-popup' // SweetAlert 팝업 클래스
			},
			didOpen: () => {
				// SweetAlert 팝업의 z-index 조정
				const swalPopup = document.querySelector('.swal2-popup');
				swalPopup.style.zIndex = '1060';
			},

			// resolve 함수 생각하면 good...
		}).then(async (result) => {
			if (result.isConfirmed) {
				let checkedIds = [];
				checkedIds.push(thisMessageId);
				trashmail(checkedIds);
			} else {

			}
		})
	})

	// 목록 중요메일 버튼 클릭시
	mailImportantBtn.addEventListener("click", () => {
		// 체크되어있는 모든 체크박스 가지고 오기
		// 모든 체크박스들을 가져온 셀렉터를 하나씩 돌아가면서 선택되어있는지, 아닌지 확인
		let checkedCheckBoxes = [];
		checkBoxList.forEach(cb => {
			if (cb.checked) {
				// 체크되어있으면 checkedCheckBoxes 배열에 넣기기
				checkedCheckBoxes.push(cb);
			}
		});

		if (checkedCheckBoxes.length != 0) {
			Swal.fire({
				title: "중요메일로 설정하시겠습니까?",
				text: "중요메일 설정시 중요메일 메뉴에서 확인할 수 있습니다.",
				icon: "warning",

				showCancelButton: true,
				confirmButtonText: '승인',
				cancelButtonText: '취소',
				confirmButtonColor: 'blue',
				cancelButtonColor: 'red',

			}).then(async (result) => {
				// result(결과)가 confirm이면(승인이면)
				if (result.isConfirmed) {
					// 체크 박스에 담긴 애들을 통해 걔네들의 message id 가져와서 배열에 담기
					let checkedIds = [];
					checkedCheckBoxes.forEach(ccb => {
						let itsMessageId = ccb.parentNode.parentNode.dataset.messageid;
						checkedIds.push(itsMessageId);
					});
					importantMail(checkedIds);

				} else {

				}
			})
		}

	});
	
	// 모달창 안에서 중요메일 클릭시
	modalImportantBtn.addEventListener("click", () => {
		let thisMessageId = document.querySelector('#mail-id').value;
		Swal.fire({
			title: "중요메일로 설정하시겠습니까?",
			text: "중요메일 설정시 중요메일 메뉴에서 확인할 수 있습니다.",
			icon: "warning",
			// candel 버튼
			showCancelButton: true,
			// confirm 버튼 텍스트
			confirmButtonText: '승인',
			// cancel 버튼 텍스트
			cancelButtonText: '취소',
			// confirm 버튼 색 지정(승인)
			confirmButtonColor: 'blue',
			// cancel 버튼 색 지정(취소)
			cancelButtonColor: 'red',
			customClass: {
				popup: 'swal2-popup' // SweetAlert 팝업 클래스
			},
			didOpen: () => {
				// SweetAlert 팝업의 z-index 조정
				const swalPopup = document.querySelector('.swal2-popup');
				swalPopup.style.zIndex = '1060';
			},

			// resolve 함수 생각하면 good...
		}).then(async (result) => {
			if (result.isConfirmed) {
				let checkedIds = [];
				checkedIds.push(thisMessageId);
				importantMail(checkedIds);
			} else {

			}
		})
	}) // 모달창 안에서 중요메일 클릭시
	
	// 메일 전달 버튼 클릭시
	modalForwardBtn.addEventListener("click", () => {
		forwardForm.action = window.location.pathname+"/forward";
		console.log(forwardForm.action)
		forwardForm.requestSubmit();
	})
	
	// 메일 답장 버튼 클릭시
	modalReplyBtn.addEventListener("click", () => {
		forwardForm.action = window.location.pathname+"/reply";
		forwardForm.requestSubmit();
	})

});
