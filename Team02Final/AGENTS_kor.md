# 프로젝트 작업 안내

## 개요

- Maven 기반 Java 웹 애플리케이션입니다.
- 패키징은 `war`이며, 최종 산출물 이름은 `work2gether`입니다.
- 주요 기술 스택은 Spring MVC 5.3.x, Spring Security, MyBatis, JSP, Tiles, Oracle JDBC, HikariCP, Log4j2, Lombok입니다.
- Git 저장소가 아니며 루트에 `.svn` 디렉터리가 있습니다. 변경 이력 확인은 Git 명령에 의존하지 마세요.

## 주요 경로

- `pom.xml`: Maven 의존성, 플러그인, WAR 빌드 설정
- `src/main/java/kr/or/ddit`: 애플리케이션 Java 소스
- `src/main/resources/kr/or/ddit/spring`: Spring root context 설정
- `src/main/resources/kr/or/ddit/mybatis/mappers`: MyBatis mapper XML
- `src/main/webapp/WEB-INF/web.xml`: 웹 애플리케이션, 필터, DispatcherServlet 설정
- `src/main/webapp/WEB-INF/springmvc/dispatcher-servlet.xml`: Spring MVC, Tiles, 정적 리소스, WebSocket 설정
- `src/main/webapp/WEB-INF/views`: JSP 화면
- `src/main/webapp/WEB-INF/tiles/tiles.xml`: Tiles 레이아웃 정의
- `src/main/webapp/resources`: CSS, JS, 이미지, 외부 프론트엔드 리소스
- `src/test`: 테스트 소스 및 테스트 리소스

## 패키지 구조 관례

기능별 패키지 아래에 보통 다음 계층이 나뉩니다.

- `controller`: Spring MVC 컨트롤러
- `service`: 비즈니스 로직
- `dao`: 데이터 접근 계층, MyBatis mapper 연동
- `vo` / `dto`: 요청, 응답, 도메인 데이터 객체

주요 기능 패키지는 `employee`, `attendance`, `approval`, `project`, `notice`, `survey`, `schedule`, `room`, `roomReservation`, `cloud`, `gmail`, `message`, `provider`, `vacation` 등입니다.

## 빌드와 검증

- 전체 빌드: `mvn clean package`
- 테스트 실행: `mvn test`
- 의존성 다운로드가 필요한 경우 네트워크 접근이 필요할 수 있습니다.
- 로컬 WAS에서 실행할 때는 생성된 WAR 또는 IDE의 WTP 설정을 사용합니다.

## 개발 시 주의사항

- 기존 구조와 명명 규칙을 우선 따르세요. 새 기능은 같은 도메인 패키지 안에서 `controller` / `service` / `dao` / `vo` 구조에 맞춰 추가하는 편이 안전합니다.
- JSP 화면은 `WEB-INF/views` 아래에 두고, Tiles 레이아웃과 기존 include 파일을 먼저 확인하세요.
- 정적 리소스는 `/resources/**` 매핑을 통해 제공됩니다.
- 업로드 이미지 리소스 매핑에 로컬 절대 경로 `D:/multipartDir/saveDir/`가 설정되어 있습니다. 실행 환경이 바뀌면 별도 설정이 필요할 수 있습니다.
- `src/main/resources/kr/or/ddit/db/DBInfo.properties`, OAuth credential, token 관련 파일에는 민감 정보가 있을 수 있으므로 값 노출이나 불필요한 변경을 피하세요.
- 일부 XML/주석에 문자 인코딩이 깨져 보이는 내용이 있습니다. 인코딩을 일괄 변환하거나 포맷팅하는 변경은 기능 수정과 분리해서 다루세요.
- Lombok을 사용하므로 IDE나 빌드 환경에 Lombok 지원이 필요합니다.

## 작업 원칙

- 관련 컨트롤러, 서비스, DAO, mapper XML, JSP를 함께 확인한 뒤 변경하세요.
- SQL 변경 시 MyBatis mapper의 namespace, DAO 메서드명, VO 필드명을 같이 검증하세요.
- 공통 유틸, 보안, 파일 업로드, 메일, AWS/S3 관련 코드는 영향 범위가 넓으므로 변경 전 호출 지점을 검색하세요.
- 변경 후 가능한 최소 검증으로 `mvn test` 또는 `mvn clean package`를 실행하고, 실패 시 실패 원인을 기록하세요.
