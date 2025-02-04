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
			if (disposition && disposition.indexOf('attachment') !== -1) {
				const matches = /filename="(.+)"/.exec(disposition);
				if (matches != null && matches[1]) {
					filename = matches[1];  // 파일명 추출
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
async function trashmail(checkedIds){
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


document.addEventListener("DOMContentLoaded", function () {
	const contextPath = document.querySelector('#page-title').dataset.contextpath;
	// 모달창
	const myModal = new bootstrap.Modal(document.querySelector('#mail-detail'));
	// 메일의 tr 태그(클릭시 메일 보여야 함)
	const mailSelector = document.querySelectorAll('.mail-list-table tbody tr');
	// 메일 새로 고침 버튼
	const mailRefreshBtn = document.querySelector('#mail-refresh');
	// 나의 메일 주소 data 속성으로 저장되어있는 곳
	const myMailArea = document.querySelector('#my-mail-area');
	// 메일 삭제 버튼
	const maildeleteBtn = document.querySelector('#delete-btn');
	// 전체 선택 체크박스
	const totalCheckBox = document.querySelector('#total-checkbox');
	// 낱개 선택 체크박스
	const checkBoxList = document.querySelectorAll('.one-checkBox');
	// 모달창 재작성버튼
	const modalRewriteBtn = document.querySelector('#modal-rewrite-btn');
	// 모달창 삭제버튼
	const modalDeleteBtn = document.querySelector('#modal-del-btn');

	// 받는 사람 적는 input 태그
	const toInput = document.querySelector('#to-area');
	const ccInput = document.querySelector('#cc-area');
	const bccInput = document.querySelector('#bcc-area');

	const toList = document.querySelector('#to-list');
	const ccList = document.querySelector('#cc-list');
	const bccList = document.querySelector('#bcc-list');

	const subjectInput = document.querySelector('#mailSubject');



	// dropzone 관련 
	Dropzone.autoDiscover = false; // Dropzone 자동 탐색 비활성화

	const dropzone = new Dropzone("div.dropzone", {
		url: "#",
		// 자동보내기(true면 올리자마자 서버요청, false면 버튼 눌러야 전송)
		autoProcessQueue: false,
		// true로 설정하면 사용자가 드래그 앤 드롭 외에도 클릭으로 파일을 선택할 수 있음
		//clickable: true, 
		// 자동으로 파일을 서버에 업로드하지 않음
		autoQueue: false,
		//파일 업로드 썸네일 생성
		createImageThumbnails: true,
		// 썸네일 이미지 높이
		thumbnailHeight: 120,
		// 썸네일 이미지 너비
		thumbnailWidth: 120,
		// 업로드 파일수(최대)
		maxFiles: 30,
		// 최대업로드용량 : MB
		maxFilesize: 25,
		// 서버에서 사용할 formdata 파라미터 이름(기본설정은 file)
		//         paramName: 'image', 
		// 동시파일업로드 수(이걸 지정한 수 만큼 여러파일을 한번ㄱㄱ)
		//parallelUploads: 2,
		// 다중업로드 기능(false 비활성화)
		uploadMultiple: false,
		//커넥션 타임아웃, 데이터가 클 경우 넉넉히 설정해야함
		timeout: 300000,
		// 파일 옆에 삭제 링크 표시
		addRemoveLinks: true,
		// 삭제버튼 표시 텍스트
		dictRemoveFile: '삭제'
	});

	// input file에 파일을 넣으면 drop zone에 자동 추가
	let fileInput = document.querySelector("#file-input");
	fileInput.addEventListener("change", function(event) {
		const files = event.target.files;

		// 선택된 파일들을 Dropzone에 추가
		for (let i = 0; i < files.length; i++) {
			dropzone.addFile(files[i]);
		}
		// 업로드한 후에 input file 초기화
		event.target.value = "";
	});
	// dropzone 관련 



	// toast ui editor 관련
	let initialValue = '';
	const editor = new toastui.Editor({
		el: document.querySelector('#editor'),
		previewStyle: 'vertical',
		height: '500px',
		initialValue: initialValue,
		initialEditType: 'wysiwyg',
		toolbarItems: [
			['heading', 'bold', 'italic', 'strike'],
			['hr', 'quote'],
			['ul', 'ol', 'task', 'indent', 'outdent'],
			['table', 'link'],
			['code', 'codeblock'],
			['scrollSync'],
		],
	});
	// toast ui editor 관련


	// 글자수 초과시 ... 으로 표시
	const cells = document.querySelectorAll(".to"); // 모든 <td> 요소 선택
	const maxLength = 20; // 허용할 최대 글자수

	cells.forEach(cell => {
		if (cell.textContent.length > maxLength) {
			cell.textContent = cell.textContent.substring(0, maxLength) + "...";
		}
	}); // 글자수 초과시 ... 으로 표시

	// 검색 관련 스크립트
	let $searchForm = $("#searchForm");
	let $searchBtn = $("#search-btn");
	$searchBtn.on("click", function () {
		let $parent = $(this).parents(".search-area");
		$parent.find(":input[name]").each(function (index, ipt) {

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
				let { mailMessageId, empMail, dmailTo, dmailCc, dmailBcc, dmailDate, dmailSubject, mailAttachmentList, dmailContent } = await resp.json();
				/* 
				document.querySelector('#mail-id').innerHTML = mailMessageId;
				document.querySelector('#mail-from').innerHTML = empMail;
				document.querySelector('#mail-to').value = dmailTo;
				document.querySelector('#mail-cc').value = dmailCc;
				document.querySelector('#mail-bcc').value = dmailBcc;
				document.querySelector('#mail-date').innerHTML = dmailDate;
				document.querySelector('#mail-title').value = dmailSubject;
				document.querySelector('#mail-content').innerHTML = dmailContent;

				//     				document.querySelector('#attach-area').innerHTML = mailAttachmentList[0].mailattachmentName;

				let code = '';

				mailAttachmentList.forEach((mailAttachment, i) => {
					if (mailAttachment.mailattachmentName) {
						code += `<a href='javascript:void(0)' onclick="getAttach('${mailAttachment.mailmessageId}','${mailAttachment.mailattachmentId}')">${mailAttachment.mailattachmentName}</a><br/>`;
					}

				})

				document.querySelector('#attach-area').innerHTML = code;
				*/

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
		let thisMessageId = document.querySelector('#mail-id').innerHTML;
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

	// 메일 주소 작성 input 태그 관련 이벤트
	const validateEmail = (email) => {
		const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		// .test는 정규식 객체에서 제공하는 거... true false 반환함
		return emailRegex.test(email);
	};

	function plusEmail(cnt, type, list, myInput) {
		let email = myInput.value.trim();
		if (email && validateEmail(email)) {
			list.insertAdjacentHTML("beforeend",
				`<div class="add-email" id="mail-${type}-${cnt}">
						<span class="email-id">${email}</span>
						<a href="javascript:void(0);" onclick="deleteElement('#mail-${type}-${cnt}')">
							<img class="icon" alt="삭제" src="${contextPath}/resources/images/delete-icon.png"/>
						</a>
					 </div>`)
			myInput.value = "";
		} else if (email) {
			alert("유효하지 않은 이메일 주소입니다.");
		}
	}

	let toCnt = 0;
	toInput.addEventListener("keydown", (e) => {

		if (e.key === " " || e.key === ",") {
			plusEmail(toCnt, "to", toList, toInput);
			toCnt++;
			e.preventDefault();
		}
	});

	toInput.addEventListener('blur', (e) => {
		plusEmail(toCnt, "to", toList, toInput);
		toCnt++;
		e.preventDefault();

	})

	let ccCnt = 0;
	ccInput.addEventListener("keydown", (e) => {

		if (e.key === " " || e.key === ",") {
			plusEmail(ccCnt, "cc", ccList, ccInput);
			ccCnt++;
			e.preventDefault();
		}
	});

	ccInput.addEventListener('blur', (e) => {
		plusEmail(ccCnt, "cc", ccList, ccInput);
		ccCnt++;
		e.preventDefault();

	})

	let bccCnt = 0;
	bccInput.addEventListener("keydown", (e) => {

		if (e.key === " " || e.key === ",") {
			plusEmail(bccCnt, "bcc", bccList, bccInput);
			bccCnt++;
			e.preventDefault();
		}
	});

	bccInput.addEventListener('blur', (e) => {
		plusEmail(bccCnt, "bcc", bccList, bccInput);
		bccCnt++;
		e.preventDefault();
	})
	
	

});