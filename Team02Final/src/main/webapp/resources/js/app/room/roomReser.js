function paging(page) {
	console.log(page);
	searchForm.page.value = page;
	searchForm.requestSubmit();
}

document.addEventListener("DOMContentLoaded", function() {

	let $searchForm = $("#searchForm");
	let $searchBtn = $("#select-searchWord");
	$searchBtn.on("change", function() {
		let $parent = $(this).parents(".search-area")// div태그를 찾은것
		$parent.find(":input[name]").each(function(index, ipt) {//부모의 자식중 input태그중에 name속성을 갖고있는 
			console.log(ipt.name, ipt.value)
			//$searchForm.find(`[name="${ipt.name}"]`).val(ipt.value); 아래꺼랑 똑같음
			if (searchForm[ipt.name]) { //해당 엘리먼트가 있으면
				searchForm[ipt.name].value = ipt.value
			}
			//searchForm.requestSubmit();-
			$searchForm.submit();// 제이쿼리에서는requestSubmit(); =   submit()임
		})
	})


	
	//등록 버튼을 눌렀을때 url >> room/new >> roomController에
	const btnInsert = document.querySelector("#btn-insert");

	if (btnInsert) {
		btnInsert.addEventListener("click", function() {
			location.href = 'room/newForm';
		});
	}



	// 관리 버튼 클릭 이벤트
	
	const btnUpdateButtons = document.querySelectorAll('#btn-update');
	btnUpdateButtons.forEach(button => {
		button.addEventListener("click", function() {
			const url = this.getAttribute("data-url"); // data-url에서 URL 가져오기
			if (url) {
				location.href = url; // 해당 URL로 이동
			} else {
				console.error("URL오류.");
			}
		});
	});



	//상세 페이지에서 예약하기 버튼을 눌렀을때 
	const reserveButtons = document.querySelectorAll(".reserve-button");

	//예약하기기를 누르면 시설의 상세페이지로 넘어감
	//forEach 를 돌린 이유는 버튼이 여러개이기떄문에
	reserveButtons.forEach(button => {
		button.addEventListener("click", function() {
			const urlDetile = this.getAttribute("data-url"); // data-url에서 URL 가져오기
			if (urlDetile) {
				window.location.href = urlDetile; //상세 페이지로 이동
			} else {
				console.error("URL이 잘못도미");
			}
		});
	});

	//웹페이지가 완전히 준비되었을 때 실행됨.
	//모달,,열기위해서
	$(document).ready(function() {
		//id="exLargeModal"인 요소를 찾아서 실행해줘
		$("#exLargeModal").on("show.bs.modal", function(event) {

			//relatedTarget은 Bootstrap의 모달 이벤트에서 제공하는 기본 객체임. 
			//모달이 열릴 때, 어떤 요소(버튼 등)가 모달을 열었는지 알려주는것이 relatedTarget 임!! 
			const button = $(event.relatedTarget); //예약현황의 버튼!!
			const roomId = button.data("room-id"); // 클릭된 버튼의 data-room-id 값을 가져옴
			const myEmpId = document.querySelector('#sessionEmpId'); //id가 sessionEmpId인 input태그를 가져오고
			const sessionEmpId = myEmpId.dataset.emId; //input태그 안에 'data-em-id' 값을 가져온다.
			const roomPosiId = document.querySelector("#sessionPosiId"); 
			const rommReserPosiId = roomPosiId.dataset.poId; // 'data-po-id' 값 가져오기


			//비동기방식
			$.ajax({
				url: 'roomTime/' + roomId + '/rList', //여기를 갔다가 get으로 selectRoomEmpList를 가져와서
				method: "GET",
				dataType: "JSON",
				success: function(data) { //selectRoomEmpList 여기에 json의 형태로 여러개의 반환값을 여기안에서 success 콜백함수에서 처리
					if (data.length === 0) {
						// 데이터가 없을 때 "예약현황이 없습니다." 메시지 표시
						$('#modalContent').html(`
	                        <div class="alert alert-info text-center" role="alert">
	                            예약현황이 없습니다.
	                        </div>
	                    `);
						return; // 아래 코드를 실행하지 않음
					}

					let tableHeaders = `
	                    <th>호수</th>
	                    <th>시설명</th>
	                    <th>예약시간</th>
	                    <th>예약자 부서</th>
	                    <th>예약자명</th>
	                `;

					// 조건에 따라 "삭제" 열 추가
					  // 조건에 따라 "삭제" 열 추가
					  if (
						data.some(
							(reser) =>
								sessionEmpId === reser.employee.empId || rommReserPosiId === "7"
						)
					) {
						tableHeaders += `<th>예약취소</th>`;
					}

					let tableRows = '';
					data.forEach(function (reser) {
						// 조건에 따라 삭제 버튼 추가
						const buttonTd =
							sessionEmpId === reser.employee.empId || rommReserPosiId === "7"
								? `<td>
									   <button class="btn btn-danger delete-reservation-btn" data-reservation-id="${reser.reserId}">
										   예약취소
									   </button>
								   </td>`
								: "";

						tableRows += `
	                        <tr>
	                            <td>${reser.room.roomHosu}</td>
	                            <td>${reser.room.roomName}</td>
	                            <td>${reser.timeReser.timeRange}</td>
	                            <td>${reser.department.departName}</td>
	                            <td>${reser.employee.empName}</td>
	                            ${buttonTd}
	                        </tr>
	                    `;
					});
					//id가 modalContent인 모달안에 찍어주기
					$('#modalContent').html(` 
	                    <table class="table table-bordered">
	                        <thead>
	                            <tr>
	                                ${tableHeaders}
	                            </tr>
	                        </thead>
	                        <tbody>
	                            ${tableRows}
	                        </tbody>
	                    </table>
	                `);

					// 삭제 버튼에 이벤트 핸들러 추가(제이쿼리로)
					$('.delete-reservation-btn').on('click', function() {

						//현재 이벤트가 발생한 요소 자신 >>this >> reservation-id를 갖고있는 요소 말하는 것
						const reservationId = $(this).data('reservation-id'); // 내가 예약한 시설의 예약번호를 가져와서 넣어주는것
						Swal.fire({
							title: "삭제하시겠습니까?",
							text: "삭제 후 복구할 수 없습니다!",
							icon: "warning",
							showCancelButton: true,
							confirmButtonColor: "#3085d6",
							cancelButtonColor: "#d33",
							confirmButtonText: "삭제",
							cancelButtonText: "취소",
							customClass: {
								popup: 'custom-swal-popup'
							}
						}).then((result) => {
							if (result.isConfirmed) {
								$.ajax({
									url: 'roomTime/' + reservationId + '/delete',
									method: 'POST',
									success: function() {
										Swal.fire({
											title: "삭제 성공!",
											text: "예약이 삭제되었습니다.",
											icon: "success",

										}).then(() => {
											$("#exLargeModal").modal("hide");
											window.location.reload();
										});
									},
									error: function() {
										Swal.fire({
											icon: "error",
											title: "삭제 실패!",
											text: "예약 삭제에 실패했습니다.",
										});
									},
								});
							}
						});
					});
				},
				error: function() {
					$("#modalContent").html("<p>데이터를 가져오는 데 실패했습니다.</p>");
				},
			});
		});
	});
});










