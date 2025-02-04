package kr.or.ddit.approval.vo;

import lombok.Data;

import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class ApprovalVO {

    private String approvalId;      // 결재번호 1, 2, 3 형식임
    private String docId;           //문서번호 결재번호와 복합키이룸
    private LocalDate approvalDate; //결재일자
    private String approvalNum;     // 결재자차수 1차 2차 3차로 배정이 문서저장될때 같이 insert돼서 배정됨
    private String empId;           // 결재자 사원번호
    @Size(max = 2000, message = "의견은 2000자를 초과할 수 없습니다.")
    private String approvalComment; // 결재의견 (반려 시에만 필수)
    private String approvalStatus;  // 상태 1. 미열람(1차) 2. 열람(결재처리중) 3. 승인 4. 반려 5. 대기중(2차, 3차)
    private String approvalYn;      // 전결 사용여부 YN
    private String apprlineRid;     // 전결사용시의 실제 결재자여부(2차 전결 사용시 3차도 결재자는 2차결재자가됨)

    // 추가 필요한 필드들
    private String empName;         // 결재자 이름
    private String approvalStatusName;  // 결재 상태명
    private String approverFinalYn;    // 전결 권한 여부
}
