let allDepartments = []; // 부서 전체 목록 저장 배열
let allPositions = [];   // 직급 전체 목록 저장 배열
let allStatuses = [];    // 재직 상태 목록 저장 배열
let allVstaCode = [];    // 기준 년도 목록 저장 배열 
let addVacaBtn;
// 서버에서 기준연도 목록 가져오는 function 
function fetchGetVstaCode(){
    return fetch(`${contextPath}/${companyId}/hr/employee/readVstaCode`)
           .then(response => response.json())
           .then(data => {
            if (!Array.isArray(data)) {
                console.error("Expected an array but got:", data);
                return; // 함수 종료
            }
                allVstaCode = data.map(vsta => ({
                    id : vsta.vstaCode 
                    ,name : vsta.vstaCode
                }));
                // 기준년도 selectYear에 초기 렌더링 
                modrenderOptions('selectYear',allVstaCode,allVstaCode[0]?.id || '');
           })
           .catch(error => console.log("기준연도 데이터 없음 : ",error))
}

// 조직도를 통해 추가한 대상 삭제하는 함수
function deleteVacationElement(empId){
	let dataId = `vac_${empId}`;
	document.getElementById(dataId).remove();
}

let empVacaModalBody = document.querySelector("#empVacaModalBody");
function fetchVacationDetail(empId , vstaCode){
    empVacaModalBody.innerHTML = ""; // 항상 초기화

    const year = new Date().getFullYear().toString();
    
    if(vstaCode === year){
        console.log("true");
        // addVacaBtn.style.visibility ='visible';  // 느림
        addVacaBtn.style.display = 'block';
    }else{
        console.log("false");
        // addVacaBtn.style.visibility ='hidden';
        addVacaBtn.style.display = 'none';
    }

    return fetch(`${contextPath}/${companyId}/hr/employee/vsDetail/${vstaCode}/${empId}`, {
        headers : {
            "Content-Type" : "application/json"
        }
    }) .then((resp) => {
        if (resp.status === 204 || resp.status === 404) {
            // 서버가 빈 응답을 보낸 경우
            return null;
        }
        if (!resp.ok) {
            throw new Error("휴가 정보 조회 실패");
        }
        return resp.json().catch(() => null); // JSON 변환 실패 시 null 반환
    })
    .then(data => {
        let row = "";
        if(!data){
            row = `
            <div class="table-responsive"> 
                <table class="table">
                    <tr>
                        <th>연도</th>
                        <td><select id="selectYear"></select></td>
                    </tr>
                    
                </table>
                <div class="no-data-message" style="text-align: center; margin: 20px 0; color: #888;">
                        해당 연도에 휴가정보가 없습니다.
                </div>

            </div>
        `;
        
        }else{

         row = `
            <div class="table-responsive"> 
                <table class="table">
                    <tr>
                        <th>연도</th>
                        <td><select id="selectYear"></select></td>
                    </tr>
                    <tr>
                        <th>사원명</th>
                        <td>${data.employee.empName}</td>
                    </tr>
                    <tr>
                        <th>부서</th>
                        <td>${data.departName}</td>
                    </tr>
                    <tr>
                        <th>직급</th>
                        <td>${data.posiName}</td>
                    </tr>
                    <tr>
                        <th>부여개수</th>
                        <td>${data.vstaAllcount}</td>
                    </tr>
                    <tr>
                        <th>사용개수</th>
                        <td>${data.vstaUse}</td>
                    </tr>
                    <tr>
                        <th>현재남은개수</th>
                        <td>${data.vstaNowcount}</td>
                    </tr>
                    <tr>
                        <th>병가사용개수</th>
                        <td>${data.vstaSickcount}</td>
                    </tr>
                    <tr>
                        <th>추가부여개수</th>
                        <td>${data.vstaAppend}</td>
                    </tr>
                </table>
            </div>
        `;
        }
        empVacaModalBody.insertAdjacentHTML('beforeend',row);
        modrenderOptions("selectYear",allVstaCode,vstaCode);
        // let vsta = document.getElementById('#selectYear').value();
        // console.log(vsta);

        // 연도 변경 이벤트 리스너 등록
        const yearSelect = document.querySelector("#selectYear");
        yearSelect?.addEventListener("change", (e) => {
            let selectedYear = e.target.value;
            
            fetchVacationDetail(empId, selectedYear);
        });

    })
    .catch(error => {
        // 요청 실패 또는 데이터가 없을 경우 처리
        console.error("휴가 상세 가져오기 실패 :", error);
    });
}


// 서버에서 부서 목록 가져오는 function 
async function fetchDepartments(elementId) {
    return fetch(`${contextPath}/${companyId}/hr/employee/departments`)
        .then(response => response.json())
        .then(data => {
            allDepartments = data.map(dept => ({ code: dept.departCode, name: dept.departName })); // 모든 부서의 코드와 이름을 가져옴
            renderOptions(elementId, allDepartments, ''); // 가져온 부서리스트를 select 태그에 렌더링 
        })
        .catch(error => console.error("부서 목록 가져오기 실패: ", error));
}


// 서버에서 직급 목록 가져오는 function 
async function fetchPositions(elementId) {
    return fetch(`${contextPath}/${companyId}/hr/employee/positions`)
        .then(response => response.json())
        .then(data => {
            allPositions = data.map(posi => ({  // 모든 부서의 코드와 이름을 가져옴 
                id: posi.code, // 직급코드 
                name: posi.codeComment // 직급 이름 
            }));
            renderOptions(elementId, allPositions, ''); // 가져온 직급리스트를 select 태그에 렌더링 
        })
        .catch(error => console.error("직급 목록 가져오기 실패: ", error));
}


