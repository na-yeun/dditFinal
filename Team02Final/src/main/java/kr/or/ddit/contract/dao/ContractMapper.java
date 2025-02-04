package kr.or.ddit.contract.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.contract.vo.EmpCountVO;
import kr.or.ddit.contract.vo.FirstSettingVO;
import kr.or.ddit.contract.vo.PaymentVO;
import kr.or.ddit.contract.vo.ScaleVO;
import kr.or.ddit.contract.vo.StorageVO;
import kr.or.ddit.organitree.vo.ContractVO;

@Mapper
public interface ContractMapper {

	
	/** 
	 * 업체 규모 옵션리스트 조회 (민재)
	 * @return
	 */
	public List<ScaleVO> selectAllScaleList();
	
	/**
	 * 스토리지 용량 옵션 리스트 조회 (민재)
	 * @return
	 */
	public List<StorageVO> selectAllStorageList();
	
	/**
	 * 사용 인원 옵션 리스트 조회 (민재)
	 * @return
	 */
	public List<EmpCountVO> selectAllEmpCountList();
	
	
	/**
	 * 페이징 처리 위한 행 개수 조회 (민쟤)
	 * @param paging
	 * @return
	 */
	public int selectTotalRecord(PaginationInfo<ContractVO> paging);
	
	/**
	 * 현재 계약중인 업체 목록 조회(민재)
	 * @return
	 */
	public List<ContractVO> selectContINGCompanyList(PaginationInfo<ContractVO> paging);
	
	
	/**
	 * 옵션에 사용할 업체규모 목록 조회 (민재)
	 * @return
	 */
	public List<ScaleVO> selectScaleList();
	
	
	/**
	 * 계약중인 업체 목록 엑셀 다운로드 (민재) 
	 * @return
	 */
	public List<ContractVO> selectDownloadExcelContractingCompanyList(Map<String, Object> condition);
	
	
	/**
	 * 한 회사의 계약 상세정보 조회 (민재) 
	 * @return
	 */
	public ContractVO selectOneCompanyDetail(String contractId);
	
	
	/**
	 * 계약중인 업체 정보 수정 (민재)
	 * @param contract
	 * @return
	 */
	public int updateContractingCompanyInfo(ContractVO contract);
	
	
	/**
	 * 계약 신청 상태가 2 (대기)인 업체 조회 - 아직 승인/반려처리가 안된 업체 (민재)
	 * @return
	 */
	public List<ContractVO> selectWaitCompanyList(PaginationInfo<ContractVO> paging);
	
	
	/**
	 * 계약 신청 상태가 대기인 업체 개수 조회 (민재)
	 * @return
	 */
	public int selectWaitTotalCount(PaginationInfo<ContractVO> paging);
	
	
	/**
	 * 계약 신청 상태기 '대기'인 업체의 신청 상세 정보 조회 (민재) 
	 * @param contractId
	 * @return
	 */
	public ContractVO selectOneWaitCompanyDetail(String contractId);
	
	
	/**
	 * 운영자가 반려 클릭시 상태코드 바꾸고 반려사유 업데이트 (민재)
	 * @param contract
	 * @return
	 */
	public int updateWaitCompanyStatusReject(ContractVO contract);
	
	
	/**
	 * 운영자가 승인 클릭 시, 상태코드(세팅대기) 바꾸고 승인날짜 업데이트 (민재)
	 * 
	 * @param contractId
	 * @return
	 */
	public int updateWaitCompanyStatusOK(ContractVO contract);
	
	/**
	 * 운영자가 반려 클릭시 payment테이블 결제상태코드 N 변경 (민재)
	 * @param contractId
	 * @return
	 */
	public int updatePaymentStatusReject(String contractId);
	
	
	/**
	 * 초기 세팅 대기업체 페이징 처리 (민재)
	 * @param paging
	 * @return
	 */
	public int selectFirstSettingTotalCount(PaginationInfo<ContractVO> paging);
	/**
	 * 초기 세팅대기업체 목록 조회 (민재)
	 * @return
	 */
	public List<ContractVO> selectWaitFirstSettingList(PaginationInfo<ContractVO> paging);
	
	
	/**
	 * 업체가 선택한 초기세팅 데이터 insert(민재)
	 * @param fSetting
	 * @return
	 */
	public int insertFirstSetting(FirstSettingVO fSetting);
	
	/**
	 * 업체가 제출한 초기세팅 데이터를 운영자가 반영 클릭 update '1' (승인)
	 * @return
	 */
	public int updateContractStatusOK(String empId); 
		
	
	/**
	 * 사용자가 제출한 신청양식 등록 로직 (민재) 결제 로직 제외 
	 * @param contract
	 * @return
	 */
	public int insertContractRequestForm(ContractVO contract);
	
	/**
	 * 사용자가 신청한 옵션값 결제 테이블 insert (민재) 
	 * @param payment
	 * @return
	 */
	public int insertPayment(PaymentVO payment);
	
	/**
	 * 계약업체 아이디 max값 조회 (민재)
	 * @param yyyymmdd
	 * @return
	 */
	public String selectMaxContractIdByJoin(String yyyymmdd);
	
	/**
	 * payId 생성 하기위한  sysdate의 max 아이디값 조회 (민재)
	 * @param yyyymmdd
	 * @return
	 */
	public String selectMaxPayIdByJoin(String aaaabbcc);
	
	
	/**
	 * 하나의 업체가 선택하여 제출한 초기세팅 정보 조회 (민재) 
	 * @param contractId
	 * @return
	 */
	public ContractVO selectOneFSettingDetail(String contractId);
	
	
	/**
	 * 운영자가 최종 승인 (반영) 버튼을 클릭하면 버킷명과 신청상태 udpate (민재) 
	 * @param contract
	 * @return
	 */
	public int updateBucketAndStatus(ContractVO contract);
	
	
	/**
	 * 최종 승인 메일에 같이 보낼 contractStart value 조회해서 가져오기 
	 * @param contractId
	 * @return
	 */
	public ContractVO selectContractStart(String contractId);
	
	
	
	
	
	
	
}
