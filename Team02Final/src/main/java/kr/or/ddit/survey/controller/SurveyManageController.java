package kr.or.ddit.survey.controller;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.commons.validate.UpdateGroup;
import kr.or.ddit.survey.service.SurveyService;
import kr.or.ddit.survey.vo.SurveyBoardVO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kimny
 *
 */
@Slf4j
@Controller
@RequestMapping("/{companyId}/survey/manage")
public class SurveyManageController {
	@Inject
	public SurveyService surveyService;
	
//	public static final String MODELNAME = "surveyBoard";
	
	/**
	 * 설문조사 추가 양식 폼으로 이동
	 * @return
	 */
	@GetMapping
	public String getSurveyForm() {
		return "survey/surveyInsertForm";
	}
	
	
	/**
	 * 설문조사 추가
	 * @param board
	 * @param errors
	 * @param model
	 * @return
	 */
	@PostMapping
	public ResponseEntity postSurveyForm(
			@Validated(InsertGroup.class) @RequestBody SurveyBoardVO board
			, BindingResult errors
			, Model model
			
	) {
		if(errors.hasErrors()) {
			// 검증 실패
//			String errAttrName = BindingResult.MODEL_KEY_PREFIX + MODELNAME;
//			model.addAttribute(errAttrName, errors);
			return ResponseEntity.badRequest().body("검증 실패");
		} else {
			ServiceResult result = surveyService.createSurvey(board);
			if(result==ServiceResult.OK) {
				return ResponseEntity.ok("성공");
			} else {
				return ResponseEntity.internalServerError().body("서버오류");
			}
		}
	}
	
	/**
	 * 수정 양식 폼으로 이동
	 * @param sboardNo
	 * @return
	 */
	@GetMapping("{sboardNo}/edit")
	public String getUpdateForm(
			@PathVariable("companyId") String companyId
			, @PathVariable("sboardNo") String sboardNo
			, Model model
	) {
		// board no를 통해 board 정보를 가지고 오고
		// 수정 가능한지 아닌지 확인하고(이미 진행중이거나 종료됐으면 수정 불가능함)
		// 불가능하면 디테일 form으로 이동 / 가능하면 update 폼으로 이동
		SurveyBoardVO boardDetail = surveyService.readSurveyBoardDetail(sboardNo);
		
		boolean isPossibleUpdate = surveyService.checkUpdate(boardDetail);
		
		if(isPossibleUpdate) {
			// 수정 가능
			model.addAttribute("detail", boardDetail);
			return "survey/surveyUpdateForm";
		} else {
			// 수정 불가능
			model.addAttribute("message","수정할 수 없는 설문조사입니다.");
			model.addAttribute("messageKind","error");
			return String.format("redirect:/%s/survey/%s",companyId, sboardNo);
		}
		
				
	}
	
	/**
	 * 수정 양식에서 수정하여 전송(수정)
	 * @param board
	 * @param errors
	 * @return
	 */
	@PutMapping("{sboardNo}/edit")
	@ResponseBody
	public ResponseEntity updateSurvey(
			@PathVariable("sboardNo") String sboardNo
			, @RequestBody @Validated(UpdateGroup.class) SurveyBoardVO board
			, BindingResult errors
	) {
		
		if(errors.hasErrors()) {
			// 검증 실패
			return ResponseEntity.badRequest().body("검증 실패");
		} else {
			if(sboardNo.equals(board.getSboardNo())) {
				// 둘이 같아야 업데이트 할 수 있음
				ServiceResult result = surveyService.modifySurveyBoard(board);
				
				if(result==ServiceResult.OK) {
					return ResponseEntity.ok("성공");
				} else {
					return ResponseEntity.internalServerError().body("서버 오류");
				}
			} else {
				return ResponseEntity.badRequest().body("검증 실패");
			}
			
		}
	}

	
	
	/**
	 * 설문조사 게시글 한 개 삭제
	 * @param sboardNo
	 * @return
	 */
	@DeleteMapping("{sboardNo}")
	@ResponseBody
	public ResponseEntity deleteSurvey(
		@PathVariable("sboardNo") String sboardNo
	) {
		if(sboardNo == null) {
			return ResponseEntity.badRequest().body("설문조사 게시글 번호 누락");
		} else {
			ServiceResult result = surveyService.deleteSurveyBoard(sboardNo);
			if(result==ServiceResult.OK) {
				return ResponseEntity.ok("성공");
			} else if(result==ServiceResult.PKDUPLICATED) {
				return ResponseEntity.status(HttpStatus.SC_CONFLICT).body("데이터베이스 오류");
			}else {
				return ResponseEntity.internalServerError().body("서버 오류");
			}
			
		}
	}
}
