package kr.or.ddit.aws.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThumbnailDTO {
	private String imageUrl;
	private String fileName;
	private String size;
	private String lastModified;
	private String filePath;
}
