package kr.or.ddit.approval.service;

import kr.or.ddit.approval.dto.ApprovalDocumentDTO;
import kr.or.ddit.approval.vo.ApprovalFormVO;
import kr.or.ddit.atch.vo.AtchFileDetailVO;
import kr.or.ddit.employee.vo.EmployeeVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ApprovalFormService {

    /**
     * 처음에 결재생성하고 셀렉트박스로 무슨양식고를지
     * 정하기위해 제목을 뽑아내는 메소드
     * @return
     */
    public List<ApprovalFormVO> getApprovalFormTitles();

    /**
     * 셀렉스 박스에서 선택한 결재문서양식의 내용을 비동기로 불러오는 로직
     * @param apprformId
     * @return
     */
    public String getFormContent(String apprformId);

    /**
     * 결재문서에 필요한 필수 데이터들을 짜집기해서 문서에 합치는 로직
     * @param approvalInfoMap
     * @param requestData
     * @return
     */
    public String processVacationForm(Map<String, String> approvalInfoMap,
                                      Map<String, Object> requestData);

    /**
     * 결재문서에 결재선 리스트 삽입하는 로직 (결재란 만드는거임)
     * @param documentHtml
     * @param approvers
     * @return
     */
    public String generateApprovalLineHtml(String documentHtml, List<Map<String, Object>> approvers);

    /**
     * 결재문서 최종본 등록하는 로직(기안)
     * @param dto
     * @param myEmp
     * @return
     */
    public String processApprovalDraft(ApprovalDocumentDTO dto, List<MultipartFile> attachments, EmployeeVO myEmp);

    /**
     * 첫 미리보기 문서 양식 렌더링하기(휴가신청서, 지출결의서, 자유양식서)
     * @param apprformId
     * @param myEmp
     * @param requestData
     * @return
     */
    public String processPreviewFormRenderer(String apprformId, EmployeeVO myEmp, Map<String, Object> requestData);

}
