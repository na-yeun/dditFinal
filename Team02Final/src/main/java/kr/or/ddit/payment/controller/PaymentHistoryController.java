package kr.or.ddit.payment.controller;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.swing.text.NumberFormatter;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.paging.renderer.DefaultPaginationRenderer;
import kr.or.ddit.commons.paging.renderer.PaginationRenderer;
import kr.or.ddit.contract.vo.PaymentVO;
import kr.or.ddit.organitree.vo.ContractVO;
import kr.or.ddit.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/payHistory")
public class PaymentHistoryController {
	
	@Inject
	private PaymentService paymentService;
	
	@GetMapping
	public String payHistory(
			@PathVariable String companyId
			, Optional<Integer> page
			, Model model
	) {
			
		PaginationInfo paging = new PaginationInfo();
		paging.setCurrentPage(page.orElse(1));
		Map<String, Object> variousCondition = new HashMap<>();
		// 옵션에서 사용할 various condition 들어갈 자리 (아직 미정)
		variousCondition.put("companyId", companyId);
		
		
		
		paging.setVariousCondition(variousCondition);
	    List<PaymentVO> payHistorylist = paymentService.readMyCompanyPayHistory(paging);

	   
	    
	    PaginationRenderer renderer = new DefaultPaginationRenderer();
		String pagingHtml = renderer.renderPagination(paging, "fnPaging");
		
		log.info("payHistorylist >>> {}",payHistorylist);
		
		model.addAttribute("list", payHistorylist);
		model.addAttribute("pagingHtml", pagingHtml);
		model.addAttribute("variousCondition", variousCondition);
		
		
		return "payment/payHistory";
	}
	
	@GetMapping("/myCompPayDetail/{contractId}")
	@ResponseBody
	public ResponseEntity<?> selectMyCompanyPayDetail(
			  @PathVariable("companyId") String companyId
			, @PathVariable("contractId") String contractId
	) {
		ContractVO contract = paymentService.readMyCompanyPayDetail(contractId);
		return ResponseEntity.ok(contract);
	}
	
	
	
}
