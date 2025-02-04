package kr.or.ddit.employee.vo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.annotation.ValidFileExtension;
import kr.or.ddit.commons.validate.EditGroup;
import kr.or.ddit.commons.validate.JoinGroup;
import kr.or.ddit.commons.validate.PhoneAuthGroup;
import kr.or.ddit.commons.validate.UpdateGroup;
import kr.or.ddit.commons.vo.CommonCodeVO;
import kr.or.ddit.organitree.vo.DepartmentVO;
import kr.or.ddit.organitree.vo.TeamMemberVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.coobird.thumbnailator.Thumbnails;

@Data
@ToString
@EqualsAndHashCode(of = { "empId" }, callSuper = true)
@NoArgsConstructor
public class EmployeeVO extends AccountVO implements Serializable {

	private String empId;
	@NotBlank(groups = { JoinGroup.class, UpdateGroup.class })
	@Size(max=30)
	private String empPass;
	@NotBlank(groups = { PhoneAuthGroup.class })
	private String empName;

	@Pattern(regexp = "^\\d{8}$")
	private String empBirth;
	@NotBlank(groups = JoinGroup.class)
	private String empGender;
	@NotBlank(groups = JoinGroup.class)
	private String empAddr1;
	@NotBlank(groups = JoinGroup.class)
	private String empAddr2;
	@NotBlank(groups = { PhoneAuthGroup.class, JoinGroup.class })
	@Pattern(regexp = "\\d{11}", message = "전화번호는 숫자 11자리여야 합니다.")
	private String empPhone;
	@NotBlank(groups = PhoneAuthGroup.class)
	@Email
	private String empMail;
	private String empJoin;
	private String empStatus;

	private String posiId;
	private String departCode;
	private String companyId;
	@NotBlank(groups = EditGroup.class)
	@Size(max=30, message = "30글자 이상 설정할 수 없습니다.", groups = EditGroup.class)
	private String myComment;

	// 회원 프로필 이미지 관리용
	private byte[] empImg; // 데이터베이스 지원
	@ValidFileExtension(allowedExtensions = {"jpg", "png", "gif"}, message = "이미지 파일의 형식이 맞지 않습니다.")
	private MultipartFile empImage; // 클라이언트 업로드 파일 지원

	// 회원 도장 이미지 관리용
	private byte[] empSignimg; // 데이터베이스 지원
	@ValidFileExtension(allowedExtensions = {"jpg", "png", "gif"}, message = "이미지 파일의 형식이 맞지 않습니다.")
	private MultipartFile empSignimage; // 클라이언트 업로드 파일 지원

	public void setEmpImage(MultipartFile empImage) throws IOException {
		this.empImage = empImage;
		this.empImg = processImage(empImage);
	}

	public String getBase64EmpImg() {
		if (empImg != null && empImg.length > 0) {
			return Base64.getEncoder().encodeToString(empImg);
		} else {
			return null;
		}
	}

	public void setEmpSignimage(MultipartFile empSignimage) throws IOException {
		this.empSignimage = empSignimage;
		this.empSignimg = processImage(empSignimage);
	}

	public String getBase64EmpSignimg() {
		if (empSignimg != null && empSignimg.length > 0) {
			return Base64.getEncoder().encodeToString(empSignimg);
		} else {
			return null;
		}
	}

	// 이미지 압축 메서드 (Thumbnailator 사용)
	private byte[] compressImage(MultipartFile image) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// 이미지 압축 (예: 800px 크기 제한, 품질 70%)
		Thumbnails.of(image.getInputStream()).size(500, 500) // 크기 설정
				.outputQuality(0.8) // 품질 설정 (0.0 ~ 1.0)
				.toOutputStream(outputStream);

		return outputStream.toByteArray(); // 압축된 이미지를 byte[]로 반환
	}

	private byte[] processImage(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			return null;
		}
		return compressImage(file); // 이미지 압축 로직 호출
	}

	// Has A
	private OAuthVO oAuth;

	private List<CommonCodeVO> commonList;
	private String posiName;
	// Has Many
	private List<TeamMemberVO> teamMemberList;
	// Has A
	private DepartmentVO departmentVO;

}
