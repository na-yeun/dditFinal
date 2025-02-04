package kr.or.ddit.aws.bucket;

import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;

public class ReadBucket {
	 public Bucket getBucket(AmazonS3 s3, String bucketName) {
	        Bucket bucket = null;
	        List<Bucket> buckets = s3.listBuckets();
	        for (Bucket b : buckets) {
	            if (b.getName().equals(bucketName)) {
	            	bucket = b;
	            }
	        }
	        return bucket;
	    }
}
