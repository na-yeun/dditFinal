package kr.or.ddit.aws.object;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class InsertFolder {
	public void createPublicFolder(AmazonS3 s3 , String bucketName) {
		String folderPath = "PUBLIC/";
	    // 폴더 경로 뒤에 슬래시(`/`)를 추가하여 폴더처럼 인식되도록 한다
	    if (!folderPath.endsWith("/")) {
	        folderPath += "/";
	    }

	    // S3는 빈 파일을 폴더로 간주하므로 내용이 없는 InputStream 사용
	    ObjectMetadata metadata = new ObjectMetadata();
	    metadata.setContentLength(0); // 파일 크기: 0

	    // 빈 InputStream 생성
	    InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

	    // 폴더 생성용 PutObjectRequest
	    PutObjectRequest request = new PutObjectRequest(bucketName, folderPath, emptyContent, metadata);

	    // 폴더 업로드
	    s3.putObject(request);
	}
	
	public void createPersonalFolder(AmazonS3 s3 , String bucketName, String empId) {
		// 폴더 경로 뒤에 슬래시(`/`)를 추가하여 폴더처럼 인식되도록 한다
		String folderPath = "PERSONAL/"+empId+"/";

	    // S3는 빈 파일을 폴더로 간주하므로 내용이 없는 InputStream 사용
	    ObjectMetadata metadata = new ObjectMetadata();
	    metadata.setContentLength(0); // 파일 크기: 0

	    // 빈 InputStream 생성
	    InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

	    // 폴더 생성용 PutObjectRequest
	    PutObjectRequest request = new PutObjectRequest(bucketName, folderPath, emptyContent, metadata);

	    // 폴더 업로드
	    s3.putObject(request);
	}
	
	public void createFolder(AmazonS3 s3 , String bucketName, String KeyName , String newFolderName) {
		// 폴더 경로 뒤에 슬래시(`/`)를 추가하여 폴더처럼 인식되도록 한다
	    if (!newFolderName.endsWith("/")) {
	    	newFolderName += "/";
	    }
	    
	    String folderPath = KeyName + newFolderName;
	    
	    // S3는 빈 파일을 폴더로 간주하므로 내용이 없는 InputStream 사용
	    ObjectMetadata metadata = new ObjectMetadata();
	    metadata.setContentLength(0); // 파일 크기: 0

	    // 빈 InputStream 생성
	    InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

	    // 폴더 생성용 PutObjectRequest
	    PutObjectRequest request = new PutObjectRequest(bucketName, folderPath, emptyContent, metadata);

	    // 폴더 업로드
	    s3.putObject(request);
	}
}
