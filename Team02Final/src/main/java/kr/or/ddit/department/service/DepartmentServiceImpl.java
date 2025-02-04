package kr.or.ddit.department.service;

import kr.or.ddit.department.dao.DepartmentMapper;
import kr.or.ddit.organitree.vo.DepartmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    DepartmentMapper mapper;

    @Override
    public String readDepartNameByDepartCode(String departCode) {
        return mapper.selectDepartNameByDepartCode(departCode);
    }

    @Override
    public List<DepartmentVO> readDepartmentList() {
        return mapper.selectDepartmentList();
    }
}
