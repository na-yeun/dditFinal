document.addEventListener("DOMContentLoaded", () => {
    // 저장 버튼과 저장할 영역 셀렉터
    const savePdfBtn = document.getElementById('save-pdf-btn');
    const saveContainer = document.getElementById('pdfContent');

    // 임시 CSS 적용 함수
    const applyTemporaryStyles = (element) => {
        element.dataset.originalStyle = element.getAttribute('style') || ''; // 기존 스타일 저장
        element.style.width = '210mm'; // A4 가로 크기
        element.style.padding = '10mm'; // 여백
        element.style.backgroundColor = 'white'; // 배경색
        element.style.border = '1px solid black'; // 테두리
        element.style.boxSizing = 'border-box'; // 패딩 포함 크기 계산
    };

    // 원래 스타일 복구 함수
    const restoreOriginalStyles = (element) => {
        element.setAttribute('style', element.dataset.originalStyle || ''); // 기존 스타일 복구
        delete element.dataset.originalStyle; // 임시 저장된 스타일 삭제
    };

    // 버튼 클릭 이벤트
    savePdfBtn.addEventListener('click', () => {
        // PDF 저장을 위해 임시 스타일 적용
        applyTemporaryStyles(saveContainer);

        html2canvas(saveContainer, {
            scale: 2, // 해상도 증가
            useCORS: true // CORS 문제 해결
        }).then(function (canvas) {
            // 캔버스 이미지 데이터를 가져옵니다
            var imgData = canvas.toDataURL('image/png');

            // PDF 기본 설정
            var pdfWidth = 210; // A4 가로 크기 (mm)
            var pdfHeight = 297; // A4 세로 크기 (mm)
            var imgWidth = pdfWidth - 20; // 양쪽 마진(10mm씩)을 뺀 너비
            var imgHeight = canvas.height * imgWidth / canvas.width; // 비율에 맞춘 높이
            var position = 10; // PDF의 상단 마진 위치 (10mm)

            // jsPDF 생성
            var doc = new jsPDF('p', 'mm', 'a4'); // 'p': 세로 방향

            // 이미지가 A4 한 페이지에 맞으면 삽입
            if (imgHeight <= pdfHeight - 20) {
                doc.addImage(imgData, 'PNG', 10, position, imgWidth, imgHeight);
            } else {
                // 이미지가 A4 한 페이지를 넘어갈 경우 페이지를 나눠서 삽입
                var heightLeft = imgHeight;
                var currentPosition = position;

                // 첫 페이지에 이미지 추가
                doc.addImage(imgData, 'PNG', 10, currentPosition, imgWidth, imgHeight);
                heightLeft -= pdfHeight - 20;

                // 추가 페이지 처리
                while (heightLeft > 0) {
                    currentPosition = heightLeft - imgHeight;
                    doc.addPage();
                    doc.addImage(imgData, 'PNG', 10, currentPosition, imgWidth, imgHeight);
                    heightLeft -= pdfHeight - 20;
                }
            }

            // PDF 저장
            doc.save('문서.pdf');

            // PDF 저장 후 원래 스타일 복구
            restoreOriginalStyles(saveContainer);
        }).catch(() => {
            // 에러 발생 시 원래 스타일 복구
            restoreOriginalStyles(saveContainer);
        });
    });
});
