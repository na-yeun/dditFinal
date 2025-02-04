package kr.or.ddit.organitree.systemController;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.commons.validate.UpdateGroup;
import kr.or.ddit.organitree.service.OrganiTreeService;
import kr.or.ddit.organitree.vo.DepartmentVO;
import lombok.extern.slf4j.Slf4j;
import retrofit2.http.DELETE;

@Slf4j
@Controller
@RequestMapping("/{companyId}/hr/organi")
public class OrganiSystemController {
	
	@Inject
	private OrganiTreeService service;
	
	@GetMapping
	public String get() {
		return "organi/organiSystem";
	}
	
	@GetMapping("/list")
	@ResponseBody
	public List<DepartmentVO> organiSystem(
			  @PathVariable("companyId") String companyId
			
		) {
		return service.readOrganiList();
	}
	
	@GetMapping("{departCode}")
	@ResponseBody
	public DepartmentVO selectOneDepart(
			  @PathVariable("companyId") String companyId
			, @PathVariable("departCode")String departCode
		) {
		
		return service.readOneDepartment(departCode);
		
	}
	
	@PutMapping("/edit")
	@ResponseBody
	public int updateOneDepart(
			 @PathVariable("companyId") String companyId
			 ,@Validated(UpdateGroup.class) @RequestBody DepartmentVO department
		) {
		
		return service.modifyOneDepartment(department);
	}
	
	@PostMapping("/add")
	@ResponseBody
	public int insertOneDepart(
		    @PathVariable("companyId") String companyId
		  , @Validated(InsertGroup.class) @RequestBody DepartmentVO department	
		  , HttpSession session 	
	) {
		department.setContractId(companyId);
		return service.createOneDepartment(department);
	}
	
	@DeleteMapping("/remove")
	@ResponseBody
	public int deleteOneDepart(
			  @PathVariable("companyId") String companyId
			, @RequestBody String departName
	) {
		ServiceResult result = service.removeOneDepartment(departName);
		log.info("결과는 : {}",result);
		switch (result) {
		case OK:
				return 1;		
		case FAIL:
		default:
			break;
		}
		return 0;
	
		
		
	}
}
