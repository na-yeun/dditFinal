

document.addEventListener('DOMContentLoaded', () => {

	document.querySelectorAll('.date-column').forEach(td => {
		const isoDate = td.textContent.trim(); // td에 들어 있는 ISO 날짜 가져오기
		if (isoDate) {
			const date = new Date(isoDate);
			if (!isNaN(date.getTime())) { // 유효한 날짜인지 확인
				// 수동으로 포맷 생성
				const year = String(date.getFullYear()).slice(2); // 연도 두 자리
				const month = String(date.getMonth() + 1).padStart(2, '0'); // 월 두 자리
				const day = String(date.getDate()).padStart(2, '0'); // 일 두 자리
				const hours = String(date.getHours()).padStart(2, '0'); // 시 두 자리
				const minutes = String(date.getMinutes()).padStart(2, '0'); // 분 두 자리

				// 원하는 형식으로 설정
				td.textContent = `${year}-${month}-${day} ${hours}:${minutes}`;
			} else {
				console.error(`Invalid date format: ${isoDate}`);
			}
		}
	});


	// 쪽지쓰기 모달 열기
	function openModal() {
		const myModal = new bootstrap.Modal(document.getElementById('modalScrollable'), {
			backdrop: "static", // 백드롭 고정
			keyboard: false     // ESC 키로 닫기 방지
		});
		myModal.show();

		// 모달 열릴 때 백드롭 관리
		document.body.classList.add('modal-open'); // 백드롭 효과 유지
	}

	const messageWriteButton = document.getElementById('messageWrite');
	if (messageWriteButton) {
		messageWriteButton.addEventListener('click', openModal);
	}

	// 주소록 모달 열기
	document.getElementById('addReceiverButton').addEventListener('click', function() {
		const secondModal = new bootstrap.Modal(document.getElementById('secondaryModal'), {
			backdrop: "static", // 백드롭 고정
			keyboard: false     // ESC 키로 닫기 방지
		});
		secondModal.show();
	});

	// 주소록 모달 닫기
	document.getElementById('main-recemapping').addEventListener('click', () => {
		closeSecondaryModal();
	});

	document.getElementById('closeBtn').addEventListener('click', closeSecondaryModal);

	function closeSecondaryModal() {
		// 주소록 모달 닫기
		const secondaryModalInstance = bootstrap.Modal.getInstance(document.getElementById('secondaryModal'));
		if (secondaryModalInstance) {
			secondaryModalInstance.hide();
		}

		// Bootstrap이 관리하는 백드롭 복원
		const mainModalInstance = bootstrap.Modal.getInstance(document.getElementById('modalScrollable'));
		if (mainModalInstance) {
			mainModalInstance._backdrop._config.isVisible = true; // 백드롭 표시
			mainModalInstance._backdrop.show();                  // 백드롭 다시 생성
		}

		// 사이드바 클릭 방지 복원
		document.body.classList.add('modal-open');
	}
	// 모든 쪽지 보내기 버튼에 이벤트 리스너 추가
	document.querySelectorAll('.messageWriteButton').forEach(button => {
		button.addEventListener('click', () => {
			const myModal = new bootstrap.Modal(document.getElementById('modalScrollable'), {
				backdrop: "static", // 백드롭 고정
				keyboard: false     // ESC 키로 닫기 방지
			});
			myModal.show();

			// 백드롭 관리
			document.body.classList.add('modal-open');
		});
	});
	// 쪽지쓰기 모달 닫기
	document.getElementById('modalScrollable').addEventListener('hidden.bs.modal', function() {
		// 모든 백드롭 제거
		document.querySelectorAll('.modal-backdrop').forEach(backdrop => backdrop.remove());
		document.body.classList.remove('modal-open');

		// 모달 데이터 초기화
		document.getElementById('smesTitle').value = ''; // 제목 초기화
		document.getElementById('smesContent').value = ''; // 내용 초기화
		document.getElementById('semergencyYn').checked = false; // 긴급 여부 초기화

		const receiverTagsBox = document.getElementById('receiverTags');
		while (receiverTagsBox.firstChild) {
			receiverTagsBox.removeChild(receiverTagsBox.firstChild); // 태그 초기화
		}
	});

	// 보강: 주소록 모달 닫힐 때 처리
	document.getElementById('secondaryModal').addEventListener('hidden.bs.modal', function() {
		const mainModalInstance = bootstrap.Modal.getInstance(document.getElementById('modalScrollable'));
		if (mainModalInstance) {
			mainModalInstance._backdrop._config.isVisible = true;
			mainModalInstance._backdrop.show();
		}
		document.body.classList.add('modal-open'); // 백드롭 유지
	});

	const smesTitle = document.getElementById("smesTitle");
	const smesContent = document.getElementById("smesContent");
	const titleMaxLength = 100;
	const contentMaxLength = 666;

	// 제목 입력 필드 글자수 검증
	smesTitle.addEventListener("input", () => {
		if (smesTitle.value.length > titleMaxLength) {
			smesTitle.value = smesTitle.value.substring(0, titleMaxLength);

			// SweetAlert2 경고창 띄우기
			Swal.fire({
				icon: 'warning',
				title: '글자수 제한 초과',
				text: `제목은 최대 ${titleMaxLength}자까지만 입력 가능합니다.`,
				confirmButtonText: '확인'
			});
		}
	});

	// 내용 입력 필드 글자수 검증
	smesContent.addEventListener("input", () => {
		if (smesContent.value.length > contentMaxLength) {
			smesContent.value = smesContent.value.substring(0, contentMaxLength);

			// SweetAlert2 경고창 띄우기
			Swal.fire({
				icon: 'warning',
				title: '글자수 제한 초과',
				text: `내용은 최대 ${contentMaxLength}자까지만 입력 가능합니다.`,
				confirmButtonText: '확인'
			});
		}
	});



		
		const companyId = document.getElementById("hiddenCompanyIdMessage").value;
	$(document).ready(function() {
		 // 검색 버튼과 인풋 높이를 맞추는 함수
		 function adjustHeight() {
			const searchInput = document.getElementById('search-input');
			const searchButton = document.getElementById('search-button');
			
			if (searchInput && searchButton) {
				const inputHeight = searchInput.offsetHeight;
				searchButton.style.height = `${inputHeight}px`; // 버튼 높이를 인풋과 동일하게 설정
			}
		}
	
		// 페이지 로드 시 실행
		adjustHeight();
	
		// 윈도우 크기가 변경될 때 실행 (반응형 처리)
		window.addEventListener('resize', adjustHeight);

		const url = `${contextPathSend}/${companyId}/directory/folder`; // url 변수 정의
		console.log("contextPath",contextPath)
		// JSTree 초기화 및 조직도 데이터 로드
		function getJson(searchKeyword = '') {
			$.ajax({
				type: 'get',
				url: url,
				
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
								directory.push({
									id: emp.empId,
									parent: item.departCode,
									text: `${emp.posiName} ${emp.empName}`, // 직급 + 이름 표시
									icon: false,
								});
							});
						}
					});
					console.log("url>>",url)

					// JSTree 업데이트
					const tree = $('#treeDirectory').jstree(true);
					if (tree) {
						tree.settings.core.data = directory; // 데이터 갱신
						tree.refresh(); // 트리 새로고침
					} else {
						// 트리가 없으면 초기화
						$('#treeDirectory').jstree({
							core: { data: directory },
							plugins: ['wholerow', 'checkbox'], // 다중 선택 가능
						});
					}
				},
				error: function() {
					alert('조직도를 불러오는 중 오류가 발생했습니다.');
				},
			});
		}

		// 첫 번째 모달의 "주소록" 버튼 클릭 시 두 번째 모달 열기
		$('#addReceiverButton').on('click', function() {
			const secondaryModal = new bootstrap.Modal(document.getElementById('secondaryModal'));
			secondaryModal.show();
			getJson(); // 조직도 로드
		});

		// 검색 버튼 이벤트 추가
		$('#search-button').on('click', function() {
			const searchKeyword = $('#search-input').val().trim();
			getJson(searchKeyword); // 검색어와 함께 데이터 로드
			console.log("searchKeyword::", searchKeyword);
		});


		// JSTree 내부에서 검색 기능 추가
		$('.search-wrapper button').on('click', function() {
			const searchInput = $('.search-wrapper input').val().trim(); // 검색창에 입력된 텍스트 가져오기
			if (searchInput === "") return; // 검색창이 비어있으면 return

			const tree = $('#treeDirectory').jstree(true); // JSTree 인스턴스 가져오기
			const foundNodes = []; // 검색 결과를 저장할 배열

			// JSTree의 모든 노드를 평면 구조로 가져옴
			const nodes = tree.get_json('#', { flat: true });

			if (Array.isArray(nodes)) {
				// 검색어가 포함된 노드를 찾음
				for (let i = 0; i < nodes.length; i++) {
					if (nodes[i].text && nodes[i].text.includes(searchInput)) {
						foundNodes.push(nodes[i]); // 일치하는 노드 저장
					}
				}
			} else {
				console.error("JSTree에서 노드를 가져오는데 실패했습니다.");
				return;
			}

			// 검색 결과 처리
			if (foundNodes.length > 1) {
				// 다중 결과 시 SweetAlert2로 라디오 버튼 선택 제공
				let options = "";
				foundNodes.forEach((node, index) => {
					options += `
                    <div>
                        <input type="radio" id="node_${index}" name="selectedNode" value="${index}">
                        <label for="node_${index}">${node.text}</label>
                    </div>`;
				});

				Swal.fire({
					title: "다중 결과! 선택해주세요.",
					html: `<form id="nodeForm">${options}</form>`,
					confirmButtonText: "확인",
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
							tree.deselect_all();
							tree.select_node(selectedNode.id);
						});
						console.log("선택된 노드: ", selectedNode.text);
					}
				});
			} else if (foundNodes.length === 1) {
				// 검색 결과가 한 개일 때
				const singleNode = foundNodes[0];
				tree.open_node(singleNode.parent, function() {
					tree.deselect_all();
					tree.select_node(singleNode.id);
				});
				console.log("선택된 노드: ", singleNode.text);
			} else {
				// 검색 결과가 없을 때
				Swal.fire({
					position: "center",
					icon: "error",
					title: "검색 결과가 없습니다.",
					showConfirmButton: false,
					timer: 3000
				});
			}
		});

		// 확인 버튼 클릭 시 수신자 ID 필드에 추가
		$('#main-recemapping').on('click', function() {
			const selectedNodes = $('#treeDirectory').jstree('get_selected', true);
			const selectedReceivers = selectedNodes
				.filter(node => !node.children.length) // 최하위 노드만 선택
				.map(node => ({ id: node.id, text: node.text })); // ID와 텍스트 가져오기

			const receiverTagsContainer = $('#receiverTags');

			selectedReceivers.forEach(receiver => {
				// 중복 확인
				if ($(`#receiverTags .receiver-tag[data-id="${receiver.id}"]`).length === 0) {
					// 태그 생성
					const tagHtml = `
                    <div class="receiver-tag" data-id="${receiver.id}" 
                        style="display: inline-flex; align-items: center; background-color: #e7fbfd; border: 0px solid #ced4da; border-radius: 15px; padding: 5px 10px; margin-right: 5px;">
                        ${receiver.text}
                        <button type="button" class="btn-close ms-2" aria-label="Close" 
                            style="font-size: 12px; margin-left: 5px; color: #fa3928; background-color: #e7fbfd;"></button>
                    </div>`;
					receiverTagsContainer.append(tagHtml);
				}
			});

			// 태그 삭제 기능
			$('#receiverTags').on('click', '.btn-close', function() {
				$(this).parent().remove();
			});

			const secondaryModalInstance = bootstrap.Modal.getInstance(document.getElementById('secondaryModal'));
			if (secondaryModalInstance) {
				secondaryModalInstance.hide();
			}

			const secondaryBackdrop = document.querySelector('.modal-backdrop');
			if (secondaryBackdrop) {
				secondaryBackdrop.remove();
			}

			// 백드롭 제거
			$('.modal-backdrop').last().remove();
		});
	});



	//답장버튼을 누르면 쪽지쓰기 모달이 열리면서 제목이랑 수신자가 맵핑이 되는 코드
	$('#sendReceiveBtn').on('click', function() {
		// 현재 열려 있는 모달 닫기
		const modalMessageReceive = bootstrap.Modal.getInstance(document.getElementById('modalMessageReceive'));
		if (modalMessageReceive) {
			modalMessageReceive.hide(); // 기존 모달 닫기
		}

		// 모달 닫힌 후 새로운 모달 열기
		$('#modalMessageReceive').on('hidden.bs.modal', function() {
			const sendMessageModal = new bootstrap.Modal(document.getElementById('modalScrollable'), {
				backdrop: "static",
				keyboard: false
			});
			sendMessageModal.show();
		});


		// 발신자 정보를 읽어옴
		const senderId = $('#receiveHiddenSendId').val(); // 수신쪽지 상세보기의 발신자 ID
		const senderName = $('#receiverIdModal').val(); // 발신자 이름 (추가적인 데이터가 필요하다면)console.log("senderName>>>>>",senderName)
		const messageTitle = $('#rmesTitleModal').val(); // 수신쪽지 상세보기의 제목
		const receiverTagsContainer = $('#receiverTags');

		// 중복 체크
		if ($(`#receiverTags .receiver-tag[data-id="${senderId}"]`).length === 0) {
			// 태그 생성 및 추가
			const tagHtml = `
            <div class="receiver-tag" data-id="${senderId}" 
                style="display: inline-flex; align-items: center; background-color: #e7fbfd; border: 1px solid #ced4da; border-radius: 15px; padding: 5px 10px; margin-right: 5px;">
                ${senderName || senderId} <!-- 이름 또는 ID 표시 -->
                <button type="button" class="btn-close ms-2" aria-label="Close" 
                    style="font-size: 12px; margin-left: 5px; color: #fa3928; background-color: #e7fbfd;"></button>
            </div>`;
			receiverTagsContainer.append(tagHtml);
		}

		// 태그 삭제 기능 (이벤트 중복 방지)
		$('#receiverTags').off('click', '.btn-close').on('click', '.btn-close', function() {
			$(this).parent().remove();
		});


		// 쪽지 보내기 모달에 값 설정
		$('#receiverTags').val(senderId); // 수신자 필드에 발신자 ID 설정

		$('#smesTitle').val(`[RE] ${messageTitle}`); // 제목 필드에 'RE:' 추가
		$('#smesContent').val(''); // 내용 필드는 비움





	});

	// 쪽지 전송 버튼 클릭 이벤트
	document.querySelector('#sendModal').addEventListener('click', function() {
		// 제목과 내용 값 가져오기
		const messageTitle = document.getElementById('smesTitle').value.trim();
		const messageContent = document.getElementById('smesContent').value.trim();
		// 폼 데이터 수집
		const formData = new FormData(document.getElementById('messageForm'));
		// 긴급 여부 체크박스 값 설정
		const semergencyYnCheckbox = document.getElementById("semergencyYn");
		formData.set("semergencyYn", semergencyYnCheckbox.checked ? "Y" : "N");
		// 수신자 태그에서 ID 수집
		const receivers = [];
		document.querySelectorAll('#receiverTags .receiver-tag').forEach(tag => {
			receivers.push(tag.getAttribute('data-id'));
		});

		if (receivers.length === 0) {
			Swal.fire({
				title: "수신자 누락!",
				text: "수신자를 선택하세요.",
				icon: "warning",
				confirmButtonText: "확인"
			});
			return;
		}
		if (!messageTitle) {
			Swal.fire({
				title: "제목누락!",
				text: "제목을 입력하세요.",
				icon: "warning",
				confirmButtonText: "확인"
			});
			return;
		}

		if (!messageContent) {
			Swal.fire({
				title: "내용누락!",
				text: "내용을 입력하세요.",
				icon: "warning",
				confirmButtonText: "확인"
			});
			return;
		}


		formData.append('receivers', JSON.stringify(receivers));

		// 폼 데이터를 URL 인코딩 형식으로 변환
		const formObject = {};
		formData.forEach((value, key) => {
			formObject[key] = value;
		});
		console.log("FormData Content:");
		for (const [key, value] of formData.entries()) {
			console.log(`${key}: ${value}`);
		}

		// 서버로 AJAX 요청 전송
		Swal.fire({
			title: "쪽지를 전송하시겠습니까?",
			text: "전송된 쪽지는 수정할 수 없습니다!",
			icon: "question",
			showCancelButton: true,
			confirmButtonColor: "#3085d6",
			cancelButtonColor: "#d33",
			confirmButtonText: "확인",
			cancelButtonText: "취소"
		}).then((result) => {
			if (result.isConfirmed) {
				$.ajax({
					url: `send`,
					method: 'POST',
					contentType: 'application/x-www-form-urlencoded', // URL 인코딩으로 전송
					data: $.param(formObject), // URL 쿼리 문자열 형식으로 변환
					success: function(data) {
						if (data.result === 'OK') {
							Swal.fire({
								title: "전송 성공!",
								text: "쪽지가 성공적으로 전송되었습니다.",
								icon: "success",
								confirmButtonText: "확인"
							}).then(() => {
								location.reload(); // 페이지 새로고침
							});
						} else {
							Swal.fire({
								title: "전송 실패",
								text: `쪽지 전송에 실패했습니다: ${data.error || '알 수 없는 오류'}`,
								icon: "error",
								confirmButtonText: "확인"
							});
						}
					},
					error: function(xhr, status, error) {
						console.error('Error:', error);
						Swal.fire({
							title: "오류 발생",
							text: "서버 요청 중 문제가 발생했습니다.",
							icon: "error",
							confirmButtonText: "확인"
						});
					}
				});
			}
		});
	});









});