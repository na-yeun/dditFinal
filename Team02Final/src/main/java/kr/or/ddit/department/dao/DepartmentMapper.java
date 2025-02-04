package kr.or.ddit.department.dao;

import kr.or.ddit.organitree.vo.DepartmentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    public String selectDepartNameByDepartCode(String departCode);

    public List<DepartmentVO> selectDepartmentList();

}
