package kr.or.ddit.organitree.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.paging.SimpleCondition;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.organitree.service.OrganiTreeService;
import kr.or.ddit.organitree.vo.DepartmentVO;

@RequestMapping("/{companyId}/organiList")
@Controller
public class OrganiReadController {
	
	@Inject
	private OrganiTreeService service;
	
	
	@GetMapping
	public String organiList(
			@PathVariable("companyId") String companyId  
			, HttpSession session		 
			, Model model) {
// 주석처리는 다음에.
//		String sessionCompanyId = (String) session.getAttribute("companyId");
		
//		if(companyId.equals(sessionCompanyId)) {		
			
			
			
			List<DepartmentVO> list = service.readOrganiList();
			model.addAttribute("organiList", list);
			return "organi/organiTree";
//		}
		
//		return "redirect:/login";
		
	}
	
	
}
