package kr.or.ddit.survey.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.survey.dao.SurveyMapper;
import kr.or.ddit.survey.dao.SurveyResultMapper;
import kr.or.ddit.survey.vo.SurveyBoardVO;
import kr.or.ddit.survey.vo.SurveyItemVO;
import kr.or.ddit.survey.vo.SurveyQuestionVO;
import kr.or.ddit.survey.vo.SurveyResultVO;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class SurveyServiceImpl implements SurveyService {
	@Inject
	private SurveyMapper mapper;
	@Inject
	private SurveyResultMapper resultMapper;
	
	@Override
	public int readTotalRecord(PaginationInfo paging) {
		return mapper.selectTotalRecord(paging);
	}
	
	
	@Override
	public List<SurveyBoardVO> readSurveyBoardList(PaginationInfo paging) {
		int totalRecord = mapper.selectTotalRecord(paging);
		paging.setTotalRecord(totalRecord);
		List<SurveyBoardVO> list = mapper.selectSurveyBoardList(paging);
		
		
		LocalDate now = LocalDate.now();
		for(SurveyBoardVO board : list) {
			LocalDate endDate = board.getSurboardEnddate();
			Period endPeriod = Period.between(now, endDate);
			String surboardYn = board.getSurboardYn();
			int days = endPeriod.getDays();
			if(days>=0 && days<=3) {
				board.setEndDateWarning("WARNING");
			}
			
			LocalDate startDate = board.getSurboardStdate();
			if(startDate.isAfter(now)) {
				// 시작날짜가 현재날짜보다 이후이면
				board.setStartDateCheck("BEFORESTART");
			} else if (startDate.isEqual(now)&&surboardYn.equals("N")) {
				// 시작날짜가 오늘날짜랑 같고, board의 상태가 N이면 Y로 업데이트 해야함
				board.setSurboardYn("Y");
				mapper.updateSurveyBoard(board);
			}
		}
		return list;
	}

	@Override
	public SurveyBoardVO readSurveyBoardDetail(String sboardNo) {
		// TODO Auto-generated method stub
		SurveyBoardVO surveyBoard = mapper.selectSurveyBoardDetail(sboardNo);
		
		if(surveyBoard==null) {
			return null;
		} else {
			String yn = surveyBoard.getSurboardYn();
			// 지금 날짜
			LocalDate now = LocalDate.now();
			// 시작날짜
			LocalDate startDate = surveyBoard.getSurboardStdate();
			// 종료날짜
			LocalDate endDate = surveyBoard.getSurboardEnddate();
			// 지금날짜와 종료날짜 사이의 차이
			Period endPeriod = Period.between(now, endDate);
			int days = endPeriod.getDays();
			
			// 현재 진행중일 때 종료날짜 체크
			if(yn.equals("Y")) {
				if(days>=0 && days<=3) {
					surveyBoard.setEndDateWarning("WARNING");
				}
				surveyBoard.setDetailUrl("survey/surveyDetail");
			// 진행중이 아닐 때 yn.equals("N") 일 때
			} else  {
				if(days<0) {
					// 날짜가 이미 지났음 ==> 이미 종료
					// result 테이블에서 죄다 count 해와야함
					surveyBoard.setDetailUrl("survey/surveyEndDetail");
				} else if (startDate.isAfter(now)) {
					// 아직 시작을 안 했음
					surveyBoard.setStartDateCheck("BEFORESTART");
					surveyBoard.setDetailUrl("survey/surveyDetail");
				} else {
					surveyBoard.setDetailUrl("survey/surveyEndDetail");
				}
			}
			return surveyBoard;
		}
	}
	
	@Override
	public boolean checkUpdate(SurveyBoardVO boardDetail) {
		boolean isPossibleUpdate = false;
		
		String yn = boardDetail.getSurboardYn(); // Y면 진행중, 고칠 수 없음 N 이어야함
		String startDateCheck = boardDetail.getStartDateCheck(); // BEFORESTART 이면 아직 시작전 고칠 수 있음
		
		// N이고 BEFORESTART(null이 아님) 이면 진행중이 아니고 시작 전, 수정 가능하다는 뜻
		// Y이고 BEFORESTART(null)이면 진행중이고 시작전이 아니다 시작했다는 뜻...
		if(yn.equals("N")&&startDateCheck!=null) {
			// 수정 가능한 상태
			isPossibleUpdate = true;
		}
		
		return isPossibleUpdate;
	}
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public ServiceResult createSurvey(SurveyBoardVO board) {
		try {
			mapper.insertSurveyBoard(board);
			String sboardNo = board.getSboardNo();
			List<SurveyQuestionVO> questions = board.getSurveyQuestionList();
			for(SurveyQuestionVO question:questions) {
				question.setSboardNo(sboardNo);
				mapper.insertSurveyQuestion(question);
				String surquesNo = question.getSurquesNo();
				
				List<SurveyItemVO> items = question.getSurveyItemList();
				for(SurveyItemVO item:items) {
					item.setSboardNo(sboardNo);
					item.setSurquesNo(surquesNo);
					mapper.insertSurveyItem(item);
				}
			}
			return ServiceResult.OK;
		} catch (Exception e) {
			return ServiceResult.FAIL;
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public ServiceResult modifySurveyBoard(SurveyBoardVO updateBoard) {
		try {
				
			// delete를 하기 위해서는 기존의 데이터와 새로 들어온 데이터를 비교해야함
			// 기존 데이터에는 있으나 새로 들어온 데이터에는 없으면 delete
			// 기존 데이터 먼저 주루룩 뽑아오고....
			String sboardNo = updateBoard.getSboardNo();
			// 기존의 설문조사 게시글
			SurveyBoardVO originBoard = mapper.selectSurveyBoardDetail(sboardNo);
			// 기존의 질문 List
			List<SurveyQuestionVO> originQuestionList = originBoard.getSurveyQuestionList();
			for(SurveyQuestionVO originQuestion : originQuestionList) {
				// 기존의 아이템 List
				List<SurveyItemVO> originSurveyItemList = originQuestion.getSurveyItemList();
				
				// 기존 질문에는 있지만 새로 받아온 질문에는 없으면 delete를 해야함
				// 새로 받아온 데이터에서 질문 list 꺼내기
				List<SurveyQuestionVO> updateQuestions = updateBoard.getSurveyQuestionList();
				// 기존 질문의 질문 번호(PK)
				String originQuesNo = originQuestion.getSurquesNo();
				
				
				// 새로 들어온 questions들의 surquesno를 비교해봤을 때 똑같은 게 있는지 없는지..
				boolean existsQuesUpdate = Optional.ofNullable(updateQuestions) // null 체크
							                       .orElse(Collections.emptyList()) // null일 경우 빈 리스트 처리
							                       .stream()
							                       .anyMatch(updateQuestion -> 
							                            updateQuestion != null
							                            && originQuesNo.equals(updateQuestion.getSurquesNo())); // surquesNo 비교
				
				// 질문이 똑같은 게 없을 경우 (해당 질문과 item 삭제)
				if (!existsQuesUpdate) {
					// WHERE SURQUES_NO = ${originQuesNo} 삭제해야함
					// 질문이 삭제됐으면 item도 삭제되어야함
					mapper.deleteSurveyQuestion(originQuesNo);
					mapper.deleteSurveyItemsWithQuestion(originQuesNo);
				}
				
				for(SurveyQuestionVO updateQuestion : updateQuestions) {
					String updateQuestionNo = updateQuestion.getSurquesNo();
					if(updateQuestionNo!=null) {
						if(updateQuestionNo.equals(originQuesNo)) {
							List<SurveyItemVO> updateItemList = updateQuestion.getSurveyItemList();
							for(SurveyItemVO originItem : originSurveyItemList) {
								String originItemNo = originItem.getSuritemNo();
								boolean existsItemUpdate = Optional.ofNullable(updateItemList) // null 체크
					                       .orElse(Collections.emptyList()) // null일 경우 빈 리스트 처리
					                       .stream()
					                       .anyMatch(updateItem -> 
					                       			updateItem != null
					                            && originItemNo.equals(updateItem.getSuritemNo())); // surquesNo 비교
								log.info("여기 확인할게요~~~ {} ==> {}",originItemNo,existsItemUpdate);
								// 질문이 똑같은 게 없을 경우 (해당 질문과 item 삭제)
								if (!existsItemUpdate) {
									// WHERE SURQUES_NO = ${originQuesNo} 삭제해야함
									// 질문이 삭제됐으면 item도 삭제되어야함
									mapper.deleteSurveyItem(originItemNo);
								}
							}
							
						}
					}
					
				}
				
			}
			
			
			
			
			// 기존 update와 새로운 insert 실행
			// board 업데이트
			mapper.updateSurveyBoard(updateBoard);
			
			List<SurveyQuestionVO> updateQuestions = updateBoard.getSurveyQuestionList();
			
			// question 업데이트
			for(SurveyQuestionVO question : updateQuestions) {
				// 업데이트이기 때문에 게시판 번호는 고정임
				question.setSboardNo(sboardNo);
				
				String surquesNo = question.getSurquesNo();
				// surquesNo가 비어있으면 새로 추가된 질문(새로 추가됐으면 항목도 추가돼야함)
				
				List<SurveyItemVO> updateItems = question.getSurveyItemList();
				if(surquesNo==null) {
					mapper.insertSurveyQuestion(question);
					// 새로 insert한 surquesNo를 받아와서 그에 해당하는 항목들을 insert 해야함
					surquesNo = question.getSurquesNo();
					
					for(SurveyItemVO item : updateItems) {
						item.setSboardNo(sboardNo);
						item.setSurquesNo(surquesNo);
						mapper.insertSurveyItem(item);
					}
					
				// surquesNo가 비어있지 않으면 기존에 있던 질문 수정
				} else {
					mapper.updateSurveyQuestion(question);
					
					// item 업데이트
					for(SurveyItemVO item:updateItems) {
						String suritemNo = item.getSuritemNo();
						item.setSboardNo(sboardNo);
						item.setSurquesNo(surquesNo);
						
						// suritemNo가 null이면 새로 추가된 항목
						if(suritemNo==null) {
							
							mapper.insertSurveyItem(item);
						// suritemNo가 null이 아니면 기존에 있던 항목 수정
						} else {
							mapper.updateSurveyItem(item);
						}
						
					}
				}
			}
			
			
			return ServiceResult.OK; // 성공 시 반환
	    } catch (DataIntegrityViolationException e) {
	        // 데이터 무결성 위반 (PK, FK, 유니크 제약 조건 등)
	        return ServiceResult.PKDUPLICATED;
	    } catch (DataAccessException e) {
	        // MyBatis에서 발생한 일반적인 데이터 접근 예외
	        e.printStackTrace(); // 로그 기록
	        return ServiceResult.SERVERERROR;
	    } catch (Exception e) {
	        // 기타 예외 처리
	        e.printStackTrace(); // 로그 기록
	        return ServiceResult.SERVERERROR;
	    }
	}
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public ServiceResult deleteSurveyBoard(String sboardNo) {
		
		try {
			mapper.deleteSurveyItemAll(sboardNo);
			mapper.deleteSurveyQuestionAll(sboardNo);
			mapper.deleteSurveyBoard(sboardNo);
			resultMapper.deleteSurveyResult(sboardNo);
	        
			return ServiceResult.OK; // 성공 시 반환
	    } catch (DataIntegrityViolationException e) {
	        // 데이터 무결성 위반 (PK, FK, 유니크 제약 조건 등)
	        return ServiceResult.PKDUPLICATED;
	    } catch (DataAccessException e) {
	        // MyBatis에서 발생한 일반적인 데이터 접근 예외
	        e.printStackTrace(); // 로그 기록
	        return ServiceResult.SERVERERROR;
	    } catch (Exception e) {
	        // 기타 예외 처리
	        e.printStackTrace(); // 로그 기록
	        return ServiceResult.SERVERERROR;
	    }
	}

	

}
