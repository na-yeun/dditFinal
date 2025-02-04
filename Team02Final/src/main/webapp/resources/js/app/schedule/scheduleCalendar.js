

function toKoreaTimeISOString(date) {
	const offset = 9 * 60 * 60000; // UTC+9 한국 시간대 오프셋 (ms 단위)
	const koreaTime = new Date(date.getTime() + offset);
	return koreaTime.toISOString().slice(0, 16);
}

// 유틸리티 함수들 선언
function restrictScheduleTypeToOptionsAndSetDefault() {
	const scheduleTypeInput = document.querySelector("#scheduleType");
	if (!scheduleTypeInput) return;

	// 모든 옵션 초기화
	Array.from(scheduleTypeInput.options).forEach(option => {
		//option.disabled = true; // 모든 옵션 비활성화
		option.selected = false; // 기본값 해제
	});

	const isManager = hiddenPosiId === "7"; // 회사 관리자 여부
	const isDeptHead = hiddenPosiId === "4"; // 부서 관리자 여부

	if (isManager) {
		// 회사 관리자: 회사 일정만 가능
		enableAndSetDefaultOption(scheduleTypeInput, ["1"]);
	} else if (isDeptHead) {
		// 부서 관리자: 부서 일정과 개인 일정 가능
		enableAndSetDefaultOption(scheduleTypeInput, ["2", "3"]);
	} else {
		// 일반 사원: 개인 일정만 가능
		enableAndSetDefaultOption(scheduleTypeInput, ["3"]);
	}
}

function enableAndSetDefaultOption(scheduleTypeInput, allowedValues) {
	allowedValues.forEach(value => {
		const option = Array.from(scheduleTypeInput.options).find(opt => opt.value === value);
		if (option) {
			option.disabled = false; // 활성화
		}
	});

	// 기본값 설정
	if (allowedValues.length > 0) {
		const defaultOption = Array.from(scheduleTypeInput.options).find(opt => allowedValues.includes(opt.value));
		if (defaultOption) {
			defaultOption.selected = true; // 기본 선택
		}
	}
}
function removeOption(scheduleTypeInput, value) {
	const option = Array.from(scheduleTypeInput.options).find(opt => opt.value === value);
    if (option) {
        option.disabled = true; // 옵션 비활성화
        option.style.color = "gray"; // 텍스트 색상을 회색으로 변경
		option.style.backgroundColor = "#A9A9A9"; // 진한 회색 배경색 (DarkGray)
		option.style.cursor = "not-allowed"; // 커서도 비활성화 스타일로 변경
    }
}

