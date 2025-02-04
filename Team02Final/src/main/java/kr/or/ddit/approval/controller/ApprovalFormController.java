package kr.or.ddit.approval.controller;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.approval.dto.ApprovalDocumentDTO;
import kr.or.ddit.approval.service.ApprovalFormService;
import kr.or.ddit.approval.service.ApprovalLineService;
import kr.or.ddit.approval.vo.ApprovalFormVO;
import kr.or.ddit.employee.service.EmployeeService;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.expense.service.ExpenseService;
import kr.or.ddit.security.AccountVOWrapper;
import kr.or.ddit.vacation.service.VacationStatusService;
import kr.or.ddit.vacation.vo.VacationStatusVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/{companyId}/approval")
@Slf4j
public class ApprovalFormController {

    @Autowired
    private ApprovalFormService approvalFormService;

    @Autowired
    private ApprovalLineService approvalLineService;

    @Autowired
    private VacationStatusService vacationStatusService;

    @Autowired
    private ExpenseService expenseService;

    /**
     * 실시간 알림용
     */
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 결재 문서 생성
    @GetMapping("/new")
    public String newApprovalForm(Model model) {
        //결재문서 양식 제목만 갖고오는거(초기에 가져와야함)
        List<ApprovalFormVO> formTitles = approvalFormService.getApprovalFormTitles();
        model.addAttribute("formTitles", formTitles);

        return "approval/approvalForm";
    }


