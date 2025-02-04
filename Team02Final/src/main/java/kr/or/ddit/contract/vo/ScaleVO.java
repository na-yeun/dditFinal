package kr.or.ddit.contract.vo;

import java.util.List;

import javax.validation.constraints.NotBlank;

import kr.or.ddit.organitree.vo.ContractVO;
import lombok.Data;

@Data
public class ScaleVO {
	@NotBlank
	private String scaleId;
	private String scaleSize;
	private Long scalePrice;
	
	private List<ContractVO> contractList;
}
