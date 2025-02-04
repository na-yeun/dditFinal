const cancelBtn = document.querySelector(".btn-danger");
cancelBtn.addEventListener("click" , () => {
    location.href = `${contextPath}/resources/jsp/groupwareDemo.jsp`
})


function getAddrForm() {
	new daum.Postcode(
		{
			oncomplete: function(data) {
				// 팝업에서 검색결과 항목을 클릭했을때 실행

				var roadAddr = data.roadAddress; // 도로명 주소 변수
				var extraRoadAddr = ''; // 참고 항목 변수

				// 법정동명이 있을 경우 추가(법정리는 제외)
				// 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
				if (data.bname !== ''
					&& /[동|로|가]$/g.test(data.bname)) {
					extraRoadAddr += data.bname;
				}

				// 건물명이 있고, 공동주택일 경우 추가
				if (data.buildingName !== ''
					&& data.apartment === 'Y') {
					extraRoadAddr += (extraRoadAddr !== '' ? ', '
						+ data.buildingName : data.buildingName);
				}

				// 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
				if (extraRoadAddr !== '') {
					extraRoadAddr = ' (' + extraRoadAddr + ')';
				}

				// 우편번호와 주소 정보를 해당 필드에 넣는다.
				document.getElementById("roadAddress").value = roadAddr;

				// 참고항목 문자열이 있을 경우 해당 필드에 넣는다.
				if (roadAddr !== '') {
					document.getElementById("extraAddress").value = extraRoadAddr;
					close();
				} else {
					document.getElementById("extraAddress").value = '';
				}

				var guideTextBox = document.getElementById("guide");
				// 사용자가 '선택 안함'을 클릭한 경우, 예상 주소라는 표시를 해준다.
				if (data.autoRoadAddress) {
					var expRoadAddr = data.autoRoadAddress
						+ extraRoadAddr;
					guideTextBox.innerHTML = '(예상 도로명 주소 : '
						+ expRoadAddr + ')';
					guideTextBox.style.display = 'block';

				} else {
					guideTextBox.innerHTML = '';
					guideTextBox.style.display = 'none';
				}
			}
		}).open();
	new daum.Postcode({
		onclose: function(state) {
			//state는 우편번호 찾기 화면이 어떻게 닫혔는지에 대한 상태 변수 이며, 상세 설명은 아래 목록에서 확인하실 수 있습니다.
			if (state === 'FORCE_CLOSE') {
				//사용자가 브라우저 닫기 버튼을 통해 팝업창을 닫았을 경우, 실행될 코드를 작성하는 부분입니다.

			} else if (state === 'COMPLETE_CLOSE') {
				//사용자가 검색결과를 선택하여 팝업창이 닫혔을 경우, 실행될 코드를 작성하는 부분입니다.
				//oncomplete 콜백 함수가 실행 완료된 후에 실행됩니다.
			}
		}
	});
}

function automap(){
	console.log("클릭쿨릭");
	const contractCompany = document.querySelector("#contractCompany");
	const contractName = document.querySelector("#contractName");
	const contractTel = document.querySelector("#contractTel");
	const contractEmail = document.querySelector("#contractEmail");


	
	contractCompany.value = "대덕인재개발원";
	contractName.value = "김대덕";
	contractTel.value = "01066517325";
	contractEmail.value = "yuminjae56@gmail.com";
	


}



