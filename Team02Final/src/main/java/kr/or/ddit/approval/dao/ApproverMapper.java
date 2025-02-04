package kr.or.ddit.approval.dao;

import kr.or.ddit.approval.vo.ApproverVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApproverMapper {
    /**
     * 결재선 그룹에 포함된 결재자 정보 넣는 매퍼
     * @param approver
     */
    public int insertApprover(ApproverVO approver);
}
