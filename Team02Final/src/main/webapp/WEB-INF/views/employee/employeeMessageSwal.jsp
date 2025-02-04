<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<c:if test="${not empty message}">
	<c:if test="${messageKind eq 'error'}">
		<script>
			Swal.mixin({
			    toast: true,
			    position: 'center',
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
			    icon: 'error',
			    title: `${message}`
			})
		</script>
	</c:if>
	<c:if test="${messageKind eq 'success'}">
		<script>
			Swal.mixin({
			    toast: true,
			    position: 'top',
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
			    icon: 'success',
			    title: `${message}`
			})
		</script>
	</c:if>
</c:if>
<c:remove var="message" scope="session"></c:remove>
<c:remove var="messageKind" scope="session"></c:remove>

<style>
	#search-btn{
		width: 100px;
	}
	
</style>