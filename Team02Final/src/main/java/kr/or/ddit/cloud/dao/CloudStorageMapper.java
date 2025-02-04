package kr.or.ddit.cloud.dao;

import org.apache.ibatis.annotations.Mapper;

import kr.or.ddit.cloud.vo.CloudVO;

@Mapper
public interface CloudStorageMapper {
	
	public CloudVO selectCloud(String empId);
	
}
