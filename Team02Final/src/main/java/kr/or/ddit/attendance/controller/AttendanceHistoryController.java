package kr.or.ddit.attendance.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.attendance.service.AttendanceHistoryService;
import kr.or.ddit.attendance.vo.AttendTbVO;
import kr.or.ddit.attendance.vo.AttendanceHistoryVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.paging.renderer.DefaultPaginationRenderer;
import kr.or.ddit.commons.paging.renderer.PaginationRenderer;
import kr.or.ddit.commons.vo.CommonCodeVO;
import kr.or.ddit.employee.vo.EmployeeVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/hr/attendance")
public class AttendanceHistoryController {
	
	@Inject
	private AttendanceHistoryService service;
	
	@GetMapping
	public String moveAHPage(
			@PathVariable String companyId
			   , Optional<Integer> page
			   ,@RequestParam(required = false) String department // 부서 필터
			   ,@RequestParam(required = false) String position   // 직급 필터
			   ,@RequestParam(required = false) String searchWord // 검색 필터
			   ,@RequestParam(required = false) String startDate  // 시작날짜 필터
			   ,@RequestParam(required = false) String endDate    // 종료날짜 필터
			   , Model model
				) {
				
				log.info("department >>>> {}" ,department);
				log.info("position >>>> {}" ,position);
				log.info("searchWord >>>> {}" ,searchWord);
				log.info("startDate >>>> {}" ,startDate);
				log.info("endDate >>>> {}" ,endDate);
				PaginationInfo paging = new PaginationInfo();
				paging.setCurrentPage(page.orElse(1));
				Map<String, Object> variousCondition = new HashMap<>();
				if(!(StringUtils.isBlank(startDate)&&StringUtils.isBlank(endDate))) {
					variousCondition.put("startDate", startDate);
					variousCondition.put("endDate", endDate);
					model.addAttribute("startDate", startDate);
					model.addAttribute("endDate", endDate);
				}
				
				if(StringUtils.isNotBlank(position)) {
					variousCondition.put("position", position);
					model.addAttribute("position",position);
				}
				if(StringUtils.isNotBlank(department)) {
					variousCondition.put("department", department);
					model.addAttribute("department",department);
				}
				if (StringUtils.isNotBlank(searchWord)) {
			        variousCondition.put("searchWord", searchWord);
			        model.addAttribute("searchWord", searchWord);
			    }
				paging.setVariousCondition(variousCondition);
				List<AttendanceHistoryVO> attendlist = service.readAllAttendanceHistoryList(paging);

			    PaginationRenderer renderer = new DefaultPaginationRenderer();
				String pagingHtml = renderer.renderPagination(paging, "fnPaging");

				model.addAttribute("list", attendlist);
				model.addAttribute("pagingHtml", pagingHtml);
				model.addAttribute("variousCondition", variousCondition);
		return "attendance/attendanceSystem";
	}
	
	
//	@GetMapping("/attendList")
//	@ResponseBody
//	public Map<String, Object> selectAllAttendList(
//			@PathVariable("companyId") String companyId
//		  , @RequestParam(required = false , defaultValue = "1") int page 
//		  , @RequestParam(required = false) String department
//		  , @RequestParam(required = false) String position
//		  , @RequestParam(required = false) String startDate
//		  , @RequestParam(required = false) String endDate
//		  , @RequestParam(required = false) String searchWord 
//			
//	){
//		// 페이징 처리 객체 생성 및 설정
//		PaginationInfo<AttendanceHistoryVO> paging = new PaginationInfo<AttendanceHistoryVO>();
//		paging.setCurrentPage(page);
//		
//		// 옵션 및 검색 조건 Map에 저장 
//		Map<String, Object> condition = new HashMap<String, Object>();
//		condition.put("department", department);
//		condition.put("position", position);
//		condition.put("startDate", startDate);
//		condition.put("endDate", endDate);
//		condition.put("searchWord", searchWord);
//		
//		paging.setVariousCondition(condition);
//		log.info("vcondition : {}",condition);
//		List<AttendanceHistoryVO> list = service.readAllAttendanceHistoryList(paging);
//		
//		// 결과 데이터 반환 
//		Map<String, Object> result = new HashMap<String, Object>();
//		result.put("attendList", list);
//		result.put("currentPage", paging.getCurrentPage());
//		result.put("totalPage", paging.getTotalPage());
//		result.put("startPage", paging.getStartPage());
//		result.put("endPage", Math.min(paging.getEndPage(), paging.getTotalPage()));
//		result.put("totalRecord", paging.getTotalRecord());
//		
//		return result;
//	}
	
