<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    
 
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>서비스 이용 신청 완료 </title>
<style>
body {
    font-family: Arial, sans-serif;
    background-color: #f8f9fa;
    margin: 0;
    padding: 0;
    display: flex; /* Flexbox 활성화 */
    justify-content: center; /* 가로 중앙 정렬 */
    align-items: center; /* 세로 중앙 정렬 */
    height: 100vh; /* 화면 전체 높이 */
}

.container {
	width : 500px;
    max-width: 800px;
    background: #ffffff;
    padding: 20px 30px;
    border-radius: 8px;
    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
    text-align: center;
}

    h3 {
        color: #343a40;
        margin-bottom: 20px;
    }
    p {
        color: #6c757d;
        font-size: 16px;
        line-height: 1.6;
    }
    .btn {
        display: inline-block;
        margin: 20px 10px;
        padding: 10px 20px;
        font-size: 14px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        text-decoration: none;
    }
    .btn-primary {
        background-color: #6366f1;
        color: #ffffff;
    }
    .btn-primary:hover {
        background-color: #4f46e5;
    }
    .btn-secondary {
        background-color: #e9ecef;
        color: #343a40;
    }
    .btn-secondary:hover {
        background-color: #ced4da;
    }
</style>

</head>
<body>
<div class="container">
	<c:set value="${contract }" var="cont"></c:set>
    <h3>${cont.contractCompany } 업체의 서비스이용 신청 완료</h3>
    <br>
    <p>서비스 이용 신청이 성공적으로 완료 되었습니다.</p>
    <p>승인 결과는 해당 이메일로 전송됩니다. 감사합니다.</p>
    <div>
    	<!--  홈은 데모페이지 컨트롤러 연결 후 링크 걸어줄 것.  -->
        <a href="${pageContext.request.contextPath}/contract/all/groupWareDemo" class="btn btn-primary">홈으로 이동</a> 
        
    </div>
</div>
</body>
</html>