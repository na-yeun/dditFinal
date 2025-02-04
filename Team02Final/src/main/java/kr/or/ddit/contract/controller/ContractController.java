package kr.or.ddit.contract.controller;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.exception.PKNotFoundException;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.contract.service.ContractService;
import kr.or.ddit.contract.vo.EmpCountVO;
import kr.or.ddit.contract.vo.ScaleVO;
import kr.or.ddit.contract.vo.StorageVO;
import kr.or.ddit.employee.service.EmployeeService;
import kr.or.ddit.organitree.vo.ContractVO;
import kr.or.ddit.provider.vo.ProviderVO;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/contract")
public class ContractController {
	@Inject
	private ContractService service;
	@Inject
	private EmployeeService employeeService;
	
	// 그룹웨어 데모페이지 이동 
	@GetMapping("/all/groupWareDemo")
	public String goGroupDemo() {
		return "redirect:/resources/jsp/groupwareDemo.jsp";
	}
	
	// 신청양식 폼으로 보내는 컨트롤러 
	@GetMapping("/all/requestCont")
	public String selectOptionList(Model model){
		
		List<EmpCountVO> empCountList = service.readAllEmpCountList();
		List<StorageVO> storageList = service.readAllStorageList();
		List<ScaleVO> scaleList = service.readAllScaleList();
		
		model.addAttribute("empCountList",empCountList);
		model.addAttribute("storageList",storageList);
		model.addAttribute("scaleList",scaleList);
		return "requestForm";

	}
		
	// 인증번호 전송 버튼을 클릭하면 실행됨
			@PostMapping("/all/contractAuthCheck")
			@ResponseBody
			public ResponseEntity<String> sendSMS(
			        @RequestParam("contractTel") String contractTel,
					// 인증번호를 세션에 저장하기 위해
					HttpSession session
			){
				
				log.info("contAuthCheck contTel >>>> {} ",contractTel);
				if (contractTel == null || !contractTel.matches("^\\d{10,11}$")) {
			        return ResponseEntity.badRequest().body("전화번호는 숫자만 입력 가능하며 10~11자리여야 합니다.");
			    }
				if(StringUtils.isBlank(contractTel)) {
					// 검증 실패
					return ResponseEntity.badRequest().body("빈 칸 없이 전송해주세요.");
				}
				 
			
					// 검증 통과
					
					try {
						// 데이터베이스에 해당 정보로 등록된 정보가 있는지 확인
						// 없으면 exception, 있으면 try 안에 실행
						
						String verificationCode = employeeService.generateVerificationCode();
						String smsText = String.format("[Work2gether] 인증번호 [%s]를 입력해주세요. 사칭/전화사기에 주의하세요.", verificationCode);
						session.setAttribute("verificationCode", verificationCode);
						// log.info("==> ☎️☎️☎️☎️☎️☎️☎️☎️전송될 메세지 : {}", smsText);
						
						employeeService.sendOauthSMS(contractTel.trim(),smsText);
						
						// 성공적으로 응답 반환
						return ResponseEntity.ok("문자 전송 완료");
					} catch(PKNotFoundException e) {
						// 등록된 정보가 없을 때 exception 발생
						return ResponseEntity.badRequest().body("일치하는 정보가 없습니다.");
					}
					
				} 
			
				@PostMapping("/all/contractAuthCheck/checkTelAuth")
				@ResponseBody
				public ResponseEntity<String> authCodeCheck(
						// 클라이언트가 입력한 인증번호
						@RequestParam(value = "authCode", required = true) String authCode,
						// 세션에 저장된 인증번호를 꺼내오기 위해..
						HttpSession session
		//				Model model
				) {
					// 세션에 저장된 인증번호 꺼내오기
					String verificationCode = (String) session.getAttribute("verificationCode");
					
					log.info("check authCode >>> {}" , authCode);
					log.info("check verifi >>> {}",verificationCode);
					
					// 세션에 저장된 인증번호와 입력한 인증번호 비교
					if (StringUtils.isNotBlank(verificationCode) && verificationCode.equals(authCode)) {
						// 상태 ok로 리턴
						return ResponseEntity.ok().build();
					} else {
						return ResponseEntity.badRequest().body("인증번호가 일치하지 않습니다. 다시 입력해주세요.");
					}
				}
			
	
	
