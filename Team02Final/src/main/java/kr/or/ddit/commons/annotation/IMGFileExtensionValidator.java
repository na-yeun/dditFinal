package kr.or.ddit.commons.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class IMGFileExtensionValidator implements ConstraintValidator<ValidFileExtension, MultipartFile> {

    private String[] allowedExtensions;

    @Override
    public void initialize(ValidFileExtension constraintAnnotation) {
        allowedExtensions = constraintAnnotation.allowedExtensions();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;  // 파일이 없으면 검사를 생략
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return false;
        }

        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        for (String extension : allowedExtensions) {
            if (fileExtension.equals(extension.toLowerCase())) {
                return true;  // 확장자가 허용된 것과 일치하면 유효
            }
        }
        return false;  // 허용된 확장자와 일치하지 않으면 유효하지 않음
    }
}