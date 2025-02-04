package kr.or.ddit.payment.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.contract.vo.PaymentVO;
import kr.or.ddit.organitree.vo.ContractVO;
import kr.or.ddit.payment.dao.PaymentMapper;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private PaymentMapper paymentMapper;

	@Override
	public List<PaymentVO> readMyCompanyPayHistory(PaginationInfo<PaymentVO> paging) {
		
		if(paging != null) {
			int totalRecord = paymentMapper.selectTotalCount(paging);
			paging.setTotalRecord(totalRecord);
		}
		return paymentMapper.selectMyCompanyPayHistory(paging);
		
		
	}

	@Override
	public ContractVO readMyCompanyPayDetail(String contractId) {
		if(StringUtils.isBlank(contractId)) {
			throw new IllegalArgumentException("계약업체 아이디값 누락");
		}
		return paymentMapper.selectMyCompanyPayDetail(contractId);
	}
	
	
	
}
