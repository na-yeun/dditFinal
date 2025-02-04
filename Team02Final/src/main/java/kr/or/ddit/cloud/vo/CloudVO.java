package kr.or.ddit.cloud.vo;

import lombok.Data;

@Data
public class CloudVO {
	private String empId;
	private String perCloudPath;
	private String pubCloudPath;
	
	private String contractBucket;
	private String provAccesskey;
	private String provSecretkey;
}