// 서버에서 재직 상태 목록 가져오는 function  
function fetchStatuses() { 
    return fetch(`${contextPath}/${companyId}/hr/employee/commonCodes`)
        .then(response => response.json())
        .then(data => {
            allStatuses = data.map(code => ({  // 모든 재직상태 코드 , 이름 가져옴 
                id: code.code,  // 재직상태 코드 
                name: code.codeComment // 상태 이름 
            }));
            renderOptions('selectStatus', allStatuses, ''); // 가져온 재직상태 리스트를 select 태그에 렌더링 
        })
        .catch(error => console.error("재직 상태 목록 가져오기 실패: ", error));
}

// ★★★★★옵션 렌더링 함수★★★★★ (필터링을 위한 select 태그에 옵션 추가)
function renderOptions(elementId, optionList, selectedValue) {
    const selectElement = document.querySelector(`#${elementId}`);  // 대상 select 요소 가져오기 ( 부서 , 직급 , 상태 중에서)
    selectElement.innerHTML = '<option value="">전체</option>';  // 기본옵션 "전체" 

    // 옵션 리스트를 순회하면서 select 태그에 추가 
    optionList.forEach(item => {
        selectElement.insertAdjacentHTML("beforeend", `
            <option value="${item.code || item.id}" ${selectedValue === (item.code || item.id) ? 'selected' : ''}>
                ${item.name}
            </option>
        `);
    });
}

// 옵션 렌더링 함수 (option 에 전체 없는 렌더링 함수.)
function modrenderOptions(elementId, optionList, selectedValue) {
    const selectElement = document.querySelector(`#${elementId}`);  // 대상 select 요소 가져오기 ( 부서 , 직급 , 상태 중에서)
    selectElement.innerHTML = ''; // 기존 옵션을 모두 제거 (전체 옵션 포함)

    // 옵션 리스트를 순회하면서 select 태그에 추가
    optionList.forEach(item => {
        selectElement.insertAdjacentHTML("beforeend", `
            <option value="${item.code || item.id}" ${selectedValue === (item.code || item.id) ? 'selected' : ''}>
                ${item.name}
            </option>
        `);
    });
}



// 필터 컨테이너 값과 form의 hidden 필드를 동기화하는 함수
function syncFormWithFilters() {
    // 필터 컨테이너의 값 가져오기
    const startDate = document.querySelector('#startJoinDate').value;
    const endDate = document.querySelector('#endJoinDate').value;
    const department = document.querySelector('#selectDepart').value;
    const position = document.querySelector('#selectPosi').value;
    const status = document.querySelector('#selectStatus').value;
    const searchWord = document.querySelector('#searchInput').value;

    // form hidden 필드 업데이트
    document.querySelector('input[name="startDate"]').value = startDate;
    document.querySelector('input[name="endDate"]').value = endDate;
    document.querySelector('input[name="department"]').value = department;
    document.querySelector('input[name="position"]').value = position;
    document.querySelector('input[name="status"]').value = status;
    document.querySelector('input[name="searchWord"]').value = searchWord;
}

// 검색 버튼 클릭 시 form 동기화 후 제출
document.querySelector('.search-container button').addEventListener('click', (e) => {
    e.preventDefault(); // 기본 제출 방지
    // 선택된 값 가져오기
    const startDate = document.querySelector('#startJoinDate').value;
    const endDate = document.querySelector('#endJoinDate').value;
    const department = document.querySelector('#selectDepart').value;
    const position = document.querySelector('#selectPosi').value;
    const status = document.querySelector('#selectStatus').value;
    const searchWord = document.querySelector('#searchInput').value;

    // 로컬 스토리지에 저장 (비동기 방식일때 이렇게 사용용)
    localStorage.setItem('filterOptions', JSON.stringify({
        startDate,
        endDate,
        department,
        position,
        status,
        searchWord
    }));
    syncFormWithFilters(); // hidden 필드 동기화
    document.querySelector('#searchForm').submit(); // 폼 제출
});

function paging(page) {
    const searchForm = document.querySelector("#searchForm");
	console.log(page);
	searchForm.page.value = page;
	searchForm.requestSubmit();
}


