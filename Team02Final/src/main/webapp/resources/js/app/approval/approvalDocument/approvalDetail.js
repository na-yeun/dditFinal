// 초기화
document.addEventListener('DOMContentLoaded', () =>
    ApprovalDetail.init());

const ApprovalDetail = {
    init() {
        // 초기 설정
        this.initVariables();
        this.EventListeners();
    },

    // 공통으로 사용할 변수들 초기화
    // 여태까지 jsp에서했지만 이번에는 js에서 세팅하고 초기화해보기
    initVariables() {
        this.contextPath = document.querySelector('#contextPath')?.value || '';
        this.companyId = document.querySelector('#companyId')?.value || '';
        this.docId = document.querySelector('#docId')?.value;
        this.docStatus = document.querySelector('#docStatus')?.value;
    },

    // 이벤트 리스너 등록해놓기
    EventListeners() {
        //전결 버튼 누를때
        document.getElementById('finalApproveBtn')?.addEventListener('click', () => this.finalApproveDocument());
        //삭제 버튼 누를때
        document.getElementById('deleteBtn')?.addEventListener('click', () => this.deleteDocument());
        //승인 버튼 누를때
        document.getElementById('approveBtn')?.addEventListener('click', () => this.approveDocument());
        //반려 버튼 누를때
        document.getElementById('rejectBtn')?.addEventListener('click', () => this.rejectDocument());
        //의견 작성 버튼 누를때
        document.getElementById('commentBtn')?.addEventListener('click', () => this.openCommentModal());
        //의견 저장 버튼 누를때
        document.getElementById('saveCommentBtn')?.addEventListener('click', () => this.saveComment());
        //목록 버튼 누를때
        document.getElementById('listBtn')?.addEventListener('click', () => this.goToList());
    },

    /**
     * fetchAPI란 반복되는 코드들이 있을 때 한 곳에서 관리할때 사용한다
     * 깔끔하게 사용하기 목적
     * @param url
     * @param method
     * @param data
     * @returns {Promise<string>}
     */
    // API 호출 공통 메소드 << 얘는 진짜 템플릿이다 케이스대로들어가서 실행해주는역할
    async fetchAPI(url, method, data) {
        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: data ? JSON.stringify(data) : null // 바디가 있으면 json문자열화, 없으면 넓값으로 세팅
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || '처리 중 오류가 발생했습니다.');
            }

            return await response.text();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    },

    // 문서 삭제
    async deleteDocument() {
        const { isConfirmed } = await Swal.fire({
            icon: 'warning',
            title: '문서 삭제',
            text: '문서를 삭제하시겠습니까?',
            showCancelButton: true,
            confirmButtonText: '삭제',
            cancelButtonText: '취소',
            confirmButtonColor: '#dc3545',
            reverseButtons: false
        });

        if (isConfirmed) {
            try {
                await this.fetchAPI(
                    `${this.contextPath}/${this.companyId}/approval/delete/${this.docId}`,
                    'DELETE'
                );

                await Swal.fire({
                    icon: 'success',
                    title: '삭제 완료',
                    text: '문서가 삭제되었습니다.'
                });

                this.goToList();
            } catch (error) {
                Swal.fire({
                    icon: 'error',
                    title: '오류 발생',
                    text: error.message
                });
            }
        }
    },

    // 결재 승인
    async approveDocument() {
        const comment = document.getElementById('approvalComment')?.value || '';

        if (comment && comment.length > 2000) {
            Swal.fire({
                icon: 'warning',
                title: '입력 오류',
                text: '의견은 2000자를 초과할 수 없습니다.'
            });
            return;
        }

        const result = await Swal.fire({
            title: '결재 승인',
            text: '문서를 승인하시겠습니까?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '승인',
            cancelButtonText: '취소'
        });

        if (result.isConfirmed) {
            try {
                const message = await this.fetchAPI(
                    `${this.contextPath}/${this.companyId}/approval/approve/${this.docId}`,
                    'POST',
                    { approvalComment: comment }
                );

                Swal.fire({
                    icon: 'success',
                    title: '승인 완료',
                    text: message || '승인되었습니다.'
                }).then(() => {
                    location.reload();
                });
            } catch (error) {
                Swal.fire({
                    icon: 'error',
                    title: '오류 발생',
                    text: error.message
                });
            }
        }
    },

    // 결재 반려
    async rejectDocument() {
        const comment = document.getElementById('approvalComment')?.value;

        if (!comment) {

            await Swal.fire({
                icon: 'warning',
                title: '입력 오류',
                text: '반려 시에는 의견 작성이 필수입니다.'
            });
            this.openCommentModal();
            return;
        }

        if (comment.length > 2000) {
            Swal.fire({
                icon: 'warning',
                title: '입력 오류',
                text: '의견은 2000자를 초과할 수 없습니다.'
            });
            return;
        }

        const result = await Swal.fire({
            title: '문서 반려',
            text: '문서를 반려하시겠습니까?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: '반려',
            cancelButtonText: '취소'
        });

        if (result.isConfirmed) {
            try {
                const message = await this.fetchAPI(
                    `${this.contextPath}/${this.companyId}/approval/reject/${this.docId}`,
                    'POST',
                    { approvalComment: comment }
                );

                Swal.fire({
                    icon: 'success',
                    title: '반려 완료',
                    text: message || '반려되었습니다.'
                }).then(() => {
                    location.reload();
                });
            } catch (error) {
                Swal.fire({
                    icon: 'error',
                    title: '오류 발생',
                    text: error.message
                });
            }
        }
    },

    // 전결 처리
    async finalApproveDocument() {
        const comment = document.getElementById('approvalComment')?.value || '';

        if (comment && comment.length > 2000) {
            Swal.fire({
                icon: 'warning',
                title: '입력 오류',
                text: '의견은 2000자를 초과할 수 없습니다.'
            });
            return;
        }

        const result = await Swal.fire({
            title: '전결 처리',
            text: '전결 처리하시겠습니까?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '전결',
            cancelButtonText: '취소'
        });

        if (result.isConfirmed) {
            try {
                const message = await this.fetchAPI(
                    `${this.contextPath}/${this.companyId}/approval/finalApprove/${this.docId}`,
                    'POST',
                    {
                        approvalComment: comment,
                        approvalYn: 'Y'
                    }
                );

                Swal.fire({
                    icon: 'success',
                    title: '전결 완료',
                    text: message || '전결 처리되었습니다.'
                }).then(() => {
                    location.reload();
                });
            } catch (error) {
                Swal.fire({
                    icon: 'error',
                    title: '오류 발생',
                    text: error.message
                });
            }
        }
    },

    // 모달 의견창 열기
    openCommentModal() {
        const commentModal = new bootstrap.Modal(document.getElementById('commentModal'));
        const textarea = document.getElementById('approvalComment');
        const lengthDisplay = document.getElementById('commentLength');

        // 글자수 표시 이벤트표시
        textarea.addEventListener('input', function() {
            lengthDisplay.textContent = this.value.length;
        });

        commentModal.show();
    },

    // 모달 의견 저장하기
    saveComment() {
        const comment = document.getElementById('approvalComment').value;
        if (!comment) {
            Swal.fire({
                icon: 'warning',
                title: '입력 오류',
                text: '의견을 입력해주세요.'
            });
            return;
        }

        if (comment.length > 2000) {
            Swal.fire({
                icon: 'warning',
                title: '입력 오류',
                text: '의견은 2000자를 초과할 수 없습니다.'
            });
            return;
        }
        const commentModal = bootstrap.Modal.getInstance(document.getElementById('commentModal'));
        commentModal.hide();
    },

    // 목록으로 이동하기 메소드
    goToList() {
        const urlParams = new URLSearchParams(window.location.search);
        const returnPage = urlParams.get('returnPage');

        if (returnPage === 'vacation') {
            location.href = `${this.contextPath}/${this.companyId}/vacation/history`;
        } else {
            location.href = `${this.contextPath}/${this.companyId}/approval/list`;
        }
    }
};

