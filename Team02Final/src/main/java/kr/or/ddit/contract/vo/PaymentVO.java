package kr.or.ddit.contract.vo;

import java.time.LocalDate;

import kr.or.ddit.organitree.vo.ContractVO;
import lombok.Data;

@Data
public class PaymentVO {
	
	private Integer rnum;
	
	private String payId;
	private Long payAmount;
	private String payStatus;
	private LocalDate payDate;
	private String payMethod;
	private String contractId;	
	
	private Integer payCount;
	private String month;
	
	private Integer payTotal;
	
	private ContractVO contract;
	
}
