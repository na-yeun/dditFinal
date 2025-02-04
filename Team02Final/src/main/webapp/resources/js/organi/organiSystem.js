
document.addEventListener("DOMContentLoaded", () => {
	
	// 트리 데이터를 로드하고 렌더링하는 함수. 
	async function loadAndRenderTree() {
		try {
			// 서버에서 비동기로 조직도 데이터 가져오기
			let resp = await fetch(`${contextPath}/${companyId}/hr/organi/list`, {
				headers: {
					"Content-Type": "application/json"
				}
			});
			if (!resp.ok) throw new Error("조직도 데이터를 불러오지 못했습니다."); // not 200 

			const departmentList = await resp.json(); // JSON 데이터
			console.log("부서리스트: ", departmentList);

			// 트리 구조 변환
			const treeData = createTree(departmentList);

			// 기존 트리 초기화 (HTML 요소 내부 비우기)
			const organiTreeContainer = document.getElementById("organiTree");
			organiTreeContainer.innerHTML = "";

			// 트리 렌더링 옵션 
			const options = {
				width: 900,            // 트리의 너비
				height: 530,            // 트리의 높이
				nodeWidth: 150,         // 각 노드의 너비
				nodeHeight: 90,        // 각 노드의 높이
				childrenSpacing: 50,    // 부모와 자식 간의 간격
				siblingSpacing: 40,     // 형제 노드 간의 간격
				direction: 'top'        // 트리 방향: 위에서 아래로
			};

			// ApexTree 인스턴스를 생성하고 트리 렌더링
			const tree = new ApexTree(organiTreeContainer, options);
			tree.render(treeData[0]); // 최상위 노드부터 렌더링
		} catch (error) {
			console.error("트리 렌더링 오류: ", error);
		}
	}

	/**
	 * 부서 데이터를 트리 구조로 변환하는 함수
	 */
	function createTree(data) {
		const tree = []; // 최상의 노드를 저장할 배열 
		const map = {};  // 각 부서를 key-value 형태로 저장할 객체 

		// 모든 데이터를 map에 저장하고 자식 배열 초기화
		data.forEach(dept => {
			map[dept.departCode] = {
				id: dept.departCode,        // 노드 ID
				name: dept.departName,      // 노드 이름 (ApexTree가 이 값을 사용)
				children: []  // 자식 노드를 담을 배열. 
			};
		});

		// 부모 노드에 자식 노드 연결
		data.forEach(dept => {
			if (dept.departParentcode) { // 부모 부서코드가 존재하면 부모 노드에 자식 추가
				map[dept.departParentcode]?.children.push(map[dept.departCode]); 
			} else {
				tree.push(map[dept.departCode]); // 부모가 없으면 최상위 노드로 간주. 
			}
		});

		return tree;
	}

	// 트리 초기 로드 및 렌더링
	loadAndRenderTree();
	window.updateTree = loadAndRenderTree; // 전역함수화 : 다른 함수에서도 다시 트리 렌더링하기위해 
	//=============================================================================================	
	// 조직도에서 부서 노드 클릭했을 때 이벤트 처리 
	document.getElementById("organiTree").addEventListener("click", async (e) => {

		e.preventDefault(); 

		// 클릭된 요소를 확인하고, 가장 가까운 'g' 태그 찾기
		// apextree가 만들면서 g 태그로 생성됨. 
		const target = e.target.closest("g");
		if (target) {
			const departCode = target.getAttribute("data-self"); // 노드의 departCode 가져오기 
																// 코드검사 해보면 data-self 가 departCode 값임.
			console.log("선택한 부서 : ", departCode)

			try {
				// 서버에서 선택된 부서의 정보 비동기로 가져오기. 
				let resp = await fetch(`${contextPath}/${companyId}/hr/organi/${departCode}`, {
					headers: {
						"Content-type": "application/json"
					}
				}); // fetch end 
				if (resp.ok) {
					const data = await resp.json(); // JSON 데이터 변환. 
					console.log(data);

					// 
					// SweetAlert2 모달창 띄우기
					Swal.fire({
						title: "부서 관리",
						html: `
                            <div>
                                <p>부서 이름: <strong>${data.departName}</strong></p>
                                <label><input type="radio" name="action" value="add"> 추가</label><br>
                                <label><input type="radio" name="action" value="update"> 수정</label><br>
                                <label><input type="radio" name="action" value="delete"> 삭제</label>
                            </div>
                        `,
						confirmButtonText: "확인",
						showCancelButton: true,
						cancelButtonText:"취소",
						preConfirm: () => {  // 확인 버튼 클릭시 실행됨. 
							// 라디오 버튼에서 선택된 값 가져오기
							const selectedAction = document.querySelector('input[name="action"]:checked');
							if (!selectedAction) { // 선택을 안할시에 
								Swal.showValidationMessage("옵션을 선택해주세요!"); 
								return false;
							}
							return selectedAction.value; // 선택된 값 반환. 
						}
					}).then((result) => {  // 선택된 결과 처리 
						if (result.isConfirmed) { 
							const action = result.value; // 선택한 액션 값.
							console.log("선택한 작업: ", action);

							// 선택한 작업에 따라 다른 함수 실행
							if (action === "add") {  
								addDepartment(departCode);
							} else if (action === "update") {
								updateDepartment(departCode);
							} else if (action === "delete") {
								deleteDepartment(departCode);
							}
						}
					});
				}
			} catch (error) {
				console.log(error);
			}

		}

	});
	// 부서 선택하면 라디오 버튼 끝 
	// ===========================================================================================================

	// update 처리 
	function updateDepartment(departCode) {

		Swal.fire({
			title: "부서 수정", 							// 모달창 제목 
			input: "text",	   					   		   // 입력 타입 : 텍스트 
			inputLabel: "수정할 부서 이름",					// 입력창 라벨 
			inputPlaceholder: "부서 이름을 입력하세요",		 // 입력창 플레이스홀더
			showCancelButton: true,						   // 취소 버튼 표시 
			confirmButtonText: "수정",                     // 확인 버튼 텍스트. 
			cancelButtonText : "취소",
			preConfirm: (departName) => {
				// 확인 버튼을 눌렀을 때 실행되는 함수. 
				// 입력된 부서 이름이 없으면 경고메시지 표시하고 함수 종료. 
				if (!departName) {  
					Swal.showValidationMessage("부서 이름을 입력해주세요!");
					return false;
				}
				// 서버로 수정 요청 보내기 (비동기)
				return fetch(`${contextPath}/${companyId}/hr/organi/edit`, {
					method: 'PUT'
					, headers: {
						"Content-Type": "application/json"
					}
					, body: JSON.stringify({ departCode, departName })  // 수정할 데이터 전송 
				})
					.then((resp) => {
						if (!resp.ok) throw new Error("수정 실패"); // 실패시 

						return resp.json(); // 성공시 JSON데이터 변환. 
					})
					.then((data) => {
						console.log("데이터 왔다잉 :", data);

						if(data === 0){
							Swal.fire({
								title : "수정 실패"
							  , text : "이미 부서명이 존재합니다."
							  , icon : "error"
							});
							return false;
						}
						
						// 수정 성공시 실행. 
						Swal.fire({
							position: "center",  					    // 모달창 위치 
							icon: "success",     						// 성공 아이콘 
							title: "수정 완료!",  						 // 성공 메시지 제목 
							text: "정상적으로 수정이 완료되었습니다.", 	   // 메시지 내용
							showConfirmButton: false, 					// 확인 버튼 숨김
							timer: 3000									// 3초후 자동 닫힘 
						});
						loadAndRenderTree();  // 리스트 다시 렌더링
					}).catch((error) => {

						Swal.fire("수정 실패", error.message, "error");
					})



			}
		})

	}
	// update 처리 끝
	//=================================================================================================

	// 부서 add 시작 


	function addDepartment(departCode) {
		// SweetAlert2 를 사용해 부서이름 입력창 띄우기 
		Swal.fire({
			title: "부서 등록",
			input: "text",
			inputLabel: "등록할 부서 이름",
			inputPlaceholder: "부서 이름을 입력하세요",
			showCancelButton: true,
			cancelButtonText : "취소",
			confirmButtonText: "등록",
			preConfirm: async (departName) => {  // 확인버튼 클릭시 실행되는 함수. 
				console.log("preConfirm 호출됨");
				
				if (!departName) {  // 입력된 부서 이름이 없으면 경고메시지 출력 후 함수 종료 
					Swal.showValidationMessage("부서 이름을 입력해주세요!");
					return false;
				}

				const requestBody = {
					departParentcode: departCode, // 클릭한 노드의 departCode를 부모로 설정하기
					departName: departName 		  // 입력한 부서 이름
				};
				
				// 서버로 비동기로 부서 추가 요청 
				return fetch(`${contextPath}/${companyId}/hr/organi/add`, {
					method: 'POST'
					, headers: {
						"Content-Type": "application/json"
					}
					, body: JSON.stringify(requestBody) // 추가할 데이터 전송 
				})
					.then((resp) => {
						if (!resp.ok) throw new Error("등록 실패");  // 실패

						return resp.json();  // 성공 
					})
					.then((data) => {
						console.log("데이터 왔다잉 :", data);
						if(data === 0){
							// 부서명이 중복될 경우 
							Swal.fire({
								icon : "error"
							  , title : "등록실패"
							  , text : "이미 부서명이 존재합니다."
							});
							return false;
						}
						Swal.fire({
							position: "center",
							icon: "success",
							title: "등록 완료!",
							text: "정상적으로 등록이 완료되었습니다.",
							showConfirmButton: false,
							timer: 3000
						});
						loadAndRenderTree();  // 리스트 다시 렌더링
					}).catch((error) => {
						console.log("fetch 요청 실패", error);
						Swal.fire("등록 실패", error.message, "error");
					});
			}
		});
	}
	// 부서 add 끝 

	// 부서 remove 시작 
	function deleteDepartment(departCode) {
		// SweetAlert2를 사용해 삭제 확인 창 표시 
		Swal.fire({
			title: "정말로 삭제하시겠습니까?",
			text: "삭제 후에는 복구할 수 없습니다.",
			icon: "warning",
			showCancelButton: true,
			confirmButtonColor: "#3085d6",  
			cancelButtonColor: "#d33",
			confirmButtonText: "삭제",
			cancelButtonText: "취소"
		}).then((result) => {
			// 사용자가 "삭제" 버튼을 클릭했을 때만 실행
			if (result.isConfirmed) {
				fetch(`${contextPath}/${companyId}/hr/organi/remove`, {
					method: 'DELETE',
					headers: {
						"Content-Type": "application/json"
					},
					body: JSON.stringify(departCode) // 삭제할 부서코드 전송. 
				})
					.then((resp) => {
						if (!resp.ok) throw new Error("삭제 실패");

						return resp.text(); // 서버에서 반환된 결과를 텍스트로 변환하기. 
					})
					.then((result) => {
						if (parseInt(result) === 1) {  // 서버에서 1 반환시 삭제 성공. 
							console.log("delete data:", result);
							Swal.fire({
								title: "삭제되었습니다.",
								text: "부서가 성공적으로 삭제되었습니다.",
								icon: "success"
							});
							loadAndRenderTree(); // 삭제 후 리스트 렌더링
						} else {
							Swal.fire({  // 아닐 경우에 실패 메시지.
								title: "부서를 삭제할 수 없습니다.",
								text: "하위 부서나 소속 직원이 존재합니다.",
								icon: "error"
							});
						}
					})
					.catch((error) => {
						console.log("fetch 요청 실패", error);
						Swal.fire({
							title: "오류 발생",
							text: "삭제 중 오류가 발생했습니다.",
							icon: "error"
						});
					});
			}
		});
	}	// function remove end




}); // DOM end 