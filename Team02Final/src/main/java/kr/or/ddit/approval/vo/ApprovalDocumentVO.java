package kr.or.ddit.approval.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.or.ddit.atch.vo.AtchFileDetailVO;
import kr.or.ddit.atch.vo.AtchFileVO;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@ToString
public class ApprovalDocumentVO {


    private String docId;
    private String docTitle;
    @ToString.Exclude
    private String docContent;
    private String docPreserve;
    private String docStatus;
    private String allowDelegation;
    private String empId;
    private String apprlineId;
    private String apprformId;
    // 문서 생성일자 추가
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate; // 생성일자


    // 첨부파일 관련 추가
    @Nullable
    private Integer atchFileId;

    @JsonIgnore
    @ToString.Exclude
    @Nullable
    @Valid
    private AtchFileVO atchFile;

    @JsonIgnore
    @ToString.Exclude
    private MultipartFile[] uploadFiles;

    public void setUploadFiles(MultipartFile[] uploadFiles) {
        List<AtchFileDetailVO> fileDetails = Optional.ofNullable(uploadFiles)
                .map(Arrays::stream)
                .orElse(Stream.empty())
                .filter(f->!f.isEmpty())
                .map(AtchFileDetailVO::new)
                .collect(Collectors.toList());
        if(!fileDetails.isEmpty()) {
            this.uploadFiles = uploadFiles;
            atchFile = new AtchFileVO();
            atchFile.setFileDetails(fileDetails);
        }
    }

    // 기존 테이블에는 없는것---------------------
    private int rnum;

    private String empName;
    private String docStatusName;

    @JsonIgnore
    @ToString.Exclude
    private List<ApprovalVO> approvalList;  // 결재선(결재자) 정보 리스트

    private String approverFinalYn;    // 전결 권한 여부 approver테이블에서 갖고오는거임

    private List<ReferenceEmployeeVO> referenceList;  // Map 대신 VO 리스트로 변경
}
