package kr.or.ddit.aws.object;

import java.net.URL;

import com.amazonaws.services.s3.AmazonS3;

public class GetObjectImageUrl {
	
	public String getObjcetImageUrl(AmazonS3 s3, String bucketName, String keyName) {
		URL url = s3.getUrl(bucketName, keyName);
		return url.toString();
	}
	
}
