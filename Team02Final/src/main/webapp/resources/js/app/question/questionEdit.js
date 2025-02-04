/**
 * 
 */

function gotoquDetail(quNo){
    window.location.href = `${contextPath}/${companyId}/question/${quNo}`;
}

document.addEventListener("DOMContentLoaded", () => {
    const editor = new toastui.Editor({
        el: document.querySelector('#editor'),
        height: '500px',          // 에디터 높이
        initialEditType: 'wysiwyg',
        initialValue: initialValue,
        previewStyle: 'vertical'
    });
    
    const checkbox = document.getElementById("questionSecretynCheckbox");
    const hiddenInput = document.getElementById("questionSecretynHidden");
 
    // 초기값 설정 (기존 상태 유지)
    checkbox.checked = hiddenInput.value === "Y";
     
         // 체크박스 상태 변경 이벤트
    checkbox.addEventListener("change", function () {
        hiddenInput.value = checkbox.checked ? "Y" : "N";
    });

// 기존 첨부파일 삭제 버튼 이벤트 바인딩
    document.querySelectorAll("[data-atch-file-id][data-file-sn]")
        .forEach(el => {
            el.addEventListener("click", async (e) => {
                e.preventDefault();

                const atchFileId = el.dataset.atchFileId;
                const fileSn = el.dataset.fileSn;

                try {
                    const resp = await fetch(`${contextPath}/${companyId}/question/${quNo}/atch/${atchFileId}/${fileSn}`, {
                        method: "DELETE",
                        headers: {
                            "Accept": "application/json"
                        }
                    });

                    if (resp.ok) {
                        const result = await resp.json();
                        if (result.success) {
                            //삭제된 파일 HTML요소 제거하기
                            el.parentElement.remove();
                        } else {
                            alert("파일 삭제에 실패해버렸다~~")
                        }
                    } else {
                        alert("서버문제인듯~")
                    }
                } catch (error){
                    console.error("파일 삭제중 오류 발생함 : ", error);
                    alert("파일 삭제중에 오류가 발생함");
                }
            })
        });

 
     const form = document.querySelector('form');
     form.addEventListener('submit', function (e) {
         e.preventDefault(); // 폼 제출 막기

         // 제목 체크
         const titleInput = document.querySelector('#quTitle');
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
         document.getElementById('quContent').value = contentHtml;
         form.submit();

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

});