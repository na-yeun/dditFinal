package kr.or.ddit.provider.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.contract.vo.EmpCountVO;
import kr.or.ddit.contract.vo.PaymentVO;
import kr.or.ddit.contract.vo.ScaleVO;
import kr.or.ddit.organitree.vo.ContractVO;

@Mapper
public interface ProviderMapper {
	

	// ====================================================================================================================
	// 격리 대상 시작 
	/**
	 *  현재 계약중인 업체 정보 (조건 : '승인' , '계약종료') (민재)
	 * @return
	 */
	public List<ContractVO> selectContractTypeCountStat(@Param("contractStart") String contractStart);
	
	
	/**
	 * 특정 월의 계약 수 조회 (조건 : '승인' , '계약종료') (민재)
	 * @return 
	 */
	public List<PaymentVO> selectMonthlyContractCountStat(@Param("contractStart") String contractStart);
	
	
	/**
	 * 업체 규모 통계 비율 (민재)
	 * @return
	 */
	public List<ContractVO> selectScaleCountStat(@Param("contractStart") String contractStart);
	
	
	/**
	 * 클라우드 스토리지 선택 용량 (민재)
	 * @return
	 */
	public List<ContractVO> selectStorageCountStat(@Param("contractStart") String contractStart);
	
	
	/**
	 * 사용 인원 수 통계 (민재)
	 * @return
	 */
	public List<ContractVO> selectEmpCountStat(@Param("contractStart") String contractStart);
	// 격리 대상 끝 
	// ====================================================================================================================
	
	/**
	 * 통계에서 사용할 연도 옵션 (민재)
	 * @return
	 */
	public List<ContractVO> selectOptionYearsList();
	
	/**
	 * 오늘의 매출 조회 
	 * @return
	 */
	public PaymentVO selectTotalPaymentLastYear(String payDate);
	
	/**
	 * 올해의 매출 조회 (민재)
	 * @return
	 */
	public PaymentVO selectTotalPaymentThisYear(String payDate);
	
	
	
	/**
	 * 사용인원수 옵션용 (민재)
	 * @return
	 */
	public List<EmpCountVO> selectEmpCountList();
	
	
	/**
	 * 업종 옵션용 (민재)
	 * @return
	 */
	public List<ContractVO> selectContractTypeList();
	
	
	
	/**
	 * 업종별 계약 분석에서 사용(민재) 
	 * @param contractStart
	 * @return
	 */
	public List<ContractVO> selectContractTypeStat(@Param("contractStart") String contractStart);
	
	
	/**
	 * 업종 계약 분석에서 바 클릭했을때 (믽재)
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> selectContractTypeCollapseStat(Map<String, Object> params);
	
	
	
	/**
	 * 월별 계약 추세 조회 (민재)
	 * @param contractStart
	 * @return
	 */
	public List<PaymentVO> selectMonthlyContractStat(@Param("contractStart") String contractStart);
	
	
	/**
	 * 월별 계약 추세 하나 클릭했을ㄸ ㅐ(민재0)
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> selectMonthlyContractCollapseStat(Map<String, Object> params);
	
	
	/**
	 * 업체 규모 별 계약 조회(민재) 
	 * @param contractStart
	 * @return
	 */
	public List<ContractVO> selectScaleContStat(@Param("contractStart") String contractStart);
	
	
	/**
	 * 업체 규모별 계약에서 상세 조회 (민재)
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> selectScaleContCollapseStat(Map<String, Object> params);
	
	
	/**
	 * 스토리지별 계약 조회(민재)
	 * @param contractStart
	 * @return
	 */
	public List<ContractVO> selectStorageContStat(@Param("contractStart") String contractStart);
	
	/**
	 * 스토리지별 계약에서 상세 조회 (민재)
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> selectStorageContCollapseStat(Map<String, Object> params);
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
