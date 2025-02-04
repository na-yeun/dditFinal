package kr.or.ddit.aws.object;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class UploadObjects {
	
	public void insertPerCloudObject(AmazonS3 s3, String bucketName, 
			String personalPath, String innerFolderPath, MultipartFile[] files) {
		for (MultipartFile file : files) {
			String savePath = "";
			if(StringUtils.isBlank(innerFolderPath)) {
				savePath = personalPath + file.getOriginalFilename();				
			}else {
				savePath = personalPath + innerFolderPath + file.getOriginalFilename();
			}
			System.out.println("savePath =================> " + savePath);
            try {
                File tempFile = convertMultipartFileToFile(file);
                s3.putObject(new PutObjectRequest(bucketName, savePath, tempFile));
                tempFile.delete();
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
	}

	public void insertPubCloudObject(AmazonS3 s3, String bucketName, String publicPath,
			String innerFolderPath, MultipartFile[] files) {
		for (MultipartFile file : files) {
			String savePath = "";
			if(StringUtils.isEmpty(innerFolderPath)) {
				savePath = publicPath + file.getOriginalFilename();				
			}else {
				savePath = publicPath + innerFolderPath + file.getOriginalFilename();
			}
			
			try {
				File tempFile = convertMultipartFileToFile(file);
				s3.putObject(new PutObjectRequest(bucketName, savePath, tempFile));
				tempFile.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        return tempFile;
    }
	
}
