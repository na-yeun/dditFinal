package kr.or.ddit.approval.dao;

import kr.or.ddit.approval.vo.ApprovalFormVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprovalFormMapper {

    public List<ApprovalFormVO> selectFormTitles();

    public String selectFormContent(String apprformId);

}
