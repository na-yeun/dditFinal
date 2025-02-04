package kr.or.ddit.project.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
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
public class ProjectInsertController {
	public static final String MODELNAME = "newProject";
	
	@Autowired
	private ProjectService service;

	@ModelAttribute(MODELNAME) 
	public ProjectVO project() {
		return new ProjectVO();
	}
	
	@PostMapping("/new")
	@ResponseBody
	public ResponseEntity<?> createProject(
	    @PathVariable("companyId") String companyId,
	    @RequestBody ProjectVO project, // JSON 데이터를 받기 위해 @RequestBody 추가
	    Authentication authentication
	) {
	    AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
	    AccountVO account = principal.getAccount();

	    EmployeeVO myEmp = (EmployeeVO) account;
	    project.setProjLastup(myEmp.getEmpId()); // EmployeeVO의 empId 필드 값 설정
	    ServiceResult result = service.createProject(project);
	    if(result.equals(ServiceResult.OK)) {
	        System.out.println("Project creation successful");
	        List<ProjectMemberVO> list = new ArrayList<>();
	        ProjectMemberVO member = new ProjectMemberVO();
	        String projId = service.readProjectNoProjId(project);
	        member.setProjId(projId);
	        member.setEmpId(myEmp.getEmpId());
	        member.setProjectRolenm("담당자");
	        member.setJoinDate(project.getProjSdate());
	        member.setLeaveDate(project.getProjEdate());
	        list.add(member);
	        ServiceResult memberResult = service.insertProjectMember(list);
	        if(memberResult.equals(ServiceResult.OK)) {
	            System.out.println("Member insertion successful");
	            return ResponseEntity.ok("성공");
	        } else {
	            System.out.println("Member insertion failed");
	            return ResponseEntity.badRequest().build();
	        }
	    } else {
	        System.out.println("Project creation failed");
	        return ResponseEntity.badRequest().build();
	    }

	}

	
	@PostMapping("/{projId}/addMembers")
	public ResponseEntity<?> insertProjectMember(
	        @RequestBody Map<String, List<ProjectMemberVO>> requestBody,
	        @PathVariable("projId") String projId) {
	    
	    // Map에서 members 리스트 추출
	    List<ProjectMemberVO> projectMemberList = requestBody.get("members");
	    
	    for (ProjectMemberVO member : projectMemberList) {
	        member.setProjId(projId);
	    }
	    
	    service.insertProjectMember(projectMemberList);
	    
	    return ResponseEntity.ok().build();
	}
	
	
	@PostMapping("/{projId}/addTask")
	public ResponseEntity<?> insertProjectTask(
			@PathVariable("projId") String projId,
			ProjectTaskVO projectTaskVO,
			Authentication authentication
			){
        AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
        AccountVO account = principal.getAccount();

        EmployeeVO myEmp = (EmployeeVO)account;
		log.info("{}", projectTaskVO);
		ServiceResult result = service.insertProjectTask(projectTaskVO);
		if(result.equals(ServiceResult.OK)){
			ProjectTaskVO projectTask = service.readTaskId(projectTaskVO);
			TaskHistoryVO taskHistoryVO = new TaskHistoryVO();
			taskHistoryVO.setEmpId(myEmp.getEmpId());
			taskHistoryVO.setTaskId(projectTask.getTaskId());
			taskHistoryVO.setTaskMethod("생성");
			taskHistoryVO.setTaskDate(LocalDateTime.now());
			taskHistoryVO.setTaskTitle(projectTaskVO.getTaskNm());
			taskHistoryVO.setProjId(projId);
			ServiceResult taskResult = service.insertTaskHistory(taskHistoryVO);
			if(taskResult.equals(ServiceResult.OK)) {
				return ResponseEntity.ok().build();
			}else {
				return ResponseEntity.badRequest().build();
			}
		}else {
			return ResponseEntity.badRequest().build();
		}
		
	}
	
	
}
