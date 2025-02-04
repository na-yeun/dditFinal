/**
 * 
 */
// 객관형 추가함수
function appendMultiForm(area, num) {
	area.insertAdjacentHTML("afterend",
		`<div class="question-area">
			<p class="question-title">질문내용을 입력해주세요.</p>
			<input type="text" class="form-control q-content" placeholder="ex_ 근무연차를 선택해주세요." required="required"/>
			
			<p class="question-title">중복 선택이 가능한가요?</p>
			<input type="radio" name="dupleyn-${num}" value="Y" class="form-check-input duple"/>예
			<input type="radio" name="dupleyn-${num}" value="N" class="form-check-input duple dupleyn-check" checked/>아니오
		</div>
		<div class="item">
			<p class="question-title">질문의 항목을 입력해주세요. <span class="form-info">항목은 추가된 순서대로 왼쪽부터 화면에 출력됩니다.</span></p>
			<input type="text" class="form-control qitem" placeholder="ex_매우만족" required="required" maxlength="1000"/>
		</div>	
		<div class="question-btn-area">
			<button type="button" class="btn btn-primary btn-sm plus-item">항목추가</button>
			<button type="button" class="btn btn-success btn-sm plus-question">질문추가</button>
			<button type="button" class="btn btn-danger btn-sm delete-question">질문삭제</button>
		</div>`);
}

// 서술형 추가함수
function appendSubjForm(area) {
	area.insertAdjacentHTML("AfterEnd",
		`<div class="question-area">
			<p class="question-title">질문내용을 입력해주세요.</p>
			<input type="text" class="form-control q-content" placeholder="ex_ 근무연차를 선택해주세요." maxlength="1000"/>
			<div class="item">
				<input type="hidden" class="form-control qitem" value="자유롭게 입력해주세요."/>
			</div>
		</div>
		<div class="question-btn-area">
			<button type="button" class="btn btn-success btn-sm plus-question">질문추가</button>
			<button type="button" class="btn btn-danger btn-sm delete-question">질문삭제</button>
		</div>
		`);
}

