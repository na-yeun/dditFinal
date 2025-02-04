package kr.or.ddit.approval.controller;

import kr.or.ddit.approval.dao.ApprovalDocumentMapper;
import kr.or.ddit.approval.service.ApprovalDocumentService;
import org.springframework.core.io.Resource;
import kr.or.ddit.atch.service.AtchFileService;
import kr.or.ddit.atch.vo.AtchFileDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import java.io.IOException;
import java.nio.charset.Charset;

@Controller
@RequestMapping("/{companyId}/approval")
public class ApprovalFileController {

    @Autowired
    private ApprovalDocumentService documentService;
    @Autowired
    private AtchFileService atchFileService;

    @GetMapping("/{docId}/atch/{atchFileId}/{fileSn}")
    public ResponseEntity<Resource> downloadApprovalDocument(
            @PathVariable int atchFileId,
            @PathVariable int fileSn) throws IOException {

        // 기존 로직
        AtchFileDetailVO atch = documentService.download(atchFileId, fileSn);
        Resource savedFile = atch.getSavedFile();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(atch.getFileSize());
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(atch.getOrignlFileNm(), Charset.forName("UTF-8")).build();
        headers.setContentDisposition(disposition);
        return ResponseEntity.ok().headers(headers).body(savedFile);
    }
}
