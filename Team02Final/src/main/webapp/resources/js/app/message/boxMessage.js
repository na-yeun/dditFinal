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
	// 모달 초기화
	const boxMessageModalElement = document.getElementById('modalMessageBox'); // HTML에서 모달의 ID와 일치하도록 설정
	const boxMessageModal = new bootstrap.Modal(boxMessageModalElement); // Bootstrap Modal 객체 생성

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
	const messageBox = document.querySelectorAll('.clickable-title-box'); // 제목 클릭 이벤트 대상
	const selectAllCheckbox = document.getElementById('selectAllBox'); // 전체 선택 체크박스
	const rowCheckboxes = document.querySelectorAll('.rowCheckboxBox'); // 개별 체크박스
	const deleteButton = document.getElementById('boxMessageBtn'); // 삭제 버튼

	// 1. 전체 선택/해제 이벤트
	selectAllCheckbox.addEventListener('change', function() {
		const isChecked = this.checked;
		rowCheckboxes.forEach(checkbox => {
			checkbox.checked = isChecked;
		});
	});

	// 2. 개별 체크박스 상태 변경 시 전체 선택 체크박스 상태 업데이트
	rowCheckboxes.forEach(checkbox => {
		checkbox.addEventListener('change', function() {
			const allChecked = Array.from(rowCheckboxes).every(cb => cb.checked);
			const someChecked = Array.from(rowCheckboxes).some(cb => cb.checked);
			selectAllCheckbox.checked = allChecked;
			selectAllCheckbox.indeterminate = !allChecked && someChecked;
		});
	});

	// 3. 삭제 버튼 클릭 시 처리
	deleteButton.addEventListener('click', function() {
		const selectedIds = Array.from(document.querySelectorAll('.rowCheckboxBox:checked'))
			.map(checkbox => checkbox.closest('tr').dataset.id); // tr에 ID가 저장되어 있다고 가정

		if (selectedIds.length === 0) {
			Swal.fire({
				title: "항목 누락!",
				text: "삭제할 항목을 선택하세요.",
				icon: "warning",
				confirmButtonText: "확인"
			});
			return;
		}
		
		
		// 삭제 요청 전송
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

				fetch(`box/${selectedIds}/del`, {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
					},
					body: JSON.stringify({ mboxIds: selectedIds }),
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
								title: "삭제중 오류 발생!",
								text: data.message || "삭제중 오류발생!",
								icon: "error",
								confirmButtonText: "확인"
							});
						}
					})
					.catch(error => {
						console.error('삭제 요청 오류:', error);
						Swal.fire({
								title: "삭제 실패!",
								text: data.message || "삭제중 실패!",
								icon: "error",
								confirmButtonText: "확인"
							});
					});
			}
		});
	});

	messageBox.forEach(row => {
		row.addEventListener('click', function() {

			const btitle = this.dataset.title;
			const bcontent = this.dataset.content;
			const sendhuman = this.dataset.sendhuman;
			const bemergency = this.dataset.yn;

			// 모달에 데이터 바인딩
			document.getElementById('mboxTitleModal').value = btitle;
			document.getElementById('mboxContentModal').value = bcontent;
			document.getElementById('mbsendIdModal').value = sendhuman;
			document.getElementById('mboxEmergencyynModal').checked = (bemergency === 'Y');

			// 모달 표시
			boxMessageModal.show();
		});
	});


});