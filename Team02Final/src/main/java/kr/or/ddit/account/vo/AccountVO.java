package kr.or.ddit.account.vo;

import java.util.List;

import javax.validation.constraints.NotBlank;

import kr.or.ddit.commons.validate.LoginGroup;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.employee.vo.OAuthVO;
import kr.or.ddit.provider.vo.ProviderVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "accountMail")
public class AccountVO {
	@NotBlank(groups = LoginGroup.class)
	private String accountMail;
	
	@NotBlank(groups = LoginGroup.class)
	private String accountPass;
	
	private String accountYn;
	
	
	private String userType;
	private List<String> roles;
	
	private EmployeeVO employee;
	private ProviderVO provider;
	private OAuthVO oauth;

}