// 이벤트 바인딩 : 페이지 로드 시 실행되는 함수 
document.addEventListener("DOMContentLoaded", () => {
    fetchDepartments('selectDepart');  // 부서 목록을 불러옴 
    fetchPositions('selectPosi');    // 직급 목록을 불러옴 
    fetchStatuses();     // 재직상태 목록을 불러옴
    // employeeList(1);     // 첫번째 페이지의 직원 목록을 불러옴 
    fetchGetVstaCode();


    const startDateInput = document.getElementById("startJoinDate");
    const endDateInput = document.getElementById("endJoinDate");
    const department = document.querySelector('#selectDepart');
    const position = document.querySelector('#selectPosi');
    const status = document.querySelector('#selectStatus');
    const searchWord = document.querySelector("#searchInput");
    const searchForm = document.querySelector("#searchForm");
	   
    // 초기화 버튼. (필드 초기화)
    const reloadBtn = document.querySelector("#reload-btn");
    reloadBtn.addEventListener("click", () => {
        startDateInput.value = "";
        endDateInput.value = "";
        department.value="";
        position.value="";
        status.value="";
        searchWord.value="";
        
        // localStorage의 필터 값 초기화
        // (필드에 선택한 옵션값이 남아있어서 직접 localStorage에 set한걸 remove로 삭제해줌)
        localStorage.removeItem('filterOptions');

        // 폼의 hidden 필드 초기화
        $(":input[type=hidden]").each(function() {
            $(this).val("");
        });
        // document.querySelectorAll("input[type='hidden']").forEach(function(){
        //     this.value="";
        // })
        searchForm.submit();
    });

      // LocalStorage에서 저장된 값 가져오기
      const savedFilters = JSON.parse(localStorage.getItem('filterOptions')) || {};

      // 필터 값 복원
      document.querySelector('#startJoinDate').value = savedFilters.startDate || '';
      document.querySelector('#endJoinDate').value = savedFilters.endDate || '';
      document.querySelector('#selectDepart').value = savedFilters.department || '';
      document.querySelector('#selectPosi').value = savedFilters.position || '';
      document.querySelector('#selectStatus').value = savedFilters.status || '';
      document.querySelector('#searchInput').value = savedFilters.searchWord || '';
  
      // 비동기로 옵션 로드 후, 저장된 값으로 선택 설정
      fetchDepartments('selectDepart').then(() => {
          document.querySelector('#selectDepart').value = savedFilters.department || '';
      });
  
      fetchPositions('selectPosi').then(() => {
          document.querySelector('#selectPosi').value = savedFilters.position || '';
      });
  
      fetchStatuses().then(() => {
          document.querySelector('#selectStatus').value = savedFilters.status || '';
      });

	// 나연
	// 휴가 등록
	let addVacationSaveBtn = document.querySelector('#vacation-save-btn');
	let addVacationBtn = document.querySelector('#addVacation');
	
	const vacationModal = new bootstrap.Modal(document.querySelector('#vacation-save'), {
			backdrop: "static", // 백드롭 고정
			keyboard: false     // ESC 키로 닫기 방지
		});
	
	// 휴가등록 버튼 클릭시 일괄등록 모달 오픈
	addVacationBtn.addEventListener("click", () => {
		vacationModal.show();
		
	});
	
	// 조직도 가지고 오는 함수
	function getJson(searchKeyword = '') {
		$.ajax({
			type: 'get',
			url: `${contextPath}/${companyId}/directory/folder`,
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
							let departmentName = item.departName?item.departName:"";
							
							let empMail = emp.empMail?emp.empMail:"";
							
							directory.push({
								id: emp.empId,
								parent: item.departCode,
								text: `${emp.posiName} ${emp.empName}`, // 직급 + 이름 표시
								department: departmentName,
								empMail : empMail,
								icon: false,
							});
						});
					}
				});

				// JSTree 업데이트
				const mailtree = $('vacation-tree-directory').jstree(true);
				if (mailtree) {
			        mailtree.settings.core.data = directory.map(node => ({
			            id: node.id,
			            parent: node.parent,
			            text: node.text,
			            icon: node.icon,
			            data: { 
							department: node.department,
							empMail : node.empMail

						}
			        }));
			        mailtree.refresh(); // 트리 새로고침
			    } else {
			        // 트리가 없으면 초기화
			        $('#vacation-tree-directory').jstree({
			            core: {
			                data: directory.map(node => ({
			                    id: node.id,
			                    parent: node.parent,
			                    text: node.text,
			                    icon: node.icon,
			                    data: { 
									department: node.department,
									empMail : node.empMail
								} // 부서명 추가
			                }))
			            },
			            plugins: ['wholerow', 'checkbox'] // 플러그인 설정
			        });
			    }
			},
			error: function() {
				alert('조직도를 불러오는 중 오류가 발생했습니다.');
			},
		});
	}
	
	// 조직도 검색 버튼 클릭시 조직도 오픈
	let vacationToBtn = document.querySelector('#vacation-to-btn');
	let vacationToSearchModal;
	vacationToBtn.addEventListener("click", () => {
		
		vacationToSearchModal = new bootstrap.Modal(document.querySelector('#search-modal'), {
			backdrop: "static", // 백드롭 고정
			keyboard: false     // ESC 키로 닫기 방지
		});	
		getJson();
		vacationToSearchModal.show();
	})
	
	// 조직도 모달창 내의 검색
	$('#vacation-search-btn').on('click', function(e) {
		const searchName = $('#vacation-search-input-area').val().trim(); // 검색창에 입력된 텍스트 가져오기
		if (searchName === "") return; // 검색창이 비어있으면 return

		const tree = $('#vacation-tree-directory').jstree(true); // JSTree 인스턴스 가져오기
		const foundNodes = []; // 검색 결과를 저장할 배열

		// JSTree의 모든 노드를 평면 구조로 가져옴
		const nodes = $('#vacation-tree-directory').jstree(true).get_json('#', { flat: true });

		if (Array.isArray(nodes)) {
			// 검색어가 포함된 노드를 찾음
			for (let i = 0; i < nodes.length; i++) {
				
				if (nodes[i].text && nodes[i].text.includes(searchName)) {
					foundNodes.push(nodes[i]); // 일치하는 노드 저장
				}
			}
		} else {
			console.error("JSTree에서 노드를 가져오는데 실패했습니다.");
			return;
		}
		
		// 기존 선택된 노드 저장
		let previouslySelectedNodes = tree.get_selected();
		if(previouslySelectedNodes){
			// 검색 결과 처리
			if (foundNodes.length > 1) {
				// 다중 결과 시 SweetAlert2로 라디오 버튼 선택 제공
				let options = "";
				foundNodes.forEach((node, index) => {
					const departmentName = node.data?.department || `<b>부서없음</b>`;
					options += `
	                <div>
	                    <input type="checkbox" id="node_${index}" name="selectedNode" value="${index}">
	                    <label for="node_${index}"><b>${departmentName}</b> ${node.text}</label>
	                </div>`;
				});
	
				Swal.fire({
					title: "선택해주세요.",
					html: `<form id="nodeForm">${options}</form>`,
					confirmButtonText: "확인",
					cancelButtonText: "취소",
					showCancelButton: true,
					focusConfirm: false,
					preConfirm: () => {
						const selectedValues = Array.from(document.querySelectorAll('input[name="selectedNode"]:checked'))
                									.map(input => foundNodes[input.value]);
						
						if (selectedValues.length > 0) {
					        return selectedValues; // 선택된 노드 배열 반환
					    } else {
					        Swal.showValidationMessage("최소 한 명을 선택해주세요.");
					    }
						
					}
				}).then((result) => {
					if (result.isConfirmed && result.value) {
						const selectedNodes = result.value; // 선택된 모든 노드
				        const updatedSelection = [...previouslySelectedNodes, ...selectedNodes.map(node => node.id)];
				
				        // 선택된 모든 노드 열기 및 선택
				        selectedNodes.forEach(selectedNode => {
				            tree.open_node(selectedNode.parent, function() {
				                tree.deselect_all();
				                tree.select_node(updatedSelection);
				            });
				        });
				
				        $('#search-input-area').val('');
						
					}
				});
			} else if (foundNodes.length === 1) {
				// 검색 결과가 한 개일 때
				const singleNode = foundNodes[0];
				tree.open_node(singleNode.parent, function() {
					const updatedSelection = [...previouslySelectedNodes, singleNode.id]; // 병합
			        tree.deselect_all();
			        tree.select_node(updatedSelection);
				});
				$('#search-input-area').val('');
			} else {
				// 검색 결과가 없을 때
				Swal.fire({
					position: "center",
					icon: "error",
					title: "검색 결과가 없습니다.",
					showConfirmButton: false,
					timer: 3000
				});
				$('#vacation-search-input-area').val('');
			}
		}
	});

	// 추가 버튼 클릭시 대상에 체크된 사원 추가
	$('#confirm-vacation-plus-btn').on('click', function() {
		const selectedNodes = $('#vacation-tree-directory').jstree('get_selected', true);
		
		const selectedReceivers = selectedNodes
			.filter(node => !node.children.length) // 최하위 노드만 선택
			.map(node => ({
				id: node.id
				, text: node.text
				, empMail : node.data?.empMail || ""
			})); // ID와 텍스트 가져오기
		const vacationToArea = $('#vacation-to-area');
		
		selectedReceivers.forEach(receiver => {
			// 중복 확인
			if ($(`#vacation-to-area .receiver-tag[id="vac_${receiver.id}"]`).length === 0) {
				// 태그 생성
				const tagHtml = `
                    <div class="receiver-to-tag" id="vac_${receiver.id}" data-emp-id="${receiver.id}">
                        <span class="my-label">${receiver.text}</span>
                        <a href="javascript:void(0);" onclick="deleteVacationElement('${receiver.id}')">
							<img class="icon" alt="삭제" src="${contextPath}/resources/images/delete-icon.png"/>
						</a>
                    </div>`;
				vacationToArea.append(tagHtml);
			}
		});
		
		discheckJstree();
		
		vacationToSearchModal.hide();
		
		
	});
	// 모달 닫기 누르면 체크되어있었던 모든 노드 선택 해제해야함
	$('#search-modal').on('hidden.bs.modal', function () {
	    discheckJstree();
	});
	
	// 체크 다 해제하는 함수
	function discheckJstree(){
		const tree = $('#vacation-tree-directory').jstree(true);
	    if (tree) {
	        tree.deselect_all(); // 모든 선택된 노드 해제
	    }
	}
	// 검색 관련 코드 END
	
	addVacationSaveBtn.addEventListener("click", async () => {
		let vacationUrl = `${contextPath}/${companyId}/hr/vacation/manage`;
		let method = '';
		// 대상 리스트로 가지고 오기
		let toArea = document.querySelector('#vacation-to-area');
		console.log("toArea",toArea)
		let toAll = toArea.querySelectorAll('.receiver-to-tag');
		
		
		let toArray = Array.from(toAll, element => element.dataset.empId);
		
		// 종류 가지고 오기
		let vacationDetailValue = document.querySelector('#vacation-detail').value;
		// 개수 가지고 오기
		let vacationCntValue = document.querySelector('#vacation-cnt').value;
		let data = {
			"vacationTo" : toArray
		};
		// 종류 검증
		if(vacationDetailValue=='') {
			// 종류가 비어있을 경우
			Swal.mixin({
			    toast: true,
			    position: "center",
			    showConfirmButton: false,
			    timer: 3000,
			    timerProgressBar: true,
			    
			    // 알림 열렸을 때 실행되는 콜백함수
			    // toast 인자로 알림 DOM 요소 접근
			    didOpen: (toast) => {
			    	// 토스트에 마우스를 올렸을 때 타이머 멈추는 이벤트(알림이 안 닫힘)
			        toast.addEventListener('mouseenter', Swal.stopTimer)
			        // 토스트에 마우스 치우면 타이머 진행 이벤트
			        toast.addEventListener('mouseleave', Swal.resumeTimer)
			    }
			}).fire({
			    icon: "error",
			    title: "휴가 종류를 선택해주세요.",
			    customClass: {
			        title: 'swal-title',
			        text: 'swal-text'
			    }
			})
			return;
		} else if(vacationDetailValue=='normal') {
			method = 'post'
		} else if(vacationDetailValue=='plus') {
			method = 'put'
		}
		
		// 개수 검증
		if(vacationCntValue<=0){
			// 휴가 개수가 0개이거나 음수일 때
			Swal.mixin({
			    toast: true,
			    position: "center",
			    showConfirmButton: false,
			    timer: 3000,
			    timerProgressBar: true,
			    
			    // 알림 열렸을 때 실행되는 콜백함수
			    // toast 인자로 알림 DOM 요소 접근
			    didOpen: (toast) => {
			    	// 토스트에 마우스를 올렸을 때 타이머 멈추는 이벤트(알림이 안 닫힘)
			        toast.addEventListener('mouseenter', Swal.stopTimer)
			        // 토스트에 마우스 치우면 타이머 진행 이벤트
			        toast.addEventListener('mouseleave', Swal.resumeTimer)
			    }
			}).fire({
			    icon: "error",
			    title: "휴가 개수는 0보다 많아야 합니다.",
			    customClass: {
			        title: 'swal-title',
			        text: 'swal-text'
			    }
			})
			return;
		} else {
			data['vacationCnt']=vacationCntValue;
		}		
		
		
		
		let resp = await fetch(vacationUrl, {
			method: method,
			headers : {
				'content-type' : 'application/json'
			},
			body : JSON.stringify(data)
		});
		
		if(resp.ok){
			let {status, message, failSet} = await resp.json();
			
			if(status=='warning'){
				
				let receiverDiv = document.querySelectorAll('.receiver-to-tag');
				receiverDiv.forEach(element=>{
					let elementId = element.dataset.empId;
					if(!failSet.includes(elementId)){
						element.remove();
					}
				})
						
				Swal.fire({
					title:"주의",
					html:"이미 일반휴가 등록이 되어 등록이 불가능하거나 <br> 일반휴가가 등록되지 않아 <br> 추가 휴가를 지급할 수 없는 직원이 존재합니다.<br>\"대상\"에서 확인해주세요.",
					icon: status
				})
			} else if(status=='success'){
				Swal.fire({
					title:"성공",
					text:"휴가 등록이 완료되었습니다.",
					icon: status
				}).then(()=>{
					location.reload();
				})
				
			} else if(status=='error'){
				Swal.fire({
					title:"실패",
					html:"서버 오류로 휴가를 추가하지 못 했습니다.",
					icon: status
				})
			}
		} else {
			console.error();
		}
	})
		

   

    // 시작일 변경 시 종료일의 최소 날짜를 설정
    startDateInput.addEventListener("change", function () {
        const startDate = this.value;
        endDateInput.min = startDate; // 종료일 최소값을 시작일로 설정
    });

    // 종료일 변경 시 유효성 검증
    endDateInput.addEventListener("change", function () {
        const startDate = startDateInput.value;
        const endDate = this.value;

        if (endDate < startDate) {
            alert("종료일은 시작일보다 이전일 수 없습니다.");
            this.value = ""; // 잘못된 입력값 초기화
        }
    });

    // 사원 등록 버튼 이벤트 바인딩 (사원 등록에 대한 처리는 hrAddEmployee.js 에서 처리함. )
    const addBtn = document.querySelector('#addEmp');
    if (addBtn) {
        addBtn.addEventListener("click", () => {

            const width = 1250; // 창 너비
            const height = 650; // 창 높이 
            // 화면의 중앙에 배치하기 위해 위치 계산
            const left = (window.innerWidth / 2) - (width / 2) + window.screenX;
            const top = (window.innerHeight / 2) - (height / 2) + window.screenY;
            
            window.open(
                `${contextPath}/${companyId}/hr/employee/addForm`, // 열릴 url 경로
                "_blank", // 새 창으로 열기 
                `width=${width},height=${height},left=${left},top=${top}` // 창의 크기 및 위치
            );

        });
    } else {
        console.error("사원추가 버튼이 DOM에 존재하지 않습니다.");
    }



    // 정보수정 버튼을 눌렀을 때 모달 
    const empModifyBtn = document.querySelector("#empModifyBtn"); //
    const modEmpModal = document.querySelector("#modEmpModal");
    let modEmpInstanceModal = null;  // 모달 인스턴스 변수 
    const modEmpModalBody = document.querySelector("#modEmpModalBody");

    // 수정버튼 클릭 이벤트 처리 
    document.addEventListener("click", (e) => {
        if (e.target.closest("#empModifyBtn")) {    // 수정 버튼이 클릭되었는지 확인 
            e.preventDefault();

            // 클릭된 버튼의 부모 <tr>에서 empId가져오기 
            const targetRow = e.target.closest("tr"); 
            // 첫번째 자식이 empId임. (첫번째 열에서 사원ID 추출) 
            const empId = targetRow.querySelector("td:first-child").innerText.trim();
            console.log("선택된 empId : ", empId);


            fetch(`${contextPath}/${companyId}/hr/employee/readOneEmp/${empId}`)
                .then(resp => {
                    if (!resp.ok) throw new Error("사원 한명 조회중 오류");

                    return resp.json();
                })
                .then(data => {
                    console.log("사원 한명 조회 데이터 :", data);
                    
                    // 모달 생성후 show 
                    modEmpInstanceModal = new bootstrap.Modal(modEmpModal);
                    modEmpInstanceModal.show();

                    modEmpModalBody.innerHTML =
                        `
                <div class="table-responsive"> 
                    <table class="table">
                    <tr>
                    <th>프로필 이미지</th>
                    <td>${data.empImg
                            ? `<img src="data:image/*;base64,${data.base64EmpImg}" alt="${data.empName}" style="max-width: 100px; max-height:100px">`
                            : `<img src="${contextPath}/resources/images/profile-img.jpg" style="max-width: 100px; max-height:100px">`} </td></tr>
                        <tr>
                            <th>사원번호</th>
                            <td>${data.empId || '-'} </td>
                        </tr>

                        <tr>
                            <th>사원이름</th>
                            <td>${data.empName || '-'} </td>
                        </tr>
                        <tr>
                            <th>생년월일</th>
                            <td>${data.empBirth || '-'} </td>
                        </tr>
                        <tr>
                            <th>성별</th>
                            <td>${data.empGender || '-'} </td>
                        </tr>
                        <tr>
                            <th>주소</th>
                            <td>${data.empAddr1 || '-'} </td>
                        </tr>
                        <tr>
                            <th>상세주소</th>
                            <td>${data.empAddr2 || '-'} </td>
                        </tr>
                        <tr>
                            <th>핸드폰번호</th>
                            <td>${data.empPhone.substring(0, 3)}-${data.empPhone.substring(3, 7)}-${data.empPhone.substring(7, 11)} </td>
                        </tr>
                        <tr>
                            <th>입사일</th>
                            <td>
								<input type="date" class="form-control" name="empJoin" id="empJoin" value="${data.empJoin.substring(0, 4)}-${data.empJoin.substring(4, 6)}-${data.empJoin.substring(6, 8)}">
							</td>
                        </tr>
                        <tr>
                            <th>도장이미지</th>
                            <td>${data.empSignimg
                                ? `<img src="data:image/*;base64,${data.base64EmpSignimg}" alt="${data.empName}" style="max-width: 100px; max-height:100px">`
                            : `없음`
                            }</td>
                        </tr>
                        <tr>
                            <th>재직상태</th>
                            <td>
                            <select id="modSelectStatus" class="form-select"></select>
                                
                            </td>
                            
                            
                        </tr>
                        <tr>
                            <th>직급</th>
                            <td>
                            <select id="modSelectPosi" class="form-select"></select> 
                            </td>
                        </tr>
                        <tr>
                            <th>이메일</th>
                            <td>${data.empMail} </td>
                        </tr>
                        <tr>
                            <th>부서</th>
                            <td>
                            <select id="modSelectDepart" class="form-select"></select>
                            </td>
                        </tr>
                    </table>

                </div>
                `;
                
                // 상태 , 직급 ,부서 데이터를 select 태그에 채우기 
                // 함수의 파라미터의 맨 마지막은 selectedValue 이기 때문에 함수호출 맨마지막에 처음에 보여줄 값을 설정해주는 것.
                fetchStatuses().then(() => {
                    const selectedStatusId = allStatuses.find(item => item.name === data.empStatus)?.id || '';
                    modrenderOptions("modSelectStatus", allStatuses, selectedStatusId);
                });
                
                fetchPositions('selectPosi').then(() => {
                    const selectedPositionId = allPositions.find(item => item.name === data.posiName)?.id || '';
                    modrenderOptions("modSelectPosi", allPositions, selectedPositionId);
                });
                
                fetchDepartments('selectDepart').then(() => {
                    modrenderOptions("modSelectDepart", allDepartments, data.departmentVO.departCode);
                });
                

                })
                .catch(error => {
                    console.log("데이터 전송 오류 :", error);
                })

        }
    })

    document.addEventListener("click", (e) => {
        if (e.target.id === "modEmpBtn") { // 클릭된 요소의 id가 'modEmpBtn' 인지 확인 
            e.preventDefault();
    
            // 버튼 클릭 시점에 최신 DOM 요소를 가져옵니다.
			const joinDate = document.querySelector('#empJoin').value;
			let empJoin = joinDate.replace(/-/g, "");
            const status = document.querySelector("#modSelectStatus")?.value || ''; // 선택된 재직 상태 값을 가져오고, 없으면 빈 문자열 설정정 
            const position = document.querySelector("#modSelectPosi")?.value || ''; // 선택된 직급 값을 가져오고, 없으면 빈 문자열 설정 
            const depart = document.querySelector("#modSelectDepart")?.value || ''; // 선택된 부서 값을 가져오고, 없으면 빈 문자열 설정 
    
            // empId 가져오기 : 모달 내부 테이블의 두번째 행(tr)의 첫번째 td값 
            const empId = document.querySelector("#modEmpModalBody table tr:nth-child(2) td").innerText.trim();
            
            // 수정할 데이터를 객체로 만들기.
            const formData = {
                empId: empId,
                empStatus: status,
                posiId: position,
                departCode: depart,
				empJoin: empJoin
            };
    
            fetch(`${contextPath}/${companyId}/hr/employee/modOneEmp`, {
                method: 'PUT',
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(formData)
            })
            .then(resp => {
                if (!resp.ok) throw new Error("수정 도중 오류 발생");
                return resp.json();
            })
            .then(data => {
                modEmpInstanceModal.hide();

                modEmpModal.addEventListener('hidden.bs.modal', ()=>{
                    Swal.fire({
                        title : "수정 완료"
                        ,text : "사원정보 수정이 완료되었습니다."
                        ,icon : "success"
                    }).then(()=>{
                        // employeeList(1);
                        paging(1);
                    });

                })
             
            })
            .catch(error => {
                console.error("서버 오류: ", error);
            });
        }
    });
    
    document.addEventListener("click",(e)=>{
        if(e.target.closest("#empRemoveBtn")){  // 선택된 요소의 id가 empRemoveBtn인지 확인  
            e.preventDefault();
            // 클릭된 버튼의 부모 <tr>에서 empId가져오기 
            const targetRow = e.target.closest("tr"); 
            // tr 요소의 첫번째 자식 td 에서 사원번호 가져오기 
            const empId = targetRow.querySelector("td:first-child").innerText.trim();

            Swal.fire({
                title: "정말 퇴사처리 하시겠습니까?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonText: "삭제",
                cancelButtonText: "취소",
                // reverseButtons: true 
            }).then((result)=> {
                if(result.isConfirmed){ // 사용자가 삭제 버튼을 눌렀을 경우 퇴사 처리 진행.
                    fetch(`${contextPath}/${companyId}/hr/employee/removeOneEmp/${empId}`,{
                        method : "DELETE" 
                    })
                    .then(resp => {
                        if(!resp.ok) throw new Error("퇴사 처리 도중 문제")
                        return resp.text();
                    })
                    .then(data => {
                        Swal.fire({
                            title: "퇴사 처리 완료",
                            text: "퇴사처리 완료 되었습니다.",
                            icon: "success"
                        }).then(()=>{
                            // employeeList(1);
                            paging(1);
                        })
                    }).catch(error => {
                        console.log(error);
                        Swal.fire({
                            title: "오류 발생",
                            text: "퇴사처리중 중 문제가 발생했습니다.",
                            icon: "error"
                        });
                    })// catch end  
                } // isConfirmed end 
            })

        }
    })

    
    let empVacaModal = document.querySelector("#empVacaModal");
    let empVacaInstanceModal = null;
    // 휴가 현황 버튼 클릭시 이벤트
    document.addEventListener("click" ,(e) => {
        if(e.target.closest("#empVacaDetailBtn")){
            
            e.preventDefault();
            // 클릭된 버튼의 부모 <tr>에서 empId가져오기 
            const targetRow = e.target.closest("tr"); 
            // tr 요소의 첫번째 자식 td 에서 사원번호 가져오기 
            const empId = targetRow.querySelector("td:first-child").innerText.trim();

            empVacaInstanceModal = new bootstrap.Modal(empVacaModal);
            empVacaModalBody.dataset.empId = empId;
            empVacaInstanceModal.show();

            fetchGetVstaCode().then(()=> {
                let defaultVstaCode = allVstaCode[0]?.id || '';
                fetchVacationDetail(empId,defaultVstaCode);
            });
            
             // 이벤트 리스너 중복 방지
        // const year = new Date();
        addVacaBtn = document.querySelector("#addVacaBtn");
        // if(addVacaBtn.style.visibility !== 'hidden'){
        //     addVacaBtn.style.visibility = 'hidden'
        // }else{
            
        //     addVacaBtn.style.visibility = 'visible';
        // }
        addVacaBtn.removeEventListener("click", handleAddVacation); // 기존 이벤트 제거
        addVacaBtn.addEventListener("click", handleAddVacation); // 새로운 이벤트 등록
    }
});

            
    // 휴가 추가 부여 로직
