package kr.or.ddit.approval.vo;

import lombok.Data;

@Data
public class ApproverVO {

    private String approverNum;
    private String apprlineId;
    private String empId;
    //전결권 지정 여부
    private String approverFinalYn;

    //비동기를통해서 프론트단에서 백단에서넘어온걸 다시담기
    //DB에는 이 컬럼이없음
    private String departName;
    private String posiName;
    private String empName;
}
