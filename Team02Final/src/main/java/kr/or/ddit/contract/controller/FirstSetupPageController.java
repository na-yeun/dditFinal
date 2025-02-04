package kr.or.ddit.contract.controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.contract.service.ContractService;
import kr.or.ddit.contract.vo.FirstSettingVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/setupPage")
public class FirstSetupPageController {
	
	@Inject
	private ContractService contractService;
	
	@PostMapping("/firstSet")
	public String goSetupPage(
			@RequestParam(required = true, value = "contractId") String contractId 
		  , @RequestParam(required = true, value = "contractCompany") String contractCompany
		  , Model model
	) { 
		
		model.addAttribute("contractId",contractId);
		model.addAttribute("contractCompany",contractCompany);
		return "setupPage";
		
	}
	
	// 뒤로가기 (오류있음)
	@PostMapping("/gobackSetup")
	@ResponseBody
	public Map<String, String> gobackToSetupPage(
		) {
	    Map<String, String> response = new HashMap<>();
	    response.put("redirectUrl", "firstSet");
	    return response;
	}

	
	@PostMapping("/previewSetupPage")
	public String goPreSetupPage(
			@RequestParam String attendStart,
	        @RequestParam String attendEnd,
	        @RequestParam String contractCompany,
			@ModelAttribute FirstSettingVO fSetting
			, Model model
	) { 	
			log.info("previewSetupPage : company >>>>>{}" , contractCompany);
			String firstAttend = attendStart + "-" + attendEnd;
			log.info("previewSetupPage : fSetting >>>>> {}" , fSetting);
			fSetting.setFirstAttend(firstAttend); // VO에 값 설정
			model.addAttribute("fSetting",fSetting);
			return "preSetupPage";
		
		
			
		
	}
	
	
	
	// 최종 완료 신청 
	@PostMapping("/firstSave")
	public String setupSave(
			  @RequestParam("contractCompany") String contractCompany
			, @ModelAttribute FirstSettingVO fSetting
			, Model model
			, RedirectAttributes redirectAttributes
	) { 
		if(StringUtils.isBlank(fSetting.getContractId())) {
			throw new IllegalArgumentException("계약업체 아이디 누락");
		}
		if(StringUtils.isBlank(fSetting.getFirstPosition())) {
			throw new IllegalArgumentException("초기 직급값 누락");
		}
		if(StringUtils.isBlank(fSetting.getFirstDepart())) {
			throw new IllegalArgumentException("초기 부서값 누락");
		}
		if(StringUtils.isBlank(fSetting.getFirstEmploy())) {
			throw new IllegalArgumentException("초기 휴가일수 누락");
		}
		if(StringUtils.isBlank(fSetting.getFirstAttend())) {
			throw new IllegalArgumentException("초기 출퇴근시간 누락");
		}
		if(fSetting.getUseElec().equals("Y")&&StringUtils.isBlank(fSetting.getFirstElec())) {
			throw new IllegalArgumentException("초기 전자결재값 누락");
		}
			
		log.info("fSetting >>> {}",fSetting);
		ServiceResult result = contractService.addFirstSetting(fSetting);
		log.info("firstSave : result >>> {} ", result);
		switch (result) {
		case OK:
			redirectAttributes.addFlashAttribute("contractCompany" , contractCompany);
			redirectAttributes.addFlashAttribute("fSetting",fSetting);
			return "redirect:firstSetsuccess";
		case FAIL:
			redirectAttributes.addFlashAttribute("fSetting",fSetting);
			return "redirect:firstSet";

		default:
            throw new IllegalStateException("예상치 못한 결과: " + result);
		}

		
	}
	@GetMapping("/firstSetsuccess")
	public String goSuccessPage(
		    @ModelAttribute("contractCompany") String contractCompany
		   ,@ModelAttribute("fSetting") FirstSettingVO fSetting

	) {
		log.info("success : contCompany >>> {} " , contractCompany);
		log.info("success : fSetting >>> {} " , fSetting);
		return "firstSetupSuccess";
	}
	
	
	@PostMapping("/finalURL")
	public String goLastURL(
			@RequestParam(required = true, value = "contractId") String contractId 
		  , @RequestParam(required = true, value = "contractCompany") String contractCompany
		  , @RequestParam(required = true, value = "contractStart") String contractStart
		  , Model model
	) {
		log.info("contractStart value: {}", contractStart);
		log.info("contractId value: {}", contractId);
		log.info("contractCompany value: {}", contractCompany);
	
		// 현재 날짜와 contractStart 비교
		// "yyyyMMdd" 형식 처리
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime startDateTime = LocalDate.parse(contractStart, formatter).atStartOfDay(); // 날짜 -> 날짜/시간 변환
        LocalDateTime now = LocalDateTime.now(); // 현재 시간 가져오기
	    
		log.info("startDateTime value: {}", startDateTime);
		log.info("now value: {}", now);
	    
	    // 남은시간 계산 
	    long remainingDays = ChronoUnit.DAYS.between(now.toLocalDate(), startDateTime);
	    Duration duration = Duration.between(now, startDateTime);
	    
	    log.info("remainingDays value: {}", remainingDays);
		log.info("duration value: {}", duration);

		
	    long hours = duration.toHours() %24;
	    long minutes = duration.toMinutes() % 60;
	    long seconds = duration.toMillis() /1000 % 60;
	    
	    log.info("hours value: {}", hours);
		log.info("minutes value: {}", minutes);
		log.info("seconds value: {}", seconds);
	    
	    
	    
	    if (remainingDays > 0) {
	        // 계약 시작일이 남아 있는 경우
	    	log.info("finalURL countDown >>> {} ", remainingDays);
	        model.addAttribute("remainingDays", remainingDays);
	        model.addAttribute("hours", hours);
	        model.addAttribute("minutes", minutes);
	        model.addAttribute("seconds", seconds);
	        model.addAttribute("contractId", contractId);
	        model.addAttribute("contractCompany", contractCompany);
	        model.addAttribute("contractStart", contractStart);
	        return "countdown"; // 남은 시간을 보여주는 페이지
	    } else {
	        // 계약 시작일이 오늘이거나 지난 경우
	    	System.out.println("aaaaa");
	        return "redirect:/a001/login"; // 바로 로그인 페이지로 이동
	    }
	}
	
	
}
