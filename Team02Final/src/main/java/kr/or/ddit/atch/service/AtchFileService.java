package kr.or.ddit.atch.service;

import kr.or.ddit.atch.vo.AtchFileDetailVO;
import kr.or.ddit.atch.vo.AtchFileVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface AtchFileService {
	/**
	 * 첨부파일 그룹 저장(메타데이터와 2진 데이터 분리 저장)
	 * 
	 * @param atchFile
	 * @param saveFolder 2진 데이터 저장 디렉토리
	 * @throws Exception
	 */
	public void createAtchFile(AtchFileVO atchFile, File saveFolder);

	/**
	 * 사용중인 첨부파일 그룹 조회
	 * 
	 * @param atchFileId
	 * @param saveFolder 2진 데이터 저장 디렉토리
	 * @return
	 */
	public default AtchFileVO readAtchFileEnable(int atchFileId, File saveFolder) {
		return readAtchFile(atchFileId, true, saveFolder);
	}

	/**
	 * 사용 여부에 따른 첨부파일 그룹 조회
	 * 
	 * @param atchFileId
	 * @param enable
	 * @param saveFolder
	 * @return
	 */
	public AtchFileVO readAtchFile(int atchFileId, boolean enable, File saveFolder);

	/**
	 * 첨부파일 한건 조회
	 * 
	 * @param atchFileId
	 * @param fileSn
	 * @param saveFolder 2진 데이터 저장 디렉토리
	 * @return
	 */
	public AtchFileDetailVO readAtchFileDetail(int atchFileId, int fileSn, File saveFolder);

	/**
	 * 파일 한건 삭제 (메타데이터와 2진 데이터 분리 삭제)
	 * 
	 * @param atchFileId
	 * @param fileSn
	 * @param saveFolder 2진 데이터 저장 디렉토리
	 */
	public void removeAtchFileDetail(int atchFileId, int fileSn, File saveFolder);

	/**
	 * 파일 그룹 비활성화
	 * 
	 * @param atchFildId
	 */
	public void disableAtchFile(int atchFildId);

	/**
	 * 사용하지 않는 파일 그룹 제거
	 * 
	 * @param atchFileId
	 */
	public void removeDiabledAtchFile(int atchFileId);

    public String saveImageFile(MultipartFile file, String boardType) throws IOException;
}