	// 미리보기 영수증으로 보내는 컨트롤러 
	@PostMapping("/all/previewReceipt")
	public String previewReceipt(
			@RequestParam("contractCompany") String contractCompany
		  , @RequestParam("contractName") String contractName
		  , @RequestParam("contractTel") String contractTel
		  , @RequestParam("contractEmail") String contractEmail
		  , @RequestParam("contractStart")@DateTimeFormat(iso = ISO.DATE)  LocalDate contractStart
		  , @RequestParam("contractEnd")@DateTimeFormat(iso = ISO.DATE) LocalDate contractEnd
		  , @RequestParam("contractType") String contractType
		  , @RequestParam("empCountId") String empCountData
		  , @RequestParam("storageId") String storageData
		  , @RequestParam("scaleId") String scaleData
		  , @RequestParam("contractAddr1") String contractAddr1
		  , @RequestParam("contractAddr2") String contractAddr2
		  , Model model
	) {
		

		
		log.info("form데이터 왔다 : {}", contractCompany);
		log.info("startdate >>> : {}" , contractStart);
		log.info("endDate >>> : {}" , contractEnd);
		
		// 신청양식에서 선택한 사용인원 아이디와 가격
		String[] pickEmpCount = empCountData.split(",");
		String empCountId = pickEmpCount[0];
		int empCountPrice = Integer.parseInt(pickEmpCount[1]);
		String empCount = pickEmpCount[2];
		log.info("선택한 사용인원 아이디 >>> :{}",empCountId);
		log.info("선택한 사용인원 가격 >>> :{}",empCountPrice);
		log.info("선택한 사용인원 이름 >>> : {}",empCount);
		
		model.addAttribute("empCountId",empCountId);
		model.addAttribute("empCountPrice",empCountPrice);
		model.addAttribute("empCount",empCount);
		
		
		
		// 신청양식에서 선택한 스토리지용량 아이디와 가격 
		String[] pickStorage = storageData.split(",");
		String storageId = pickStorage[0];
		int storagePrice = Integer.parseInt(pickStorage[1]);
		String storageSize = pickStorage[2];
		log.info("선택한 용량 가격 >>> :{}",storageId);
		log.info("선택한 용량 가격 >>> :{}",storagePrice);
		log.info("선택한 용량 이름 >>> :{}",storageSize);
		
		model.addAttribute("storageId",storageId);
		model.addAttribute("storagePrice",storagePrice);
		model.addAttribute("storageSize",storageSize);
		
		// 신청양식에서 선택한 업체규모 아이디와 가격
		String[] pickScale = scaleData.split(",");
		String scaleId = pickScale[0];
		int scalePrice = Integer.parseInt(pickScale[1]);
		String scaleSize = pickScale[2];
		log.info("선택한 업체규모 아이디 >>> :{}",scaleId);
		log.info("선택한 업체규모 가격 >>> :{}",scalePrice);
		log.info("선택한 업체규모 이름 >>> :{}",scaleSize);
		
		model.addAttribute("scaleId",scaleId);
		model.addAttribute("scalePrice",scalePrice);
		model.addAttribute("scaleSize",scaleSize);
		
		int totalPrice = empCountPrice + storagePrice + scalePrice;
		log.info("선택옵션 계산 가격 : {}",totalPrice);
		model.addAttribute("totalPrice",totalPrice);
		
		model.addAttribute("contractCompany",contractCompany); // 계약업체명 
		model.addAttribute("contractName",contractName);       // 계약업체 대표이름
		model.addAttribute("contractTel",contractTel);		   // 계약업체 대표 전화번호
		model.addAttribute("contractEmail",contractEmail);	   // 계약업체 대표 메일
		model.addAttribute("contractStart",contractStart);	   // 게약시작일
		model.addAttribute("contractEnd",contractEnd);		   // 계약종료일
		model.addAttribute("contractType",contractType);	   // 업종명 
		model.addAttribute("contractAddr1",contractAddr1);
		model.addAttribute("contractAddr2",contractAddr2);
		return "preReceipt";
	}
	
