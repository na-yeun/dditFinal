<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/security/tags"  prefix="security" %>


<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/sneat-1.0.0/assets/vendor/libs/perfect-scrollbar/perfect-scrollbar.css" />
<style>
#thumbnailImg{
	height : 100px;
	width : 100px;
}
#folderIcon {
	height : 33px;
	width : 33px;
}
#fileIcon {
	height : 33px;
	width : 33px;
}
#vertical-example{
	height: 600px;
}
#cloudView, #thumbNailView{
	font-size: 0.8rem;
}
#cloudView{
	height: 600px;
	width: 700px;
}
#thumbNailView {
    height: 600px;
    width: 450px;
    padding: 0; /* 내부 여백 제거 */
    margin: 0; /* 외부 여백 제거 */
}
.nav-align-top {
    margin-top: 0; /* 상단 여백 제거 */
}
.folder-name, .file-name{
	text-decoration: underline;
}
.folder-name:hover, .file-name:hover{
	cursor: pointer; 
}
#searchKeyWord {
    flex: 1;
}
</style>
<h4 id="page-title">나의 클라우드</h4>
<hr/>

	<div class="d-flex justify-content-between align-items-center">
		<div  class="d-flex align-items-center gap-2">
			<button type="button" class="btn btn-primary me-2" onclick="location.reload();">
				<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAUBJREFUSEvtlb0uBVEUhb8ViWhUCg8gEUGjkggJBVFpNTwBD3DfQPQ8AZ1GofRTEIlKI24UHkChEh1Z7siMzMydmTPj3tuIU+6z9/rO2WftHDHgpQHrUxtgexy4jg+0JOmlzuFqAWyPArfAbCz6ACxIegtBggDbw8AFsJgTuwFWJH1UQSoBtqP9U2CjROQE2JTkMkgIcADsBNpwKGm3McB2C9gL9Tjeb0naL8otvIHtLeCopniSti3pOF/TBbC9DpwBQw0Bnx2nrUm6TNdlALbnYjuONBRP0t87jotm5D4J/ABsTwB3wNgvxZOyV2Be0nMUSAOegMkexZPyR0kzeUAbmOoToC1pOgNIC9suHZzKqZW6TFNm0z8IUO76RW3M5zR6g39Axn22r4DlVPBc0mrOysGc0jfo07B9ywS/zF5hX3xheBmUkmBfAAAAAElFTkSuQmCC"/>
			</button>
			<button type="button" class="btn btn-primary me-2" onclick="triggerFileInput()">
				<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAX5JREFUSEvtlD8vBFEUxX8n8adQaCgkJKJRKigkgkRBI6JQSESHreh0OtGoNQjfAZ/AUkiIxGcQQWQjQSmufWt2MzvzZt9uYru95X3n3d+7J2dGNLnU5Pm0AEGHgxaZ2QCwCcwCg0A38AwcAweS3mpRagLMbA64ANozhnwCq5LOsiCZADMbA66BzoAPP8CMpEufzgswM9e/Lb5+NGjyn+AFGJb0kdSnAGY2VLRkD1j2DD+MejnP2Y4kd6+qqgBmtgKcAh2eATfAVNTPA+MJzb2k1MYVQOS5s8VXLikjklx6MLM+4AHoTYjvgF1J5+V+HODSMu+Z/g1MSnIbVMrM3AZXQJvnzmI5WXGAe2WPR7whyWU+VWa2Dhx5jvKSpl0/DvgCujIsKrUllfRmZrV0xQ+zIKn02DjAZX7inwBPkvqTgAUg84tscIMTSWtVgGj1fWA7sH7o+DFK3HsKEEGWgC2XnNCkxHkh+rXkJL2mYtrgsLrlwd913ZMyhC1A0MFfFhFvGeGSkYUAAAAASUVORK5CYII="/>
				파일 업로드
			</button>
			<button type="button" class="btn btn-primary me-2" id="downloadBtn">
				<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAOJJREFUSEvtlcERAVEQRLvd3IUgCK4yUCUPQiEPVTJwJQghuLtp1eofWPt3xmEVVfo8v9/8nvm7RM9iz/74DoAktd2UZNhgWGDjPyBcpN+LSNIawDK82nPBhuSqeaZ1iyQNAGwBzJOQHYAFyWsKUFZzCGAPYBJAjgBmJC+tb6XrsKQRgAOAcaXuBGBK8lzzCR+aJJsbYtijbGpzQ6oKASUux+S4HJvlOByL4+lUClAgHrgHb3mgHmyoNKBA7mtI0muc0luAlGOj6POA2ncn233zH/Fyg94B2U6zdZ+fQbazbN0Nx7hMGe8/H44AAAAASUVORK5CYII="/>
				파일 다운로드</button>
			<button type="button" class="btn btn-primary me-2" data-bs-toggle="modal" data-bs-target="#smallModal">
				<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAMBJREFUSEvVlcEJAjEQRd+vQxDEgxZhBx5sxosdaEM24c2LN1HwJtjCyAorIRrdZJnA5hrmv/9nwkQ4HznrUw9gZitgC8yjVAdgKelekvadwMxuwCghcgIWkh65kBBgucU/zKwl7Zt7D0Cje5Y09QQg6WXeK0EZoHVlZn/nVZRgmIDWdeoJp9rVuUXugND5MGdQNUHOruo85BzRb2nDVXEFxqWCUd1F0iTeRc2HswNmPSFHYPOxrnuKJsvr/cleCZ5Mt3IZxSvCMQAAAABJRU5ErkJggg=="/>
			</button>
			<button type="button" class="btn btn-secondary me-2" id="deleteBtn">
				<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAKpJREFUSEvtlbENwjAQRd8v2IQKVsgYDJEh6BkBsQMzMEB6kJiF4lMFQZTY5yihSVxa1nvnb/ssZh6amU9WYLsGzgOF1JIuqSKTAtsVcAM2A5AXUElqhiQ/AtueIjJJH+5/BVNU32X0nsHYqL6jaUULELTb7sbWNz8qolWQvUVrRAuIqKQBljy0O7ArgQMPSftoNz0AJ2AblDyBo6RrSBCEhpZlP/0QJbHoDUJ3mhnQWSt8AAAAAElFTkSuQmCC"/>
			</button>
			<button type="button" class="btn btn-primary" id="fileMoveBtn">
				파일 이동
			</button>
			<button type="button" class="btn btn-warning" id="pasteBtn" style="display: none;">
				이동하기
			</button>
		</div>
		<div class="navbar-nav align-items-center">
          <div class="nav-item d-flex align-items-center">
            <input type="text" id="searchKeyWord" class="form-control" placeholder="검색어를 입력해주세요." aria-label="검색어를 입력해주세요.">
            <button type="button" class="btn btn-primary me-2" id="searchBtn">검색</button>
          </div>
        </div>
    </div>
		<div class="table-responsive">
		<table class="table">
			<tr>
				<td colspan="2" id="cloudView">
					<div class="card overflow-hidden mb-6" id="vertical-example">
						<div class="card-body" id="vertical-example">
						
					<div class="mb-4">
					  <label for="exampleFormControlReadOnlyInput1" class="form-label">현재 경로</label>
					  <input class="form-control" type="text" id="currentPathForm" readonly />
					</div>
					<table class="table" id="cloudStorage">
					    <thead>
					        <tr>
					        	<th>
					        		<input type="checkbox" id="allCheck">
					        	</th>
					            <th>종류</th>
					            <th>이름</th>
					            <th>크기</th>
					            <th>마지막 수정일</th>
					        </tr>
					    </thead>
					    <tbody>
							<c:choose>
								<c:when test="${not empty folders || not empty files }">
							        <!-- 폴더 출력 -->
							        <c:forEach var="folder" items="${folders}">
							        	<c:choose>
							        		<c:when test="${folder.name != empId}">
									            <tr>
									            	<td>
									            		<input type="checkbox" name="folder" value="${folder.name}">
									            	</td>
									                <td>
									                	<img id="folderIcon" alt="folderImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-folder.png"></img>
									                </td>
									                <td>
									                	<span class="folder-name" onclick="folderThumbnail('','${folder.name}')" ondblclick="navigateToFolder('${folder.name}')">${folder.name}</span>
									                </td>
									                <td>${folder.size}</td>
									                <td>${folder.lastModified}</td>
									            </tr>
							        		</c:when>
							        	</c:choose>
							        </c:forEach>
							
							        <!-- 파일 출력 -->
							        <c:forEach var="file" items="${files}">
							            <tr>
							            	<td>
							            		<input type="checkbox" name="file" value="${file.name}">
							            	</td>
							                <td>
									        	<c:if test="${fn:endsWith(file.name, '.css')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-css.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.doc')}">
													<img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-doc.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.gif')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-gif.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.html')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-html.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.jar')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-html.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.java')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-java.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.jpeg')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-jpeg.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.js')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-js.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.mp4')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-mp4.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.odt')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-odt.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.pdf')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-pdf.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.php')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-php.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.png')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-png.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.ppt')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-ppt.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.txt')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-txt.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.xls')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-xls.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.xlsx')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-xlsx.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.xml')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-xml.png" />
												</c:if>
									        	<c:if test="${fn:endsWith(file.name, '.zip')}">
												    <img id="fileIcon" alt="fileImg" src="${pageContext.request.contextPath}/resources/images/icon/icon-zip.png" />
												</c:if>
							                </td>
							                <td>
							                	<span class="file-name" onclick="thumbnail('${file.name}')" id="object">${file.name}</span>
							                </td>
							                <td>${file.size}</td>
							                <td>${file.lastModified}</td>
							            </tr>
							        </c:forEach>
								</c:when>
							<c:otherwise>
								<tr>
									<td colspan="4">파일이 아직 업로드 되지 않았습니다.</td>
								</tr>
							</c:otherwise>
						</c:choose>
					    </tbody>
					</table>
					</div>
					</div>
				</td>
			<td id="thumbNailView">
				<div class="nav-align-top" style="width: 440px; position: relative; top: -70px;">
					<ul class="nav nav-tabs" role="tablist">
					  <li class="nav-item">
					    <button type="button" class="nav-link active" role="tab" data-bs-toggle="tab" data-bs-target="#navs-top-home" aria-controls="navs-top-home" aria-selected="true">상세조회</button>
					  </li>
					</ul>
					  <div class="tab-content">
					    <div class="tab-pane fade show active" id="navs-top-align-home">
					      <div class="table-responsive">
					      	<table class="table table-boarderless">
					      		<tbody id="thumbnail">
					      		
					      		</tbody>
					      	</table>
					      </div>
					    </div>
					 </div>
				</div>
			</td>
		</tr>
	</table> 
	</div>
	<form>
		 <input type="file" id="fileInput" multiple="multiple" style="display: none;" />
	</form>
	
