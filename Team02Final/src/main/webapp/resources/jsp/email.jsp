<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>메일 관리</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f4f4f9;
            font-family: Arial, sans-serif;
        }

        .email-container {
            background: white;
            padding: 2rem;
            margin: 2rem auto;
            border: 1px solid #ddd;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            max-width: 900px;
        }

        .email-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
        }

        .email-title {
            font-size: 1.8rem;
            font-weight: bold;
        }

        .email-list {
            margin-top: 2rem;
        }

        .email-table {
            width: 100%;
            border-collapse: collapse;
        }

        .email-table th,
        .email-table td {
            border: 1px solid #ccc;
            padding: 8px 12px;
            text-align: left;
        }

        .email-table th {
            background-color: #f7f7f7;
        }

        .email-details {
            display: none;
            margin-top: 2rem;
        }

        .btn {
            min-width: 100px;
        }
    </style>
</head>

<body>
    <header class="bg-primary text-white p-3 text-center">
        <h1>메일 관리</h1>
        <p>받은 메일을 관리하고 상세 내용을 확인하세요!</p>
    </header>

    <main class="container my-5">
        <!-- 메일 리스트 -->
        <section id="email-list" class="email-container">
            <div class="email-header">
                <div class="email-title">메일함</div>
            </div>
            <table class="table table-striped email-table">
                <thead>
                    <tr>
                        <th>번호</th>
                        <th>제목</th>
                        <th>보낸 사람</th>
                        <th>받은 날짜</th>
                        <th>액션</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>1</td>
                        <td>프로젝트 보고서</td>
                        <td>이영희</td>
                        <td>2024-12-24</td>
                        <td><button class="btn btn-primary btn-sm" onclick="viewEmail(1)">보기</button></td>
                    </tr>
                    <tr>
                        <td>2</td>
                        <td>팀 회의 일정</td>
                        <td>박민수</td>
                        <td>2024-12-23</td>
                        <td><button class="btn btn-primary btn-sm" onclick="viewEmail(2)">보기</button></td>
                    </tr>
                    <tr>
                        <td>3</td>
                        <td>연말 정산 안내</td>
                        <td>김철수</td>
                        <td>2024-12-22</td>
                        <td><button class="btn btn-primary btn-sm" onclick="viewEmail(3)">보기</button></td>
                    </tr>
                </tbody>
            </table>
        </section>

        <!-- 메일 상세 보기 -->
        <section id="email-details" class="email-container">
            <div class="email-header">
                <div class="email-title" id="email-subject">메일 제목</div>
                <button class="btn btn-secondary" onclick="backToList()">목록으로 돌아가기</button>
            </div>
            <p><strong>보낸 사람:</strong> <span id="email-sender">홍길동</span></p>
            <p><strong>받은 날짜:</strong> <span id="email-date">2024-12-24</span></p>
            <div id="email-content">
                메일 내용이 여기에 표시됩니다.
            </div>
        </section>
    </main>

    <footer class="bg-dark text-white text-center p-3">
        <p>© 2024 work2gether 메일 관리. 모든 권리 보유.</p>
    </footer>

    <script>
        const emails = [
            {
                id: 1,
                subject: "프로젝트 보고서",
                sender: "이영희",
                date: "2024-12-24",
                content: "프로젝트 보고서를 첨부하였습니다. 확인 부탁드립니다."
            },
            {
                id: 2,
                subject: "팀 회의 일정",
                sender: "박민수",
                date: "2024-12-23",
                content: "다음 주 팀 회의 일정은 수요일 오전 10시입니다."
            },
            {
                id: 3,
                subject: "연말 정산 안내",
                sender: "김철수",
                date: "2024-12-22",
                content: "연말 정산 관련 서류를 준비해 주세요."
            }
        ];

        function viewEmail(id) {
            const email = emails.find(e => e.id === id);
            if (email) {
                document.getElementById("email-subject").innerText = email.subject;
                document.getElementById("email-sender").innerText = email.sender;
                document.getElementById("email-date").innerText = email.date;
                document.getElementById("email-content").innerText = email.content;

                document.getElementById("email-list").style.display = "none";
                document.getElementById("email-details").style.display = "block";
            }
        }

        function backToList() {
            document.getElementById("email-details").style.display = "none";
            document.getElementById("email-list").style.display = "block";
        }
    </script>
</body>

</html>