	@PostMapping("/all/addContReqForm")
	public String insertContractRequestForm(
			@ModelAttribute ContractVO contract
		  , @RequestParam("totalPrice") String totalPrice
		  , RedirectAttributes redirectAttributes
	) {
		
		ServiceResult result = service.addContractRequestForm(contract, totalPrice);
		switch (result) {
		case OK:
			redirectAttributes.addFlashAttribute("contract",contract);
			redirectAttributes.addFlashAttribute("totalPrice",totalPrice);
			
			log.info("addContReqForm Suc : totalPrice >>> {}" , totalPrice);
			log.info("addContReqForm Suc : contract >>> {}",contract);
			return "redirect:reqFormSuccess";
			
		case FAIL:
			redirectAttributes.addFlashAttribute("contract",contract);
			
			log.info("addContReqForm Fail : contract >>> {}", contract);
			return "redirect:contract";
		default:
			throw new IllegalStateException("예상치 못한 결과: " + result);
		}
		
	}
	
	@GetMapping("/all/reqFormSuccess")
	public String requestFormSuccess(
			@ModelAttribute ContractVO contract
			
			
			, RedirectAttributes redirectAttributes
	) {
		redirectAttributes.addFlashAttribute("contract",contract);
		return "requestFormSuccess";
		
	}
	
// =========================================================================================================================================================
	
	// 계약업체 목록 이동
	@GetMapping("/contCompanyList")
	public String contCompanyList(){
		return "provider/main/contractCompanyList.jsp";
	}
		
	@GetMapping("/readContINGCompanyList")
	@ResponseBody
	public Map<String, Object> selectContractingCompany(
				
			@RequestParam(required = false ,defaultValue = "1") int page
		  , @RequestParam(required = false) String searchWord
		  , @RequestParam(required = false) String type
		  , @RequestParam(required = false) String scaleSize
			
		){
		PaginationInfo<ContractVO> paging = new PaginationInfo<ContractVO>();
		paging.setCurrentPage(page);
		
		log.info("업체규모 : {}",scaleSize);
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("searchWord", searchWord);
		condition.put("type", type);
		condition.put("scaleSize", scaleSize);
		;		
		paging.setVariousCondition(condition);
		List<ContractVO> list = service.readContINGCompanyList(paging);
		
		// 결과 데이터 반환 
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("companyList", list);
		result.put("currentPage", paging.getCurrentPage());
		result.put("totalPage",paging.getTotalPage());
		result.put("startPage", paging.getStartPage());
		result.put("endPage",  Math.min(paging.getEndPage(), paging.getTotalPage()));
		result.put("totalRecord", paging.getTotalRecord());
		
		
		
		return result;
	}
	
	
	@GetMapping("/readScaleList")
	@ResponseBody
	public List<ScaleVO> selectScaleList(){
		return service.readAllScaleList();
	}
	
	
	@GetMapping("/downloadExcelContingCompanyList")
	public ResponseEntity<byte[]> downloadContractingCompanyList(
			
				@RequestParam(required = false) String searchWord
			  , @RequestParam(required = false) String type
			  , @RequestParam(required = false) String scaleSize
	){
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("searchWord", searchWord);
			condition.put("type", type);
			condition.put("scaleSize", scaleSize);
			
			List<ContractVO> list = service.readDownloadExcelContractingCompanyList(condition);
			try(Workbook workbook = new XSSFWorkbook()){
				Sheet sheet = workbook.createSheet("계약업체 리스트"); 
				
				Row headerRow = sheet.createRow(0);
				String[] headers = {
					"번호", "업체명", "업체 전화번호", "업체 이메일", "업종명", "사용인원"
					, "스토리지 용량", "업체규모", "계약시작일", "계약종료일", "남은 일수"
				};
				for(int i =0; i< headers.length; i++){
					Cell cell = headerRow.createCell(i);  // 각 열에 해당하는 셀 생성
					cell.setCellValue(headers[i]); 		  // 셀에 헤더 텍스트 설정 

					// 스타일 추가
					CellStyle style = workbook.createCellStyle(); // 셀 스타일  객체 생성
					Font font = workbook.createFont(); 
					font.setBold(true);
					style.setFont(font);
					style.setAlignment(HorizontalAlignment.CENTER); // 셀 내 텍스트 가운데 정렬
					cell.setCellStyle(style);

					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i,(sheet.getColumnWidth(i))+1024);
				}
				
				int dataNum = 1;
				int rowNum = 1;
				
				for(ContractVO data : list){
					Row row = sheet.createRow(rowNum++);

					row.createCell(0).setCellValue(dataNum++); // 새로운 변수 선언해서 초기값에 1 부여 ( = dataNum )
					row.createCell(1).setCellValue(data.getContractCompany());
					row.createCell(2).setCellValue(data.getContractTel());
					row.createCell(3).setCellValue(data.getContractEmail());
					row.createCell(4).setCellValue(data.getContractType());
					row.createCell(5).setCellValue(data.getEmpCount().getEmpCount());
					row.createCell(6).setCellValue(data.getStorage().getStorageSize());
					row.createCell(7).setCellValue(data.getScale().getScaleSize());
					row.createCell(8).setCellValue(data.getContractStart());
					row.createCell(9).setCellValue(data.getContractEnd());
					row.createCell(10).setCellValue(data.getRemainDays() < 0 ? 0 : data.getRemainDays());
					
						
				}
					
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					workbook.write(out);
					byte[] excelData = out.toByteArray();
					
					String filename = URLEncoder.encode("계약업체 리스트.xlsx" , StandardCharsets.UTF_8.toString()).replace("+", " ");
					return ResponseEntity.ok()
							             .contentType(MediaType.APPLICATION_OCTET_STREAM)
							             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" +filename)
							             .body(excelData);
					
			}catch(Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
	}
		
