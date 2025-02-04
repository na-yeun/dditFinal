package kr.or.ddit.organitree.vo;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import kr.or.ddit.contract.vo.EmpCountVO;
import kr.or.ddit.contract.vo.FirstSettingVO;
import kr.or.ddit.contract.vo.PaymentVO;
import kr.or.ddit.contract.vo.ScaleVO;
import kr.or.ddit.contract.vo.StorageVO;
import lombok.Data;

@Data
public class ContractVO {
	private int rnum;
	
	@NotBlank
	private String contractId;
	
	private String contractCompany;
	
	private String contractName;
	
	@Pattern(regexp = "\\d{11}", message = "전화번호는 숫자 11자리여야 합니다.")
	private String contractTel;
	
	private String contractEmail;
	
	private String contractStart;
	
	private String contractEnd;
	
	private LocalDate contractApprovalDate;
	
	private String contractBucket;
	
	private String contractType;
	
	private String contractStatus;
	
	private String contractReject;
	
	private String empCountId;
	
	private String provId;
	
	private String storageId;
	
	private String scaleId;
	
	private String contractAddr1;
	private String contractAddr2;
	
	
	private LocalDate payDate;
	private LocalDate firstRequestDate;
	// 계약 남은일 조회 위해 추가 (민재)
	@Min(value = 0)
	private int remainDays;
	
	private Integer contractCount;
	private Integer scaleCount;
	private Integer storageCount;
	private Integer empCnt;
	
	
	
	private List<DepartmentVO> departmentList;
	
	private List<PaymentVO> paymentList;
	private ScaleVO scale;
	private EmpCountVO empCount;
	private StorageVO storage;
	private FirstSettingVO fSetting;
}
