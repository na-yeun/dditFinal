
package kr.or.ddit.employee.controller;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.account.dao.AccountMapper;
import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.validate.UpdateGroup;
import kr.or.ddit.employee.service.EmployeeService;
import kr.or.ddit.employee.service.GoogleOAuthService;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.employee.vo.OAuthVO;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/mypage")
public class EmployeeMyInfoUpdateController {
//	@Inject
//	private GoogleOAuthService oAuthService;
	
//	@Inject
//	private AccountMapper accountMapper;

//	@Autowired
//	private AuthenticationManagerBuilder authenticationManagerBuilder;
	
	@Inject
	private EmployeeService empService;

	// 마이페이지 전체 페이지
	@GetMapping
	public String getMyPagePage() {
		return "employee/emplyeeMyPage";
	}

	// 비밀번호 확인 결과 매핑
	@PostMapping
	@ResponseBody
	public Map<String, String> checkPassword(
			Authentication authentication
			, HttpSession session
			, RedirectAttributes redirectAttributes
			, @RequestParam("passCheck") String passCheck
	) {
		
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;
		
		Map<String, String> response = new HashMap<>();

		if (myEmp.getEmpPass().equals(passCheck)) {
			// 비밀번호가 일치한다면,
			session.removeAttribute("oAuth");
			redirectAttributes.addFlashAttribute("passwordCheckSuccess",true);
			response.put("success", "true");
			response.put("redirectUrl", "mypage/edit");
		} else {
			response.put("success", "false");
			response.put("message", "비밀번호가 일치하지 않습니다.");
		}
		return response;
	}

	// 비밀번호 확인창에서 비밀번호 일치시, 개인정보수정 페이지로 이동
	@GetMapping("edit")
	public String getUpdateMyInfoForm(
			Authentication authentication
			, HttpSession session
			, Model model
	) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO myEmp = (EmployeeVO) account;
		
		// 비밀번호 인증을 하고 온 사람인지 아닌지 확인해야함
		
			OAuthVO oAuth = (OAuthVO)session.getAttribute("oAuth");
			String myEmpMail =null;
			if(oAuth!=null) {
				myEmpMail = oAuth.getOauthEmpmail();
			} else {
				myEmpMail = myEmp.getEmpMail();
			}
			model.addAttribute("myEmpMail", myEmpMail);
			
			model.addAttribute("myEmp", myEmp);
			return "employee/employeeUpdateForm";
	}

	@PostMapping("edit")
	public String updateMyInfo(
			Authentication authentication
			, @PathVariable("companyId") String companyId
			, @Validated(UpdateGroup.class) EmployeeVO updateEmp
			, BindingResult errors
			, @RequestParam(value = "empImgDelete", required = false) Boolean empImgDelete
			, @RequestParam(value = "signImgDelete", required = false) Boolean signImgDelete
			, HttpSession session
			, RedirectAttributes redirectAttributes

	) {

		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;

		if (!errors.hasErrors()) {
			// 검증에 오류가 없을 때

			// 이미지 삭제 요청 했을 경우 준비
			// 프로필 이미지 삭제 처리
			if (Boolean.TRUE.equals(empImgDelete)) {
				empService.deleteEmpImg(updateEmp.getEmpId());
			}

			// 도장 이미지 삭제 처리
			if (Boolean.TRUE.equals(signImgDelete)) {
				empService.deleteEmpSignImg(updateEmp.getEmpId());
			}
			
			// session에 oauth 라는 이름으로 담겨져있으면 oauth 업데이트 요청임
			OAuthVO oAuth = (OAuthVO) session.getAttribute("oAuth");
			if(oAuth!=null) {
				updateEmp.setOAuth(oAuth);
			}
			
			ServiceResult empResult = empService.modifyMyEmployee(updateEmp);
			
			if (empResult == ServiceResult.OK) {
				// oauth insert랑 emp update 성공했을 때
				// session에 저장되어있는 데이터 초기화하기
				redirectAttributes.addFlashAttribute("message", "회원정보 수정에 성공했습니다!");
				redirectAttributes.addFlashAttribute("messageKind", "success");
			} else if(empResult == ServiceResult.PKDUPLICATED){
				// pk 충돌시
				redirectAttributes.addFlashAttribute("message", "google 계정에 문제가 있습니다. 올바른 계정으로 인증해주세요.");
				redirectAttributes.addFlashAttribute("messageKind", "error");
			} else {
				redirectAttributes.addFlashAttribute("message", "서버 오류로 회원정보 수정에 실패했습니다. 다시 시도해주세요.");
				redirectAttributes.addFlashAttribute("messageKind", "error");
			}
			
		} else {
			// 검증에 오류가 있을 때
			Map<String, String> errorMap = new HashMap<>();
			for (FieldError error : errors.getFieldErrors()) {
			    errorMap.put(error.getField(), error.getDefaultMessage());
			}
			
			
			redirectAttributes.addFlashAttribute("errorMap", errorMap);
			
			redirectAttributes.addFlashAttribute("myEmp", updateEmp);
			redirectAttributes.addFlashAttribute("message", "정보가 제대로 입력되지 않았습니다.");
			redirectAttributes.addFlashAttribute("messageKind", "error");
		}
		
		session.invalidate();
		return "redirect:/" + companyId + "/mypage/edit";
	}
}
