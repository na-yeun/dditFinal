package kr.or.ddit.cloud.service;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.amazonaws.services.auditmanager.model.URL;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import kr.or.ddit.annotation.RootContextWebConfig;
import kr.or.ddit.aws.bucket.CreateAWSBucket;
import kr.or.ddit.aws.connection.GetAWSUser;
import kr.or.ddit.aws.dto.S3ObjectDTO;
import kr.or.ddit.aws.object.DeleteObjects;
import kr.or.ddit.aws.object.GetObjectImageUrl;
import kr.or.ddit.aws.object.InsertFolder;
import kr.or.ddit.aws.object.ReadObjects;
import kr.or.ddit.aws.object.UploadObjects;
import kr.or.ddit.cloud.vo.CloudVO;

@RootContextWebConfig
class CloudStorageServiceImplTest {

	@Inject
	private CloudStorageService service;
	
	@Test
	void testReadCloud() {
		CloudVO cloud = service.readCloudInfo("EMP009");
		GetAWSUser awsUser = new GetAWSUser();
		ReadObjects readObjects = new ReadObjects();
		AmazonS3 s3 = awsUser.getAWSUser(cloud.getProvAccesskey(), cloud.getProvSecretkey());
		List<S3ObjectSummary> objList = readObjects.getObjects(s3, cloud.getContractBucket(), cloud.getPerCloudPath());
		
//		객체 이동 (복사 -> 생성 -> 원본삭제)
		
//		복사할 객체 경로
		String objectPath = "images/profileImg/jam/";
		
//		이동하고싶은 객체 경로
		
//		String copyPath = "images/";
//
//		CopyObjectRequest copyObjectRequest = new CopyObjectRequest(cloud.getContractBucket(),
//				cloud.getPerCloudPath() + objectPath, cloud.getContractBucket(), cloud.getPerCloudPath() + copyPath);
//		s3.copyObject(copyObjectRequest);
//		System.out.println("복사 완료 ====================> O");
//
//		List<String> list = new ArrayList<String>();
//		list.add(objectPath);
//		DeleteObjects deleteObjects = new DeleteObjects();
//		deleteObjects.DeleteObjects(s3, cloud.getContractBucket(), cloud.getPerCloudPath(), list);
//		System.out.println("삭제 성공");
		
		
		
//		검색 - 성공
		String SearchKeyWord = "pro";
		
		ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(cloud.getContractBucket()).withPrefix(cloud.getPerCloudPath());
		
		ListObjectsV2Result result;
		
		result = s3.listObjectsV2(req);
		
		for(S3ObjectSummary objectSummary : result.getObjectSummaries()) {
			if(objectSummary.getKey().contains(SearchKeyWord)) {
				if(objectSummary.getKey().endsWith("/")) {
					System.out.println(" - folder - " + objectSummary.getKey() + " (Size: " + objectSummary.getSize() + " bytes)");
				}else {
					System.out.println(" - file - " + objectSummary.getKey() + " (Size: " + objectSummary.getSize() + " bytes)");
				}
			}
		}
		
		
//		Map<String, List<S3ObjectDTO>> searchObjList = readObjects.getS3Objects(s3, cloud.getContractBucket(), "lombok.jar");
		
//		for(S3ObjectSummary obj : objList) {
//			System.out.println("Key : "+obj.getKey());
//		}
		
//		for (Map.Entry<String, List<S3ObjectDTO>> entry : searchObjList.entrySet()) {
//		    String key = entry.getKey();
//		    List<S3ObjectDTO> objects = entry.getValue();
//
//		    System.out.println("Key: " + key);
//		    for (S3ObjectDTO obj : objects) {
//		        System.out.println("Object: " + obj); // S3ObjectDTO의 toString() 메서드가 정의되어 있으면 객체 정보 출력
//		    }
//		}
		
//		GetObjectImageUrl getObjectImageUrl = new GetObjectImageUrl();
//		String keyName = cloud.getPerCloudPath()+"두비두밥.png";
//		String urlPath = getObjectImageUrl.getObjcetImageUrl(s3, cloud.getContractBucket(), keyName);
//		System.out.println(urlPath);
//		파일 업로드
//		String fileName = "sample.jpg";
//		String sampleImgPath = "D:/00.medias/images/"+fileName;

//		UploadObjects uploadObjects = new UploadObjects();
//		uploadObjects.uploadObject(s3, cloud.getContractBucket(), cloud.getPerCloudPath()+fileName, sampleImgPath);
//		
		
//		파일 삭제
//		String[] keyNames = {"이거폴더나오면정상/testfolder/","이거폴더나오면정상/chihiro014.jpg"};
//		List로 바뀜
//		DeleteObjects deleteObjects = new DeleteObjects();
//		deleteObjects.DeleteObjects(s3, cloud.getContractBucket(), cloud.getPerCloudPath(), keyNames);
		
//		InsertFolder insertFolder = new InsertFolder();
		
//		폴더 생성
//		String folderName = "testfolder";
//		insertFolder.createFolder(s3, cloud.getContractBucket(), cloud.getPerCloudPath(), folderName);
		
//		공용 스토리지 경로 생성 - 성공
//		insertFolder.createPublicFolder(s3, cloud.getContractBucket());
		
//		사원 개인 스토리지 경로 생성 - 성공
//		insertFolder.createPersonalFolder(s3, cloud.getContractBucket(), "EMP013");
		
//		버킷 생성 - 성공
//		CreateAWSBucket crAWSBucket = new CreateAWSBucket();
//		crAWSBucket.createBucket(s3, cloud.getContractBucket());
	}

	@Test
	@Disabled
	void testCreateCloud() {
		
	}

	@Test
	@Disabled
	void testDeleteCloud() {
		fail("Not yet implemented");
	}

}