	@GetMapping("/downloadAttendanceExcel")
	public ResponseEntity<byte[]> downloadAttendanceExcel(
			@PathVariable("companyId") String companyId
		  , @RequestParam(required = false) String department
		  , @RequestParam(required = false) String position
		  , @RequestParam(required = false) String startDate
		  , @RequestParam(required = false) String endDate
		  , @RequestParam(required = false) String searchWord 
		  
	) {	
		// 옵션 및 검색 조건 Map에 저장 
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("department", department != null ? department : "");
				condition.put("position", position != null ? position : "");
				condition.put("startDate", startDate != null ? startDate : "");
				condition.put("endDate", endDate != null ? endDate : "");
				condition.put("searchWord", searchWord != null ? searchWord : "");
				
				log.info("다운로드 컨디션 : {}" , condition);
				List<AttendanceHistoryVO> list = service.readDownloadAttendanceExcel(condition);
				try(Workbook workbook = new XSSFWorkbook()){
					
				
				// 엑셀 파일 생성 (POI)
				
				List<EmployeeVO> empName = service.readEmpName(searchWord);
				
				Sheet sheet = null;
				
				// 사원이름 검색을 하고 다운로드를 했을 때 사원이름을 검증해서 존재한다면 "XXX 근태 기록표.xlsx" 이런식으로 출력하게 하기위해 
				boolean exists = empName != null && !empName.isEmpty() && empName.stream()
                        .filter(emp -> emp != null && emp.getEmpName() != null)
                        .anyMatch(emp -> emp.getEmpName().equals(searchWord));

					if (searchWord == null || searchWord.trim().isEmpty()) {
					  sheet = workbook.createSheet("근태기록표");
					} else if (exists) {
						String replaceSearchWord = searchWord.replace("+", " ").replace("/", "").trim(); // 안전한 문자열 처리
					    sheet = workbook.createSheet(replaceSearchWord + " 근태기록표");
					} else {
					  log.warn("검색어에 맞는 사원이 없습니다: {}", searchWord);
					  sheet = workbook.createSheet("근태기록표");
					}
				Row headerRow = sheet.createRow(0); 
				String[] headers = {"날짜","사원명","직급","부서","출근시간","퇴근시간","초과근무여부","초과근무시간(분)"
						, "출근상태", "퇴근상태", "지각사유" , "조퇴사유"
				};
				for (int i = 0; i < headers.length; i++) { // 헤더 배열에 있는 항목을 반복하여 Excel 셀에 넣음
		            Cell cell = headerRow.createCell(i);  // 각 열에 해당하는 셀 생성 
		            cell.setCellValue(headers[i]); 		  // 셀에 헤더 텍스트 설정 

		            // 스타일 추가 (선택적으로 사용하면 되는부분) 없어도 상관 x 
		            CellStyle style = workbook.createCellStyle(); // 셀 스타일 객체 생성
		            Font font = workbook.createFont();		// 폰트 스타일 객체 생성 
		            font.setBold(true);						// 폰트 굵게 
		            style.setFont(font);					// 셀 스타일에 폰트 설정 
		            style.setAlignment(HorizontalAlignment.CENTER); // 셀 내 텍스트를 가운데 정렬 
		            cell.setCellStyle(style);						// 생성한 스타일을 셀에 적용 
		            
		            sheet.autoSizeColumn(i);
		            sheet.setColumnWidth(i, (sheet.getColumnWidth(i))+1024);
		            
				}
				
					int rowNum = 1;
					for (AttendanceHistoryVO data : list) {
					    Row row = sheet.createRow(rowNum++);

					    row.createCell(0).setCellValue(data.getAtthisId());
					    row.createCell(1).setCellValue(data.getEmp() != null && data.getEmp().getEmpName() != null ? data.getEmp().getEmpName() : ""); // null 방어
					    row.createCell(2).setCellValue(data.getPosiName() != null ? data.getPosiName() : "");
					    row.createCell(3).setCellValue(data.getDepartName() != null ? data.getDepartName() : "");
					    row.createCell(4).setCellValue(data.getHahisTime() != null ? data.getHahisTime() : "");
					    row.createCell(5).setCellValue(data.getHleaveTime() != null ? data.getHleaveTime() : "");
					    row.createCell(6).setCellValue(data.getAtthisOverYn() != null ? data.getAtthisOverYn() : "");
					    row.createCell(7).setCellValue(data.getAtthisOver() != null ? data.getAtthisOver() : 0);
					    row.createCell(8).setCellValue(data.getAttstaIdIn() != null ? data.getAttstaIdIn() : "");
					    row.createCell(9).setCellValue(data.getAttstaIdOut() != null ? data.getAttstaIdOut() : "");
					    row.createCell(10).setCellValue(data.getAtthisCause() != null ? data.getAtthisCause() : "없음");
					    row.createCell(11).setCellValue(data.getEarlyLeaveCause()!= null ? data.getEarlyLeaveCause() : "없음");
					}

					
					// 엑셀 데이터 바이트배열로 변환 
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					workbook.write(out);
					byte[] excelData = out.toByteArray();
					
					
					
					String filename;
					if (searchWord == null || searchWord.trim().isEmpty()) {
						filename = URLEncoder.encode("근태기록표.xlsx", StandardCharsets.UTF_8.toString());
					} else if(exists){
						filename = URLEncoder.encode(searchWord + " 근태기록표.xlsx", StandardCharsets.UTF_8.toString()).replace("+"," ");
					}else {
						filename = URLEncoder.encode("근태기록표.xlsx", StandardCharsets.UTF_8.toString());
					}
						
					
					return ResponseEntity.ok()
							.contentType(MediaType.APPLICATION_OCTET_STREAM)
							.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" +filename)
							.body(excelData);
					
				} catch (IOException e) {
					e.printStackTrace();
			        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

				}
					
		}
			
