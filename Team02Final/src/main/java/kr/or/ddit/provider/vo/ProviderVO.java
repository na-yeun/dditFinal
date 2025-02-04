package kr.or.ddit.provider.vo;

import java.io.Serializable;

import kr.or.ddit.account.vo.AccountVO;
import lombok.Data;

@Data
public class ProviderVO extends AccountVO implements Serializable{
	private String provId;
	private String provPass;
	private String provAccesskey;
	private String provSecretkey;
	private String accountMail;
	private String posiId;
	
	
	
	private String providerMail;
	private AccountVO account;
	
}
