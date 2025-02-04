<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>   
<!DOCTYPE html>
<html lang="en" class="light-style layout-menu-fixed" dir="ltr" data-theme="theme-default" data-assets-path="${pageContext.request.contextPath}/assets/" data-template="vertical-menu-template-free">
<!-- Toastify CSS -->
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/toastify-js/src/toastify.min.css">
<!-- Toastify JS -->
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/toastify-js"></script>


<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">
  <title>work2gether</title>
  <meta name="description" content="">
  <meta name="keywords" content="">

  <!-- Preloaded Scripts -->
  <tiles:insertAttribute name="preScript"/>


</head>

<body>


	  <!-- Alert Message -->
<c:if test="${not empty message and not empty messageKind}">
	<script>
		let kind = `${messageKind}`;
		Swal.mixin({
		    toast: true,
		    position: "center",
		    showConfirmButton: false,
		    timer: 3000,
		    timerProgressBar: true,
		    
		    // 알림 열렸을 때 실행되는 콜백함수
		    // toast 인자로 알림 DOM 요소 접근
		    didOpen: (toast) => {
		    	// 토스트에 마우스를 올렸을 때 타이머 멈추는 이벤트(알림이 안 닫힘)
		        toast.addEventListener('mouseenter', Swal.stopTimer)
		        // 토스트에 마우스 치우면 타이머 진행 이벤트
		        toast.addEventListener('mouseleave', Swal.resumeTimer)
		    }
		}).fire({
		    icon: kind,
		    title: `${message}`,
		    customClass: {
		        title: 'swal-title',
		        text: 'swal-text'
		    }
		})
	</script>
	<c:remove var="message" scope="session"/>
	<c:remove var="messageKind" scope="session"/>
</c:if>


  <!-- Layout Wrapper -->
  <div class="layout-wrapper layout-content-navbar">
    <div class="layout-container">
      <!-- Sidebar -->
      <aside id="layout-menu" class="layout-menu menu-vertical menu bg-menu-theme">
        <tiles:insertAttribute name="sidebar" />
      </aside>
      <!-- /Sidebar -->

      <!-- Layout Page -->
      <div class="layout-page">
       

        <!-- Content Wrapper -->
        <div class="content-wrapper">
        
          <!-- Main Content -->
          <main id="main" class="main">
          	<div class="card container">

 
            	
            	<tiles:insertAttribute name="content" />
            </div>
          </main>
          <!-- /Main Content -->

          <!-- Footer -->
          <footer id="footer" class="content-footer footer bg-footer-theme">
            <tiles:insertAttribute name="footer" />
          </footer>
          <!-- /Footer -->
<!--           <div class="content-backdrop fade"></div> -->
        </div>
        <!-- /Content Wrapper -->
      </div>
      <!-- /Layout Page -->
    </div>

    <!-- Layout Overlay -->
    <div class="layout-overlay layout-menu-toggle"></div>
  </div>
  <!-- /Layout Wrapper -->

  <!-- Back to Top Button -->
  <a href="#" class="back-to-top d-flex align-items-center justify-content-center">
    <i class="bx bx-up-arrow-alt"></i>
  </a>

  <!-- Postloaded Scripts -->
  <tiles:insertAttribute name="postScript" />
 

</body>
</html>


