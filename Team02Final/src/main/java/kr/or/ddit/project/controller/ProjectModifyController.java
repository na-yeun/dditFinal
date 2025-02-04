package kr.or.ddit.project.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import kr.or.ddit.commons.exception.BoardException;
import kr.or.ddit.commons.validate.UpdateGroup;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.project.service.ProjectService;
import kr.or.ddit.project.vo.ProjectMemberVO;
import kr.or.ddit.project.vo.ProjectTaskVO;
import kr.or.ddit.project.vo.ProjectVO;
import kr.or.ddit.project.vo.TaskHistoryVO;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/project")
public class ProjectModifyController {
	
	public static final String MODELNAME = "targetProject";
	
	
	@Autowired
	private ProjectService service;
	
	
	@PostMapping("/{projId}/update")
	@ResponseBody
	public ResponseEntity<?> updateProject(
	    @RequestBody ProjectVO project,
	    @PathVariable("companyId") String companyId,
	    @PathVariable("projId") String projId,
	    Authentication authentication
	) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO myEmp = (EmployeeVO) account;
		project.setProjLastup(myEmp.getEmpId());
		project.setProjId(projId);
		ServiceResult result = service.modifyProject(project);
		if(result.equals(ServiceResult.OK)) {
			return ResponseEntity.ok("성공");
		}else {
			return ResponseEntity.badRequest().build();
		}
		
	}
	
	@DeleteMapping("/{projId}")
	public ResponseEntity<?> deleteProject(
			@PathVariable("projId") String projId,
			Model model) {
		
		ServiceResult result = service.removeProject(projId);
		
		if(result.equals(ServiceResult.OK)) {
			return ResponseEntity.ok().build();
		}else {
			return ResponseEntity.badRequest().build();
		}
	}
	
	@PostMapping("/{projId}/updateTask")
	public ResponseEntity<?> updateTask(
	        @PathVariable("projId") String projId,
	        @RequestParam Map<String, String> formData,
	        Authentication authentication
	) {
        AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
        AccountVO account = principal.getAccount();

        EmployeeVO myEmp = (EmployeeVO)account;
		
	    // formData를 VO로 변환
	    ProjectTaskVO projectTaskVO = new ProjectTaskVO();
	    projectTaskVO.setTaskId(formData.get("taskId"));
	    projectTaskVO.setTaskNm(formData.get("taskNm"));
	    projectTaskVO.setTaskContent(formData.get("taskContent"));

	    // 문자열을 LocalDate로 변환
	    try {
	        projectTaskVO.setTaskSdate(LocalDate.parse(formData.get("taskSdate")));
	        projectTaskVO.setTaskEdate(LocalDate.parse(formData.get("taskEdate")));
	    } catch (DateTimeParseException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format");
	    }

	    projectTaskVO.setTaskProgress(Long.parseLong(formData.get("taskProgress")));
	    projectTaskVO.setTaskColor(formData.get("taskColor"));
	    projectTaskVO.setTaskStatus(formData.get("taskStatus"));
	    projectTaskVO.setProjectMemberid(formData.get("projectMemberid") != null ? formData.get("projectMemberid") : null);
	    projectTaskVO.setProjId(projId);

	    // Service 호출
	    ServiceResult result = service.updateTask(projectTaskVO);

	    if (result.equals(ServiceResult.OK)) {
	    	TaskHistoryVO taskHistoryVO = new TaskHistoryVO();
	    	taskHistoryVO.setEmpId(myEmp.getEmpId());
	    	taskHistoryVO.setTaskId(formData.get("taskId"));
	    	taskHistoryVO.setTaskMethod("수정");
	    	taskHistoryVO.setTaskDate(LocalDateTime.now());
	    	taskHistoryVO.setTaskTitle(formData.get("taskNm"));
	    	taskHistoryVO.setProjId(projId);
	    	ServiceResult historyResult = service.updateTaskHistory(taskHistoryVO);
	    	
	    	if(historyResult.equals(ServiceResult.OK)) {
	    		return ResponseEntity.ok("성공");
	    	}else {
	    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("실패");
	    	}
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("실패");
	    }
	}

	@DeleteMapping("/{projId}/{taskId}")
	public ResponseEntity<?> deleteTask(
			@PathVariable String projId
			, @PathVariable String taskId
			, Authentication authentication
			) {
        AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
        AccountVO account = principal.getAccount();

        EmployeeVO myEmp = (EmployeeVO)account;
        
        ProjectTaskVO taskVO = service.readProjectTask(projId, taskId);
    	TaskHistoryVO taskHistoryVO = new TaskHistoryVO();
    	taskHistoryVO.setEmpId(myEmp.getEmpId());
    	taskHistoryVO.setTaskId(taskId);
    	taskHistoryVO.setTaskMethod("삭제");
    	taskHistoryVO.setTaskDate(LocalDateTime.now());
    	taskHistoryVO.setTaskTitle(taskVO.getTaskNm());
    	taskHistoryVO.setProjId(projId);
    	ServiceResult historyResult = service.deleteTaskHistory(taskHistoryVO);
		if(historyResult.equals(ServiceResult.OK)) {
			ServiceResult result = service.deleteTask(taskId);
			ServiceResult childResult = service.deletechildTask(taskId);
			if(result.equals(ServiceResult.OK) || childResult.equals(ServiceResult.OK)) {
					return ResponseEntity.ok("성공");
	    	}else {
	    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("실패");					
	    	}
		}else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("실패");
		}
	}
	
	@PutMapping("/projectMember/{projectMemberid}")
	@ResponseBody
	public ResponseEntity<?> updateProjectMember(
	        @PathVariable("projectMemberid") String projectMemberid, // 경로 변수와 일치시킴
	        @RequestBody ProjectMemberVO projectMemberVO) {
	    projectMemberVO.setProjectMemberid(projectMemberid); // 경로 변수로 받은 값을 VO에 설정
	    ServiceResult result = service.modifyProjectMember(projectMemberVO);

	    if (result.equals(ServiceResult.OK)) {
	        return ResponseEntity.ok("성공");
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("실패");
	    }
	}

	@DeleteMapping("/projectMember/{projectMemberid}")
	@ResponseBody
	public ResponseEntity<?> deleteProjectMember(@PathVariable("projectMemberid") String projectMemberid){
	    ServiceResult result = service.removeProjectMember(projectMemberid);

	    if (result.equals(ServiceResult.OK)) {
	        return ResponseEntity.ok("성공");
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("실패");
	    }
	}
	
}
