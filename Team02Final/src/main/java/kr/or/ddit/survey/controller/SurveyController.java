package kr.or.ddit.survey.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.paging.SimpleCondition;
import kr.or.ddit.commons.paging.renderer.DefaultPaginationRenderer;
import kr.or.ddit.commons.paging.renderer.PaginationRenderer;
import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.security.AccountVOWrapper;
import kr.or.ddit.survey.service.SurveyResultService;
import kr.or.ddit.survey.service.SurveyService;
import kr.or.ddit.survey.vo.SurveyBoardVO;
import kr.or.ddit.survey.vo.SurveyResultVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/survey")
public class SurveyController {
	@Inject
	private SurveyService boardService;
	@Inject
	private SurveyResultService resultService;
	
	
	// 설문조사 list 조회
	@GetMapping
	public String getSurveyList(
			Authentication authentication
			, @ModelAttribute("condition") SimpleCondition simpleCondition
			, Optional<Integer> page
			, Model model
			, @PathVariable("companyId") String companyId
	) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		
		EmployeeVO myEmp = (EmployeeVO)account;
		
		
		PaginationInfo paging = new PaginationInfo();
		paging.setCurrentPage(page.orElse(1));
		paging.setSimpleCondition(simpleCondition);
		
		Map<String, Object> variousCondition = new HashMap<>();
		variousCondition.put("empId", myEmp.getEmpId());
		paging.setVariousCondition(variousCondition);
		
	 	List<SurveyBoardVO> surveyBoardList = boardService.readSurveyBoardList(paging);
		
		PaginationRenderer renderer = new DefaultPaginationRenderer();
		String pagingHtml = renderer.renderPagination(paging, "fnPaging");
		log.info("pagingHtml : {}",pagingHtml);
		model.addAttribute("list", surveyBoardList);
		model.addAttribute("pagingHtml", pagingHtml);
		
		return "survey/surveyList";
	}
	
	// 설문조사 한 개 상세조회
	@GetMapping("{surveyId}")
	public String getSurveyDetail(
			Authentication authentication
			, @PathVariable("surveyId") String surveyId
			, @PathVariable("companyId") String companyId
			, Model model
			, RedirectAttributes redirectAttributes
	) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		
		EmployeeVO myEmp = (EmployeeVO)account;
		
		SurveyBoardVO detail = boardService.readSurveyBoardDetail(surveyId);
		
		if(detail==null) {
			return "redirect:/"+companyId+"/survey";
		} else {
			model.addAttribute("detail", detail);
			
			boolean isAble = resultService.readSurveyExist(myEmp.getEmpId(), surveyId);
			model.addAttribute("isAble", isAble);
			
			
			return detail.getDetailUrl();
		}
	}
	
	// 설문조사 결과 조회
	@GetMapping("{surveyId}/result")
	@ResponseBody
	public Map<String, Map<String,Object>> getResult(
			@PathVariable("surveyId") String surveyId
	){
		Map<String, Map<String,Object>> resultMap = resultService.readSurveyResultList(surveyId);
		return resultMap;
		
	}
	
	// 설문조사 참여
	@PostMapping("{surveyId}")
	public ResponseEntity postSurveyResult(
			Authentication authentication
			, @PathVariable("surveyId") String surveyId
			, @Validated(InsertGroup.class) @RequestBody List<SurveyResultVO> resultList
			, BindingResult errors
			, Model model
	) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		
		EmployeeVO myEmp = (EmployeeVO)account;
		
		if(errors.hasErrors()) {
			// 검증 실패
			return ResponseEntity.badRequest().body("필수항목 누락");
			
		} else {
			// 검증 성공
			ServiceResult serviceResult = null;
			for(SurveyResultVO result : resultList) {
				result.setEmpId(myEmp.getEmpId());
				result.setSboardNo(surveyId);
			}
			serviceResult = resultService.createSurveyResult(resultList);
			
			if(serviceResult==ServiceResult.OK) {
				return ResponseEntity.ok().body("성공");
			} else if (serviceResult==ServiceResult.PKDUPLICATED){
				return ResponseEntity.badRequest().body("실패");
			} else {
				return ResponseEntity.internalServerError().body("실패");
			}
		}
		
		
		
	}
}
