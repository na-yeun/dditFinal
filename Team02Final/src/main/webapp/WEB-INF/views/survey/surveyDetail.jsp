<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %> 
<!DOCTYPE html>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/survey/surveyDetail.css">
<h4 id="page-title">설문참여</h4>
<hr/>

<c:if test="${isAble eq false}">
	<script>
		Swal.fire({
			title: "이미 참여한 설문조사",
			html: "이미 참여한 설문조사입니다. <br> 재참여가 불가능합니다.",
			icon: "error"
		});
		document.addEventListener("DOMContentLoaded", () => {
			const allInputsAndTextareas = document.querySelectorAll('input, textarea');
			allInputsAndTextareas.forEach((element)=>{
				element.disabled = true;
			});
			
			let sbmBtn = document.querySelector('#submit-btn');
			if(sbmBtn){
				sbmBtn.style.display = 'none';
			}
		})
	</script>
</c:if>
<c:if test="${isAble eq true}">
	<script>
		document.addEventListener("DOMContentLoaded", () => {
			const allInputsAndTextareas = document.querySelectorAll('input, textarea');
			allInputsAndTextareas.forEach((element)=>{
				element.disabled = false;
			});
			let sbmBtn = document.querySelector('#submit-btn');
			if(sbmBtn){
				sbmBtn.style.display = 'block';
				
			}
		})
	</script>
</c:if>
<security:authorize access="isAuthenticated()">
	<security:authentication property="principal" var="principal" />
	<c:set value="${principal.account.posiId}" var="posiId"></c:set>
	<c:set value="${principal.account.departCode}" var="departCode"></c:set>
	<c:set value="${principal.account.empId}" var="empId"></c:set>
</security:authorize>

<!-- 설문조사가 아직 진행중이 아니고 / 전체공개이고 관리자나 대표 계정이 아닐 때 / 부서 공개이고 부서장 계정이 아닐 때 아래 알림이 떠야함-->
<c:if test="${(not empty detail.startDateCheck) 
				and
			  ((detail.surboardTarget eq 'ALL') and (posiId != 6 and posiId !=7)
			    or
			   ((departCode eq detail.surboardTarget) and (posiId != 3 and posiId !=4))
			  )}">
	<script>
		Swal.fire({
			title: "진행 전 설문조사입니다.",
			html: "아직 설문조사가 시작되지 않았습니다.<br>설문조사 시작 후 참여해주세요.",
			icon: "error",
			confirmButtonText: '확인',
		}).then(result => {
			if (document.referrer && document.referrer !== window.location.href) {
               window.location.href = document.referrer;
            } else {
               // referrer가 없으면 기본 뒤로 가기
               window.history.back();
            }
		});
	</script>
</c:if>


<!-- 권한이 없는 설문조사일때 -->
<c:if test="${(detail.surboardTarget != 'ALL') and (departCode != detail.surboardTarget)}">
	<script>
		Swal.fire({
			title: "권한이 없습니다.",
			html: "해당 설문조사에 조회 및 참여 권한이 없습니다.",
			icon: "error",
			confirmButtonText: '확인',
		}).then(result => {
			if (document.referrer && document.referrer !== window.location.href) {
               window.location.href = document.referrer;
            } else {
               // referrer가 없으면 기본 뒤로 가기
               window.history.back();
            }
		});
	</script>
</c:if>



