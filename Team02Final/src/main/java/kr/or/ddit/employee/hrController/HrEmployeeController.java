package kr.or.ddit.employee.hrController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.paging.renderer.DefaultPaginationRenderer;
import kr.or.ddit.commons.paging.renderer.PaginationRenderer;
import kr.or.ddit.commons.vo.CommonCodeVO;
import kr.or.ddit.employee.service.EmployeeService;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.organitree.vo.DepartmentVO;
import kr.or.ddit.vacation.service.VacationHistoryService;
import kr.or.ddit.vacation.service.VacationStatusService;
import kr.or.ddit.vacation.vo.VacationDTO;
import kr.or.ddit.vacation.vo.VacationStatusVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/hr/employee")
public class HrEmployeeController {
	@Inject
	private EmployeeService employeeService;
	
	@GetMapping
	public String goHrEmployeeList(
			@PathVariable String companyId
		   , Optional<Integer> page
		   ,@RequestParam(required = false) String department // 부서 필터
		   ,@RequestParam(required = false) String position   // 직급 필터
		   ,@RequestParam(required = false) String searchWord // 검색 필터
		   ,@RequestParam(required = false) String startDate  // 시작날짜 필터
		   ,@RequestParam(required = false) String endDate    // 종료날짜 필터
		   ,@RequestParam(required = false) String status	  // 재직상태 필터 
		   , Model model
			) {
			
			log.info("department >>>> {}" ,department);
			log.info("position >>>> {}" ,position);
			log.info("searchWord >>>> {}" ,searchWord);
			log.info("startDate >>>> {}" ,startDate);
			log.info("endDate >>>> {}" ,endDate);
			log.info("status >>>> {}" ,status);
			PaginationInfo paging = new PaginationInfo();
			paging.setCurrentPage(page.orElse(1));
			Map<String, Object> variousCondition = new HashMap<>();
			// 파라미터 검증
			if(!(StringUtils.isBlank(startDate)&&StringUtils.isBlank(endDate))) {
				variousCondition.put("startDate", startDate);
				variousCondition.put("endDate", endDate);
				model.addAttribute("startDate", startDate);
				model.addAttribute("endDate", endDate);
			}
			if(StringUtils.isNotBlank(status)) {
				variousCondition.put("status", status);
				model.addAttribute("status",status);
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
			// 파라미터 검증 끝
			paging.setVariousCondition(variousCondition);
		    List<EmployeeVO> emplist = employeeService.readEmployeeList(paging);

		    PaginationRenderer renderer = new DefaultPaginationRenderer();
			String pagingHtml = renderer.renderPagination(paging, "fnPaging");

			model.addAttribute("list", emplist);
			model.addAttribute("pagingHtml", pagingHtml);
			model.addAttribute("variousCondition", variousCondition);
			
		return "hrEmp/hrEmployeeList";
	}
		
	
	/*
	 * 사원 목록 조회 (JSON 형식 반환 페이징처리 포함)
	 */
//	@GetMapping("/hrList")
//	@ResponseBody
//	public Map<String, Object> hrEmployeeList(
//				@PathVariable String companyId
//			   ,@RequestParam(required = false ,defaultValue = "1") int page
//			   ,@RequestParam(required = false) String department // 부서 필터
//			   ,@RequestParam(required = false) String position   // 직급 필터
// 			   ,@RequestParam(required = false) String searchWord // 검색 필터
//			   ,@RequestParam(required = false) String startDate  // 시작날짜 필터
//			   ,@RequestParam(required = false) String endDate    // 종료날짜 필터
//			   ,@RequestParam(required = false) String status	  // 재직상태 필터 
//			  // 사원리스트에서 사용하는 옵션 파라미터 
//	){
//		// 페이징 처리 객체 생성 및 설정 
//		PaginationInfo<EmployeeVO> paging = new PaginationInfo<EmployeeVO>();
//		paging.setCurrentPage(page);
//		
//		// 옵션 및 검색 조건 Map에 저장
//		Map<String, Object> condition = new HashMap<String, Object>();
//		condition.put("department", department);
//		condition.put("position", position);
//		condition.put("searchWord", searchWord);
//		condition.put("startDate", startDate);
//		condition.put("endDate", endDate);
//		condition.put("status", status);
//		
//		paging.setVariousCondition(condition); 
//		log.info("vcondition : {}" , condition);
//	    List<EmployeeVO> list = service.readEmployeeList(paging);
//
//	    // 결과 데이터 반환 (JSON)
//        Map<String, Object> result = new HashMap<>();
//        result.put("employeeList", list);
//        result.put("currentPage", paging.getCurrentPage());
//        result.put("totalPage", paging.getTotalPage());
//        result.put("startPage", paging.getStartPage());
//        result.put("endPage", Math.min(paging.getEndPage(), paging.getTotalPage()));
//        result.put("totalRecord", paging.getTotalRecord());
//         
//		return result;
//	}
	
	
	/**
	 * 단일 사원 정보 조회  
	 */
	@GetMapping("/readOneEmp/{empId}")
	@ResponseBody
	public EmployeeVO selectOneEmployee(
			@PathVariable("companyId") String companyId
		  , @PathVariable("empId") String empId
			
	) {
		log.info("empId : {} ",empId);
		log.info("companyId : {} ",companyId);
		
		return employeeService.readOneEmployee(empId); 
	}
	
	
	/**
	 * 옵션에 사용할 부서 목록 조회 
	 */
	@GetMapping("/departments")
	@ResponseBody
	public List<DepartmentVO> getDepartments(
			@PathVariable("companyId") String companyId
			){
		return employeeService.readAllDeptList();
	}
	
	/**
	 * 옵션에 사용할 직급 목록 조회 
	 */
	@GetMapping("/positions")
	@ResponseBody
	public List<CommonCodeVO> getPositions(
			@PathVariable("companyId") String companyId
			){
		return employeeService.readAllPosi();
	}
	
	/**
	 * 상태 코드 목록 조회 
	 */
	@GetMapping("/commonCodes")
	@ResponseBody
	public List<CommonCodeVO> getCommonCodes(@PathVariable("companyId") String companyId) {
	    return employeeService.readStatusType();
	}
	
	/**
	 * 사원 등록 폼 페이지 이동 
	 */
	@GetMapping("/addForm")
	public String addEmployeeForm(
			@PathVariable("companyId") String companyId
			) {
		return "/hrEmp/addEmpWindowForm";
	}
	
	/**
	 * 사원 등록 양식 엑셀 다운로드 
	 */
	@GetMapping("/downloadAddTemplate")
	public ResponseEntity<byte[]> downloadAddForm() {
	    try (Workbook workbook = new XSSFWorkbook()) { // XSSFWorkbook 객체를 생성하여 Excel 파일을 메모리에 만듦.
	        // 시트 생성 (새로운 시트를 생성하고 이름을 "사원 등록 양식" 으로 설정
	        Sheet sheet = workbook.createSheet("사원 등록 양식"); 

	        // 헤더 행 생성
	        Row headerRow = sheet.createRow(0); // 첫번째 행 (index 0 )을 생성하여 헤더로 사용 
	        // 헤더에 들어갈 문자열 배열 설정 
	        String[] headers = {"사원명", "이메일", "입사일자", "직급", "부서", "생년월일", "성별", "전화번호"};
	        
	              
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

	            // 열 너비 자동 조정
	            sheet.autoSizeColumn(i); // 각 열의 너비를 자동으로 조정하여 내용에 맞게 설정 
	            sheet.setColumnWidth(i, (sheet.getColumnWidth(i))+1024);
	        }

	        // 엑셀 데이터를 ByteArray로 변환
	        ByteArrayOutputStream out = new ByteArrayOutputStream(); // 메모리에서 데이터를 저장할 바이트 배열 출력 스트림 
	        workbook.write(out);									 // 생성된 Excel 데이터를 출력 스트림에 씀
	        byte[] excelData = out.toByteArray();					 
	        // 출력스트림의 내용을 바이트 배열로 변환
	        // 이유 : 파일을 클라이언트들에게 응답으로 전달하기 위해서는 데이터가 바이트 형태어야 하기 때문에 .

	        // 한글 파일 이름 처리
	        String fileName = URLEncoder.encode("사원등록양식.xlsx", StandardCharsets.UTF_8.toString());
	        // 파일 이름을 UTF-8로 인코딩하여 한글이 깨지지 않도록 설정 
	        
	        // HTTP 응답 설정
	        return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_OCTET_STREAM) // 컨텐츠 타입을 바이너리 데이터로 설정 (파일 다운로드용)
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName) // 파일 다운로드를 위한 헤더 설정
	                .body(excelData);  // 응답 본문에 바이트 배열 데이터를 담아 반환
	                

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body(null);
	    }
	}
	
	
	/**
	 * 엑셀 파일 읽어오기 
	 */
	@PostMapping(value = "/excelUploadEmp", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> uploadExcel(
	        @RequestPart("empFile") MultipartFile empFile  // 클라이언트에서 업로드된 엑셀 파일 받음.
	) {
	    Map<String, Object> result = new LinkedHashMap<>(); // 결과 데이터를 저장할 Map 생성

	    try {

	        // 1. 파일 유효성 검사 : 비어있는지 확인
	        if (empFile.isEmpty() || StringUtils.isBlank(empFile.getOriginalFilename())) {
	            Map<String, Object> errorMap = new HashMap<>();
	            errorMap.put("error", "파일이 비어있습니다.");
	            return ResponseEntity.badRequest().body(errorMap);
	        }

	        // 2. 임시 파일 생성 및 저장 후 엑셀 파일 읽기 
	        File tempFile = File.createTempFile("tempFile", "_", null);  // 임시파일 생성 
	        tempFile.deleteOnExit();		// JVM 종료시 임시 파일 삭제 
	        empFile.transferTo(tempFile);   // 업로드된 파일을 임시파일로 저장 
	        
	        // 3. Apache POI 를 사용하여 엑셀 파일 읽기.
	        FileInputStream fis = new FileInputStream(tempFile);
	        Workbook workbook = new XSSFWorkbook(fis); // 엑셀 Workbook 객체 생성
	        Sheet sheet = workbook.getSheetAt(0);	   // 첫 번째 시트 읽기.

	        // 데이터가 없는 경우 처리 로직
	        if (sheet.getLastRowNum() == 0) {
	            Map<String, Object> errorMap = new HashMap<>();
	            errorMap.put("error", "엑셀 파일에 데이터가 없습니다.");
	            return ResponseEntity.badRequest().body(errorMap);
	        }

	        // 엑셀데이터 읽고 데이터 처리
	        for (Row row : sheet) {
	            if (row.getRowNum() == 0) continue; // 첫 번째 행은 헤더로 간주하고 건너뜀

	            // 각 행 데이터를 저장할 Map
	            Map<String, Object> rowData = new LinkedHashMap<>();
	            
	            // 각 열(Cell) 데이터 읽기 
	            for (int i = 0; i < row.getLastCellNum(); i++) {
	                Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // 빈 셀도 포함하여 읽기. 
	                Object value1 = null;

	                // 빈 셀 검증  : 데이터가 비어있으면 에러 처리 
	                if (cell.getCellType() == CellType.BLANK || cell.toString().trim().isEmpty()) {
	                    workbook.close();
	                    Map<String, Object> errorMap = new HashMap<>();
	                    errorMap.put("error", String.format("누락된 데이터가 발견되었습니다. (행: %d, 열: %d)", row.getRowNum() + 1, i + 1));
	                    return ResponseEntity.badRequest().body(errorMap);
	                }

	                // 셀 타입에 따른 데이터 처리
	                switch (cell.getCellType()) {
	                    case STRING:
	                        value1 = cell.getStringCellValue();	// 문자열 데이터 처리 
	                        break;
	                    case NUMERIC:
	                        if (DateUtil.isCellDateFormatted(cell)) {	// 날짜 형식일 경우 
	                            value1 = new SimpleDateFormat("yyyyMMdd").format(cell.getDateCellValue());
	                        } else { // 숫자일 경우 
	                            double numericValue = cell.getNumericCellValue();
	                            value1 = (numericValue == (long) numericValue)
	                                      ? String.valueOf((long) numericValue)
	                                    : String.valueOf(numericValue);

	                            // 전화번호 처리: 헤더가 "전화번호"인 경우 앞에 0 추가
	                            if ("전화번호".equals(sheet.getRow(0).getCell(i).getStringCellValue())) {
	                                value1 = String.format("0%s", value1);
	                            }
	                        }
	                        break;
	                    case BOOLEAN:
	                        value1 = String.valueOf(cell.getBooleanCellValue());
	                        break;
	                    default:
	                        break;
	                }

	                // 각 열의 헤더를 키로 사용하여 데이터 추가
	                String key = sheet.getRow(0).getCell(i).getStringCellValue();
	                rowData.put(key, value1);
	            }

	            // 각 행 데이터를 결과 Map에 추가
	            result.put("row" + row.getRowNum(), rowData);
	        }

	        workbook.close();
	        return ResponseEntity.ok(result);

	    } catch (IOException e) {
	        e.printStackTrace();
	        Map<String, Object> errorMap = new HashMap<>();
	        errorMap.put("error", "파일 처리 중 오류가 발생했습니다.");
	        return ResponseEntity.status(500).body(errorMap);
	    }
	}





	/**
	 * 여러 사원 다중 insert
	 */
	@PostMapping(value="/addAllEmp", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> insertAllEmployee(
			@RequestBody List<Map<String, Object>> empData	// JSON 배열 형태의 데이터를 받음	
	){ 
		try {
			
			for(Map<String, Object> emp : empData) { // 각 직원 데이터를 로그에 출력 
				log.info("empData : {}" , emp);
			}
				
			ServiceResult result = employeeService.createEmployee(empData);
			switch (result) {
			case OK:
				return ResponseEntity.ok().body(Collections.singletonMap("message", "데이터 등록 성공"));
				
				
			case FAIL:
				return ResponseEntity.badRequest().body(Collections.singletonMap("error", "데이터 등록 실패"));
				
			default:
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						   .body(Collections.singletonMap("error", "서버 오류 "));
			
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                     .body(Collections.singletonMap("error", "서버 오류 발생"));
		}
	}
	
	/**
	 * 사원 한명 insert
	 */
	@PostMapping("/addOneEmp")
	@ResponseBody
	public ResponseEntity<String> insertOneEmployee(
			@PathVariable("companyId") String companyId
			, @RequestBody EmployeeVO emp
	) {
		ServiceResult result = employeeService.createOneEmployee(emp);
		log.info("사원한명등록 result >>>> {}" ,result);
		switch (result) {
		case OK:
            return ResponseEntity.ok("등록 성공");
        case FAIL:
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록 실패");
        default:
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청");
	
	}	
	    	
	}
	
	/**
	 * 사원 수정 
	 */
	@PutMapping("/modOneEmp")
	@ResponseBody
	public ResponseEntity<String> updateOneEmployee(
			@PathVariable("companyId") String companyId
			,@Validated @RequestBody EmployeeVO emp
	){
		ServiceResult result = employeeService.modifyEmployee(emp);
		switch (result) {
		case OK:
            return ResponseEntity.ok("수정 성공");
        case FAIL:
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패");
        default:
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청");
		}
	
	}
	
	/*
	 	사원 퇴사처리 로직 
	 */
	@DeleteMapping("/removeOneEmp/{empId}")
	@ResponseBody
	public ResponseEntity<String> removeOneEmployee(
			@PathVariable("companyId") String companyId
			,@PathVariable("empId") String empId
	){
		log.info("사원번호 : {}",empId);
		ServiceResult result = employeeService.removeOneEmployee(empId);
		switch (result) {
		case OK:
            return ResponseEntity.ok("삭제 성공");
        case SERVERERROR:
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
        case PKDUPLICATED:
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PK 중복 오류");
        case FAIL:
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
        default:
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청");
		}
	
	}
	
	// 잠시 보류 
	@GetMapping("/vsDetail/{vstaCode}/{empId}")
	@ResponseBody
	public VacationStatusVO selectOneVacationStatusDetail(
			@PathVariable("companyId") String companyId
		  , @PathVariable("vstaCode") String vstaCode
		  , @PathVariable("empId") String empId
		  , HttpServletResponse resp
	) {
		if(!StringUtils.isBlank(empId)) {
	
			return employeeService.readOneVacationStatusDetail(vstaCode,empId);
		}
		return null;
		
	}
	
	@GetMapping("/readVstaCode")
	@ResponseBody
	public List<VacationStatusVO> selectVstaCodeList(
			@PathVariable("companyId") String companyId
	){
			return employeeService.readVstaCodeList();
	}
	
	@PutMapping("/modVacationStatus")
	@ResponseBody
	public ResponseEntity<String> updateVacationStatus(
			@PathVariable("companyId") String companyId
		  , @RequestBody VacationStatusVO va
	){	
		
		ServiceResult result = employeeService.modifyVacationStatus(va);
		switch (result) {
		case OK:
			return ResponseEntity.ok("휴가 추가부여 완료");
		case FAIL:
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("휴가 추가부여 실패");
		default:
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청 ");
		}
	}
	
	
	
	@GetMapping("/myDepart/{empId}")
	@ResponseBody
	public DepartmentVO selectMyDepartName(
			@PathVariable("companyId") String companyId
		  , @PathVariable("empId") String empId
	){
		if(empId != null ) {
			DepartmentVO myDepart = employeeService.readMyDepartName(empId);
			return myDepart;
		}
		return null;
	}
	
	
}	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

