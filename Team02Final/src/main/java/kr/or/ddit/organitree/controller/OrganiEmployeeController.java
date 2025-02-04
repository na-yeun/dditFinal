package kr.or.ddit.organitree.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.organitree.service.OrganiTreeService;

@Controller
@RequestMapping("/{companyId}/organiEmployee")
public class OrganiEmployeeController {
	
	@Inject
	private OrganiTreeService service;
	
	
	
	@GetMapping("{departCode}")
	@ResponseBody
	public List<EmployeeVO> SelectOneOrganiList(
			  @PathVariable("companyId") String companyId
			, @PathVariable("departCode") String departCode 
			
	) {
		List<EmployeeVO> list = service.readOneOrganiList(departCode);
		return list;
	}
		
	@GetMapping("/detail/{empId}")
	@ResponseBody
	public EmployeeVO selectOneEmployee(
			  @PathVariable("companyId") String companyId
			, @PathVariable("empId") String empId
	) {
		EmployeeVO employee = service.readOneEmployeeDetail(empId);
		return employee;
	}
	
	@GetMapping("/search/{empName}")
	@ResponseBody
	public List<EmployeeVO> searchOneEmployee(
			  @PathVariable("companyId") String companyId
			, @PathVariable("empName") String empName
				
	) {
		List<EmployeeVO> searchList = service.searchOneEmployee(empName);		
		return searchList;
		
	}
	
	
}