		@GetMapping("/readOneCompanyDetail/{contractId}")
		@ResponseBody
		public ContractVO selectOneCompanyDetail(
				@PathVariable("contractId") String contractId
		){
			log.info("계약업체 아이디 : {}" , contractId);
			return service.readOneCompanyDetail(contractId);
		}
		
		@PutMapping("/modContractingCompanyInfo")
		@ResponseBody
		public ResponseEntity<String> updateContractingCompanyInfo(
				@Validated @RequestBody ContractVO contract
		) {
			ServiceResult result = service.modifyContractingCompanyInfo(contract);
			switch (result) {
			case OK:
				return ResponseEntity.ok().body("수정 성공");
			case FAIL : 	
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러");
			default:
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청");
			}
			
		}
	
// =========================================================================================================================================================
	// 승인 대기업체 목록 
		
		@GetMapping("/contractWait")
		public String goWaitApproval() {
			return "provider/main/contractWait.jsp";
		}
		
		
		@GetMapping("/readWaitCompanyList")
		@ResponseBody
		public Map<String, Object> selectWaitCompanyList(
				
				@RequestParam(required = false , defaultValue = "1") int page
			  , @RequestParam(required = false) String searchWord
				
		){
			PaginationInfo<ContractVO> paging = new PaginationInfo<ContractVO>();
			paging.setCurrentPage(page);
			
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("searchWord", searchWord);
			paging.setVariousCondition(condition); 
			List<ContractVO> list = service.readWaitCompanyList(paging);
			
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("waitCompanyList", list);
			result.put("currentPage", paging.getCurrentPage());
			result.put("totalPage",paging.getTotalPage());
			result.put("startPage", paging.getStartPage());
			result.put("endPage",  Math.min(paging.getEndPage(), paging.getTotalPage()));
			result.put("totalRecord", paging.getTotalRecord());
			
			
			return result;
		}
	
		@GetMapping("/readOneWaitCompany/{contractId}")
		@ResponseBody
		public ContractVO selectOneWaitCompanyDetail(
				@PathVariable("contractId") String contractId
		) {
			log.info("계약업체 아이디 왔니? >>>> {} ",contractId);
			return service.readOneWaitCompanyDetail(contractId);		
		}
		
