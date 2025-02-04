/**
 * 
 */
document.addEventListener("DOMContentLoaded", function () {
    // 에디터 초기값 (지금은 아무것도 없는 빈 문자열)
    var initialValue = '';
    const goListBtn = document.getElementById("goListBtn");

    goListBtn.addEventListener("click", (e)=> {
        location.href = `${contextPath}/${companyId}/question`;
    });

    // Toast UI Editor 초기화
    const editor = new toastui.Editor({
        el: document.querySelector('#editor'),
        height: '500px',          // 에디터 높이
        initialEditType: 'wysiwyg',
        initialValue: initialValue,
        previewStyle: 'vertical'
    });

    const form = document.querySelector('#question-form');
    form.addEventListener('submit', function (e) {
        // 에디터의 현재 HTML 내용을 얻음
        var contentHtml = editor.getHTML();
        document.getElementById('quContent').value = contentHtml;
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


    const dataInputBtn = document.getElementById("dataInputBtn");

    dataInputBtn.addEventListener("click",()=>{

        const title = "기능 문의드립니다.";
        const categoryId = "3";
        const checkbox = true;
    
        editor.setHTML("<p>채팅 기능이 추가되었으면 좋겠습니다 !</p>");
        // 입력 필드에 값 설정
        document.querySelector('input[name="quTitle"]').value = title;
        document.querySelector('select[name="goryId"]').value = categoryId;
    
        // 비공개 여부 체크박스 설정
        const checkboxInput = document.getElementById("questionSecretynCheckbox");
        const hiddenInput = document.getElementById("questionSecretynHidden");
        checkboxInput.checked = checkbox;
        hiddenInput.value = checkbox ? "Y" : "N";
    
    });

});




