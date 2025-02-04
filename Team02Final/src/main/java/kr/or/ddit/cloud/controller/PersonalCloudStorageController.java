package kr.or.ddit.cloud.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.aws.connection.GetAWSUser;
import kr.or.ddit.aws.dto.MoveObjectDTO;
import kr.or.ddit.aws.dto.ThumbnailDTO;
import kr.or.ddit.aws.object.CopyObjects;
import kr.or.ddit.aws.object.DeleteObjects;
import kr.or.ddit.aws.object.DownloadObjects;
import kr.or.ddit.aws.object.GetObjectImageUrl;
import kr.or.ddit.aws.object.InsertFolder;
import kr.or.ddit.aws.object.SearchObjects;
import kr.or.ddit.aws.object.UploadObjects;
import kr.or.ddit.cloud.service.CloudStorageService;
import kr.or.ddit.cloud.vo.CloudVO;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.security.AccountVOWrapper;

@Controller
@RequestMapping("/{companyId}/perCloud")
public class PersonalCloudStorageController {
	private GetAWSUser awsUser;
	private UploadObjects uploadObject;
	private DownloadObjects downloadObjects;
	private InsertFolder insertFolder;
	private DeleteObjects deleteObjects;
	private GetObjectImageUrl getObjcetImageUrl;
	private SearchObjects searchObjects;
	private CopyObjects copyObjects;
	
	@PostConstruct
	public void init() {
		this.awsUser = new GetAWSUser();
		this.uploadObject = new UploadObjects();
		this.downloadObjects = new DownloadObjects();
		this.insertFolder = new InsertFolder();
		this.deleteObjects = new DeleteObjects();
		this.getObjcetImageUrl = new GetObjectImageUrl();
		this.searchObjects = new SearchObjects();
		this.copyObjects = new CopyObjects();
	}
	
	
	@Autowired
	private CloudStorageService service;
	
	
	@GetMapping
	public String getPersonalCloudStorage(
		Authentication authentication,
		Model model
		) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO emp = (EmployeeVO) account;
		String empId = emp.getEmpId();
		CloudVO cloud = service.readCloudInfo(empId);
		
		AmazonS3 s3 = awsUser.getAWSUser(cloud.getProvAccesskey(), cloud.getProvSecretkey());
		
		String prefix = ""; // AWS에 접속할 경로
		
		prefix = cloud.getPerCloudPath();
		if (!prefix.endsWith("/")) {
			prefix += "/";
		}

	    // S3 요청: prefix와 delimiter를 사용
	    ListObjectsV2Request request = new ListObjectsV2Request()
	            .withBucketName(cloud.getContractBucket())
	            .withPrefix(prefix)  // 현재 경로
	            .withDelimiter("/"); // 하위 폴더 구분

	    ListObjectsV2Result result = s3.listObjectsV2(request);

	    // 파일과 폴더 분리
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    List<Map<String, String>> files = fileReadMethod(prefix, result, sdf);
	    List<Map<String, String>> folders = folderReadMethod(prefix, result);

	    // Model에 데이터 추가
	    model.addAttribute("empId", empId);
	    model.addAttribute("cloud", cloud);
	    model.addAttribute("folders", folders);
	    model.addAttribute("files", files);
	    
