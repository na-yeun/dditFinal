package kr.or.ddit.department.service;

import kr.or.ddit.organitree.vo.DepartmentVO;

import java.util.List;

public interface DepartmentService {

    /**
     * DEPART_CODE로 DEPART_NAME 갖고오는 메소드
     * @param departCode
     * @return
     */
    public String readDepartNameByDepartCode(String departCode);

    public List<DepartmentVO> readDepartmentList();
}
