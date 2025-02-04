const ApprovalEditor = {
    editor: null,

    initialize: function(contentFieldId, editorElementId) {  // editorElementId 매개변수 추가하기

        // 기존 에디터가 있다면 제거
        this.destroy();

        // 에디터 초기값
        const initialValue = '';

        // Toast UI Editor 초기화
        this.editor = new toastui.Editor({
            el: document.querySelector(`#${editorElementId}`),
            height: '400px',
            initialEditType: 'wysiwyg',
            initialValue: initialValue,
            previewStyle: 'vertical',
            hideModeSwitch: true  // 모드 전환 버튼 숨기기
        });

        // 에디터 내용 변경 시 hidden textarea에 반영
        this.editor.on('change', () => {
            document.getElementById(contentFieldId).value = this.editor.getHTML();
        });
    },

    // 에디터 인스턴스 제거 함수 추가
    destroy: function() {
        if (this.editor) {
            this.editor.destroy();
            this.editor = null;
        }
    },

    getContent: function() {
        return this.editor ? this.editor.getHTML() : '';
    },

    setContent: function(content) {
        if (this.editor) {
            this.editor.setHTML(content || '');
        }
    }
};