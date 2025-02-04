package kr.or.ddit.contract.service;

import java.util.List;
import java.util.Map;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.contract.vo.EmpCountVO;
import kr.or.ddit.contract.vo.FirstSettingVO;
import kr.or.ddit.contract.vo.ScaleVO;
import kr.or.ddit.contract.vo.StorageVO;
import kr.or.ddit.organitree.vo.ContractVO;

public interface ContractService {
	
	/**
	 * 업체 규모 옵션 리스트 조회 (민재)
	 * @return
	 */
	public List<ScaleVO> readAllScaleList();
	
	/**
	 * 사용인원 옵션 리스트 조회 (민재)
	 * @return
	 */
	public List<EmpCountVO> readAllEmpCountList();
	
	
	/**
	 * 스토리지 용량 옵션 리스트 조회 (민재)
	 * @return
	 */
	public List<StorageVO> readAllStorageList();
	
	
	/**
	 * 현재 계약중인 업체 목록 조회 
	 * @param paging
	 * @return
	 */
	public List<ContractVO> readContINGCompanyList(PaginationInfo<ContractVO> paging);
	
	
	/**
	 * 업체 규모 옵션 리스트 조회 (민재) 위에랑 똑같은데 까먹고 새로씀;
	 * @return
	 */
	public List<ScaleVO> readScaleList();
	
	/**
	 * 계약업체 목록 엑셀 다운로드 사용 (민재)
	 * @param condition
	 * @return
	 */
	public List<ContractVO> readDownloadExcelContractingCompanyList(Map<String, Object> condition);
	
	/**
	 * 한 회사의 계약 상세정보 조회 (민재)
	 * @return
	 */
	public ContractVO readOneCompanyDetail(String contractId);
	
	
	/**
	 * 계약중인 업체 정봇 수정 (민재) 
	 * @param contract
	 * @return
	 */
	public ServiceResult modifyContractingCompanyInfo(ContractVO contract);
	
	
	/**
	 * 계약신청상태가 2 (대기)인 업체 목록 조회 (민재) 
	 * @return
	 */
	public List<ContractVO> readWaitCompanyList(PaginationInfo<ContractVO> paging);
	
	
	/**
	 * 계약신청 상태가 2 (대기)인 업체의 신청 상세 조회 (민재) 
	 * @param contractId
	 * @return
	 */
	public ContractVO readOneWaitCompanyDetail(String contractId);
	
	/**
	 * 운영자가 반려 버튼 클릭시 처리 로직 (민재)
	 * @param contractId
	 * @return
	 */
	public ServiceResult modifyWaitCompanyStatusReject(ContractVO contract);
	
	
	/**
	 * 운영자가 승인 버튼 클릭시 처리 로직 (민재)
	 * @param contract
	 * @return
	 */
	public ServiceResult modifyWaitCompanyStatusOK(ContractVO contract);
	
	
	/**
	 * 운영자가 승인 한 후 신청상태가 '세팅 대기'인 목록 (민재)
	 * @param paging
	 * @return
	 */
	public List<ContractVO> readWaitFirstSettingList(PaginationInfo<ContractVO> paging);
	
	
	/**
	 * 업체의 초기세팅 데이터 등록 
	 * @param fSetting
	 * @return
	 */
	public ServiceResult addFirstSetting(FirstSettingVO fSetting);
	
	
	/**
	 * 업체의 신규계약양식 작성 데이터 등록 + 결제 로직 
	 * @param contract
	 * @param payAmount
	 * @return
	 */
	public ServiceResult addContractRequestForm(ContractVO contract, String payAmount);
	
	
	/**
	 * 하나의 업체 세팅상세정보 조회 
	 * @param contractId
	 * @return
	 */
	public ContractVO readOneFSettingDetail(String contractId);
	
	
	/**
	 * 운영자가 최종 승인 (반영) 버튼을 클릭하면 버킷명과 신청상태 udpate (민재) 
	 * @param contract
	 * @return
	 */
	public ServiceResult modifyBucketAndStatus(ContractVO contract);
	
	
}
