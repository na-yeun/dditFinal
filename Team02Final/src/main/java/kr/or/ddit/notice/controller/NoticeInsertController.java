package kr.or.ddit.notice.controller;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.notice.service.NoticeService;
import kr.or.ddit.notice.vo.NoticeVO;
import kr.or.ddit.security.AccountVOWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/{companyId}/notice")
public class NoticeInsertController {
    public static final String MODELNAME = "newNotice";

    @Autowired
    private NoticeService service; // 서비스 인터페이스 주입 (BoardService와 유사한 구조 가정)

    @ModelAttribute(MODELNAME)
    public NoticeVO notice() {
        return new NoticeVO();
    }

    // 공지사항 등록 폼 이동
    @GetMapping("/newForm")
    public String newNoticeForm(
            @PathVariable("companyId") String companyId,
            @ModelAttribute(MODELNAME) NoticeVO notice) {
        return "notice/noticeForm";
    }

    // 공지사항 등록 처리
    @PostMapping("/new")
    public String createNotice(
            @PathVariable("companyId") String companyId,
            @Validated(InsertGroup.class) @ModelAttribute(MODELNAME) NoticeVO notice,
            BindingResult errors,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute(MODELNAME, notice);

        String lvn = null;
        if(!errors.hasErrors()) {
            AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
            AccountVO account = principal.getAccount();

            EmployeeVO myEmp = (EmployeeVO)account;
            if (myEmp != null) {
                notice.setEmpId(myEmp.getEmpId()); // EmployeeVO의 empId 필드 값 설정
            } else {
                redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
                return "redirect:/{companyId}/login"; // 로그인 페이지로 리다이렉트
            }
            ServiceResult result = service.createNotice(notice);
            switch (result) {
                case OK:
                    // 등록 성공시 목록 페이지로 이동 (회사 ID 포함)
                    lvn = "redirect:/" +companyId + "/notice";
                    break;
                case FAIL:
                    lvn = "redirect:/" + companyId + "/notice/new";
                    redirectAttributes.addFlashAttribute("message", "서버 오류입니다. 다시 시도해주세요.");
                    break;
                default:
                    lvn = "redirect:/" + companyId + "/notice/new";
                    redirectAttributes.addFlashAttribute("message", "알 수 없는 오류가 발생했습니다.");
                    break;
            }
        } else {
            // 구체적인 에러 메시지 처리
            if (errors.hasFieldErrors()) {
                // 필드 에러 메시지 가져오기
                String errorMessage = errors.getFieldErrors()
                        .stream()
                        .map(error -> error.getDefaultMessage())
                        .findFirst()
                        .orElse("필수 항목이 누락되었습니다.");

                redirectAttributes.addFlashAttribute("message", errorMessage);
                redirectAttributes.addFlashAttribute("messageKind", "error"); // SweetAlert용 타입
            }
            lvn = String.format("redirect:/%s/notice/newForm", companyId);
        }


        return lvn;
    }
}

