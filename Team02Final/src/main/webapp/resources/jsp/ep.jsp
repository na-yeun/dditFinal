<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>전자결재 체험</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f4f4f9;
            font-family: Arial, sans-serif;
        }

        .document-container {
            background: white;
            padding: 2rem;
            margin: 2rem auto;
            border: 1px solid #ddd;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            max-width: 900px;
        }

        .document-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
        }

        .document-title {
            font-size: 1.8rem;
            font-weight: bold;
        }

        .approval-box {
            display: flex;
            justify-content: flex-start;
            align-items: center;
            gap: 1rem;
        }

        .approval-box div {
            width: 100px;
            height: 100px;
            display: flex;
            justify-content: center;
            align-items: center;
            border: 1px solid #000;
            text-align: center;
        }

        .document-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 2rem;
        }

        .document-table th,
        .document-table td {
            border: 1px solid #ccc;
            padding: 8px 12px;
            text-align: left;
        }

        .document-table th {
            background-color: #f7f7f7;
        }

        .document-footer {
            text-align: center;
            margin-top: 1.5rem;
        }

        .btn {
            min-width: 100px;
        }

        .leave-summary {
            max-width: 900px;
            margin: 1rem auto;
            background: white;
            padding: 1rem;
            border: 1px solid #ddd;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        .leave-summary table {
            width: 100%;
            border-collapse: collapse;
        }

        .leave-summary th,
        .leave-summary td {
            border: 1px solid #ccc;
            padding: 8px 12px;
            text-align: center;
        }

        .leave-summary th {
            background-color: #f7f7f7;
        }
    </style>
</head>

<body>
    <header class="bg-primary text-white p-3 text-center">
        <h1>전자결재 체험</h1>
        <p>간단한 전자결재 프로세스를 체험해보세요!</p>
    </header>

    <main class="container my-5">
        <!-- 휴가 현황 -->
        <section class="leave-summary">
            <h2 class="text-center">휴가 현황</h2>
            <table>
                <tr>
                    <th>보유 휴가</th>
                    <th>사용 휴가</th>
                    <th>포상 휴가</th>
                </tr>
                <tr>
                    <td>10일</td>
                    <td>5일</td>
                    <td>2일</td>
                </tr>
            </table>
        </section>

        <!-- 결재 문서 상세 보기 -->
        <section id="doc-details">
            <div class="document-container">
                <div class="document-header">
                    <div class="document-title" id="doc-title">연차 신청서</div>
                    <div class="approval-box">
                        <div>부서장</div>
                        <div>이사</div>
                        <div>대표</div>
                    </div>
                </div>
                <table class="document-table">
                    <tr>
                        <th>부서</th>
                        <td id="doc-department">영업팀</td>
                        <th>직급</th>
                        <td id="doc-position">대리</td>
                    </tr>
                    <tr>
                        <th>성명</th>
                        <td id="doc-author">홍길동</td>
                        <th>유형</th>
                        <td id="doc-type">연차</td>
                    </tr>
                    <tr>
                        <th>기간</th>
                        <td colspan="3" id="doc-period">2024-01-01 ~ 2024-01-05 (5일)</td>
                    </tr>
                    <tr>
                        <th>세부 사항</th>
                        <td colspan="3" id="doc-details">개인적인 사유로 연차를 신청합니다.</td>
                    </tr>
                </table>
                <p class="text-center">
                    위와 같이 연차를 신청하오니 허락하여 주시기 바랍니다.<br>
                    <strong>2024년 01월 01일</strong><br>
                    <strong>신청자:</strong> 홍길동 (서명)
                </p>
                <div class="document-footer">
                    <button class="btn btn-success" onclick="approveDocument()">승인</button>
                    <button class="btn btn-danger" onclick="rejectDocument()">반려</button>
                    <button class="btn btn-secondary" onclick="backToList()">목록으로 돌아가기</button>
                </div>
            </div>
        </section>
    </main>

    <footer class="bg-dark text-white text-center p-3">
        <p>© 2024 work2gether 전자결재 체험. 모든 권리 보유.</p>
    </footer>

    <script>
        // 문서 데이터 (더미 데이터)
        const documents = [
            { id: 1, title: "2024년 연차 계획", content: "2024년 연차 계획 문서입니다.", author: "김철수", status: "대기" },
            { id: 2, title: "프로젝트 예산 승인", content: "프로젝트 예산 승인 문서입니다.", author: "이영희", status: "승인" },
            { id: 3, title: "휴가 신청", content: "휴가 신청 문서입니다.", author: "박민수", status: "반려" }
        ];

        // 문서 보기
        function viewDocument(id) {
            const doc = documents.find(d => d.id === id);
            if (doc) {
                document.getElementById("doc-title").innerText = doc.title;
                document.getElementById("doc-author").innerText = doc.author;
                document.getElementById("doc-status").innerText = doc.status;
            }
        }

        // 문서 승인
        function approveDocument() {
            const status = document.getElementById("doc-status");
            if (status.innerText === "승인") {
                
                return;
            }
            status.innerText = "승인";
           
        }

        // 문서 반려
        function rejectDocument() {
            const status = document.getElementById("doc-status");
            if (status.innerText === "반려") {
                
                return;
            }
            status.innerText = "반려";
           
        }

        // 목록으로 돌아가기
        function backToList() {
            
            location.href=`${pageContext.request.contextPath}/resources/jsp/groupwareDemo.jsp`;
            
        }
    </script>
</body>

</html>
