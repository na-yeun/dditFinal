function paging(page) {
	console.log(page);
	searchForm.page.value = page;
	searchForm.requestSubmit();
}


document.addEventListener('DOMContentLoaded', () => {
	let $searchForm = $("#searchForm");
	let $searchBtn = $(".search-btn");
	$searchBtn.on("click", function() {
		let $parent = $(this).parents(".search-area")// div태그를 찾은것
		$parent.find(":input[name]").each(function(index, ipt) {//부모의 자식중 input태그중에 name속성을 갖고있는 
			console.log(ipt.name, ipt.value)
			//$searchForm.find(`[name="${ipt.name}"]`).val(ipt.value); 아래꺼랑 똑같음
			if (searchForm[ipt.name]) { //해당 엘리먼트가 있으면
				searchForm[ipt.name].value = ipt.value
			}
			//searchForm.requestSubmit();
			$searchForm.submit();// 제이쿼리에서는requestSubmit(); =   submit()임
		})
	})

	// 모든 클릭 가능한 제목 요소를 선택
	const clickableTitles = document.querySelectorAll('.clickable-title-receive');

	clickableTitles.forEach((title) => {
		title.addEventListener('click', function() {
			// 클릭한 요소에 "clicked" 클래스 추가
			title.classList.add('clicked');
		});
	});

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


	const selectAllCheckbox = document.getElementById('selectAllReceive'); // 전체 선택 체크박스
	const rowCheckboxes = document.querySelectorAll('.rowCheckboxReceive'); // 개별 체크박스
	const deleteButton = document.getElementById('receiveMessageBtn'); // 삭제 버튼
	const modal = new bootstrap.Modal(document.getElementById('modalMessageReceive')); // 모달 초기화
	const messageRows = document.querySelectorAll('.clickable-title-receive'); // 제목 클릭 이벤트 대상

	// **1. 전체 선택 체크박스 이벤트**
	selectAllCheckbox.addEventListener('change', function() {
		const isChecked = this.checked;
		rowCheckboxes.forEach(checkbox => {
			checkbox.checked = isChecked;
		});
	});

	// **2. 개별 체크박스 상태 변경 시 전체 선택 체크박스 상태 업데이트**
	rowCheckboxes.forEach(checkbox => {
		checkbox.addEventListener('change', function() {
			const allChecked = Array.from(rowCheckboxes).every(cb => cb.checked);
			const someChecked = Array.from(rowCheckboxes).some(cb => cb.checked);
			selectAllCheckbox.checked = allChecked;
			selectAllCheckbox.indeterminate = !allChecked && someChecked;
		});
	});


	// **3. 삭제 버튼 클릭 이벤트**
	deleteButton.addEventListener('click', function() {
		// 선택된 체크박스 가져오기
		const selectedCheckboxes = document.querySelectorAll('.rowCheckboxReceive:checked');

		// 선택된 체크박스 값 수집 (String으로 유지)
		const selectedMessages = Array.from(selectedCheckboxes)
			.map(checkbox => {
				// 체크박스의 가장 가까운 tr 안에 있는 .clickable-title-receive 요소 찾기
				const tdElement = checkbox.closest('tr').querySelector('.clickable-title-receive');
				return tdElement ? tdElement.dataset.id : null; // rmesId 가져오기
			})
			.filter(id => id); // 유효한 ID만 유지

		// 선택된 메시지가 없으면 경고
		if (selectedMessages.length === 0) {
			Swal.fire({
				title: "항목 누락",
				text: "삭제할 항목을 선택하세요.",
				icon: "warning",
				confirmButtonText: "OK"
			});
			return;
		}
		Swal.fire({
			title: "삭제하시겠습니까?",
			text: "삭제된 쪽지는 복구할 수 없습니다!",
			icon: "warning",
			showCancelButton: true,
			confirmButtonColor: "#3085d6",
			cancelButtonColor: "#d33",
			confirmButtonText: "확인",
			cancelButtonText: "취소"
		}).then((result) => {
			if (result.isConfirmed) {
				// 삭제 요청
				const rmesId = document.getElementById("receiveHiddenId").value;
				fetch(`receive/${rmesId}/del`, {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
					},
					body: JSON.stringify({ rmesIds: selectedMessages }) // String 배열로 전송
				})
					.then(response => response.json())
					.then(data => {
						if (data.success) {
							Swal.fire({
								title: "삭제 성공!",
								text: "선택한 쪽지가 삭제되었습니다.",
								icon: "success",
								confirmButtonText: "확인"
							}).then(() => {
								window.location.reload(); // 새로고침
							});
						} else {
							Swal.fire({
								title: "삭제 실패!",
								text: data.message || "삭제에 실패했습니다.",
								icon: "error",
								confirmButtonText: "확인"
							});
						}
					})
					.catch(error => {
						console.error('Error:', error);
						Swal.fire({
							title: "오류 발생",
							text: "메시지를 삭제하는 중 오류가 발생했습니다.",
							icon: "error",
							confirmButtonText: "확인"
						});
					});
			}
		});
	});

	// **4. 제목 클릭 이벤트로 모달 데이터 표시**
	messageRows.forEach(row => {
		row.addEventListener('click', function() {
			const messageId = this.dataset.id; // 메시지 ID
			const isRead = this.dataset.read; // 읽음 여부
			const titleElement = this.querySelector('a'); // 제목 요소
			const iconElement = this.parentElement.querySelector('.message-icon i'); // 아이콘 요소


			// 모달에 데이터 바인딩
			const title = this.dataset.title;
			const content = this.dataset.content;
			const sender = this.dataset.sender;
			const emergency = this.dataset.emergency;

			

			// 읽음 상태가 "N"인 경우에만 서버 업데이트 요청
			if (isRead === 'N') { // DOM 상태 즉시 업데이트
				this.setAttribute('data-read', 'Y'); // 읽음 상태 변경
				titleElement.style.fontWeight = 'normal'; // 제목 읽음 스타일
				
				this.setAttribute('data-read', 'Y'); // 프론트 상태 업데이트

				if (iconElement) {
					iconElement.classList.remove('bi-envelope-fill'); // 읽지 않은 상태 아이콘 제거
					iconElement.classList.add('bi-envelope-open'); // 읽음 상태 아이콘 추가
					iconElement.style.color = '#6c757d'; // 아이콘 색상 회색으로 변경
				}
				fetch(`receive/up`, {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
					},
					body: JSON.stringify({ rmesId: messageId }), // 서버로 ID 전달
				})
					.then(response => response.json())
					.then(data => {
						if (data.success) {


						} else {
							console.error('읽음 상태 업데이트 실패:', data.message);
						}
					})
					.catch(error => {
						console.error('Error:', error);
					});
			}
			
			document.getElementById('rmesTitleModal').value = title;
			document.getElementById('rmesContentModal').value = content;
			document.getElementById('receiverIdModal').value = sender;

			const emergencyCheckbox = document.getElementById('remergencyYnModal');
			emergencyCheckbox.checked = (emergency === 'Y'); // Y일 때 체크
			emergencyCheckbox.disabled = true; // 사용자 상호작용은 비활성화

			// 모달 표시
			modal.show();
		});
	});



	// 선택된 메시지 ID를 가져오는 함수
	function getSelectedMessageData() {
		return Array.from(document.querySelectorAll('.rowCheckboxReceive:checked')).map((checkbox) => {
			const row = checkbox.closest('tr');
			const title = row.querySelector('.clickable-title-receive').dataset.title;
			const content = row.querySelector('.clickable-title-receive').dataset.content;
			const sender = row.querySelector('.clickable-title-receive').dataset.sender;
			const emergency = row.querySelector('.clickable-title-receive').dataset.emergency;
			const date = row.querySelector('.clickable-title-receive').dataset.date;
			const sendId = row.querySelector('.clickable-title-receive').dataset.send;

			return {
				id: checkbox.closest('tr').querySelector('.clickable-title-receive').dataset.id,
				title: title,
				content: content,
				sender: sender,
				emergency: emergency,
				date: date,
				sendId: sendId

			};
		});
	}
	//보관함을 누르면
	document.getElementById("messageBox").addEventListener("click", function() {
		const selectedMessages = getSelectedMessageData();

		if (selectedMessages.length === 0) {
			Swal.fire({
				title: "항목 누락",
				text: "선택된 쪽지가 없습니다.",
				icon: "warning",
				confirmButtonText: "OK"
			});
			return;
		}

		// Fetch API를 사용해 데이터를 전송
		fetch(`box/new`, {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify({
				messages: selectedMessages, // 전체 데이터 전송
			}),
		})
			.then((response) => response.json())
			.then((data) => {
				if (data.success) {
					Swal.fire({
						title: "보관함으로 이동 완료!",
						icon: "success",
						confirmButtonText: "확인"
					}).then(() => {
						window.location.reload(); // 새로고침
					});

				} else {
					alert(data.message || "보관함 이동 실패!");
				}
			})
			.catch((error) => {
				console.error("에러 발생:", error);
				Swal.fire({
					title: "보관함 이동실패!",
					text: "보관함 이동중 오류 발생!",
					icon: "error",
					confirmButtonText: "확인"
				});
			});
	});



	// 보관함 메시지 로드 함수 (예시)
	function loadStorageMessages() {
		fetch(`box`)
			.then((response) => response.json())
			.then((data) => {
				displayStorageMessages(data); // 받은 메시지를 화면에 표시하는 함수
			})
			.catch((error) => console.error("보관함 메시지 로드 실패:", error));
	}

	// 메시지 표시 (모달에 로드)
	function displayStorageMessages(messages) {
		const modalBody = document.querySelector("#modalMessageReceive .modal-body");
		modalBody.innerHTML = ""; // 기존 내용 초기화

		messages.forEach((message) => {
			const messageElement = `
				<div>
					<h5>${message.title}</h5>
					<p><strong>발신자:</strong> ${message.sender}</p>
					<p>${message.content}</p>
				</div>
				<hr>
			`;
			modalBody.innerHTML += messageElement;
		});
	}




});

