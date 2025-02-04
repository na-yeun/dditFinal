<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>일정 관리</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.8/index.global.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f4f4f9;
            font-family: Arial, sans-serif;
        }

        .calendar-container {
            max-width: 900px;
            margin: 2rem auto;
            background: white;
            padding: 1.5rem;
            border: 1px solid #ddd;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        .modal-header {
            background-color: #007bff;
            color: white;
        }
    </style>
</head>

<body>
    <header class="bg-primary text-white p-3 text-center">
        <h1>일정 관리</h1>
        <p>캘린더에서 일정을 관리하고 추가, 삭제, 드래그 앤 드롭하세요!</p>
    </header>

    <main class="calendar-container">
        <div id="calendar"></div>
    </main>

    <!-- 일정 추가 모달 -->
    <div class="modal fade" id="eventModal" tabindex="-1" aria-labelledby="eventModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="eventModalLabel">일정 추가</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="eventForm">
                        <div class="mb-3">
                            <label for="eventTitle" class="form-label">일정 제목</label>
                            <input type="text" class="form-control" id="eventTitle" required>
                        </div>
                        <div class="mb-3">
                            <label for="eventStart" class="form-label">시작 시간</label>
                            <input type="datetime-local" class="form-control" id="eventStart" required>
                        </div>
                        <div class="mb-3">
                            <label for="eventEnd" class="form-label">종료 시간</label>
                            <input type="datetime-local" class="form-control" id="eventEnd">
                        </div>
                        <button type="submit" class="btn btn-primary">추가</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <footer class="bg-dark text-white text-center p-3">
        <p>© 2024 일정 관리 시스템. 모든 권리 보유.</p>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.8/index.global.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const calendarEl = document.getElementById('calendar');
            const calendar = new FullCalendar.Calendar(calendarEl, {
                initialView: 'dayGridMonth',
                editable: true,
                selectable: true,
                events: [
                    {
                        title: '팀 회의',
                        start: '2024-12-30T10:00:00',
                        end: '2024-12-30T12:00:00'
                    }
                ],
                select: function (info) {
                    const modal = new bootstrap.Modal(document.getElementById('eventModal'));
                    document.getElementById('eventStart').value = info.startStr;
                    document.getElementById('eventEnd').value = info.endStr;
                    modal.show();
                },
                eventDrop: function (info) {
                    alert(`일정이 변경되었습니다: ${info.event.title}`);
                },
                eventClick: function (info) {
                    if (confirm(`${info.event.title} 일정을 삭제하시겠습니까?`)) {
                        info.event.remove();
                    }
                }
            });

            calendar.render();

            document.getElementById('eventForm').addEventListener('submit', function (e) {
                e.preventDefault();
                const title = document.getElementById('eventTitle').value;
                const start = document.getElementById('eventStart').value;
                const end = document.getElementById('eventEnd').value;
                calendar.addEvent({
                    title,
                    start,
                    end
                });
                const modal = bootstrap.Modal.getInstance(document.getElementById('eventModal'));
                modal.hide();
                e.target.reset();
            });
        });
    </script>
</body>
</html>