			@GetMapping("/selectAttendTime")
			@ResponseBody
			public AttendTbVO selectAttendTime(
					@PathVariable("companyId") String companyId
					
			) {
				log.info("출퇴근 시간 조회 결과 : {}", service.readAttendTime());
				return service.readAttendTime();
			}
					
			
			@GetMapping("/allAttendTimeList")
			@ResponseBody
			public ResponseEntity<?> selectAllAttendTimeList(
					@PathVariable("companyId") String companyId
			){	
				List<CommonCodeVO> attendTimeList = service.readAllAttendTimeList();
				log.info("출근 시간 데이터 목록 :{} " , attendTimeList);
				if(attendTimeList == null || attendTimeList.isEmpty()) {
					return ResponseEntity.status(HttpStatus.NO_CONTENT).body("출근시간 데이터 없다잉");
				}
				return ResponseEntity.ok(attendTimeList);
				
			}
			
			
			@GetMapping("/allLeaveTimeList")
			@ResponseBody
			public ResponseEntity<?> selectAllLeaveTimeList(
					@PathVariable("companyId") String companyId
			){	
				List<CommonCodeVO> leaveTimeList = service.readAllLeaveTimeList();
				log.info("퇴근시간 데이터 목록 : {}" , leaveTimeList);
				if(leaveTimeList == null || leaveTimeList.isEmpty()) {
					return ResponseEntity.status(HttpStatus.NO_CONTENT).body("퇴근 시간 데이터 없다잉");
				}
				return ResponseEntity.ok(leaveTimeList);
				
			}
			
			
			@PutMapping("/modAttendTime")
			public ResponseEntity<String> updateAttendTime(
					@PathVariable("companyId") String companyId
				  , @Validated @RequestBody AttendTbVO att
			){
				ServiceResult result = service.modifyAttendTime(att);
				switch (result) {
				case OK:
					return ResponseEntity.ok("등록 성공");
				case FAIL:
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록 실패");
				default:
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청");
				}	
				
			}
			
