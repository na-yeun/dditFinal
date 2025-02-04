document.addEventListener("DOMContentLoaded", () => {
    const editor = new toastui.Editor({
        el: document.querySelector('#editor'),
        height: '500px',
        initialEditType: 'wysiwyg',
        initialValue: initialValue,
        previewStyle: 'vertical'
    });
    console.log("JS에서의 noticeNo:", window.noticeNo); // window 객체를 통해 접근
    const updateForm = document.querySelector('#updateForm');
    updateForm.addEventListener('submit', function (e) {
        e.preventDefault(); // 폼 제출 막기

        // 제목 체크
        const titleInput = document.querySelector('#noticeName');
        if (!titleInput.value.trim()) {
            Swal.mixin({
                toast: true,
                position: 'top',
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true,
                didOpen: (toast) => {
                    toast.addEventListener('mouseenter', Swal.stopTimer)
                    toast.addEventListener('mouseleave', Swal.resumeTimer)
                }
            }).fire({
                icon: 'error',
                title: '제목은 필수입니다.'
            });
            titleInput.focus();
            return false;
        }

        // 내용 체크
        const contentHtml = editor.getHTML();
        if (!contentHtml || contentHtml.trim() === '' || contentHtml === '<p><br></p>') {
            Swal.mixin({
                toast: true,
                position: 'top',
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true,
                didOpen: (toast) => {
                    toast.addEventListener('mouseenter', Swal.stopTimer)
                    toast.addEventListener('mouseleave', Swal.resumeTimer)
                }
            }).fire({
                icon: 'error',
                title: '내용은 필수입니다.'
            });
            editor.focus();
            return false;
        }

        // 모든 검증 통과시
        document.querySelector('#noticeContent').value = contentHtml;
        updateForm.submit();
    });


    // 이미지 업로드 훅 설정(공통코드로 쓰기위해 POST에 배치)
    editor.addHook('addImageBlobHook', function (blob, callback) {
        var formData = new FormData();
        formData.append('uploadFile', blob);

        fetch(uploadImageUrl, {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                callback(data.url);
            })
            .catch(error => console.error(error));
    });

    // 기존 첨부파일 삭제 버튼 이벤트
    document.querySelectorAll("[data-atch-file-id][data-file-sn]")
        .forEach(el => {
            el.addEventListener("click", async (e) => {
                e.preventDefault();

                // 삭제 전 확인
                const result = await Swal.fire({
                    title: '파일 삭제',
                    text: "첨부파일을 삭제하시겠습니까?",
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: '삭제',
                    cancelButtonText: '취소'
                });

                if (result.isConfirmed) {
                    const atchFileId = el.dataset.atchFileId;
                    const fileSn = el.dataset.fileSn;

                    try {
                        const resp = await fetch(`${contextPath}/${companyId}/notice/${noticeNo}/atch/${atchFileId}/${fileSn}`, {
                            method: "DELETE",
                            headers: {
                                "Accept": "application/json"
                            }
                        });

                        if (resp.ok) {
                            const result = await resp.json();
                            if (result.success) {
                                Swal.fire(
                                    '삭제 완료',
                                    '파일이 삭제되었습니다.',
                                    'success'
                                );
                                el.parentElement.remove();
                            } else {
                                Swal.fire(
                                    '삭제 실패',
                                    '파일 삭제에 실패했습니다.',
                                    'error'
                                );
                            }
                        }
                    } catch (error) {
                        console.error("파일 삭제중 오류 발생:", error);
                        Swal.fire(
                            '오류 발생',
                            '파일 삭제 중 오류가 발생했습니다.',
                            'error'
                        );
                    }
                }
            });
        });

    // 중요 공지 체크박스 처리
    const checkbox = document.getElementById("noticeImportantCheckbox");
    const hiddenInput = document.getElementById("noticeImportantHidden");
    checkbox.checked = hiddenInput.value === "Y";
    checkbox.addEventListener("change", function () {
        hiddenInput.value = checkbox.checked ? "Y" : "N";
    });
});