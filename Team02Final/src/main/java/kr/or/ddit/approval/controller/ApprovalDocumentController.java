package kr.or.ddit.approval.controller;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.approval.service.ApprovalDocumentService;
import kr.or.ddit.approval.service.ApprovalFormService;
import kr.or.ddit.approval.vo.ApprovalDocumentVO;
import kr.or.ddit.approval.vo.ApprovalFormVO;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.paging.SimpleCondition;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.security.AccountVOWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 나의 전자결재 문서 리스트, 상세 조회
 * 나의 전자결재 문서 생성
 */
@Controller
@RequestMapping("/{companyId}/approval")
public class ApprovalDocumentController {

    public static final String MODELNAME = "approvalDocument";

    @Autowired
    private ApprovalFormService approvalFormService;

    @Autowired
    private ApprovalDocumentService approvalDocumentService;



    @ModelAttribute(MODELNAME)
    public ApprovalDocumentVO approvalDocument() {
        return new ApprovalDocumentVO();
    }

    // 1. 초기 진입용 메소드 draft로 고정하기
    @GetMapping("/list")
    public String getApprovalDocumentList(
            @ModelAttribute("condition") SimpleCondition condition,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model,
            Authentication authentication
    ) {
        AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
        AccountVO account = principal.getAccount();
        EmployeeVO myEmp = (EmployeeVO) account;
        String empId = myEmp.getEmpId();

        // 초기 진입시 draft 탭의 데이터도 함께 로드
        PaginationInfo paging = new PaginationInfo();
        paging.setCurrentPage(page);
        paging.setSimpleCondition(condition);

        List<?> documentList = approvalDocumentService.getDraftList(paging, empId);

        model.addAttribute("list", documentList);
        model.addAttribute("paging", paging);
        model.addAttribute("condition", condition);

        return "approval/approvalList";  // 전체 뷰 반환
    }

    // 나의 전자결재 문서 리스트 입맛에맞게 조회하기
    @GetMapping("/list/{tabId}")
    public ResponseEntity<Map<String,Object>> getApprovalDocumentTab(
            @PathVariable String tabId, // 처음에는 무조건 draft 그다음엔 사용자클릭에따라
            @ModelAttribute("condition") SimpleCondition condition,
            @RequestParam(defaultValue = "1") int page,
            Authentication authentication) {

        AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
        AccountVO account = principal.getAccount();
        EmployeeVO myEmp = (EmployeeVO) account;
        String empId = myEmp.getEmpId();

        PaginationInfo paging = new PaginationInfo();
        paging.setCurrentPage(page);
        paging.setSimpleCondition(condition);

        List<?> documentList; //뭐로돌아올지몰라서 와일드카드로
        switch (tabId) {
            case "draft":
                documentList = approvalDocumentService.getDraftList(paging, empId);
                break;
            case "approval":
                documentList = approvalDocumentService.getToBeApprovedList(paging, empId);
                break;
            case "progress":
                documentList = approvalDocumentService.getInProgressList(paging, empId);
                break;
            case "reference":
                documentList = approvalDocumentService.getReferenceList(paging, empId);
                break;
            default:
                return ResponseEntity.badRequest().body(null);
        }

        Map<String, Object> response = new HashMap<>(); //맵에 담아서 보내버리기
        response.put("list", documentList); // 안에 들은 값이랑
        response.put("paging", paging); // 페이징 정보

        return ResponseEntity.ok(response);
    }

    //결재 문서 상세조회
    @GetMapping("/detail/{docId}")
    public String getApprovalDocumentDetail(
            @PathVariable String docId,
            Model model,
            Authentication authentication) {

        AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
        AccountVO account = principal.getAccount();
        EmployeeVO myEmp = (EmployeeVO) account;
        String empId = myEmp.getEmpId();

        // 문서 정보와 결재선 정보 조회함
        // 그와동시에 미열람상태를 열람상태로만들고 결재자와 문서 상태를 바꿈 결재진행중으로
        ApprovalDocumentVO document = approvalDocumentService.getDocumentDetail(docId, empId);

        // 현재 결재자의 approverFinalYn 정보 찾기
        String approverFinalYn = document.getApprovalList().stream()
                .filter(approval -> approval.getEmpId().equals(empId))
                .map(approval -> approval.getApproverFinalYn())
                .findFirst()
                .orElse("N");  // 기본값은 N으로

        // 현재 사용자가 결재할 수 있는 상태인지 확인
        boolean isCurrentApprover = document.getApprovalList().stream()
                .filter(approval -> approval.getEmpId().equals(empId))
                .anyMatch(approval -> approval.getApprovalStatus().equals("2"));  // 진행중 상태만 체크

        // 첫 번째 결재자(approvalNum=1)의 상태 확인
        boolean canDelete = document.getApprovalList().stream()
                .filter(approval -> approval.getApprovalNum().equals("1"))
                .anyMatch(approval -> approval.getApprovalStatus().equals("1")); // 1: 미열람 상태

        // 문서 작성자인지 확인
        boolean isWriter = document.getEmpId().equals(empId);

        model.addAttribute("document", document);
        //jsp에서 결재진행할수있는 결재자인지 확인하기위해 또 삭제가능상태인지 알기위해
        model.addAttribute("canDelete", canDelete && isWriter); // 작성자이면서 첫번째 결재자가 미열람 상태일 때만 true
        model.addAttribute("isCurrentApprover", isCurrentApprover);
        model.addAttribute("approverFinalYn", approverFinalYn);  // 전결권 정보 추가

        return "approval/approvalDetail";
    }

    @DeleteMapping("/delete/{docId}")
    @ResponseBody
    public ResponseEntity<String> deleteDocument(
            @PathVariable String docId,
            Authentication authentication) {
        try {
            AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
            AccountVO account = principal.getAccount();
            EmployeeVO myEmp = (EmployeeVO) account;
            String empId = myEmp.getEmpId();

            // 문서 삭제 처리
            return approvalDocumentService.deleteDocument(docId, empId);

        } catch (Exception e) {
            // 예상치 못한 예외에 대한 처리만 컨트롤러에서 수행하기
            return ResponseEntity.internalServerError()
                    .body("문서 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }



}
