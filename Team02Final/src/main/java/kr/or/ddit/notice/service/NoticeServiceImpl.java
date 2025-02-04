package kr.or.ddit.notice.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kr.or.ddit.atch.service.AtchFileService;


import kr.or.ddit.atch.vo.AtchFileDetailVO;
import kr.or.ddit.atch.vo.AtchFileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.core.io.Resource;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.notice.dao.NoticeMapper;
import kr.or.ddit.notice.vo.NoticeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService{

    @Autowired
    private final NoticeMapper noticeMapper;
    @Autowired
    private final AtchFileService atchFileService; // 첨부파일 서비스 공통
    // properties 설정 (FreeBoard 때와 동일하게 init)
    @Value("#{dirInfo.saveDir}")
    private Resource saveFolderRes;
    private File saveFolder;

    @PostConstruct
    public void init() throws IOException {
        this.saveFolder = saveFolderRes.getFile();
    }

    @Override
    public ServiceResult createNotice(NoticeVO notice) {
        // 첨부파일 있는 경우 처리
        Integer atchFileId = Optional.ofNullable(notice.getAtchFile())
                .filter(af-> !CollectionUtils.isEmpty(af.getFileDetails()))
                .map(af -> {
                    atchFileService.createAtchFile(af, saveFolder);
                    return af.getAtchFileId();
                }).orElse(null);

        notice.setAtchFileId(atchFileId);

        int rowcnt = noticeMapper.insertNotice(notice);

        return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
    }

    private Integer mergeSavedDetailsAndNewDetails(AtchFileVO savedAtchFile, AtchFileVO newAtchFile) {
        AtchFileVO mergeAtchFile = new AtchFileVO();
        List<AtchFileDetailVO> fileDetails = Stream.concat(
                Optional.ofNullable(savedAtchFile)
                        .filter(saf->! CollectionUtils.isEmpty(saf.getFileDetails()))
                        .map(saf->saf.getFileDetails().stream())
                        .orElse(Stream.empty())
                , Optional.ofNullable(newAtchFile)
                        .filter(naf->! CollectionUtils.isEmpty(naf.getFileDetails()))
                        .map(naf->naf.getFileDetails().stream())
                        .orElse(Stream.empty())
        ).collect(Collectors.toList());

        mergeAtchFile.setFileDetails(fileDetails);

        if( ! mergeAtchFile.getFileDetails().isEmpty() ) {
            atchFileService.createAtchFile(mergeAtchFile, saveFolder);
        }

        if (savedAtchFile != null && savedAtchFile.getFileDetails() != null) {
            // 기존 첨부파일 그룹은 비활성화
            atchFileService.disableAtchFile(savedAtchFile.getAtchFileId());
        }

        return mergeAtchFile.getAtchFileId();
    }

    @Override
    public ServiceResult modifyNotice(NoticeVO notice) {

        //시그치너에서 오는 notice는 바뀐데이터
        //savedNoticed는 기존 db에서갖고오는 바뀌지않은 데이터
        NoticeVO savedNotice = readNoticeDetail(notice.getNoticeNo());
        AtchFileVO savedAtchFile = savedNotice.getAtchFile();


        Integer newAtchFileId = Optional.ofNullable(notice.getAtchFile())
                .filter(af -> af.getFileDetails() != null)
                // 추가,병합,유지 작업의 반복문 돌리러가기
                .map(af ->mergeSavedDetailsAndNewDetails(savedAtchFile, af))
                .orElse(null);

        notice.setAtchFileId(newAtchFileId);

        int rowcnt = noticeMapper.updateNotice(notice);

        return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
    }

    @Override
    public void removeNotice(String noticeId) {
        NoticeVO notice = noticeMapper.selectNoticeDetail(noticeId);
        Optional.ofNullable(notice.getAtchFileId())
                .ifPresent(fid -> atchFileService.disableAtchFile(fid));
        noticeMapper.deleteNotice(noticeId);
    }

    @Override
    public List<NoticeVO> readNoticeList(PaginationInfo<NoticeVO> paging) {
        int totalRecord = noticeMapper.selectTotalRecord(paging);
        paging.setTotalRecord(totalRecord);
        return noticeMapper.selectNoticeList(paging);
    }

    @Override
    public NoticeVO readNoticeDetail(String NoticeNo) {
        return noticeMapper.selectNoticeDetail(NoticeNo);
    }

    @Override
    public AtchFileDetailVO download(int atchFileId, int fileSn) {
        return Optional.ofNullable(atchFileService.readAtchFileDetail(atchFileId, fileSn, saveFolder))
                .filter(fd -> fd.getSavedFile().exists())
                .orElseThrow(() -> new RuntimeException(String.format("[%d, %d]해당 파일이 없음.", atchFileId, fileSn)));
//                .orElseThrow(() -> new BoardException(String.format("[%d, %d]해당 파일이 없음.", atchFileId, fileSn)));
    }

    @Override
    public void removeFile(int atchFileId, int fileSn) {
        atchFileService.removeAtchFileDetail(atchFileId, fileSn, saveFolder);
    }
}
