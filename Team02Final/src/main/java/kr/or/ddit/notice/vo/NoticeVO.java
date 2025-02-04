package kr.or.ddit.notice.vo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import kr.or.ddit.atch.vo.AtchFileDetailVO;
import kr.or.ddit.atch.vo.AtchFileVO;
import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.commons.validate.UpdateGroup;
import kr.or.ddit.employee.vo.EmployeeVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeVO {


    private String noticeNo;          // 공지사항 번호 (PK)

    @NotBlank(groups = {InsertGroup.class, UpdateGroup.class}, message = "제목은 필수입니다.")
    @Size(groups = {InsertGroup.class, UpdateGroup.class}, max = 200, message = "제목은 200자를 초과할 수 없습니다.")
    private String noticeName;

    @NotBlank(groups = {InsertGroup.class, UpdateGroup.class}, message = "내용은 필수입니다.")
    @Size(groups = {InsertGroup.class, UpdateGroup.class}, max = 4000, message = "내용은 4000자를 초과할 수 없습니다.")
    private String noticeContent;     // 공지사항 내용

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private LocalDate noticeDate;     // 작성일

    private String noticeImportant;   // 중요 공지 여부 (Y/N)
    private String empId;             // 작성자 사원 번호 (FK)

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

    private EmployeeVO employee;      // NoticeVO Has A EmployeeVO (1:1)


    private int rnum;    // 행번호를 위한 필드 추가
}