document.addEventListener("DOMContentLoaded", function() {

	


	//모달 id값을 가져옴 --> 모달 한번 열고 닫을때 값을 다 없애기 위해서
	const scheduleModal = document.getElementById("basicModal");

    // 모달이 닫힐 때 값을 초기화
    scheduleModal.addEventListener("hidden.bs.modal", function() {
          // 모든 입력 필드 초기화
		const mySchStart = document.querySelector("#schStart");
		const mySchEnd = document.querySelector("#schEnd");
		const mySchTitle = document.querySelector("#eventName");
		const mySchContent = document.querySelector("#eventContent");
		const mySchAllday = document.querySelector("#allDay");
		const scheduleTypeInput = document.querySelector("#scheduleType");
		const colorSettings = document.querySelector("#colorSettings");

		// 값 초기화
		if (mySchStart) {
			mySchStart.value = "";
			mySchStart.disabled = false; // 비활성화 해제
		}
		if (mySchEnd) {
			mySchEnd.value = "";
			mySchEnd.disabled = false; // 비활성화 해제
		}
		if (mySchTitle) mySchTitle.value = "";
		if (mySchContent) mySchContent.value = "";
		if (mySchAllday) mySchAllday.checked = false;

		// 일정 유형 초기화
		if (scheduleTypeInput) {
			scheduleTypeInput.value = ""; // 기본값 설정
			scheduleTypeInput.disabled = false; // 활성화
		}

		// 색상 설정 초기화
		if (colorSettings) {
			colorSettings.innerHTML = ""; // 색상 설정 초기화
		}
    });


	//const companyId = document.querySelector("#hiddenCompanyId").value;
	const empId = document.querySelector("#hiddenEmpId").value;
	const calendarEl = document.querySelector("#calendar");
	const mySchStart = document.querySelector("#schStart");
	const mySchEnd = document.querySelector("#schEnd");
	const mySchTitle = document.querySelector("#eventName");
	const mySchContent = document.querySelector("#eventContent");
	const mySchAllday = document.querySelector("#allDay");
	// const mySchBColor = document.querySelector("#schBColor");
	// const mySchFColor = document.querySelector("#schFColor");
	const tabButtons = document.querySelectorAll(".tab-button"); // 분류 버튼 그룹
	const hiddenDeptCodeElement = document.querySelector("#hiddenDeptCode");
	const departCode = hiddenDeptCodeElement ? hiddenDeptCodeElement.value : "";
	const hiddenPosiId = document.querySelector("#hiddenPosiId").value;
	let schetypeId = ""; // 전역 변수 선언: 일정 유형(회사/부서/개인) 전역변수로 빼기
	const modalFooterSchedule = document.querySelector("#modalFooterSchedule");
	console.log("modalFooterSchedule>>>", modalFooterSchedule)

	const scheduleTypeInput = document.querySelector("#scheduleType");
	


	// 초기 상태 설정
	function updateColorSettingsVisibility() {
		// const selectedValue = scheduleTypeInput.value; // 현재 선택된 일정 분류 값
		// const isDeptHead = hiddenPosiId === "4"; // 부서 관리자 여부
		// const isManager = hiddenPosiId === "7"; // 회사 관리자 여부
		const colorSettings = document.querySelector("#colorSettings"); // 색상 설정 요소
		let scheduleTypeSelect = document.querySelector('#scheduleType');
		if (!colorSettings) return;
		scheduleTypeSelect.addEventListener("change", e => {
			let scheType = e.target.value;

			if(scheType==='3'){
				colorSettings.innerHTML = 
				`
				<div id="colorSettings" style="display: block;">
					<br> <label for="schBcolor" class="form-label">배경색</label> 
					<input type="color" id="schBColor" value="#ede9fb"> 
					<label for="schFcolor" class="form-label">글자색</label> 
					<input type="color" id="schFColor" value="#000000"> 
				</div>`;
			} else {
				colorSettings.innerHTML = ``;
			}
		})
		
	}

	// 초기 로드 시 상태 업데이트
	updateColorSettingsVisibility();

	// 일정 분류 변경 시 상태 업데이트
	scheduleTypeInput.addEventListener("change", updateColorSettingsVisibility);


	// 수정 버튼 생성
	const modifyEventBtnSchedule = document.createElement("button");
	modifyEventBtnSchedule.id = "modifyEventBtnSchedule";
	modifyEventBtnSchedule.className = "btn btn-success";
	modifyEventBtnSchedule.textContent = "수정";


	// 삭제 버튼 생성
	const deleteEventBtnSchedule = document.createElement("button");
	deleteEventBtnSchedule.id = "deleteEventBtnSchedule";
	deleteEventBtnSchedule.className = "btn btn-danger";
	deleteEventBtnSchedule.textContent = "삭제";

	// 수정, 삭제 버튼 추가
	modalFooterSchedule.prepend(modifyEventBtnSchedule); // 수정 버튼 맨 앞
	modalFooterSchedule.insertBefore(deleteEventBtnSchedule, modifyEventBtnSchedule.nextSibling); // 삭제 버튼 수정 버튼 뒤

	// 닫기 버튼을 맨 오른쪽으로 이동
	const closeModalBtn = modalFooterSchedule.querySelector("#closeModal");
	if (closeModalBtn) {
		modalFooterSchedule.appendChild(closeModalBtn);
	}



	const eventName = document.getElementById("eventName");
	const eventContent = document.getElementById("eventContent");
	const maxLength = 160; // 최대 글자 수

	// 제목 글자수 제한
	if (eventName) {
		eventName.addEventListener("input", () => {
			if (eventName.value.length > maxLength) {
				eventName.value = eventName.value.substring(0, maxLength); // 초과된 글자 잘라내기
				Swal.fire({
					icon: "warning",
					title: "글자수 초과",
					text: `제목은 최대 ${maxLength}자까지 입력 가능합니다.`,
					confirmButtonText: "확인",
				});
			}
		});
	}

	// 내용 글자수 제한
	if (eventContent) {
		eventContent.addEventListener("input", () => {
			if (eventContent.value.length > maxLength) {
				eventContent.value = eventContent.value.substring(0, maxLength); // 초과된 글자 잘라내기
				Swal.fire({
					icon: "warning",
					title: "글자수 초과",
					text: `내용은 최대 ${maxLength}자까지 입력 가능합니다.`,
					confirmButtonText: "확인",
				});
			}
		});
	}



	// 초기화: 버튼 상태 설정
	function updateButtonVisibility(isExistingEvent = false, schetypeId = "", event = null) {
		const isManager = hiddenPosiId === "7"; // 회사 관리자 여부
		const isDeptHead = hiddenPosiId === "4"; // 부서 관리자 여부


		const addEventBtn = document.getElementById("addEventBtn");
		if (!addEventBtn) return;

		// 버튼 초기화
		modifyEventBtnSchedule.style.display = "none";
		deleteEventBtnSchedule.style.display = "none";
		addEventBtn.style.display = "none";

		// 새 일정 등록일 경우
		if (!isExistingEvent) {
			const scheduleTypeInput = document.querySelector("#scheduleType");
			if (!scheduleTypeInput) return;

			const selectedOption = Array.from(scheduleTypeInput.options).find(option => option.selected);
			const selectedType = selectedOption ? selectedOption.value : "";

			// 회사 관리자
			if (isManager) {
				addEventBtn.style.display = "inline-block";
			}
			// 부서 관리자
			else if (isDeptHead && (selectedType === "2" || selectedType === "3")) {
				addEventBtn.style.display = "inline-block";
			}
			// 일반 사원
			else if (!isManager && !isDeptHead && selectedType === "3") {
				addEventBtn.style.display = "inline-block";
			}
		} else {
			// 기존 이벤트 수정/삭제 처리
			if (schetypeId === "1" && isManager) {
				modifyEventBtnSchedule.style.display = "inline-block";
				deleteEventBtnSchedule.style.display = "inline-block";
			} else if (schetypeId === "2" && isDeptHead) {
				modifyEventBtnSchedule.style.display = "inline-block";
				deleteEventBtnSchedule.style.display = "inline-block";
			} else if (schetypeId === "3" && empId === empId) {
				modifyEventBtnSchedule.style.display = "inline-block";
				deleteEventBtnSchedule.style.display = "inline-block";
				
			}
		}
	}

	function restrictScheduleTypeToOptions(allowedOptions) {
		const scheduleTypeInput = document.querySelector("#scheduleType");
		if (!scheduleTypeInput) return;

		// 옵션 초기화 및 제한
		Array.from(scheduleTypeInput.options).forEach(option => {
			if (allowedOptions.includes(option.value)) {
				option.disabled = false;

			} else {
				option.disabled = true;
			}

		});
	}


	//캘린더 헤더 옵션
	const headerToolbar = {
		left: 'prevYear,prev,next,nextYear today',
		center: 'title',
		right: 'dayGridMonth,dayGridWeek,timeGridDay'
	}


	// FullCalendar 초기화
	const calendar = new FullCalendar.Calendar(calendarEl, {
		height: "900px",
		expandRows: true,
		initialView: "dayGridMonth",
		slotMinTime: '09:00',
		slotMaxTime: '18:00',
		locale: "kr",
		headerToolbar: headerToolbar,
		editable: true,
		selectable: true,
		selectMirror: true,
		navLinks: true,
		weekNumbers: true,
		dayMaxEventRows: true
		
	});

	// 초기 일정 불러오기 및 일정 갱신 함수
	function updateCalendarEventsSchedule(schetypeId) {
		console.log("요청된 schetypeId:", schetypeId); // 디버그 로그
		
		 // 회사 관리자 설정
		 if (hiddenPosiId === "7") { // 회사 관리자
			 // 1. 모든 버튼 숨김
			 tabButtons.forEach((button) => {
				button.style.display = "none"; // 모든 버튼 숨김
			});
	
	
			// 2. 드롭다운에서 "회사" 옵션만 활성화
			Array.from(scheduleTypeInput.options).forEach(option => {
				if (option.value !== "1") {
					option.disabled = true; // "회사" 외 옵션 비활성화
				} else {
					option.selected = true; // "회사" 선택
				}
			});
	
			// 3. 캘린더 초기화: 회사 일정만 표시
			schetypeId = "1"; // 회사 일정
			calendar.removeAllEventSources(); // 모든 이벤트 소스 제거
			calendar.addEventSource({
				url: `../schedule`, // 서버의 일정 API URL
				method: "GET",
				extraParams: {
					schetypeId: schetypeId // 회사 일정만 요청
				},
				failure: function() {
					console.error("서버에서 이벤트를 불러오지 못했습니다.");
					Swal.fire({
						icon: "error",
						title: "데이터 불러오기 실패!",
						text: "다시 시도해주세요.",
					});
				}
			});
			calendar.refetchEvents(); // 새 데이터 로드
			// 캘린더 렌더링
			calendar.render();
		} else {
			// 기존 이벤트 소스 제거
			calendar.removeAllEventSources();
		
			// 새로운 이벤트 소스 추가
			calendar.addEventSource({
				url: `../schedule`, // 서버의 일정 API URL
				method: "GET",
				extraParams: {
					schetypeId: schetypeId || "",
					departCode: schetypeId === "2" ? departCode : "",
					empId: schetypeId === "3" ? empId : ""
				},
				success: function (events) {
					console.log("불러온 이벤트:", events); // 불러온 데이터를 확인합니다.
				},
				failure: function () {
					console.error("서버에서 이벤트를 불러오지 못했습니다.");
					Swal.fire({
						icon: "error",
						title: "데이터 불러오기 실패!",
						text: "다시 시도해주세요.",
						customClass: {
							popup: "custom-swal-popup"
						}
					});
				}
			});
			calendar.refetchEvents(); // 새 데이터 로
			// 회사 관리자가 아닌 경우 기존 로직 적용
			calendar.render();
		}



		
	}

	// 캘린더 초기 렌더링
	//calendar.render();

	// 초기 로드 시 전체 일정 불러오기
	updateCalendarEventsSchedule(""); // 전체 일정은 schetypeId 없이 요청


	function resetModalFields() {
		const mySchTitle = document.querySelector("#eventName");
    const mySchContent = document.querySelector("#eventContent");
    const mySchStart = document.querySelector("#schStart");
    const mySchEnd = document.querySelector("#schEnd");
    const mySchAllday = document.querySelector("#allDay");
    const scheduleTypeInput = document.querySelector("#scheduleType");
    const colorSettings = document.querySelector("#colorSettings");

    // 필드 초기화
    if (mySchTitle) mySchTitle.value = "";
    if (mySchContent) mySchContent.value = "";
    if (mySchStart) mySchStart.value = "";
    if (mySchEnd) mySchEnd.value = "";
    if (mySchAllday) mySchAllday.checked = false;

    // 일정 분류 초기화 (빈 값으로 설정)
    if (scheduleTypeInput) {
        scheduleTypeInput.value = "";
        scheduleTypeInput.disabled = false; // 새 일정에서는 수정 가능하도록 설정
    }

    // 색상 설정 초기화
    if (colorSettings) {
        colorSettings.innerHTML = "";
    }
	}




	// 버튼 클릭 시 이벤트 처리
	tabButtons.forEach((button) => {
		button.addEventListener("click", function() {
			// 모든 버튼에서 active 클래스 제거
			tabButtons.forEach((btn) => btn.classList.remove("active"));
			this.classList.add("active"); // 클릭한 버튼에 active 추가
			const scheduleTypeInput = document.querySelector("#scheduleType");
			
			// if (this.textContent === "개인") {
			// 	scheduleTypeInput = ""; // 전체 일정
			// }

			if (this.textContent === "전체") {
				schetypeId = ""; // 전체 일정
				restrictScheduleTypeToOptions(["1", "2", "3"]); // 모든 옵션 활성화
			} else if (this.textContent === "부서") {
				schetypeId = "2"; // 전체 일정
				restrictScheduleTypeToOptions(["1", "2", "3"]); // 모든 옵션 활성화
			}

			// 권한 기반 기본값 설정
			if (schetypeId === "" && hiddenPosiId === "7" && hiddenPosiId !== "4") {
				schetypeId = "1";
			}

			// 버튼의 텍스트에 따라 schetypeId 값을 설정
			//전체를 누르면 회사, 부서, 개인 일정을 보여주는것
			if (this.textContent === "전체") {
				schetypeId = ""; // "전체" 버튼은 빈 문자열로 설정
			} else if (this.textContent === "회사") {
				schetypeId = "1"; // 회사 일정
			} else if (this.textContent === "부서") {
				schetypeId = "2"; // 부서 일정
			} else if (this.textContent === "개인") {
				schetypeId = "3"; // 개인 일정
			}
			updateColorSettingsVisibility();
			// 3. 선택한 값이 드롭다운에도 반영되도록 설정
			//
			// if (scheduleTypeInput) {
			// 	scheduleTypeInput.value = schetypeId || ""; // 드롭다운 값 업데이트
			// }

			


			// 4. 캘린더 업데이트
			restrictScheduleTypeToOptionsAndSetDefault(schetypeId); // 모든 옵션 활성화
			updateCalendarEventsSchedule(schetypeId);
			calendar.refetchEvents(); // FullCalendar 이벤트 다시 로드
		});
	});



	/*modalFooter.appendChild(modifyEventBtn);
	modalFooter.appendChild(deleteEventBtn);*/

	
	// 모달 열기 전에 초기화
	calendar.on("select", (info) => {
		resetModalFields(); // 입력 필드 초기화
		const scheduleTypeInput = document.querySelector("#scheduleType");
		const colorSettings = document.querySelector("#colorSettings");
		if (scheduleTypeInput) {
			scheduleTypeInput.disabled = false; // 분류 선택 활성화
		}
		
		const isManager = hiddenPosiId === "7"; // 회사 관리자 여부
		const isDeptHead = hiddenPosiId === "4"; // 부서 관리자 여부
		const isEmployee = !isManager && !isDeptHead; // 일반 사원 여부


		restrictScheduleTypeToOptionsAndSetDefault(schetypeId);


		  // 개인 일정 설정
		  
			if (colorSettings && schetypeId === "3") {
				colorSettings.innerHTML = `
					<div id="colorSettings" style="display: block;">
						<br> <label for="schBcolor" class="form-label">배경색</label> 
						<input type="color" id="schBColor" value="#ede9fb">
						<label for="schFcolor" class="form-label">글자색</label> 
						<input type="color" id="schFColor" value="#000000"> 
					</div>`;
			} else {
				if (colorSettings) colorSettings.innerHTML = ""; // 색상 설정 숨기기
			}
		

		// 일정 분류 기본값 설정 (모달 열릴 때)
		if (scheduleTypeInput) {
			if (isManager) {
				// 회사 관리자는 "회사" 일정 기본 선택
				scheduleTypeInput.value = "1";
			} else if (isDeptHead) {
				// 부서 관리자는 "회사" 일정 선택하지 않도록 기본값 제거
				scheduleTypeInput.value = "";
			} else {
				// 일반 사원은 "개인" 일정만 선택 가능
				scheduleTypeInput.value = "3";
			}
		}	

	



		// 일정 분류 기본값 설정
		// if (scheduleTypeInput) {
		// 	if (schetypeId === "1" && isManager) {
		// 		scheduleTypeInput.value = "1"; // 회사 일정으로 설정
		// 	} else if (schetypeId === "2" && isDeptHead) {
		// 		scheduleTypeInput.value = "2"; // 부서 일정으로 설정
		// 	} else if (schetypeId === "3") {
		// 		scheduleTypeInput.value = "3"; // 개인 일정으로 설정
		// 	}
		// }

		updateColorSettingsVisibility()


		// 전체(회사, 부서, 개인일정을 다보여줌)
		// 일정을 등록할때 권한에 따라 옵션을 다 달리 보여줌
		if (schetypeId === "") {
			if (isManager) {
				// 회사 관리자: 회사 일정 디폴트
				restrictScheduleTypeToOptions(["1"]);
			} else if (isDeptHead) {
				// 부서 관리자: 부서 일정 디폴트
				restrictScheduleTypeToOptions(["2", "3"]);
			} 
			else {
				// 일반 사원: 개인 일정 디폴트
				restrictScheduleTypeToOptions(["3"]);
			}
		}

		// 권한별로 옵션 숨기기

		if (isDeptHead && scheduleTypeInput) {
			removeOption(scheduleTypeInput, "1"); // "회사" 옵션 완전히 제거
			if (!["2", "3"].includes(scheduleTypeInput.value)) {
				// 기본값을 "2" (부서 일정)로 설정
				scheduleTypeInput.value = "2";
			}
		} else if (isManager && scheduleTypeInput) {
			removeOption(scheduleTypeInput, "3"); // 
			removeOption(scheduleTypeInput, "2"); // 
			updateButtonVisibility(false, "1"); // 등록 버튼 활성화


		} else if (isEmployee && scheduleTypeInput) {
			removeOption(scheduleTypeInput, "1"); // 일반 사원: "회사" 옵션 제거
			removeOption(scheduleTypeInput, "2"); // 일반 사원: "부서" 옵션 제거
			

		}
		
		// 버튼 상태 갱신 (여기서 호출)
		updateButtonVisibility(false, schetypeId);

		// 시간 설정
		const now = new Date();
		const start = new Date(info.start);
		start.setHours(now.getHours(), now.getMinutes(), 0);

		const end = new Date(info.end);
		end.setHours(0, 0, 0);

		mySchStart.value = toKoreaTimeISOString(start); // 한국 시간 적용
		mySchEnd.value = toKoreaTimeISOString(end);

		scheduleTypeInput.value = schetypeId;
	// 모달 열기
		const scheduleModal = new bootstrap.Modal(document.getElementById("basicModal"), {
			backdrop: "static",
			keyboard: false
		});
		scheduleModal.show();
	});



	const dataMappingBtnSche = document.getElementById("dataMappingBtnS");
		
	dataMappingBtnSche.addEventListener("click", (e) => {
		e.preventDefault();
		const colorSettings = document.querySelector("#colorSettings"); // 색상 설정 요소
		colorSettings.innerHTML = 
				` <div id="colorSettings" style="display: block;">
                <br> <label for="schBcolor" class="form-label">배경색</label> 
                <input type="color" id="schBColor" value="#ede9fb">
                <label for="schFcolor" class="form-label">글자색</label> 
                <input type="color" id="schFColor" value="#000000"> 
            </div>`;
		console.log("버튼 클릭됨");
		const scheduleType = document.getElementById("scheduleType");
		if (scheduleType) scheduleType.value = "3";

		const eventName = document.getElementById("eventName");
		if (eventName) eventName.value = "기획안 메일보내기";

		const eventContent = document.getElementById("eventContent");
		if (eventContent) eventContent.value = "work2gether 프로젝트 기획안 대리님이랑 부장님께 메일 18:00까지 보내기";

		console.log("데이터 맵핑 완료");
		
	});

	calendar.render();

	

	
	//등록 이벤트를 하는 부분
	document.getElementById("addEventBtn").addEventListener("click", () => {
		
	// 일정 유형에 따른 색상 처리
	const backgroundColor = document.querySelector("#schBColor")?.value || "#000000"; // 배경색 기본값
	const textColor = document.querySelector("#schFColor")?.value || "#FFFFFF"; // 글자색 기본값
	

		// 날짜 포맷을 ISO 8601 형식으로 변환
		const formatDate = (date) => {
			const d = new Date(date);
			const year = d.getFullYear();
			const month = String(d.getMonth() + 1).padStart(2, "0");
			const day = String(d.getDate()).padStart(2, "0");
			const hours = String(d.getHours()).padStart(2, "0");
			const minutes = String(d.getMinutes()).padStart(2, "0");
			return `${year}-${month}-${day}T${hours}:${minutes}`; // 초 단위 제거
		};

		
		const today = new Date();
		today.setHours(0, 0, 0, 0); // 오늘 날짜 자정으로 설정
		const yesterday = new Date(today);
		yesterday.setDate(today.getDate() - 1); // 오늘 날짜 -1

		const endDate = new Date(mySchEnd.value); // 종료일

		// 종료일 조건 확인
		if (endDate <= yesterday) {
			Swal.fire({
				icon: "error",
				title: "종료일 오류!",
				text: "종료일은 오늘보다 이전일 수 없습니다.",
				customClass: {
					popup: 'custom-swal-popup'
				}
			});
			mySchEnd.focus(); // 종료일 입력창으로 포커스 이동
			return; // 등록 중단
		}


		//제목을 입력하지 않은 경우 알러트창
		const scheduleTypeInput = document.querySelector("#scheduleType");

		if (!mySchTitle.value) {

			Swal.fire({
				icon: "error",
				title: "제목을 입력해주세요",
				customClass: {
					popup: 'custom-swal-popup'
				}
			});
			mySchTitle.focus();
			return;
		}

		if (!scheduleTypeInput || !scheduleTypeInput.value) {
			Swal.fire({
				icon: "error",
				title: "일정 유형을 선택해주세요",
				customClass: {
					popup: 'custom-swal-popup'
				}

			});
			scheduleTypeInput.focus(); // 포커스 설정
			return; // 등록 중단
		}
		// 드롭다운에서 선택한 일정 유형 가져오기
		schetypeId = scheduleTypeInput ? scheduleTypeInput.value : schetypeId;
		 const selectedType = scheduleTypeInput.value;
		const isManager = hiddenPosiId === "7"; // 회사 관리자 여부
		const isDeptHead = hiddenPosiId === "4"; // 부서 관리자 여부
		const isEmployee = !isManager && !isDeptHead; // 일반 사원 여부
		// 부서 관리자가 회사 일정(1)을 선택한 경우 등록 차단
		if (isDeptHead && selectedType === "1") {
			Swal.fire({
				icon: "error",
				title: "권한 없음",
				text: "부서 관리자는 회사 일정을 등록할 수 없습니다.",
			});
			return; // 등록 중단
		}
	
		// 일반 사원이 회사 일정(1) 또는 부서 일정(2)을 선택한 경우 등록 차단
		if (isEmployee && (selectedType === "1" || selectedType === "2")) {
			Swal.fire({
				icon: "error",
				title: "권한 없음",
				text: "일반 사원은 회사 또는 부서 일정을 등록할 수 없습니다.",
			});
			return; // 등록 중단
		}

		const event = {
			scheTitle: mySchTitle.value,  // 제목
			scheContent: mySchContent.value, // 상세 내용
			scheSdate: formatDate(mySchStart.value),  // 시작일 (ISO 형식)
			scheEdate: formatDate(mySchEnd.value),    // 종료일 (ISO 형식)
			scheLastup: empId, // 예시
			// 직원 ID
			schetypeId: schetypeId || "",
			departCode: schetypeId === "2" ? departCode : "",
			empId: schetypeId === "3" ? empId : "",
			empId: empId, // 권한 확인 후 설정된 empId
			empId: empId, // 권한 확인 후 설정된 empId
			scheduleColor: schetypeId === "3" ? { // 개인 일정일 경우에만 색상 설정
				scheBcolor: backgroundColor, 
				scheFcolor: textColor
			} : null // 개인 일정이 아니면 색상 설정 제외
		};
		console.log(event)

		

		fetch(`../schedule/add`, {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify(event),
		})
			.then((response) => {
				if (!response.ok) {
					throw new Error("서버 요청 실패");
				}
				return response.json();
			})
			.then((data) => {
				console.log("서버로부터 응답:", data);
				
				calendar.removeAllEventSources(); // 기존 소스 제거
					calendar.addEventSource({
						url: `../schedule`, // 새 데이터를 포함한 소스 추가
						method: "GET"
					});
				Swal.fire({

					icon: "success",
					title: "등록 완료!",
					showConfirmButton: false,
					timer: 1500
				});
				

				const scheduleModal = bootstrap.Modal.getInstance(document.getElementById("basicModal"));
				scheduleModal.hide();

			})
			.catch((error) => {
				console.error("Error:", error);
				Swal.fire({
					icon: "error",
					title: "일정 등록 실패!",
					text: "다시 일정을 등록해주세요!",
					customClass: {
						popup: 'custom-swal-popup'
					}

				});
			});
	});

	let currentScheId = null; // 현재 선택된 이벤트 ID 저장 변수

	//내가 등록된 일정을 눌렀을때~
	calendar.on("eventClick", (info) => {

		const event = info.event; // 클릭한 이벤트 객체
		currentScheId = event.id;  // 이벤트 ID (scheId)
		const eventOwnerId = event.extendedProps.empId; // 이벤트 소유자 ID
		
		const schetypeId = event.extendedProps.schetypeId || "";
		// 날짜 포맷 함수
		const formatDate2 = (date) => {
			const d = new Date(date);
			const year = d.getFullYear();
			const month = String(d.getMonth() + 1).padStart(2, "0");
			const day = String(d.getDate()).padStart(2, "0");
			const hours = String(d.getHours()).padStart(2, "0");
			const minutes = String(d.getMinutes()).padStart(2, "0");
			return `${year}-${month}-${day}T${hours}:${minutes}`; // 초 단위 제거
		};
		resetModalFields(); // 필드 초기화
		restrictScheduleTypeToOptionsAndSetDefault(schetypeId); // 권한 기반 옵션 제한 및 기본값 설정

		// 일정 분류 설정 및 수정 불가능하게 처리
		const scheduleTypeInput = document.querySelector("#scheduleType");
		
		if (scheduleTypeInput) {
			if (schetypeId === "1") {
				scheduleTypeInput.value = "1"; // 회사 일정
			} else if (schetypeId === "2") {
				scheduleTypeInput.value = "2"; // 부서 일정
			} else if (schetypeId === "3") {
				scheduleTypeInput.value = "3"; // 개인 일정
			} else {
				scheduleTypeInput.value = ""; // 기본값 (빈값)
			}
		}
	

		// 기존 이벤트 데이터를 모달에 채워 넣음
		// 시작일과 종료일을 포맷하여 설정
		mySchStart.value = event.start ? formatDate2(event.start) : ""; // 시작일
		mySchEnd.value = event.end ? formatDate2(event.end) : ""; // 종료일
		mySchTitle.value = event.title || "";
		mySchContent.value = event.extendedProps.scheContent || "";
		mySchAllday.checked = event.allDay || false;
			
		// 일정 분류 수정 불가능하도록 설정 (필요 시)
		if (scheduleTypeInput) {
			scheduleTypeInput.disabled = true; // 수정 불가능
		}

		// 색상 설정 (개인 일정인 경우)
		const colorSettings = document.querySelector("#colorSettings");
		if (colorSettings && event.extendedProps.schetypeId === "3") {
			colorSettings.innerHTML = `
				<div id="colorSettings" style="display: block;">
					<br> <label for="schBcolor" class="form-label">배경색</label> 
					<input type="color" id="schBColor" value="${event.backgroundColor || '#ede9fb'}">
					<label for="schFcolor" class="form-label">글자색</label> 
					<input type="color" id="schFColor" value="${event.textColor || '#000000'}">
				</div>`;
		} else {
			if (colorSettings) colorSettings.innerHTML = ""; // 색상 설정 숨기기
		}
	
		
		// 버튼 상태 업데이트(수정 ,삭제버튼)
		const hiddenPosiId = document.querySelector("#hiddenPosiId").value;
		const isManager = hiddenPosiId === "7"; // 회사 관리자
		const isDeptHead = hiddenPosiId === "4"; // 부서 관리자

		if (schetypeId === "1") {
			// 회사 일정: 회사 관리자만 수정/삭제 가능
			updateButtonVisibility(isManager, schetypeId, eventOwnerId);
		} else if (schetypeId === "2") {
			// 부서 일정: 부서 관리자만 수정/삭제 가능
			updateButtonVisibility(isDeptHead, schetypeId, eventOwnerId);
		} else if (schetypeId === "3") {
			// 개인 일정: 이벤트 소유자만 수정/삭제 가능
			updateButtonVisibility(eventOwnerId === empId, schetypeId, eventOwnerId);
		} else if (schetypeId === "") {
			// 전체 캘린더: 일정 유형별로 권한 처리
			const canEditCompany = isManager; // 회사 일정은 회사 관리자만 가능
			const canEditDept = isDeptHead && event.extendedProps.departCode === userDepartCode; // 부서 일정은 부서 관리자만 가능
			const canEditPersonal = eventOwnerId === empId; // 개인 일정은 소유자만 가능

			// 버튼 상태 업데이트 (조건에 따라 활성화)
			updateButtonVisibility(canEditCompany || canEditDept || canEditPersonal, schetypeId, eventOwnerId);
		}



		// 수정 버튼 클릭 시 서버로 수정된 데이터를 전송
		modifyEventBtnSchedule.onclick = () => {
			const backgroundColor = document.querySelector("#schBColor")?.value || "#000000"; // 기본 배경색
    		const textColor = document.querySelector("#schFColor")?.value || "#FFFFFF"; // 기본 글자색

			const today = new Date();
			today.setHours(0, 0, 0, 0); // 오늘 날짜 자정으로 설정
			const yesterday = new Date(today);
			yesterday.setDate(today.getDate() - 1); // 오늘 날짜 -1

			const endDate = new Date(mySchEnd.value); // 종료일

			// 종료일 조건 확인
			if (endDate <= yesterday) {
				Swal.fire({
					icon: "error",
					title: "종료일 오류!",
					text: "종료일은 오늘보다 이전일 수 없습니다.",
					customClass: {
						popup: 'custom-swal-popup'
					}
				});
				mySchEnd.focus(); // 종료일 입력창으로 포커스 이동
				return; // 수정 중단
			}



			if (!mySchTitle.value) {
				Swal.fire({
					icon: "error",
					title: "일정 제목 누락!",
					text: "일정 제목을 입력해주세요!",
					customClass: {
						popup: 'custom-swal-popup'
					}

				});
				mySchTitle.focus();
				return;
			}
			
			const updatedEvent = {
				scheId: event.id,  // 일정번호
				scheSdate: mySchStart.value,  // 시작일
				scheEdate: mySchEnd.value,    // 종료일
				scheTitle: mySchTitle.value,  // 제목
				scheContent: mySchContent.value, // 상세 내용
				scheLastup: empId,            // 수정자 ID
				empId: event.extendedProps.empId, // 직원 ID (기존 값 유지)
				schetypeId: schetypeId, // 기존 값 유지 // 기존 이벤트 유형 유지   
				departCode: schetypeId === "2" ? departCode : "",
				empId: empId, // 권한 확인 후 설정된 empId
				scheduleColor: schetypeId === "3" ? { // 개인 일정일 경우에만 색상 설정
					scheBcolor: backgroundColor, 
					scheFcolor: textColor
				} : null // 개인 일정이 아니면 색상 설정 제외
			};

			// 서버로 수정 요청
			fetch(`../schedule/${event.id}/edit`, {
				method: "POST",
				headers: {
					"Content-Type": "application/json"
				},
				body: JSON.stringify(updatedEvent)
			})
				.then((response) => {
					if (!response.ok) {
						throw new Error("서버 요청 실패");
					}
					return response.json();
				})
				.then((data) => {
					console.log("서버 응답:", data);
					
					//   // 일정 유형에 따른 색상 업데이트 처리
					//   if (schetypeId === "1" || schetypeId === "2") {
					// 	// 회사/부서 일정의 경우
					// 	event.setProp("backgroundColor", updatedEvent.scheduleColor.scheBcolor);
					// 	event.setProp("textColor", updatedEvent.scheduleColor.scheFcolor);
					// } else if (schetypeId === "3") {
					// 	// 개인 일정의 경우
					// 	event.setProp("backgroundColor", updatedEvent.scheduleColor.scheBcolor);
					// 	event.setProp("textColor", updatedEvent.scheduleColor.scheFcolor);
					// }
			
					// FullCalendar 이벤트 객체 업데이트
					event.setStart(updatedEvent.scheSdate);
					event.setEnd(updatedEvent.scheEdate);
					event.setProp("title", updatedEvent.scheTitle);
					event.setExtendedProp("scheContent", updatedEvent.scheContent);

					Swal.fire({

						icon: "success",
						title: "수정 완료!",
						showConfirmButton: false,
						timer: 1500
					});
					calendar.refetchEvents(); // 서버에서 다시 이벤트를 불러옵니다.
				})
				.catch((error) => {
					console.error("Error:", error);
					Swal.fire({
						icon: "error",
						title: "수정 실패!",
						text: "다시 일정을 수정해주세요!",
						customClass: {
							popup: 'custom-swal-popup'
						}

					});
				});

			const scheduleModal = bootstrap.Modal.getInstance(document.getElementById("basicModal"));
			scheduleModal.hide();
		};

		deleteEventBtnSchedule.onclick = () => {



			const scheduleModal = bootstrap.Modal.getInstance(document.getElementById("basicModal"));
			scheduleModal.hide();


			Swal.fire({
				title: "정말 삭제하시겠습니까?",
				text: "삭제 후에는 복구할 수 없습니다!",
				icon: "warning",
				showCancelButton: true,
				confirmButtonColor: "#3085d6",
				cancelButtonColor: "#d33",
				confirmButtonText: "네, 삭제합니다!",
				cancelButtonText: "취소"
			}).then((result) => {
				if (result.isConfirmed) {
					// 삭제 요청 AJAX 호출
					$.ajax({
						url: `../schedule/${currentScheId}/delete`,
						type: "DELETE",
						data: JSON.stringify({
							empId: empId,
							departCode: document.querySelector("#hiddenDeptCode").value
						}),
						contentType: "application/json",
						dataType: "text",
						async: true,
						success: function(result) {
							if (result === "success") {
								info.event.remove();
								calendar.refetchEvents(); // 서버에서 데이터 다시 가져오기
								Swal.fire({
									title: "삭제 완료!",
									text: "이벤트가 성공적으로 삭제되었습니다.",
									icon: "success"
								});
							} else {
								Swal.fire({
									icon: "error",
									title: "삭제 실패!",
									text: "삭제에 실패하였습니다. 다시 시도해주세요."
								});
							}
						},
						error: function(error) {
							console.error("삭제 요청 실패:", error);
							Swal.fire({
								icon: "error",
								title: "삭제 실패!",
								text: "서버 오류로 삭제에 실패하였습니다."
							});
						},
						complete: function() {
							deleteEventBtnSchedule.disabled = false;
							deleteEventBtnSchedule.textContent = "삭제";
						},
					});
				}
			});
		}
		const scheduleModal = new bootstrap.Modal(document.getElementById("basicModal"), {
			backdrop: "static",
			keyboard: false
		});
		scheduleModal.show();
	});

	// 종일 체크박스 변경 이벤트 처리
	mySchAllday.addEventListener("change", function() {
		if (mySchAllday.checked) {
			// 종일이 체크되었을 때: 시작일은 현재 시간, 종료일은 다음날 자정
			const now = new Date();
			const startFormatted = toKoreaTimeISOString(now);

			// 종료일: 다음날 자정
			const endOfDay = new Date(now);
			endOfDay.setDate(endOfDay.getDate() + 1); // 다음날
			endOfDay.setHours(0, 0, 0, 0); // 자정으로 설정
			const endFormatted = toKoreaTimeISOString(endOfDay);

			// 설정된 값 적용
			mySchStart.value = startFormatted;
			mySchEnd.value = endFormatted;

			// 시작일과 종료일 비활성화
			mySchStart.disabled = true;
			mySchEnd.disabled = true;
		} else {
			// 종일 체크 해제 시: 시작일과 종료일 활성화
			mySchStart.disabled = false;
			mySchEnd.disabled = false;

			// 기존 값을 유지하도록 처리
			if (!mySchStart.value) {
				const now = new Date();
				mySchStart.value = toKoreaTimeISOString(now);
			}
			if (!mySchEnd.value) {
				const later = new Date();
				later.setHours(later.getHours() + 1); // 1시간 이후로 설정
				mySchEnd.value = toKoreaTimeISOString(later);
			}
		}
	});

	calendar.on("eventDrop", function(info) {


		const event = info.event; // 이동된 이벤트 객체

		const eventOwnerId = event.extendedProps.empId; // 이벤트 소유자 ID
		const schetypeId = event.extendedProps.schetypeId; // 일정 유형 (1: 회사, 2: 부서, 3: 개인)
		const userDeptCode = document.querySelector("#hiddenDeptCode").value; // 현재 사용자의 부서 코드
		const hiddenPosiId = document.querySelector("#hiddenPosiId").value; // 현재 사용자의 직급 ID
		const empId = document.querySelector("#hiddenEmpId").value; // 현재 사용자 ID

		const isManager = hiddenPosiId === "7"; // 회사 관리자 여부
		const isDeptHead = hiddenPosiId === "4"; // 부서 관리자 여부

		// 날짜 포맷 함수
		const formatDate = (date) => {
			const d = new Date(date);
			const year = d.getFullYear();
			const month = String(d.getMonth() + 1).padStart(2, "0");
			const day = String(d.getDate()).padStart(2, "0");
			const hours = String(d.getHours()).padStart(2, "0");
			const minutes = String(d.getMinutes()).padStart(2, "0");
			return `${year}-${month}-${day}T${hours}:${minutes}`; // 초 단위 제거
		};

		// 권한 제한 처리
		if (
			(schetypeId === "1" && !isManager) || // 회사 일정: 회사 관리자만 수정 가능
			(schetypeId === "2" && (!isDeptHead || event.extendedProps.departCode !== userDeptCode)) || // 부서 일정: 부서 관리자만 수정 가능
			(schetypeId === "3" && eventOwnerId !== empId) // 개인 일정: 소유자만 수정 가능
		) {
			Swal.fire({
				icon: "error",
				title: "권한 부족!",
				text: "해당 일정을 수정할 권한이 없습니다.",
				customClass: {
					popup: "custom-swal-popup",
				},
			});
			info.revert(); // 이벤트 위치 되돌리기
			return;
		}

		// 시작일과 종료일을 포맷하여 설정
		const startDate = formatDate(event.start);
		const endDate = formatDate(event.end);
		// 종료일이 오늘 날짜 -1 보다 작거나 같은 경우 처리
		const today = new Date();
		today.setHours(0, 0, 0, 0); // 오늘 날짜 자정으로 설정
		const yesterday = new Date(today);
		yesterday.setDate(today.getDate() - 1); // 오늘 날짜 -1

		
		if (endDate <= yesterday) {
			Swal.fire({
				icon: "error",
				title: "종료일 오류!",
				text: "종료일은 오늘보다 이전일 수 없습니다.",
				customClass: {
					popup: 'custom-swal-popup'
				}
			});
			info.revert(); // 이벤트 위치 되돌리기
			return; // 수정 중단
		}



		// 이동 확인 다이얼로그
		Swal.fire({
			title: "일정을 수정하시겠습니까?",
			text: "이동된 일정은 서버에 업데이트됩니다.",
			icon: "warning",
			showCancelButton: true,
			confirmButtonColor: "#3085d6",
			cancelButtonColor: "#d33",
			confirmButtonText: "확인",
			cancelButtonText: "취소"
		}).then((result) => {
			if (result.isConfirmed) {

				// 수정된 이벤트 데이터 객체
				const updatedEvent = {
					scheId: event.id, // 일정번호
					scheSdate: startDate, // 시작일 (드롭된 일정의 새로운 시작일)
					scheEdate: endDate, // 종료일 (드롭된 일정의 새로운 종료일)
					scheTitle: event.title, // 제목
					scheContent: event.extendedProps.scheContent || "", // 상세 내용
					scheLastup: empId, // 수정자 ID
					empId: event.extendedProps.empId, // 직원 ID (기존 값 유지)
					schetypeId: event.extendedProps.schetypeId, // 기존 값 유지
					departCode: event.extendedProps.departCode || "", // 부서 코드
					scheBcolor: event.backgroundColor || "#000000", // 배경색
					scheFcolor: event.textColor || "#FFFFFF" // 글자색
				};



				// 서버로 수정 요청 전송
				fetch(`../schedule/${event.id}/edit`, {
					method: "POST",
					headers: {
						"Content-Type": "application/json"
					},
					body: JSON.stringify(updatedEvent)
				})
					.then(response => {
						if (!response.ok) {
							throw new Error("서버 요청 실패");
						}
						return response.json();
					})
					.then(data => {
						console.log("일정 수정 성공:", data);
						Swal.fire({
							icon: "success",
							title: "일정이 수정되었습니다!",
							showConfirmButton: false,
							timer: 1500
						});
						calendar.refetchEvents(); // 서버에서 다시 이벤트를 불러옵니다.
					})
					.catch(error => {
						console.error("일정 수정 실패:", error);
						// 수정 실패 시 이전 위치로 되돌림
						info.revert();
						Swal.fire({
							icon: "error",
							title: "수정 실패!",
							text: "일정을 다시 시도해주세요!"
						});
					});
			} else {
				info.revert(); // 사용자가 취소를 누르면 이벤트를 원래 위치로 되돌림
			}
		});
	});


	// 관리자가 회사랑 부서 일정 색깔 커스텀 하는 것
// 	document.getElementById("colorBtnSchedule").addEventListener("click", () => {
// 		const colorModal = new bootstrap.Modal(document.getElementById("colorCustomizationModal"));  
// 		// 서버에서 색상 데이터를 가져오기
// 		fetch(`${contextPathSchedule}/${companyIdS}/schedule/getColors`, {
// 		  method: "GET",
// 		  headers: { "Content-Type": "application/json" }
// 		})
// 		  .then(response => {
// 			if (!response.ok) throw new Error("Failed to fetch color settings");
// 			return response.json();
// 		  })
// 		  .then(data => {
// 			// 서버에서 가져온 색상 데이터를 모달 필드에 반영
// 			document.getElementById("companyBackgroundColor").value = data.companyBackgroundColor || "#87CEEB"; // 기본값
// 			document.getElementById("companyTextColor").value = data.companyTextColor || "#000000"; // 기본값
// 			document.getElementById("deptBackgroundColor").value = data.deptBackgroundColor  // 기본값
// 			document.getElementById("deptTextColor").value = data.deptTextColor || "#000000"; // 기본값
// 			 console.log("Fetched color settings:", data); // 서버 응답 데이터를 확인
// 		  })
// 		  .catch(error => {
// 			console.error("Error fetching color settings:", error);
// 			Swal.fire({
// 			  icon: "error",
// 			  title: "색상 데이터를 불러오는 데 실패했습니다.",
// 			  text: "다시 시도해주세요."
// 			});
// 		  });
	  
// 		colorModal.show();
// 	  });
	 
	
// 	  function saveColorSettings(companyBackgroundColor, companyTextColor, deptBackgroundColor, deptTextColor) {
// 		const updateRequests = [
// 			// 회사 일정 색상 저장
// 			fetch(`${contextPathSchedule}/${companyIdS}/schedule/updateColors`, {
// 				method: "POST",
// 				headers: {
// 					"Content-Type": "application/json",
// 				},
// 				body: JSON.stringify({
// 					schetypeId: "1",
// 					backgroundColor: companyBackgroundColor,
// 					textColor: companyTextColor,
// 				}),
// 			}),
// 			// 부서 일정 색상 저장
// 			fetch(`${contextPathSchedule}/${companyIdS}/schedule/updateColors`, {
// 				method: "POST",
// 				headers: {
// 					"Content-Type": "application/json",
// 				},
// 				body: JSON.stringify({
// 					schetypeId: "2",
// 					backgroundColor: deptBackgroundColor,
// 					textColor: deptTextColor,
// 				}),
// 			}),
// 		];
	
// 		Promise.all(updateRequests)
// 			.then((responses) => {
// 				if (responses.some((res) => !res.ok)) {
// 					throw new Error("일부 색상 업데이트 실패");
// 				}
// 				return Promise.all(responses.map((res) => res.json()));
// 			})
// 			.then((data) => {
// 				console.log("색상 업데이트 성공:", data);
	
// 				// 모달 닫기
// 				const colorModal = bootstrap.Modal.getInstance(document.getElementById("colorCustomizationModal"));
// 				colorModal.hide();
	
// 				Swal.fire({
// 					icon: "success",
// 					title: "색상 업데이트 성공!",
// 					text: "변경된 색상이 저장되었습니다.",
// 				});
	
// 				// 캘린더 리로드 (필요 시)
// 				calendar.refetchEvents();
// 			})
// 			.catch((error) => {
// 				console.error("색상 업데이트 실패:", error);
// 				Swal.fire({
// 					icon: "error",
// 					title: "색상 업데이트 실패!",
// 					text: "다시 시도해주세요.",
// 				});
// 			});
// 	}
	
// 	document.getElementById("saveColorSettingsBtn").addEventListener("click", () => {
//     const companyBackgroundColor = document.getElementById("companyBackgroundColor").value;
//     const companyTextColor = document.getElementById("companyTextColor").value;
//     const deptBackgroundColor = document.getElementById("deptBackgroundColor").value;
//     const deptTextColor = document.getElementById("deptTextColor").value;

//     // 입력값 검증
//     if (!companyBackgroundColor || !companyTextColor || !deptBackgroundColor || !deptTextColor) {
//         Swal.fire({
//             icon: "warning",
//             title: "입력값 확인 필요",
//             text: "모든 색상 값을 선택해주세요.",
//         });
//         return;
//     }

//     // 색상 데이터 서버로 전송
//     saveColorSettings(companyBackgroundColor, companyTextColor, deptBackgroundColor, deptTextColor);
// });
	  

function fetchColors(url, method, body = null) {
    return fetch(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: body ? JSON.stringify(body) : null,
    });
}

	// 색상 데이터를 서버에서 가져오기
document.getElementById("colorBtnSchedule").addEventListener("click", () => {
    const colorModal = new bootstrap.Modal(document.getElementById("colorCustomizationModal"));
    fetchColors(`${contextPathSchedule}/${companyIdS}/schedule/getColors`, "GET")
        .then(response => {
            if (!response.ok) throw new Error("Failed to fetch color settings");
            return response.json();
        })
        .then(data => {
            document.getElementById("companyBackgroundColor").value = data.companyBackgroundColor || "#87CEEB";
            document.getElementById("companyTextColor").value = data.companyTextColor || "#000000";
            document.getElementById("deptBackgroundColor").value = data.deptBackgroundColor || "#FFFFFF";
            document.getElementById("deptTextColor").value = data.deptTextColor || "#000000";
        })
        .catch(error => console.error("Error fetching color settings:", error));
    colorModal.show();
});

// 색상 데이터 서버에 저장
document.getElementById("saveColorSettingsBtn").addEventListener("click", () => {
    const companyBackgroundColor = document.getElementById("companyBackgroundColor").value;
    const companyTextColor = document.getElementById("companyTextColor").value;
    const deptBackgroundColor = document.getElementById("deptBackgroundColor").value;
    const deptTextColor = document.getElementById("deptTextColor").value;

    if (!companyBackgroundColor || !companyTextColor || !deptBackgroundColor || !deptTextColor) {
        Swal.fire({
            icon: "warning",
            title: "입력값 확인 필요",
            text: "모든 색상 값을 선택해주세요.",
        });
        return;
    }

    Promise.all([
        fetchColors(`${contextPathSchedule}/${companyIdS}/schedule/updateColors`, "POST", {
            schetypeId: "1",
            backgroundColor: companyBackgroundColor,
            textColor: companyTextColor,
        }),
        fetchColors(`${contextPathSchedule}/${companyIdS}/schedule/updateColors`, "POST", {
            schetypeId: "2",
            backgroundColor: deptBackgroundColor,
            textColor: deptTextColor,
        }),
    ])
        .then(responses => {
            if (responses.some(res => !res.ok)) throw new Error("Some updates failed");
            return Promise.all(responses.map(res => res.json()));
        })
        .then(() => {
            const colorModal = bootstrap.Modal.getInstance(document.getElementById("colorCustomizationModal"));
            colorModal.hide();
            Swal.fire({
                icon: "success",
                title: "색상 업데이트 성공!",
                text: "변경된 색상이 저장되었습니다.",
            });
            calendar.refetchEvents();
        })
        .catch(error => {
            console.error("Error updating color settings:", error);
            Swal.fire({
                icon: "error",
                title: "색상 업데이트 실패!",
                text: "다시 시도해주세요.",
            });
        });
});





});

 

