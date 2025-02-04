package kr.or.ddit.organitree.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.organitree.service.OrganiTreeService;
import kr.or.ddit.organitree.vo.DepartmentVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/directory")
public class OrganiDirectoryController {
	
	@Inject
	private OrganiTreeService service;
	
	@GetMapping
	public String get() {
		return "organi/organiDirectory";
	}
	
	@GetMapping("folder")
	@ResponseBody
	public List<DepartmentVO> organiDir(
			@PathVariable("companyId") String companyId	 
	) {
		List<DepartmentVO> list = service.readDirectory();
		log.info("distinct dir : {} " , list);
		return list;	
	}
}
