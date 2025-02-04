package kr.or.ddit.approval.dao;

import kr.or.ddit.approval.vo.ApprovalDocumentVO;
import kr.or.ddit.commons.paging.PaginationInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApprovalDocumentMapper {
    public int getNextDocSeq(String prefix);

    public int insertApprovalDocument(ApprovalDocumentVO document);


    /** 탭 전환시 조회해서 갖고오는영역 */
    public int selectDraftCount(@Param("paging") PaginationInfo paging,
                                @Param("empId") String empId);
    public List<ApprovalDocumentVO> selectDraftList(@Param("paging") PaginationInfo paging,
                                             @Param("empId") String empId);

    public int selectToBeApprovedCount(@Param("paging") PaginationInfo paging,
                                       @Param("empId") String empId);
    public List<ApprovalDocumentVO> selectToBeApprovedList(@Param("paging") PaginationInfo paging,
                                                           @Param("empId") String empId);

    public int selectInProgressCount(@Param("paging") PaginationInfo paging,
                                     @Param("empId") String empId);
    public List<ApprovalDocumentVO> selectInProgressList(@Param("paging") PaginationInfo paging,
                                                         @Param("empId") String empId);

    public int selectReferenceCount(@Param("paging") PaginationInfo paging,
                                    @Param("empId") String empId);
    public List<ApprovalDocumentVO> selectReferenceList(@Param("paging") PaginationInfo paging,
                                                        @Param("empId") String empId);
    /** 탭 전환시 조회해서 갖고오는영역 */

    public ApprovalDocumentVO selectDocumentDetail(String docId);

    public void updateDocumentStatus(
            @Param("docId") String docId,
            @Param("status") String status
    );

    public void updateDocumentContent(@Param("docId") String docId,
                                      @Param("document") ApprovalDocumentVO document);

    public int deleteDocument(String docId);

    public String selectEmpIdByDocumentId(String docId);
}
