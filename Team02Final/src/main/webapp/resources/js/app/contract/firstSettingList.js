// 계약신청상태가 '세팅대기기'인 업체 목록 조회 
function settingCompany(page = 1) {

    const searchWord = document.querySelector("#searchInput")?.value || '';

    fetch(`${contextPath}/contract/readFSettingList?page=${page}&searchWord=${searchWord}`)
        .then((resp) => {
            if (!resp.ok) throw new Error("대기 업체 목록 조회중 오류 발생 ");
            return resp.json();
        })
        .then((data) => {
            console.log("대기업체 목록 응답 데이터 :", data);
            const waitCompanyList = data.waitSettingCompanyList;
            const waitTbody = document.querySelector("#waitTbody");

            waitTbody.innerHTML = "";

            waitCompanyList.forEach((list) => {
                
                const firstRequestDateText = list.firstRequestDate 
                ?   `<span class="badge bg-success">승인대기중</span>`
                    :
                    `<span class="badge bg-warning">메일발송완료</span>`;
                
                
            
                const row = `
                    <tr id="rowOne" data-contract-id="${list.contractId}" 
                                    data-contract-company="${list.contractCompany}" 
                                    data-contract-email="${list.contractEmail}" >
                        <td>${list.rnum} </td>
                        <td>${firstRequestDateText} </td>                              
                        <td>${list.contractCompany}</td>
                        <td>${list.contractName}</td>
                        <td>${list.contractType}</td>
                        <td>${list.contractTel.substring(0,3)}-${list.contractTel.substring(3,7)}-${list.contractTel.substring(7,11)}</td>
                        <td>${list.contractEmail}</td>
                        <td>${list.firstRequestDate || '-'}</td>
                    </tr>
                        
                `;
                waitTbody.insertAdjacentHTML("beforeend", row);
            });
            renderPagination(data);
            if (waitCompanyList.length === 0) {
                innerTbody.insertAdjacentHTML("beforeend", `<tr><td colspan="7">대기중인 업체가 없습니다.</td></tr>`);
            }
        })
        .catch((error) => {
            console.log("대기업체 목록 조회중 오류 :", error);

        })
}

function renderPagination(data) {
    const pagination = document.getElementById("pagination");
    pagination.innerHTML = "";

    const { currentPage, totalPage, startPage, endPage } = data;

    // totalPage가 1 이하일땐 페이지 네이션 숨기기 
    if (totalPage <= 1) {
        pagination.style.display = "none"; // 페이지 네이션 숨김
        return;
    } else {
        pagination.style.display = "block"; // 페이지네이션 표시
    }

    let paginationHTML = "";

    // 이전 버튼 (첫페이지 아닐때만 렌더링 )
    if (currentPage > 1) {
        paginationHTML += `<button data-page="${currentPage - 1}" class="pagination-button btn"> &lt; </button>`;
    }
    // 페이지 번호 버튼
    for (let i = startPage; i <= endPage; i++) {
        paginationHTML += `
            <button data-page="${i}" class="pagination-button btn ${i === currentPage ? 'active' : ''}" >${i}</button>
        `;
    }
    // 다음 버튼 ( 마지막 페이지가 아닐때만 렌더링 )
    if (currentPage < totalPage) {
        paginationHTML += `<button data-page="${currentPage + 1}" class="pagination-button btn"> &gt; </button>`
    }
    pagination.insertAdjacentHTML("beforeend", paginationHTML);

    pagination.addEventListener("click", function (e) {
        const target = e.target;
        if (target.tagName === 'BUTTON' && target.dataset.page) {
            const page = parseInt(target.dataset.page, 10);
            settingCompany(page);
        }
    })
}

