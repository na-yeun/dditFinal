package kr.or.ddit.approval.controller;

import kr.or.ddit.department.service.DepartmentService;
import kr.or.ddit.employee.service.EmployeeService;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.organitree.vo.DepartmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/{companyId}/approval")
public class ApproverController {

    @Autowired
    DepartmentService departmentService;

    @Autowired
    EmployeeService employeeService;


    //조직도를 갖고오기위해 부서리스트갖고오는 메소드
    @GetMapping("/orgTree")
    @ResponseBody
    public List<DepartmentVO> getOrganizationDepartments() {
        return departmentService.readDepartmentList();
    }

    //조직도 안에 사원들을 배치하기위해 리스트를 갖고오는 메소드
    @GetMapping("/employees")
    @ResponseBody
    public List<Map<String, Object>> getEmployeesListForDept() {
        // 보낼데이터만 보내고싶어서 가공해서 보내기로함
        return employeeService.readEmployeeListForDept();
    }
}
