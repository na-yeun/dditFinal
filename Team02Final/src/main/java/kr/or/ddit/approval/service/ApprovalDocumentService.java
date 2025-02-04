package kr.or.ddit.approval.service;

import kr.or.ddit.approval.vo.ApprovalDocumentVO;
import kr.or.ddit.atch.vo.AtchFileDetailVO;
import kr.or.ddit.commons.paging.PaginationInfo;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ApprovalDocumentService {


    /**
     * 내가 기안한 문서 갖고오는거임 페이지네이션된 리스트
     * @param paging
     * @return
     */
    public List<ApprovalDocumentVO> getDraftList(PaginationInfo paging, String empId);

    /**
     * 내가 결재할 문서 갖고오는것 페이지네이션된 리스트
     * @param paging
     * @return
     */
    public List<ApprovalDocumentVO> getToBeApprovedList(PaginationInfo paging, String empId);

    /**
     * 나와관련된 문서가 결재중인상태인거 갖고오는것 페이지뭐시기
     * @param paging
     * @return
     */
    public List<ApprovalDocumentVO> getInProgressList(PaginationInfo paging, String empId);

    /**
     * 내가 참조당한 문서 갖고오는것 페이..
     * @param paging
     * @return
     */
    public List<ApprovalDocumentVO> getReferenceList(PaginationInfo paging, String empId);
    /**
     * 결재문서에 첨부파일 링크 삽입될건데 그거누르면 다운로드되게하려고
     * @param atchFileId
     * @param fileSn
     * @return
     */
    public AtchFileDetailVO download(int atchFileId, int fileSn);

    /**
     * 결재 문서 상세 조회
     * 조회와 동시에 필요한 상태 업데이트 수행
     * @param docId
     * @return
     */
    public ApprovalDocumentVO getDocumentDetail(String docId, String empId);

    public ResponseEntity<String> deleteDocument(String docId, String empId);
}
