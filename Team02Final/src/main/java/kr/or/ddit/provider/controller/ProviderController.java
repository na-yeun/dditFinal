package kr.or.ddit.provider.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.contract.vo.PaymentVO;
import kr.or.ddit.organitree.vo.ContractVO;
import kr.or.ddit.provider.service.ProviderService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/prov")
public class ProviderController {
	
	@Inject
	private ProviderService provService;
	
	@GetMapping("/main")
	public String getProvMain() {
		return "provider/main/provStat.jsp";
	}
	
	@GetMapping("/provFilterOption")
	public String getFilterOptionPage() {
		return "provider/common/filterOption.jsp";
	}
	
	// 계약 업체 결제정보 페이지 이동 
	@GetMapping("/contCompanyPayHistory")
	public String getCompanyPayHistoryPage() {
		return "provider/main/provContCompanyPayHistoryPage.jsp";
	}
	
	
	//==============================================================================================
	// 계약업체 통계 페이지 시작 
	
	
	// 스토리지별 계약 통계 
	@GetMapping("/storageContStatPage")
	public String getStorageStatPage() {
		return "provider/stat/storageContStatPage.jsp";
	}
	
	// 스토리지 계약 통계 초기 렌더링 
	@GetMapping("/getStorageContStat")
	@ResponseBody
	public ResponseEntity<?> selectStorageContStat(
			@RequestParam(required = false) String contractStart
		){
		List<ContractVO> storageContList = provService.readStorageContStat(contractStart);
		log.info("getStorageContStat : storageContList >>> {}" , storageContList);
		return ResponseEntity.ok(storageContList);
	}
	
	// 스토리지 계약 통계에서 하나 클릭 상세 조회 
	@GetMapping("/getStorageContCollapseStat")
	@ResponseBody
	public Map<String, Object> selectStorageContractCollapseStat(
			@RequestParam(required = true , name = "storageId") String storageId
		  , @RequestParam(required = false) String contractStart
		){
			
		if(StringUtils.isBlank(storageId)) {
			throw new IllegalArgumentException("storageId 값 누락");
		}
		List<Map<String, Object>> storageContList = provService.readStorageContractCollapseStat(storageId, contractStart);
		log.info("getStorageContCollapseStat : storageContList >>> {} ", storageContList);
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("contractType", storageContList.stream()
				.filter(map -> "업종".equals(map.get("CATEGORY")))
				.collect(Collectors.toList())
				);
		response.put("scale", storageContList.stream()
				.filter(map -> "업체 규모".equals(map.get("CATEGORY")))
				.collect(Collectors.toList())
				);
		return response;
				
	}
	
	
	
	
	// 업체규모 통계 페이지 이동 
	@GetMapping("/scaleContStatPage")
	public String getScaleContractStatPage() {
		return "provider/stat/scaleContStatPage.jsp";
	}
	
	// 업체규모 통계 초기렌더링
	@GetMapping("/getScaleContStat")
	public ResponseEntity<?> selectScaleContStat(
			@RequestParam(required = false) String contractStart
	){	
		List<ContractVO> scaleContList = provService.readScaleContStat(contractStart);
		log.info("selectScaleContStat :  scaleContList >>> {}" ,scaleContList);
		return ResponseEntity.ok(scaleContList);
	}
	
	// 업체규모 통계에서 하나 클릭 
	@GetMapping("/getScaleContCollapseStat")
	@ResponseBody
	public Map<String, Object> selectScaleContractCollapseStat(
			@RequestParam(required = true , name = "scaleId") String scaleId
		  , @RequestParam(required = false, value="contractStart") String contractStart 
			
	){
			List<Map<String, Object>> scaleContList = provService.readScaleContractCollapseStat(scaleId, contractStart);
			Map<String, Object> response = new HashMap<String, Object>();
			response.put("storage", scaleContList.stream()
		             .filter(map -> "스토리지 용량".equals(map.get("CATEGORY")))
		             .collect(Collectors.toList()));
			
			response.put("contractType", scaleContList.stream()
					.filter(map -> "업종".equals(map.get("CATEGORY")))
					.collect(Collectors.toList()));
			response.put("empCount", scaleContList.stream()
					.filter(map -> "사용 인원".equals(map.get("CATEGORY")))
					.collect(Collectors.toList()));
			return response;
	}
	
	
	// 월별 계약추세 페이지 이동 
		@GetMapping("/monthlyStatPage")
		public String getMonthlyContractStatPage() {
			return "provider/stat/monthlyContStatPage.jsp";
		}
		
	// 월별 계약 추세 통계 초기렌더링 
	@GetMapping("/getMonthlyContStat")
	@ResponseBody
	public ResponseEntity<?> selectMonthlyContractStat(
			@RequestParam(required = false) String contractStart
	){	
		log.info("getMonth : contStart >>> {}",contractStart);
		List<PaymentVO> monthContList = provService.readMonthlyContractStat(contractStart);
		log.info("monthContList >>> {}",monthContList);
			return ResponseEntity.ok(monthContList);
	}
	// 월별 계약 추세에서 하나 클릭 상세조회
	@GetMapping("/getMonthlyContCollapse")
	@ResponseBody
	public Map<String, Object> selectMonthlyContractCollapseStat(
			@RequestParam(name = "month") String month
	      , @RequestParam(required = false , value = "contractStart") String contractStart
	){
		List<Map<String, Object>> monthStat = provService.readMonthlyContractCollapseStat(month, contractStart);
		Map<String, Object> response = new HashMap<>();
		response.put("scale", monthStat.stream()
	             .filter(map -> "업체 규모".equals(map.get("CATEGORY")))
	             .collect(Collectors.toList()));
		
		response.put("contractType", monthStat.stream()
				.filter(map -> "업종".equals(map.get("CATEGORY")))
				.collect(Collectors.toList()));
		
		return response;
	}
	
	
	
