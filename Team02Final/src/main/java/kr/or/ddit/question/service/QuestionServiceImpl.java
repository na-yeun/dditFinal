package kr.or.ddit.question.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import kr.or.ddit.atch.service.AtchFileService;
import kr.or.ddit.atch.vo.AtchFileDetailVO;
import kr.or.ddit.atch.vo.AtchFileVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.exception.BoardException;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.question.dao.QuestionMapper;
import kr.or.ddit.question.vo.CategoryVO;
import kr.or.ddit.question.vo.QuestionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
	
	private final AtchFileService atchFileService;
	
	// DirectoryInfo 설정 및 properties 파읿 빈 등록(context-common.xml)
	@Value("#{dirInfo.saveDir}")
	private Resource saveFolderRes;
	private File saveFolder;
	
	@PostConstruct
	public void init() throws IOException {
		this.saveFolder = saveFolderRes.getFile();
	}
	
	@Override
	public int readTotalRecord(PaginationInfo paging) {
		return mapper.selectTotalRecord(paging);
	}
	
	@Autowired
	private QuestionMapper mapper;
		
	@Override
	public QuestionVO readQuestion(String quNo) {
		QuestionVO question = mapper.selectQuestion(quNo);
		if(question == null)
			throw new BoardException(String.format("%d 번 글이 없음", quNo));
		return question;
	}

	
	@Override
	public List<QuestionVO> readQuestionList(PaginationInfo paging) {
		if(paging!=null) {
			int totalRecord = mapper.selectTotalRecord(paging);
			paging.setTotalRecord(totalRecord);
		}
		return mapper.selectQuestionList(paging);
	}

	@Override
	public ServiceResult createQuestion(QuestionVO question) {
		Integer atchFileId = Optional.ofNullable(question.getAtchFile())
				.filter(af->! CollectionUtils.isEmpty(af.getFileDetails()))
				.map(af -> {
					atchFileService.createAtchFile(af, saveFolder); // 먼저 돌아가야 하는 이유 ?
					return af.getAtchFileId();
				}).orElse(null);
		
		question.setAtchFileId(atchFileId);
		int rowcnt = mapper.insertQuestion(question);
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
	public ServiceResult modifyQuestion(QuestionVO question) {

        //시그치너에서 오는 notice는 바뀐데이터
        //savedNoticed는 기존 db에서갖고오는 바뀌지않은 데이터
        QuestionVO savedNotice = readQuestion(question.getQuNo());
        AtchFileVO savedAtchFile = savedNotice.getAtchFile();


        Integer newAtchFileId = Optional.ofNullable(question.getAtchFile())
                .filter(af -> af.getFileDetails() != null)
                // 추가,병합,유지 작업의 반복문 돌리러가기
                .map(af ->mergeSavedDetailsAndNewDetails(savedAtchFile, af))
                .orElse(null);

        question.setAtchFileId(newAtchFileId);
		int rowcnt = mapper.updateQuestion(question);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public ServiceResult removeQuestion(String quNo) {
		QuestionVO question = mapper.selectQuestion(quNo);
		Optional.ofNullable(question.getAtchFileId())
				.ifPresent(fid -> atchFileService.disableAtchFile(fid));
		int rowcnt = mapper.deleteQuestion(question.getQuNo());
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}
	
	@Override
	public AtchFileDetailVO download(int atchFileId, int fileSn) {
		return Optional.ofNullable(atchFileService.readAtchFileDetail(atchFileId, fileSn, saveFolder))
						.filter(fd -> fd.getSavedFile().exists())
						.orElseThrow(() -> new BoardException(String.format("[%d, %d]해당 파일이 없음.", atchFileId, fileSn)));
	}
	
	@Override
	public void removeFile(int atchFileId, int fileSn) {
		atchFileService.removeAtchFileDetail(atchFileId, fileSn, saveFolder);
	}

	@Override
	public List<CategoryVO> readCategoryList() {
		List<CategoryVO> category = mapper.selectCategoryList();
		return category;
	}

	@Override
	public ServiceResult updateAnswer(QuestionVO question) {
		int rowcnt = mapper.updateAnswer(question);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public ServiceResult deleteAnswer(QuestionVO question) {
		int rowcnt = mapper.deleteAnswer(question);
		return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}
	
}
