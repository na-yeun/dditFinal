package kr.or.ddit.approval.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
public class ApprovalLineVO {
    private String apprlineId;

    @NotBlank(message = "결재선 제목이 누락되었습니다.")
    @Size(max = 30, message = "결재선 제목은 30글자를 초과할 수 없습니다.")
    private String apprlineTitle;

    private String apprlineNum;

    private LocalDate apprlineDate;

    private String empId;

    // 오라클 DB에는 등록안됐지만 비동기로 여기에 바로 다 담아가는게 효율적이라 추가함
    private List<ApproverVO> approvers;
}
