<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>    

     <!-- Layout wrapper -->
    <div class="layout-wrapper layout-content-navbar">
      <div class="layout-container">
        <!-- Menu -->

        <aside id="layout-menu" class="layout-menu menu-vertical menu bg-menu-theme">
          <div class="app-brand demo">
            
            <!-- sneat 로고 시작 -->
<a href="${pageContext.request.contextPath }/prov/main" >
<img 
  alt="logo" 
  src="${pageContext.request.contextPath }/resources/sneat-1.0.0/assets/img/layouts/logo.png" 
  style="width: 65px; height: 55px; margin-left: -18px; margin-right: -10px;"></a>

              <!-- sneat 아이콘 끝 -->
              <a href="${pageContext.request.contextPath }/prov/main">
              <span class="app-brand-text demo menu-text fw-bolder ms-2" style="margin-left: -5px;">work2gether</span>
              </a>
              

            <a href="javascript:void(0);" class="layout-menu-toggle menu-link text-large ms-auto d-block d-xl-none">
              <i class="bx bx-chevron-left bx-sm align-middle"></i>
            </a>
          </div>

          <div class="menu-inner-shadow"></div>
          
          
          
          
			<!-- 타이틀  -->
          <ul class="menu-inner py-1">


			<!-- 
				토글을 주고싶을땐 class="menu-link menu-toggle"
				토글은 안주고싶을땐 class="menu-link"
			 -->
			<!-- Layouts -->
                
			
            <li class="menu-item">
              <a href="${pageContext.request.contextPath}/contract/contractWait" class="menu-link" >
                <i class='bx bx-list-plus'></i>
                <div data-i18n="Basic" style="margin-left :15px;">승인 대기업체 목록</div>
              </a>
            </li>

            
             <li class="menu-item">		
              <a href="${pageContext.request.contextPath}/contract/firstSettingList" class="menu-link">
                <i class='bx bx-list-plus'></i>
                <div data-i18n="Layouts" style="margin-left :15px;">세부 세팅대기업체 목록</div>
              </a>
            </li> 
			
			<li class="menu-item">		
              <a href="${pageContext.request.contextPath }/contract/contCompanyList" class="menu-link">
                <i class='bx bx-list-check'></i>
                <div data-i18n="Layouts" style="margin-left :15px;">계약업체목록</div>
              </a>
            </li> 
			
             <li class="menu-item">		
              <a href="${pageContext.request.contextPath}/contract/question" class="menu-link">
                <i class='menu-icon tf-icons bx bx-note'></i>
                <div data-i18n="Layouts">문의게시판</div>
              </a>
            </li> 
             <li class="menu-item">		
              <a href="javascript:void(0)" class="menu-link menu-toggle">
                <i class='bx bx-line-chart'></i>
                <div data-i18n="Layouts" style="margin-left :15px;">계약업체 통계</div>
              </a>
              
              <ul class="menu-sub">
						<li class="menu-item"><a
							href="${pageContext.request.contextPath}/prov/contTypeStatPage"
							class="menu-link">
								<div data-i18n="Without menu">업종별 계약 분석</div>
						</a></li>
						<li class="menu-item"><a
							href="${pageContext.request.contextPath}/prov/monthlyStatPage"
							class="menu-link">
								<div data-i18n="Without navbar">월별 계약 추세</div>
						</a></li>
						<li class="menu-item"><a
							href="${pageContext.request.contextPath}/prov/scaleContStatPage"
							class="menu-link">
								<div data-i18n="Container">업체 규모별 분석</div>
						</a></li>
					
						<li class="menu-item"><a
							href="${pageContext.request.contextPath}/prov/storageContStatPage"
							class="menu-link">
								<div data-i18n="Container">스토리지별 계약 현황</div>
						</a></li>
					
			
					
					</ul>
            </li> 
		  </ul>	  
		  
		  
		  <ul>
		  	<li>
		  		<a href="${pageContext.request.contextPath}/provider/logout">
				<i class="bx bx-power-off me-2"></i> <span class="align-middle">로그아웃</span>
				</a>
			</li>
		  </ul>
		  

        </aside>
        <!-- / Menu -->
      </div>
    </div>

