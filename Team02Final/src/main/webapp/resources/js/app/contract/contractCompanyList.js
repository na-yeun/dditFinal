let allScales = [];  // 업체규모 목록을 담는 배열

let contractId;
// 서버에서 업체규모 목록 가져와서 select에 렌더링 
function fetchScales() {
    return fetch(`${contextPath}/contract/readScaleList`)
            .then(resp => resp.json())
            .then(data => {
                allScales = data.map(sc => ({
                    id : sc.scaleId,
                    name : sc.scaleSize 
                }));
            renderOptions('selectScale',allScales,'');
            })
            .catch(error => console.log("업체규모 목록 조회 실패 : ",data));
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

// 계약업체 목록 조회 
function contractingCompany(page=1){
    const searchWord = document.getElementById("searchInput")?.value || ''; // 업체명 검색어 
    const type = document.getElementById("selectContType")?.value || '';    // 업종별 선택값 
    const scaleSize = document.getElementById("selectScale")?.value || '';    // 업체규모 선택값 

    fetch(`${contextPath}/contract/readContINGCompanyList?page=${page}&searchWord=${searchWord}&type=${type}&scaleSize=${scaleSize}`)
        .then((resp) => {
            if(!resp.ok) throw new Error("계약업체 목록 조회중 오류");
            return resp.json();
        })
        .then((data) => {
            console.log("계약업체 응답 리스트 : " ,data);
            const contCompanyList = data.companyList;
            let innerTbody = document.querySelector("#innerTbody");
            innerTbody.innerHTML="";
            
            contCompanyList.forEach((list)=> {
                const row = 
            `
                <tr id="rowOne" data-contract-id = "${list.contractId}">
                    <td>${list.rnum}</td>
                    <td>${list.contractCompany}</td>
                    <td>${list.contractName}</td>
                    <td>${list.contractTel.substring(0,3)}-${list.contractTel.substring(3,7)}-${list.contractTel.substring(7,11)}</td>
                    <td>${list.contractType}</td>
                    <td>${list.scale.scaleSize}</td>
                    <td>${list.contractStart.substring(0, 4)}-${list.contractStart.substring(4, 6)}-${list.contractStart.substring(6, 8)}</td>
                    <td>${list.contractEnd.substring(0, 4)}-${list.contractEnd.substring(4, 6)}-${list.contractEnd.substring(6, 8)}</td>
                    <td style="color : ${list.remainDays < 30 ? 'red' : 'black'}">${list.remainDays < 0 ? 0 : list.remainDays}</td>
                      


                </tr>
                `;
                innerTbody.insertAdjacentHTML("beforeend",row);
            });
                renderPagination(data);
            
            

            if(contCompanyList.length === 0) {
                innerTbody.insertAdjacentHTML("beforeend",`<tr><td colspan="7">계약업체가 없습니다.</td></tr>`)
            }
        })
        .catch((error) => {
            console.log("계약업체 목록 가져오기 실패 :" , error);
        })
    }

    function renderPagination(data){
    const pagination = document.getElementById("pagination"); 
    pagination.innerHTML="";

    const {currentPage, totalPage, startPage, endPage} = data;

    // totalPage가 1 이하일땐 페이지 네이션 숨기기 
    if(totalPage <= 1){
        pagination.style.display ="none"; // 페이지 네이션 숨김
        return;
    }else{
        pagination.style.display ="block"; // 페이지네이션 표시
    }

    let paginationHTML = "";

    // 이전 버튼 (첫페이지 아닐때만 렌더링 )
    if(currentPage > 1){
        paginationHTML += `<button data-page="${currentPage - 1}" class="pagination-button btn"> &lt; </button>`;
    }
    // 페이지 번호 버튼
    for(let i =startPage; i<=endPage; i++){
        paginationHTML += `
            <button data-page="${i}" class="pagination-button btn ${i === currentPage ? 'active' : ''}" >${i}</button>
        `;
    }
    // 다음 버튼 ( 마지막 페이지가 아닐때만 렌더링 )
    if(currentPage < totalPage){
        paginationHTML += `<button data-page="${currentPage + 1}" class="pagination-button btn"> &gt; </button>`
    }
    pagination.insertAdjacentHTML("beforeend",paginationHTML); 

    pagination.addEventListener("click", function(e){
        const target = e.target;
        if(target.tagName === 'BUTTON' && target.dataset.page) {
            const page = parseInt(target.dataset.page , 10);
            contractingCompany(page);
        }
    });



}




document.addEventListener("DOMContentLoaded" , () => {
    fetchScales();
    contractingCompany(1);

    document.querySelector("#searchInput")?.addEventListener("input",()=> contractingCompany(1));
    document.querySelector("#selectScale")?.addEventListener("change",()=> contractingCompany(1));
    document.querySelector("#selectContType")?.addEventListener("change",()=>contractingCompany(1));
   

     // 화살표 오름 내림차순 정렬 
     const headers = document.querySelectorAll("th");
     headers.forEach((header, index) => {
         header.addEventListener("click", () => {
             const order = header.dataset.order || "asc";
             const tbody = document.getElementById("innerTbody");
             const rows = Array.from(tbody.querySelectorAll("tr"));
 
             // 열 이름 가져오기 (data-column 속성 활용)
             const columnName = header.dataset.column;
 
             // 행 정렬 수행
             const sortedRows = rows.sort((a, b) => {
                 const aValue = a.children[index].innerText.trim();
                 const bValue = b.children[index].innerText.trim();
 
                 // 숫자로 처리해야 하는 열인지 확인
                 if (columnName === "rnum") {
                     // rnum은 숫자로 정렬
                     return order === "asc"
                         ? Number(aValue) - Number(bValue)
                         : Number(bValue) - Number(aValue);
                 } else {
                     // 그 외는 문자열로 정렬
                     return order === "asc"
                         ? aValue.localeCompare(bValue)
                         : bValue.localeCompare(aValue);
                 }
             });
 
             // 정렬된 행으로 테이블 업데이트
             tbody.innerHTML = "";
             sortedRows.forEach((row) => tbody.appendChild(row));
 
             // 화살표 방향 토글
             header.dataset.order = order === "asc" ? "desc" : "asc";
             updateArrowDirection(header, order);
         });
     });
      // 화살표 오름 내림 차순 끝 

    const downloadBtn = document.querySelector("#downloadBtn"); 
    downloadBtn.addEventListener("click" , (e) => {
        e.preventDefault();

        const scaleSize = document.querySelector("#selectScale")?.value || '';
        const type = document.querySelector("#selectContType")?.value || '';
        const searchWord = document.querySelector("#searchInput")?.value || '';

        window.location.href = `${contextPath}/contract/downloadExcelContingCompanyList?type=${type}&scaleSize=${scaleSize}&searchWord=${searchWord}`

    })

    // 계약업체에서 tbody를 클릭했을때 상세 조회 
    let contractId;
    let comDetailModal = document.querySelector("#comDetailModal");
    let comDetailModalBody = document.querySelector("#comDetailModalBody");
    let comDetailInstanceModal = null;
     
    document.addEventListener("click" , (e) => {
        // e.preventDefault();  // 이거때문에 페이지 이동이 안됐던거임. 
        const clickRow = e.target.closest("#innerTbody tr");
        if(clickRow){
            console.log("디테일 클릭");

            contractId = clickRow.dataset.contractId;
            console.log("계약업체아이디 : ",contractId);

            comDetailInstanceModal = new bootstrap.Modal(comDetailModal);
            comDetailInstanceModal.show();

            comDetailModalBody.innerHTML = "";

            fetch(`${contextPath}/contract/readOneCompanyDetail/${contractId}`)
                .then(resp => {
                    if(!resp.ok) throw new Error("계약 상세 불러오는중  오류 발생");
                    return resp.json();
                })
                .then(data => {
                    console.log("계약 상세 응답 데이터 : " ,data);
                    
                    // 선택한 직급 처리
                let positionHtml = "";
                if (data.fsetting.firstPosition) {
                    data.fsetting.firstPosition.split(",").forEach((position) => {
                        positionHtml += `<span class="inline-value">${position.trim()}</span>`;
                    });
                } else {
                    positionHtml = `<span class="inline-value">N/A</span>`;
                }

                // 선택한 부서 처리
                let departHtml = "";
                if (data.fsetting.firstDepart) {
                    data.fsetting.firstDepart.split(",").forEach((depart) => {
                        departHtml += `<span class="inline-value">${depart.trim()}</span>`;
                    });
                } else {
                    departHtml = `<span class="inline-value">N/A</span>`;
                }

                // 선택한 전자결재 문서 처리
                let elecHtml = "";
                if (data.fsetting.firstElec) {
                    data.fsetting.firstElec.split(",").forEach((elec) => {
                        elecHtml += `<span class="inline-value">${elec.trim()}</span>`;
                    });
                } else {
                    elecHtml = `<span class="inline-value">N/A</span>`;
                }

                comDetailModalBody.innerHTML = 
                `
                    <div class="table-responsive">
                        <table>
                            <tr>
                                <th data-column="contractCompany">업체명</th>
                                <td>
                                <input type="text" id="contractCompany" class="form-control" value="${data.contractCompany}" required />
                                </td>
                            </tr>                
                        <tr>
                            <th data-column="contractName">업체 대표명</th>
                            <td>
                            <input type="text" id="contractName" class="form-control" value="${data.contractName}" required />
                            </td>
                        </tr>
                
                        <tr>
                            <th data-column="modTypeOption">업체 대표명</th>
                            <td>
                            <select id="modTypeOption" class="form-select">
                                <option value="IT">IT</option>
                                <option value="서비스업">서비스업</option>
                                <option value="유통업">유통업</option>
                                <option value="농/축산업">농/축산업</option>
                                <option value="제조업">제조업</option>
                                <option value="기타">기타</option>
                            </select>
                            </td>
                        </tr>

                        <tr>
                            <th data-column="contractTel">업체 전화번호</th>
                            <td>
                            <input type="text" id="contractTel" class="form-control" value="${data.contractTel}" required />
                            </td>    
                        </tr>
                        <tr>
                            <th data-column="contractEmail">업체 이메일</th>
                            <td>
                            <input type="email" id="contractEmail" class="form-control" value="${data.contractEmail}" 
                                required pattern="[a-z0-9._%+\-]+@[a-z0-9.\-]+\.[a-z]{2,}$"/>
                            </td>
                        </tr>
                    </table>
                    </div>

                     <div class="important-info-section">
        <h5>계약 상세 정보</h5>
        <hr/>
        <!-- 계약기간과 남은일수를 한 줄로 정렬 -->
        <div class="row-three-item">
            <div class="form-item">
                <label class="form-label">계약기간</label>
                <p class="form-value"> 
                    ${data.contractStart.substring(0, 4)}-${data.contractStart.substring(4, 6)}-${data.contractStart.substring(6, 8)} 
                    ~ 
                    ${data.contractEnd.substring(0, 4)}-${data.contractEnd.substring(4, 6)}-${data.contractEnd.substring(6, 8)}
                </p>
            </div>
            <div class="form-item" style="margin-right: 250px;">
                <label class="form-label">남은 계약 일수</label>
                <p style="color: ${data.remainDays < 30 ? 'red' : 'black'}" class="form-value">
                    ${data.remainDays < 0 ? 0 : data.remainDays}일
                </p>
            </div>
        </div>

            <!-- 사용인원, 업체규모, 스토리지 용량 -->
            <div class="row-three-item">
                <div class="form-item">
                    <label class="form-label">사용인원</label>
                    <p class="form-value">${data.empCount.empCount}</p>
                </div>
                <div class="form-item">
                    <label class="form-label">업체규모</label>
                    <p class="form-value">${data.scale.scaleSize}</p>
                </div>
                <div class="form-item">
                    <label class="form-label">스토리지 용량</label>
                    <p class="form-value">${data.storage.storageSize}</p>
                </div>
            </div>

            <!-- 선택한 직급 -->
            <div class="form-item">
                <label class="form-label">선택한 직급</label>
                <div class="inline-container">${positionHtml}</div>
            </div>

            <!-- 선택한 부서 -->
            <div class="form-item">
                <label class="form-label">선택한 부서</label>
                <div class="inline-container">${departHtml}</div>
            </div>

            <!-- 선택한 휴가일수 -->
            <div class="form-item" style="display : flex; align-items:center; gap:20px;">
                <label class="form-label">선택한 휴가일수 : </label>
                <p class="form-value" style="margin-top:10px;">${data.fsetting.firstEmploy || "0"}일</p>
            </div>

            <!-- 선택한 출퇴근 시간 -->
            <div class="form-item" style="display : flex; align-items:center; gap:20px;">
                <label class="form-label">선택한 출퇴근 시간 : </label>
                <p class="form-value" style="margin-top:10px;">
                    출근: ${data.fsetting.firstAttend?.split("-")[0]?.trim() || "N/A"}, 
                    퇴근: ${data.fsetting.firstAttend?.split("-")[1]?.trim() || "N/A"}
                </p>
            </div>

            <!-- 선택한 전자결재 문서 -->
            <div class="form-item">
                <label class="form-label">선택한 전자결재 문서</label>
                <div class="inline-container">${elecHtml}</div>
            </div>
        </div>

        <!-- 결제 정보 섹션 -->
        <div class="payment-info-section" style="margin-top: 20px;">
            <h5>결제 정보</h5>
            <hr/>
            <!-- 신청일자와 결제금액을 한 줄로 정렬 -->
            <div class="row-three-item">
                <div class="form-item">
                    <label class="form-label">신청일자</label>
                    <p class="form-value">${data.fsetting.firstRequestDate || "N/A"}</p>
                </div>
                <div class="form-item">
                    <label class="form-label">결제금액</label>
                    <p class="form-value">${data.paymentList[0].payAmount.toLocaleString()}원</p>
                </div>
            </div>
        </div>
    `;
                
                    // db에서 가져온 업종명에 selected 하기 위해 사용 
                    const selectedType = `${data.contractType}`; 
                    let modTypeOption = document.querySelector('#modTypeOption');
                    for(let i =0; i< modTypeOption.options.length; i++){
                        if(modTypeOption.options[i].value === selectedType){
                            modTypeOption.options[i].selected = true;
                            break;
                        }
                    }

                })
                .catch(error => {
                    console.log("오류 발생 :", error);
                });   
                
                // 이벤트 리스너 중복으로 인한 removeEventListener 추가 
                const modCompyInfoBtn = document.querySelector("#modCompyInfoBtn");
                modCompyInfoBtn.removeEventListener("click", modCompyInfoHandler);
                modCompyInfoBtn.addEventListener("click", modCompyInfoHandler);
                
                    
            }
    }) // 상세 조회 끝. 
    // 계약업체 정보 수정
    let modCompyInfoHandler = (e) => {
        // e.preventDefault();
        console.log("aa");
        
        const contractCompany = document.querySelector("#contractCompany")?.value || '';
        const contractName = document.querySelector("#contractName")?.value || '';
        const contractTel = document.querySelector("#contractTel")?.value || '';
        const contractEmail = document.querySelector("#contractEmail")?.value || '';
        const contractType = document.querySelector("#modTypeOption")?.value || '';
        
        let body = { 
            contractId, contractCompany, contractName, contractTel, contractEmail, contractType
        };                                      

        fetch(`${contextPath}/contract/modContractingCompanyInfo`,{
                method : "PUT"
              , headers : {
                "Content-Type" : "application/json"
              }
              , body : JSON.stringify(body) 
        })
        .then((resp) => {
            if(!resp.ok) throw new Error("계약업체 정보 수정중 오류발생");
            return resp.json();
        })
        .then((data) => {
            console.log("정보 수정 응답 데이터 : ",data);
            Swal.fire({
                title : "정보 수정 완료"
              , text : "계약업체 정보 수정이 완료 되었습니다."
              , icon : "success"
              , customClass: {
                popup: 'swal2-custom-popup'
            }
            })
            comDetailInstanceModal.hide();
            contractingCompany(1);
        })
        .catch(error => {
            console.log("정보 수정중 오류 발생 " ,error);
            Swal.fire({
                title:"수정 실패"
              , text : "계약업체 정보 수정중 오류발생"
              , icon : "error"
              , customClass: {
                  popup: 'swal2-custom-popup'
              },
              didClose: () => { // SweetAlert2 창이 닫히면 z-index 복구 
               document.querySelector('.swal2-container').style.zIndex = 1050;
              }
            })
        })
    }




}); // DOM end 