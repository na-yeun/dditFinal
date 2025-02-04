  	// 데이터 삽입 버튼 이벤트
      function autoMapping() {
        // 선택하고자 하는 직급과 부서 데이터 (예제 데이터)
        const positionData = ["사원", "과장", "차장", "대리", "팀장", "부장", "이사"];
        const departmentData = ["인사부", "영업부", "재무부", "총무부","경영부"];
    
        // 직급 체크박스 자동 선택
        positionData.forEach(function(position) {
            let checkbox = document.querySelector(`input[name='firstPosition'][value='${position}']`);
            if (checkbox) {
                console.log("직급 true")
                checkbox.checked = true;
            }else{
                console.log("직급 false")
            }
        });
    
        // 부서 체크박스 자동 선택
        departmentData.forEach(function(department) {
            let checkbox = document.querySelector(`input[name='firstDepart'][value='${department}']`);
            if (checkbox) {
                console.log("부서 true")
                checkbox.checked = true;
            }else{
                console.log("부서 false")
            }
        });
        
        const firstEmploy = document.querySelector("#firstEmploy");
        if(firstEmploy){
            firstEmploy.value = 15;
        }

        const attendStart = document.querySelector("#attendStart");
        if(attendStart){
            attendStart.value = "09:00";
        }
        const attendEnd = document.querySelector("#attendEnd");
        if(attendEnd){
            attendEnd.value = "18:00";
        }
}

    
    
    
        // 휴가 0일~30일 사이 설정 가능 조건 
        // 0을 최소 30일 최대로 설정 하는 함수.
        function handleVacaInput(input) {
            const vacaValue = input.value;
            if (vacaValue > 30) {
                input.vacaValue = 30;
            } else if (vacaValue < 0) {
                input.vacaValue = 0;
            }

        }

        const cancelBtn = document.querySelector(".btn-danger");
        cancelBtn.addEventListener("click", () => {
            window.history.back();
        })


        document.addEventListener("DOMContentLoaded", () => {



            const autoMappingBtn = document.querySelector("#autoMappingBtn");
            autoMappingBtn.addEventListener("click", () => {
                console.log("auto true");
                autoMapping();
            })

            const elecYes = document.getElementById("elecYes");
            const elecNo = document.getElementById("elecNo");
            const elecSettings = document.getElementById("elecSettings");

            elecYes.addEventListener("change", () => {
                if (elecYes.checked) elecSettings.style.display = "block";
            });

            elecNo.addEventListener("change", () => {
                if (elecNo.checked) elecSettings.style.display = "none";
            });

            document.getElementById("setupPageForm").addEventListener("submit", function (e) {

                const positionCheckboxes = document.querySelectorAll('input[name="firstPosition"]');
                const isPositionChecked = Array.from(positionCheckboxes).some(checkbox => checkbox.checked);
                
                // 부서 체크박스 검사
                const departmentCheckboxes = document.querySelectorAll('input[name="firstDepart"]');
                const isDepartmentChecked = Array.from(departmentCheckboxes).some(checkbox => checkbox.checked);
                
                // 유효성 검사
                if (!isPositionChecked) {
                   
                    e.preventDefault(); // 제출 방지
                    return;
                }
            
                if (!isDepartmentChecked) {
                    
                    e.preventDefault(); // 제출 방지
                    return;
                }
            
                // 모든 검사 통과 시 제출
            




                const startTime = 60; // 시작 시간 (01:00)
                const endTime = 1440; // 종료 시간 (24:00)
                const interval = 30; // 간격 (30분)

                console.log("Initializing select options...");
                const optionsHTML = generateOptions(startTime, endTime, interval);

                const attendStartSelect = document.getElementById("attendStart");
                const attendEndSelect = document.getElementById("attendEnd");

                if (attendStartSelect && attendEndSelect) {
                    console.log("Adding options to select elements...");
                    attendStartSelect.innerHTML = optionsHTML;
                    attendEndSelect.innerHTML = optionsHTML;
                } else {
                    console.error("Select elements not found!");
                }
                
                const start = document.getElementById("attendStart").value;
                const end = document.getElementById("attendEnd").value;

                if (!start || !end) {
                    alert("출근 시간과 퇴근 시간을 모두 입력해주세요.");
                    e.preventDefault(); // 폼 제출 중단
                    return;
                }

                document.getElementById("hiddenFirstAttend").value = `${start}-${end}`;

            });


            

            

        }); // DOM END 