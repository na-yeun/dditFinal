package kr.or.ddit.approval.service;

import kr.or.ddit.approval.dao.ApprovalLineMapper;
import kr.or.ddit.approval.dao.ApproverMapper;
import kr.or.ddit.approval.vo.ApprovalLineVO;
import kr.or.ddit.approval.vo.ApproverVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class ApprovalLineServiceImpl implements ApprovalLineService {

    @Autowired
    ApprovalLineMapper approvalLineMapper;

    @Autowired
    ApproverMapper approverMapper;

    //결재선 저장하기 로직
    @Override
    public Map<String, Object> saveApprovalLine(ApprovalLineVO approvalLine, String empId) {

        // 제목 유효성 검사 추가
        if (approvalLine.getApprlineTitle() == null || approvalLine.getApprlineTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("결재선 제목이 누락되었습니다.");
        }
        if (approvalLine.getApprlineTitle().length() > 30) {
            throw new IllegalArgumentException("결재선 제목은 30글자를 초과할 수 없습니다.");
        }

        // 0. 결재라인ID 만들기 바로 메소드안에 넣으려했는데 밑에 결재자정보 입력하는데도 써야해서 빼냄
        String apprLineId = generateApprLineId();

        // 1. 결재선 정보 생성하기

        //ID
        approvalLine.setApprlineId(apprLineId);
        //제목
        approvalLine.setApprlineTitle(approvalLine.getApprlineTitle());
        //총 차수
        approvalLine.setApprlineNum(String.valueOf(approvalLine.getApprovers().size()));
        //만든 생성일자
        approvalLine.setApprlineDate(LocalDate.now());
        //만든 결재자
        approvalLine.setEmpId(empId);

        // 2. 결재선 저장
        int result = approvalLineMapper.insertApprovalLine(approvalLine);
        if (result != 1) {
            throw new RuntimeException("결재선 저장에 실패했습니다.");
        }

        // 3. 결재자 정보 저장하기
        List<ApproverVO> approvers = approvalLine.getApprovers();
        for (int i = 0; i < approvers.size(); i++) {

            //꺼내기
            ApproverVO approver = approvers.get(i);
            //검증
            if (approver.getEmpId() == null) {
                throw new IllegalArgumentException("결재자 정보가 유효하지 않습니다");
            }
            //집어넣기
            //몇차의 결재자인지
            approver.setApproverNum(String.valueOf(i + 1));
            //어느 결재선그룹의 결재자인지
            approver.setApprlineId(apprLineId);
            //결재자 사원ID와 전결권자 여부는 갖고있음

            log.info("approver get EMP ID============ {}",approver.getEmpId());
            log.info("approver get FINAL YN=================={}", approver.getApproverFinalYn());

            int res = approverMapper.insertApprover(approver);
            if (res != 1) {
                throw new RuntimeException("결재자 저장에 실패했습니다.");
            }
        }

        // 저장 후 바로 DB에서 읽어와 alias 형태로 변환 없이 return 가능
        List<Map<String, Object>> savedApprovers =
                approvalLineMapper.selectApproverDetailsByLineId(apprLineId);

        // 결과 반환
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("apprlineId", apprLineId);
        resultMap.put("apprlineTitle", approvalLine.getApprlineTitle());
        resultMap.put("apprlineDate", LocalDate.now());
        resultMap.put("approvers", savedApprovers);

        return resultMap;
    }

    //여기서 한번 사원 id로 결재선들을 갖고오고 결재선id로 결재자정보들을 다갖고와야할거같음
    @Override
    public List<Map<String,Object>> getSavedLines(String empId) {
        List<Map<String, Object>> result = new ArrayList<>();

        // 1. 사원의 결재선 목록 조회
        List<ApprovalLineVO> lines = approvalLineMapper.selectApprLinesByEmpId(empId);

        // 2. 각 결재선의 결재자 정보 조회 및 조합하기
        for (ApprovalLineVO line : lines) {

            List<Map<String, Object>> approvers
                    = approvalLineMapper.selectApproverDetailsByLineId(line.getApprlineId());

            Map<String, Object> lineInfo = new HashMap<>();
            lineInfo.put("apprlineId", line.getApprlineId());
            lineInfo.put("apprlineTitle", line.getApprlineTitle());
            lineInfo.put("apprlineDate", line.getApprlineDate());
            lineInfo.put("approvers", approvers);

            result.add(lineInfo);

        }
        return result;
    }

    @Override
    public void removeApprovalLine(String lineId) {
        // 결재자 먼저 삭제 (FK관계라서 굳이 cascade안쓰고 쿼리두번쓰기로)
        approvalLineMapper.deleteApproversByLineId(lineId);

        // 결재선 삭제
        approvalLineMapper.deleteApprovalLine(lineId);
    }

    @Override
    public List<Map<String, Object>> getSavedApproversByApprlineId(String lineId) {

        return approvalLineMapper.selectApproverDetailsByLineId(lineId);

    }


    //결재선 만들기 메소드~~
    private String generateApprLineId() {
        // Mapper에서 오늘 마지막으로 생성된 일련번호를 조회  -> 결과는 0000, 0001..등등
        // 일련번호를 숫자로 변환후 +1 한다음 일련번호를 4자리로 포맷한다
        String formattedSeq = String.format("%04d",
                        Integer.parseInt(approvalLineMapper.selectApprLineLastSeq()) + 1);
        // 현재 날짜를 YYMMDD형식으로 가져오기
        String today = new SimpleDateFormat("yyMMdd").format(new Date());

        // 합치기
        return "AL" + today + formattedSeq;
    }
}
