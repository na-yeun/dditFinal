<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>


<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>투표 및 설문조사</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
       <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
        }

        .survey-container {
            max-width: 800px;
            margin: 2rem auto;
            background: white;
            padding: 2rem;
            border: 1px solid #ddd;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        .survey-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1rem;
            border: 1px solid #ddd;
            border-radius: 5px;
            margin-bottom: 1rem;
            background-color: #f7f7f7;
        }

        .survey-item.running {
            background-color: #d4edda;
        }

        .survey-item.ended {
            background-color: #f8d7da;
        }

        .survey-actions a {
            margin-left: 0.5rem;
            text-decoration: none;
        }

        .status-label {
            padding: 0.3rem 0.5rem;
            border-radius: 5px;
            font-size: 0.9rem;
            color: white;
        }

        .status-label.running {
            background-color: #28a745;
        }

        .status-label.ended {
            background-color: #dc3545;
        }
    </style>
</head>

<body>
    <header class="bg-primary text-white text-center py-3">
        <h1>투표 및 설문조사</h1>
    </header>

    <main class="survey-container">
        <div class="survey-item running">
            <div>
                <span class="status-label running">진행중</span>
                <strong>2024년 워크샵 개최 장소 선정</strong>
            </div>
            <div class="survey-actions">
                <a href="#" class="btn btn-sm btn-primary">참여하기</a>
                <a href="#" class="btn btn-sm btn-secondary">결과 보기</a>
            </div>
        </div>

        <div class="survey-item ended">
            <div>
                <span class="status-label ended">종료</span>
                <strong>2024년 직원 복지 제도 개선 설문</strong>
            </div>
            <div class="survey-actions">
                <a href="#" class="btn btn-sm btn-secondary">결과 보기</a>
            </div>
        </div>

        <div class="survey-item running">
            <div>
                <span class="status-label running">진행중</span>
                <strong>업무용 노트북 구매 의견 조사</strong>
            </div>
            <div class="survey-actions">
                <a href="#" class="btn btn-sm btn-primary">참여하기</a>
                <a href="#" class="btn btn-sm btn-secondary">결과 보기</a>
            </div>
        </div>
    </main>

    <footer class="bg-dark text-white text-center py-3">
        <p>© 2024 투표 및 설문조사 시스템. 모든 권리 보유.</p>
    </footer>
</body>

</html>