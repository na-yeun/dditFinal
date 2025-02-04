package kr.or.ddit.contract.vo;

import java.util.List;

import javax.validation.constraints.NotBlank;

import kr.or.ddit.organitree.vo.ContractVO;
import lombok.Data;

@Data
public class EmpCountVO {
	
	@NotBlank
	private String empCountId;
	private String empCount;
	private Long empCountPrice;
	
	private List<ContractVO> contractList;
}