document.addEventListener("DOMContentLoaded", () => {
	const surveyForm = document.querySelector('#survey-form');
	const cancelBtn = document.querySelector('#cancel-btn');
	const surboardStdate = document.querySelector('#surboardStdate');
	const surboardEnddate = document.querySelector('#surboardEnddate');
	
		
	const myEmpId = surveyForm.dataset.empId;
	
	
	
	// 발표용 데이터 넣는 버튼
	
	const insertDataBtn = document.querySelector('#insert-pre-btn');
	if(insertDataBtn){
		insertDataBtn.addEventListener("click", () => {
			document.querySelector('#surboardNm').value = '교육 및 성장 기회 조사';
		    document.querySelector('#surboardContent').value = '자신의 역량을 개발할 기회를 충분히 제공받고 있는지에 대한 조사를 진행합니다.';
		    document.querySelector('#surboardStdate').value = '2025-01-20';
		    document.querySelector('#surboardEnddate').value = '2025-01-26';
		})
	}
	 
	
	
	surboardStdate.addEventListener("blur", () => {
		let stdate = new Date(surboardStdate.value);
		let today = new Date();
		today.setHours(0, 0, 0, 0);
		if(stdate<today){
			Swal.fire({
				title: "시작날짜를 다시 설정해주세요.",
				html: "시작 날짜가 오늘 날짜보다 이전일 수 없습니다.",
				icon: "warning",

				// confirm 버튼 텍스트
				confirmButtonText: '확인',
				
				// confirm 버튼 색 지정(승인)
				confirmButtonColor: 'blue',
				// cancel 버튼 색 지정(취소)
				cancelButtonColor: 'red',

				// resolve 함수 생각하면 good...
			})
			surboardStdate.value="";
		}
	})
	
	surboardEnddate.addEventListener("blur", () => {
		let stdate = new Date(surboardStdate.value);
		let enddate = new Date(surboardEnddate.value);
		if(stdate>enddate){
			Swal.fire({
				title: "종료 날짜를 다시 설정해주세요.",
				html: "종료 날짜가 시작 날짜보다 이전일 수 없습니다.",
				icon: "warning",
	
				// confirm 버튼 텍스트
				confirmButtonText: '확인',
				
				// confirm 버튼 색 지정(승인)
				confirmButtonColor: 'blue',
				// cancel 버튼 색 지정(취소)
				cancelButtonColor: 'red',
	
				// resolve 함수 생각하면 good...
			})
			surboardEnddate.value="";
		}
	});
	

	// input 이벤트
	document.addEventListener("input", e => {
		// 질문내용 입력시 그 값을 질문 옆에 출력하기
		if (e.target.classList.contains("q-content")) {
			let contentValue = e.target.value;
			//q-content-value
			let papa = e.target.parentNode;
			let grandpa = papa.parentNode;
			let contentArea = grandpa.querySelector('.q-content-value')
			contentArea.innerHTML = ` : ${contentValue}`;
		}
		
	})

	// 변경이벤트
	document.addEventListener("change", e => {
		
		// 객관형 전부 셀렉트 해서 개수 새기
		let multiCount = document.querySelectorAll('.select-categoty');
		let num = multiCount.length;
		
		
		// 객관형, 서술형 선택시 이벤트
		if (e.target.classList.contains("select-categoty")) {
			let surquesType = e.target.value;
			// 객관형 선택시
			if (surquesType == 'S_MULTI') {

				// 혹시 바뀌었을 수 있으니까 초기화를 해야함
				let mydiv = e.target.closest('div');
				let beforeQuestionArea = mydiv.querySelector('.question-area');
				let beforeQuestionBtnArea = mydiv.querySelector('.question-btn-area');
				if (beforeQuestionArea && beforeQuestionBtnArea) {
					Swal.fire({
						title: "질문 종류를 변경하시겠습니까?",
						html: "질문의 종류를 변경하면 해당 질문에 작성하신 내용은 사라집니다. <br> 변경하시겠습니까?",
						icon: "warning",

						// candel 버튼 (기본값 false, 취소버튼 안 보임)
						showCancelButton: true,
						// confirm 버튼 텍스트
						confirmButtonText: '변경',
						// cancel 버튼 텍스트
						cancelButtonText: '취소',
						// confirm 버튼 색 지정(승인)
						confirmButtonColor: 'blue',
						// cancel 버튼 색 지정(취소)
						cancelButtonColor: 'red',

						// resolve 함수 생각하면 good...
					}).then(result => {
						// result(결과)가 confirm이면(승인이면)
						if (result.isConfirmed) {
							beforeQuestionArea.remove();
							beforeQuestionBtnArea.remove();

							// 객관형 양식 추가
							num++;
							appendMultiForm(e.target, num);

						} else {
							// 취소를 눌렀을 경우
							e.target.value = 'S_SUBJ';
							return;
						}
					})
				} else {
					// 객관형 양식 추가
					num++;
					appendMultiForm(e.target, num);
				}


				// 서술형 선택시	
			} else if (surquesType == 'S_SUBJ') {
				// 혹시 바뀌었을 수 있으니까 초기화를 해야함
				let mydiv = e.target.closest('div');
				let beforeQuestionArea = mydiv.querySelector('.question-area');
				let beforeQuestionBtnArea = mydiv.querySelector('.question-btn-area');
				
				if (beforeQuestionArea && beforeQuestionBtnArea) {
					let beforeItems = mydiv.querySelectorAll('.item');
					let beforeBtns = mydiv.querySelectorAll('.question-btn-area');
					console.log("beforeItems",beforeItems);
					Swal.fire({

						title: "질문 종류를 변경하시겠습니까?",
						html: "질문의 종류를 변경하면 해당 질문에 작성하신 내용은 사라집니다. <br> 변경하시겠습니까?",
						icon: "warning",

						// candel 버튼 (기본값 false, 취소버튼 안 보임)
						showCancelButton: true,
						// confirm 버튼 텍스트
						confirmButtonText: '변경',
						// cancel 버튼 텍스트
						cancelButtonText: '취소',
						// confirm 버튼 색 지정(승인)
						confirmButtonColor: 'blue',
						// cancel 버튼 색 지정(취소)
						cancelButtonColor: 'red',

						// resolve 함수 생각하면 good...
					}).then(result => {
						// result(결과)가 confirm이면(승인이면)
						if (result.isConfirmed) {
							beforeQuestionArea.remove();
							beforeQuestionBtnArea.remove();
							beforeItems.forEach(element=>element.remove());
							beforeBtns.forEach(element=>element.remove());

							// 서술형 양식 추가
							appendSubjForm(e.target);
						} else {
							e.target.value = 'S_MULTI';
							return;
						}
					})

				} else {
					// 서술형 양식 추가
					appendSubjForm(e.target);
				}
			}
		}


	});

	// 클릭 관련 이벤트들
	document.addEventListener("click", e => {
		// '항목추가' 버튼 클릭
		if (e.target.classList.contains("plus-item")) {
			let appendItem = e.target.closest('div');
			appendItem.insertAdjacentHTML("AfterEnd",
				`<div class="item">
						<p class="question-title">질문의 항목을 입력해주세요. <span class="form-info">항목은 추가된 순서대로 왼쪽부터 화면에 출력됩니다.</span></p>
							<input type="text" class="form-control qitem " placeholder="ex_기타"/>
					</div>
					<div class="question-btn-area">
						<button type="button" class="btn btn-primary btn-sm plus-item">항목추가</button>
						<button type="button" class="btn btn-success btn-sm plus-question">질문추가</button>
						<button type="button" class="btn btn-danger btn-sm delete-question">질문삭제</button>
					</div>`
			);

			appendItem.innerHTML = `<button type="button" class="btn btn-warning btn-sm delete-item">항목삭제</button>`;

		}

		// 항목삭제 버튼 클릭(항목영역과 버튼영역 모두 삭제)
		if (e.target.classList.contains("delete-item")) {
			let btnArea = e.target.closest('div');
			let itemArea = btnArea.previousElementSibling;

			btnArea.remove();
			itemArea.remove();
		}

		// '질문추가' 버튼 클릭
		if (e.target.classList.contains("plus-question")) {
			let appendArea = e.target.closest('div').parentElement;

			appendArea.insertAdjacentHTML("AfterEnd",
				`<div class="q-drag-area">
					<div class="q-area" draggable="true">
						<p class="question">
							질문 <span class="q-content-value"></span>
							<span class="collapse-icon-area">
								<svg class="collapse-icon bf-collapse" xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor" class="bi bi-arrows-collapse" viewBox="0 0 16 16">
								  <path fill-rule="evenodd" d="M1 8a.5.5 0 0 1 .5-.5h13a.5.5 0 0 1 0 1h-13A.5.5 0 0 1 1 8m7-8a.5.5 0 0 1 .5.5v3.793l1.146-1.147a.5.5 0 0 1 .708.708l-2 2a.5.5 0 0 1-.708 0l-2-2a.5.5 0 1 1 .708-.708L7.5 4.293V.5A.5.5 0 0 1 8 0m-.5 11.707-1.146 1.147a.5.5 0 0 1-.708-.708l2-2a.5.5 0 0 1 .708 0l2 2a.5.5 0 0 1-.708.708L8.5 11.707V15.5a.5.5 0 0 1-1 0z"/>
								</svg>
								
								
								<svg class="collapse-icon af-collapse hidden" xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor" class="bi bi-arrows-expand" viewBox="0 0 16 16">
								  <path fill-rule="evenodd" d="M1 8a.5.5 0 0 1 .5-.5h13a.5.5 0 0 1 0 1h-13A.5.5 0 0 1 1 8M7.646.146a.5.5 0 0 1 .708 0l2 2a.5.5 0 0 1-.708.708L8.5 1.707V5.5a.5.5 0 0 1-1 0V1.707L6.354 2.854a.5.5 0 1 1-.708-.708zM8 10a.5.5 0 0 1 .5.5v3.793l1.146-1.147a.5.5 0 0 1 .708.708l-2 2a.5.5 0 0 1-.708 0l-2-2a.5.5 0 0 1 .708-.708L7.5 14.293V10.5A.5.5 0 0 1 8 10"/>
								</svg>
							</span>
						</p>
						
						<p class="question-title">질문의 종류를 선택해주세요.</p>
						<select class="form-select mb-0 select-categoty" required="required">
							<option value>--------종류--------</option>
							<option value="S_MULTI">객관형</option>
							<option value="S_SUBJ">서술형</option>
						</select>
					</div>
				</div>`
			);
		}

		// 질문삭제 버튼 클릭
		if (e.target.classList.contains("delete-question")) {
			// 질문이 한개밖에 없을 때는 삭제를 막을 거임
			let qAreaAll = document.querySelectorAll('.q-area');
			let qAreaAllCount = qAreaAll.length;
			if (qAreaAllCount == 1) {
				Swal.fire({
					title: '삭제 실패',
					html: '질문이 한 개일 때는 <br>질문을 삭제할 수 없습니다.',
					icon: 'error'
				});
			} else {
				let qArea = e.target.closest('div').parentElement;
				qArea.remove();
			}
		}

		// 접기 펼치기 버튼 클릭시
		if (e.target.classList.contains("collapse-icon")) {
			let parentPTag = e.target.parentNode;
			let qDiv = parentPTag.closest('div');
			let all = qDiv.querySelectorAll("*:not(.collapse-icon-area):not(.collapse-icon):not(path):not(.q-content-value)");

			let bf = parentPTag.querySelector('.bf-collapse');
			let af = parentPTag.querySelector('.af-collapse');
			bf.classList.toggle("hidden");
			af.classList.toggle("hidden");
			all.forEach(element => {
				element.classList.toggle("hidden");
			})
		}

	});


	// 드래그 관련 이벤트들
	document.addEventListener("dragstart", e => {
		if (e.target.classList.contains("q-area")) {
			e.target.classList.add("dragging");
		}
	});

	document.addEventListener("dragend", e => {
		if (e.target.classList.contains("q-area")) {
			e.target.classList.remove("dragging");
		}
	})

	document.addEventListener("dragover", e => {
		e.preventDefault();

		const dragArea = e.target.closest(".q-drag-area");
		if (!dragArea) return;

		const afterElement = getDragAfterElement(dragArea, e.clientY);
		const draggable = document.querySelector(".dragging");

		if (afterElement === null) {
			dragArea.appendChild(draggable);
		} else {
			dragArea.insertBefore(draggable, afterElement);
		}
	})

	// 특정 위치에 드롭된 요소를 찾는 함수
	function getDragAfterElement(container, y) {
		// 컨테이너 내부의 q-area 요소를 선택
		const draggableElements = [...container.querySelectorAll(".q-area:not(.dragging)")];

		return draggableElements.reduce(
			(closest, child) => {
				const box = child.getBoundingClientRect();
				const offset = y - box.top - box.height / 2;

				if (offset < 0 && offset > closest.offset) {
					return { offset: offset, element: child };
				} else {
					return closest;
				}
			},
			{ offset: Number.NEGATIVE_INFINITY }
		).element;
	}
	
	



	// 최종 등록 버튼 클릭시
	surveyForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      let surboardNm = document.querySelector('#surboardNm').value;
      let surboardContent = document.querySelector('#surboardContent').value;
      let surboardStdate = document.querySelector('#surboardStdate').value;
      let surboardEnddate = document.querySelector('#surboardEnddate').value;

      let surveyBoard = {
         "surboardNm": surboardNm,
         "surboardContent": surboardContent,
         "surboardStdate": surboardStdate,
         "surboardEnddate": surboardEnddate,
         "empId": myEmpId
      };

      // surveyBoard 안에 SurveyQuestionVO 객체 list를 넣어야함
      // SurveyQuestionVO 객체를 먼저 만들어서 list로 만들기
      let surveyQuestionList = [];

      // 질문을 모두 모아서
      const questions = document.querySelectorAll('.q-area');
      // 하나씩 순회..
      questions.forEach((element, index) => {
         let surquesType = element.querySelector('.select-categoty').value;

         let surquesContent = element.querySelector('.q-content').value


         let surveyQuestion = {
            "surquesType": surquesType,
            "surquesContent": surquesContent,
            "surquesOrder": index
         };

         let surveyItemList = [];
         // 객관형이라면
         if (surquesType == 'S_MULTI') {
            let surquesDupleyn = element.querySelector('.duple:checked').value;
            surveyQuestion['surquesDupleyn'] = surquesDupleyn

         }


         let itemsAll = element.querySelectorAll('.item');
         itemsAll.forEach((elm, inx) => {
            let suritemContent = elm.querySelector('.qitem').value;
            surveyItemList.push({
               "suritemContent": suritemContent,
               "suritemIndex": inx
            })
         })

         surveyQuestion['surveyItemList'] = surveyItemList;

         surveyQuestionList.push(surveyQuestion);


         surveyBoard['surveyQuestionList'] = surveyQuestionList;
      })

      let url = window.location.pathname;
      let resp = await fetch(url, {
         method: "post",
         headers: {
            "Content-Type": "application/json",
         },
         body: JSON.stringify(surveyBoard)
      });

      if (resp.status === 200) {
         Swal.fire({
            title: "완료",
            text: "설문조사 추가가 완료되었습니다!",
            icon: "success",
            confirmButtonText: '확인',
            confirmButtonColor: 'blue',
         }).then(result => {
            if (document.referrer && document.referrer !== window.location.href) {
               window.location.href = document.referrer;
            } else {
               window.history.back();
            }
         });
      } else if (resp.status === 400) {
         Swal.fire({
            title: "검증실패",
            html: "검증 실패로 추가할 수 없습니다.<br>누락된 내용이나 글자수를 초과한 내용이 없는지 확인해주세요.",
            icon: "error",
            confirmButtonText: '확인',
            confirmButtonColor: 'blue',
         });
      } else {
         Swal.fire({
            title: "서버오류",
            html: "오류로 인해 추가할 수 없습니다.<br>다시 시도해주세요.",
            icon: "error",
            confirmButtonText: '확인',
            confirmButtonColor: 'blue',
         });
      }
   })

	
	
	// 취소 버튼 클릭시
	cancelBtn.addEventListener("click", () => {
		Swal.fire({
			icon: "warning",
			title: "정말로 취소하시겠습니까?",
			html: "입력한 내용은 저장되지 않습니다. <br> 취소하시겠습니까?",

			// candel 버튼 (기본값 false, 취소버튼 안 보임)
			showCancelButton: true,
			// confirm 버튼 텍스트
			confirmButtonText: '확인',
			// cancel 버튼 텍스트
			cancelButtonText: '취소',
			// confirm 버튼 색 지정(승인)
			confirmButtonColor: 'blue',
			// cancel 버튼 색 지정(취소)
			cancelButtonColor: 'red',

			// resolve 함수 생각하면 good...
		}).then(result => {
			// result(결과)가 confirm이면(승인이면)
			if (result.isConfirmed) {
				if (document.referrer && document.referrer !== window.location.href) {
					window.location.href = document.referrer;
				} else {
					// referrer가 없으면 기본 뒤로 가기
					window.history.back();
				}
			}
		})
	});
	
	
	
	
	// 업데이트 코드
	const updateBtn = document.querySelector('#update-btn');
	if (updateBtn) {
		updateBtn.addEventListener("click", async () => {
			let surboardNm = document.querySelector('#surboardNm').value;
			let surboardContent = document.querySelector('#surboardContent').value;
			let surboardStdate = document.querySelector('#surboardStdate').value;
			let surboardEnddate = document.querySelector('#surboardEnddate').value;
			let surveyForm = document.querySelector('#survey-form');
			let sboardNo = surveyForm.dataset.sboardNo;
	
			
			let surveyBoard = {
				"sboardNo" : sboardNo,
				"surboardNm": surboardNm,
				"surboardContent": surboardContent,
				"surboardStdate": surboardStdate,
				"surboardEnddate": surboardEnddate,
				"empId": myEmpId
			};
				
			// surveyBoard 안에 SurveyQuestionVO 객체 list를 넣어야함
			// SurveyQuestionVO 객체를 먼저 만들어서 list로 만들기
			let surveyQuestionList = [];
	
			// 질문을 모두 모아서
			const questions = document.querySelectorAll('.q-area');
			// 하나씩 순회..
			questions.forEach((element, index) => {
				let surquesNo = element.dataset.surquesNo;
				let surquesType = element.querySelector('.select-categoty').value;
				let surquesContent = element.querySelector('.q-content').value
				
				let surveyQuestion = {
					"surquesNo": surquesNo,
					"surquesType": surquesType,
					"surquesContent": surquesContent,
					"surquesOrder": index
				};
	
				let surveyItemList = [];
				// 객관형이라면
				if (surquesType == 'S_MULTI') {
					let surquesDupleyn = element.querySelector('.duple:checked').value;
					surveyQuestion['surquesDupleyn'] = surquesDupleyn
				}
	
				let itemsAll = element.querySelectorAll('.item');
				itemsAll.forEach((elm, inx) => {
					let suritemNo = elm.dataset.suritemNo;
					let suritemContent = elm.querySelector('.qitem').value;

					surveyItemList.push({
						"suritemNo": suritemNo,
						"suritemContent": suritemContent,
						"suritemIndex": inx
					})
				})
	
				surveyQuestion['surveyItemList'] = surveyItemList;
	
				surveyQuestionList.push(surveyQuestion);
	
				surveyBoard['surveyQuestionList'] = surveyQuestionList;
			})

			console.log("surveyBoard",surveyBoard);
			
			
			let url = window.location.pathname;
			let resp = await fetch(url, {
				method: "put",
				headers: {
					"Content-Type": "application/json"
				},
				body: JSON.stringify(surveyBoard)
			});
			
			
			if (resp.status === 200) {
				Swal.fire({
					title: "완료",
					text: "수정이 완료되었습니다!",
					icon: "success",
					confirmButtonText: '확인',
					confirmButtonColor: 'blue',
				}).then(() => {
					location.href=`${contextPath}/${companyId}/survey`;
				});
			} else if (resp.status === 400) {
				Swal.fire({
					title: "검증실패",
					html: "검증 실패로 수정할 수 없습니다.<br>누락된 내용이나 글자수를 초과한 내용이 없는지 확인해주세요.",
					icon: "error",
					confirmButtonText: '확인',
					confirmButtonColor: 'blue',
				});
			} else {
				Swal.fire({
					title: "서버오류",
					html: "오류로 인해 수정할 수 없습니다.<br>다시 시도해주세요.",
					icon: "error",
					confirmButtonText: '확인',
					confirmButtonColor: 'blue',
				});
			}
			
			
		})
	}
})