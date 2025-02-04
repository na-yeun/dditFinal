package kr.or.ddit.approval.dao;

import kr.or.ddit.approval.vo.ApprovalReferenceVO;
import kr.or.ddit.approval.vo.ReferenceEmployeeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprovalReferenceMapper {
    public int getNextRefSeq(String yearMonth);

    public int insertDocumentReference(ApprovalReferenceVO reference);

    public int insertReferenceEmployees(List<ReferenceEmployeeVO> refEmployees);
}
