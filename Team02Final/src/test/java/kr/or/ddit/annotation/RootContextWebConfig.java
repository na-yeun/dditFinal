package kr.or.ddit.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

@Retention(RUNTIME)
@Target(TYPE)
@SpringJUnitWebConfig(locations = "file:src/main/resources/kr/or/ddit/spring/context-*.xml")
public @interface RootContextWebConfig {

}
