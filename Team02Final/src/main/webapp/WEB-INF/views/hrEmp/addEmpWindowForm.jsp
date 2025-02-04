<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>    
   
<security:authorize access="isAuthenticated()">
   <security:authentication property="principal" var="principal"/>
   <c:set value="${principal.account.empName }" var="empName"></c:set>                        
   <c:set value="${principal.account.base64EmpImg }" var="empImg"></c:set>                        
   <c:set value="${principal.account.companyId }" var="companyId"></c:set>                        
</security:authorize>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사원 등록</title>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/lib/bootstrap-5.0.2-dist/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/lib/sweetalert2.min.css">
<style>
/* 스타일 조정 */
.file-container {
    display: flex;
    align-items: center;
    gap: 5px; /* 첨부파일과 아이콘 사이 여백 */
    margin-left : 15px;
}

.file-container h6 {
    margin: 0;
    font-size: 0.9rem; /* 글씨 작게 */
}

.file-input {
    position: relative;
    display: inline-block;
    cursor: pointer;
}

.file-input input[type="file"] {
    position: absolute;
    top: 0;
    left: 0;
    opacity: 0; /* 파일 input 숨김 */
    width: 100%;
    height: 100%;
    cursor: pointer;
}

.btn-sm-custom {
    font-size: 0.85rem;
    padding: 5px 10px; /* 버튼 크기 조정 */
}

.button-container {
    display: flex;
    gap: 8px; /* 버튼 사이 여백 */
    justify-content: flex-end;
    margin-bottom: 10px; /* 버튼 아래 여백 */
/*     margin-right : 15px; */
}
</style>
</head>
<body>
<div class="d-flex justify-content-between align-items-center mb-2" style="margin-top:20px;">
    <!-- 첨부파일 영역 -->
    <div class="file-container d-flex align-items-center gap-2">
        <h6 class="mb-0">첨부파일</h6>
        <label class="file-input">
            <i class="fas fa-paperclip text-primary" style="font-size: 1.2rem;"></i>
            <input type="file" id="fileUpload" accept=".xls,.xlsx"/>
        </label>
        <span id="fileName" style="font-size: 0.85rem; color: #555;">파일을 선택하세요</span>
    </div>

    <!-- 버튼 영역 -->
    <div class="button-container d-flex gap-2" style="margin-top:5px;">

	

<form id="uploadForm" enctype="multipart/form-data" method="POST" style="margin: 0;">
		<button id="uploadBtn" class="btn btn-primary btn-sm-custom">
           	<i class="fas fa-upload"></i>첨부파일데이터업로드
        </button>
</form>
       
        <button  id="downloadBtn" class="btn btn-secondary btn-sm-custom">
            <i class="fas fa-download"></i> 사원등록양식 다운로드
        </button>
        <button id="addOneEmp" class="btn btn-info btn-sm-custom">
            <i class="fas fa-keyboard"></i> 사원개별등록
        </button>
        <button id="addAllEmp" class="btn btn-success btn-sm-custom">
            <i class="fas fa-file-excel"></i> 선택사원등록버튼
        </button>
    </div>
</div>
<hr/>
<div class="table-container">
    <table class="table table-bordered text-center">
        <thead class="table-light">
            <tr>
            	<th><input type="checkbox" class="rowAll"/></th>
                <th>사원명</th>
                <th>이메일</th>
                <th>입사일자</th>
                <th>직급</th>
                <th>부서명</th>
                <th>생년월일</th>
                <th>성별</th>
                <th>전화번호</th>
            </tr>
        </thead>
        <tbody id="innerEmpInfo">
           	
        </tbody>
    </table>
</div>


<div id="addEmpModal" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
      	<h4>사원 등록</h4>
      	
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div id="addEmpModalBody" class="modal-body table-responsive">
<form id="addEmpForm" method="POST">
      <table class="table">
      	<tr>
      		<td>사원명</td>
      		<td><input type="text" id="empName" name="empName" class="form-control" required/></td>
      	</tr>
      	<tr>
      		<td>이메일</td>
      		<td><input type="email" id="empMail" name="empMail" class="form-control" 
      		 placeholder="예)  test123@gmail.com" required pattern="[a-z0-9._%+\-]+@[a-z0-9.\-]+\.[a-z]{2,}$"/></td>
      	</tr>
      	<tr>
      		<td>입사일자</td>
      		<td><input type="date" id="empJoin" name="empJoin" class="form-control" required/></td>
      	</tr>
        <tr>
      		<td>직급</td>
      		<td>
      			<select id="selectPosi" class="form-select">
      				
      			</select>
      		
      		</td>
      	</tr>   
        <tr>
      		<td>부서</td>
      		<td>
      			<select id="selectDepart" class="form-select">
      				
      			</select>
      		</td>
      	</tr>   
        <tr>
      		<td>생년월일</td>
      		<td><input type="date" id="empBirth" name="empBirth" class="form-control" required/></td>
      	</tr>   
        <tr>
      		<td>성별</td>
      		<td>
      			<select id="selectGen" required class="form-select">
      				<option value="M" selected>남성</option>
      				<option value="F">여성</option>
      				
      			</select>
      		</td>
      	</tr>   
        <tr>
      		<td>전화번호</td>
      		<td><input type="tel" id="empPhone" name="empPhone" class="form-control" placeholder="예)  01012345678" required 
             pattern="^\d{10,11}$" 
        	 title="전화번호는 10~11자리 숫자여야 합니다."/></td>
      	</tr>   
      </table>	 
		  <div class="d-flex justify-content-end" style="gap:10px;">
        <button type="submit" id="addOneEmpBtn" class="btn btn-primary">등록</button>
        <button type="button" id="closeBtn" class="btn btn-danger" data-bs-dismiss="modal">취소</button>
    </div>
</form>		
      </div>
    </div>
  </div>
</div>


<script src="${pageContext.request.contextPath }/resources/lib/sweetalert2.min.js"></script>
<script src="${pageContext.request.contextPath }/resources/lib/bootstrap-5.0.2-dist/js/bootstrap.bundle.min.js"></script>
<script>const companyId = "${companyId}"
		const contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath }/resources/js/app/employee/hrAddEmployee.js"></script>


</body>
</html>
