
// 모달 인스턴스 변수
let employeeListModalInstance = null; // 직원 목록 모달 인스턴스
let employeeDetailModalInstance = null; // 직원 상세 모달 인스턴스
let searchModal = null;
let searchDetailInstanceModal = null; 

function sendMail(){
	let empMail = document.querySelector('.emp-mail').innerHTML;
	
	let url = `${contextPath}/${companyId}/mail/send/?empMail=${empMail}`;
	location.href = url;
}

function sendMessage(me){
	let modalBody = me.closest('#modalBody');

	// 받는 사람 정보 select
	let mEmpId = modalBody.querySelector('.empId').innerHTML;
	let mEmpName = modalBody.querySelector('.emp-name').innerHTML;
	
	console.log("modalbody >>>" , modalBody);
	console.log("mEmpId >>>" , mEmpId);
	console.log("mEmpName >>>" , mEmpName);
	

	// 쪽지 보내기 모달에 데이터 찍기
	const tagHtml = `
            <div class="receiver-tag" data-id="${mEmpId}" 
                style="display: inline-flex; align-items: center; background-color: #e7fbfd; border: 1px solid #ced4da; border-radius: 15px; padding: 5px 10px; margin-right: 5px;">
                ${mEmpName || mEmpId} <!-- 이름 또는 ID 표시 -->
                <button type="button" class="btn-close ms-2" aria-label="Close" 
                    style="font-size: 12px; margin-left: 5px; color: #fa3928; background-color: #e7fbfd;"></button>
            </div>`;
	const receiverTagsContainer = $('#receiverTags');
	receiverTagsContainer.append(tagHtml);
	employeeDetailModalInstance.hide();
	// 쪽지 보내기 모달 오픈
	const myModal = new bootstrap.Modal(document.getElementById('modalScrollable'), {
		backdrop: "static", // 백드롭 고정
		keyboard: false     // ESC 키로 닫기 방지
	});
	myModal.show();

	// 모달 열릴 때 백드롭 관리
	document.body.classList.add('modal-open'); // 백드롭 효과 유지

    // "데이터 맵핑" 버튼을 긴급 여부 체크박스 옆에 추가
    const urgencyContainer = document.querySelector('.form-check'); // 긴급 여부 부모 요소 선택
    if (urgencyContainer) {
        // 버튼이 중복으로 추가되지 않도록 기존 버튼 제거
        const existingButton = document.getElementById('dataMappingBtn');
        if (existingButton) {
            existingButton.remove();
        }

        // 데이터 맵핑 버튼 생성
        const dataMappingBtn = document.createElement('button');
        dataMappingBtn.id = 'dataMappingBtn';
        dataMappingBtn.type = 'button';
        dataMappingBtn.className = 'btn btn-outline-secondary'; // 버튼 스타일
        dataMappingBtn.textContent = '데이터 맵핑';
        dataMappingBtn.style.position = 'absolute';
        dataMappingBtn.style.right = '10px'; // 오른쪽 끝에서 10px 여백 추가
        dataMappingBtn.style.padding = '3px 6px'; // 버튼 크기 줄이기
        dataMappingBtn.style.fontSize = '12px'; // 글씨체 작게 설정
        dataMappingBtn.style.lineHeight = '1.2'; // 줄 높이 조정

        // 버튼을 긴급 여부 체크박스 옆에 추가
        urgencyContainer.appendChild(dataMappingBtn);

        // "데이터 맵핑" 버튼 클릭 이벤트
        dataMappingBtn.addEventListener('click', () => {
            // 긴급 여부 체크
            const urgencyCheckbox = document.getElementById('semergencyYn');
            if (urgencyCheckbox) {
                urgencyCheckbox.checked = true;
            }

            // 제목 및 내용 맵핑
            const messageTitleInput = document.getElementById('smesTitle');
            const messageContentTextarea = document.getElementById('smesContent');

            if (messageTitleInput) {
                messageTitleInput.value = "안녕하십니까 영업부 김형욱 부서장입니다";
            }

            if (messageContentTextarea) {
                messageContentTextarea.value = "전에 말씀하신 부분 체크해서 메일 보냈습니다. 확인 부탁드립니다.";
            }

          
        });
    }
}

