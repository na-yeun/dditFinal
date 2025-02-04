package kr.or.ddit.contract.vo;

import java.util.List;

import javax.validation.constraints.NotBlank;

import kr.or.ddit.organitree.vo.ContractVO;
import lombok.Data;

@Data
public class StorageVO {
	@NotBlank
	private String storageId;
	private String storageSize;
	private Long storagePrice;
	
	private List<ContractVO> contractList;
}
