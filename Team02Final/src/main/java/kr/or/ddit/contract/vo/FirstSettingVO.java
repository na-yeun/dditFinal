package kr.or.ddit.contract.vo;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;

import kr.or.ddit.organitree.vo.ContractVO;
import lombok.Data;

@Data
public class FirstSettingVO {
	@NotBlank
	private String contractId;
	private String firstPosition;
	private String firstDepart;
	private String firstEmploy;
	private String firstAttend;
	private String useElec;            // 전자결재 사용 여부 (yes/no)
	private String firstElec;
	private LocalDate firstRequestDate;
	
	
	private ContractVO contract;
	
}