    //이번에는 첨부파일도 받아야하기때문에 requestPart로 받는다
    //진짜 기안임
    @PostMapping(value = "/draft", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<String> draftApproval(
            @RequestPart("jsonParam") ApprovalDocumentDTO dto,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments,
            Authentication authentication) {
        try {
            //log.info("기안 문서 작성 시작 - DTO: {}", dto);

            // 사원 정보 추출
            AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
            AccountVO account = principal.getAccount();
            if (!(account instanceof EmployeeVO)) {
                //log.error("사원 정보를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사원 정보를 찾을 수 없습니다.");
            }
            EmployeeVO myEmp = (EmployeeVO) account;
            //log.info("기안자 정보 - 사번: {}, 이름: {}", myEmp.getEmpId(), myEmp.getEmpName());

            // 문서 등록 처리
            String documentId;
            try {
                documentId = approvalFormService.processApprovalDraft(dto, attachments, myEmp);
                //log.info("문서 등록 완료 - 문서ID: {}", documentId);
            } catch (Exception e) {
                //log.error("문서 등록 중 오류 발생", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("문서 등록 중 오류가 발생했습니다: " + e.getMessage());
            }

            try {
                // 배열의 첫 번째 요소(인덱스 0)가 1차 결재자
                String receiverId = dto.getApprovers().stream()
                        .findFirst()
                        .map(approver -> {
                            String empId = (String) approver.get("EMPID");  // 키가 "EMPID"임
                            //log.info("1차 결재자 ID 찾음: {}", empId);
                            return empId;
                        })
                        .orElseThrow(() -> new RuntimeException("1차 결재자를 찾을 수 없습니다."));

                // WebSocket 알림 전송
                Map<String, Object> payload = new HashMap<>();
                payload.put("title", "결재 승인 알림");
                payload.put("content", myEmp.getEmpName() + "님의 기안 문서가 도착했습니다 문서를 확인해주세요.");
                payload.put("senderId", myEmp.getEmpId());
                payload.put("sender", myEmp.getEmpName());
                payload.put("documentId", documentId);
                payload.put("receiverId", receiverId); // 추가
                
                messagingTemplate.convertAndSendToUser(
                        receiverId,
                        "/queue/FA",
                        payload
                );
                log.info("WebSocket 메시지 전송: receiverId={}, payload={}", receiverId, payload);

                return ResponseEntity.ok(documentId);
            } catch (Exception e) {
                //log.error("기안 문서 처리 중 예상치 못한 오류 발생", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("처리 중 오류가 발생했습니다: " + e.getMessage());
            }
        } catch (Exception e) {
            //log.error("처리 중 예상치 못한 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    //결재자추가하기
    @PostMapping("/{lineId}/appendApprovers")
    @ResponseBody
    public ResponseEntity<String> appendApprovers(
            @PathVariable String lineId,
            @RequestBody Map<String, Object> requestData) {
        try {
            // 문서 꺼내기
            String documentHtml = (String) requestData.get("documentHtml");
            log.info("받은 문서 HTML 길이: {}", documentHtml != null ? documentHtml.length() : "null");
            log.debug("받은 문서 HTML 내용: {}", documentHtml);  // 개발 환경에서만 로그 출력

            if (documentHtml == null || documentHtml.isEmpty()) {
                return ResponseEntity.badRequest().body("문서 내용이 없습니다~~!");
            }

            /**
             * @SuppressWarnings - Java 컴파일러의 타입 캐스팅 관련 경고를 무시하도록 하는 어노테이션.
             * 컴파일러 경고가 계속 표시돼서 다른 중요한 경고들을 놓칠 수 있음
             * 따라서 타입 안전성을 확신할 수 있는 상황에서는 경고를 억제하는 것이 코드 가독성에 도움이 될 수 있어서 넣음.
             */

            // 결재선(결재자들) 결재선 id로 조회해서 꺼내오기
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> approvers = approvalLineService.getSavedApproversByApprlineId(lineId);

            log.info("받은 결재자 수: {}", approvers != null ? approvers.size() : "null");
            log.debug("받은 결재자 정보: {}", approvers);
            if (approvers == null || approvers.isEmpty()) {
                return ResponseEntity.badRequest().body("결재자 정보가 없습니다!~~!");
            }

            String updatedHtml = approvalFormService.generateApprovalLineHtml(documentHtml, approvers);
            log.info("생성된 HTML 길이: {}", updatedHtml.length());

            // 실제로 결재선이 추가되었는지 확인
            boolean containsApproverList = updatedHtml.contains("approver-box");
            log.info("결재선 포함 여부: {}", containsApproverList);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                    .body(updatedHtml);
        } catch (Exception e) {
            log.error("결재선 추가 중 오류 발생한듯~!", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("결재선 추가 중 오류가 발생했습니다");
        }
    }

    // 미리보기 문서 만들기
    @PostMapping("/{apprformId}/preview")
    @ResponseBody
    public ResponseEntity<String> previewDocument(
            @PathVariable String apprformId,
            @RequestBody Map<String, Object> requestData,
            Authentication authentication) {
        try {
            AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
            AccountVO account = principal.getAccount();
            EmployeeVO myEmp = (EmployeeVO) account;

            String previewHtml = approvalFormService.processPreviewFormRenderer(apprformId, myEmp, requestData);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                    .body(previewHtml);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("미리보기 생성 중 오류가 발생했습니다.");
        }
    }


    //만약 휴가 신청서를 클릭했을때의 분기
    //나의 휴가 현황판을 비동기로 바로갖고오는코드임 - 휴가서비스와 연결
    @GetMapping("/vacation/status")
    @ResponseBody
    public VacationStatusVO getVacationStatusFromEmpId(
            Authentication authentication
    ) {
        AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
        AccountVO account = principal.getAccount();
        EmployeeVO myEmp = (EmployeeVO) account;
        return vacationStatusService.readVacationStatusThisYear(myEmp.getEmpId());
    }

    //휴가 신청서를 클릭했을때의 분기
    //휴가 종류 갖고와서 선택할때 셀렉트박스로 보여주기위한 연결
    @GetMapping("/vacation/types")
    @ResponseBody
    public List<Map<String, Object>> getVacationType() {
        return vacationStatusService.readVacationTypeList();
    }

    //지출 결의서를 클릭했을때 각종 기본정보를 갖고오는것
    @GetMapping("/expense/types")
    @ResponseBody
    public List<Map<String, Object>> getExpenseType() {
        return expenseService.readExpenseTypeList();
    }

    //지출 결의서를 클릭했을때 각종 기본정보를 갖고오는것
    @GetMapping("/expense/categories")
    @ResponseBody
    public List<Map<String, Object>> getExpenseCategory() {
        return expenseService.readExpenseCategoryList();
    }
}