//회사, 부서 색깔
// document.getElementById("saveColorSettingsBtn").addEventListener("click", () => {
// 	const companyBackgroundColor = document.getElementById("companyBackgroundColor").value;
// 	const companyTextColor = document.getElementById("companyTextColor").value;
// 	const deptBackgroundColor = document.getElementById("deptBackgroundColor").value;
// 	const deptTextColor = document.getElementById("deptTextColor").value;

// 	const updateRequests = [
// 		fetch(`${contextPathSchedule}/${companyIdS}/schedule/updateColors`, {
// 			method: "POST",
// 			headers: {
// 				"Content-Type": "application/json",
// 			},
// 			body: JSON.stringify({
// 				schetypeId: "1",
// 				backgroundColor: companyBackgroundColor,
// 				textColor: companyTextColor,
// 			}),
// 		}),
// 		fetch(`${contextPathSchedule}/${companyIdS}/schedule/updateColors`, {
// 			method: "POST",
// 			headers: {
// 				"Content-Type": "application/json",
// 			},
// 			body: JSON.stringify({
// 				schetypeId: "2",
// 				backgroundColor: deptBackgroundColor,
// 				textColor: deptTextColor,
// 			}),
// 		}),
// 	];

// 	Promise.all(updateRequests)
// 		.then((responses) => {
// 			if (responses.some((res) => !res.ok)) {
// 				throw new Error("Some updates failed");
// 			}
// 			return Promise.all(responses.map((res) => res.json()));
// 		})
// 		.then((data) => {
// 			// 기존 이벤트 소스 제거 후 새로 추가
// 			calendar.removeAllEventSources();
// 			calendar.addEventSource({
// 				url: `${contextPathSchedule}/${companyIdS}/schedule`,
// 				method: "GET",
// 			});
			
			
// 			const colorModal = bootstrap.Modal.getInstance(document.getElementById("colorCustomizationModal"));
// 			colorModal.hide();
// 			calendar.refetchEvents(); // 서버에서 업데이트된 색상을 가져옵니다.
// 			Swal.fire({
// 				icon: "success",
// 				title: "색상 업데이트 성공!",
// 				text: "모든 색상이 저장되었습니다.",
// 			});
			
// 		})
// 		.catch((error) => {
// 			console.error("Error updating color settings:", error);
// 			Swal.fire({
// 				icon: "error",
// 				title: "색상 업데이트 실패!",
// 				text: "다시 시도해주세요.",
// 			});
// 		});
// });
