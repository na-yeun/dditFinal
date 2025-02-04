package kr.or.ddit.position.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PositionMapper {
    public String selectPosiNameByPosiId(String posiId);
}
