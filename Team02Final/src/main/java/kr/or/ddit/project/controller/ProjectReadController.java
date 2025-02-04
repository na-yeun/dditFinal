
package kr.or.ddit.project.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.project.service.ProjectService;
import kr.or.ddit.project.vo.ProjectMemberVO;
import kr.or.ddit.project.vo.ProjectTaskDTO;
import kr.or.ddit.project.vo.ProjectTaskVO;
import kr.or.ddit.project.vo.ProjectVO;
import kr.or.ddit.project.vo.TaskHistoryVO;
import kr.or.ddit.security.AccountVOWrapper;
import kr.or.ddit.teamHistory.service.TeamHistoryService;
import kr.or.ddit.teamHistory.vo.TeamHistoryVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/project")
public class ProjectReadController {

	@Autowired
	private ProjectService projService;

	@Autowired
	private TeamHistoryService teamService;
	
	// 페이징 아직 안함
	@GetMapping
	public String getProjectList(Authentication authentication, Model model) {
	    AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
	    AccountVO account = principal.getAccount();

	    EmployeeVO myEmp = (EmployeeVO) account;
		
	    List<ProjectVO> list = projService.readProjectList(myEmp.getEmpId());
	    List<TeamHistoryVO> teamList = teamService.readTeamHistoryList();
	    
	    list.forEach(project -> {
	        if (project.getProjSdate() != null && project.getProjEdate() != null) {
	            LocalDate today = LocalDate.now();
	            LocalDate startDate = project.getProjSdate();
	            LocalDate endDate = project.getProjEdate();

	            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
	            long elapsedDays = ChronoUnit.DAYS.between(startDate, today);

	            if (totalDays > 0) {
	                int progress = (int) ((double) elapsedDays / totalDays * 100);
	                project.setCalculateProgress(Math.max(0, Math.min(progress, 100))); // 0~100 범위 제한
	            } else {
	                project.setCalculateProgress(0); // 총 기간이 0일 경우 0%
	            }
	        } else {
	            project.setCalculateProgress(0); // 시작일/종료일이 없을 경우 0%
	        }
	    });
	    
	    // Team ID로 그룹화
	    Map<String, List<TeamHistoryVO>> teamGrouped = teamList.stream()
	        .collect(Collectors.groupingBy(TeamHistoryVO::getTeamId));

	    model.addAttribute("project", list);
	    model.addAttribute("teamGrouped", teamGrouped);
	    return "project/projectList";
	}
	
	@PostMapping("/{projId}")
	@ResponseBody
	public ProjectVO getProject(@PathVariable("projId") String projId) {
		return projService.readProject(projId);
	}

	@GetMapping("/apps")
	@ResponseBody
	public List<ProjectVO> getProjectListApps(Authentication authentication) {
	    AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
	    AccountVO account = principal.getAccount();

	    EmployeeVO myEmp = (EmployeeVO) account;
	    
	    List<ProjectVO> list = projService.readProjectList(myEmp.getEmpId());
	    
	    list.forEach(project -> {
	        if (project.getProjSdate() != null && project.getProjEdate() != null) {
	            LocalDate today = LocalDate.now();
	            LocalDate startDate = project.getProjSdate();
	            LocalDate endDate = project.getProjEdate();

	            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
	            long elapsedDays = ChronoUnit.DAYS.between(startDate, today);

	            if (totalDays > 0) {
	                int progress = (int) ((double) elapsedDays / totalDays * 100);
	                project.setCalculateProgress(Math.max(0, Math.min(progress, 100))); // 0~100 범위 제한
	            } else {
	                project.setCalculateProgress(0); // 총 기간이 0일 경우 0%
	            }
	        } else {
	            project.setCalculateProgress(0); // 시작일/종료일이 없을 경우 0%
	        }
	    });
		return list;
	}

	@GetMapping("/{projId}")
	public String getProjectDetail(@PathVariable String projId, Model model) {
		ProjectVO project = projService.readProject(projId);
		List<ProjectMemberVO> memberList = projService.readProjectMemberList(projId);
		Map<String, List<LocalDate>> dateMap = projService.readProjectDate(projId);
		List<TaskHistoryVO> taskHistoryList = projService.readTaskHistoryAll(projId);
		List<LocalDate> week = dateMap.get("week");
		List<LocalDate> month = dateMap.get("month");
		
		model.addAttribute("project", project);
		model.addAttribute("memberList", memberList);
		model.addAttribute("week", week);
		model.addAttribute("month", month);
		model.addAttribute("taskList", taskHistoryList);
		return "project/projectDetail";
	}
	
	@GetMapping("/{projId}/dhtmlx")
	@ResponseBody
	public Map<String, List<ProjectTaskDTO>> getProjectTaskList(
			@PathVariable String projId
			) {
		Map<String, List<ProjectTaskDTO>> response = projService.readProjectTaskList(projId);
		log.info("{}",response.toString());
		return response;
	}
	
	@GetMapping("/projectMember/{projMemberId}")
	@ResponseBody
	public ProjectMemberVO getProjectMember(@PathVariable("projMemberId") String projMemberId) {
		ProjectMemberVO projectMemberVO = projService.readProjectMember(projMemberId);
		return projectMemberVO;
	}
	
	@GetMapping("/{projId}/projectMember")
	@ResponseBody
	public List<ProjectMemberVO> getProjectMemberList(@PathVariable("projId") String projId){
		List<ProjectMemberVO> list =  projService.readProjectMemberList(projId);
		return list;
	}
	
	@PostMapping("/{projId}/task")
	@ResponseBody
	public ProjectTaskVO getProjectTask(
	        @PathVariable("projId") String projId,
	        @RequestBody Map<String, String> requestBody // JSON 요청 본문에서 추출
	) {
	    String taskId = requestBody.get("taskId");
	    log.info("{}, {}", projId, taskId);
	    ProjectTaskVO projectTaskVO = projService.readProjectTask(projId, taskId);
	    return projectTaskVO; // JSON 형식으로 반환
	}
	
	@PostMapping("{projId}/taskHistory")
	@ResponseBody
	public List<TaskHistoryVO> getTaskHistoryList(
			@RequestBody Map<String, String> requestBody
			){
		String taskId = requestBody.get("taskId");
		return projService.readTaskHistoryList(taskId);
	}
	
	@GetMapping("/{projId}/taskList")
	@ResponseBody
	public List<TaskHistoryVO> getTaskHistoryAll(@PathVariable("projId") String projId){
		List<TaskHistoryVO> list = projService.readTaskHistoryAll(projId);
		return list;
	}
	
}
