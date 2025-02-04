<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>설정 확인 페이지</title>
<link href="${pageContext.request.contextPath }/resources/lib/sweetalert2.min.css" rel="stylesheet"> 
<link href="${pageContext.request.contextPath }/resources/sneat-1.0.0/assets/vendor/css/core.css" rel="stylesheet"/>
<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/sneat-1.0.0/assets/vendor/css/theme-default.css" />
<style>
        body {
            background-color: #f8f9fa; /* 부드러운 회색 배경 */
            font-family: Arial, sans-serif;
        }
        .container {
            background-color: #ffffff;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            padding: 30px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            max-width: 600px;
            margin: 50px auto;
        }
        h2 {
            font-size: 24px;
            font-weight: bold;
            color: #343a40;
        }
        .form-label {
            font-size: 16px;
            font-weight: bold;
            color: #495057;
        }
           ul {
       padding-left: 0; /* 기본 들여쓰기 제거 */
       list-style-type: none; /* 리스트 불릿 제거 */
       display: flex; /* 플렉스 박스 활성화 */
       flex-wrap: wrap; /* 줄바꿈 허용 */
       gap: 15px; /* 항목 사이 간격 */
  		}
	    ul li {
	        font-size: 14px;
	        color: #6c757d;
	        background-color: #f8f9fa; /* 부드러운 배경 */
	        padding: 5px 10px;
	        border-radius: 4px;
	        border: 1px solid #dee2e6; /* 가벼운 테두리 */
	    }
        p {
            font-size: 14px;
            color: #6c757d;
            margin: 0;
        }
        .text-end {
            margin-top: 20px;
        }
        .btn {
            font-size: 14px;
            padding: 8px 16px;
            border-radius: 4px;
        }
        .btn-primary {
            background-color: #6366f1;
            border-color: #6366f1;
            color: #ffffff;
        }
        .btn-primary:hover {
            background-color: #4f46e5;
            border-color: #4f46e5;
        }
        .btn-danger {
            background-color: #e63946;
            border-color: #e63946;
            color: #ffffff;
        }
        .btn-danger:hover {
            background-color: #d62828;
            border-color: #d62828;
        }
    </style>
</head>
<body>
    <div class="container mt-5">
        <h2 class="mb-4 text-center">설정 확인</h2>
        <form id="preSetupPageForm" method="POST" action="${pageContext.request.contextPath}/setupPage/firstSave">
            <input type="hidden" name="contractId" value="${fSetting.contractId}"/>
            <input type="hidden" name="firstPosition" value="${fSetting.firstPosition}"/>
            <input type="hidden" name="firstDepart" value="${fSetting.firstDepart}"/>
            <input type="hidden" name="firstEmploy" value="${fSetting.firstEmploy}"/>
            <input type="hidden" name="firstAttend" value="${fSetting.firstAttend}"/>
            <input type="hidden" name="firstElec" value="${fSetting.firstElec}"/>
            <input type="hidden" name="useElec" value="${fSetting.useElec}"/>
            <input type="hidden" name="contractCompany" value="${contractCompany }"/>
            
            <!-- 직급 설정 -->
            <div class="mb-4">
                <label class="form-label">직급 설정</label>
                <ul>
                    <c:forEach var="position" items="${fSetting.firstPosition}">
                        <li>${position}</li>
                    </c:forEach>
                </ul>
            </div>
            
            <!-- 부서 설정 -->
            <div class="mb-4">
                <label class="form-label">부서 설정</label>
                <ul>
                    <c:forEach var="depart" items="${fSetting.firstDepart}">
                        <li>${depart}</li>
                    </c:forEach>
                </ul>
            </div>

            <!-- 휴가 일수 -->
            <div class="mb-4">
                <label class="form-label">휴가 일수</label>
                <p>${fSetting.firstEmploy}일</p>
            </div>

            <!-- 출퇴근 시간 -->
            <div class="mb-4">
                <label class="form-label">출퇴근 시간</label>
                <c:if test="${not empty fSetting.firstAttend}">
                    <p>
                        출근: ${fn:split(fSetting.firstAttend, '-')[0]}, 
                        퇴근: ${fn:split(fSetting.firstAttend, '-')[1]}
                    </p>
                </c:if>
            </div>

            <!-- 전자결재 사용 여부 -->
            <div class="mb-4">
                <label class="form-label">전자결재 사용 여부</label>
                <p>${fSetting.useElec == 'Y' ? '사용함' : '사용 안 함'}</p>
            </div>

            <!-- 전자결재 종류 -->
            <div class="mb-4" style="${fSetting.useElec == 'Y' ? '' : 'display:none;'}">
                <label class="form-label">전자결재 종류</label>
                <ul>
                    <c:forEach var="elec" items="${fSetting.firstElec}">
                        <li>${elec}</li>
                    </c:forEach>
                </ul>
            </div>
            
            <!-- 확인 및 저장 버튼 -->
            <div class="text-end">
                <button type="submit" class="btn btn-primary">최종 신청</button>
                <button type="button" class="btn btn-danger" onclick="goBackToSetupPage()">취소</button>
            </div>
        </form>
    </div>

<script>
// 현재 오류 있음.
function goBackToSetupPage() {
    fetch('${pageContext.request.contextPath}/setupPage/gobackSetup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
            contractId: '${contractId}',
            contractCompany: '${contractCompany}'
        })
        	
        
    })
    .then(response => response.json()) // JSON 응답 처리
    .then(data => {
        if (data.redirectUrl) {
            window.location.href = data.redirectUrl; // 리다이렉트 수행
        }
    })
    .catch(error => console.error('Error:', error));
}


// function submitForm(){
// 	const formData = new FormData(document.getElementById("preSetupPageForm"));
//     fetch('${pageContext.request.contextPath}/setupPage/firstSave', {
//         method: 'POST',
//         body: formData
//     })
//     .then(response => response.json())
//     .then(data => {
//         if (data.status === 'success') {
//             Swal.fire({
            	
//             })
//         } else {
//             alert('저장 실패: ' + data.message);
//         }
//     });
// }


</script>
<script src="${pageContext.request.contextPath}/resources/lib/sweetalert2.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    
</body>
</html>
