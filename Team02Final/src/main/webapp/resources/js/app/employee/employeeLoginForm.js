/**
 * 
 */


const myJoinModal = new bootstrap.Modal('#joinModal');
const myFindPassModal = new bootstrap.Modal('#passFindModal');

function openJoinModal() {
	// input 태그들 초기화
	document.querySelectorAll('#joinModal input')
		.forEach(input => {
        	input.value = '';
    });

	myJoinModal.show();
}

function openFindPassModal() {
	// input 태그들 초기화
	document.querySelectorAll('#passFindModal input')
		.forEach(input => {
        	input.value = ''; //
    });

	myFindPassModal.show();
}

function validateInput(input, maxLength) {
	// 숫자가 아닌 문자는 제거
	input.value = input.value.replace(/[^0-9]/g, '');

	if (input.value.length > maxLength) {
		input.value = input.value.slice(0, maxLength);
	}
}

document.addEventListener("DOMContentLoaded", () => {
	const contextPath = document.body.dataset.contextpath;
	
	const pathName = window.location.pathname; // /work2gether/a001/login 이런식으로 들어옴
	const regex = /\/work2gether\/([^/]+)/i;	// 정규식 /work2gether/{여기의 값을 꺼내옴}/이후 뎁스
	const match = pathName.match(regex);
	let companyId;
	if (match && match[1]) {
		companyId = match[1];
	}
	
	// 발표용 데이터 매핑
	const empBtn = document.querySelector('#emp-btn');
	const manageBtn = document.querySelector('#manage-btn');
	const provBtn = document.querySelector('#provider-btn');
	const empMailMapping = document.querySelector('#emp-mail-mapping');
	const empPassMapping = document.querySelector('#emp-pass-mapping');

	empBtn.addEventListener("click", () => {
		empMailMapping.value = "dditgrow"
	});

	manageBtn.addEventListener("click", () => {
		empMailMapping.value="kyungjurhdqn";
		empPassMapping.value="123";

	});

	provBtn.addEventListener("click", () => {
		empMailMapping.value = "provider";
		empPassMapping.value="1234";
	});

	const searchPassPreBtn = document.querySelector('#search-pass-pre-btn');
	searchPassPreBtn.addEventListener("click", () => {
		document.querySelector('#search-pass-name').value="김형욱";
		document.querySelector('#search-pass-mail').value="dditgrow@gmail.com";
		document.querySelector('#search-pass-tel').value="01091699964";
	});

	const authEmpPreBtn = document.querySelector('#auth-emp-pre-btn');
	authEmpPreBtn.addEventListener("click", () => {
		document.querySelector('#auth-mail').value="nykeepcoding@gmail.com";
		document.querySelector('#auth-pass').value="123";
	});
	
	// caps lock 안내
	// Caps Lock 상태 추적 변수
	let isCapsLockActive = false;
	
	// Caps Lock 상태 확인 함수
	const checkCapsLock = (e, target) => {
	    if (e.getModifierState && e.getModifierState('CapsLock')) {
	        isCapsLockActive = true; // Caps Lock 활성화 상태로 설정
	        showTooltip(target);
	    } else {
	        isCapsLockActive = false; // Caps Lock 비활성화 상태로 설정
	        hideTooltip();
	    }
	};
	
	// Caps Lock 상태를 강제로 확인하는 함수 (focus 시 호출)
	const forceCheckCapsLock = (target) => {
	    // Caps Lock 상태를 감지할 수 없으므로 임의로 상태를 표시
	    if (isCapsLockActive) {
	        showTooltip(target);
	    } else {
	        hideTooltip();
	    }
	};
	
	// 커스텀 툴팁 생성
	const tooltip = document.createElement('div');
	tooltip.className = 'custom-tooltip';
	tooltip.innerText = '⚠️ Caps Lock이 활성화되어있습니다! ⚠️';
	document.body.appendChild(tooltip);
	
	// 툴팁 위치와 상태 설정
	const showTooltip = (target) => {
	    const rect = target.getBoundingClientRect();

		const modal = target.closest('.modal');
    	const tooltipParent = modal || document.body;
		tooltipParent.appendChild(tooltip);

	    tooltip.style.left = `${rect.left + window.scrollX}px`; // X축 좌표
	    tooltip.style.top = `${rect.bottom + window.scrollY + 5}px`; // Y축 좌표
	    tooltip.style.visibility = 'visible'; // 툴팁 보이기
	};
	
	const hideTooltip = () => {
	    tooltip.style.visibility = 'hidden'; // 툴팁 숨기기
	};
	
	// 입력 필드 이벤트 추가
	const passwordCapsLocks = document.querySelectorAll('.passwordCapsLock');
	passwordCapsLocks.forEach((element) => {
	    // 키 입력 시 Caps Lock 상태 확인
	    element.addEventListener('keydown', function (e) {
	        checkCapsLock(e, element);
	    });
	
	    // 입력 필드에 포커스 시 Caps Lock 상태 강제로 확인
	    element.addEventListener('focus', function () {
	        forceCheckCapsLock(element);
	    });
	
	    // 입력 필드에서 포커스 해제 시 툴팁 숨기기
	    element.addEventListener('blur', hideTooltip);
	});
	// capslock 안내
	
	

	const loginForm = document.getElementById("loginForm");
    const emailInput = document.querySelector("input[name='accountMail']");


	// 비밀번호 찾기 요청 form 태그
	const findPassModalForm = document.querySelector('#find-modal-form');

	
	
	// 비밀번호 찾기
	findPassModalForm.addEventListener("submit", async (e) => {
		e.preventDefault();
		const empPhone = document.querySelector('#search-pass-tel');
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
			let url = `${contextPath}/${companyId}/findPW`;
			let resp = await fetch(url, {
				method: "post",
				body: new URLSearchParams(new FormData(findPassModalForm))
			});
	
			if (resp.ok) {
				Swal.fire({
					title: "임시비밀번호 발송 완료",
					html: "입력하신 이메일로 임시 비밀번호가 발송되었습니다. <br> 확인해주세요!",
					icon: "success"
				}).then(() => {
					myFindPassModal.hide();
					location.reload();
				});
	
			} else {
				let message = await resp.text();
				Swal.fire({
					title: "임시비밀번호 발송 실패",
					text: message,
					icon: "error"
				}).then(() => {
					myFindPassModal.hide();
					location.reload();
				});
			}
			
		}

	})

})