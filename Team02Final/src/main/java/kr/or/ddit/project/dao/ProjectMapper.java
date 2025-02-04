package kr.or.ddit.project.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.project.vo.ProjectMemberVO;
import kr.or.ddit.project.vo.ProjectTaskVO;
import kr.or.ddit.project.vo.ProjectVO;
import kr.or.ddit.project.vo.TaskHistoryVO;

@Mapper
public interface ProjectMapper {
	
	public List<ProjectVO> readProjectList(String empId);
	
	public ProjectVO readProject(String projId);
	
	public int insertProject(ProjectVO project);
	
	public int updateProject(ProjectVO project);
	
	public int deleteProject(String projId);
	
	public List<ProjectMemberVO> readProjectMemberList(String projId);
	
	public int insertProjectMember(ProjectMemberVO projectMemberVO);
	
	public List<ProjectTaskVO> readProjectTaskList(String projId);
	
	public int insertProjectTask(ProjectTaskVO projectTaskVO);
	
	public ProjectTaskVO readProjectTask(@Param("projId") String projId, @Param("taskId") String taskId);
	
	public int updateTask(ProjectTaskVO projectTaskVO);
	
	public int deleteTask(String taskId);
	
	public int deletechildTask(String taskId);
	
	public ProjectTaskVO readTaskId(ProjectTaskVO projectTaskVO);
	
	public List<TaskHistoryVO> readTaskHistoryList(String taskId);
	
	public int insertTaskHistory(TaskHistoryVO taskHistoryVO);
	
	public int updateTaskHistory(TaskHistoryVO taskHistoryVO);
	
	public int deleteTaskHistory(TaskHistoryVO taskHistoryVO);
	
	public List<TaskHistoryVO> readTaskHistoryAll(String projId);
	
	public ProjectMemberVO readProjectMember(String projMemberId);
	
	public int modifyProjectMember(ProjectMemberVO projectMemberVO);

	public int removeProjectMember(String projectMemberid);
	
	public String readProjectNoProjId(ProjectVO projectVO);
}
