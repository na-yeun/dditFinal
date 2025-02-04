package kr.or.ddit.provider.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.contract.vo.EmpCountVO;
import kr.or.ddit.contract.vo.PaymentVO;
import kr.or.ddit.organitree.vo.ContractVO;
import kr.or.ddit.provider.dao.ProviderMapper;

@Service
public class ProviderServiceImpl implements ProviderService {
	
	@Autowired
	private ProviderMapper provMapper;

	
	
	
	// ======================================================================================================================
	// 격리 대상 시작 
	@Override
	public List<ContractVO> readContractTypeCountStat(String contractStart) {
			return provMapper.selectContractTypeCountStat(contractStart);
	}
			
		
	@Override
	public List<PaymentVO> readMonthlyContractCountStat(String contractStart) {
			return provMapper.selectMonthlyContractCountStat(contractStart);
	}

	@Override
	public List<ContractVO> readScaleCountStat(String contractStart) {

			return provMapper.selectScaleCountStat(contractStart);
	}

	@Override
	public List<ContractVO> readStorageCountStat(String contractStart) {
			return provMapper.selectStorageCountStat(contractStart);
	}

	@Override
	public List<ContractVO> readEmpCountStat(String contractStart) {
			return provMapper.selectEmpCountStat(contractStart);
	}

// 격리 대상 끝 
// ======================================================================================================================
	@Override
	public List<ContractVO> readOptionYearsList() {
		
		return provMapper.selectOptionYearsList();
	}


	@Override
	public PaymentVO readTotalPaymentLastYear(String payDate) {
		payDate = String.valueOf(LocalDate.now().getYear()-1);
		return provMapper.selectTotalPaymentLastYear(payDate);
	}


	@Override
	public PaymentVO readTotalPaymentThisYear(String payDate) {
		payDate = String.valueOf(LocalDate.now().getYear());
		return provMapper.selectTotalPaymentThisYear(payDate);
	}


	@Override
	public List<EmpCountVO> readEmpCountList() {
		return provMapper.selectEmpCountList();
	}


	@Override
	public List<ContractVO> readContractType() {
		return provMapper.selectContractTypeList();
	}


	
	@Override
	public List<ContractVO> readContractTypeStat(String contractStart) {
		return provMapper.selectContractTypeStat(contractStart);
	}


	@Override
	public List<Map<String, Object>> readContractTypeCollapseStat(String contractType, String contractStart) {
		Map<String, Object> params = new HashMap<>();
        params.put("contractType", contractType);
        params.put("contractStart", contractStart);
		return provMapper.selectContractTypeCollapseStat(params);
	}


	@Override
	public List<PaymentVO> readMonthlyContractStat(String contractStart) {
		return provMapper.selectMonthlyContractStat(contractStart);
	}


	@Override
	public List<Map<String, Object>> readMonthlyContractCollapseStat(String month, String contractStart) {
		Map<String , Object> params = new HashMap<String, Object>();
		params.put("payId", month);
		params.put("contractStart", contractStart);
		return provMapper.selectMonthlyContractCollapseStat(params);
	}


	@Override
	public List<ContractVO> readScaleContStat(String contractStart) {
		return provMapper.selectScaleContStat(contractStart);
	}


	@Override
	public List<Map<String, Object>> readScaleContractCollapseStat(String scaleId, String contractStart) {
		Map<String, Object> params = new HashMap<String, Object>();
		if(StringUtils.isBlank(scaleId)) {
			throw new IllegalArgumentException("scaleId값 누락");
		}
		String scaleSize = scaleId.trim();
		String contStart = contractStart.trim();
		
		params.put("scaleId", scaleSize);
		params.put("contractStart", contStart);
		return provMapper.selectScaleContCollapseStat(params);
	}


	@Override
	public List<ContractVO> readStorageContStat(String contractStart) {
		return provMapper.selectStorageContStat(contractStart);
	}


	@Override
	public List<Map<String, Object>> readStorageContractCollapseStat(String storageId, String contractStart) {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storageId", storageId);
		params.put("contractStart", contractStart);
		return provMapper.selectStorageContCollapseStat(params);
	}





	
	
	
}
