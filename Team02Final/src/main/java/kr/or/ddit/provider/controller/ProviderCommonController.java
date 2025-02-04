package kr.or.ddit.provider.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.contract.vo.EmpCountVO;
import kr.or.ddit.organitree.vo.ContractVO;
import kr.or.ddit.provider.service.ProviderService;

@Controller
@RequestMapping("/provCommon")
public class ProviderCommonController {
	@Inject
	private ProviderService provService;
	
	@GetMapping("/getEmpcnt")
	@ResponseBody
	public List<EmpCountVO> selectEmpCountList(){
		return provService.readEmpCountList();
	}
	
	@GetMapping("/getContType")
	@ResponseBody
	public List<ContractVO> selectContractType(){
		return provService.readContractType();
	}
	
}
