package kr.or.ddit.approval.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class ApprovalReferenceVO {


    @NotBlank
    private String refId; // 참조그룹번호 PK
    @NotBlank
    private String docId; // 문서ID FK

}
