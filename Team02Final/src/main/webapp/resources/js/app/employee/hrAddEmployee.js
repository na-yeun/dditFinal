
let allDepartments = []; // 부서 전체 목록 (서버에서 가져온 데이터를 담을 용도 )
let allPositions = [];   // 직급 전체 목록 (서버에서 가져온 데이터를 담을 용도 )

// =============================================================================================================================
// hr/employee.js 에 설명 작성 해놓음.(같은 코드)
// 서버에서 부서 목록 가져오기
function fetchDepartments() {
    fetch(`${contextPath}/${companyId}/hr/employee/departments`)
        .then(response => response.json())
        .then(data => {
            // 서버에서 받아온 데이터를 가공해 allDepartments 배열에 저장장
            allDepartments = data.map(dept => ({ code: dept.departCode, name: dept.departName }));
            // 부서목록 데이터를 select태그에 렌더링 
            renderOptions('selectDepart', allDepartments, '');
        })
        .catch(error => console.error("부서 목록 가져오기 실패: ", error));
}

// 서버에서 직급 목록 가져오기
function fetchPositions() {
    fetch(`${contextPath}/${companyId}/hr/employee/positions`)
        .then(response => response.json())
        .then(data => {
            // 서버에서 받아온 데이터를 가공해 allPositions 배열에 저장 
            allPositions = data.map(posi => ({
                id: posi.code,
                name: posi.codeComment
            }));
             // 직급 목록 데이터를 select 태그에 렌더링
            renderOptions('selectPosi', allPositions, '');
        })
        .catch(error => console.error("직급 목록 가져오기 실패: ", error));
}


// ★★★★★옵션 렌더링 함수★★★★★
function renderOptions(elementId, optionList, selectedValue) {
    // 렌더링 대상의 select 태그 가져옴옴
    const selectElement = document.querySelector(`#${elementId}`);
    // select 태그의 기존 내용을 초기화 ("선택" 기본옵션만 남김)
    selectElement.innerHTML = '<option value="">선택</option>';

    // 옵션 목록 데이터를 반복하여 select 태그에 추가 
    optionList.forEach(item => {
        selectElement.insertAdjacentHTML("beforeend", `
            <option value="${item.code || item.id}" ${selectedValue === (item.code || item.id) ? 'selected' : ''}>
                ${item.name}
            </option>
        `);
    });
}
// =============================================================================================================================

