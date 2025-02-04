package kr.or.ddit.approval.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class ReferenceEmployeeVO {

    @NotBlank
    private String readStatusYn;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate readDate;
    @NotBlank
    private String empId;
    @NotBlank
    private String refId;

    // 추가 필드
    private String empName;        // 참조자 이름
    private String statusName;     // 읽음/안읽음 상태명
}
