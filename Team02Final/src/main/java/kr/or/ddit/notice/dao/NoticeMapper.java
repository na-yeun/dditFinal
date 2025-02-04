package kr.or.ddit.notice.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.notice.vo.NoticeVO;

@Mapper
public interface NoticeMapper{
    public int insertNotice(NoticeVO notice);

    public NoticeVO selectNoticeDetail(String NoticeNo);

    public int updateNotice(NoticeVO notice);

    public int deleteNotice(String NoticeNo);

	public int selectTotalRecord(PaginationInfo<NoticeVO> paging);

	public List<NoticeVO> selectNoticeList(PaginationInfo<NoticeVO> paging);
}
