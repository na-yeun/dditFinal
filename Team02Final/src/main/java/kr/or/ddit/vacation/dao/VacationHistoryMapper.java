package kr.or.ddit.vacation.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.vacation.vo.VacationHistoryVO;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VacationHistoryMapper{
	
	
	/**
	 * 페이징 처리를 위해 record 수를 카운트
	 * @param paging
	 * @return
	 */
	public int selectTotalRecord(PaginationInfo paging);
	
	
	
	/**
	 * 한 명의 휴가 기록 리스트 조회(검색가능)
	 * @param paging TODO
	 * @return 없어도 empty list 반환
	 */
	public List<VacationHistoryVO> selectVacationHistoryList(PaginationInfo paging);
	
	
	
	/**
	 * 모든 휴가 이력 리스트 조회 (민재)
	 * @param paging
	 * @return
	 */
	public List<VacationHistoryVO> selectAllVacationHistoryList(PaginationInfo<VacationHistoryVO> paging);
	
	public int selectTRecord(PaginationInfo<VacationHistoryVO> paging);

	public int insertVacationHistory(VacationHistoryVO vacationHistory);

	public int updateVacationStatus(@Param("docId") String docId);


	public int deleteVacationStatus(@Param("docId") String docId);

	public VacationHistoryVO getVacationHistoryInfo(@Param("docId") String docId);
}