document.addEventListener("DOMContentLoaded", () => {

    fetchDepartments(); // 부서 목록 렌더링 함수 호출
    fetchPositions();   // 직급 목록 렌더링 함수 호출

    const fileInput = document.getElementById("fileUpload");    // 파일 업로드 input요소 
    const uploadForm = document.getElementById("uploadForm");   // 파일 업로드 form 요소
    const uploadBtn = document.getElementById("uploadBtn");     // 업로드 버튼 요소 

    // 파일 선택 시 파일 이름 표시
    fileInput.addEventListener("change", function () {
        const fileName = this.files.length > 0 ? this.files[0].name : "파일을 선택하세요";
        // 파일이 선택되면 파일 이름 표시 , 없으면 기본 메시지 출력
        document.getElementById("fileName").textContent = fileName;
    });

    // 파일 업로드 form 제출 이벤트 
    uploadForm.addEventListener("submit", (e) => {
        e.preventDefault(); // form 기본 제출 막기

        const file = fileInput.files[0]; // 선택된 파일 가져오기 

        // 파일이 선택되지 않은 경우
        if (!file) {
            Swal.fire({
                title: "선택된 파일이 없습니다.",
                text: "파일을 선택해주세요.",
                icon: "error"
            });
            return; // 함수 종료 
        }

        // FormData 객체 생성 및 파일 데이터 추가
        const formData = new FormData();
        formData.append("empFile", file);
        

        fetch(`${contextPath}/${companyId}/hr/employee/excelUploadEmp`, {
            method: "POST",
            body: formData // FormData 객체를 전송하면 Content-Type 자동 설정됨
        })
            .then((resp) => {
                if (!resp.ok) {
                    return resp.json().then((error) => {
                        throw new Error(error.error || "업로드 실패");
                    })
                }
                return resp.json();
            })
            .then((data) => {
                console.log("bb");
                renderTable(data); // 정상 데이터 테이블에 렌더링
              
            })
            .catch((error) => {
                console.log("error  >>>", error);
                console.log("aa1111111");
                
                Swal.fire({
                    title : "엑셀 파일 입력값 누락"
                  , text: "엑셀 파일 입력값이 누락되었습니다."
                  , icon : "error"
                })
                  
            });
    });

    // insert 할 데이터를 담을 배열 
    let tableData = [];

     // 컨트롤러에서 엑셀의 헤더가 한글로 저장됨 (한글 헤더를 내부키로 변환하는 매핑 객체체)
     const headerMapping = {
        "사원명": "EMP_NAME", //외부키 "사원명"이 내부키 "EMP_NAME" 으로 변환
        "이메일": "EMP_MAIL", //외부키 "이메일"이 내부키 "EMP_MAIL" 으로 변환 .....
        "입사일자": "EMP_JOIN",
        "직급": "POSI_ID",
        "부서": "DEPART_CODE",
        "생년월일": "EMP_BIRTH",
        "성별": "EMP_GENDER",
        "전화번호": "EMP_PHONE"
    };



    // 테이블에 데이터 렌더링하는 함수. 
    function renderTable(data) {
        const tbody = document.getElementById("innerEmpInfo");
        tbody.innerHTML = "";

        tableData = [];  // 기존 데이터  초기화 

       
        

        Object.keys(data).forEach((rowKey) => {
            const row = data[rowKey];

            // 한글 헤더 => 내부 키로 매핑 변환
            // 내부키란? 
            // 엑셀 파일의 헤더는 사용자가 지정한 이름임 ex) "사원명" ,"전번".... 
            // 하지만 코드에서는 각 데이터를 처리하거나 특정 규칙을 따른 키 이름을 사용하는 것이 편리 
            // 위에 const headerMapping이 변환 하는것.  
            const transformRow = {};
            Object.keys(row).forEach((header)=>{
                const headerKey = headerMapping[header]; 
                if(headerKey){
                    transformRow[headerKey] = row[header];
                }
            });

            // 전화번호 데이터 처리 (앞에 0 추가) 엑셀에선 맨앞이 0 이면 쓸모없는 숫자로 인식 
            // '로 처리해도 되지만 그렇지 않을수도 있기 때문에 조건 걸기 
            if(transformRow.EMP_PHONE && !isNaN(transformRow.EMP_PHONE)){
                transformRow.EMP_PHONE = transformRow.EMP_PHONE.padStart(11,'0'); // 길이가 11자리가 되도록 0 추가 하기 
            }

            // 성별 코드 변환 
            let genderCode = "";
            let empGender = transformRow.EMP_GENDER?.trim() || "";
            switch(empGender){
                case "남성" :
                case "남" :
                case "남자" :
                    genderCode = "M";
                    break;
                
                case "여성" :
                case "여" :
                case "여자" :
                    genderCode = "F";
                    break;
                default : 
                    throw new Error("성별 잘못입력");
                
            }

            // if (transformRow.EMP_GENDER.trim() === "남성" || transformRow.EMP_GENDER.trim() === "남" ||  transformRow.EMP_GENDER.trim() === "남자") {
            //     console.log("성별 M >>> ",transformRow.EMP_GENDER)
            //     genderCode = "M";
            // } else if (transformRow.EMP_GENDER.trim() === "여성" || transformRow.EMP_GENDER.trim() === "여" || transformRow.EMP_GENDER.trim() === "여자") {
            //     console.log("성별 F >>> ",transformRow.EMP_GENDER)
            //     genderCode = "F";
            // }

            // 직급 코드 변환
            const posiMatch = allPositions.find(pos => pos.name.trim() === transformRow.POSI_ID.trim());
            const posiId = posiMatch ? posiMatch.id : "";

            // 부서 코드 변환
            const deptMatch = allDepartments.find(dept => dept.name.trim() === transformRow.DEPART_CODE.trim());
            const departCode = deptMatch ? deptMatch.code : "";

            // 테이블 행 (tr) 생성 및 데이터 삽입 
            console.log("empGender >>>> " , empGender);
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td><input type="checkbox" class="row-checkbox"/></td>
                <td>${transformRow.EMP_NAME || ""}</td>
                <td>${transformRow.EMP_MAIL || ""}</td>
                <td>${transformRow.EMP_JOIN || ""}</td>
                <td>${transformRow.POSI_ID || ""}</td>
                <td>${transformRow.DEPART_CODE || ""}</td>
                <td>${transformRow.EMP_BIRTH || ""}</td>
                <td>${transformRow.EMP_GENDER || ""}</td>
                <td>${transformRow.EMP_PHONE || ""}</td>
            `;
            tbody.appendChild(tr); // 생성된 행을 tbody에 추가 



            // 데이터를 tableData 배열에 추가 
            tableData.push({
                  empName: transformRow.EMP_NAME || ""
                , empMail: transformRow.EMP_MAIL || ""
                , empJoin: transformRow.EMP_JOIN || ""
                , posiId: posiId
                , departCode: departCode
                , empBirth: transformRow.EMP_BIRTH || ""
                , empGender: genderCode
                , empPhone: transformRow.EMP_PHONE || ""
            });


        });  // Object.keys end 
    }

    // 체크박스 이벤트 처리 
    // 전체선택 / 해제 (th 체크박스)
    const selectAllCheckbox = document.querySelector(".rowAll");

    // td 체크박스 (개별 선택)
    const tbody = document.querySelector("#innerEmpInfo");

    // 1. 전체 선택/해제 이벤트
    selectAllCheckbox.addEventListener("change", function () {
        const allCheckboxes = tbody.querySelectorAll(".row-checkbox");
        allCheckboxes.forEach((checkbox) => {
            checkbox.checked = this.checked; // th 상태에 따라 td 모두 체크/해제
        });
    });

    // 2. 개별 체크박스 상태 변화 시 th 체크박스 업데이트
    tbody.addEventListener("change", function (e) {
        if (e.target && e.target.classList.contains("row-checkbox")) {
            const allCheckboxes = tbody.querySelectorAll(".row-checkbox");
            const allChecked = Array.from(allCheckboxes).every((checkbox) => checkbox.checked); // 모두 체크됨 확인
            const someChecked = Array.from(allCheckboxes).some((checkbox) => checkbox.checked); // 일부만 체크 확인

            selectAllCheckbox.checked = allChecked; // 모두 체크되면 th도 체크
            selectAllCheckbox.indeterminate = !allChecked && someChecked; // 일부만 체크되면 indeterminate 상태
        }
    });

    // 일괄등록 버튼 이벤트 
    document.getElementById("addAllEmp").addEventListener("click", () => {

        // 체크된 행의 데이터 필터링
        const selectedRows = []; // 선택된 value를 담는 배열 생성
        document.querySelectorAll("#innerEmpInfo .row-checkbox").forEach((checkbox , index) => {
            if(checkbox.checked){
                selectedRows.push(tableData[index]); // 체크된 행의 데이터를 tableData에서 추출 
            }
        })


        // 선택된 데이터가 없는 경우 swal 경고창
        if (selectedRows.length === 0) {
            Swal.fire({
                title: "등록할 사원이 없습니다.",
                icon: "warn"
            });
            return;
        } // if end 

        // 일괄등록 요청 전송 
        fetch(`${contextPath}/${companyId}/hr/employee/addAllEmp`, {
            method: "POST"
            , headers: {
                "Content-Type": "application/json"
            }
            , body: JSON.stringify(selectedRows)
        })
            .then(resp => {
                if (!resp.ok) throw new Error("insert 데이터 전송 실패");

                return resp.json();
            })
            .then(data => {
                console.log("insertFetch 데이터 왔다 :", data);
                Swal.fire({
                    title: "등록 성공"
                    , text: "사원등록에 성공했습니다."
                    , icon: "success"
                }).then(() => {
                    window.close();
                });
                
            })
            .catch(error => {
                console.log(error);
                Swal.fire({
                    title: "등록 실패"
                    , text: "데이터명 오타"
                    , icon: "error"
                })
            })
    })
    // 일괄등록 버튼 이벤트 끝 

    // 사원 등록 양식 다운로드 이벤트 리스너
    const downloadBtn = document.querySelector("#downloadBtn");
    downloadBtn.addEventListener("click", (e) => {
        e.preventDefault();
        window.location.href = `${contextPath}/${companyId}/hr/employee/downloadAddTemplate`;
    });

    const addEmpModal = document.querySelector('#addEmpModal');
    const addOneEmp = document.querySelector("#addOneEmp");
    const modalBody = document.querySelector("#addEmpModalBody");
    
    
    // 사원 개별등록 모달 show 
    let addEmpInstanceModal = null;
    addOneEmp.addEventListener("click" , (e) => {
        e.preventDefault();

        console.log("개별등록 클릭");
        addEmpInstanceModal = new bootstrap.Modal(addEmpModal);
        addEmpInstanceModal.show();
    })


    
    // 사원 개별 등록 
   const addEmpForm = document.querySelector('#addEmpForm');
   addEmpForm.addEventListener("submit", (e)=>{
        e.preventDefault();

        // 개별 등록 양식에 작성한 value 가져오기.
        let formData = {
            empName: document.getElementById("empName").value,
            empMail: document.getElementById("empMail").value,
            empJoin: document.getElementById("empJoin").value,
            posiId: document.getElementById("selectPosi").value,
            departCode: document.getElementById("selectDepart").value,
            empBirth: document.getElementById("empBirth").value,
            empGender: document.getElementById("selectGen").value,
            empPhone: document.getElementById("empPhone").value
        }
        console.log(formData);
        fetch(`${contextPath}/${companyId}/hr/employee/addOneEmp`,{
            method : "POST"
            ,headers : {
                "Content-Type" : "application/json"
            }
            ,body : JSON.stringify(formData)
        })
        .then(resp => {
            if (resp.ok) {
                return resp.json();
            } else {
                throw new Error("Network response was not ok");
            }      
        })
        .then(data => {  
                console.log("개별등록 data : ", data);
                Swal.fire({
                    title : "등록 성공"
                    , text: "사원이 성공적으로 등록되었습니다."
                    , icon: "success"
                })
                .then(()=> {
                    addEmpInstanceModal.hide();
                    window.close();
                })
            

        }).catch(error => {
            console.log("에러 발생 :",error);
        })

    })
});

        


