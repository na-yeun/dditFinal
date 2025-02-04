package kr.or.ddit.approval.dao;

import kr.or.ddit.approval.vo.ReferenceEmployeeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReferenceEmployeeMapper {

    public int updateReferenceStatus(
            @Param("docId") String docId,
            @Param("empId") String empId);

    public List<ReferenceEmployeeVO> selectReferencesByDocId(
            @Param("docId") String docId);
}