document.addEventListener("DOMContentLoaded", () => {

    document.getElementById("backToList").addEventListener("click", () => {
        if (employeeDetailModalInstance) {
            employeeDetailModalInstance.hide(); // 상세 모달 닫기
        }

        if (!employeeListModalInstance) {
            // 리스트 모달 초기화
            const modalElement = document.getElementById("employeeListModal");
            if (modalElement) {
                employeeListModalInstance = new bootstrap.Modal(modalElement);
            } else {
                console.error("리스트 모달 엘리먼트를 찾을 수 없습니다.");
            }
        }

        if (employeeListModalInstance) {
            employeeListModalInstance.show(); // 리스트 모달 다시 표시
        }
    });


    // 조직도 클릭 이벤트
    document.getElementById("organiTree").addEventListener("click", async (e) => {
        e.preventDefault();
        const target = e.target.closest("g");  // apexTree.js를 사용하면 트리 노드가 g태그로 자동 생성됨. 

        if (target) {
            const departCode = target.getAttribute("data-self"); // TEAM ID 가져오기

            try {
                let resp = await fetch(`${contextPath}/${companyId}/organiEmployee/${departCode}`, { 
                    headers: { "Content-Type": "application/json" }
                });

                if (resp.ok) {
                    const data = await resp.json();

                    // 모달 내용 설정
                    const modalBody = document.getElementById("employeeListModalBody"); // 모달 아이디 가져오기 .
                    if(data.length > 0){ // 데이터값이 있으면 사원 목록 출력 

                   
                    modalBody.innerHTML = `
                       
                        <table class="table table-responsive table-bordered employeeTable">  
                            <thead>
                                <tr>
                                    <th>사원번호</th>
                                    <th>사원명</th>
                                    <th>직급</th>
                                    <th>전화번호</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${data.map(emp => ` 
                                    <tr class="selectOne" data-emp-id="${emp.employeeList[0]?.empId}">
                                        <td>${emp.employeeList[0]?.empId || '사원번호 없음'}</td>
                                        <td>${emp.employeeList[0]?.empName || '사원명 없음'}</td>
                                        <td>${emp.posiName || '직급 정보 없음'}</td>
                                        <td>${emp.employeeList[0]?.empPhone.substring(0,3)}-${emp.employeeList[0]?.empPhone.substring(3,7)}-${emp.employeeList[0]?.empPhone.substring(7,11)}</td>
                                    </tr>
                                `).join('')} 
                            </tbody>
                        </table>
                    `;  // 받아온 데이터를 map으로 돌리기. 값이 없을때의 값도 추가 .

                     // 모달 인스턴스 생성 및 표시
                     const modalElement = document.getElementById("employeeListModal");  // employeeListModal 아이디 get 
                     if (!employeeListModalInstance) {
                         employeeListModalInstance = new bootstrap.Modal(modalElement);
                     }
                     employeeListModalInstance.show();  // 이미 Modal객체 생성한 인스턴스를 의미! show() 로 모달 호출 
                     
                    }else{  // 데이터 값이 없으면 아래 출력 
                        swal.fire({
                            title: "소속 직원 없음",
                            text:"이 부서에는 소속 직원이 없습니다.",
                            icon:"error"
                        });
                    }
                }                                      // aria-hidden 속성 false 로 변경 
                    
            } catch (error) {
                console.error("오류 발생: ", error);
            }
        }
    });

    // 직원 목록에서 한명 클릭 시 상세 모달 표시
    // 이벤트 위임 기법 : 부모 요소에 이벤트 리스너 등록하고, 이벤트의 target을 통해 하위요소 이벤트 처리 
    // 여기서는 .selectOne 클래스가 적용된 tr 태그가 하위요소임.
    document.addEventListener("click", async (e) => {   
                                    
        const targetEmploy = e.target.closest(".selectOne");  
        if (targetEmploy) {
            const empId = targetEmploy.getAttribute("data-emp-id");  // 위에서 생성한 data-emp-id 의 속성 가져오기 (내가 클릭한 사원 아이디)
       
            try {
                let resp = await fetch(`${contextPath}/${companyId}/organiEmployee/detail/${empId}`, {
                    headers: { "Content-Type": "application/json" }
                });

                // 뒤로가기 버튼 이벤트
                document.getElementById("backToList").addEventListener("click", () => {
                    if (employeeDetailModalInstance) {
                        employeeDetailModalInstance.hide(); // 직원 상세 모달 닫기
                        employeeListModalInstance.show();
                    }
                       
                });


                if (resp.ok) {
                    const data = await resp.json();

                    // 기존 직원 목록 모달 닫기
                    if (employeeListModalInstance) {
                        employeeListModalInstance.hide(); // 모달 숨겨버리기~
                                                          // aria-hidden 속성 false 로 변경 
                    }

                    // 상세 모달 내용 설정
                    const modalBody = document.getElementById("modalBody");
                    modalBody.innerHTML = ` 
                        <table class="table table-striped table-bordered">
                            <tbody>
                                <tr>
                                    <th>사진</th>
                                    <td>
                                        ${data.empImg 
                                            ? `<img src="data:image/*;base64,${data.base64EmpImg}" alt="${data.empName}" style="max-width: 100px;">` 
                                            : `<img src="${contextPath}/resources/images/profile-img.jpg" style="max-width: 100px;">`}
                                    </td>
                                </tr>
                                <tr><th>사원번호</th><td class="empId" data-emp-mail=${data.empMail}>${data.empId}</td></tr>
                                <tr><th>사원명</th><td class="emp-name">${data.empName}</td></tr>
                                <tr><th>직급</th><td>${data.posiName || '직급 정보 없음'}</td></tr>
                                <tr><th>전화번호</th><td>${data.empPhone.substring(0,3)}-${data.empPhone.substring(3,7)}-${data.empPhone.substring(7,11)}</td></tr>
                                <tr><th>이메일</th><td class="emp-mail">${data.empMail || '이메일 없음'}</td></tr>
                                <tr><th>입사일</th><td>${data.empJoin.substring(0, 4)}-${data.empJoin.substring(4, 6)}-${data.empJoin.substring(6, 8)}</td></tr>
                                
                            </tbody>
                        </table>
                        <div style="display: flex; justify-content: center; margin-top: 10px;">
                        <a href="javascript:void(0)" onclick="sendMail()" style="margin: 0 15px;">
                          <span 
                            data-bs-toggle="tooltip" 
                            data-bs-placement="bottom" 
                            data-bs-html="true" 
                            title="<span>메일</span>">
                            <i class="bx bx-mail-send" style="font-size: 2rem; color: #555; cursor: pointer;"></i>
                         </span>
                            </a>
                        <a href="javascript:void(0)" onclick="sendMessage(this)" style="margin: 0 15px;">
                           <span 
                            data-bs-toggle="tooltip" 
                            data-bs-placement="bottom" 
                            data-bs-html="true" 
                            title="<span>쪽지</span>">
                            <i class="bx bx-message-dots" style="font-size: 2rem; color: #555; cursor: pointer;"></i>
                           </span>
                        </a>
                                
                        </div>

                       
                    `;

                    // 상세 모달 표시
                    const detailModalElement = document.getElementById("employeeModal"); 
                    if (!employeeDetailModalInstance) {
                        employeeDetailModalInstance = new bootstrap.Modal(detailModalElement);
                    }
                    employeeDetailModalInstance.show();

                     // Bootstrap Tooltip 초기화
                    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
                    [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));

                }
            } catch (error) {
                console.error("오류 발생: ", error);
            }
        }
    });
//==================================================================================================
/*
    hidden.bs.modal 는 모달이 완전히 닫힌 후 이벤트 리스너 실행 
    blur() 메소드는 해당 요소의 포커스를 해제함
    : 주로 모달이 닫힐 때 남아있는 포커스로 인해 화면 요소에 의도치 않게 포커스가 남아있지 않도록 처리 

*/
    // 모달 닫힘 이벤트로 상태 초기화
    document.getElementById("employeeListModal").addEventListener("hidden.bs.modal", () => {
        document.activeElement.blur(); // 포커스 해제
        employeeListModalInstance = null; // if(!employeeListModalInstance) 조건이 참이 되어 새로운 인스턴스 생성
    });
    document.getElementById("employeeModal").addEventListener("hidden.bs.modal", () => {
        document.activeElement.blur(); // 포커스 해제 
        employeeDetailModalInstance = null; 
    });
	

    // =============================================================================================
    // 직원 검색.  같은이름이 두명 이상일 시, 모달로 리스트 출력, 한명일 시, 바로 상세정보 출력 
    // 한명도 없을 때는 검색 결과가 없습니다 출력하기 (sweetalert)

    document.querySelector("#searchBtn").addEventListener("click", async (e) => {
        console.log("검색클릭 ");
        
        const empName = document.querySelector("#empName").value.trim();
        if(!empName){
            Swal.fire({
                position: "center",
                icon: "error",
                title: "사원명을 검색해주세요.",
                showConfirmButton: false,
                timer: 3000
            });
            return; // 검색 요청을 보내지 않고 종료
        }
    
        let resp = await fetch(`${contextPath}/${companyId}/organiEmployee/search/${empName}`, {
            headers: { 
                "Content-Type": "application/json" 
            }
        });
        // // 뒤로가기 버튼 이벤트
        // document.getElementById("goToList").addEventListener("click", () => {
        //     searchDetailInstanceModal.hide(); // 직원 상세 모달 닫기
        //     searchModal.show();
        // });
        
        if (resp.ok) {
            let employeeListModalBody = document.querySelector("#employeeListModalBody");
            let employeeListModal = document.getElementById("employeeListModal");
            const data = await resp.json();
            
            
            if (data.length > 1) {  // 비동기로 가져온 data 배열의 갯수가 2개 이상인 친구들은 모달로 리스트 출력
                
                employeeListModalInstance = new bootstrap.Modal(employeeListModal);
                employeeListModalBody.innerHTML = `
                    <table class="table table-responsive table-bordered employeeTable">
                        <thead>
                            <tr>
                                <th>사원번호</th>
                                <th>사원명</th>
                                <th>직급</th>
                                <th>전화번호</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${data.map(emp => `
                                <tr class="onePick" data-emp-id="${emp.empId}">
                                    <td>${emp.empId}</td>
                                    <td>${emp.empName}</td>
                                    <td>${emp.posiName || '직급 정보 없음'}</td>
                                    <td>${emp.empPhone.substring(0,3)}-${emp.empPhone.substring(3,7)}-${emp.empPhone.substring(7,11)}</td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                `;
                document.querySelector("#empName").value = "";   // 검색한후 모달이 출력되면서 검색창 초기화 
                employeeListModalInstance.show();
                


                // 이벤트 위임: 리스트 클릭 시 상세 정보 표시
                employeeListModalBody.addEventListener("click", (e) => {

                    if (employeeListModalInstance) {
                        employeeListModalInstance.hide(); // 모달 숨겨버리기~
                                                          // aria-hidden 속성 false 로 변경 
                    }

                    const target = e.target.closest(".onePick"); // 위의 tr태그 class 
                    if (target) {
                        const empId = target.dataset.empId; // 클릭한 사원의 ID 추출
                        const emp = data.find(emp => emp.empId === empId);  // empId가 dataset.empId와 일치하는 객체 find 
                        // ? 는 null과 undefined를 먼저 확인해준다. 
                        // 상세 정보 모달로 내용 업데이트

                        const employeeModal = document.querySelector("#employeeModal");
                        const modalBody = document.querySelector("#modalBody");
                        
                        modalBody.innerHTML = `
                            <table class="table table-striped table-bordered">
                                <tbody>
                                    <tr>
                                        <th>사진</th>
                                        <td>
                                            ${emp.empImg 
                                                ? `<img src="data:image/*;base64,${emp.base64EmpImg}" alt="${emp.empName}" style="max-width: 100px;">` 
                                                : `<img src="${contextPath}/resources/images/profile-img.jpg" style="max-width: 100px;">`}
                                        </td>
                                    </tr>
                                    <tr><th>사원번호</th><td class="empId" data-emp-mail=${emp.empMail}>${emp.empId}</td></tr>
                                    <tr><th>사원명</th><td class="emp-name">${emp.empName}</td></tr>
                                    <tr><th>직급</th><td>${emp.posiName || '직급 정보 없음'}</td></tr> 
                                    <tr><th>전화번호</th><td>${emp.empPhone.substring(0,3)}-${emp.empPhone.substring(3,7)}-${emp.empPhone.substring(7,11)}</td></tr>
                                    <tr><th>이메일</th><td class="emp-mail">${emp.empMail || '이메일 없음'}</td></tr>
                                    <tr><th>입사일</th><td>${emp.empJoin.substring(0, 4)}-${emp.empJoin.substring(4, 6)}-${emp.empJoin.substring(6, 8)}</td></tr>
                                </tbody>
                            </table>
                            <div style="display: flex; justify-content: center; margin-top: 10px;">
                        <a href="javascript:void(0)" onclick="sendMail()" style="margin: 0 15px;">
                          <span 
                            data-bs-toggle="tooltip" 
                            data-bs-placement="bottom" 
                            data-bs-html="true" 
                            title="<span>메일</span>">
                            <i class="bx bx-mail-send" style="font-size: 2rem; color: #555; cursor: pointer;"></i>
                         </span>
                            </a>
                        <a href="javascript:void(0)" onclick="sendMessage(this)" style="margin: 0 15px;">
                           <span 
                            data-bs-toggle="tooltip" 
                            data-bs-placement="bottom" 
                            data-bs-html="true" 
                            title="<span>쪽지</span>">
                            <i class="bx bx-message-dots" style="font-size: 2rem; color: #555; cursor: pointer;"></i>
                           </span>
                        </a>
                                
                        </div>
                        `;
                        document.querySelector("#empName").value = "";
                        if (!employeeDetailModalInstance) {
                            employeeDetailModalInstance = new bootstrap.Modal(employeeModal);
                        } 
                        employeeDetailModalInstance.show(); 
                        
                       const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
                    [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
                   
            
                    }
                });
            } else if (data.length == 1) {
                const emp = data[0];
                modalBody.innerHTML = `
                    <table class="table table-striped table-bordered">
                        <tbody>
                            <tr>
                                <th>사진</th>
                               <td>
                                        ${emp.empImg 
                                            ? `<img src="data:image/*;base64,${emp.base64EmpImg}" alt="${emp.empName}" style="max-width: 100px;">` 
                                            : `<img src="${contextPath}/resources/images/profile-img.jpg" style="max-width: 100px;">`}
                                    </td>
                            </tr>
                            <tr><th>사원번호</th><td class="empId" data-emp-mail=${emp.empMail}>${emp.empId}</td></tr>
                            <tr><th>사원명</th><td class="emp-name">${emp.empName}</td></tr>
                            <tr><th>직급</th><td>${emp.posiName || '직급 정보 없음'}</td></tr>
                            <tr><th>전화번호</th><td>${emp.empPhone.substring(0,3)}-${emp.empPhone.substring(3,7)}-${emp.empPhone.substring(7,11)}</td></tr>
                            <tr><th>이메일</th><td class="emp-mail">${emp.empMail || '이메일 없음'}</td></tr>
                            <tr><th>입사일</th><td>${emp.empJoin.substring(0, 4)}-${emp.empJoin.substring(4, 6)}-${emp.empJoin.substring(6, 8)}</td></tr>
                        </tbody>
                    </table>
                       <div style="display: flex; justify-content: center; margin-top: 10px;">
                        <a href="javascript:void(0)" onclick="sendMail()" style="margin: 0 15px;">
                          <span 
                            data-bs-toggle="tooltip" 
                            data-bs-placement="bottom" 
                            data-bs-html="true" 
                            title="<span>메일</span>">
                            <i class="bx bx-mail-send" style="font-size: 2rem; color: #555; cursor: pointer;"></i>
                         </span>
                            </a>
                        <a href="javascript:void(0)" onclick="sendMessage(this)" style="margin: 0 15px;">
                           <span 
                            data-bs-toggle="tooltip" 
                            data-bs-placement="bottom" 
                            data-bs-html="true" 
                            title="<span>쪽지</span>">
                            <i class="bx bx-message-dots" style="font-size: 2rem; color: #555; cursor: pointer;"></i>
                           </span>
                        </a>
                                
                        </div>
                `;
                document.querySelector("#empName").value = ""; // 검색창 초기화 
                if (!employeeDetailModalInstance) {
                    employeeDetailModalInstance = new bootstrap.Modal(employeeModal);
                }
                employeeDetailModalInstance.show();
                
                const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
                    [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));

            } else if(data.length == 0){  // 검색 결과 없을 때 sweetalert 
                Swal.fire({
                    position: "center",
                    icon: "error",
                    title: "검색결과가 없습니다.",
                    showConfirmButton: false,
                    timer: 3000
                });
                document.querySelector('#empName').value = "";
            }
        } else {
            console.error(resp.status);
        }
    }); // 검색 끝
    


	


}); // DOM end