document.addEventListener("DOMContentLoaded", function()  {
	
	
	
	
	let isPhoneVerified = false; // 문자인증 완료 여부를 저장하는 변수
	
	const submitBtn = document.querySelector('#submitBtn'); // 서브밋 버튼
	submitBtn.disabled = true;

	// 문자인증 form 내부의 모든 input 태그 선택 ( nope )
	const inputFields = document.querySelector("#contractTel");
	
	const sendSmsBtn = document.querySelector('#send-sms-btn'); // 문자인증 버튼
	const authBtn = document.querySelector("#check-auth-btn");

	// 문자인증 요청 결과 출력 영역
	const guidePhoneAuth = document.querySelector('#guide-phone-auth');
	// 문자인증 최종 결과 출력 영역
	const phoneAuthResult = document.querySelector('#phone-auth-result');
	// 인증번호 입력칸
	const authCode = document.querySelector('#auth-input');

	if (!sendSmsBtn) {
        console.error('sendSmsBtn 찾을 수 없습니다.');
        return;
    }
    if (!authBtn) {
        console.error('authBtn 찾을 수 없습니다.');
        return;
    }
	

	sendSmsBtn.addEventListener("click", async () => {
	

		const contractTel = document.querySelector("#contractTel").value.trim();
		
		// 전화번호 유효성 검사
		if (!/^\d{10,11}$/.test(contractTel)) {
			alert("전화번호는 숫자만 입력 가능하며 10~11자리여야 합니다.");
			return;
		}
		
		const data = new URLSearchParams({ contractTel });
		// 비동기 요청으로 form 태그에 작성된 정보들을 controller로 전송
		let resp = await fetch(`${contextPath}/contract/all/contractAuthCheck`, {
			method: 'POST',
			headers: {
				"Content-Type": "application/x-www-form-urlencoded" // 요청 본문 타입 설정
			},
			body: data
		});

		let result;

		if (resp.ok) {
			// 성공
			let message = await resp.text();
			result = message.replace(/"/g, '');

			// 인증번호 입력란 placeholder 바꾸고 disabled 풀기
			authCode.setAttribute('placeholder', '인증번호를 입력해주세요.');
			authCode.disabled = false;
			guidePhoneAuth.style.color = 'blue';
			// sms-modal-form 안의 input 태그들 전체 readonly로 바꾸기
			inputFields.readOnly = true;


		} else {
			// 실패
			let error = await resp.text();
			guidePhoneAuth.style.color = 'red';
			result = error.replace(/"/g, '');
		}
		guidePhoneAuth.innerHTML = result;

	})


	authBtn.addEventListener("click", async () => {
		// authCode에 작성한 데이터를 controller로 보내기(POST로!!!)
		const authCode = document.querySelector("#auth-input").value.trim();
		const data = new URLSearchParams({ authCode });
		let resp = await fetch(`${contextPath}/contract/all/contractAuthCheck/checkTelAuth`, {
			method: 'POST',
			headers: {
				"Content-Type": "application/x-www-form-urlencoded" // 요청 본문 타입 설정
			},
			body: data
		});

		if (resp.ok) {
			isPhoneVerified = true;
			phoneAuthResult.style.color = 'green';
            phoneAuthResult.textContent = "인증이 완료되었습니다.";
			// 인증되면 제출버튼 활성화 
			submitBtn.disabled = false;

			Swal.fire({
				title: "인증 완료",
				text:  "인증이 완료되었습니다.",
				icon: "success"
		
			})
		} else {
			let result = await resp.text();
			let html = result.replace(/"/g, '');
			phoneAuthResult.style.color = 'red';
			phoneAuthResult.innerHTML = html;

			isPhoneVerified = false;
			// 인증 안되면 제출버튼 비활성화 
			submitBtn.disabled = true;

		}

		
		

		



		// const contractStartInput = document.getElementById("contractStart");
		// const contractEndInput = document.getElementById("contractEnd");
		// const today = new Date().toISOString().split("T")[0]; // 오늘 날짜 계산
	
		// contractStartInput.min = today; // 최소값 설정
	
		// contractEndInput.readOnly = true;
	
		// // 계약시작일 변경 시 종료일의 최소 날짜를 설정
		// contractStartInput.addEventListener("change", function () {
		// 	const startDateValue = this.value;
		// 	if(!startDateValue) return;
			
		// 	const startDate = new Date(startDateValue);
		// 	const endDate = new Date(startDate);
		// 	endDate.setFullYear(endDate.getFullYear()+1); // 1년 추가
	
		// 	// 종료일 자동 설정
		// 	contractEndInput.value = endDate.toISOString().split("T")[0];
		// 	contractEndInput.min = startDateValue;
		// });
		
	})

	const contractStart = document.querySelector("#contractStart"); 
		const today = new Date().toISOString().split("T")[0];
		console.log("today >>> " ,today );
		// 오늘 날짜 매핑 
		contractStart.value = today;

		
		// 선택된 라디오 값
		const contractEndRadio = document.querySelectorAll("input[name='contractEnd']");
		const contractEnd = document.querySelector("#contractEnd");

		let selectedValue;

		contractEndRadio.forEach(ce => {
			ce.addEventListener("change" , () => {
				if(ce.checked){
					let startDate = new Date(today); // 계약 시작일
					selectedValue = ce.value;
					console.log("라디오 버튼 선택ㄱ ㅏㅄ >>>" , ce.value);
					
					switch(selectedValue){
						case "3개월" : 
							startDate.setMonth(startDate.getMonth()+3);
							break;
						
						case "6개월" :
							startDate.setMonth(startDate.getMonth()+6);
							break;

						case "1년" : 
							startDate.setFullYear(startDate.getFullYear()+1);
							break;
						
						case "3년" : 
							startDate.setFullYear(startDate.getFullYear()+3);
							break;
						
						case "5년" : 
							startDate.setFullYear(startDate.getFullYear()+5);
					}
					const endDate = startDate.toISOString().split("T")[0];
					contractEnd.value = endDate;
					console.log("게약종료일 >>>>> ", endDate);


				}

			})
		});

        
}); // DOM end 
	






