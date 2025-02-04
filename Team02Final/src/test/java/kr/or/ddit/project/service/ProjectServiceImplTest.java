package kr.or.ddit.project.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kr.or.ddit.annotation.RootContextWebConfig;
import kr.or.ddit.project.dao.ProjectMapper;
import kr.or.ddit.project.vo.ProjectTaskDTO;
import kr.or.ddit.project.vo.ProjectVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RootContextWebConfig
class ProjectServiceImplTest{
	
	@Autowired
	private ProjectMapper mapper;

	@Autowired
	private ProjectService service;
	
	@Test
	void testReadProjectList() {
		List<ProjectVO> list = mapper.readProjectList("EMP009");
		for(ProjectVO project : list){
			System.out.println(project.toString());
		}
	}

	@Test
	void testReadProject() {
		String projId = "20241224001";
		ProjectVO project = mapper.readProject(projId);
	}
	
	@Test
	void testInsertProject() {

	}
	
	@Test 
	void testUpdateProject(){
		ProjectVO project = mapper.readProject("20241224001");
		project.setProjTitle("테스트");
		project.setProjContent("내용");
		
		mapper.updateProject(project);
	}
	
	@Test
	void testDeleteProject() {
		
	}
	
	
	@Test
	void testReadProjectTaskList() {
		String projId = "20241224001";
//		List<ProjectTaskDTO> taskDto = service.readProjectTaskList(projId);
//		
//		log.info("taskDto ==> {}", taskDto.toString());
	}

}
 