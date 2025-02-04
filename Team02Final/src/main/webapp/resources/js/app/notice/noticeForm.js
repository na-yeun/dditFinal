document.addEventListener("DOMContentLoaded", function () {
	
	
    // 에디터 초기값 (지금은 아무것도 없는 빈 문자열)
    var initialValue = '';

    // Toast UI Editor 초기화
    const editor = new toastui.Editor({
        el: document.querySelector('#editor'),
        height: '500px',          // 에디터 높이
        initialEditType: 'wysiwyg',
        initialValue: initialValue,
        previewStyle: 'vertical'
    });

    //const form = document.querySelector('form');
    const noticeForm = document.querySelector('#notice-form');
    noticeForm.addEventListener('submit', function (e) {
        e.preventDefault(); // 일단 폼 제출을 막기

        // 에디터의 현재 HTML 내용을 얻음
        const titleInput = document.querySelector('#noticeName');
        const contentHtml = editor.getHTML();
		
        // 제목 체크
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
                title: '제목을 입력해주세요.'
            });
            titleInput.focus();
            return false;
        }

        // 내용 체크
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
                title: '내용을 입력해주세요.'
            });
            editor.focus();
            return false;
        }

        document.querySelector('#noticeContent').value = contentHtml;

        // 폼 제출
        noticeForm.submit();
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


//<%-- 에디터 post부분에서 불러오는과정 여기다가 넣어도되는건지는 검증안됨--%>
//<%-- 12월 10일 민경주 추가

// 폼 전송 시 에디터 내용을 숨긴 textarea(#noticeContent)에 넣고 submit

// document.querySelector('form')는 현재 DOM(문서)에서 가장 처음 나타나는
// <form> 엘리먼트 하나를 찾아 반환합니다. 즉, 해당 페이지에 여러 개의 <form> 태그가 있더라도,
// 이 코드는 첫 번째 <form> 요소만 선택하게 됩니다.

// 만약 특정한 폼을 선택하고 싶다면 그 폼에 id나 class를 부여한 뒤,
// document.querySelector('#myFormId') 또는
// document.querySelector('.myFormClass')와 같이 좀 더 구체적인 셀렉터를 사용해야 합니다.
//--%>

