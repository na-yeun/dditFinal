package kr.or.ddit.vacation.service;

import java.util.List;
import java.util.Set;

import kr.or.ddit.approval.dto.ApprovalDocumentDTO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.vacation.vo.VacationDTO;
import kr.or.ddit.vacation.vo.VacationHistoryVO;

public interface VacationHistoryService {

	/**
	 * 휴가 데이터 입력하기
	 * @param documentId
	 * @param dto
	 * @param empId
	 */
	public void preProcessVacationDocument(String documentId, ApprovalDocumentDTO dto, String empId);

    /**
	 * 휴가 사용 이력 추가
	 * @return
	 */
	public ServiceResult createVacationHistory();
	
	/**
	 * 휴가 사용 이력 수정
	 * @return
	 */
	public ServiceResult modifyVacationHistory();
	
	/**
	 * 휴가 사용 이력 삭제
	 * @return
	 */
	public ServiceResult deleteVacationHistory();
	
	public VacationHistoryVO readVacationHistory();
	
	/**
	 * 회원 한 명 혹은 전체 휴가 이력 조회(회원 한 명은 empId 담아서 넘기면 됨)
	 * @param paging
	 * @return
	 */
	public List<VacationHistoryVO>readVacationHistoryList(PaginationInfo paging);
	
	
	/**
	 * 전사원 휴가 이력 리스트 조회 
	 * @param paging
	 * @return
	 */
	public List<VacationHistoryVO> readAllVacationHistoryList(PaginationInfo<VacationHistoryVO> paging);

	/**
	 * 휴가 데이터 결재 처리(최종승인, 전결, 반려시 후처리 로직)
	 * @param docId
	 * @param statusCode
	 * @return
	 */
    public int processVacationDocument(String docId, String statusCode);
    
    
}
