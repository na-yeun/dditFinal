package kr.or.ddit.payment.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.contract.vo.PaymentVO;
import kr.or.ddit.organitree.vo.ContractVO;

@Mapper
public interface PaymentMapper {
	
	/**
	 * 내 회사 결제 이력(민재)
	 * @return
	 */
	public List<PaymentVO> selectMyCompanyPayHistory(PaginationInfo<PaymentVO> paging);
	
	/**
	 * 전체 행 개수 조회 (민재)
	 * @param paging
	 * @return
	 */
	public int selectTotalCount(PaginationInfo<PaymentVO> paging);
	
	
	/**
	 * 내 회사 결제 이력 상세 (민재)
	 * @param companyId
	 * @param contractId
	 * @return
	 */
	public ContractVO selectMyCompanyPayDetail(@Param("companyId")String contractId);
	
	
}
