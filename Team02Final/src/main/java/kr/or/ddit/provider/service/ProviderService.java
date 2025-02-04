package kr.or.ddit.provider.service;

import java.util.List;
import java.util.Map;

import com.google.api.client.util.Maps;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.contract.vo.EmpCountVO;
import kr.or.ddit.contract.vo.PaymentVO;
import kr.or.ddit.organitree.vo.ContractVO;

public interface ProviderService {
	
	
	
	/** 
	 * 업종별 계약 분석에서 사용(민재) 
	 * @param contractStart
	 * @return
	 */
	public List<ContractVO> readContractTypeStat(String contractStart);
	
	/**
	 * 업종 계약 분석에서 바 클릭했을때 (믽재)
	 * @param contractType
	 * @param contractStart
	 * @return
	 */
	public List<Map<String, Object>> readContractTypeCollapseStat(String contractType, String contractStart);

	
	/** 월별 계약 추세 조회 (민쟤) 
	 * @param contractStart
	 * @return
	 */
	public List<PaymentVO> readMonthlyContractStat(String contractStart);
	
	/**
	 * 월별 계약에서 하나 클릭(민재)
	 * @param month
	 * @param contractStart
	 * @return
	 */
	public List<Map<String, Object>> readMonthlyContractCollapseStat(String month , String contractStart);
	
	/**
	 * 업체규모 통계 조회 (민재)
	 * @param contractStart
	 * @return
	 */
	public List<ContractVO> readScaleContStat(String contractStart);
	
	
	/** 업체규모 통계에서 상세 조회 (민재)
	 * @param scaleId
	 * @param contractStart
	 * @return
	 */
	public List<Map<String, Object>> readScaleContractCollapseStat(String scaleId,String contractStart);
	
	
	
	/**
	 * 스토리지별 계약 조회(민재)
	 * @param contractStart
	 * @return
	 */
	public List<ContractVO> readStorageContStat(String contractStart);
	
	/**
	 * 스토리지별 계약 상세조회 (민쟤)
	 * @param storageId
	 * @param contractStart
	 * @return
	 */
	public List<Map<String, Object>> readStorageContractCollapseStat(String storageId, String contractStart);
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ======================================================================================================================
	// 격리 대상 시작 
	/**
	 * 현재 계약중인 업체 정보 (조건 : '승인' , '계약종료') (민재)
	 * @param contractStart
	 * @return
	 */
	public List<ContractVO> readContractTypeCountStat(String contractStart);
	
	/**
	 * 특정 월의 계약 수 조회 (조건 : '승인' , '계약종료') (민재)
	 * @return
	 */
	public List<PaymentVO> readMonthlyContractCountStat(String contractStart);
	
	/**
	 * 업체 규모 통계 비율 (민재)
	 * @param contractStart
	 * @return
	 */
	public List<ContractVO> readScaleCountStat(String contractStart);
	
	/**
	 * 클라우드 스토리지 선택 용량 (민재)
	 * @param contractStart
	 * @return
	 */
	public List<ContractVO> readStorageCountStat(String contractStart);
	
	/**
	 * 사용 인원 수 통계 (민재)
	 * @param contractStart
	 * @return
	 */
	public List<ContractVO> readEmpCountStat(String contractStart);
	
	
	// 격리 대상 끝 
	// ======================================================================================================================
	/**
	 * 통계에서 사용할 연도 옵션 (민재)
	 * @return
	 */
	public List<ContractVO> readOptionYearsList();
	
	
	/**
	 * 오늘의 매출 조회 (민재)
	 * @return
	 */
	public PaymentVO readTotalPaymentLastYear(String payDate);
	
	
	/**
	 * 올해의 매출 조회 (민재)
	 * @param payDate
	 * @return
	 */
	public PaymentVO readTotalPaymentThisYear(String payDate);
	
	/**
	 * 사용인원수 옵션용 (민재)
	 * @return
	 */
	public List<EmpCountVO> readEmpCountList();
	
	/**
	 * 업종 옵션 리스트 조회(민쟤)
	 * @return
	 */
	public List<ContractVO> readContractType();
	
	
	
	
}