<div class="modal fade" id="smallModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-sm" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLabel2">새 폴더 생성</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <div class="row">
          <div class="col mb-6">
            <label for="nameSmall" class="form-label">폴더 이름</label>
            <input type="text" id="folderName" class="form-control" placeholder="생성하실 폴더 이름을 입력해주세요." required="required">
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" id="makefolderBtn">생성</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
      </div>
    </div>
  </div>
  <br><br>
</div>
<script>
	<security:authorize access="isAuthenticated()">
		<security:authentication property="principal" var="principal"/>
		<c:set value="${principal.account.empName }" var="empName"></c:set>                        
		<c:set value="${principal.account.base64EmpImg }" var="empImg"></c:set>                        
		<c:set value="${principal.account.companyId }" var="companyId"></c:set>                        
		<c:set value="${principal.account.empId}"  var="empId"></c:set>
		var myEmpId = '${empId}'; // JSP에서 JavaScript 변수로 전달
	</security:authorize>
// 	let contextPath = "${pageContext.request.contextPath}";
	const currentPath = "${cloud.perCloudPath}"
	const newPath = `${currentPath}${folderName}/`
	let path = "";
	
	function triggerFileInput() {
	    const fileInput = document.getElementById('fileInput');
	    fileInput.click(); // 숨겨진 파일 선택기를 클릭
	}
</script>
<script src="${pageContext.request.contextPath}/resources/sneat-1.0.0/assets/vendor/libs/perfect-scrollbar/perfect-scrollbar.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/app/cloud/personalStorage.js"></script>