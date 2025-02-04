package kr.or.ddit.question.vo;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.or.ddit.atch.vo.AtchFileDetailVO;
import kr.or.ddit.atch.vo.AtchFileVO;
import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.commons.validate.UpdateGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(of = "quNo")
public class QuestionVO {
	private int rnum;					// 글 순번
	
	private String quNo;				// 문의 게시글 번호
	
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class})
	@Size(max = 100)
	private String quTitle;				// 문의 게시글 제목
	
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class})
	@Size(max = 4000)
	private String quContent;			// 문의 게시글 내용
	
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private LocalDate quDate;			// 문의 게시글 작성 날짜
	
	private String quYn;				// 문의 게시글 답변 여부
	
	@Size(max = 4000)
	private String answContent;			// 문의 게시글 답변 내용
	
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private LocalDate answDate;			// 문의 게시글 답변 작성 날짜
	
	@NotBlank
	@Size(max = 100)
	private String questionId;			// 문의 게시글 작성자 ID
	
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class})
	@Size(max = 20)
	private String goryId;		// 문의 게시글 카테고리 번호
	
	private String answerId;			// 문의 게시글 답변 작성자 ID(운영자)
	
	private String quSecretyn;			// 문의 게시글 공개 비공개 여부
	
	private String empName;
	
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
	
	
}