function handleAddVacation(e) {
    e.preventDefault();

    Swal.fire({
        position: 'center',
        title: "휴가 추가 부여",
        input: "number",
        inputLabel: "추가 부여할 휴가 일수를 입력하세요",
        inputAttributes: {
            min: 1,
            max: 365,
            step: 1,
        },
        showCancelButton: true,
        confirmButtonText: "완료",
        cancelButtonText: "취소",
        customClass: {
            popup: 'swal2-custom-z-index' // 사용자 정의 클래스 추가
            // ,popup : 'swal2-custom-position'
        }
        ,target: '#empVacaModal' // SweetAlert2가 이 모달을 기준으로 생성됨
    }).then((result) => {
        if (result.isConfirmed) {
            const addVacationDays = parseInt(result.value, 10);
            if (!isNaN(addVacationDays) && addVacationDays > 0) {
                const empVacaModalBody = document.querySelector("#empVacaModalBody");
                const empId = empVacaModalBody.dataset.empId;
                const vstaCode = new Date().getFullYear();

                fetch(`${contextPath}/${companyId}/hr/employee/modVacationStatus`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ empId, vstaCode, addVacationDays }),
                })
                    .then((resp) => {
                        if (!resp.ok) throw new Error("추가 부여중 오류 발생");
                        return resp.json();
                    })
                    .then((data) => {
                        empVacaInstanceModal.hide();
                        Swal.fire({
                            position: 'center',
                            title: "추가 부여 완료",
                            text: `${addVacationDays}일의 휴가가 성공적으로 추가되었습니다.`,
                            icon: "success",
                            customClass: {
                                popup: 'swal2-custom-z-index' // 사용자 정의 클래스 추가
                                // ,popup : 'swal2-custom-position'
                            }
                        }).then(() => {
                            // employeeList(1);
                            paging(1);
                        });
                            
                    })
                    .catch((error) => {
                        console.log("추가 부여중 에러 발생 ", error);
                        Swal.fire({
                            position: 'center',
                            title: "휴가 부여 실패",
                            text: "휴가 부여중 오류",
                            icon: "error",
                            customClass: {
                                popup: 'swal2-custom-z-index' // 사용자 정의 클래스 추가
                                // ,popup : 'swal2-custom-position'
                            }
                        });
                    });
            } else {
                Swal.fire({
                    position: 'center',
                    title: "유효하지 않음",
                    text: "올바른 값을 입력하세요",
                    icon: "error",
                    customClass: {
                        popup: 'swal2-custom-z-index' // 사용자 정의 클래스 추가
                        // ,popup : 'swal2-custom-position'
                    }
                });
            }
        }
    });
}
// // 필터 선택 시 직원 목록 갱신 (검색어는 input 으로 받음)
    // document.querySelector('#selectStatus')?.addEventListener("change", () => employeeList(1));
    // document.querySelector('#selectDepart')?.addEventListener("change", () => employeeList(1));
    // document.querySelector('#selectPosi')?.addEventListener("change", () => employeeList(1));
    // document.querySelector('#startJoinDate')?.addEventListener("change", () => employeeList(1));
    // document.querySelector('#endJoinDate')?.addEventListener("change", () => employeeList(1));
    // document.querySelector('#searchInput')?.addEventListener("input", () => employeeList(1));

    // 1230 추가 (민재)
    // document.querySelector('#searchInput').addEventListener('input', (e) => {
    //     document.querySelector('input[name="searchWord"]').value = e.target.value;
    // });
    
    // document.querySelector('#startJoinDate').addEventListener('change', (e) => {
    //     document.querySelector('input[name="startDate"]').value = e.target.value;
    // });
    
    // document.querySelector('#endJoinDate').addEventListener('change', (e) => {
    //     document.querySelector('input[name="endDate"]').value = e.target.value;
    // });
    
    // document.querySelector('#selectDepart').addEventListener('change', (e) => {
    //     document.querySelector('input[name="department"]').value = e.target.value;
    // });
    
    // document.querySelector('#selectPosi').addEventListener('change', (e) => {
    //     document.querySelector('input[name="position"]').value = e.target.value;
    // });


