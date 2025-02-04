/**
 * 
 */

document.addEventListener("DOMContentLoaded", ()=>{

	const listBtn = document.getElementById("listBtn");
	const showCmtForm = document.getElementById("showCmtForm");
	const commentArea = document.getElementById("commentArea");
	const commentForm = document.getElementById("commentForm");
	const answForm = document.getElementById("answForm");
	const editAnsBtn = document.getElementById("editAnsBtn");
	const commentEditForm = document.getElementById("commentEditForm");
	const editForm = document.getElementById("editForm");
    const editTextarea = document.getElementById("editTextarea");
    
    const dataInputBtn = document.getElementById("dataInputBtn");

    dataInputBtn.addEventListener("click", ()=>{
        document.getElementById("ansTextarea").value = "답변입니다.";
    });

	
	listBtn.addEventListener("click", () => {
		window.location.href = `${contextPath}/contract/question`;
	});
	
	
	editForm.addEventListener("submit", (e)=>{
		e.preventDefault(); 
		
		let answContent = document.getElementById("editTextarea").value;

		if(answContent.length === 0 || answContent == null){
			Swal.fire({
				title: "답변 등록 실패",
				html: "답변이 비어있습니다.<br/>답변을 작성한 후 다시 시도해 주세요.",
				icon: "error"
			});
			
			return;
		} 

		// FormData 객체 생성
        const data = new FormData(editForm);

        // 서버에 데이터 전송
        fetch(`${contextPath}/contract/question/${quNo}/answ`, {
            method: 'POST',
            body: data // FormData 객체를 직접 전송
        })
        .then((resp) => {
            if (!resp.ok) {
				 // 실패 메시지 표시
	            Swal.fire({
	                title: "등록 실패",
	                text: "답변 등록 중 문제가 발생했습니다.",
	                icon: "error"
	            });
				throw new Error('서버 응답 오류');
            }
			
			// 성공 메시지 표시
            Swal.fire({
                title: "답변 수정",
                text: "답변이 성공적으로 수정되었습니다.",
                icon: "success",
				timer:2000,
				showConfirmButton : false
            });

            // 성공 시 페이지 새로고침 또는 다른 작업 수행
            setTimeout(() => {
                 location.reload();
            }, 2000);
			
        });
	});
	
	
	editAnsBtn.addEventListener("click", () => {
        const answContentTd = commentArea.querySelector("tr:nth-child(3) td:nth-child(2)");
        const answContent = answContentTd ? answContentTd.innerText.trim() : "";

        editTextarea.value = answContent;

        commentArea.style.display = "none";
        commentEditForm.style.display = "block";
    });

	
	 showCmtForm.addEventListener("click", () => {
        if (commentArea.style.display === "none") {
            commentArea.style.display = "block";
            commentForm.style.display = "none";
        } else {
            commentArea.style.display = "none";
            commentForm.style.display = "block";
        }
    });
	
    answForm.addEventListener("submit", (e) => {
        e.preventDefault();
		let answContent = document.getElementById("ansTextarea").value;

		if(answContent.length === 0 || answContent == null){
			Swal.fire({
				title: "답변 등록 실패",
				html: "답변이 비어있습니다.<br/>답변을 작성한 후 다시 시도해 주세요.",
				icon: "error"
			});
			
			return;
		} 

        // FormData 객체 생성
        const formData = new FormData(answForm);

        // 서버에 데이터 전송
        fetch(`${contextPath}/contract/question/${quNo}/answ`, {
            method: 'POST',
            body: formData // FormData 객체를 직접 전송
        })
        .then((resp) => {
            if (!resp.ok) {
				 // 실패 메시지 표시
	            Swal.fire({
	                title: "등록 실패",
	                text: "답변 등록 중 문제가 발생했습니다.",
	                icon: "error"
	            });
				throw new Error('서버 응답 오류');
            }
			
			// 성공 메시지 표시
            Swal.fire({
                title: "답변 등록",
                text: "답변이 성공적으로 등록되었습니다.",
                icon: "success",
				timer:2000,
				showConfirmButton : false
            });

            // 성공 시 페이지 새로고침 또는 다른 작업 수행
            setTimeout(() => {
                location.reload(); // 페이지 새로고침
            }, 2000);
			
        });
        
    });
	
});