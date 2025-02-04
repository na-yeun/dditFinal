package kr.or.ddit.cloud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.ddit.cloud.dao.CloudStorageMapper;
import kr.or.ddit.cloud.vo.CloudVO;

@Service
public class CloudStorageServiceImpl implements CloudStorageService {
	
	@Autowired
	private CloudStorageMapper mapper;
	
	@Override
	public CloudVO readCloudInfo(String empId) {
		return mapper.selectCloud(empId);
	}

	@Override
	public int createCloudInfo(CloudVO cloud) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteCloudInfo(String empId) {
		// TODO Auto-generated method stub
		return 0;
	}

}