// 페이징 렌더링 함수
// function renderPagination(data) {
//     const pagination = document.getElementById('pagination'); // 페이지네이션 컨테이너 
//     pagination.innerHTML = ""; // 기존 페이지네이션션 초기화

//     const { currentPage, totalPage, startPage, endPage } = data;

//     // **totalPage가 1 이하일 때 페이지네이션 숨기기**
//     if (totalPage <= 1) {
//         pagination.style.display = "none"; // 페이지네이션 숨김
//         return;
//     } else {
//         pagination.style.display = "block"; // 페이지네이션 표시
//     }

//     let paginationHTML = ""; // 페이지네이션 HTML 문자열 초기화 

//     // **이전 버튼 (첫 페이지가 아닐 때만 렌더링)**
//     if (currentPage > 1) {
//         paginationHTML += `<button data-page="${currentPage - 1}" class="pagination-button btn"> &lt; </button>`;
//     }

//     // **페이지 번호 버튼**
//     for (let i = startPage; i <= endPage; i++) {
//         paginationHTML += `
//             <button data-page="${i}" class="pagination-button btn ${i === currentPage ? 'active' : ''}">${i}</button>
//         `;
//     }

//     // **다음 버튼 (마지막 페이지가 아닐 때만 렌더링)**
//     if (currentPage < totalPage) {
//         paginationHTML += `<button data-page="${currentPage + 1}" class="pagination-button btn"> &gt; </button>`;
//     }

