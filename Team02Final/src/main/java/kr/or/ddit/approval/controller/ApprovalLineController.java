package kr.or.ddit.approval.controller;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.approval.service.ApprovalLineService;
import kr.or.ddit.approval.vo.ApprovalLineVO;
import kr.or.ddit.approval.vo.ApproverVO;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/{companyId}/approval/line")
public class ApprovalLineController {

    @Autowired
    ApprovalLineService approvalLineService;

    //저장되어있는 결재선을 갖고오는 메소드임
    @GetMapping("/saved")
    @ResponseBody
    public ResponseEntity<?> getSavedLines(
            Authentication authentication){
        try {
            AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
            AccountVO account = principal.getAccount();

            EmployeeVO myEmp = (EmployeeVO)account;
            List<Map<String, Object>> savedLines = approvalLineService.getSavedLines(myEmp.getEmpId());
            return ResponseEntity.ok(savedLines);
        } catch (Exception e) {
            log.error("저장된 결재선 조회 중 오류가 발생함 !", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("결재선 조회 중 오류 발생 했다!");
        }
    }

    //결재선 저장하는 메소드임
    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<?> saveApprovalLine(
            // 자바스크립트에서 이미 순서를 정해진 리스트형식으로 오기때문에 순서걱정할필요가없다
            @RequestBody ApprovalLineVO approvalLine,
            Authentication authentication) {
        try {
            AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
            AccountVO account = principal.getAccount();
            EmployeeVO myEmp = (EmployeeVO)account;

            //결재선과 결재선안에 있는 결재자 만들고 오기
            Map<String, Object> savedLine =
                    approvalLineService.saveApprovalLine(approvalLine, myEmp.getEmpId());
            return ResponseEntity.ok(savedLine);
        } catch (Exception e) {
            log.error("결재선 저장 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("결재선 저장 중 오류 발생한듯");
        }
    }

    //결재선 삭제하는 메소드임
    @DeleteMapping("/{lineId}")
    @ResponseBody
    public ResponseEntity<?> deleteApprovalLine(
            @PathVariable String lineId) {
        try {
            approvalLineService.removeApprovalLine(lineId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("결재선 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("결재선 삭제 중 오류가 발생했습니다.");
        }
    }
}
