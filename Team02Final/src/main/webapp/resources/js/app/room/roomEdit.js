/**
 * 
 */document.addEventListener("DOMContentLoaded", () => {
    const deleteBtn = document.getElementById("roomImgDelBtn");
    const form = document.querySelector("form");
    
    if (deleteBtn) {
        deleteBtn.addEventListener("click", () => {
            // 삭제 상태 전달을 위한 hidden input 생성
            const hiddenInput = document.createElement("input");
            hiddenInput.type = "hidden";
            hiddenInput.name = "roomImgDelete";
            hiddenInput.value = "true";
            form.appendChild(hiddenInput);

            // 이미지와 삭제 버튼 제거
            const imageWrapper = document.getElementById("roomImageWrapper");
            if (imageWrapper) imageWrapper.remove();
        });
    }
    
    const delBtn = document.getElementById("btn-delete");
    delBtn.addEventListener("click", function (e) {
        e.preventDefault();
		const companyId = document.getElementById('hidden-company-id').value;

        const url = this.getAttribute("data-url");
        if (!url) {
            alert("삭제 URL이 설정되지 않았습니다.");
            return;
        }

        Swal.fire({
            title: "삭제하시겠습니까?",
            text: "삭제 후 복구할 수 없습니다!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "삭제",
            cancelButtonText: "취소"
        }).then((result) => {
            if (result.isConfirmed) {
                // 삭제 요청
                fetch(url, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                })
                .then((response) => response.json())
                .then((data) => {
                    if (data === "success") {
                        Swal.fire({
                            title: "삭제 완료!",
                            text: "시설이 정상적으로 삭제되었습니다!",
                            icon: "success",
                        }).then(() => {
                            location.href = "../../../" + companyId + "/room";
                        });
                    } else {
                        Swal.fire({
                            icon: "error",
                            title: "삭제 실패!",
                            text: data.message || "예약이 있는 시설은 삭제 불가능합니다!",
                        });
                    }
                })
                .catch((error) => {
                    console.error("삭제 요청 중 오류 발생:", error);
                    Swal.fire({
                        icon: "error",
                        title: "삭제 실패!",
                        text: "삭제 중 오류가 발생했습니다.",
                    });
                });
            }
        });
    });
});