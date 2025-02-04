package kr.or.ddit.approval.service;

import kr.or.ddit.approval.vo.ApprovalLineVO;
import kr.or.ddit.approval.vo.ApproverVO;

import java.util.List;
import java.util.Map;

public interface ApprovalLineService {

    /**
     * 결재선 저장하고 결재자까지 저장하고 올거임
     * @param approvalLine
     * @param empId
     */
    public Map<String, Object> saveApprovalLine(ApprovalLineVO approvalLine, String empId);

    public List<Map<String,Object>> getSavedLines(String empId);

    /**
     * 결재선 삭제 비동기 로직
     * @param lineId
     */
    public void removeApprovalLine(String lineId);

    /**
     * 결재선 ID으로 결재자들 정보 갖고오는거
     * @param lineId
     */
    public List<Map<String, Object>> getSavedApproversByApprlineId(String lineId);
}
