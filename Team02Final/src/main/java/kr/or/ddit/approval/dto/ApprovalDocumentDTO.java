package kr.or.ddit.approval.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

@Data
public class ApprovalDocumentDTO {

    @NotBlank(message = "Document HTML은 비어 있을 수 없습니다.")
    private String documentHtml;         // 문서 HTML

    @NotBlank(message = "전결 허용 여부는 필수입니다.")
    private String allowDelegation;      // 전결 허용 여부

    @NotNull(message = "결재자 정보는 필수입니다.")
    @Size(min = 1, message = "최소 한 명 이상의 결재자가 필요합니다.")
    private List<Map<String, Object>> approvers;  // 결재자 정보

    private List<Map<String, Object>> references; // 참조자 정보

    @NotBlank(message = "Form ID는 필수입니다.")
    private String formId;               // 결재 문서 유형 코드

    @NotNull(message = "FormData는 필수입니다.")
    private Map<String, Object> formData; // 문서 유형별 추가 데이터

    @NotBlank(message = "결재선 ID는 필수입니다.")
    private String apprlineId;           // 결재선 ID

//    MultipartFile[] attachments; //첨부파일
}
