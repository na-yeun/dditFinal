<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/css/provider/provStat.css">
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

	<!-- 매출 컨테이너 -->
	<div class="sales-container">
	    <div class="sales-item today-sales">
	        <h5>전년도 매출</h5>
	        <p id="todaySalesAmount">-</p>
	    </div>
	    <div class="sales-item year-sales">
	        <h5>올해의 매출</h5>
	        <p id="yearSalesAmount">-</p>
	    </div>
	</div>
	     	<h6 style="font-color : gray;"><strong>* 오늘 날짜 기준 1년전까지의 통계입니다.</strong></h6> 
	
	<!-- 미니 차트 컨테이너 -->
	<div class="mini-charts-container">
	    <!-- 상단 바/라인 차트 -->
	    <div class="mini-chart-item bar-line">
	        <h5>업종별 계약</h5>
	        <canvas id="contTypeChart"></canvas>
	    </div>
	    <div class="mini-chart-item bar-line">
	        <h5>월별 계약</h5>
	        <canvas id="monthlyContChart"></canvas>
	    </div>
	
	    <!-- 하단 도넛/파이 차트 -->
	    <div class="mini-chart-item doughnut-pie">
	        <h5>업체 규모별 계약</h5>
	        <canvas id="contScaleChart"></canvas>
	    </div>
	    <div class="mini-chart-item doughnut-pie">
	        <h5>사용인원별 계약</h5>
	        <canvas id="contEmpcntChart"></canvas>
	    </div>
	    <div class="mini-chart-item doughnut-pie">
	        <h5>스토리지별 계약</h5>
	        <canvas id="contStorageChart"></canvas>
	    </div>
	</div>


<!-- JavaScript 로직 포함 -->
<script>
    const contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath }/resources/js/app/provider/provStat.js"></script>
