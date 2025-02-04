package kr.or.ddit.aws.object;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class SearchObjects {

	public Map<String, Object> searchObjects(AmazonS3 s3, String bucketName, String folderName, String searchKeyWord) {
	    List<Map<String, Object>> items = new ArrayList<>();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	    // 재귀적으로 S3 객체 검색
	    searchInS3(s3, bucketName, folderName ,folderName, searchKeyWord, items, sdf);

	    // 결과 반환
	    Map<String, Object> response = new HashMap<>();
	    response.put("items", items);

	    return response;
	}

	private void searchInS3(AmazonS3 s3, String bucketName, String ownPath, String prefix, String searchKeyWord,
			List<Map<String, Object>> items, SimpleDateFormat sdf) {
		ListObjectsV2Request request = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(prefix)
				.withDelimiter("/");

		ListObjectsV2Result result = s3.listObjectsV2(request);

		// 소문자로 변환된 검색어
		String lowerCaseSearchKeyWord = searchKeyWord.toLowerCase();

		// 폴더 필터링
		for (String commonPrefix : result.getCommonPrefixes()) {
			if (commonPrefix.toLowerCase().contains(lowerCaseSearchKeyWord)) { // 대소문자 구분 제거
				Map<String, Object> folderInfo = new HashMap<>();
				folderInfo.put("path", commonPrefix.substring(ownPath.length()));
				folderInfo.put("name", commonPrefix.substring(prefix.length(), commonPrefix.length() - 1));
				folderInfo.put("type", "Folder");
				folderInfo.put("size", "-");
				folderInfo.put("lastModified", "-");
				items.add(folderInfo);
			}

			// 재귀적으로 하위 폴더 탐색
			searchInS3(s3, bucketName, ownPath, commonPrefix, searchKeyWord, items, sdf);
		}

		// 파일 필터링
		for (S3ObjectSummary obj : result.getObjectSummaries()) {
			String key = obj.getKey();
			if (!key.equals(prefix) && key.toLowerCase().contains(lowerCaseSearchKeyWord)) { // 대소문자 구분 제거
				Map<String, Object> fileInfo = new HashMap<>();
				fileInfo.put("path", key.substring(ownPath.length()));
				fileInfo.put("name", key.substring(prefix.length()));
				fileInfo.put("type", "File");
				fileInfo.put("size", formatFileSize(obj.getSize()));
				fileInfo.put("lastModified", sdf.format(obj.getLastModified()));
				items.add(fileInfo);
			}
		}
	}

	// 파일 크기 포맷팅
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