//     // HTML 삽입
//     pagination.insertAdjacentHTML("beforeend", paginationHTML); // 페이지네이션 HTML을 페이지네이션 컨테이너에 추가가

//     // 이벤트 리스너 추가
//     pagination.addEventListener('click', function (event) {
//         const target = event.target;    // 이벤트가 발생한 요소 가져옴 
//         if (target.tagName === 'BUTTON' && target.dataset.page) { // 버튼 태그이면서 데이터 속성 'page'가 있는지 확인 
//             const page = parseInt(target.dataset.page, 10); // 버튼의 페이지 번호를 정수로 변환 
//             employeeList(page);      // 해당 페이지의 직원 목록을 다시 불러옴. 
//         }
//     });
// }


// 직원 리스트를 불러오는 함수 (페이지 번호를 기본 1로 설정 )
// function employeeList(page = 1) {
//     const department = document.querySelector('#selectDepart')?.value || '';    // 부서 선택 값 
//     const position = document.querySelector('#selectPosi')?.value || '';        // 직급 선택 값 
//     const startDate = document.querySelector('#startJoinDate')?.value || '';    // 시작 날짜 
//     const endDate = document.querySelector('#endJoinDate')?.value || '';        // 종료 날짜 
//     const status = document.querySelector('#selectStatus')?.value || '';        // 재직 상태
//     const searchWord = document.querySelector('#searchInput')?.value || '';     // 검색어 입력 값 

