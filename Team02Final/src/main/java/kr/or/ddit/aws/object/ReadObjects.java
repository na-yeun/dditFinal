package kr.or.ddit.aws.object;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import kr.or.ddit.aws.dto.S3ObjectDTO;

public class ReadObjects {
	
	public List<S3ObjectSummary> getObjects(AmazonS3 s3,String bucketName, String keyName){
		List<S3ObjectSummary> objectList = new ArrayList<S3ObjectSummary>();
		try {
			ListObjectsV2Request req = new ListObjectsV2Request()
					.withBucketName(bucketName)
					.withPrefix(keyName);
			
			ListObjectsV2Result result = s3.listObjectsV2(req);
			
			objectList = result.getObjectSummaries();
		} catch (AmazonServiceException e) {
			System.err.print(e.getErrorMessage());
			System.exit(1);
		}
		
		return objectList;
	}
	
	public Map<String, List<S3ObjectDTO>> getS3Objects(AmazonS3 s3, String bucketName, String prefix) {
	    List<S3ObjectDTO> files = new ArrayList<>();
	    List<S3ObjectDTO> folders = new ArrayList<>();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	    // S3 요청: prefix와 delimiter 사용
	    ListObjectsV2Request request = new ListObjectsV2Request()
	            .withBucketName(bucketName)
	            .withPrefix(prefix)  // 현재 폴더 경로
	            .withDelimiter("/"); // 하위 폴더 구분

	    ListObjectsV2Result result = s3.listObjectsV2(request);

	    // 폴더 목록 가져오기
	    for (String commonPrefix : result.getCommonPrefixes()) {
	        String folderName = commonPrefix.substring(prefix.length(), commonPrefix.length() - 1);
	        S3ObjectDTO folderDTO = new S3ObjectDTO(folderName, "Folder", "-", "-");
	        folders.add(folderDTO);
	    }

	    // 파일 목록 가져오기
	    for (S3ObjectSummary summary : result.getObjectSummaries()) {
	        String fileName = summary.getKey().substring(prefix.length());
	        if (!fileName.isEmpty()) { // 폴더 자체를 나타내는 key 제외
	            S3ObjectDTO fileDTO = new S3ObjectDTO(
	                    fileName,
	                    "File",
	                    formatFileSize(summary.getSize()),
	                    sdf.format(summary.getLastModified())
	            );
	            files.add(fileDTO);
	        }
	    }

	    Map<String, List<S3ObjectDTO>> objectMap = new HashMap<>();
	    objectMap.put("folders", folders);
	    objectMap.put("files", files);
	    return objectMap;
	}

	
	private String formatFileSize(long size) {
	    if (size < 1024) {
	        return size + " Bytes";
	    } else if (size < 1024 * 1024) {
	        return String.format("%.2f KB", size / 1024.0);
	    } else if (size < 1024 * 1024 * 1024) {
	        return String.format("%.2f MB", size / (1024.0 * 1024));
	    } else {
	        return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
	    }
	}


}
