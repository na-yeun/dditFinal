package kr.or.ddit.aws.object;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class DownloadObjects {
	 // S3 객체로부터 ZIP 파일 생성
    public File createZipFromS3(AmazonS3 s3, List<String> fileKeys, String bucketName) throws IOException {
        // 임시 ZIP 파일 생성
        File zipFile = File.createTempFile("files", ".zip");

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (String key : fileKeys) {
                try {
                    // S3에서 객체 가져오기
                    S3Object s3Object = s3.getObject(new GetObjectRequest(bucketName, key));
                    InputStream inputStream = s3Object.getObjectContent();

                    // ZIP Entry 추가
                    ZipEntry zipEntry = new ZipEntry(key.substring(key.lastIndexOf("/") + 1));
                    zos.putNextEntry(zipEntry);

                    // 데이터를 ZIP에 쓰기
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }

                    inputStream.close();
                    zos.closeEntry();
                } catch (Exception e) {
                	e.printStackTrace();
                }
            }
        }
        return zipFile;
    }
}
