<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %> 
<!DOCTYPE html>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/survey/surveyDetail.css">
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<style>
	canvas{
		width:600px;
		height: auto;;
	}
</style>

<h4 id="page-title">결과조회</h4>
<hr/>

<security:authorize access="isAuthenticated()">
	<security:authentication property="principal" var="principal" />
	<c:set value="${principal.account.posiId}" var="posiId"></c:set>
	<c:set value="${principal.account.departCode}" var="departCode"></c:set>
</security:authorize>




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

<c:if test="${(detail.surboardTarget eq 'ALL') or (departCode eq detail.surboardTarget)}">
	<table class="table" data-sboard-no="${detail.sboardNo}" id="main-table">
		<tr>
			<td class="table-title">제목</td>
			<td colspan="4">${detail.surboardNm}</td>
			<td class="table-title">작성일</td>
			<td colspan="2">${detail.surboardWrite}</td>
		</tr>
			
		<!-- 종료상태 출력 부분 아래에 써야함 -->
		<c:if test="${detail.surboardYn eq 'N'}">
			<tr>
				<td class="table-title">설문시작일</td>
				<td>${detail.surboardStdate}</td>
				
				<td class="table-title">설문종료일</td>
				<td>${detail.surboardEnddate}</td>
				
				<td class="table-title">대상</td>
				<td>${detail.surboardTargetName}</td>
				<td class="table-title">진행여부</td>
				
				<td>
					<span class="badge rounded-pill bg-secondary">종료</span>
				</td>
			</tr>
			<tr>
				<td class="table-title">설명</td>
				<td colspan="7">${detail.surboardContent}</td>
			</tr>
			<tr>
				<td colspan="8">
					<div class="survey-question-item">
						<div class="survey-question-content">
		            		🗒️설문조사 참여도
		            	</div>
		            	<div class="multi-options-container">
		                	<div>
		                		<div>
									<canvas class="chart-area" id="participation"></canvas>
								</div>
			                </div>
						</div>
					</div>
				</td>
			</tr>
			
			<tr>
				<td colspan="8">
					<div class="survey-question-content">그래프 모양</div>
					<div>
						<button class="btn btn-outline-primary chart-type" type="button" id="doughnut">doughnut</button>
						<button class="btn btn-outline-primary chart-type" type="button" id="line">line</button>
						<button class="btn btn-outline-primary chart-type" type="button" id="pie">pie</button>
						<button class="btn btn-outline-primary chart-type" type="button" id="polarArea">polarArea</button>
					</div>
				</td>
			</tr>
			
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
				                	<div>
				                		<div>
				                			<canvas class="chart-area" id="mychart_${surveyQuestion.surquesNo}"></canvas>
				                		</div>
										
				                		
					                </div>
			                	</div>
			                </c:if>
			                <!-- 단일 선택 -->
			                <c:if test="${surveyQuestion.surquesDupleyn eq 'N'}">
			                	<div class="survey-question-content">
				            		${status.count}. ${surveyQuestion.surquesContent} <span class="multi-info">다중선택 불가능</span>
				            	</div>
			                	<div class="multi-options-container">
				                	<div>
				                		<div>
				                			<canvas class="chart-area" id="mychart_${surveyQuestion.surquesNo}"></canvas>
				                		</div>
					                </div>
			                	</div>
			                </c:if>
				            </c:if>
			            </div>
			            <c:if test="${surveyQuestion.surquesType eq 'S_SUBJ'}">
			                <!-- 주관식 -->
			                <div class="survey-question-content">
			            		${status.count}. ${surveyQuestion.surquesContent}
			            	</div>
			            	<c:if test="${(((posiId eq 6) or (posiId eq 7)) and (detail.surboardTarget eq 'ALL')) or (((posiId eq 3) or (posiId eq 4)) and (detail.surboardTarget eq departCode))}">
				                <div class="textarea-container" id="sub_${surveyQuestion.surquesNo}">
					                
				                </div>
			                </c:if>
			                <c:if test="${!(((posiId eq 6) or (posiId eq 7)) and (detail.surboardTarget eq 'ALL')) or (((posiId eq 3) or (posiId eq 4)) and (detail.surboardTarget eq departCode))}">
				                <div class="textarea-container">
					                서술형의 경우에는 관리자 혹은 게시글 작성자만 조회할 수 있습니다.
				                </div>
			                </c:if>
			            </c:if>
			        </td>
			    </tr>
			</c:forEach>
			<tr>
				<td colspan="8">
					<div class="btn-area">
						<!-- 관리자나 대표이면서 대상이 전체 이거나 팀장이나 부장이면서 대상이 해당 부서일 경우 -->
						<c:if test="${(((posiId eq 6) or (posiId eq 7)) and (detail.surboardTarget eq 'ALL')) or (((posiId eq 3) or (posiId eq 4)) and (detail.surboardTarget eq departCode))}">
							<button type="button" id="delete-btn" class="btn btn-danger">삭제</button>
						</c:if>
						<button type="button" id="before-btn" class="btn btn-secondary">목록</button>
					</div>
				</td>
			</tr>
		</c:if>	
	</table>
</c:if>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/app/survey/surveyEndDetail.js">
</script>