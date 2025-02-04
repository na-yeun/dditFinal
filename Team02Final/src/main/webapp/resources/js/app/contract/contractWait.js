

// 계약신청상태가 '대기'인 업체 목록 조회 
function waitCompany(page = 1) {

    const searchWord = document.querySelector("#searchInput")?.value || '';

    fetch(`${contextPath}/contract/readWaitCompanyList?page=${page}&searchWord=${searchWord}`)
        .then((resp) => {
            if (!resp.ok) throw new Error("대기 업체 목록 조회중 오류 발생 ");
            return resp.json();
        })
        .then((data) => {
            console.log("대기업체 목록 응답 데이터 :", data);
            const waitCompanyList = data.waitCompanyList;
            const waitTbody = document.querySelector("#waitTbody");
            
            waitTbody.innerHTML = "";

            waitCompanyList.forEach((list) => {
                
                const row = `
                    <tr id="rowOne" data-contract-id="${list.contractId}" 
                        data-contract-email="${list.contractEmail}"
                        data-contract-company="${list.contractCompany}">
                        <td>${list.rnum}</td>
                        <td>${list.contractCompany}</td>
                        <td>${list.contractName}</td>
                        <td>${list.contractType}</td>
                        <td>${list.contractTel.substring(0,3)}-${list.contractTel.substring(3,7)}-${list.contractTel.substring(7,11)}</td>
                        <td>${list.contractEmail}</td>
                        <td>${list.payDate}</td>
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
            waitCompany(page);
        }
    })
}



document.addEventListener("DOMContentLoaded", () => {
    waitCompany(1);

    document.querySelector("#searchInput")?.addEventListener("input", () => waitCompany(1));


    let contractId;
    let contractEmail;
    let contractCompany;
    let waitComModal = document.querySelector("#waitComModal");
    let waitComModalBody = document.querySelector("#waitComModalBody");
    let waitComInstanceModal = null;
    let contractReject;
    document.addEventListener("click", (e) => {

        const clickRow = e.target.closest("#waitTbody tr");
        if (clickRow) {

            console.log("클릭한 행의 dataset:", clickRow.dataset);
            

            contractId = clickRow.dataset.contractId;
            contractEmail = clickRow.dataset.contractEmail;
            contractCompany = clickRow.dataset.contractCompany;
            
            console.log("내가 선택한 계약업체아이디 : ", contractId);
            console.log("내가 선택한 계약업체이메일 :", contractEmail);
            console.log("내가 선택한 업체 이름 : " , contractCompany);

            // 반려 버튼에 dataset 참조
            var rejectBtn = document.querySelector("#rejectBtn");
            rejectBtn.dataset.contractId = contractId;
            rejectBtn.dataset.contractEmail = contractEmail;
            rejectBtn.dataset.contractCompany = contractCompany;

            // 승인 버튼에 dataset 참조
            var okBtn = document.querySelector("#okBtn");
            okBtn.dataset.contractId = contractId;
            okBtn.dataset.contractEmail = contractEmail;
            okBtn.dataset.contractCompany = contractCompany;


            waitComInstanceModal = new bootstrap.Modal(waitComModal);
            waitComInstanceModal.show();

            waitComModalBody.innerHTML = "";

            fetch(`${contextPath}/contract/readOneWaitCompany/${contractId}`)
                .then((resp) => {
                    if (!resp.ok) throw new Error("대기업체 하나 조회중 오류 발생");
                    return resp.json();
                })
                .then((data) => {
                    console.log("대기업체 상세 응답 데이터 : ", data);
                    waitComModalBody.innerHTML = `
                    <div class="table-responsive">
                        <table class="table">
                            <tr data-contract-id="${data.contractId}" 
                                data-contract-email="${data.contractEmail}"
                                data-contract-company="${data.contractCompany}">
                                <th data-column="contractCompany">
                                    업체명
                                </th>
                                <td>${data.contractCompany}</td>
                            </tr>
                            <tr>
                                <th data-column="contractName">
                                    업체 대표 성함
                                </th>
                                <td>${data.contractName}</td>
                            </tr>
                            <tr>
                                <th data-column="contractTel">
                                    업체 전화번호
                                </th>
                                <td>${data.contractTel.substring(0,3)}-${data.contractTel.substring(3,7)}-${data.contractTel.substring(7,11)}</td>
                            </tr>
                            <tr>
                                <th data-column="contractEmail">
                                    업체 이메일
                                </th>
                                <td>${data.contractEmail}</td>
                            </tr>
                        </table>
                    </div>
                
                    <div class="important-info-section">
                        <h5>계약 상세 정보</h5>
                        <hr/>
                        <p><strong>업종명:</strong> ${data.contractType}</p>
                        <p><strong>계약 기간:</strong> 
                            ${data.contractStart.substring(0, 4)}-${data.contractStart.substring(4, 6)}-${data.contractStart.substring(6, 8)}
                            ~
                            ${data.contractEnd.substring(0, 4)}-${data.contractEnd.substring(4, 6)}-${data.contractEnd.substring(6, 8)}
                        </p>
                        <p><strong>사용인원:</strong> ${data.empCount.empCount}</p>
                        <p><strong>스토리지 용량:</strong> ${data.storage.storageSize}</p>
                        <p><strong>업체 규모:</strong> ${data.scale.scaleSize}</p>
                    </div>
                
                    <div class="payment-info-section">
                        <h5>결제 정보</h5>
                        <hr/>
                        <p><strong>결제일:</strong> ${data.paymentList[0].payDate}</p>
                        <p><strong>결제수단:</strong> ${data.paymentList[0].payMethod}</p>
                        <p><strong>결제 금액:</strong> ${data.paymentList[0].payAmount.toLocaleString()}원</p>
                    </div>
                `;
                
                })
                .catch((error) => {
                    console.log("정보 가져오는 중 오류 발생 :", error);
                });

            // 반려 버튼 클릭 이벤트 
            rejectBtn.removeEventListener("click", rejectBtnHandler);
            rejectBtn.addEventListener("click", rejectBtnHandler);

            okBtn.removeEventListener("click",okBtnHandler);
            okBtn.addEventListener("click",okBtnHandler);
        }

    }) // 상세 조회 끝 

    // 승인버튼 클릭 시 이벤트 처리 
    let okBtnHandler = (e) => {
        console.log("aaaa");

        const contractId = e.target.dataset.contractId;
        const contractEmail = e.target.dataset.contractEmail;
        const contractCompany = e.target.dataset.contractCompany;
        console.log("아이디 값 :", contractId);
        console.log("이메일 값 :", contractEmail);
        console.log("회사명 값 :" ,contractCompany);
        const body = {
            contractId
          , contractEmail
          , contractCompany
        }

        Swal.fire({
            title:"승인 처리"
          , text : "승인 처리 하시겠습니까?"
          , icon : "question"
          , showCancelButton : true
          , cancelButtonText : "아니오"
          , confirmButtonText : "예"
        })
        .then((result) => {
            if(result.isConfirmed){
                
                fetch(`${contextPath}/contract/modStatusOK`, {
                    method : "PUT"
                  , headers : {
                    "Content-Type" : "application/json"
                    }
                  , body : JSON.stringify(body)
                })
                .then((resp) => {
                    if(!resp.ok) throw new Error("승인 처리 중 오류 발생");
                    return resp.json();
                })
                .then((data) => {
                    console.log("승인처리 응답 데이터 ",data);
                    Swal.fire({
                        title : "승인 완료"
                      , text : "승인이 완료되었습니다."
                      , icon : "success"
                    })
                    waitComInstanceModal.hide();
                    waitCompany(1);
                })

            }
        })
          

    }
    
    // 반려버튼 클릭 시 이벤트 처리 
    let rejectInstanceModal = null;
    let rejectBtnHandler = (e) => {
        console.log("bbb");

        const contractId = e.target.dataset.contractId;
        const contractEmail = e.target.dataset.contractEmail;
        const contractCompany = e.target.dataset.contractCompany;
        console.log("아이디 값 :", contractId);
        console.log("이메일 값 :", contractEmail);
        console.log("회사명 값 :" ,contractCompany);

        let rejectModal = document.querySelector("#rejectModal");
        rejectInstanceModal = new bootstrap.Modal(rejectModal);
        rejectInstanceModal.show();

        let rejectBody = document.querySelector("#rejectModalBody textarea");
        if (!rejectBody) {
            console.error("textarea를 찾을 수 없습니다.");
            return;
        }
        
        rejectBody.value = "";

        const submitBtn = document.querySelector("#submitBtn");
        submitBtn.addEventListener("click", () => {
            contractReject = rejectBody.value.trim();

            if(!contractReject){
                Swal.fire({
                    title : "반려 사유 입력오류"
                  , text : "반려 사유를 입력해주세요"
                  , icon : "error"
                });
                return;
            }else if(contractReject.length > 150){
                Swal.fire({
                    title: "반려 사유 입력 오류",
                    text: "반려 사유는 150자 이내로 입력해주세요.",
                    icon: "error",
                });
                return;
            }

            const body = {
                contractId, contractReject , contractEmail, contractCompany
            }
            fetch(`${contextPath}/contract/modStatusReject`, {
                method: 'PUT'
                , headers: {
                    "Content-Type": "application/json"
                }
                , body: JSON.stringify(body)
            })
                .then((resp) => {
                    if (!resp.ok) throw new Error("수정 도중 오류 발생");
                    return resp.json();
                })
                .then((data) => {
                    console.log("reject버튼 응답 데이터 : ", data);
                    Swal.fire({
                        title: "반려 완료"
                        , text: "반려 처리가 완료되었습니다."
                        , icon: "success"
                    })
                    rejectInstanceModal.hide();
                    waitComInstanceModal.hide();
                    waitCompany(1);
                })
                .catch(error => {
                    console.log("반려처리중 에러 발생",error);
                })

        })

    

    }



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




    // 화살표 방향 업데이트 함수
    function updateArrowDirection(header, order) {
        document.querySelectorAll("th .arrow").forEach((arrow) => {
            arrow.innerText = "▼"; // 기본 방향
        });
        const arrow = header.querySelector(".arrow");
        arrow.innerText = order === "asc" ? "▲" : "▼"; // 오름차순/내림차순에 따른 방향 설정
    }

});// DOM end