	// 업종별 계약 분석 페이지 
	@GetMapping("/contTypeStatPage")
	public String getContractTypeStatPage() {
		return "provider/stat/contTypeStatPage.jsp";
	}
	
	// 업종 계약 분석 통계 초기 렌더링  
	@GetMapping("/getContTypeStat")
	@ResponseBody
	public ResponseEntity<?> selectContractType(
			@RequestParam(name = "contractStart" ,required = false) String contractStart 
	){	
		log.info("getContTypeStat : contractStart >>>> {}" ,contractStart);
		List<ContractVO> contTypeStatList = provService.readContractTypeStat(contractStart);
		log.info("getContTypeStat : contTypeStatList >>>> {}" ,contTypeStatList);
		return ResponseEntity.ok(contTypeStatList);
	}
	
	// 업종 계약 분석에서 바 하나 클릭 상세조회
	@GetMapping("/getContTypeCollapse")
	@ResponseBody
	public Map<String, Object> selectContractTypeCollapseStat(
			@RequestParam(required = false) String contractType
          , @RequestParam(required = false) String contractStart
	){
		
		log.info("getContTypeCollapse : contractType >>> {}" ,contractType);
		log.info("getContTypeCollapse : contractStart >>> {}" ,contractStart);
		String contType = contractType.trim();
		String start = contractStart.trim();
		
        List<Map<String, Object>> stats = provService.readContractTypeCollapseStat(contType, start);
        Map<String, Object> response = new HashMap<>();
        response.put("empCount", stats.stream()
                .filter(map -> "사용 인원".equals(map.get("CATEGORY")))
                .collect(Collectors.toList()));  

		response.put("storage", stats.stream()
		               .filter(map -> "용량".equals(map.get("CATEGORY")))
		               .collect(Collectors.toList()));  
		
		response.put("scale", stats.stream()
		             .filter(map -> "업체 규모".equals(map.get("CATEGORY")))
		             .collect(Collectors.toList()));
		
		// 응답 데이터 로그로 확인
		log.info("응답 데이터 확인: {}", response);
		
		return response;
	}
 		
	// 계약업체 통계 페이지 시작 끝 
	//==============================================================================================
	// 일단 보류 대상 
	// 통계 처리 
	@GetMapping("/getStatData")
	@ResponseBody
	public ResponseEntity<?> getStatData(
		@RequestParam(required = true) String statType 
	  , @RequestParam(required = false, value = "contractStart") String contractStart
		
	){	
		log.info("getStatData : statType >>> {}" , statType);
		
		switch (statType) {
		case "contType":
			List<ContractVO> contTypeStatList = provService.readContractTypeCountStat(contractStart);
			// 리스트 null 검증 
			if(contTypeStatList == null || contTypeStatList.isEmpty()) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류");
			}
			return ResponseEntity.ok(contTypeStatList);
			
		case "monthlyCont":	
			List<PaymentVO> monthlyContStatList = provService.readMonthlyContractCountStat(contractStart);
			if(monthlyContStatList == null || monthlyContStatList.isEmpty()) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류");
			}
			return ResponseEntity.ok(monthlyContStatList);
			
		case "contScale":
			List<ContractVO> contScaleStatList = provService.readScaleCountStat(contractStart);
			if(contScaleStatList == null || contScaleStatList.isEmpty()) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류");
			}
			return ResponseEntity.ok(contScaleStatList);
		
		case "contEmpcnt":
			List<ContractVO> contEmpcntStatList = provService.readEmpCountStat(contractStart);
			if(contEmpcntStatList == null || contEmpcntStatList.isEmpty()) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류");
			}
			return ResponseEntity.ok(contEmpcntStatList);
			
		case "contStorage":
			List<ContractVO> contStorageStatList = provService.readStorageCountStat(contractStart);
			if(contStorageStatList == null || contStorageStatList.isEmpty()) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류");
			}
			return ResponseEntity.ok(contStorageStatList);
			
		default:
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청");
		}
		
	}
	//==============================================================================================

		
		@GetMapping("/readOptionYears")
		@ResponseBody
		public List<ContractVO> selectOptionYearList(){
			return provService.readOptionYearsList();
		}
	
		@GetMapping("/todayTotal")
		@ResponseBody
		public PaymentVO selectTotalPaymentLastYear(String payDate) {
			return provService.readTotalPaymentLastYear(payDate);
		}
		
		@GetMapping("/thisYearTotal")
		@ResponseBody
		public PaymentVO selectTotalPaymentThisYear(String payDate) {
			return provService.readTotalPaymentThisYear(payDate);
		}
		
		
//		@GetMapping("/getContractStatData")
//	    public ResponseEntity<List<Map<String, Object>>> selectContractStatistics(
//	            @RequestParam(value="statType" ,required = false) String statType,
//	            @RequestParam(value="contractStart" , required = false) String contractStart) {
//	        List<Map<String, Object>> statistics = provService.readContractStatistics(statType, contractStart);
//	        return ResponseEntity.ok(statistics);
//	    }
		
		
		
}

