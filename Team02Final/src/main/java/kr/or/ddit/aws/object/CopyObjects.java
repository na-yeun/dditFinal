package kr.or.ddit.aws.object;

import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;

public class CopyObjects {
	public void copyObjects(AmazonS3 s3, String bucketName, String pathName, List<String> originObjects, List<String> targetObjects) {
		DeleteObjects deleteObjects = new DeleteObjects();
		 for (int i = 0; i < originObjects.size(); i++) {
				String sourceKey = pathName+originObjects.get(i); // 원본 경로
				String destinationKey = pathName+targetObjects.get(i); // 대상 경로

				try {
					// 파일 복사
					CopyObjectRequest copyRequest = new CopyObjectRequest(bucketName, sourceKey, bucketName,destinationKey);
					s3.copyObject(copyRequest);

					// 원본 파일 삭제
					s3.deleteObject(bucketName, sourceKey);
				} catch (Exception e) {
					System.err.println("파일 이동 중 오류 발생: 원본(" + sourceKey + "), 대상(" + destinationKey + ")");
					e.printStackTrace();
				}
		 }
	}
}
 