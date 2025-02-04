package kr.or.ddit.approval.controller;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.approval.service.ApprovalService;
import kr.or.ddit.approval.vo.ApprovalVO;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/{companyId}/approval")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    //결재 승인했을때
    @PostMapping("/approve/{docId}")
    public ResponseEntity<String> approveDocument(
            @PathVariable String docId,
            @RequestBody ApprovalVO approval,
            Authentication authentication) throws IOException {
        try{
            AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
            AccountVO account = principal.getAccount();
            EmployeeVO myEmp = (EmployeeVO) account;

            String message = approvalService.approveDocument(docId, myEmp, approval);
            return ResponseEntity.ok(message);

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("승인 검증 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            log.error("승인 처리 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("결재 처리 중 오류가 발생했습니다.");
        }
    }

    //결재 반려했을때
    @PostMapping("/reject/{docId}")
    public ResponseEntity<String> rejectDocument(
            @PathVariable String docId,
            @Valid @RequestBody ApprovalVO approval,
            Authentication authentication
    ) {
        try {
            if (StringUtils.isEmpty(approval.getApprovalComment())) {
                return ResponseEntity.badRequest().body("반려 시에는 의견 작성이 필수입니다.");
            }

            AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
            AccountVO account = principal.getAccount();
            EmployeeVO myEmp = (EmployeeVO) account;

            // 의견 필수 체크는 서비스에서 처리한다
            String message = approvalService.rejectDocument(docId, myEmp, approval);
            return ResponseEntity.ok(message);

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("반려 검증 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            log.error("반려 처리 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("반려 처리 중 오류가 발생했습니다.");
        }
    }
    
    

    //전결처리 했을때
    @PostMapping("/finalApprove/{docId}")
    public ResponseEntity<String> finalApproveDocument(
            @PathVariable String docId,
            @RequestBody ApprovalVO approval,
            Authentication authentication
    ) {
        try {
            AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
            EmployeeVO myEmp = (EmployeeVO) principal.getAccount();

            String message = approvalService.approveFinalDocument(docId, myEmp, approval);
            return ResponseEntity.ok(message);

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("전결 검증 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            log.error("전결 처리 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("전결 처리 중 오류가 발생했습니다.");
        }
    }
}
