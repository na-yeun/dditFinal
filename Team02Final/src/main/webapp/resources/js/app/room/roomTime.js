document.addEventListener("DOMContentLoaded", function() {


	// 현재 시간 기준
	const now = new Date();
	const currentTime = `${String(now.getHours()).padStart(2, "0")}:${String(now.getMinutes()).padStart(2, "0")}`;
	const modalButton = document.getElementById("modal-button"); // 예약하기 버튼
	// 예약하기 버튼 초기 상태 비활성화
	modalButton.disabled = true;
	// 오늘 날짜 구하기
	function getFormattedDate() {
		const today = new Date(); //오늘 날짜 구하기 , Tue Dec 10/ 2024 15:09:33 GMT+0900 (한국 표준시)
		console.log(today)
		const year = today.getFullYear();
		//console.log(year) -->> 2024
		const month = String(today.getMonth() + 1).padStart(2, "0");
		//console.log(today.getMonth()) -->> 0부터 시작하기때문에 오늘 날짜에서 월을 가져온다음에 +1을 꼭 해주기
		//today.getMonth() 이것만하면 11만 나옴
		//padStart(2, "0"); 는 yyyy-mm-dd 의 형식을 맞추기위해 최소 2자리부터 시작하고 한자리면 거기에 0을 붙여서
		//형식에 맞게 맞춘것
		const date = String(today.getDate()).padStart(2, "0");

		// 요일 구하기
		const days = ["일", "월", "화", "수", "목", "금", "토"];
		const day = days[today.getDay()]; //이렇게하면 0,1,2,3,4,5,6 이 나오는데 일~토까지 순서대로 매치되서 나오게되는것
		console.log(today.getDay())

		//2024-12-10(화) 이렇게 나오게 하려고 맞춘것
		return `${year}-${month}-${date}(${day})`;

	}

	// 오늘 날짜를 선택 날짜에 넣기
	document.getElementById("selected-date").innerText = getFormattedDate();
	const selectedTimes = new Set(); // 선택된 시간을 저장
	const selectedTimeCodes = new Set();
	const timeSelect = document.getElementById("time-select");
	const options = timeSelect.querySelectorAll("option");
	const selectedTimesElement = document.getElementById("selected-times");


	options.forEach(option => {
		const roomtimeYn = option.getAttribute("data-yn"); // data-yn 값 가져오기
		const timeRange = option.textContent.split(" ")[0]; // "08:00~09:00"
		if (timeRange && timeRange.includes("~")) {
			const startTime = timeRange.split("~")[0].trim(); // "08:00"

			if (startTime) {
				const startTimeDate = new Date(`1970-01-01T${startTime}:00`);
				const currentTimeDate = new Date(`1970-01-01T${currentTime}:00`);

				if (roomtimeYn === "N") {
					// 예약이 완료된 것들은 이렇게 나오게
					option.disabled = true;
					option.setAttribute("style", "color: rgb(251,89,65) !important;");
					option.textContent = `${timeRange} (예약 불가능)`;
				} else if (currentTimeDate >= startTimeDate) {
					// 현재 시간이 시작 시간을 지난 경우
					option.disabled = true;
					option.style.color = "#e9ebeb";
					option.textContent = `${timeRange} (시간 경과)`;
				} else if (roomtimeYn === "Y") {
					// 예약 가능 상태
					option.disabled = false;
					option.style.color = "black";
					option.textContent = `${timeRange} (예약 가능)`;
				}
			}
		}
	});
	// 기본 옵션 "시간을 선택하세요" 로,,,,,
	const defaultOption = timeSelect.querySelector('option[value=""]');
	if (defaultOption) {
		defaultOption.textContent = "시간을 선택하세요"; // 텍스트 변경
	}


	// 시간 선택 시 처리
	timeSelect.addEventListener("change", function() {
		const hasSelection = Array.from(timeSelect.selectedOptions).some(option => option.value);
		modalButton.disabled = !hasSelection; // 선택 없으면 비활성화

		const selectedOption = timeSelect.options[timeSelect.selectedIndex];
		const selectedTime = selectedOption.textContent.split(" ")[0]; // "08:00~09:00"
		const selectedTimeCode = selectedOption.value; // timeCode 가져오기


		if (!selectedTimes.has(selectedTime)) {
			// 선택된 시간이 기존에 없으면 추가
			selectedTimes.add(selectedTime);
			selectedTimeCodes.add(selectedTimeCode); // timeCode도 추가
			selectedOption.classList.add("selected-option"); // 선택된 옵션에 하늘색 배경 추가
		} else {
			// 이미 선택된 경우 선택 해제
			selectedTimes.delete(selectedTime);
			selectedTimeCodes.delete(selectedTimeCode); // timeCode도 추가
			selectedOption.classList.remove("selected-option"); // 하늘색 배경 제거
		}

		// 선택된 시간 정렬
		const sortedTimes = Array.from(selectedTimes).sort();


		// 선택 회차 업데이트
		updateSelectedTimes(sortedTimes);
	});




	// 선택된 시간 업데이트
	function updateSelectedTimes(sortedTimes) {
		selectedTimesElement.innerHTML = ""; // 기존 선택된 시간을 초기화

		sortedTimes.forEach((time) => {
			// 선택된 시간 요소 생성
			const timeWrapper = document.createElement("div");
			timeWrapper.className = "selected-time-wrapper"; // 스타일 적용을 위해 클래스 추가

			const timeSpan = document.createElement("span");
			timeSpan.textContent = time;
			timeSpan.className = "time-text"; // 시간 텍스트 스타일링 클래스

			// 제거 버튼 생성
			const removeButton = document.createElement("button");
			removeButton.textContent = "x";
			removeButton.className = "remove-time-button"; // 버튼 스타일링 클래스

			removeButton.addEventListener("click", function() {
				// 시간 제거 처리
				selectedTimes.delete(time);

				// 셀렉트 박스에서 해당 옵션의 스타일 제거
				const option = Array.from(timeSelect.options).find((opt) =>
					opt.textContent.includes(time)
				);


				if (option) {
					option.classList.remove("selected-option");
					option.selected = false; // 선택 해제
				}

				const updatedTimes = Array.from(selectedTimes).sort();

				updateSelectedTimes(updatedTimes);
			});

			// 시간 + 제거 버튼 추가
			timeWrapper.appendChild(timeSpan);
			timeWrapper.appendChild(removeButton);
			selectedTimesElement.appendChild(timeWrapper);
		});
	}


	//여기는 모달창 띄우는것..
	//const modalButton = document.getElementById("modal-button");
	const modalSelectedDate = document.getElementById("modal-selected-date");
	const modalSelectedTimes = document.getElementById("modal-selected-times");
	const modalRoomHosu = document.getElementById("modal-room-hosu");
	const modalRoomName = document.getElementById("modal-room-name");
	const modalRoomNum = document.getElementById("modal-room-num");
	const modalRoomDetail = document.getElementById("modal-room-detail");
	const modalFacilityImage = document.getElementById("modal-facility-image"); // 모달 이미지 태그


	// "룸 상세정보에서 예약하기" 버튼 클릭 시 모달에 데이터 업데이트
	modalButton.addEventListener("click", function() {

		//모달에 찍으려고 다 값을 들고오기
		const selectedDate = document.getElementById("selected-date").innerText;
		const sortedTimes = Array.from(selectedTimes).sort();

		const roomHosu = document.getElementById("roomHosu").innerText;
		const roomName = document.getElementById("roomName").innerText;
		const roomNum = document.getElementById("roomNum").innerText;
		const roomDetail = document.getElementById("roomDetail").innerText;
		const roomImageSrc = document.querySelector(".facility-image-wrapper img").src; // 시설 이미지



		// 선택된 데이터를 모달에 표시 --> 모달에 들어오는 친구들
		modalSelectedDate.innerText = selectedDate;
		modalSelectedTimes.innerText = sortedTimes.join(", "); // x를 제외하고 시간 범위만 표시
		modalRoomHosu.innerText = roomHosu;
		modalRoomName.innerText = roomName;
		modalRoomNum.innerText = roomNum;
		modalRoomDetail.innerText = roomDetail;
		modalFacilityImage.src = roomImageSrc; // 이미지 업데이트



	});

	// id가 "confirm-reservation"인 버튼을 가져옴
	const confirmButton = document.getElementById("confirm-reservation");

	// 기존 이벤트 리스너 제거
	// handleReservation 이건 이벤트 핸들러 함수인데 여기서는 예약버튼을 클릭하면 실행되는거
	confirmButton.removeEventListener("click", handleReservation);

	// 새로운 이벤트 리스너 등록
	confirmButton.addEventListener("click", handleReservation);






	function handleReservation() {
		const modalRoomDetail = document.getElementById("modal-roomReser-detail");
		if (!modalRoomDetail || !modalRoomDetail.value || !modalRoomDetail.value.trim()) {
			Swal.fire({
				title: "시설 사용 용도 누락!",
				text: "시설 사용 용도를 입력해주세요.",
				icon: "warning",
				confirmButtonText: "확인",
			});
			return;
		}
		console.log("사용 용도:", modalRoomDetail.value.trim());
		// 예약 확인 팝업
		Swal.fire({
			title: '정말로 예약하시겠습니까?',
			text: "예약 후에는 취소가 어려울 수 있습니다. 신중히 결정해주세요.",
			icon: 'warning',
			showCancelButton: true,
			confirmButtonColor: '#3085d6',
			cancelButtonColor: '#d33',
			confirmButtonText: '확인',
			cancelButtonText: '취소',
			customClass: {
				popup: 'custom-swal-popup'
			}
		}).then((result) => {
			if (result.isConfirmed) {
				confirmButton.disabled = true; // 버튼 비활성화
				const modalElement = document.querySelector("#basicModal");
				const modalInstance = bootstrap.Modal.getInstance(modalElement);

				const roomId = document.getElementById("hiddenRoodId").value;
				const hosu = document.getElementById("modal-room-hosu").innerText;
				const name = document.getElementById("modal-room-name").innerText;
				const roomCause = document.getElementById("modal-roomReser-detail").value;
				const empId = confirmButton.getAttribute('data-id');
				const url2 = confirmButton.getAttribute('data-url');

				const data = {
					roomId: roomId,
					hosu: hosu,
					name: name,
					timeRanges: Array.from(selectedTimes),
					timeCodes: Array.from(selectedTimeCodes),
					reserCause: roomCause,
					empId: empId,
				};

				fetch(url2, {
					method: "POST",
					headers: {
						"Content-Type": "application/json",
					},
					body: JSON.stringify(data),
				})
					.then(response => {
						if (!response.ok) {
							throw new Error(`HTTP Error: ${response.status}`);
						}
						return response.json();
					})
					.then(responseData => {
						if (responseData.status === "success") {
							// 모달을 먼저 닫고 팝업 표시
							if (modalInstance) {
								modalInstance.hide();
							}
							// 예약된 시간 범위를 비활성화 처리
							Array.from(selectedTimes).forEach(time => {
								const option = Array.from(timeSelect.options).find((opt) =>
									opt.textContent.includes(time)
								);
								if (option) {
									option.disabled = true;
									option.style.color = "gray";
									option.textContent = `${time} (예약 불가능)`;
								}
							});
							Swal.fire({
								title: "예약이 성공적으로 완료되었습니다!",
								text: "감사합니다. 예약이 등록되었습니다.",
								icon: "success",
								confirmButtonColor: '#3085d6',
							}).then(() => {
								// 예약 성공 후 페이지 새로고침
								window.location.reload();
							});
							// 선택된 시간 초기화
							selectedTimes.clear();
							selectedTimeCodes.clear();
							updateSelectedTimes([]);
						} else {
							Swal.fire({
								title: "예약 실패",
								text: "다시 시도해주세요.",
								icon: "error",
								confirmButtonColor: '#d33',
							});
						}
					})
					.catch(error => {
						Swal.fire({
							title: "오류 발생",
							text: "예약 중 문제가 발생했습니다. 다시 시도해주세요.",
							icon: "error",
							confirmButtonColor: '#d33',
						});
					})
					.finally(() => {
						confirmButton.disabled = false; // 다시 버튼 활성화
					});
			}
		});
	}

	document.querySelectorAll(".delBtn").forEach((delBtn) => {
		delBtn.addEventListener("click", () => {
			const url = delBtn.getAttribute("data-url");
	
			Swal.fire({
				title: '삭제하시겠습니까?',
				text: "삭제 후 복구할 수 없습니다!",
				icon: 'warning',
				showCancelButton: true,
				confirmButtonColor: '#3085d6',
				cancelButtonColor: '#d33',
				confirmButtonText: '확인',
				cancelButtonText: '취소',
				customClass: {
					popup: 'custom-swal-popup'
				}
			}).then((result) => {
				if (result.isConfirmed) {
					fetch(url, {
						method: 'POST',
						headers: {
							'Content-Type': 'application/json'
						}
					})
					.then(response => {
						if (response.ok) {
							Swal.fire({
								title: '삭제 완료',
								text: '삭제가 성공적으로 처리되었습니다.',
								icon: 'success',
								confirmButtonColor: '#3085d6',
							}).then(() => {
								delBtn.closest('tr').remove(); // 삭제된 행만 제거
								window.location.reload();
							});
						} else {
							Swal.fire({
								title: "삭제 실패",
								text: "삭제에 실패했습니다.",
								icon: "error",
								confirmButtonColor: '#d33',
							});
						}
					})
					.catch(error => {
						console.error('삭제 중 오류 발생:', error);
						Swal.fire({
							title: "오류 발생",
							text: "삭제 중 문제가 발생했습니다. 다시 시도해주세요.",
							icon: "error",
							confirmButtonColor: '#d33',
						});
					});
				}
			});
		});
	});
	
	const backLocationBtn = document.getElementById("backLocationBtn");
	if (backLocationBtn) {
		backLocationBtn.addEventListener('click', () => {
			const url = backLocationBtn.getAttribute('data-url'); // data-url 속성에서 URL을 가져옴
			location.href = url; // 룸 리스트 페이지로 이동
		});
	}


});
