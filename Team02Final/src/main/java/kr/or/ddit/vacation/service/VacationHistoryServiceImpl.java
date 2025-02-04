package kr.or.ddit.vacation.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.ddit.approval.dto.ApprovalDocumentDTO;
import kr.or.ddit.approval.service.ApprovalDocumentService;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.vacation.dao.VacationHistoryMapper;
import kr.or.ddit.vacation.dao.VacationStatusMapper;
import kr.or.ddit.vacation.vo.VacationDTO;
import kr.or.ddit.vacation.vo.VacationHistoryVO;
import kr.or.ddit.vacation.vo.VacationStatusVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VacationHistoryServiceImpl implements VacationHistoryService {
	@Inject
	private VacationHistoryMapper vacationHistoryMapper;
    @Autowired
    private VacationStatusMapper vacationStatusMapper;

	@Autowired
	private ApprovalDocumentService approvalDocumentService;


	@Override
	public ServiceResult createVacationHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VacationHistoryVO readVacationHistory() {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * 휴가 신청서 후처리 메소드
	 * VACATION_HISTORY 테이블에 대기상태로 데이터 삽입
	 */
	public void preProcessVacationDocument(String documentId, ApprovalDocumentDTO dto, String empId) {
		try{
			// 데이터 구조 확인을 위한 로그
//            log.info("전체 DTO 구조: {}", dto);

			Map<String, Object> formData = dto.getFormData();
//            log.info("formData 구조: {}", formData);

			Map<String, Object> vacationData = (Map<String, Object>) formData.get("data");
//            log.info("vacationData 구조: {}", vacationData);

			//2. VacationHistory 테이블에 들어갈 데이터 세팅하기
			VacationHistoryVO vacationHistory = new VacationHistoryVO();
			//2-0-1. 휴가 코드 미리설정해서 저기서 꺼내쓸때 널포인트 안나게 막기
			vacationHistory.setVacCode((String) vacationData.get("vacCode"));

			//2-0-2. prefix생성 pk vacNo는 휴가코드-yyMM-0001이런식으로만들거임
			String prefix = String.format("%s-%s"
					, vacationHistory.getVacCode().toUpperCase()
					, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMM")));
			//2-0-3. vacNo세팅
			vacationHistory.setVacNo(prefix);
			//2-1. 사원번호는 empId
			vacationHistory.setEmpId(empId);

			//2-2. 시작일 종료일 설정
			//2-2-1. 날짜 변환
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			//2-2-2. LocalDate로 변환 후, 출력 포맷에 맞춰 변환
			String formattedStartDate = LocalDate.parse((String) vacationData.get("startDate"), inputFormatter).format(outputFormatter);
			String formattedEndDate = LocalDate.parse((String) vacationData.get("endDate"), inputFormatter).format(outputFormatter);
			//2-2-3. 세팅
			vacationHistory.setVacStartdate(formattedStartDate);
			vacationHistory.setVacEnddate(formattedEndDate);

			//2-4. 전자결재 문서번호 설정
			vacationHistory.setVacDocId(documentId);
			//2-5. 상태코드 설정(대기 :N)
			vacationHistory.setVacStatus("N");

			// 3. vacationHistory 테이블에 데이터 넣기
			int result = vacationHistoryMapper.insertVacationHistory(vacationHistory);

			if (result > 0) {
				log.info("휴가 신청 전처리 완료 : {}", documentId);
			}else {
				throw new RuntimeException("휴가 신청 전처리 실패");
			}
		} catch (Exception e) {
			log.error("휴가 신청 전처리 중 오류 발생: ", e);
			throw new RuntimeException("휴가 신청 전처리 실패", e);
		}
	}

	@Override
	public List<VacationHistoryVO> readVacationHistoryList(PaginationInfo paging) {
		int totalRecord = vacationHistoryMapper.selectTotalRecord(paging);
		paging.setTotalRecord(totalRecord);
		return vacationHistoryMapper.selectVacationHistoryList(paging);
	}

	@Override
	public ServiceResult modifyVacationHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceResult deleteVacationHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VacationHistoryVO> readAllVacationHistoryList(PaginationInfo<VacationHistoryVO> paging) {
		int totalRecord = vacationHistoryMapper.selectTRecord(paging);
		paging.setTotalRecord(totalRecord);
		return vacationHistoryMapper.selectAllVacationHistoryList(paging);
	}

	@Override
	public int processVacationDocument(String docId, String statusCode) {
		int result = 0;

		try {
			//최종승인, 전결상태
			if (statusCode.equals("3")) {
				result = vacationHistoryMapper.updateVacationStatus(docId);
				if (result > 0) {
					// 휴가 정보 조회
					VacationHistoryVO vacInfo = vacationHistoryMapper.getVacationHistoryInfo(docId);
					if (vacInfo == null) {
						log.error("휴가 정보를 찾을 수 없습니다. docId: {}, empId: {}", docId);
						return -1;
					}

					String empId = vacInfo.getEmpId();

					// 처리대상 휴가 코드 확인
					String vacCode = vacInfo.getVacCode();
					if (!isProcessableVacationType(vacCode)) {
						log.info("처리 대상이 아닌 휴가 유형입니다: {}", vacCode);
						return result;
					}

					// 휴가 현황 조회
					VacationStatusVO vacStatus = vacationStatusMapper.selectVacationStatusThisYear(empId);
					if (vacStatus == null) {
						log.error("휴가 현황 정보를 찾을 수 없습니다. empId: {}", empId);
						return -1;
					}
					vacStatus.setEmpId(empId);

					// 날짜 처리
					try {
						// DateTimeFormatter를 사용하여 날짜 형식 지정
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

						LocalDate startDate = LocalDate.parse(vacInfo.getVacStartdate(), formatter);
						LocalDate endDate = LocalDate.parse(vacInfo.getVacEnddate(), formatter);
						int workingDays = calculateWorkingDays(startDate, endDate);

						// 휴가 타입별 처리 및 업데이트
						processVacationByType(vacCode, vacStatus, workingDays);

						// 상태 업데이트
						result = vacationStatusMapper.updateVacationStatusCounts(vacStatus);
						if (result <= 0) {
							log.error("휴가 현황 업데이트 실패");
							return -1;
						}
					} catch (DateTimeParseException e) {
						log.error("날짜 형식 오류: {}", e.getMessage());
						return -1;
					}
				}
			} else if (statusCode.equals("4")) {
				result = vacationHistoryMapper.deleteVacationStatus(docId);
			} else {
				throw new IllegalArgumentException("잘못된 상태 코드입니다: " + statusCode);
			}
			return result;

		} catch (Exception e) {
			log.error("휴가 처리 중 오류 발생", e);
			return -1;
		}
	}

	// 휴가 타입별 처리를 별도 메서드로 분리
	private void processVacationByType(String vacCode, VacationStatusVO status, int days) {
		switch(vacCode) {
			case "V001": // 연차
				updateAnnualLeave(status, days);
				break;
			case "V002": // 반차
				updateHalfDayLeave(status, days);
				break;
			case "V003": // 병가
				updateSickLeave(status, days);
				break;
		}
	}

	// 연차 처리 (하루 = 1.0)
	private void updateAnnualLeave(VacationStatusVO status, int days) {
		double newUse = status.getVstaUse() + days;  // 1.0 * days
		status.setVstaUse(newUse);
		status.setVstaNowcount(status.getVstaNowcount() - days);
	}

	// 반차 처리 (하루 = 0.5)
	private void updateHalfDayLeave(VacationStatusVO status, int days) {
		double newUse = status.getVstaUse() + (days * 0.5);
		status.setVstaUse(newUse);
		status.setVstaNowcount(status.getVstaNowcount() - (days * 0.5));
	}

	// 병가 처리 (단순 더하기)
	private void updateSickLeave(VacationStatusVO status, int days) {
		status.setVstaSickcount(status.getVstaSickcount() + days);
	}


	//휴가 일수 계산 메소드(주말 제외하기가 목적임)
	private int calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
		int days = 0;
		LocalDate currentDate = startDate;

		while (!currentDate.isAfter(endDate)) {
			if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY
					&& currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
				days++;
			}
			currentDate = currentDate.plusDays(1);
		}
		return days;
	}

	// 처리 대상 휴가 타입인지 확인
	private boolean isProcessableVacationType(String vacCode) {
		return Stream.of("V001", "V002", "V003")
				.anyMatch(vacCode::equals);
	}
	
}
