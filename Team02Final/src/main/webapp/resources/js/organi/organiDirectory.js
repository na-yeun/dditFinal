// 디렉토리 구조 생성.


function getJson() {
	$.ajax({
		type: 'get'
		, url: `${contextPath}/${companyId}/directory/folder`
		, dataType: 'json'
		, success: function(data) {
			var directory = []  // JSTree 에 사용할 데이터를 담는 배열
			$.each(data, function(idx, item) {
				// 부서 데이터를 JSTree 구조에 맞게 변환하기!
				directory.push({
					id: item.departCode,      // JSTree에서 노드의 고유 식별자 
					parent: item.departParentcode || "#", // 부모 노드의 ID를 지정. 없으면 최상위 노드(#)으로 설정. 
					text: item.departName     // JSTree에서 보여질 이름 (현재는 부서명)
				});

				// 부서에 속한 사원이 있으면, 사원 데이터를 JSTree에 추가
				if (item.employeeList && item.employeeList.length > 0) {
					$.each(item.employeeList, function(empIdx, emp) {

						directory.push({
							id: emp.empId
							, parent: item.departCode      // 사원은 해당 부서의 자식 노드로 설정 

							, text: emp.posiName + " " + emp.empName  // 직급명과 사원명을 합쳐서 출력하기 

							, icon: false   // 폴더 파일등 아이콘들을 지정할 수 있지만, 사원정보이기때문에 false 로 icon 지정 안하기!

						}); // $.each end 
					}); // if end
				}

			}); // $.each end

			// JSTree 를 초기화 하고 데이터 설정하기 .
			/*
			 core , types , plugins 는 JSTree 양식에서는 거의 동일하지만 안에 들어갈 수 있는 종류는 너무 많음... 
			*/
			$("#treeDirectory").jstree({
				core: {
					data: directory  // JSTree에 사용할 데이터 
				},
				types: {
					'default': {
						'icon': 'jstree-folder' // 기본 아이콘 설정
					}
				},
				plugins: ['wholerow', 'types']  // 플러그인 추가. 
			});

			/*
				data : 가공한 데이터를 JSTree에 바인딩 
				types : 노드의 아이콘을 설정 
				plugins : JSTree 에 추가 기능 제공
					   'wholerow : 노드를 클릭할 때 전체 영역이 선택됨.
					   'types' : 노드의 타입과 아이콘을 설정 
			*/

			// 검색 버튼 이벤트 추가 부분 
			$('.search-wrapper button').on('click', function() {
				var searchInput = $('.search-wrapper input').val().trim();  // 검색창에 입력된 테스트 가져오기.
				if (searchInput === "") return;       // 검색창이 비어있으면 return; 


				var tree = $('#treeDirectory').jstree(true);    // JSTree 인스턴스 가져오기
				var foundNodes = []; // 검색 결과를 저장할 배열 

				// 노드 검색 및 처리 
				var nodes = tree.get_json('#', { flat: true }); // JSTree의 모든 노드를 평면 구조로 가져옴.


				// 노드 탐색 및 저장
				// JSTree의 모든 노드 중에서 검색어가 포함된 노드를 찾음. 
				if (Array.isArray(nodes)) {
					for (var i = 0; i < nodes.length; i++) {
						if (nodes[i].text && nodes[i].text.includes(searchInput)) { // 노드의 이름에 검색어가 포함되어 있는지 체크.
							foundNodes.push(nodes[i]); // 일치하는 모든 노드 저장
						}
					}
				} else {
					console.error("JSTree에서 노드를 가져오는데 실패했습니다.");
					return;
				}

				// 검색 결과 처리 
				// 다중결과 일 때만 sweetalert2 라디오 버튼으로 선택 처리 
				if (foundNodes.length > 1) {
					let options = "";
					foundNodes.forEach((node, index) => {
						options += `
                                <div>
                                    <input type="radio" id="node_${index}" name="selectedNode" value="${index}">
                                    <label for="node_${index}">${node.text}</label>
                                </div>
                            `;
					});

					Swal.fire({
						title: "다중 결과! 선택해주세요.",
						html: `
                                <form id="nodeForm">
                                    ${options}
                                </form>
                            `,
						confirmButtonText: "확인",
						showCancelButton: true,
						focusConfirm: false,
						preConfirm: () => {   // 사용자가 확인 버튼을 클릭했을 때 실행되는 함수임. 
							const selectedValue = document.querySelector('input[name="selectedNode"]:checked');  // 선택된 라디오 버튼 가져오기. 
							if (selectedValue) { // 사용자가 노드를 선택했는지 검증 
								return foundNodes[selectedValue.value]; // 선택된 노드의 데이터 반환 
							} else {
								Swal.showValidationMessage("하나를 선택해주세요.");  // 선택하지 않았을 경우엔 문구 출력. 
							}
						}
					}).then((result) => {
						if (result.isConfirmed && result.value) {
							var selectedNode = result.value;  // 사용자가 선택한 노드의 value 

							// 선택된 노드의 부모 열고 강조
							tree.open_node(selectedNode.parent, function() {
								tree.deselect_all();
								tree.select_node(selectedNode.id);
							});
							console.log("선택된 노드: ", selectedNode.text);
						}
					});
				} else if (foundNodes.length === 1) {
					// 검색된 노드가 한 명일 때 해당 노드를 선택하고 부모 노드를 연다. 
					var singleNode = foundNodes[0];
					tree.open_node(singleNode.parent, function() {
						tree.deselect_all();
						tree.select_node(singleNode.id);
						/* singleNode는 검색된 노드이고 parent는 해당 노드의 부모
						   ex) "김영희" 사원이 인사1팀에 속해 있다면, 인사1팀 노드가 열리게 된다. 

						   deselect_all 는 트리에서 선택된 노드를 모두 해제 ( 강조 표시 해제)
						   select_node 는 지정된 ID의 노드를 선택하고 강조 표시. 
						*/

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



		}, // function(data) end

		error: function(data) {
			alert("에러");
		}
	}); // ajax end 

}; // function end 

$(document).ready(function() {  // 모든 게 준비 되면 getJson 함수 실행 
	getJson();
});





