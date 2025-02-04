<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>




<script>
const companyId = "${companyId}";
const contextPath = "${pageContext.request.contextPath}";
</script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/lib/dist/themes/default/style.min.css" />

<!-- 트리 컨테이너 -->
<div class="tree-wrapper">
    <!-- 검색창 -->
    <div class="search-wrapper">
        <input type="text"  placeholder="키워드 검색" />
        <button>검색</button>
    </div>

    <!-- 트리 영역 -->
    <div id="treeDirectory"></div>
</div>

    
<script src="${pageContext.request.contextPath }/resources/js/organi/organiDirectory.js"></script>

