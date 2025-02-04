package kr.or.ddit.project.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.project.vo.ProjectMemberVO;
import kr.or.ddit.project.vo.ProjectTaskDTO;
import kr.or.ddit.project.vo.ProjectTaskVO;
import kr.or.ddit.project.vo.ProjectVO;
import kr.or.ddit.project.vo.TaskHistoryVO;

public interface ProjectService {
	public List<ProjectVO> readProjectList(String empId);
	
	public ProjectVO readProject(String projId);
	
	public Map<String, List<LocalDate>> readProjectDate(String projId);
	
	public ServiceResult createProject(ProjectVO project);

	public ServiceResult modifyProject(ProjectVO project);
	
	public ServiceResult removeProject(String projId);
	
	public List<ProjectMemberVO> readProjectMemberList(String projId);
	
	public ServiceResult insertProjectMember(List<ProjectMemberVO> list);

	public Map<String, List<ProjectTaskDTO>> readProjectTaskList(String projId);
	
	public ServiceResult insertProjectTask(ProjectTaskVO projectTaskVO);
	
	public ProjectTaskVO readProjectTask(String projId, String taskId);
	
	public ServiceResult updateTask(ProjectTaskVO projectTaskVO);
	
	public ServiceResult deleteTask(String taskId);

	public ServiceResult deletechildTask(String taskId);
	
	public ProjectTaskVO readTaskId(ProjectTaskVO projectTaskVO);
	
	public List<TaskHistoryVO> readTaskHistoryList(String taskId);
	
	public ServiceResult insertTaskHistory(TaskHistoryVO taskHistoryVO);
	
	public ServiceResult updateTaskHistory(TaskHistoryVO taskHistoryVO);
	
	public ServiceResult deleteTaskHistory(TaskHistoryVO taskHistoryVO);
	
	public List<TaskHistoryVO> readTaskHistoryAll(String projId);
	
	public ProjectMemberVO readProjectMember(String projMemberId);
	
	public ServiceResult modifyProjectMember(ProjectMemberVO projectMemberVO);
	
	public ServiceResult removeProjectMember(String projectMemberid);
	
	public String readProjectNoProjId(ProjectVO projectVO);
}
