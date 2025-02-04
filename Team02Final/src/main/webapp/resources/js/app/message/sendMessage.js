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

	// 클릭 이벤트로 데이터 가져오고 모달 업데이트
	const titles = document.querySelectorAll('.clickable-title a');

	titles.forEach(title => {
		title.addEventListener('click', function() {
			const smesId = this.parentElement.dataset.id;

			// AJAX 요청: 메시지 상세 데이터 가져오기
			fetch(`send/${smesId}`)
				.then(response => {
					if (!response.ok) {
						throw new Error('Network response was not ok');
					}
					return response.json();
				})
				.then(data => {
					console.log('Received data:', data); // 디버깅용

					// 데이터를 모달에 업데이트
					const semergencyYnInput = document.getElementById('semergencyYnModal');
					const smesTitleInput = document.getElementById('smesTitleModal');
					const smesContentTextarea = document.getElementById('smesContentModal');
					const receiverTags = document.getElementById('receiverTagsModal');

					// 데이터 업데이트
					if (semergencyYnInput) {
						semergencyYnInput.checked = data.semergencyYn === 'Y'; // 긴급 여부 체크박스
					}
					console.log(semergencyYnInput.checked = data.semergencyYn === 'Y')
					if (smesTitleInput) {
						smesTitleInput.value = data.smesTitle || ''; // 제목
					}

					if (smesContentTextarea) {
						smesContentTextarea.value = data.smesContent || ''; // 내용
					}

					if (receiverTags) {
						receiverTags.innerHTML = ''; // 기존 태그 초기화

						// 수신자 태그 추가
						if (data.receiverNames) {
							const receiverArray = data.receiverNames.split(', '); // 수신자 문자열을 배열로 분리
							receiverArray.forEach(receiverName => {
								const tag = document.createElement('span');
								tag.className = 'badge bg-primary';
								tag.textContent = receiverName; // 수신자 이름 표시
								receiverTags.appendChild(tag);
							});
						}
					}
					console.log(semergencyYnInput.value)
					console.log(smesTitleInput)
					// 모달 열기
					const modal = new bootstrap.Modal(document.getElementById('modalMessageSend'));
					modal.show();
				})
				.catch(error => {
					console.error('Error:', error);
					alert('메시지 데이터를 가져오지 못했습니다.');
				});
		});
	});






	const selectAllCheckbox = document.getElementById("selectAll");
	const rowCheckboxes = document.querySelectorAll(".rowCheckbox");

	// "전체 선택" 체크박스 클릭 시
	selectAllCheckbox.addEventListener("change", function() {
		rowCheckboxes.forEach((checkbox) => {
			checkbox.checked = selectAllCheckbox.checked;
		});
	});

	// 개별 행 체크박스 클릭 시
	rowCheckboxes.forEach((checkbox) => {
		checkbox.addEventListener("change", function() {
			// 전체 선택 상태 업데이트
			const allChecked = Array.from(rowCheckboxes).every((box) => box.checked);
			const someChecked = Array.from(rowCheckboxes).some((box) => box.checked);

			selectAllCheckbox.checked = allChecked;
			selectAllCheckbox.indeterminate = !allChecked && someChecked;
		});
	});


	document.querySelector('#sendMessageDelete').addEventListener('click', function() {
		// 체크된 체크박스를 가져오기
		const checkedBoxes = document.querySelectorAll('.rowCheckbox:checked');
		if (checkedBoxes.length === 0) {
			Swal.fire({
				title: "항목 누락!",
				text: "삭제할 항목을 선택하세요.",
				icon: "warning",
				confirmButtonText: "확인"
			});
			return;
		}

		// 체크된 체크박스에서 smesId 수집
		const selectedIds = Array.from(checkedBoxes).map(checkbox => {
			return checkbox.closest('tr').querySelector('.clickable-title').dataset.id;
		});


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

				const smesId = this.parentElement.dataset.id;
				// AJAX 요청으로 삭제 요청 보내기
				fetch(`send/${smesId}/del`, {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
					},
					body: JSON.stringify({ smesIds: selectedIds }), // 선택된 ID를 서버로 전송
				})
					.then(response => {
						if (!response.ok) {
							throw new Error('삭제 요청 실패');
						}
						return response.json();
					})
					.then(data => {
						if (data.success) {
							// 삭제된 항목을 테이블에서 제거
							checkedBoxes.forEach(checkbox => {
								const row = checkbox.closest('tr');
								row.remove();
							});
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
							icon: "error",
							title: "서버 오류!",
							text: "서버 오류로 삭제에 실패했습니다.",

						});
					});
			}
		});
	});

});