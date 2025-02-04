package kr.or.ddit.payment.service;

import java.util.List;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.contract.vo.PaymentVO;
import kr.or.ddit.organitree.vo.ContractVO;

public interface PaymentService {
	
	
	/** 
	 * 내 회사 결제이력 조회 (민재)
	 * @param paging
	 * @return
	 */
	public List<PaymentVO> readMyCompanyPayHistory(PaginationInfo<PaymentVO> paging);
	
	
	/**
	 * 내 회사 결제 상세 조회 (민재) 
	 * @param contractId
	 * @return
	 */
	public ContractVO readMyCompanyPayDetail(String contractId);
	
}
