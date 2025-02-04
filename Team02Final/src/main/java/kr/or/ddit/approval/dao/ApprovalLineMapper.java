package kr.or.ddit.approval.dao;

import kr.or.ddit.approval.vo.ApprovalLineVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ApprovalLineMapper {
    public String selectApprLineLastSeq();

    public int insertApprovalLine(ApprovalLineVO approvalLine);

    public List<ApprovalLineVO> selectApprLinesByEmpId(String empId);

    public List<Map<String, Object>> selectApproverDetailsByLineId(String apprlineId);

    public void deleteApproversByLineId(String lineId);

    public void deleteApprovalLine(String lineId);
}
