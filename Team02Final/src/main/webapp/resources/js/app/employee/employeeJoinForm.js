/**
 * 
 */
 
function validateInput(input, maxLength) {
	// 숫자가 아닌 문자는 제거
	input.value = input.value.replace(/[^0-9]/g, '');

	if (input.value.length > maxLength) {
		input.value = input.value.slice(0, maxLength);
	}
}

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

document.addEventListener("DOMContentLoaded", () => {

	const empPass = document.querySelector('#empPass');
	const empPassCheck = document.querySelector('#empPasscheck');
	const passwordGuide = document.querySelector('#password-guide');
	empPass.addEventListener("input", () => {
		let passwordCheckValue = empPassCheck.value; // empPassCheck 입력값
		let passwordValue = empPass.value; // empPass 입력값


		if (passwordCheckValue != passwordValue) {
			passwordGuide.style.color = 'red';
			passwordGuide.innerHTML = `비밀번호가 일치하지 않습니다.`;

		} else {
			passwordGuide.style.color = 'gray';
			passwordGuide.innerHTML = `비밀번호가 일치합니다.`;
		}
	});
	
	empPassCheck.addEventListener("input", () => {
		let passwordCheckValue = empPassCheck.value; // empPassCheck 입력값
		let passwordValue = empPass.value; // empPass 입력값


		if (passwordCheckValue != passwordValue) {
			passwordGuide.style.color = 'red';
			passwordGuide.innerHTML = `비밀번호가 일치하지 않습니다.`;

		} else {
			passwordGuide.style.color = 'gray';
			passwordGuide.innerHTML = `비밀번호가 일치합니다.`;
		}
	});

	joinForm.addEventListener("submit", e => {
		e.preventDefault();
		
		let isPhoneConfirm = false;
		let isGoogle = false;
		let isPassword = false;
		
		// 핸드폰 번호 확인
		if (!empPhone.value.startsWith("010")) {
			Swal.mixin({
				toast: true,
				position: 'top',
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
				icon: 'error',
				title: `핸드폰 번호를 다시 확인해주세요.`
			})
		} else {
			isPhoneConfirm = true;
		}
		
		// 비밀번호 일치 확인
		let comment = passwordGuide.innerText;
		let empPassCheckValue = empPassCheck.value;
		let passwordGuideValue = passwordGuide.value;
		if (comment != '비밀번호가 일치합니다.' 
			||
			!(empPassCheckValue || passwordGuideValue)
		){
			Swal.mixin({
				toast: true,
				position: 'top',
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
				icon: 'error',
				title: `비밀번호를 확인해주세요.`
			})

		} else {
			isPassword = true;
		}
		
		// 구글 인증 확인
		let googleInfo = document.querySelector('#googleInfo').innerHTML;
		if(googleInfo.includes("구글 계정 인증완료")){
			isGoogle = true;
		} else {
			Swal.mixin({
				toast: true,
				position: 'top',
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
				icon: 'error',
				title: `구글 인증을 진행해주세요.`
			})
		}
		if(isPhoneConfirm&&isGoogle&&isPassword){
			joinForm.submit();
		}
	});

});