			@PostMapping("/addMyHahisTime")
			@ResponseBody
			public ResponseEntity<String> insertMyHahisTime(
					@PathVariable("companyId") String companyId
				  , @Validated @RequestBody AttendanceHistoryVO ah
			){
				ServiceResult result = service.addMyHahisTime(ah);
				log.info("데이터 결과 : {}",result);
				switch (result) {
				case OK:
					return ResponseEntity.ok("출근 시간 등록 성공");
				case FAIL:
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록 실패");
				default:
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청");
				}
			
			}
				
			@GetMapping("/selectMyHahisTime/{empId}")
			@ResponseBody
			public AttendanceHistoryVO selectMyAttendTime(
					@PathVariable("companyId") String companyId
					, @PathVariable("empId") String empId
			) {
				return service.readMyHahisTime(empId);
			}
			
			@GetMapping("/selectMyHleaveTime/{empId}")
			@ResponseBody
			public AttendanceHistoryVO selectMyLeaveTime(
					@PathVariable("companyId") String companyId
					, @PathVariable("empId") String empId
			) {
				return service.readMyHleaveTime(empId);
			}
			
			@PutMapping("/addMyHleaveTime")
			@ResponseBody
			public ResponseEntity<String> insertMyHleaveTime(
					@PathVariable("companyId") String companyId
				  , @Validated @RequestBody AttendanceHistoryVO ah
			){	
				ServiceResult result = service.addMyHleaveTime(ah);
				switch (result) {
				case OK:
					return ResponseEntity.ok("퇴근 등록 성공");
				case FAIL:
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록 실패");
				default:
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청");
				}
				
			}
			
			@GetMapping("/attendDetail/{empId}/{atthisId}")
			@ResponseBody
			public ResponseEntity<AttendanceHistoryVO> selectOneAttendDetail(
					@PathVariable("companyId") String companyId
				  , @PathVariable("empId") String empId
				  , @PathVariable("atthisId") String atthisId 
				  
			){
				AttendanceHistoryVO avo =  service.readOneAttendDetail(empId , atthisId);
				log.info("한명 근태 상세결과" , avo);
				if(avo == null) {
					return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
				};
				
				return ResponseEntity.ok(avo);
			}
				
			@GetMapping("/attendStatus")
			@ResponseBody
			public List<CommonCodeVO> selectAttendStatus(){
				
				return service.readAttendStatus();
			}
			
			@GetMapping("/leaveStatus")
			@ResponseBody
			public List<CommonCodeVO> selectLeaveStatus(){
				return service.readLeaveStatus();
			}
			
			@PutMapping("/modAttendLeaveStatus")
			@ResponseBody
			public ResponseEntity<String> updateAttendLeaveStatus(
					@PathVariable("companyId") String companyId
				  , @RequestBody AttendanceHistoryVO ah
			){
					ServiceResult result = service.modifyAttendLeaveStatus(ah);
					switch (result) {
					case OK:
						return ResponseEntity.ok("출퇴근 상태 수정 성공");
					case FAIL:
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패");

					default:
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청");
					}
			}
			
}




