		return "cloud/personalStorage";
	}
	
	@PostMapping
	@ResponseBody
	public Map<String, Object> getPersonalCloudStorageJson(
	    @RequestParam(value = "path", required = false) String path,
	    Authentication authentication) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO emp = (EmployeeVO) account;
	    String empId = emp.getEmpId();
	    CloudVO cloud = service.readCloudInfo(empId);

	    AmazonS3 s3 = awsUser.getAWSUser(cloud.getProvAccesskey(), cloud.getProvSecretkey());

	    // 현재 경로(prefix) 설정
	    String prefix =cloud.getPerCloudPath()+path;
	    
	    if (!prefix.endsWith("/")) {
	        prefix += "/";
	    }

	    // S3 요청: prefix와 delimiter를 사용
	    ListObjectsV2Request request = new ListObjectsV2Request()
	            .withBucketName(cloud.getContractBucket())
	            .withPrefix(prefix)
	            .withDelimiter("/");
	    
	    ListObjectsV2Result result = s3.listObjectsV2(request);
	    
	    // 날짜 format 형식
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	    // 파일과 폴더 분리
	    List<Map<String, String>> folders = folderReadMethod(prefix, result);
	    List<Map<String, String>> files = fileReadMethod(prefix, result, sdf);

	    // JSON 데이터 반환
	    Map<String, Object> response = new HashMap<>();
	    response.put("folders", folders);
	    response.put("files", files);
	    
	    return response;
	}
	
	@PostMapping("/moveObjects")
	public ResponseEntity<String> moveObjects(
		@RequestBody MoveObjectDTO moveObjectDTO,
		Authentication authentication
		){
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO emp = (EmployeeVO) account;
	    String empId = emp.getEmpId();
	    CloudVO cloud = service.readCloudInfo(empId);
	    AmazonS3 s3 = awsUser.getAWSUser(cloud.getProvAccesskey(), cloud.getProvSecretkey());
	    
	    copyObjects.copyObjects(s3, cloud.getContractBucket(), cloud.getPerCloudPath(), moveObjectDTO.getMoveSelected(), moveObjectDTO.getTargetPaths());
	    return ResponseEntity.ok("Success!");
	}
	
	
	@PostMapping("/searchObjects")
	@ResponseBody
	public Map<String, Object> searchObjects(
		@RequestParam String searchKeyWord,
		Authentication authentication
		){
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO emp = (EmployeeVO) account;
	    String empId = emp.getEmpId();
	    CloudVO cloud = service.readCloudInfo(empId);

	    AmazonS3 s3 = awsUser.getAWSUser(cloud.getProvAccesskey(), cloud.getProvSecretkey());
	    
	    return searchObjects.searchObjects(s3, cloud.getContractBucket(), cloud.getPerCloudPath(), searchKeyWord);
	}
	
	@PostMapping("/imageThumbnail")
	public ResponseEntity<ThumbnailDTO> thumbnail(
		@RequestParam String filePath,
		Authentication authentication
		) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO emp = (EmployeeVO) account;
		String empId = emp.getEmpId();
		CloudVO cloud = service.readCloudInfo(empId);
		
		AmazonS3 s3 = awsUser.getAWSUser(cloud.getProvAccesskey(), cloud.getProvSecretkey());
		
		try {
			String thumbnailPath = cloud.getPerCloudPath()+filePath;
			S3Object s3Object = s3.getObject(cloud.getContractBucket(), thumbnailPath);
			String imgUrl = getObjcetImageUrl.getObjcetImageUrl(s3, cloud.getContractBucket(), thumbnailPath);
			
	        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
	        String lastModified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(s3Object.getObjectMetadata().getLastModified());
	        long size = s3Object.getObjectMetadata().getContentLength();
	        
	        String formatSize = formatFileSize(size);
	        
	        ThumbnailDTO thumbnailDTO = new ThumbnailDTO(imgUrl, fileName, formatSize, lastModified, filePath);
	        
	        return ResponseEntity.ok(thumbnailDTO);
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}
	
	
	@PostMapping("/deleteObjects")
	public ResponseEntity<?> deleteObjects(
			@RequestBody List<String> selectedObjects
		, Authentication authentication
		){
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO emp = (EmployeeVO) account;
		String empId = emp.getEmpId();
		CloudVO cloud = service.readCloudInfo(empId);
		
		AmazonS3 s3 = awsUser.getAWSUser(cloud.getProvAccesskey(), cloud.getProvSecretkey());
		
		deleteObjects.DeleteObjects(s3, cloud.getContractBucket(), cloud.getPerCloudPath(), selectedObjects);
		
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/makeDir")
	public ResponseEntity<?> makeDir(
		@RequestParam("dirName") String dirName
		, Authentication authentication
		){
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO emp = (EmployeeVO) account;
		String empId = emp.getEmpId();
		CloudVO cloud = service.readCloudInfo(empId);
		
		AmazonS3 s3 = awsUser.getAWSUser(cloud.getProvAccesskey(), cloud.getProvSecretkey());
		
		insertFolder.createFolder(s3, cloud.getContractBucket(), cloud.getPerCloudPath(), dirName);
		
		return ResponseEntity.ok().build();
	}
	
	
	@PostMapping("/upload")
	public ResponseEntity<?> uploadFiles(
			@RequestParam("files") MultipartFile[] files
			,@RequestParam("folderName") String folderName
			, Authentication authentication){
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO emp = (EmployeeVO) account;
		String empId = emp.getEmpId();
		CloudVO cloud = service.readCloudInfo(empId);
		
		AmazonS3 s3 = awsUser.getAWSUser(cloud.getProvAccesskey(), cloud.getProvSecretkey());
		
		System.out.println("folderName =======================> "+folderName);
		uploadObject.insertPerCloudObject(s3, cloud.getContractBucket(), cloud.getPerCloudPath(), folderName, files);
		
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/downloadSingle")
	public ResponseEntity<InputStreamResource> downloadSingleFile(
		@RequestBody Map<String, String> requestBody,
		HttpServletResponse resp,
		Authentication authentication	
		){
		String filePath = requestBody.get("file");
		
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO emp = (EmployeeVO) account;
	    String empId = emp.getEmpId();
	    CloudVO cloud = service.readCloudInfo(empId);
		
	    AmazonS3 s3 = awsUser.getAWSUser(cloud.getProvAccesskey(), cloud.getProvSecretkey());
	  

	    try {
	        // S3에서 파일 가져오기
	        S3Object s3Object = s3.getObject(cloud.getContractBucket(), filePath);
	        InputStream inputStream = s3Object.getObjectContent();

	        // 파일 이름 추출
	        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

	        // 클라이언트로 파일 반환
	        InputStreamResource resource = new InputStreamResource(inputStream);

	        return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .body(resource);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.internalServerError().build();
	    }
	}
	
	@PostMapping("/downloadMultiple")
	public ResponseEntity<InputStreamResource> downloadMultipleFiles(
		@RequestBody Map<String, List<String>> requestBody,
		HttpServletResponse resp,
		Authentication authentication
		) {
		List<String> files = requestBody.get("files");
		
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO emp = (EmployeeVO) account;
	    String empId = emp.getEmpId();
	    CloudVO cloud = service.readCloudInfo(empId);

	    AmazonS3 s3 = awsUser.getAWSUser(cloud.getProvAccesskey(), cloud.getProvSecretkey());
	    
	    try {
            // ZIP 파일 생성
            File zipFile = downloadObjects.createZipFromS3(s3, files, cloud.getContractBucket());

            // ZIP 파일을 클라이언트로 반환
            InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=files.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException e) {
            // 에러 발생 시 처리
            return ResponseEntity.internalServerError()
                    .body(null);
        }
	}
	
	// 파일 처리 (ObjectSummaries 사용)
	private List<Map<String, String>> fileReadMethod(String prefix, ListObjectsV2Result result, SimpleDateFormat sdf){
		List<Map<String, String>> files = new ArrayList<Map<String,String>>();
		for (S3ObjectSummary obj : result.getObjectSummaries()) {
	        String key = obj.getKey();
	        if (!key.equals(prefix)) { // 현재 폴더 자체는 제외
	            String fileName = key.substring(prefix.length());
	            Map<String, String> fileData = new HashMap<>();
	            fileData.put("name", fileName);
	            fileData.put("type", "File");
	            fileData.put("size", formatFileSize(obj.getSize()));
	            fileData.put("lastModified", sdf.format(obj.getLastModified()));
	            files.add(fileData);
	        }
	    }
		
		return files;
	}
	
	// 폴더 리스트 만들어 주는 메소드
	private List<Map<String, String>> folderReadMethod(String prefix, ListObjectsV2Result result){
		List<Map<String, String>> folders = new ArrayList<Map<String,String>>();
		
		for (String commonPrefix : result.getCommonPrefixes()) {
	        String folderName = commonPrefix.substring(prefix.length(), commonPrefix.length() - 1);
	        Map<String, String> folderData = new HashMap<>();
	        folderData.put("name", folderName);
	        folderData.put("type", "Folder");
	        folderData.put("size", "-");
	        folderData.put("lastModified", "-");
	        folders.add(folderData);
	    }
		
		return folders;
	}
	
	// 파일 크기를 사람이 읽기 쉬운 형식으로 변환
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
