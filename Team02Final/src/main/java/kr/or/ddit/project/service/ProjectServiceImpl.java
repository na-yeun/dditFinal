package kr.or.ddit.project.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.util.DateRange;
import kr.or.ddit.project.dao.ProjectMapper;
import kr.or.ddit.project.vo.ProjectMemberVO;
import kr.or.ddit.project.vo.ProjectTaskDTO;
import kr.or.ddit.project.vo.ProjectTaskVO;
import kr.or.ddit.project.vo.ProjectVO;
import kr.or.ddit.project.vo.TaskHistoryVO;

@Service
public class ProjectServiceImpl implements ProjectService {
	 
	@Autowired
	private ProjectMapper mapper;
	
	@Override
	public List<ProjectVO> readProjectList(String empId) {
		// TODO Auto-generated method stub
		return mapper.readProjectList(empId);
	}

	@Override
	public ProjectVO readProject(String projId) {
		return mapper.readProject(projId);
	}

	@Override
	public ServiceResult createProject(ProjectVO project) {
		int rowcnt = mapper.insertProject(project);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public ServiceResult modifyProject(ProjectVO project) {
		int rowcnt = mapper.updateProject(project);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public ServiceResult removeProject(String projId) {
		int rowcnt = mapper.deleteProject(projId);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public List<ProjectMemberVO> readProjectMemberList(String projId) {
		return mapper.readProjectMemberList(projId);
	}

	@Override
	public ServiceResult insertProjectMember(List<ProjectMemberVO> list) {
		int rowcnt = 0;
		for(ProjectMemberVO projectMemberVO : list) {
			rowcnt = mapper.insertProjectMember(projectMemberVO);
		}
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public Map<String, List<LocalDate>> readProjectDate(String projId) {
		Map<String, List<LocalDate>> map = new HashMap<String, List<LocalDate>>();
		DateRange dateRange = new DateRange();
		
		ProjectVO project = mapper.readProject(projId);
		
		List<LocalDate> weeklyDates = dateRange.getWeeklyDates(project.getProjSdate(), project.getProjEdate());
		List<LocalDate> monthlyDates = dateRange.getMonthlyDates(project.getProjSdate(), project.getProjEdate());

		map.put("week", weeklyDates);
		map.put("month", monthlyDates);
		
		return map;
	}

	@Override
	public Map<String, List<ProjectTaskDTO>> readProjectTaskList(String projId) {
		Map<String, List<ProjectTaskDTO>> response = new HashMap<>();
		List<ProjectTaskVO> taskVoList = mapper.readProjectTaskList(projId);
		List<ProjectTaskDTO> taskDtoList = new ArrayList<ProjectTaskDTO>();
		
		 // DateTimeFormatter 생성 (DD-MM-YYYY 형식)
	    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		
		
		for(ProjectTaskVO task : taskVoList) {
			ProjectTaskDTO taskDto = new ProjectTaskDTO();
			taskDto.setId(task.getTaskId());
			taskDto.setText(task.getTaskNm());
			taskDto.setContent(task.getTaskContent());
			taskDto.setStart_date(task.getTaskSdate().format(dateTimeFormatter));
			taskDto.setColor(task.getTaskColor());
			taskDto.setDuration(task.getTaskDuration());
			taskDto.setOpen(task.getTaskOpen());
			taskDto.setParent(task.getTaskParentid());
			taskDto.setProjectMemberid(task.getProjectMemberid());
			taskDto.setProgress(task.getTaskProgress());
			taskDto.setStatus(task.getTaskStatus());
			taskDtoList.add(taskDto);
		}
		
		response.put("data", taskDtoList);
		
		return response;
	}

	@Override
	public ServiceResult insertProjectTask(ProjectTaskVO projectTaskVO) {
		projectTaskVO.setTaskDuration();
		
		int rowcnt = mapper.insertProjectTask(projectTaskVO);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public ProjectTaskVO readProjectTask(String projId, String taskId) {
		ProjectTaskVO projectTaskVO = mapper.readProjectTask(projId, taskId);
		return projectTaskVO;
	}

	@Override
	public ServiceResult updateTask(ProjectTaskVO projectTaskVO) {
		projectTaskVO.setTaskDuration();
		
		int rowcnt = mapper.updateTask(projectTaskVO);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public ServiceResult deleteTask(String taskId) {
		int rowcnt = mapper.deleteTask(taskId);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}
	
	@Override
	public ProjectTaskVO readTaskId(ProjectTaskVO projectTaskVO) {
		projectTaskVO.setTaskDuration();
		return mapper.readTaskId(projectTaskVO);
	}

	@Override
	public ServiceResult insertTaskHistory(TaskHistoryVO taskHistoryVO) {
		int rowcnt = mapper.insertTaskHistory(taskHistoryVO);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public ServiceResult updateTaskHistory(TaskHistoryVO taskHistoryVO) {
		int rowcnt = mapper.updateTaskHistory(taskHistoryVO);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public ServiceResult deleteTaskHistory(TaskHistoryVO taskHistoryVO) {
		int rowcnt = mapper.deleteTaskHistory(taskHistoryVO);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public List<TaskHistoryVO> readTaskHistoryList(String taskId) {
		return mapper.readTaskHistoryList(taskId);
	}

	@Override
	public ServiceResult deletechildTask(String taskId) {
		int rowcnt = mapper.deletechildTask(taskId);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public List<TaskHistoryVO> readTaskHistoryAll(String projId) {
		return mapper.readTaskHistoryAll(projId);
	}

	@Override
	public ProjectMemberVO readProjectMember(String projMemberId) {
		return mapper.readProjectMember(projMemberId);
	}

	@Override
	public ServiceResult modifyProjectMember(ProjectMemberVO projectMemberVO) {
		int rowcnt = mapper.modifyProjectMember(projectMemberVO);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public ServiceResult removeProjectMember(String projectMemberid) {
		int rowcnt = mapper.removeProjectMember(projectMemberid);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public String readProjectNoProjId(ProjectVO projectVO) {
		String projId = mapper.readProjectNoProjId(projectVO);
		return projId;
	}

}
