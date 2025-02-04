package kr.or.ddit.atch.service;

import kr.or.ddit.atch.dao.AtchFileMapper;
import kr.or.ddit.atch.vo.AtchFileDetailVO;
import kr.or.ddit.atch.vo.AtchFileVO;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.function.Failable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class AtchFileServiceImpl implements AtchFileService {

	@Autowired
	private AtchFileMapper mapper;

	@Value("#{dirInfo.saveDir}")
	private Resource saveFolderRes;
	private File saveFolder;

	@PostConstruct
	public void init() throws IOException {
		this.saveFolder = saveFolderRes.getFile();
	}

	//이미지 저장용
	@Override
	public String saveImageFile(MultipartFile file, String boardType) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("파일이 비었네!");
		}

		//날짜별 폴더(디렉토리) 만들기
		String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		String dirPath = saveFolder + File.separator + boardType + File.separator + datePath.replace("/", File.separator);

		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// 파일명 생성
		String originalFilename = file.getOriginalFilename();
		String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
		String newFileName = System.currentTimeMillis() + "." + ext;

		// 진짜 파일생성
		File targetFile = new File(dir, newFileName);

		// 썸네일 만들기
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Thumbnails.of(file.getInputStream())
				.size(800, 800)
				.outputQuality(0.7)
				.toOutputStream(outputStream);

		// 파일 출력(저장)
		try (FileOutputStream fos = new FileOutputStream(targetFile)) {
			fos.write(outputStream.toByteArray());
		}

		// 브라우저에서 접근 가능한 URL 구성
		// ResourceHandler 설정이 필요함: /resources/images/** => D:/upload/images/
		String imageUrl = "/work2gether/images/" + boardType + "/" + datePath + "/" + newFileName;
		return imageUrl;

	}


	@Override
	public void createAtchFile(AtchFileVO atchFile, File saveFolder) {
		Optional.of(atchFile)
				.map(AtchFileVO::getFileDetails)
				.ifPresent(fds -> 
					fds.forEach(
						Failable.asConsumer(fd -> fd.uploadFileSaveTo(saveFolder))
					)
				);
		mapper.insertAtchFile(atchFile);
	}

	/**
	 * 
	 * 파일 메타데이터와 2진 데이터 결합
	 * 
	 * @param fileDetail
	 * @param saveFolder
	 */
	private void mergeMetadAndBinaryData(AtchFileDetailVO fileDetail, File saveFolder) {
		FileSystemResource savedFile = new FileSystemResource(new File(saveFolder, fileDetail.getStreFileNm()));
		fileDetail.setSavedFile(savedFile);
	}

	@Override
	public AtchFileVO readAtchFile(int atchFileId, boolean enable, File saveFolder) {
		AtchFileVO atchFile = mapper.selectAtchFile(atchFileId, enable);
		Optional.ofNullable(atchFile)
				.map(AtchFileVO::getFileDetails)
				.ifPresent(fds -> 
					fds.forEach(fd -> mergeMetadAndBinaryData(fd, saveFolder))
				);
		return atchFile;
	}

	@Override
	public AtchFileDetailVO readAtchFileDetail(int atchFileId, int fileSn, File saveFolder) {
		AtchFileDetailVO fileDetail = mapper.selectAtchFileDetail(atchFileId, fileSn);
		if (fileDetail != null) {
			mergeMetadAndBinaryData(fileDetail, saveFolder);
			mapper.incrementDowncount(atchFileId, fileSn);
		}
		return fileDetail;
	}

	/**
	 * 파일 한건의 메타데이터와 2진 데이터 삭제
	 * 
	 * @param fileDetail
	 * @param saveFolder
	 * @throws IOException
	 */
	private void deleteFileDetail(AtchFileDetailVO fileDetail, File saveFolder) throws IOException {
		mergeMetadAndBinaryData(fileDetail, saveFolder);
		FileUtils.deleteQuietly(fileDetail.getSavedFile().getFile());
		mapper.deleteAtchFileDetail(fileDetail.getAtchFileId(), fileDetail.getFileSn());
	}

	@Override
	public void removeAtchFileDetail(int atchFileId, int fileSn, File saveFolder) {
		AtchFileDetailVO target = mapper.selectAtchFileDetail(atchFileId, fileSn);
		Optional.ofNullable(target)
				.ifPresent(
					Failable.asConsumer(fd -> 
						deleteFileDetail(fd, saveFolder)
					)
				);
	}

	@Override
	public void disableAtchFile(int atchFildId) {
		mapper.disableAtchFile(atchFildId);
	}

	@Override
	public void removeDiabledAtchFile(int atchFileId) {
		mapper.deleteDisabledAtchFileDetails(atchFileId);
		mapper.deleteDisabledAtchFile(atchFileId);
	}


}
