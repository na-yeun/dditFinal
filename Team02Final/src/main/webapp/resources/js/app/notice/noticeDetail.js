document.addEventListener('DOMContentLoaded', () => {

    // 클래스명으로 삭제 폼 선택하기
    const deleteForm = document.querySelector('.delete-form');
    if (deleteForm) {
        deleteForm.addEventListener('submit', async (e) => {
            e.preventDefault(); // 기본 동작 멈춰

            Swal.fire({
                title: '삭제 확인',
                text: "정말 삭제하시겠습니까?",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: '삭제',
                cancelButtonText: '취소'
            }).then((result) => {
                if (result.isConfirmed) {
                    const formData = new FormData(deleteForm);
                    fetch(deleteForm.action, {
                        method: 'POST',
                        body: formData
                    })
                        .then(response => {
                            if (response.ok) {
                                Swal.fire(
                                    '삭제 완료',
                                    '공지사항이 삭제되었습니다.',
                                    'success'
                                ).then(() => {
                                    window.location.href = `${contextPath}/${companyId}/notice`;
                                });
                            } else {
                                throw new Error('삭제 실패');
                            }
                        })
                        .catch(error => {
                            Swal.fire(
                                '오류',
                                '서버와의 통신 중 문제가 발생했습니다.',
                                'error'
                            );
                        });
                }
            });
        });
    }
});