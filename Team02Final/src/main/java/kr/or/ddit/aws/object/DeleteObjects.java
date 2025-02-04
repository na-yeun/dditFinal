package kr.or.ddit.aws.object;

import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

public class DeleteObjects {
	public void DeleteObjects(AmazonS3 s3, String bucketName, String cloudPath ,List<String> keyNames) {
		for(String key : keyNames) {
			String keyName = cloudPath+key;
			System.out.println(keyName);
			s3.deleteObject(new DeleteObjectRequest(bucketName, keyName));
		}
	}
}