//     // fetch 요청 쿼리스트링으로 전송 (페이지 , 부서 , 직급 ,시작 종료 날짜 , 재직상태 , 검색어)
//     fetch(`${contextPath}/${companyId}/hr/employee/hrList?page=${page}&department=${department}&position=${position}&startDate=${startDate}&endDate=${endDate}&status=${status}&searchWord=${searchWord}`)
//         .then((resp) => {
//             if (!resp.ok) throw new Error("응답 실패");
//             return resp.json();
//         })
//         .then((data) => {
//             console.log("응답 데이터 :", data);
//             const employeeList = data.employeeList;  // 직원 목록 추출 
//             const tbody = document.querySelector('#innerTbody'); // 테이블의 바디 
//             tbody.innerHTML = "";   // 기존 테이블 데이터 초기화 

//             // 직원 데이터를 테이블에 행 단위로 렌더링 
//             employeeList.forEach((emp) => {
//                 const statusText = allStatuses.find(item => item.id === emp.empStatus)?.name || '상태 정보 없음'; // 재직 상태 설정
//                 const positionText = emp.posiName || '직급 정보 없음';  // 직급 정보 설정 


//                 // 퇴사자는 퇴사처리 할 필요없고 , 휴가현황 필요없음. 
//                 const requireBtn = emp.empStatus !== 'Q'
//                                  ? `<a class="dropdown-item text-danger" href="javascript:void(0);" id="empRemoveBtn">
//                                             <i class="bx bx-trash me-1"></i>퇴사 처리
//                                     </a>`
//                                     : '';
//                 const vacationBtn = emp.empStatus !== 'Q'
//                                   ? `<a class="dropdown-item text-primary" href="javascript:void(0);" id="empVacaDetailBtn">
//                                         <i class="bx bx-edit-alt me-1"></i>휴가 현황
//                                     </a>`
//                                     : '';
                
