package kr.or.ddit.approval.dao;

import kr.or.ddit.approval.vo.ApprovalFormVO;
import kr.or.ddit.approval.vo.ApprovalVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApprovalMapper {


    /**
     * 결재자 결재 순번대로 배치시키기
     * @param approvalList
     * @return
     */
    public int insertApprovals(List<ApprovalVO> approvalList);


    public List<ApprovalVO> selectApprovalList(
            @Param("docId") String docId);

    public void updateMyApprovalStatus(
            @Param("docId") String docId,
            @Param("empId") String empId,
            @Param("status") String status
    );

    public void updateApproval(
            @Param("docId") String docId,
            @Param("empId") String empId,
            @Param("approval") ApprovalVO approval
    );

    public List<ApprovalVO> selectRemainingApprovers(
            @Param("empId") String empId,
            @Param("docId") String docId);

    void updateRemainingApprovers(
            @Param("docId") String docId,
            @Param("empId") String empId
    );

    boolean isLastApprover(
            @Param("docId") String docId,
            @Param("empId") String empId
    );
}
