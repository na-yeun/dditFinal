package kr.or.ddit.atch.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

/**
 * 파일 한건의 메타데이터를 가진 모델 객체로 MultipartFile 의 adapter 로 활용함.
 */
@Data
@EqualsAndHashCode(of = { "atchFileId", "fileSn" })
@NoArgsConstructor
public class AtchFileDetailVO implements Serializable {
	@JsonIgnore
	@ToString.Exclude
	@Nullable
	private transient MultipartFile uploadFile;

	public AtchFileDetailVO(MultipartFile uploadFile) {
		super();
		setUploadFile(uploadFile);
	}

	public void setUploadFile(MultipartFile uploadFile) {
		this.uploadFile = uploadFile;
		this.streFileNm = UUID.randomUUID().toString();
		this.orignlFileNm = uploadFile.getOriginalFilename();
		this.fileExtsn = FilenameUtils.getExtension(orignlFileNm);
		this.fileCn = null;
		this.fileSize = uploadFile.getSize();
		this.fileFancysize = FileUtils.byteCountToDisplaySize(fileSize);
		this.fileMime = uploadFile.getContentType();
		this.fileDwncnt = 0;
	}

	/**
	 * 파일의 2진 데이터와 메타 데이터를 분리 저장하기 위한 메소드
	 * 
	 * @param saveFolder
	 * @throws IOException
	 */
	public void uploadFileSaveTo(File saveFolder) throws IOException {
		if (uploadFile != null) {
			File saveFile = new File(saveFolder, streFileNm);
			uploadFile.transferTo(saveFile);
			this.fileStreCours = saveFile.getCanonicalPath();
		}
	}

	private Integer atchFileId;
	private Integer fileSn;
	private String fileStreCours;
	private String streFileNm;
	private String orignlFileNm;
	private String fileExtsn;
	private String fileCn;
	private long fileSize;
	private String fileFancysize;
	private String fileMime;
	private int fileDwncnt;
	
	/**
	 * 데이터베이스로부터 조회한 파일 메타데이터로 확보한 파일의 바이너리 데이터
	 */
	private Resource savedFile;
}
