package kr.or.ddit.aws.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class S3ObjectDTO {
    private String name;
    private String type;
    private String size;
    private String lastModified;
}

