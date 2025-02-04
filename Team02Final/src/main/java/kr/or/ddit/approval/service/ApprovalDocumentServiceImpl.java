package kr.or.ddit.approval.service;


import kr.or.ddit.approval.dao.ApprovalDocumentMapper;
import kr.or.ddit.approval.dao.ApprovalMapper;
import kr.or.ddit.approval.dao.ReferenceEmployeeMapper;
import kr.or.ddit.approval.vo.ApprovalDocumentVO;
import kr.or.ddit.approval.vo.ApprovalVO;
import kr.or.ddit.approval.vo.ReferenceEmployeeVO;
import kr.or.ddit.atch.service.AtchFileService;
import kr.or.ddit.atch.vo.AtchFileDetailVO;
import kr.or.ddit.commons.paging.PaginationInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ApprovalDocumentServiceImpl implements ApprovalDocumentService {

    @Autowired
    private AtchFileService atchFileService;

    @Autowired
    private ApprovalDocumentMapper approvalDocumentMapper;

    @Autowired
    private ApprovalMapper approvalMapper;

    @Autowired
    private ReferenceEmployeeMapper referenceEmployeeMapper;

    //첨부파일 처리
    @Value("#{dirInfo.saveDir}")
    private Resource saveFolderRes;
    private File saveFolder;

    @PostConstruct
    public void init() throws IOException {
        this.saveFolder = saveFolderRes.getFile();
    }

    @Override
    public List<ApprovalDocumentVO> getDraftList(PaginationInfo paging, String empId) {
        int totalRecord = approvalDocumentMapper.selectDraftCount(paging, empId);
        paging.setTotalRecord(totalRecord);
        return approvalDocumentMapper.selectDraftList(paging, empId);
    }

    @Override
    public List<ApprovalDocumentVO> getToBeApprovedList(PaginationInfo paging, String empId) {
        int totalRecord = approvalDocumentMapper.selectToBeApprovedCount(paging, empId);
        paging.setTotalRecord(totalRecord);
        return approvalDocumentMapper.selectToBeApprovedList(paging, empId);
    }

    @Override
    public List<ApprovalDocumentVO> getInProgressList(PaginationInfo paging, String empId) {
        int totalRecord = approvalDocumentMapper.selectInProgressCount(paging, empId);
        paging.setTotalRecord(totalRecord);
        return approvalDocumentMapper.selectInProgressList(paging, empId);
    }

    @Override
    public List<ApprovalDocumentVO> getReferenceList(PaginationInfo paging, String empId) {

        // 1. 기본 참조 문서 조회
        int totalRecord = approvalDocumentMapper.selectReferenceCount(paging, empId);
        paging.setTotalRecord(totalRecord);
        return approvalDocumentMapper.selectReferenceList(paging, empId);
    }

    //파일 다운로드 하는 링크
    @Override
    public AtchFileDetailVO download(int atchFileId, int fileSn) {
        return Optional.ofNullable(atchFileService.readAtchFileDetail(atchFileId, fileSn, saveFolder))
                .filter(fd -> fd.getSavedFile().exists())
                .orElseThrow(() -> new RuntimeException(String.format("[%d, %d]해당 파일이 없음.", atchFileId, fileSn)));
    }

    //문서 상세 조회
    @Transactional
    @Override
    public ApprovalDocumentVO getDocumentDetail(String docId, String empId) {
        // 1. 문서 상세 조회
        ApprovalDocumentVO documentDetail = approvalDocumentMapper.selectDocumentDetail(docId);

        // 2. 결재선(사실상 결재자) 정보 조회
        List<ApprovalVO> approvalList = approvalMapper.selectApprovalList(docId);

        // 3. 참조자 정보 조회 및 상태 업데이트
        List<ReferenceEmployeeVO> referenceList = referenceEmployeeMapper.selectReferencesByDocId(docId);

        // 4. 현재 사용자가 참조자인지 확인
        boolean isReferenceUser = referenceList.stream()
                .anyMatch(ref -> ref.getEmpId().equals(empId));
        if (isReferenceUser) {
            referenceEmployeeMapper.updateReferenceStatus(docId, empId);
            // 참조자면 읽음표시와 날짜 업데이트하고 다시 업데이트된걸 조회해서 리스트에 넣음
            referenceList = referenceEmployeeMapper.selectReferencesByDocId(docId);
        }

        // 5. 현재 사용자의 결재 정보 확인 및 상태 업데이트
        ApprovalVO myApproval = approvalList.stream()
                .filter(approval -> approval.getEmpId().equals(empId)) //내 empId와 비교해서 찾음
                .findFirst() //어차피 단 하나뿐이라 의미없긴한데 일단 넣기
                .orElse(null); //없으면 null반환

        //만약 결재선의 결재자가 있고 상태가 1번인 미열람상태라면?
        if (myApproval != null && myApproval.getApprovalStatus().equals("1")) {
            // 내 결재 상태만 2번인 진행중으로 변경한다
            approvalMapper.updateMyApprovalStatus(docId, empId, "2");

            // 문서상태도 진행중(3)으로 업데이트하기
            approvalDocumentMapper.updateDocumentStatus(docId, "3");

            // 업데이트 후 결재선 정보 재조회
            approvalList = approvalMapper.selectApprovalList(docId);
            // 문서 정보도 재조회 (상태가 변경되었으므로)
            documentDetail = approvalDocumentMapper.selectDocumentDetail(docId);
        }

        // 최종 데이터 세팅하기
        documentDetail.setApprovalList(approvalList);
        documentDetail.setReferenceList(referenceList);

        return documentDetail;
    }

    @Transactional
    @Override
    public ResponseEntity<String> deleteDocument(String docId, String empId) {
        try {
            // 삭제 권한 검증
            ApprovalDocumentVO document = approvalDocumentMapper.selectDocumentDetail(docId);
            if (document == null) {
                return ResponseEntity.badRequest().body("존재하지 않는 문서입니다.");
            }

            if (!document.getEmpId().equals(empId)) {
                return ResponseEntity.badRequest().body("문서 삭제 권한이 없습니다.");
            }

            // 첫 번째 결재자가 미열람 상태이고, 다른 결재자들이 결재를 진행하지 않았는지 확인
            boolean canDelete = document.getApprovalList().stream()
                    .anyMatch(approval ->
                            approval.getApprovalNum().equals("1") &&
                                    approval.getApprovalStatus().equals("1")
                    );
//                  &&  document.getApprovalList().stream()
//                            .noneMatch(approval ->
//                                    !approval.getApprovalStatus().equals("1") &&
//                                            !approval.getApprovalStatus().equals("5")
//                            ); 굳이 이거쓸필요는 못느끼겠음

            if (!canDelete) {
                return ResponseEntity.badRequest().body("이미 결재가 진행된 문서는 삭제할 수 없습니다.");
            }

            int result = approvalDocumentMapper.deleteDocument(docId);  // 문서 삭제

            if (result > 0) {
                return ResponseEntity.ok("문서가 성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("문서 삭제에 실패했습니다.");
            }

        } catch (Exception e) {
            log.error("문서 삭제 중 오류 발생: ", e);
            return ResponseEntity.internalServerError()
                    .body("문서 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