		@PutMapping("/modStatusReject")
		@ResponseBody
		public ResponseEntity<String> updateWaitCompanyStatusReject(
				@RequestBody ContractVO contract
				, Authentication authentication
		){	
			
			// 클라이언트에서 보낸 요청 검증 
			if(contract.getContractEmail() == null || contract.getContractId() == null
			   || contract.getContractReject() == null || contract.getContractCompany() == null	
			  ) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("contract 값 누락");
			}
			
			AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
			AccountVO account = principal.getAccount();
			ProviderVO provider = (ProviderVO) account;
			String provId = provider.getProvId();
			contract.setProvId(provId);
			
			
			log.info("MSR : principal >>>> {} ",principal);
			log.info("MSR : account >>>> {} ",account);
			log.info("MSR : provider >>>> {} ",provider);
			log.info("MSR : provId >>>> {} ",provId);
			
			
			ServiceResult result = service.modifyWaitCompanyStatusReject(contract);
			switch (result) {
			case OK:
					return ResponseEntity.ok("반려 성공");
			case FAIL:
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("반려 실패");
			default:
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청");
			}
			
		}
		
		@PutMapping("/modStatusOK")
		@ResponseBody
		public ResponseEntity<String> updateWaitCompanyStatusOK(
				@RequestBody ContractVO contract
				, Authentication authentication
		){
			if(contract.getContractId() == null || contract.getContractEmail() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("contract 값 누락");
			}
			
			AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
			AccountVO account = principal.getAccount();
			ProviderVO provider = (ProviderVO) account;
			String provId = provider.getProvId();
			contract.setProvId(provId);
			
			
			log.info("MSO : principal >>>> {} ",principal);
			log.info("MSO : account >>>> {} ",account);
			log.info("MSO : provider >>>> {} ",provider);
			log.info("MSO : provId >>>> {} ",provId);
			
			ServiceResult result = service.modifyWaitCompanyStatusOK(contract);
			switch (result) {
			case OK:
					return ResponseEntity.ok("승인 성공");
			case FAIL:
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("승인 실패");
			default:
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청");
			}
		}
	
	
// =========================================================================================================================================================
	// 세부 세팅 대기업체 목록 
		@GetMapping("/firstSettingList")
		public String goFirstSettingList() {
			return "provider/main/firstSettingList.jsp";
		}
		
		@GetMapping("/readFSettingList")
		@ResponseBody
		public Map<String, Object> selectWaitFirstSettingList(
				@RequestParam(required = false, defaultValue = "1") int page,
				@RequestParam(required = false) String searchWord) {
			PaginationInfo<ContractVO> paging = new PaginationInfo<ContractVO>();
			paging.setCurrentPage(page);

			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("searchWord", searchWord);
			paging.setVariousCondition(condition);
			List<ContractVO> list = service.readWaitFirstSettingList(paging);

			Map<String, Object> result = new HashMap<String, Object>();
			result.put("waitSettingCompanyList", list);
			result.put("currentPage", paging.getCurrentPage());
			result.put("totalPage", paging.getTotalPage());
			result.put("startPage", paging.getStartPage());
			result.put("endPage", Math.min(paging.getEndPage(), paging.getTotalPage()));
			result.put("totalRecord", paging.getTotalRecord());

			return result;
		}
		
		
	// 세부 세팅 대기업체 목록중 한 업체 상세조회
		@GetMapping("/readOneFSetting/{contractId}")
		@ResponseBody
		public ContractVO selectOneWaitFirstSetting(
				@PathVariable("contractId") String contractId 
		){
			
			return service.readOneFSettingDetail(contractId);
			
		}
		
		@PutMapping("/modifyOneBucketAndStatus")
		@ResponseBody
		public ResponseEntity<?> updateBucketAndStatus(
			
			   @RequestBody ContractVO contract
		) { 
			
			
			log.info("updateBAS >>> {}",contract);
			ServiceResult result = service.modifyBucketAndStatus(contract);
			switch (result) {
			case OK:
				return ResponseEntity.ok("최종 반영 완료");
			case FAIL: 
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("반영 실패");
			default:
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청");
			}
		}
		
		
		
		
		
}
