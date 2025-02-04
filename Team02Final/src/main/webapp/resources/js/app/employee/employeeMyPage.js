/**
 * 
 */
document.addEventListener("DOMContentLoaded", () => {
	const editMyInfo = document.querySelector('#edit-myInfo');

	editMyInfo.addEventListener("click", async e => {
		const { value: password } = await Swal.fire({
			title: "비밀번호를 입력해주세요.",
			input: "password",
			inputPlaceholder: "Enter your password",
			inputAttributes: {
				autocapitalize: "off",
			    autocorrect: "off"
			}
		});
		
		if (password) {	
			const currentUrl = window.location.pathname;
			console.log(currentUrl)
	        const xhr = new XMLHttpRequest();
	        xhr.open("POST", currentUrl, false);
	        const formData = new FormData();
	        formData.append("passCheck", password);
	        formData.append("myPageAuth", true);
	        xhr.send(formData);
	        
	        if (xhr.status === 200) {
	            const response = JSON.parse(xhr.responseText);

	            if (response.success === "true") {
	                // 서버에서 받은 URL로 리다이렉션
	                window.location.href = response.redirectUrl;
	            } else {
	                // 오류 메시지 표시
	                Swal.fire("오류", response.message, "error");
	            }
	        } else {
	            Swal.fire("오류", "서버와의 통신에 실패했습니다.", "error");
	        }
		}
	})
			
	
})	
		
