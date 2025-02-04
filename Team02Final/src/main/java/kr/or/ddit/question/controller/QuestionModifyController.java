 package kr.or.ddit.question.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.exception.BoardException;
import kr.or.ddit.commons.validate.UpdateGroup;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.notice.vo.NoticeVO;
import kr.or.ddit.provider.vo.ProviderVO;
import kr.or.ddit.question.service.QuestionService;
import kr.or.ddit.question.vo.CategoryVO;
import kr.or.ddit.question.vo.QuestionVO;
import kr.or.ddit.security.AccountVOWrapper;

@Controller
@RequestMapping
public class QuestionModifyController {
	public static final String MODELNAME = "targetQuestion";
	
	@Autowired
	private QuestionService service;
	
	@GetMapping("/{companyId}/question/{quNo}/edit")	// 문의 게시판 - 게시글 수정 폼 이동
	public String questionEditForm(Model model,
        @PathVariable("quNo") String quNo) {
		List<CategoryVO> categoryList = service.readCategoryList();
		model.addAttribute("categoryList", categoryList);
		model.addAttribute(MODELNAME, service.readQuestion(quNo));
		return "question/questionEdit";
	}
	
	@PostMapping("/{companyId}/question/{quNo}/edit")	// 문의 게시판 - 게시글 수정
	public String questionUpdate(
			@Validated(UpdateGroup.class) @ModelAttribute(MODELNAME) QuestionVO question,
            @PathVariable("companyId") String companyId,
            @PathVariable("quNo") String quNo,
            BindingResult errors,
            RedirectAttributes redirectAttributes) {
		String lvn = null;
		if (!errors.hasErrors()) {
			try {
				service.modifyQuestion(question);
				redirectAttributes.addFlashAttribute("message", "수정이 완료되었습니다.");
				redirectAttributes.addFlashAttribute("messageKind", "success");
				lvn = "redirect:/" + companyId + "/question/"+quNo;
			} catch (BoardException e) {
				redirectAttributes.addFlashAttribute(MODELNAME, question);
				redirectAttributes.addFlashAttribute("message", e.getMessage());
				redirectAttributes.addFlashAttribute("messageKind", "error");
				lvn = "redirect:/" + companyId + "/question/edit";
			}
		} else {
			// 구체적인 에러 메시지 처리
			if (errors.hasFieldErrors()) {
				String errorMessage = errors.getFieldErrors().stream().map(error -> error.getDefaultMessage())
						.findFirst().orElse("필수 항목이 누락되었습니다.");

				redirectAttributes.addFlashAttribute("message", errorMessage);
				redirectAttributes.addFlashAttribute("messageKind", "error");
			}
			redirectAttributes.addFlashAttribute(MODELNAME, question);
			redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX, errors);
			lvn = "redirect:/" + companyId + "/question/edit";
		}
		question.setAtchFile(null);
		return lvn;
	}
	
	@DeleteMapping("/{companyId}/question/{quNo}") // 문의 게시판 - 게시글 삭제
	public ResponseEntity<?> questionDelete(
			@PathVariable String quNo
			) {
	    service.removeQuestion(quNo); // 삭제 처리
	    return ResponseEntity.ok().build(); // 성공 응답 반환
	}
	
	@PostMapping("/{companyId}/question/{quNo}/answ")
	public ResponseEntity<?> updateAnsw(
		@PathVariable String quNo,
		@RequestParam String answContent,
		Authentication authentication
		){
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO emp = (EmployeeVO) account;
		String userId = emp.getEmpId();
		QuestionVO question = service.readQuestion(quNo);
		question.setAnswerId(userId);
		question.setAnswContent(answContent);
		service.updateAnswer(question);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("contract/question/{quNo}/answ")
	public ResponseEntity<?> constractUpdateAnsw(
		@PathVariable String quNo,
		@RequestParam String answContent,
		Authentication authentication
		){
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		ProviderVO prov = (ProviderVO) account;
		String userId = prov.getProvId();
		QuestionVO question = service.readQuestion(quNo);
		question.setAnswerId(userId);
		question.setAnswContent(answContent);
		service.updateAnswer(question);
		return ResponseEntity.ok().build();
	}
	
}
