/**
 * 
 */

// 메일 리스트 삭제하는 메소드..
function deleteElement(deleteMailElement) {
	let deleteElement = document.querySelector(deleteMailElement);
	deleteElement.remove();
};


document.addEventListener("DOMContentLoaded", async function() {
	
	// contextpath
	const contextPath = document.querySelector('#page-title').dataset.contextpath;
	
	// dropzone 관련 
	Dropzone.autoDiscover = false; // Dropzone 자동 탐색 비활성화

	const dropzone = new Dropzone("div.dropzone", {
		url: "#",
		// 자동보내기(true면 올리자마자 서버요청, false면 버튼 눌러야 전송)
		autoProcessQueue: false,
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
	
	// 드랍존 에러(용량 초과)
	dropzone.on("error", (f, m) => {
		if (f.size > dropzone.options.maxFilesize * 1024 * 1024) {
	        console.error(`파일 크기가 초과되었습니다: ${f.name}`);
			Swal.fire({
				title: "용량 초과",
				html: "파일 크기가 너무 큽니다. 최대 25MB까지 업로드할 수 있습니다.",
				icon: "error",
				confirmButtonText: "확인"
			})
	    } else {
	        console.error(`업로드 중 에러 발생: ${m}`);
	    }
	});
	// dropzone 관련


	// toast ui editor 관련
	let contentValue = document.querySelector('#mailContent').value;
	let initialValue = contentValue;
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
	
	// 콜렉트
	// 취소 버튼 콜렉터
	const cancelBtn = document.querySelector('#cancel-btn');
	// 임시 저장 버튼 콜렉터
	//const saveBtn = document.querySelector('#save-btn');
	// 전송 버튼 콜렉터
	const submitBtn = document.querySelector('#submit-btn');
	// 받는 사람 적는 input 태그
	const toInput = document.querySelector('#to-area');
	const ccInput = document.querySelector('#cc-area');
	const bccInput = document.querySelector('#bcc-area');

	const toList = document.querySelector('#to-list');
	const ccList = document.querySelector('#cc-list');
	const bccList = document.querySelector('#bcc-list');

	const subjectInput = document.querySelector('#mailSubject');
	
	// 발표용 데이터 넣는 버튼
	const insertDataBtn = document.querySelector('#insert-pre-btn');
	insertDataBtn.addEventListener("click", () => {
		subjectInput.value="요청하신 서류 보내드립니다.";
		editor.setHTML("안녕하세요. OO그룹 XXX 입니다. <br> 요청하신 파일 첨부하여 전송해드립니다!");
	})
	
	
	// 하단 버튼 관련 이벤트 START
	cancelBtn.addEventListener("click", () => {
		Swal.fire({
			title: "취소 하시겠습니까?",
			text: "취소하면 메일 내용이 저장되지 않습니다.",
			//showDenyButton: true,
			showCancelButton: true,
			confirmButtonText: "취소",
			//denyButtonText: "저장하지 않고 취소하기",
			cancelButtonText: "닫기",
		})
			.then(async (result) => {
				if (result.isConfirmed) {
					if (document.referrer && document.referrer !== window.location.href) {
						window.location.href = document.referrer;
					} else {
						// referrer가 없으면 기본 뒤로 가기
						window.history.back();
					}
				}
					// 저장하고 뒤로하기

					// 임시메일 저장하는 코드 필요
					/*
					var contentHtml = editor.getHTML();
					let thisMailSubject = subjectInput.value;


					// 1. 받는 사람, 참조, 숨은 참조에 작성된 사람들을 배열로 모으기
					let toEmailElements = document.querySelectorAll('#to-list .email-id');
					let toEmailArray = Array.from(toEmailElements, element => element.textContent);
					let ccEmailElements = document.querySelectorAll('#cc-list .email-id');
					let ccEmailArray = Array.from(ccEmailElements, element => element.textContent);
					let bccEmailElements = document.querySelectorAll('#bcc-list .email-id');
					let bccEmailArray = Array.from(bccEmailElements, element => element.textContent);



					// 2. 배열로 모은 사람들과, 제목, 첨부파일, 메일 내용을 모으기
					let formData = new FormData();

					toEmailArray.forEach(to => formData.append("mailTo", to));
					ccEmailArray.forEach(cc => formData.append("mailCc", cc));
					bccEmailArray.forEach(bcc => formData.append("mailBcc", bcc));

					formData.append("mailSubject", thisMailSubject);
					formData.append("mailContent", contentHtml);

					dropzone.files.forEach(file => {
						formData.append("mailFiles", file); // Dropzone 파일 객체 추가
					});
					let url = window.location.href + "/saveDraft";

					Swal.fire({
						title: "처리 중입니다...",
						html: "잠시만 기다려주세요.",
						allowOutsideClick: false,
						didOpen: () => {
							Swal.showLoading(); // 로딩 애니메이션 표시
						},
					});


					// 3. controller에게 비동기 요청 보내기
					let resp = await fetch(
						url,
						{
							method: 'post',
							body: formData
						}
					);

					if (resp.ok) {
						Swal.fire({
							title: "저장이 완료되었습니다.",
							icon: "success",
							confirmButtonText: "확인"
						}).then((result) => {
							if (result.isConfirmed) {
								if (document.referrer && document.referrer !== window.location.href) {
									// 이전 페이지로 이동
									window.location.href = document.referrer;
								} else {
									// 기본 뒤로 가기
									window.history.back();
								}
							}
						});

					} else {
						// 저장 실패시
						Swal.fire({
							title: "저장에 실패했습니다.",
							text: "다시 시도해주세요.",
							icon: "error",
							confirmButtonText: "확인"
						})
					}
					 
				} else if (result.isDenied) {
					// 저장하지 않고 뒤로가기
					if (document.referrer && document.referrer !== window.location.href) {
						window.location.href = document.referrer;
					} else {
						// referrer가 없으면 기본 뒤로 가기
						window.history.back();
					}
				}
				*/
				// 걍 창 닫기
			}); // 취소 하시겠습니까?
	});
	
	/*
	saveBtn.addEventListener("click", () => {
		Swal.fire({
			title: "메일을 저장하시겠습니까?",
			text: "저장된 메일은 임시보관함에서 확인할 수 있습니다.",
			// candel 버튼 (기본값 false, 취소버튼 안 보임)
			showCancelButton: true,
			// confirm 버튼 텍스트
			confirmButtonText: '저장',
			// cancel 버튼 텍스트
			cancelButtonText: '취소',
			// confirm 버튼 색 지정(승인)
			confirmButtonColor: 'blue',
		}).then(async (result) => {
			if (result.isConfirmed) {
				var contentHtml = editor.getHTML();
				let thisMailSubject = subjectInput.value;


				// 1. 받는 사람, 참조, 숨은 참조에 작성된 사람들을 배열로 모으기
				let toEmailElements = document.querySelectorAll('#to-list .email-id');
				let toEmailArray = Array.from(toEmailElements, element => element.textContent);
				let ccEmailElements = document.querySelectorAll('#cc-list .email-id');
				let ccEmailArray = Array.from(ccEmailElements, element => element.textContent);
				let bccEmailElements = document.querySelectorAll('#bcc-list .email-id');
				let bccEmailArray = Array.from(bccEmailElements, element => element.textContent);



				// 2. 배열로 모은 사람들과, 제목, 첨부파일, 메일 내용을 모으기
				let formData = new FormData();

				toEmailArray.forEach(to => formData.append("mailTo", to));
				ccEmailArray.forEach(cc => formData.append("mailCc", cc));
				bccEmailArray.forEach(bcc => formData.append("mailBcc", bcc));

				formData.append("mailSubject", thisMailSubject);
				formData.append("mailContent", contentHtml);

				dropzone.files.forEach(file => {
					formData.append("mailFiles", file); // Dropzone 파일 객체 추가
				});

				let url = window.location.href + "/saveDraft";

				Swal.fire({
					title: "처리 중입니다...",
					html: "잠시만 기다려주세요.",
					allowOutsideClick: false,
					didOpen: () => {
						Swal.showLoading(); // 로딩 애니메이션 표시
					},
				});

				let resp = await fetch(
					url,
					{
						method: 'post',
						body: formData
					}
				);

				if (resp.ok) {
					Swal.fire({
						title: "저장이 완료되었습니다.",
						icon: "success",
						confirmButtonText: "확인"
					});
				} else {
					// 저장 실패시
					Swal.fire({
						title: "저장에 실패했습니다.",
						text: "다시 시도해주세요.",
						icon: "error",
						confirmButtonText: "확인"
					});
				}
			}
		})
	});
	*/

	submitBtn.addEventListener("click", async (e) => {
		e.preventDefault();

		var contentHtml = editor.getHTML();
		let thisMailSubject = subjectInput.value;

		// 1. 받는 사람, 참조, 숨은 참조에 작성된 사람들을 배열로 모으기
		let toEmailElements = document.querySelectorAll('#to-list .email-id');
		let toEmailArray = Array.from(toEmailElements, element => element.dataset.mailAddress);
		let ccEmailElements = document.querySelectorAll('#cc-list .email-id');
		let ccEmailArray = Array.from(ccEmailElements, element => element.dataset.mailAddress);
		let bccEmailElements = document.querySelectorAll('#bcc-list .email-id');
		let bccEmailArray = Array.from(bccEmailElements, element => element.dataset.mailAddress);

		let messageId = document.querySelector('#message-id').value;

		// 2. 배열로 모은 사람들과, 제목, 첨부파일, 메일 내용을 모으기
		let formData = new FormData();

		toEmailArray.forEach(to => formData.append("mailTo", to));
		ccEmailArray.forEach(cc => formData.append("mailCc", cc));
		bccEmailArray.forEach(bcc => formData.append("mailBcc", bcc));

		formData.append("mailSubject", thisMailSubject);
		formData.append("mailContent", contentHtml);
		if(messageId){
			formData.append("messageId", messageId);
		}
		dropzone.files.forEach(file => {
			formData.append("mailFiles", file); // Dropzone 파일 객체 추가
		});
		
		
		
		let url = `${contextPath}/${companyId}/mail/send`;

		Swal.fire({
			title: "처리 중입니다...",
			html: "잠시만 기다려주세요.",
			allowOutsideClick: false,
			didOpen: () => {
				Swal.showLoading(); // 로딩 애니메이션 표시
			},
		});
		
		// 3. controller에게 비동기 요청 보내기
		let resp = await fetch(
			url,
			{
				method: 'post',
				body: formData
			}
		);

		if (resp.ok) {
			let jsonObj = await resp.json();
			let status = jsonObj.status;
			if(status=='success'){
				Swal.fire({
					title: "메일 발송 성공",
					text: "보낸 메일함을 확인해주세요.",
					icon: "success"
				}).then((result) => {
					location.href = url;
				});
			} else if (status=='fail'){
				let {mailTo, mailSubject} = jsonObj.errors;
				if(mailTo){
					document.querySelector('#mailToError').innerHTML = mailTo;
				}
				if(mailSubject){
					document.querySelector('#mailSubjectError').innerHTML = mailSubject;
				}
				Swal.fire({
					title: "정보 누락으로 실패",
					html: "누락된 정보가 있습니다. <br> 누락된 정보를 확인 후 다시 시도해주세요.",
					icon: "error"
				});
				
			} else if (status=='error') {
				Swal.fire({
					title: "서버 오류",
					html: "서버 오류로 메일 발송에 실패했습니다. <br> 다시 시도해주세요.",
					icon: "error"
				});
			}
			
			

		} else {
			console.log("실패");
			Swal.fire({
				title: "서버 오류",
				html: "서버 오류로 메일 발송에 실패했습니다. <br> 다시 시도해주세요.",
				icon: "error"
			});
		}
	});
	// 하단 버튼 관련 이벤트 END


	// 메일 주소 작성 input 태그 관련 이벤트 START
	const validateEmail = (email) => {
		const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		// .test는 정규식 객체에서 제공하는 거... true false 반환함
		return emailRegex.test(email);
	};

	window.plusEmail = async function plusEmail(cnt, type, list, myInputValue) {
		if (myInputValue && validateEmail(myInputValue)) {
			let innerHtmlCode = '';
			// 여기에서 비동기 요청을 통해서 이름이랑 메일을 찍자~~~~
			let searchMailUrl = `${contextPath}/${companyId}/mail/send/search?empMail=${myInputValue}`;
			
			let resp = await fetch(searchMailUrl, {
				method:'get',
				headers : {
					'content-type' : 'application/json'
				}
			});
			if(resp.ok){
				let {searchEmpName, searchEmpMail, searchEmpDepartmentName} = await resp.json();
				if(searchEmpName && searchEmpMail){
					innerHtmlCode += `<div class="add-email ${type}-mail-cnt" id="mail-${type}-${cnt}">
									<span class="email-id badge bg-label-info" data-mail-address="${searchEmpMail}">${searchEmpDepartmentName} ${searchEmpName}</span>
									<a href="javascript:void(0);" onclick="deleteElement('#mail-${type}-${cnt}')">
										<img class="icon" alt="삭제" src="${contextPath}/resources/images/delete-icon.png"/>
									</a>
								 </div>`
				} else {
					innerHtmlCode += `<div class="add-email ${type}-mail-cnt" id="mail-${type}-${cnt}">
									<span class="email-id badge bg-label-info" data-mail-address="${myInputValue}">${myInputValue}</span>
									<a href="javascript:void(0);" onclick="deleteElement('#mail-${type}-${cnt}')">
										<img class="icon" alt="삭제" src="${contextPath}/resources/images/delete-icon.png"/>
									</a>
								 </div>`
				}
			} else {
				innerHtmlCode += `<div class="add-email ${type}-mail-cnt" id="mail-${type}-${cnt}">
									<span class="email-id badge bg-label-info" data-mail-address="${myInputValue}">${myInputValue}</span>
									<a href="javascript:void(0);" onclick="deleteElement('#mail-${type}-${cnt}')">
										<img class="icon" alt="삭제" src="${contextPath}/resources/images/delete-icon.png"/>
									</a>
								 </div>`
			}
			
			list.insertAdjacentHTML("beforeend",innerHtmlCode)
			
		} else if (email) {
			Swal.fire({
				icon : "error",
				title: "메일 형식을 다시 확인해주세요."
			});
		}
	}
	

	toInput.addEventListener("keydown", (e) => {
		let toCnt = document.querySelectorAll('.to-mail-cnt').length;
		let toInputValue = toInput.value.trim();
		if (e.key === ",") {
			plusEmail(toCnt, "to", toList, toInputValue);
			toInput.value = "";
			e.preventDefault();
		}
	});

	toInput.addEventListener('blur', (e) => {
		let toCnt = document.querySelectorAll('.to-mail-cnt').length;
		let toInputValue = toInput.value.trim();
		plusEmail(toCnt, "to", toList, toInputValue);
		toInput.value = "";
		e.preventDefault();
	})

	ccInput.addEventListener("keydown", (e) => {
		let ccCnt = document.querySelectorAll('.cc-mail-cnt').length;
		let ccInputValue = ccInput.value.trim();
		if (e.key === ",") {
			plusEmail(ccCnt, "cc", ccList, ccInputValue);
			ccInput.value = "";
			e.preventDefault();
		}
	});

	ccInput.addEventListener('blur', (e) => {
		let ccCnt = document.querySelectorAll('.cc-mail-cnt').length;
		let ccInputValue = ccInput.value.trim();
		plusEmail(ccCnt, "cc", ccList, ccInputValue);
		ccInput.value = "";
		e.preventDefault();

	})

	bccInput.addEventListener("keydown", (e) => {
		let bccCnt = document.querySelectorAll('.bcc-mail-cnt').length;
		let bccInputValue = bccInput.value.trim();
		if (e.key === ",") {
			plusEmail(bccCnt, "bcc", bccList, bccInputValue);
			bccInput.value="";
			e.preventDefault();
		}
	});

	bccInput.addEventListener('blur', (e) => {
		let bccCnt = document.querySelectorAll('.bcc-mail-cnt').length;
		let bccInputValue = bccInput.value.trim();
		plusEmail(bccCnt, "bcc", bccList, bccInputValue);
		bccInput.value="";
		e.preventDefault();
	})
	// 메일 주소 작성 input 태그 관련 이벤트 END
	
	
	
	// 검색 관련 코드 START
	const searchModal = new bootstrap.Modal('#search-modal');
	
	window.openSearchModal = function () {
		getJson();
        searchModal.show();
    };
	
	// 검색 버튼 클릭 이벤트
	let searchBtns = document.querySelectorAll('.search-btns');
	let listDiv = null;
	searchBtns.forEach(element=>{
		element.addEventListener("click", e => {
			let closestTrTag = e.target.closest('tr');
			listDiv = closestTrTag.querySelector('.list-area');
			openSearchModal();
		});
	}); // 검색 버튼 클릭 이벤트
	
	// jstree(검색 버튼 눌러서 조직도)
	// JSTree 초기화 및 조직도 데이터 로드
	function getJson(searchKeyword = '') {
		$.ajax({
			type: 'get',
			url: `${contextPath}/${companyId}/directory/folder`,
			data: { searchKeyword }, // 검색 키워드 전송
			dataType: 'json',
			success: function(data) {
				const directory = [];
				$.each(data, function(idx, item) {
					directory.push({
						id: item.departCode,
						parent: item.departParentcode || '#',
						text: item.departName,
					});

					if (item.employeeList && item.employeeList.length > 0) {
						$.each(item.employeeList, function(empIdx, emp) {
							let departmentName = item.departName?item.departName:"";
							
							let empMail = emp.empMail?emp.empMail:"";
							
							directory.push({
								id: emp.empId,
								parent: item.departCode,
								text: `${emp.posiName} ${emp.empName}`, // 직급 + 이름 표시
								department: departmentName,
								empMail : empMail,
								icon: false,
							});
						});
					}
				});

				// JSTree 업데이트
				const mailtree = $('#mail-tree-directory').jstree(true);
				if (mailtree) {
			        mailtree.settings.core.data = directory.map(node => ({
			            id: node.id,
			            parent: node.parent,
			            text: node.text,
			            icon: node.icon,
			            data: { 
							department: node.department,
							empMail : node.empMail

						}
			        }));
			        mailtree.refresh(); // 트리 새로고침
			    } else {
			        // 트리가 없으면 초기화
			        $('#mail-tree-directory').jstree({
			            core: {
			                data: directory.map(node => ({
			                    id: node.id,
			                    parent: node.parent,
			                    text: node.text,
			                    icon: node.icon,
			                    data: { 
									department: node.department,
									empMail : node.empMail
								} // 부서명 추가
			                }))
			            },
			            plugins: ['wholerow', 'checkbox'] // 플러그인 설정
			        });
			    }
			},
			error: function() {
				alert('조직도를 불러오는 중 오류가 발생했습니다.');
			},
		});
	}

	// JSTree 내부에서 검색 기능 추가
	$('#search-btn').on('click', function(e) {
		const searchName = $('#search-input-area').val().trim(); // 검색창에 입력된 텍스트 가져오기
		if (searchName === "") return; // 검색창이 비어있으면 return

		const tree = $('#mail-tree-directory').jstree(true); // JSTree 인스턴스 가져오기
		const foundNodes = []; // 검색 결과를 저장할 배열

		// JSTree의 모든 노드를 평면 구조로 가져옴
		const nodes = $('#mail-tree-directory').jstree(true).get_json('#', { flat: true });

		if (Array.isArray(nodes)) {
			// 검색어가 포함된 노드를 찾음
			for (let i = 0; i < nodes.length; i++) {
				
				if (nodes[i].text && nodes[i].text.includes(searchName)) {
					foundNodes.push(nodes[i]); // 일치하는 노드 저장
				}
			}
		} else {
			console.error("JSTree에서 노드를 가져오는데 실패했습니다.");
			return;
		}
		
		// 기존 선택된 노드 저장
		let previouslySelectedNodes = tree.get_selected();
		if(previouslySelectedNodes){
			// 검색 결과 처리
			if (foundNodes.length > 1) {
				// 다중 결과 시 SweetAlert2로 라디오 버튼 선택 제공
				let options = "";
				foundNodes.forEach((node, index) => {
					const departmentName = node.data?.department || `<b>부서없음</b>`;
					options += `
	                <div>
	                    <input type="radio" id="node_${index}" name="selectedNode" value="${index}">
	                    <label for="node_${index}"><b>${departmentName}</b> ${node.text}</label>
	                </div>`;
				});
	
				Swal.fire({
					title: "선택해주세요.",
					html: `<form id="nodeForm">${options}</form>`,
					confirmButtonText: "확인",
					cancelButtonText: "취소",
					showCancelButton: true,
					focusConfirm: false,
					preConfirm: () => {
						const selectedValue = document.querySelector('input[name="selectedNode"]:checked');
						if (selectedValue) {
							return foundNodes[selectedValue.value];
						} else {
							Swal.showValidationMessage("하나를 선택해주세요.");
						}
					}
				}).then((result) => {
					if (result.isConfirmed && result.value) {
						const selectedNode = result.value;
						tree.open_node(selectedNode.parent, function() {
							const updatedSelection = [...previouslySelectedNodes, selectedNode.id];
							
							tree.deselect_all();
							tree.select_node(updatedSelection);
							//tree.select_node(selectedNode.id);
						});
						$('#search-input-area').val('');
					}
				});
			} else if (foundNodes.length === 1) {
				// 검색 결과가 한 개일 때
				const singleNode = foundNodes[0];
				tree.open_node(singleNode.parent, function() {
					const updatedSelection = [...previouslySelectedNodes, singleNode.id]; // 병합
			        tree.deselect_all();
			        tree.select_node(updatedSelection);
				});
				$('#search-input-area').val('');
			} else {
				// 검색 결과가 없을 때
				Swal.fire({
					position: "center",
					icon: "error",
					title: "검색 결과가 없습니다.",
					showConfirmButton: false,
					timer: 3000
				});
				$('#search-input-area').val('');
			}
		}
	});

	// 추가 버튼 클릭 시 수신자 ID 필드에 추가
	$('#confirm-emp-btn').on('click', function() {
		const selectedNodes = $('#mail-tree-directory').jstree('get_selected', true);
		
		const selectedReceivers = selectedNodes
			.filter(node => !node.children.length) // 최하위 노드만 선택
			.map(node => ({
				id: node.id
				, text: node.text
				, empMail : node.data?.empMail || ""
			})); // ID와 텍스트 가져오기

		selectedReceivers.forEach(receiver => {
			// 중복 확인
			if ($(`#receiverTags .receiver-tag[data-id="${receiver.id}"]`).length === 0) {
				// 태그 생성
				if (listDiv) {
					let cnt = listDiv.querySelectorAll('div').length;
					let listDivId = listDiv.id;
					let type = listDivId.split('-')[0];
					
					const tagHtml = `
						<div class="add-email ${type}-mail-cnt" id="mail-${type}-${cnt}">
							<span class="email-id badge bg-label-info" data-mail-address="${receiver.empMail}">${receiver.text}</span>
							<a href="javascript:void(0);" onclick="deleteElement('#mail-${type}-${cnt}')">
								<img class="icon" alt="삭제" src="${contextPath}/resources/images/delete-icon.png"/>
							</a>
						 </div>`;
					
					listDiv.insertAdjacentHTML("beforeend",tagHtml);
					searchModal.hide();
				}
			}
		});
		
		$('#search-modal').on('hidden.bs.modal', function () {
		    const tree = $('#mail-tree-directory').jstree(true);
		    if (tree) {
		        tree.deselect_all(); // 모든 선택된 노드 해제
		    }
		});
		
	});
	// 검색 관련 코드 END
	

});