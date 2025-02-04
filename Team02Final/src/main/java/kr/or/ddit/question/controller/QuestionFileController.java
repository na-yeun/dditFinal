package kr.or.ddit.question.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.atch.service.AtchFileService;
import kr.or.ddit.atch.vo.AtchFileDetailVO;
import kr.or.ddit.question.service.QuestionService;

@Controller
@RequestMapping("/{companyId}/question")
public class QuestionFileController {
	
    @Autowired
    private AtchFileService atchFileService;
	
	@Autowired
	private QuestionService questionService;
	
	  // noticeNo 없이 이미지를 업로드하는 엔드포인트 (예: 신규 작성 시 사용)
    @PostMapping("/atch/uploadImage")
    @ResponseBody
    public Map<String, Object> uploadImageNoNoticeNo(
            @RequestParam("uploadFile") MultipartFile file
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            String boardType = "question";

            String imageUrl = atchFileService.saveImageFile(file, boardType);

            result.put("url", imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
            result.put("error", "이미지 업로드 실패");
        }
        return result;
    }

    // noticeNo 있는 경우 (다운로드 예시)
    // /{companyId}/notice/{noticeNo}/atch/{atchFileId}/{fileSn}
    @GetMapping("/{quNo}/atch/{atchFileId}/{fileSn}")
    public ResponseEntity<Resource> download(
            @PathVariable int atchFileId,
            @PathVariable int fileSn) throws IOException {

        // 기존 로직
        AtchFileDetailVO atch = questionService.download(atchFileId, fileSn);
        Resource savedFile = atch.getSavedFile();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(atch.getFileSize());
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(atch.getOrignlFileNm(), Charset.forName("UTF-8")).build();
        headers.setContentDisposition(disposition);
        return ResponseEntity.ok().headers(headers).body(savedFile);
    }


	@DeleteMapping("/{quNo}/atch/{atchFileId}/{fileSn}")
	@ResponseBody
	public Map<String, Object> deleteAttatch(@PathVariable int atchFileId, @PathVariable int fileSn) {
		questionService.removeFile(atchFileId, fileSn);
		return Collections.singletonMap("success", true);
	}

}
