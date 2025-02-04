<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<security:authorize access="isAuthenticated()">
	<security:authentication property="principal" var="principal" />
	<c:set value="${principal.account.empName }" var="empName"></c:set>
	<c:set value="${principal.account.base64EmpImg }" var="empImg"></c:set>
	<c:set value="${principal.account.companyId }" var="companyId"></c:set>
	<c:set value="${principal.account.posiId }" var="posiId"></c:set>
	<c:set value="${principal.account.empId }" var="empId"></c:set>
	<!-- js에서 사용하기 위해 뽑아버림 (민재) -->
</security:authorize>
<!-- Layout wrapper -->
<div class="layout-wrapper layout-content-navbar">
	<div class="layout-container">
		<!-- Menu -->
		<aside id="layout-menu"
			class="layout-menu menu-vertical menu bg-menu-theme">
			<div class="app-brand demo">
				<!-- sneat 로고 시작 -->
				<a href="${pageContext.request.contextPath }/${companyId }/main">
					<img alt="logo"
					src="${pageContext.request.contextPath }/resources/sneat-1.0.0/assets/img/layouts/logo.png"
					style="width: 65px; height: 55px; margin-left: -18px; margin-right: -10px;">
				</a>

				<!-- sneat 아이콘 끝 -->
				<a href="${pageContext.request.contextPath }/a001/main"> <span
					class="app-brand-text demo menu-text fw-bolder ms-2"
					style="margin-left: -5px;">Work2gether</span>
				</a> <a href="javascript:void(0);"
					class="layout-menu-toggle menu-link text-large ms-auto d-block d-xl-none">
					<i class="bx bx-chevron-left bx-sm align-middle"></i>
				</a>
			</div>

			<div class="menu-inner-shadow"></div>
			<!-- 타이틀  -->
			<ul class="menu-inner py-1">
				<li class="nav-item navbar-dropdown dropdown-user dropdown"><a
					class="nav-link dropdown-toggle hide-arrow"
					href="javascript:void(0);" data-bs-toggle="dropdown">
						<div style="display: flex; text-align: center;">
							<!-- 로그인한 사용자의 사진이 들어가야함. -->
							<c:if test="${not empty empImg}">
								<img src="data:image/*;base64,${empImg}" alt
									class="rounded-circle" style="width: 50px; height: 50px;" />
							</c:if>
							<!-- 이미지가 없다면 -->
							<c:if test="${empty empImg}">
								<!-- /Team02Final/src/main/webapp/resources/images/profile-img.jpg -->
								<img
									src="${pageContext.request.contextPath}/resources/images/profile-img.jpg"
									alt class="rounded-circle" style="width: 47px; height: 47px;" />
							</c:if>
							<p style="padding-left: 18px; font-size: 15px; color: black;">
								<strong class="myDept"></strong> <br> ${empName}
							</p>
						</div>
				</a> <!--  -->
					<ul class="dropdown-menu dropdown-menu-end">
						<li><a class="dropdown-item"
							href="${pageContext.request.contextPath}/${companyId}/mypage">
								<i class="bx bx-user me-2"></i> <span class="align-middle">마이페이지</span>
						</a></li>
						<li>
							<div class="dropdown-divider"></div>
						</li>

						<li><a class="dropdown-item messageWriteButton"
							href="javascript:void(0);"> <i
								class='bx bx-message-dots me-2'></i> <span class="align-middle">쪽지보내기</span>
						</a></li>
						<li>
							<div class="dropdown-divider"></div>
						</li>
						<li><a class="dropdown-item"
							href="${pageContext.request.contextPath}/${companyId}/logout">
								<i class="bx bx-power-off me-2"></i> <span class="align-middle">로그아웃</span>
						</a></li>
					</ul></li>

				<c:if test="${posiId eq 7 or posiId eq 6}">
					<!-- Forms & Tables -->
					<li class="menu-header small text-uppercase">
						<span class="menu-header-text">관리</span>
					</li>
					<!-- Forms -->
					<li class="menu-item">
						<a href="javascript:void(0);" class="menu-link menu-toggle">
							<i class="menu-icon bi bi-people"></i>
							<div data-i18n="Form Elements">인사조직 관리</div>
						</a>
						<ul class="menu-sub">
							<li class="menu-item">
								<a href="${pageContext.request.contextPath}/${companyId}/hr/organi"
									class="menu-link">
									<div data-i18n="Basic Inputs">부서 관리</div>
								</a>
							</li>

							<li class="menu-item">
								<a href="${pageContext.request.contextPath}/${companyId}/hr/employee"
									class="menu-link">
									<div data-i18n="Input groups">사원 관리</div>
								</a>
							</li>
							<li class="menu-item">
								<a href="${pageContext.request.contextPath}/${companyId}/hr/attendance"
									class="menu-link">
									<div data-i18n="Input groups">근태 관리</div>
								</a>
							</li>
							<li class="menu-item">
								<a href="${pageContext.request.contextPath}/${companyId}/hr/vacation"
									class="menu-link">
									<div data-i18n="Input groups">휴가 이력</div>
								</a>
							</li>
						</ul>
					</li>
					
					<li class="menu-item">
						<a href="javascript:void(0);" class="menu-link menu-toggle">
							<i class='menu-icon tf-icons bx bx-cog'></i>
							<div data-i18n="Form Elements">회계 관리</div>
						</a>
						<ul class="menu-sub">
							<li class="menu-item">
								<a href="${pageContext.request.contextPath}/${companyId}/expense/stats"
									class="menu-link">
									<div data-i18n="Input groups">지출 관리</div>
								</a>
							</li>
							<li class="menu-item">
								<a href="${pageContext.request.contextPath}/${companyId}/payHistory"
									class="menu-link">
									<div data-i18n="Input groups">그룹웨어 결제정보</div>
								</a>
							</li>
						</ul>
					</li>
				</c:if>


				<li class="menu-header small text-uppercase"><span
					class="menu-header-text">연락 관리</span></li>
				<!-- 타이틀 끝 -->



				<!-- ✅연락관리✅ -->
				<!-- 조직도 -->
				<li class="menu-item"><a
					href="${pageContext.request.contextPath}/${companyId}/organiList"
					class="menu-link"> <i
						class="menu-icon tf-icons bx bx-collection"></i>
						<div data-i18n="Basic">조직도</div>
				</a></li>
				
				<!-- 쪽지 -->
				<li class="menu-item"><a href="javascript:void(0);"
					class="menu-link menu-toggle"> 
						<i class='menu-icon tf-icons bi bi-mailbox-flag'></i>
						<div data-i18n="Layouts">쪽지</div>
				</a> <!-- 세부 메뉴  -->
					<ul class="menu-sub">
						<li class="menu-item"><a
							href="${pageContext.request.contextPath}/${companyId}/message/receive"
							class="menu-link">
								<div data-i18n="Without menu">쪽지 수신함</div>
						</a></li>
						<li class="menu-item"><a
							href="${pageContext.request.contextPath}/${companyId}/message/send"
							class="menu-link">
								<div data-i18n="Without navbar">쪽지 발신함</div>
						</a></li>
						<li class="menu-item"><a
							href="${pageContext.request.contextPath}/${companyId}/message/box"
							class="menu-link">
								<div data-i18n="Container">쪽지 보관함</div>
						</a></li>

					</ul></li>

				<!-- 메일 -->
				<li class="menu-item"><a href="javascript:void(0);"
					class="menu-link menu-toggle"> <i
						class='menu-icon tf-icons bx bx-mail-send'></i>
						<div data-i18n="Layouts">메일</div>
				</a> <!-- 세부 메뉴  -->
					<ul class="menu-sub">
						<li class="menu-item"><a
							href="${pageContext.request.contextPath}/${companyId}/mail/send"
							class="menu-link">
								<div data-i18n="Without menu">메일 보내기</div>
						</a></li>
						<!--                 <li class="menu-item"> -->
						<%--                   <a href="${pageContext.request.contextPath}/${companyId}/mail/draft" class="menu-link"> --%>
						<!--                     <div data-i18n="Fluid">임시 보관함</div> -->
						<!--                   </a> -->
						<!--                 </li> -->
						<li class="menu-item"><a
							href="${pageContext.request.contextPath}/${companyId}/mail/sent"
							class="menu-link">
								<div data-i18n="Container">보낸 메일함</div>
						</a></li>
						<li class="menu-item"><a
							href="${pageContext.request.contextPath}/${companyId}/mail/received"
							class="menu-link">
								<div data-i18n="Without navbar">받은 메일함</div>
						</a></li>
						<li class="menu-item"><a
							href="${pageContext.request.contextPath}/${companyId}/mail/important"
							class="menu-link">
								<div data-i18n="Container">중요 메일함</div>
						</a></li>
						<li class="menu-item"><a
							href="${pageContext.request.contextPath}/${companyId}/mail/delete"
							class="menu-link">
								<div data-i18n="Blank">휴지통</div>
						</a></li>
					</ul></li>
					
				<!-- ✅작업페이지✅   -->
				<li class="menu-header small text-uppercase"><span
					class="menu-header-text">작업페이지</span></li>
								
				<!-- 일정 -->
				<li class="menu-item"><a
					href="${pageContext.request.contextPath}/${companyId}/schedule/cal"
					class="menu-link">
						<i class='menu-icon tf-icons bx bx-calendar'></i>
						<div data-i18n="Authentications">일정</div>
				</a></li>
				<!-- 전자결재 -->
				<li class="menu-item"><a href="javascript:void(0);"
					class="menu-link menu-toggle"> <i
						class='menu-icon tf-icons bx bx-copy-alt'></i>
						<div data-i18n="Misc">전자결재</div>
				</a>
					<ul class="menu-sub">
						<li class="menu-item"><a
							href="${pageContext.request.contextPath}/${companyId}/approval/list"
							class="menu-link">
								<div data-i18n="Error">나의 결재 목록</div>
						</a></li>
						<li class="menu-item"><a
							href="${pageContext.request.contextPath}/${companyId}/approval/new"
							class="menu-link">
								<div data-i18n="Under Maintenance">결재 작성하기</div>
						</a></li>
					</ul></li>
				<!-- 클라우드 -->
				<li class="menu-item"><a href="javascript:void(0);"
					class="menu-link menu-toggle"> <i
						class='menu-icon tf-icons bx bx-cloud'></i>
						<div data-i18n="Misc">클라우드</div>
				</a>
					<ul class="menu-sub">
						<li class="menu-item"><a
							href="${pageContext.request.contextPath}/${companyId}/pubCloud"
							class="menu-link">
								<div data-i18n="Error">공용 클라우드</div>
						</a></li>
						<li class="menu-item"><a
							href="${pageContext.request.contextPath}/${companyId}/perCloud"
							class="menu-link">
								<div data-i18n="Under Maintenance">개인 클라우드</div>
						</a></li>
					</ul></li>
				<!-- 프로젝트관리 -->
				<li class="menu-item"><a
					href="${pageContext.request.contextPath}/${companyId}/project"
					class="menu-link"> <i class="menu-icon tf-icons bx bx-dock-top"></i>
						<div data-i18n="Account Settings">프로젝트 관리</div>
				</a></li>
				
				<!-- 커뮤니티 -->
				<li class="menu-header small text-uppercase"><span
					class="menu-header-text">커뮤니티</span></li>

				<!-- User interface -->
				<li class="menu-item"><a
					href="${pageContext.request.contextPath}/${companyId }/room"
					class="menu-link"> <i
						class='menu-icon tf-icons bi bi-building'></i>
						<div data-i18n="Boxicons">시설 예약</div>
				</a></li>

				<li class="menu-item"><a
					href="${pageContext.request.contextPath}/${companyId}/survey"
					class="menu-link"> <!--                 <i class="menu-icon tf-icons bx bx-crown"></i> -->
						<i class='menu-icon tf-icons bx bx-columns'></i>
						<div data-i18n="Boxicons">설문 및 투표</div>
				</a></li>

				<li class="menu-item"><a
					href="${pageContext.request.contextPath}/${companyId}/notice"
					class="menu-link"> <%--                토글 추가하려면 menu link menu-toggle--%>

						<i class='menu-icon tf-icons bx bx-clipboard'></i>
						<div data-i18n="User interface">공지 사항</div>
				</a></li>

				<!-- Extended components -->
				<li class="menu-item">
					<!-- 회사 명으로 추후에 변경하겠슴둥 --> <a
					href="${pageContext.request.contextPath}/${companyId}/question"
					class="menu-link"> <i class='menu-icon tf-icons bx bx-note'></i>
						<div data-i18n="Extended UI">문의 게시판</div>
				</a>
				</li>

				<!--             <li class="menu-item"> -->
				<!--               <a href="javascript:void(0);" class="menu-link menu-toggle"> -->
				<!--                 <i class="menu-icon tf-icons bx bx-detail"></i> -->
				<!--                 <div data-i18n="Form Layouts">Form Layouts</div> -->
				<!--               </a> -->
				<!--               <ul class="menu-sub"> -->
				<!--                 <li class="menu-item"> -->
				<%--                   <a href="${pageContext.request.contextPath}/resources/sneat-1.0.0/html/form-layouts-vertical.html" class="menu-link"> --%>
				<!--                     <div data-i18n="Vertical Form">Vertical Form</div> -->
				<!--                   </a> -->
				<!--                 </li> -->
				<!--                 <li class="menu-item"> -->
				<%--                   <a href="${pageContext.request.contextPath}/resources/sneat-1.0.0/html/form-layouts-horizontal.html" class="menu-link"> --%>
				<!--                     <div data-i18n="Horizontal Form">Horizontal Form</div> -->
				<!--                   </a> -->
				<!--                 </li> -->
				<!--               </ul> -->
				<!--             </li> -->
				<!-- Tables -->
				<!--             <li class="menu-item"> -->
				<%--               <a href="${pageContext.request.contextPath}/resources/sneat-1.0.0/html/tables-basic.html" class="menu-link"> --%>
				<!--                 <i class="menu-icon tf-icons bx bx-table"></i> -->
				<!--                 <div data-i18n="Tables">Tables</div> -->
				<!--               </a> -->
				<!--             </li> -->
				<!--             Misc -->
				<!--             <li class="menu-header small text-uppercase"><span class="menu-header-text">Misc</span></li> -->

				<!--             <li class="menu-item"> -->
				<!--               <a -->
				<!--                 href="https://themeselection.com/demo/sneat-bootstrap-html-admin-template/documentation/" -->
				<!--                 target="_blank" -->
				<!--                 class="menu-link" -->
				<!--               > -->
				<!--                 <i class="menu-icon tf-icons bx bx-file"></i> -->
				<!--                 <div data-i18n="Documentation">Documentation</div> -->
				<!--               </a> -->
				<!--             </li> -->
			</ul>
		</aside>
		<!-- / Menu -->
	</div>
</div>
<script>
	const contextPath = "${pageContext.request.contextPath}";
	const companyId = "${companyId}";
	const empId = "${empId}";
</script>
<script
	src="${pageContext.request.contextPath }/resources/js/app/mainpage/sidebar.js"></script>



