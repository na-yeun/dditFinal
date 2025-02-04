/**
 * 
 */

document.addEventListener("DOMContentLoaded", () => {
	// url에서 회사코드 가지고 오기 위한 코드
	const pathName = window.location.pathname; // /work2gether/a001/login 이런식으로 들어옴
	const regex = /\/work2gether\/([^/]+)/i;	// 정규식 /work2gether/{여기의 값을 꺼내옴}/이후 뎁스
	const match = pathName.match(regex);
	let companyId;
	if (match && match[1]) {
		companyId = match[1];
	} else {
		console.log("No match found");
	}


	// 문자인증 문자 요청 form 태그
	const smsForm = document.querySelector('#sms-form');
	// 문자인증 확인 form 태그
	const authCheckForm = document.querySelector('#auth-check-form');
	// 문자인증 form 내부의 모든 input 태그 선택
	const inputFields = smsForm.querySelectorAll("input");
	// 문자인증 요청 결과 출력 영역
	const guidePhoneAuth = document.querySelector('#guide-phone-auth');
	// 문자인증 최종 결과 출력 영역
	const phoneAuthResult = document.querySelector('#phone-auth-result');
	// 인증번호 입력칸
	const authInput = document.querySelector('#auth-input');
	const accountMail = document.querySelector('#accountMail');

	// 문자인증 문자 요청 form 태그의 submit 핸들러
	smsForm.addEventListener("submit", async (e) => {
		e.preventDefault();
		let data = new URLSearchParams(new FormData(smsForm));
		let url = "/work2gether/" + companyId + "/empauth";


		// 비동기 요청으로 form 태그에 작성된 정보들을 controller로 전송
		let resp = await fetch(url, {
			method: 'post',
			body: data
		});

		let result;
		
		if(resp.status==200){
			// 성공
			let message = await resp.text();
			result = message.replace(/"/g, '');

			// 인증번호 입력란 placeholder 바꾸고 disabled 풀기
			authInput.setAttribute('placeholder', '인증번호를 입력해주세요.');
			authInput.disabled = false;
			guidePhoneAuth.style.color = 'blue';
			// sms-modal-form 안의 input 태그들 전체 readonly로 바꾸기
			inputFields.forEach(input => {
				input.readOnly = true;
			});
		} else {
			// 일치하는 정보 없음
			let error = await resp.text();
			guidePhoneAuth.style.color = 'red';
			result = error.replace(/"/g, '');
		} 
		guidePhoneAuth.innerHTML = result;

	})


	// 인증번호 입력 후 확인
	authCheckForm.addEventListener("submit", async (e) => {
		// authCode에 작성한 데이터를 controller로 보내기(POST로!!!)
		e.preventDefault();
		let url = "/work2gether/" + companyId + "/empauth/checkAuthCode";

		let formData = new FormData(authCheckForm);
		let empMail = accountMail.value;
		formData.append("empMail", empMail);
		

		let resp = await fetch(url, {
			method: 'post',
			body: new URLSearchParams(formData)
		});

		if (resp.ok) {
			Swal.fire({
				title: "승인이 완료되었습니다.",
				text: "로그인 페이지로 돌아가시겠습니까?",
				icon: "success",
				
				// candel 버튼 (기본값 false, 취소버튼 안 보임)
				showCancelButton: true,
				// confirm 버튼 텍스트
			    confirmButtonText: '확인',
			    // cancel 버튼 텍스트
			    cancelButtonText: '취소',
				// confirm 버튼 색 지정(승인)
			    confirmButtonColor: 'blue',
			    // cancel 버튼 색 지정(취소)
			    cancelButtonColor: 'red'
			
			// resolve 함수 생각하면 good...
			}).then(result => {
				// result(결과)가 confirm이면(승인이면)
				if(result.isConfirmed){
					window.location.href = "/work2gether/"+companyId+"/login"
				} else {
					window.location.href = "/work2gether/"+companyId+"/empauth"
				}
			})
		} else {
			let result = await resp.text();
			let html = result.replace(/"/g, '');
			phoneAuthResult.style.color = 'red';
			phoneAuthResult.innerHTML = html;
		}

	})

})