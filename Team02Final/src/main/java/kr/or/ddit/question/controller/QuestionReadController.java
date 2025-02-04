package kr.or.ddit.question.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.paging.SimpleCondition;
import kr.or.ddit.commons.paging.renderer.DefaultPaginationRenderer;
import kr.or.ddit.commons.paging.renderer.PaginationRenderer;
import kr.or.ddit.question.service.QuestionService;
import kr.or.ddit.question.vo.CategoryVO;
import kr.or.ddit.question.vo.QuestionVO;

@Controller
@RequestMapping
public class QuestionReadController {

	@Autowired
	private QuestionService service;
	
	@GetMapping("/{companyId}/question")	// 문의 게시판 페이지 이동
	public String questionList(
		@RequestParam(required = false, defaultValue = "1") int page,
		@ModelAttribute("condition") SimpleCondition simpleCondition,
		Model model
	) {                                 
		PaginationInfo<QuestionVO> paging = new PaginationInfo<QuestionVO>();
		paging.setCurrentPage(page);
		paging.setSimpleCondition(simpleCondition);
		PaginationRenderer renderer = new DefaultPaginationRenderer();
		List<QuestionVO> questionList = service.readQuestionList(paging);
		
		String pagingHTML  = renderer.renderPagination(paging, "fnPaging");
		
		model.addAttribute("QuestionList", questionList);
		model.addAttribute("pagingHTML", pagingHTML);
		return "question/questionList";
	}
	
	@GetMapping("/{companyId}/question/{quNo}")	// 문의 게시판 상세 조회
	public String questionDetail(@PathVariable("quNo") String quNo, Model model) {
		List<CategoryVO> categoryList = service.readCategoryList();
		QuestionVO question = service.readQuestion(quNo);
		model.addAttribute("categoryList", categoryList);
		model.addAttribute("question", question);
		return "question/questionDetail";
	}
	
	@GetMapping("/contract/question")
	public String contractQuestion(@RequestParam(required = false, defaultValue = "1") int page,
			@ModelAttribute("condition") SimpleCondition simpleCondition,
			Model model) {
		PaginationInfo<QuestionVO> paging = new PaginationInfo<QuestionVO>();
		paging.setCurrentPage(page);
		paging.setSimpleCondition(simpleCondition);
		PaginationRenderer renderer = new DefaultPaginationRenderer();
		List<QuestionVO> questionList = service.readQuestionList(paging);
		
		String pagingHTML  = renderer.renderPagination(paging, "fnPaging");
		
		model.addAttribute("QuestionList", questionList);
		model.addAttribute("pagingHTML", pagingHTML);
		return "provider/main/provQuestion.jsp";
	}
	
	@GetMapping("/contract/question/{quNo}")	// 문의 게시판 상세 조회
	public String contractQuestionDetail(@PathVariable("quNo") String quNo, Model model) {
		List<CategoryVO> categoryList = service.readCategoryList();
		QuestionVO question = service.readQuestion(quNo);
		model.addAttribute("categoryList", categoryList);
		model.addAttribute("question", question);
		return "provider/main/provQuestionDetail.jsp";
	}
	
}
