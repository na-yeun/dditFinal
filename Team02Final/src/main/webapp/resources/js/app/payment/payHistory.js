

document.addEventListener("DOMContentLoaded" , () => {

    let rowOne = document.querySelector("#rowOne");
    document.addEventListener("click" , (e) => {
        let clickRow = e.target.closest("#rowOne");
        if(clickRow){
            console.log("클뤽쿨릭릭");
            const contractId = clickRow.dataset.contractId;
            console.log("contractId >>> ", contractId);

            let payDetailModal = document.querySelector("#payDetailModal");
            let payDetailInstanceModal = null;
            let payDetailModalBody = document.querySelector("#payDetailModalBody");
            

            fetch(`${contextPath}/${companyId}/payHistory/myCompPayDetail/${contractId}`)
                .then((resp) => {
                    if(!resp.ok) throw new Error("결제 상세 조회중 오류");
                    return resp.json();
                })
                .then((data) => {
                    console.log("결제 상세 응답데이터 >>>" , data);

                    payDetailInstanceModal = new bootstrap.Modal(payDetailModal);
                    payDetailInstanceModal.show();
                    payDetailModalBody.innerHTML = "";

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

                const row = `
                <div class="container">
            
                    <!-- 결제금액, 결제수단, 결제일자 한 줄에 배치 -->
                    <div class="row-three-item">
                        <div class="form-item">
                            <label class="form-label">결제금액</label>
                            <p class="form-value">${data.paymentList[0].payAmount.toLocaleString()} 원</p>
                        </div>
                        
                        <div class="form-item">
                            <label class="form-label">결제수단</label>
                            <p class="form-value">${data.paymentList[0].payMethod}</p>
                        </div>
                        
                        <div class="form-item">
                            <label class="form-label">결제일자</label>
                            <p class="form-value">${data.paymentList[0].payDate}</p>
                        </div>
                    </div>
                    <hr/>
                    <!-- 계약기간 단독 배치 -->
                    <div class="form-item full-width-item">
                        <label class="form-label">계약기간</label>
                        <p class="form-value">
                            ${data.contractStart.substring(0, 4)}-${data.contractStart.substring(4, 6)}-${data.contractStart.substring(6, 8)} 
                            ~ 
                            ${data.contractEnd.substring(0, 4)}-${data.contractEnd.substring(4, 6)}-${data.contractEnd.substring(6, 8)}
                        </p>
                    </div>
                    <hr/>
                    <!-- 사용인원, 업체규모, 스토리지 용량 한 줄에 배치 -->
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
                    <div class="form-item">
                        <label class="form-label">선택한 휴가일수</label>
                        <p class="form-value">${data.fsetting.firstEmploy || "0"}일</p>
                    </div>
                    
                    <!-- 선택한 출퇴근 시간 -->
                    <div class="form-item">
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
            
                </div>
            `;
            

                    payDetailModalBody.insertAdjacentHTML('beforeend',row);

                })



        }

    })

})