<!-- 대상이 전체이거나 대상이 나의 부서코드와 동일한 경우 볼 수 있음 -->
<c:if test="${((detail.surboardTarget eq 'ALL') or (departCode eq detail.surboardTarget))}">
	<!-- 진행상태가 진행전이고 내가 관리자일 경우 볼 수 있음-->
	<c:if test="${empty detail.startDateCheck 
					or 
				 (not empty detail.startDateCheck and ((detail.surboardTarget eq 'ALL') and (posiId eq 6 or posiId eq 7)))
				 	or
				 (not empty detail.startDateCheck and (departCode eq detail.surboardTarget) and (posiId eq 3 or posiId eq 4))
				 }">
		
		<form id="submit-form">
			<table class="table" data-board-no="${detail.sboardNo}" id="detail-table">
				<tr>
					<td class="table-title">제목</td>
					<td colspan="4">${detail.surboardNm}</td>
					<td class="table-title">작성일</td>
					<td colspan="2">${detail.surboardWrite}</td>
				</tr>
				
				<tr>
					<td class="table-title">설문시작일</td>
					<td>${detail.surboardStdate}</td>
					
					<td class="table-title">설문종료일</td>
					<td>${detail.surboardEnddate}</td>
					
					<td class="table-title">대상</td>
					<td>${detail.surboardTargetName}</td>
					<td class="table-title">진행여부</td>
					<td>
						<c:if test="${not empty detail.startDateCheck}">
							<span class="badge rounded-pill bg-warning" id="progress">진행전</span>
						</c:if>
							
						<c:if test="${empty detail.startDateCheck}">
							<c:if test="${not empty detail.endDateWarning}">
								<span class="badge rounded-pill bg-danger">진행</span>
							</c:if>
							<c:if test="${empty detail.endDateWarning}">
								<span class="badge rounded-pill bg-primary">진행</span>
							</c:if>
						</c:if>
					</td>
				</tr>
				<tr>
					<td class="table-title">설명</td>
					<td colspan="7">${detail.surboardContent}</td>
				</tr>
				<!-- 여기서 부터 질문 -->
				<c:forEach items="${detail.surveyQuestionList}" var="surveyQuestion" varStatus="status">
				    <tr>
				        <td colspan="8">
				            <!-- 질문의 선택항목 -->
				            <div class="survey-question-item">
				            	<c:if test="${surveyQuestion.surquesType eq 'S_MULTI'}">
				            		<!-- 다중선택 -->
				            	 	<c:if test="${surveyQuestion.surquesDupleyn eq 'Y'}">
					            	<div class="survey-question-content">
					            		${status.count}. ${surveyQuestion.surquesContent} <span class="multi-info">다중선택 가능</span>
					            	</div>
				                	<div class="multi-options-container">
					                	<c:forEach items="${surveyQuestion.surveyItemList}" var="item">
					                		<div>
								                <input type="checkbox" name="${item.surquesNo}" value="${item.suritemNo}" 
								                	class="styled-checkbox form-data input-select"/>
								                <br>
								                ${item.suritemContent}
							                </div>
					                	</c:forEach>
				                	</div>
				                </c:if>
				                <!-- 단일 선택 -->
				                <c:if test="${surveyQuestion.surquesDupleyn eq 'N'}">
				                	<div class="survey-question-content">
					            		${status.count}. ${surveyQuestion.surquesContent} <span class="multi-info">다중선택 불가능</span>
					            	</div>
				                	<div class="options-container">
					                	<c:forEach items="${surveyQuestion.surveyItemList}" var="item">
					                		<div>
							                	<input type="radio" name="${item.surquesNo}" value="${item.suritemNo}" class="input-select" required="required"/>
							                	<br>
							                	${item.suritemContent}
							                </div>	                		
					                	</c:forEach>
				                	</div>
				                </c:if>
					            </c:if>
				            </div>
				            <c:if test="${surveyQuestion.surquesType eq 'S_SUBJ'}">
				                <!-- 주관식 -->
				                <div class="survey-question-content">
				            		${status.count}. ${surveyQuestion.surquesContent}
				            	</div>
				                <div class="textarea-container">
				                	<textarea class="form-control" id="${surveyQuestion.surveyItemList[0].suritemNo}" name="${surveyQuestion.surquesNo}" placeholder="${surveyQuestion.surveyItemList[0].suritemContent}" rows="10"></textarea>
				                </div>
				            </c:if>
				        </td>
				    </tr>
				</c:forEach>
				<tr>
					<td colspan="8">
						<div class="btn-area">
							<!-- 관리자나 대표이면서 대상이 전체 이거나 팀장이나 부장이면서 대상이 해당 부서일 경우 -->
							<c:if test="${(((posiId eq 6) or (posiId eq 7)) and (detail.surboardTarget eq 'ALL')) or (((posiId eq 3) or (posiId eq 4)) and (detail.surboardTarget eq departCode))}">
								<!-- 아직 시작 전인 경우에만 수정이 가능함 -->
								<c:if test="${not empty detail.startDateCheck}">
									<button type="button" id="edit-btn" class="btn btn-warning">수정</button>
								</c:if>
								<button type="button" id="delete-btn" class="btn btn-danger">삭제</button>
							</c:if>
							<c:if test="${not ((posiId eq 6) or (posiId eq 7)) and (detail.surboardTarget eq 'ALL')}">
								<button type="submit" id="submit-btn" class="btn btn-primary">제출</button>
							</c:if>
							<button type="button" id="before-btn" class="btn btn-secondary">목록</button>
						</div>
					</td>
				</tr>
			</table>
		</form>
	</c:if>
</c:if>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/app/survey/surveyDetail.js"></script>