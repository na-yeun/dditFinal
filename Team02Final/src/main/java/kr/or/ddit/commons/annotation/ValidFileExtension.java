package kr.or.ddit.commons.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = IMGFileExtensionValidator.class)  // 유효성 검사기 클래스 지정
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFileExtension {
    String message() default "Invalid file extension";  // 기본 오류 메시지
    Class<?>[] groups() default {};  // 그룹
    Class<? extends Payload>[] payload() default {};  // 추가 데이터
    String[] allowedExtensions();  // 허용된 확장자 목록
}