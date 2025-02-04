package kr.or.ddit.survey.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.survey.dao.SurveyResultMapper;
import kr.or.ddit.survey.vo.SurveyResultVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SurveyResultServiceImpl implements SurveyResultService {
	@Inject
	private SurveyResultMapper resultMapper;

	@Override
	public Map<String, Map<String, Object>> readSurveyResultList(String sboardNo) {
		// return할 결과
		Map<String, Map<String, Object>> resultMap = new HashMap<>();
		// 1) 데이터베이스에서 값 가지고 옴
		List<SurveyResultVO> resultList = resultMapper.selectSurveyResultList(sboardNo);
		Map<String, Object> countMap = new HashMap<>();
		
		Long totalCount = resultList.get(0).getTotalCount();
    	Long participatedCount = resultList.get(0).getParticipatedCount();
		
		countMap.put("totalCount",totalCount);
    	countMap.put("participatedCount",participatedCount);
    	
    	resultMap.put("countData", countMap);
    	
    	
    	if (resultList != null) {
		    for (SurveyResultVO result : resultList) {
		    	
		        String surquesNo = result.getSurquesNo(); // 질문 번호
		        String surquestType = result.getSurquesType(); // 질문 유형
		        String suritemNo = result.getSuritemNo(); // 항목 번호
		        String suritemContent = result.getSuritemContent(); // 항목 내용
		        Object totalResult = result.getTotalResult(); // 결과 값
		        
		        
		        // surquesNo에 해당하는 Map 가져오기
		        Map<String, Object> quesResultMap = (Map<String, Object>) resultMap.get(surquesNo);
		        
		        
		        if(surquestType.equals("S_MULTI")) {
		        	if (quesResultMap == null) {
			            // 처음 등록하는 질문일 경우 Map 초기화
			            quesResultMap = new HashMap<>();
			            resultMap.put(surquesNo, quesResultMap);
			        }		        	
		        	
			        // suritemNo에 해당하는 데이터 객체 생성
			        Map<String, Object> itemData = new HashMap<>();
			        itemData.put("suritemContent", suritemContent); // 항목 내용 저장
			        itemData.put("totalResult", totalResult); // 결과 값 저장
	
			        // 해당 suritemNo에 데이터 저장
			        quesResultMap.put(suritemNo, itemData);
				} else {
					if (quesResultMap == null) {
						// 처음 등록하는 질문일 경우 Map 초기화
						quesResultMap = new HashMap<>();
						resultMap.put(surquesNo, quesResultMap);
					}

					// suritemNo에 해당하는 데이터 객체 가져오기
					Map<String, Object> itemData = (Map<String, Object>) quesResultMap.get(suritemNo);
					if (itemData == null) {
						// 처음 등록하는 항목일 경우 Map 초기화
						itemData = new HashMap<>();
						itemData.put("suritemContent", suritemContent); // 항목 내용 저장
						quesResultMap.put(suritemNo, itemData);
					}

					// totalResult를 List에 누적하여 저장
					List<Object> totalResultList = (List<Object>) itemData.get("totalResult");
					if (totalResultList == null) {
						totalResultList = new ArrayList<>(); // 처음 초기화
						itemData.put("totalResult", totalResultList);
					}
					totalResultList.add(totalResult); // 새로운 답변 추가
				}
		    }
		}
    	
		return resultMap;
	}

	@Override
	public SurveyResultVO readSurveyResult(SurveyResultVO result) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean readSurveyExist(String empId, String sboardNo) {
		int rowcnt = resultMapper.selectSurveyExist(empId, sboardNo);
		if(rowcnt==0) {
			return true;
		} else{
			return false;
		}
	}
	
	@Transactional
	@Override
	public ServiceResult createSurveyResult(List<SurveyResultVO> resultList) {
		try {
			for(SurveyResultVO result : resultList) {
				resultMapper.insertSurveyResult(result);
			}
			return ServiceResult.OK;
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