// 업체 하나의 세팅상세 조회
let fsDetailModal = document.querySelector("#fsDetailModal");
let fsDetailModalBody = document.querySelector("#fsDetailModalBody");
let fsDetailInstanceModal = null;
let contractId; 
let contractCompany;
let contractEmail;
function handleRowClick(e) {
    const clickRow = e.target.closest("#waitTbody tr");
    if(clickRow){
        contractId = clickRow.dataset.contractId;
        contractCompany = clickRow.dataset.contractCompany;
        contractEmail = clickRow.dataset.contractEmail;
        console.log("contractId :",contractId);
        console.log("contractCompany :",contractCompany);
        console.log("contractEmail :",contractEmail);

        fsDetailModalBody.innerHTML = "";
        fsDetailInstanceModal = new bootstrap.Modal(fsDetailModal);
        fsDetailInstanceModal.show();

        fetch(`${contextPath}/contract/readOneFSetting/${contractId}`,{
            headers : {
                "Content-Type" : "application/json"
            }
        })
        .then((resp) => {
            if(!resp.ok) throw new Error("업체 세팅상세 조회중 오류");
            return resp.json();
        })
        .then((data) => {
            console.log("한 업체 상세 응답데이터: ",data);
            
                if(okBtn){
                    okBtn.style.display='block';
                }
            
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

    // HTML 렌더링
    fsDetailModalBody.innerHTML = `
    
    <div class="container">

            <!-- 업체명 -->
            <div class="form-item">
                <label class="form-label">업체명</label>
                <p class="form-value">${data.contractCompany || "N/A"}</p>
            </div>

            <!-- 업체 대표명 -->
            <div class="form-item">
                <label class="form-label">업체 대표명</label>
                <p class="form-value">${data.contractName || "N/A"}</p>
            </div>

            <div class="important-info-section">
                <h5>계약 상세 정보</h5>
                <hr/>

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
                    <p class="form-value">${data.fsetting.firstEmploy || "0"}일</p>
                </div>

                <!-- 선택한 출퇴근 시간 -->
                <div class="form-item" style="display : flex; align-items:center; gap:20px;">
                    <label class="form-label">선택한 출퇴근 시간</label>
                    <p class="form-value">
                        출근: ${data.fsetting.firstAttend?.split("-")[0]?.trim() || "N/A"}, 
                        퇴근: ${data.fsetting.firstAttend?.split("-")[1]?.trim() || "N/A"}
                    </p>
                </div>

                <!-- 선택한 전자결재 문서 -->
                <div class="form-item">
                    <label class="form-label">선택한 전자결재 문서</label>
                    <div class="inline-container">${elecHtml}</div>
                </div>

                <!-- 신청일자 -->
                <div class="form-item">
                    <label class="form-label">신청일자</label>
                    <p class="form-value">${data.fsetting.firstRequestDate || "N/A"}</p>
                </div>
            </div>
        </div>
    `;
                
        })
        .catch((error) => {
            console.error("세팅상세 조회중 오류 catch : ", error);
            fsDetailModalBody.innerHTML = `
                <h5>미신청 상태입니다.</h5>`; // 오류 및 데이터 없음 처리

                
                if (okBtn) {
                    okBtn.style.display = 'none';
                } 
        });
        
        

    }


}




document.addEventListener('DOMContentLoaded' , () => {
    settingCompany(1);
    document.querySelector("#searchInput")?.addEventListener("input", () => settingCompany(1));

    // 화살표 오름 내림차순 정렬 
    const headers = document.querySelectorAll("th");
    headers.forEach((header, index) => {
        header.addEventListener("click", () => {
            const order = header.dataset.order || "asc";
            const tbody = document.getElementById("waitTbody");
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
    
    const waitTbody = document.querySelector("#waitTbody");
    if (waitTbody) {
        waitTbody.removeEventListener("click", handleRowClick); // 기존 리스너 제거
        waitTbody.addEventListener("click", handleRowClick); // 새 리스너 등록
    }
    const okBtn = document.querySelector("#okBtn");
    // okBtn.addEventListener("click",handleRowClick);
    if(okBtn){
        // 반영 버튼 클릭시 이벤트
        okBtn.addEventListener("click", () => {
            console.log("okBtn click ");
            console.log("okBtn contId : " ,contractId);
            console.log("okBtn contCompany : " ,contractCompany);
            console.log("okBtn contEmail : " , contractEmail);
            let reqBody = {
                contractId , contractCompany , contractEmail
            }

            fetch(`${contextPath}/contract/modifyOneBucketAndStatus`, {
                method : 'PUT'
              , headers : {
                    "Content-Type" : "application/json"
              }
              , body : JSON.stringify(reqBody)
            })
            .then((resp) => {
                if(!resp.ok) throw new Error("반영 중 오류발생");
                return resp.json();
            })
            .then((data) => {
                console.log("최종 반영 응답데이터 :" , data);
                Swal.fire({
                    title : "결과 반영"
                  , text : "결과가 반영되었습니다."
                  , icon : "success"
                });
                fsDetailInstanceModal.hide();
                settingCompany(1);
            })

        })
    }
    
    
    
})