//                 const row = `
//                     <tr>
//                         <td>${emp.empId}</td>
//                         <td>${emp.empName}</td>
//                         <td>${emp.departmentVO.departName || ''}</td>
//                         <td>${positionText}</td>
//                         <td>${emp.empJoin.substring(0, 4)}-${emp.empJoin.substring(4, 6)}-${emp.empJoin.substring(6, 8)}</td>
//                         <td>${statusText}</td>
//                         <td>${emp.empMail}</td>
//                         <td>
//                             <div class="dropdown">
//                                 <button type="button" class="btn p-0 dropdown-toggle hide-arrow" data-bs-toggle="dropdown">
//                                     <i class="bx bx-dots-vertical-rounded"></i>
//                                 </button>
//                                 <div class="dropdown-menu">
//                                     ${vacationBtn}
//                                     <a class="dropdown-item text-primary" href="javascript:void(0);" id="empModifyBtn">
//                                         <i class="bx bx-edit-alt me-1"></i>정보 수정
//                                     </a>
//                                     ${requireBtn}
//                                 </div>
//                             </div>
//                         </td>
//                     </tr>`;
//                 tbody.insertAdjacentHTML("beforeend", row);
//             });

//             // 페이지 렌더링
//             renderPagination(data);

//             // "사원이 없습니다" 메시지 표시
//             if (employeeList.length === 0) {
//                 tbody.insertAdjacentHTML("beforeend", `<tr><td colspan="7">사원이 없습니다.</td></tr>`);
//             }
//         })
//         .catch((error) => {
//             console.log("직원 목록 가져오기 실패: ", error);
//         });
// }           

});
