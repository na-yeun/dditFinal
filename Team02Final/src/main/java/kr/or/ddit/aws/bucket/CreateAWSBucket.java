package kr.or.ddit.aws.bucket;

import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;

public class CreateAWSBucket {
	
	public Bucket createBucket(AmazonS3 s3, String bucketName) {
		ReadBucket readBucket = new ReadBucket();
		Bucket bucket = null;
	     if (s3.doesBucketExistV2(bucketName)) {
	            System.out.format("해당 Bucket이 이미 존재.\n", bucketName);
	            bucket = readBucket.getBucket(s3, bucketName);
	        } else {
	            try {
	            	bucket = s3.createBucket(bucketName);
	            } catch (AmazonS3Exception e) {
	                System.err.println(e.getErrorMessage());
	            }
	        }
	        return bucket;
	}
	
}
