<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <title>초기 세팅 페이지</title>
            <link href="${pageContext.request.contextPath }/resources/sneat-1.0.0/assets/vendor/css/core.css"
                rel="stylesheet" />
            <link rel="stylesheet"
                href="${pageContext.request.contextPath }/resources/sneat-1.0.0/assets/vendor/css/theme-default.css" />
            <link rel="stylesheet" href="${pageContext.request.contextPath }/resources/css/contract/setupPage.css">
        </head>

        <body>
            <div class="container mt-5">
            	<div style="text-align: right;">
		    	   <h2 class="mb-4 text-center">${contractCompany } <br>초기세팅 페이지 </h2>
                <button type="button" id="autoMappingBtn" class="btn btn-outline-dark btn-sm" onclick="autoMapping()">데이터삽입</button>
				</div>
                
                
                <h5>※ 그룹웨어 내에서 수정 가능합니다</h5>
                <form id="setupPageForm" method="POST"
                    action="${pageContext.request.contextPath}/setupPage/previewSetupPage">
                    <input type="hidden" name="contractId" value="${contractId}" />
                    <input type="hidden" name="contractCompany" value="${contractCompany }" />
                    <!-- 직급 설정 체크박스 -->
                    <div class="mb-4">
                        <label for="firstPosition" class="form-label">직급 설정</label>

                        <div class="d-flex flex-wrap border p-3 rounded" style="gap: 15px;">
                            <div class="form-check">
                                <input type="checkbox" name="firstPosition" value="사원" class="form-check-input" />
                                <label class="form-check-label">사원</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstPosition" value="주임" class="form-check-input" />
                                <label class="form-check-label">주임</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstPosition" value="대리" class="form-check-input" />
                                <label class="form-check-label">대리</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstPosition" value="과장" class="form-check-input" />
                                <label class="form-check-label">과장</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstPosition" value="차장" class="form-check-input" />
                                <label class="form-check-label">차장</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstPosition" value="팀장" class="form-check-input" />
                                <label class="form-check-label">팀장</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstPosition" value="부장" class="form-check-input" />
                                <label class="form-check-label">부장</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstPosition" value="이사" class="form-check-input" />
                                <label class="form-check-label">이사</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstPosition" value="상무" class="form-check-input" />
                                <label class="form-check-label">상무</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstPosition" value="전무" class="form-check-input" />
                                <label class="form-check-label">전무</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstPosition" value="부사장" class="form-check-input" />
                                <label class="form-check-label">부사장</label>
                            </div>
                        </div>
                    </div>

                    <!-- 부서 설정 체크박스 -->
                    <div class="mb-4">
                        <label for="firstDepart" class="form-label">부서 설정</label>
                        <div class="d-flex flex-wrap border p-3 rounded" style="gap: 15px;">
                            <div class="form-check">
                                <input type="checkbox" name="firstDepart" value="인사부" class="form-check-input">
                                <label class="form-check-label">인사부</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstDepart" value="영업부" class="form-check-input">
                                <label class="form-check-label">영업부</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstDepart" value="기술부" class="form-check-input">
                                <label class="form-check-label">기술부</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstDepart" value="재무부" class="form-check-input">
                                <label class="form-check-label">재무부</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstDepart" value="마케팅부" class="form-check-input">
                                <label class="form-check-label">마케팅부</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstDepart" value="경영부" class="form-check-input">
                                <label class="form-check-label">경영부</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstDepart" value="생산부" class="form-check-input">
                                <label class="form-check-label">생산부</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstDepart" value="총무부" class="form-check-input">
                                <label class="form-check-label">총무부</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstDepart" value="고객지원부" class="form-check-input">
                                <label class="form-check-label">고객지원부</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstDepart" value="IT부서" class="form-check-input">
                                <label class="form-check-label">IT부서</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstDepart" value="연구개발부" class="form-check-input">
                                <label class="form-check-label">연구개발부</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstDepart" value="품질관리부" class="form-check-input">
                                <label class="form-check-label">품질관리부</label>
                            </div>
                        </div>
                    </div>
                    <!-- 휴가 일수 설정-->
                    <div class="mb-4">
                        <label class="form-label">휴가설정</label> 
                        <input type="number" id="firstEmploy" name="firstEmploy" class="form-control" placeholder="예) 15" min="0"
                            max="30" required oninput="handleVacaInput(this)"/>
                    </div>

                    <!-- 출퇴근 시간 설정 -->
                    <div class="mb-4">
                        <label class="form-label">출퇴근 시간 설정</label>
                        <div class="d-flex flex-wrap border p-3 rounded">
                            <div class="form-group me-3">
                                <label class="form-label">출근 시간</label>
                                <!--             <input type="time" step="1800" name="attendStart" id="attendStart" class="form-control" required /> -->
                                <select id="attendStart" name="attendStart" class="form-select" required>
                                	<option value="01:00">01:00</option>
								    <option value="01:30">01:30</option>
								    <option value="02:00">02:00</option>
								    <option value="02:30">02:30</option>
								    <option value="03:00">03:00</option>
								    <option value="03:30">03:30</option>
								    <option value="04:00">04:00</option>
								    <option value="04:30">04:30</option>
								    <option value="05:00">05:00</option>
								    <option value="05:30">05:30</option>
								    <option value="06:00">06:00</option>
								    <option value="06:30">06:30</option>
								    <option value="07:00">07:00</option>
								    <option value="07:30">07:30</option>
								    <option value="08:00">08:00</option>
								    <option value="08:30">08:30</option>
								    <option value="09:00">09:00</option>
								    <option value="09:30">09:30</option>
								    <option value="10:00">10:00</option>
								    <option value="10:30">10:30</option>
								    <option value="11:00">11:00</option>
								    <option value="11:30">11:30</option>
								    <option value="12:00">12:00</option>
								    <option value="12:30">12:30</option>
								    <option value="13:00">13:00</option>
								    <option value="13:30">13:30</option>
								    <option value="14:00">14:00</option>
								    <option value="14:30">14:30</option>
								    <option value="15:00">15:00</option>
								    <option value="15:30">15:30</option>
								    <option value="16:00">16:00</option>
								    <option value="16:30">16:30</option>
								    <option value="17:00">17:00</option>
								    <option value="17:30">17:30</option>
								    <option value="18:00">18:00</option>
								    <option value="18:30">18:30</option>
								    <option value="19:00">19:00</option>
								    <option value="19:30">19:30</option>
								    <option value="20:00">20:00</option>
								    <option value="20:30">20:30</option>
								    <option value="21:00">21:00</option>
								    <option value="21:30">21:30</option>
								    <option value="22:00">22:00</option>
								    <option value="22:30">22:30</option>
								    <option value="23:00">23:00</option>
								    <option value="23:30">23:30</option>
								    <option value="24:00">24:00</option>
                                </select>

                            </div>
                            <div class="form-group">
                                <label class="form-label">퇴근 시간</label>
                                <!--             <input type="time" step="1800" name="attendEnd" id="attendEnd" class="form-control" required /> -->
                                <select id="attendEnd" name="attendEnd" class="form-select" required>
                                	<option value="01:00">01:00</option>
								    <option value="01:30">01:30</option>
								    <option value="02:00">02:00</option>
								    <option value="02:30">02:30</option>
								    <option value="03:00">03:00</option>
								    <option value="03:30">03:30</option>
								    <option value="04:00">04:00</option>
								    <option value="04:30">04:30</option>
								    <option value="05:00">05:00</option>
								    <option value="05:30">05:30</option>
								    <option value="06:00">06:00</option>
								    <option value="06:30">06:30</option>
								    <option value="07:00">07:00</option>
								    <option value="07:30">07:30</option>
								    <option value="08:00">08:00</option>
								    <option value="08:30">08:30</option>
								    <option value="09:00">09:00</option>
								    <option value="09:30">09:30</option>
								    <option value="10:00">10:00</option>
								    <option value="10:30">10:30</option>
								    <option value="11:00">11:00</option>
								    <option value="11:30">11:30</option>
								    <option value="12:00">12:00</option>
								    <option value="12:30">12:30</option>
								    <option value="13:00">13:00</option>
								    <option value="13:30">13:30</option>
								    <option value="14:00">14:00</option>
								    <option value="14:30">14:30</option>
								    <option value="15:00">15:00</option>
								    <option value="15:30">15:30</option>
								    <option value="16:00">16:00</option>
								    <option value="16:30">16:30</option>
								    <option value="17:00">17:00</option>
								    <option value="17:30">17:30</option>
								    <option value="18:00">18:00</option>
								    <option value="18:30">18:30</option>
								    <option value="19:00">19:00</option>
								    <option value="19:30">19:30</option>
								    <option value="20:00">20:00</option>
								    <option value="20:30">20:30</option>
								    <option value="21:00">21:00</option>
								    <option value="21:30">21:30</option>
								    <option value="22:00">22:00</option>
								    <option value="22:30">22:30</option>
								    <option value="23:00">23:00</option>
								    <option value="23:30">23:30</option>
								    <option value="24:00">24:00</option>
                                </select>

                            </div>
                        </div>
                    </div>
                    <!-- Hidden Input to Store Combined Value -->
                    <input type="hidden" name="firstAttend" id="hiddenFirstAttend" />



                    <!-- 전자결재 사용 여부 -->
                    <div class="mb-4">
                        <label class="form-label">전자결재 사용 여부</label>
                        <div class="d-flex align-items-center">
                            <div class="form-check me-3">
                                <input type="radio" id="elecYes" name="useElec" value="Y" class="form-check-input">
                                <label for="elecYes" class="form-check-label">예</label>
                            </div>
                            <div class="form-check">
                                <input type="radio" id="elecNo" name="useElec" value="N" class="form-check-input"
                                    checked>
                                <label for="elecNo" class="form-check-label">아니오</label>
                            </div>
                        </div>
                    </div>


                    <!-- 전자결재 설정 -->
                    <div id="elecSettings" class="mb-4" style="display: none;">
                        <label for="firstElec" class="form-label">전자결재 종류</label>
                        <div class="d-flex flex-wrap border p-3 rounded" style="gap: 15px;">
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="휴가신청" class="form-check-input">
                                <label class="form-check-label">휴가신청</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="출장신청" class="form-check-input">
                                <label class="form-check-label">출장신청</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="인사발령" class="form-check-input">
                                <label class="form-check-label">인사발령</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="지출결의서" class="form-check-input">
                                <label class="form-check-label">지출결의서</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="구매요청서" class="form-check-input">
                                <label class="form-check-label">구매요청서</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="결재문서 등록" class="form-check-input">
                                <label class="form-check-label">결재문서 등록</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="프로젝트 승인" class="form-check-input">
                                <label class="form-check-label">프로젝트 승인</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="예산안 승인" class="form-check-input">
                                <label class="form-check-label">예산안 승인</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="성과평가 보고서" class="form-check-input">
                                <label class="form-check-label">성과평가 보고서</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="교육신청서" class="form-check-input">
                                <label class="form-check-label">교육신청서</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="사직서" class="form-check-input">
                                <label class="form-check-label">사직서</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="업무보고서" class="form-check-input">
                                <label class="form-check-label">업무보고서</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="문서 열람 요청" class="form-check-input">
                                <label class="form-check-label">문서 열람 요청</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="조직 개편 요청" class="form-check-input">
                                <label class="form-check-label">조직 개편 요청</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="채용 요청" class="form-check-input">
                                <label class="form-check-label">채용 요청</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="외근 보고서" class="form-check-input">
                                <label class="form-check-label">외근 보고서</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="업무 분장표" class="form-check-input">
                                <label class="form-check-label">업무 분장표</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="회의록 결재" class="form-check-input">
                                <label class="form-check-label">회의록 결재</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="시간외근무 신청" class="form-check-input">
                                <label class="form-check-label">시간외근무 신청</label>
                            </div>
                            <div class="form-check">
                                <input type="checkbox" name="firstElec" value="장비 대여 신청" class="form-check-input">
                                <label class="form-check-label">장비 대여 신청</label>
                            </div>
                        </div>
                    </div>





                    <div class="text-end">
                        <button type="submit" id="submitBtn" class="btn btn-primary">신청하기</button>
                        <button type="button" class="btn btn-danger">취소</button>
                    </div>


                </form>

            </div>
            <script src="${pageContext.request.contextPath }/resources/js/app/provider/setupPage.js"></script>
            
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

        </body>

        </html>