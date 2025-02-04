<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>work2gether 데모</title>
    <link href="${pageContext.request.contextPath }/resources/lib/sweetalert2.min.css" rel="stylesheet"> 
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath }/resources/css/groupwareDemo/groupwareDemo.css" rel="stylesheet">
</head>
<body>
    <header>
        <h1>work2gether 데모</h1>
        <p>그룹웨어의 주요 기능을 체험해보세요!</p>
    </header>

    <main class="container my-5">
        <!-- 주요 기능 섹션 -->
        <section id="features">
            <h2 class="text-center mb-4">주요 기능</h2>
            <div class="row g-4">
                <!-- 전자결재 -->
                <div class="col-md-4">
                    <div class="card text-center shadow">
                        <div class="card-body">
                            <h5 class="card-title">전자결재</h5>
                            <p class="card-text">문서 승인 및 결재를 간편하게 처리하세요.</p>
                            <button class="btn btn-primary" onclick="showEP()">체험하기</button>
                        </div>
                    </div>
                </div>

                <!-- 일정관리 -->
                <div class="col-md-4">
                    <div class="card text-center shadow">
                        <div class="card-body">
                            <h5 class="card-title">일정관리</h5>
                            <p class="card-text">모든 일정과 회의를 한눈에 확인하세요.</p>
                            <button class="btn btn-primary" onclick="showSchedule()">체험하기</button>
                        </div>
                    </div>
                </div>

                <!-- 메일 -->
                <div class="col-md-4">
                    <div class="card text-center shadow">
                        <div class="card-body">
                            <h5 class="card-title">메일</h5>
                            <p class="card-text">효율적인 이메일 관리로 소통을 개선하세요.</p>
                            <button class="btn btn-primary" onclick="showEmail()">체험하기</button>
                        </div>
                    </div>
                </div>

                <!-- 클라우드 시스템 -->
                <div class="col-md-4">
                    <div class="card text-center shadow">
                        <div class="card-body">
                            <h5 class="card-title">클라우드 시스템</h5>
                            <p class="card-text">안전한 파일 공유 및 저장 솔루션을 제공합니다.</p>
                            <button class="btn btn-primary" onclick="showCloud()">체험하기</button>
                        </div>
                    </div>
                </div>

                <!-- 투표 및 설문조사 -->
                <div class="col-md-4">
                    <div class="card text-center shadow">
                        <div class="card-body">
                            <h5 class="card-title">투표 및 설문조사</h5>
                            <p class="card-text">팀원 의견을 빠르게 수집하세요.</p>
                            <button class="btn btn-primary" onclick="showSurvey()">체험하기</button>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- 신청 유도 섹션 -->
        <section id="apply" class="mt-5 text-center">
            <h3>이 그룹웨어를 사용해보고 싶으신가요?</h3>
            <p>신청 양식 페이지로 이동하여 신청을 완료하세요!</p>
            <button class="btn btn-success" onclick="confirmApply()">신청 양식으로 이동</button>
        </section>
    </main>

    <footer>
        <p>© 2024 work2gether 데모. 모든 권리 보유.</p>
    </footer>

    <script>
        // 기능 상세 보기
        function showFeatureDetails(feature) {
            alert("${feature} 기능을 체험해보세요!");
        }
		
        function showEP(){
            location.href = "${pageContext.request.contextPath}/resources/jsp/ep.jsp"
            
        }
        function showEmail(){
        	location.href = "${pageContext.request.contextPath}/resources/jsp/email.jsp"
        }
        function showCloud(){
        	location.href = "${pageContext.request.contextPath}/resources/jsp/cloud.jsp"
        }
        function showSurvey(){
        	location.href = "${pageContext.request.contextPath}/resources/jsp/survey.jsp"
        }
        function showSchedule(){
        	location.href = "${pageContext.request.contextPath}/resources/jsp/schedule.jsp"
        }
        	
        
        // 신청 페이지로 이동 유도
        function confirmApply() {
        	Swal.fire({
        		title : "신청 양식 페이지로 이동하시겠습니까?"
        	  , icon : "question"
        	  , showCancelButton : true	
        	  , cancelButtonText : "취소"
        	  , confirmButtonText : "예"
        	}).then((result) => {
        		if(result.isConfirmed){
        		location.href = "${pageContext.request.contextPath}/contract/all/requestCont";
        		}
        	})
        }
        			
    </script>
<script src="${pageContext.request.contextPath}/resources/lib/sweetalert2.min.js"></script>
</body>

</html>
