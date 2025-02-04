package kr.or.ddit.notice.controller;


import kr.or.ddit.commons.exception.BoardException;
import kr.or.ddit.commons.validate.UpdateGroup;
import kr.or.ddit.notice.service.NoticeService;
import kr.or.ddit.notice.vo.NoticeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/{companyId}/notice")
public class NoticeModifyController {

    public static final String MODELNAME = "targetNotice";
    @Autowired
    private NoticeService service;

//    @GetMapping("{NoticeNo}/auth")
//    public String authenticateForm() {
//        return "/authForm";
//    }

    @GetMapping("/{noticeNo}/edit")
    public String editNoticeForm(Model model,
                                 @PathVariable("noticeNo") String noticeNo,
                                 @PathVariable("companyId") String companyId) {
        model.addAttribute(MODELNAME, service.readNoticeDetail(noticeNo));
        return "notice/noticeEdit";
    }

    @PostMapping("/{noticeNo}/edit")
    public String updateNotice(@Validated(UpdateGroup.class)
                               @ModelAttribute(MODELNAME) NoticeVO notice,
                               @PathVariable("companyId") String companyId,
                               BindingResult errors,
                               RedirectAttributes redirectAttributes) {

        String lvn = null;
        if (!errors.hasErrors()) {
            try {
                service.modifyNotice(notice);
                redirectAttributes.addFlashAttribute("message", "수정이 완료되었습니다.");
                redirectAttributes.addFlashAttribute("messageKind", "success");
                lvn = "redirect:/" + companyId + "/notice/{noticeNo}";
            } catch (BoardException e) {
                redirectAttributes.addFlashAttribute(MODELNAME, notice);
                redirectAttributes.addFlashAttribute("message", e.getMessage());
                redirectAttributes.addFlashAttribute("messageKind", "error");
                lvn = "redirect:/" + companyId + "/notice/edit";
            }
        } else {
            // 구체적인 에러 메시지 처리
            if (errors.hasFieldErrors()) {
                String errorMessage = errors.getFieldErrors()
                        .stream()
                        .map(error -> error.getDefaultMessage())
                        .findFirst()
                        .orElse("필수 항목이 누락되었습니다.");

                redirectAttributes.addFlashAttribute("message", errorMessage);
                redirectAttributes.addFlashAttribute("messageKind", "error");
            }
            redirectAttributes.addFlashAttribute(MODELNAME, notice);
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX, errors);
            lvn = "redirect:/" + companyId + "/notice/edit";
        }
        notice.setAtchFile(null);
        return lvn;
    }

    /**게시글 삭제**/
    @DeleteMapping("/{noticeNo}/delete")
    public String delete(@PathVariable String noticeNo,
                         @PathVariable String companyId,
                         RedirectAttributes redirectAttributes) {
        try {
            service.removeNotice(noticeNo);
            return "redirect:/" +companyId + "/notice";
        }catch (BoardException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/notice/{noticeNo}";
        }
    }
}
