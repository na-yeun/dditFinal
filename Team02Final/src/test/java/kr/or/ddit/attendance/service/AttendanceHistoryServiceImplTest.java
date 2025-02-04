package kr.or.ddit.attendance.service;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import kr.or.ddit.annotation.RootContextWebConfig;
import kr.or.ddit.attendance.dao.AttendanceHistoryMapper;
import kr.or.ddit.attendance.vo.AttendTbVO;
import kr.or.ddit.attendance.vo.AttendanceHistoryVO;
import kr.or.ddit.commons.paging.PaginationInfo;

@RootContextWebConfig
class AttendanceHistoryServiceImplTest {
	
	@Inject
	private AttendanceHistoryMapper mapper;
	@Disabled
	@Test
	void testCreateAttendanceHistory() {
		fail("Not yet implemented");
	}
	@Disabled
	@Test
	void testModifyAttendanceHistory() {
		fail("Not yet implemented");
	}
	@Disabled
	@Test
	void testDeleteAttendanceHistory() {
		fail("Not yet implemented");
	}
	@Disabled
	@Test
	void testReadAttendanceHistoryList() {
		PaginationInfo paging = new PaginationInfo();
		
		Map<String, Object> variousCondition = new HashMap<>();
		variousCondition.put("empId", "EMP015");
		
		paging.setCurrentPage(1);
		paging.setVariousCondition(variousCondition);
		
		mapper.selectAttendanceHistoryList(paging);
	}
	@Disabled
	@Test
	void testReadAllAttendanceHistoryList() {
		PaginationInfo paging = new PaginationInfo();
		
		paging.setCurrentPage(1);
		
		mapper.selectAllAttendanceHistoryList(paging);
	}
	@Disabled
	@Test
	void testReadDownloadAttendanceExcel() {
		 // 1. 테스트 조건 설정
	    Map<String, Object> condition = new HashMap<>();
	    condition.put("department", "DEP005"); // 부서
	    condition.put("position", "1"); // 직급
	    condition.put("startDate", "2024-12-01"); // 시작일
	    condition.put("endDate", "2024-12-31"); // 종료일
	    condition.put("searchWord", null); // 검색어 (사원 이름)

	    // 2. DAO 호출 및 결과 검증
	    List<AttendanceHistoryVO> attendanceList = mapper.selectDownloadAttendaceExcel(condition);

	    // 결과 출력 (디버깅 목적)
	    System.out.println("조회된 근태 데이터: " + attendanceList.size());
	    attendanceList.forEach(System.out::println);

	    // 3. 엑셀 생성 및 다운로드 로직 테스트
	    try (Workbook workbook = new XSSFWorkbook()) {
	        Sheet sheet = workbook.createSheet("근태기록표");
	        Row headerRow = sheet.createRow(0);

	        String[] headers = {"날짜", "사원명", "직급", "부서", "출근시간", "퇴근시간", "초과근무여부", "초과근무시간(분)", "출근상태", "퇴근상태", "지각사유", "조퇴사유"};
	        for (int i = 0; i < headers.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(headers[i]);
	        }

	        int rowNum = 1;
	        for (AttendanceHistoryVO data : attendanceList) {
	            Row row = sheet.createRow(rowNum++);
	            row.createCell(0).setCellValue(data.getAtthisId());
	            row.createCell(1).setCellValue(data.getEmp() != null ? data.getEmp().getEmpName() : "");
	            row.createCell(2).setCellValue(data.getPosiName() != null ? data.getPosiName() : "");
	            row.createCell(3).setCellValue(data.getDepartName() != null ? data.getDepartName() : "");
	            row.createCell(4).setCellValue(data.getHahisTime() != null ? data.getHahisTime() : "");
	            row.createCell(5).setCellValue(data.getHleaveTime() != null ? data.getHleaveTime() : "");
	            row.createCell(6).setCellValue(data.getAtthisOverYn() != null ? data.getAtthisOverYn() : "");
	            row.createCell(7).setCellValue(data.getAtthisOver() != null ? data.getAtthisOver() : 0);
	            row.createCell(8).setCellValue(data.getAttstaIdIn() != null ? data.getAttstaIdIn() : "");
	            row.createCell(9).setCellValue(data.getAttstaIdOut() != null ? data.getAttstaIdOut() : "");
	            row.createCell(10).setCellValue(data.getAtthisCause() != null ? data.getAtthisCause() : "없음");
	            row.createCell(11).setCellValue(data.getEarlyLeaveCause() != null ? data.getEarlyLeaveCause() : "없음");
	        }

	        // 엑셀 파일로 저장 (테스트용)
	        try (FileOutputStream fos = new FileOutputStream("testAttendanceExcel.xlsx")) {
	            workbook.write(fos);
	            System.out.println("엑셀 파일 생성 완료: testAttendanceExcel.xlsx");
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	        fail("엑셀 생성 중 오류 발생");
	    }
	}
	@Disabled
	@Test
	void testReadEmpName() {
		String searchWord = "유민재";
		mapper.selectEmpName(searchWord);
		
		
	}
	@Disabled
	@Test	
	void testReadAttendTime() {
		mapper.selectAttendTime();
	}
	@Disabled
	@Test
	void testReadAllAttendTimeList() {
		mapper.selectAllAttendTimeList();
	}
	@Disabled
	@Test
	void testReadAllLeaveTimeList() {
		mapper.selectAllLeaveTimeList();
	}
	@Disabled
	@Test
	void testModifyAttendTime() {
		AttendTbVO att = new AttendTbVO();
		att.setAttendId("DEFAULT_01");
		att.setAttendTime("8");
		att.setLeaveTime("14");
		mapper.updateAttendTime(att);
	}
	@Disabled
	@Test
	void testAddMyHahisTime() {
		AttendanceHistoryVO ah = new AttendanceHistoryVO();
		String empId = "EMP011";
		String attendTime = "8";
		ah.setEmpId(empId);
		ah.setAttendTime(attendTime);
		
		mapper.insertMyHahisTime(ah);
	
		
	}
	@Disabled
	@Test
	void testReadMyHahisTime() {
		String empId = "EMP011";
		mapper.selectMyHahisTime(empId);
	}
	@Disabled
	@Test
	void testReadMyHleaveTime() {
		String empId = "EMP011";
		mapper.selectMyHleaveTime(empId);
	}
	@Disabled
	@Test
	void testAddMyHleaveTime() {
		/*
		 * HLEAVE_TIME = TO_TIMESTAMP_TZ(#{hleaveTime},
		 * 'YYYY-MM-DD"T"HH24:MI:SS.FF3TZH:TZM') , ATTHIS_OVER_YN = #{atthisOverYn} ,
		 * ATTHIS_OVER = #{atthisOver} , ATTSTA_ID_OUT = #{attstaIdOut} ,
		 * EARLYLEAVE_CAUSE = #{earlyLeaveCause} WHERE EMP_ID = #{empId} AND ATTHIS_ID =
		 * TO_CHAR(SYSDATE, 'YYYYMMDD') <!-- 당일 근태 데이터만 업데이트 -->
		 */
		AttendanceHistoryVO ah = new AttendanceHistoryVO();
		ah.setEmpId("EMP001"); // 사원 ID
	    ah.setHleaveTime("2024-12-23T18:00:00.000+09:00"); // 퇴근 시간 (ISO 8601 포맷)
	    ah.setAtthisOverYn("Y"); // 초과근무 여부
	    ah.setAtthisOver((long) 120); // 초과근무 시간 (분 단위)
	    ah.setAttstaIdOut("9"); // 퇴근 상태 코드
	    ah.setEarlyLeaveCause("개인 사유"); // 조퇴 사유
		mapper.insertMyHleaveTime(ah);
	}
	@Disabled
	@Test
	void testReadOneAttendDetail() {
		String empId = "EMP011";
		String atthisId = "20241223";
		
		mapper.selectOneAttendDetail(empId, atthisId);
	}
	@Disabled
	@Test
	void testReadAttendStatus() {
		mapper.selectAttendStatus();
	}
	
	@Test	
	void testReadLeaveStatus() {
		mapper.selectLeaveStatus();
	}
	@Disabled
	@Test
	void testModifyAttendLeaveStatus() {
		AttendanceHistoryVO ah = new AttendanceHistoryVO();
		String empId = "EMP011";
		String atthisId = "20241223";
		ah.setEmpId(empId);
		ah.setAtthisId(atthisId);
		ah.setAttstaIdIn("1");
		ah.setAttstaIdOut("8");
		
		mapper.updateAttendLeaveStatus(ah);
	}

}
