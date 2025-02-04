package kr.or.ddit.commons.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.validate.EditGroup;
import kr.or.ddit.employee.service.EmployeeService;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.project.service.ProjectService;
import kr.or.ddit.project.vo.ProjectVO;
import kr.or.ddit.security.AccountVOWrapper;
import kr.or.ddit.todolist.service.ToDoListService;
import kr.or.ddit.todolist.vo.ToDoListVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/main")
public class IndexController{
	// index도 보호해버리면 여기에서 이미 로그인 되어있는 상태여야함
	@Inject
	private ToDoListService toDoListService;
	@Inject
	private EmployeeService employeeService;
	@Autowired
	private ProjectService projectService;
	
	@GetMapping
	public String index(
			Authentication authentication
			, Model model
			, HttpSession session
	) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		
		EmployeeVO myEmp = (EmployeeVO)account;
		
		Map<String, Object> resultMap = new HashMap<>();
		
		// 캘린더 영역
		
        
        // to do list
        List<ToDoListVO> toDoListList = toDoListService.readToDoListList(myEmp.getEmpId());
        resultMap.put("toDoListList", toDoListList);
        
        // 금일 팀 휴가자
        String myDepartCode = myEmp.getDepartCode();
        
        List<EmployeeVO> vacationList = employeeService.readVacationEmployeeList(myDepartCode);
        resultMap.put("vacationList", vacationList);
        
        // 나의 프로젝트
        List<ProjectVO> projectList = projectService.readProjectList(myEmp.getEmpId());
        resultMap.put("projectList", projectList);
        
        model.addAttribute("mainPageData", resultMap);
		return "/index";
	

	}
	
	@PutMapping("comment")
	public ResponseEntity putMyComment(
			Authentication authentication
			, @Validated(EditGroup.class) @RequestBody EmployeeVO emp
			, BindingResult errors
			, Model model
			, HttpSession session
	) {
		if(errors.hasErrors()) {
			return ResponseEntity.badRequest().body("comment 누락");			
		} else {
			AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
			AccountVO account = principal.getAccount();
			
			EmployeeVO myEmp = (EmployeeVO)account;
			
			emp.setEmpMail(myEmp.getEmpMail());
			emp.setEmpPass(myEmp.getEmpPass());
			emp.setEmpId(myEmp.getEmpId());
			
			ServiceResult result = employeeService.modifyMyEmployee(emp);
				
			if(result==ServiceResult.OK) {
				return ResponseEntity.ok("성공");
			} else {
				return ResponseEntity.internalServerError().body("서버 오류");
			}
		}
	}
}

