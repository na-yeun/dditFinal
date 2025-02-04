package kr.or.ddit.question.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.exception.BoardException;
import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.notice.vo.NoticeVO;
import kr.or.ddit.question.service.QuestionService;
import kr.or.ddit.question.vo.CategoryVO;
import kr.or.ddit.question.vo.QuestionVO;
import kr.or.ddit.security.AccountVOWrapper;

@Controller
@RequestMapping("/{companyId}/question/new")
public class QuestionInsertController {
	
	@Autowired
	private QuestionService service;
	
	public static final String MODELNAME = "newQuestion";
	
	@ModelAttribute(MODELNAME)
	public QuestionVO questionVo() {
		return new QuestionVO();
	}
	
	@GetMapping // 문의 게시판 작성 form 이동
	public String questionInsertForm(Model model) {
		List<CategoryVO> categoryList = service.readCategoryList();
		model.addAttribute("categoryList", categoryList);
		
//		model.addAttribute(MODELNAME, new QuestionVO());
		return "question/questionForm";
	}
	              
	
	@PostMapping
	public String questionInsert(
	        @PathVariable("companyId") String companyId,
	        @Validated(InsertGroup.class) @ModelAttribute(MODELNAME) QuestionVO question,
	        BindingResult errors,
	        Authentication authentication,
	        RedirectAttributes redirectAttributes,
	        Model model) {

	    if (errors.hasErrors()) {
	        // 유효성 검증 실패 시 기존 입력 데이터와 오류 메시지를 반환
	        model.addAttribute("categoryList", service.readCategoryList());
	        model.addAttribute("message", "필수 항목이 누락되었거나 잘못 입력되었습니다.");
	        return "question/questionForm";
	    }

	    AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
	    AccountVO account = principal.getAccount();

	    EmployeeVO myEmp = (EmployeeVO) account;
	    if (myEmp != null) {
	        question.setQuestionId(myEmp.getEmpId());
	    } else {
	        redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
	        return "redirect:/{companyId}/login";
	    }

	    ServiceResult result = service.createQuestion(question);
	    switch (result) {
	        case OK:
	            return "redirect:/" + companyId + "/question";
	        case FAIL:
	            redirectAttributes.addFlashAttribute("message", "서버 오류입니다. 다시 시도해주세요.");
	            return "redirect:/" + companyId + "/question/new";
	        default:
	            redirectAttributes.addFlashAttribute("message", "알 수 없는 오류가 발생했습니다.");
	            return "redirect:/" + companyId + "/question/new";
	    }
	}

}
