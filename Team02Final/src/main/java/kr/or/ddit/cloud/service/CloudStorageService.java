package kr.or.ddit.cloud.service;

import kr.or.ddit.cloud.vo.CloudVO;

public interface CloudStorageService {
	
	public CloudVO readCloudInfo(String empId);
	
	public int createCloudInfo(CloudVO cloud);
	
	public int deleteCloudInfo(String empId);
	
}
