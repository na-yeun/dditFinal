package kr.or.ddit.atch.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AtchFileVO {
	private Integer atchFileId;
	private String creatDt;
	private boolean useAt;

	@JsonIgnore
	@ToString.Exclude
	@Nullable
	@Valid
	private List<@NotNull AtchFileDetailVO> fileDetails;
}
