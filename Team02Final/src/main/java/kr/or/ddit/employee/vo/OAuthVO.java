package kr.or.ddit.employee.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(of = "oauthEmpmail")
@NoArgsConstructor
public class OAuthVO {
	private String empId;
	private String oauthAccess;
	private String oauthRefresh;
	private String oauthEmpmail;
}
