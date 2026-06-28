# 프로젝트 목적

- 이 프로젝트는 `Work2gether`라는 이름의 회사/조직용 그룹웨어 웹 애플리케이션입니다.
- 주요 업무 기능은 로그인/가입, 조직도와 디렉터리, 사원/인사 관리, 근태와 휴가 관리, 쪽지, Gmail 연동 메일, 프로젝트 관리, 일정 관리, 전자결재, 공용/개인 클라우드, 공지사항, 문의 게시판, 설문/투표, 회의실/시설 예약, 계약/초기 설정, 결제 정보 조회입니다.
- 여러 업무 화면에서 `/{companyId}/...` 형태의 URL 패턴을 사용하며, 회사별 업무 공간을 전제로 합니다.
- `provider` 영역은 그룹웨어 사용 신청, 계약 업체 관리, 최초 사용 설정 흐름에 해당합니다.
- 이 프로젝트를 단순 게시판으로 보지 말고, 인증 사용자, 회사 생명주기, 권한, 메뉴, mapper SQL, JSP/JS 연동까지 함께 확인해야 합니다.

# 프로젝트 작업 안내

## 개요

- Maven 기반 Java 웹 애플리케이션입니다.
- 패키징은 `war`이며, 최종 WAR 이름은 `work2gether`입니다.
- 주요 기술 스택은 Spring MVC 5.3.x, Spring Security 5.7.x, MyBatis, JSP, Apache Tiles 3, Oracle JDBC, HikariCP, Log4j2, Lombok입니다.
- 루트에는 Git 관련 메타데이터와 Eclipse/WTP 설정 파일(`.project`, `.classpath`, `.settings`)이 포함되어 있습니다. IDE 설정 변경은 요청이 있을 때만 최소 범위로 다루세요.
- `target` 및 `Dev/dev_finalProject/workspace/Team02Final/target` 아래에는 빌드 산출물이 있으므로, 실제 소스 수정 대상과 혼동하지 마세요.

## 주요 경로

- `pom.xml`: Maven 의존성, 플러그인, WAR 빌드 설정
- `src/main/java/kr/or/ddit`: 애플리케이션 Java 소스
- `src/main/resources/kr/or/ddit/spring`: Spring root context 설정
- `src/main/resources/kr/or/ddit/mybatis/mappers`: MyBatis mapper XML
- `src/main/resources/log4j2.xml`: Log4j2 설정
- `src/main/resources/log4jdbc.log4j2.properties`: log4jdbc 설정
- `src/main/webapp/WEB-INF/web.xml`: 웹 애플리케이션, 필터, DispatcherServlet, 오류 페이지, HTTPS 제약 설정
- `src/main/webapp/WEB-INF/springmvc/dispatcher-servlet.xml`: Spring MVC, Tiles, JSON view, WebSocket, 정적 리소스 설정
- `src/main/webapp/WEB-INF/tiles/tiles.xml`: Tiles 레이아웃 정의
- `src/main/webapp/WEB-INF/views`: JSP 화면
- `src/main/webapp/WEB-INF/includee`: 일반 사용자 레이아웃 include JSP
- `src/main/webapp/WEB-INF/provIncludee`: provider 레이아웃 include JSP
- `src/main/webapp/resources`: CSS, JS, 이미지, 라이브러리, Sneat 템플릿 리소스
- `src/test`: JUnit 5 기반 테스트 소스
- `tokens`: OAuth/token 관련 저장 파일입니다. 민감 정보로 취급하고 값을 열람하거나 출력하지 마세요.

## 실제 패키지와 화면 구조

- 주요 Java 패키지는 `account`, `approval`, `atch`, `attendance`, `aws`, `cloud`, `commons`, `contract`, `department`, `employee`, `error`, `event`, `expense`, `gmail`, `message`, `notice`, `organitree`, `payment`, `position`, `project`, `provider`, `question`, `resource`, `room`, `roomReservation`, `roomTime`, `schedule`, `scheduletype`, `security`, `statistics`, `survey`, `teamHistory`, `timeReservation`, `todolist`, `vacation`입니다.
- 기능 패키지 안에서는 `controller`, `service`, `dao`, `vo`, `dto` 계열이 많이 사용됩니다. 일부 기능은 `hrController`, `systemController`, `bucket`, `connection`, `object`처럼 도메인 특화 하위 패키지를 쓰므로 기존 구조를 먼저 확인하세요.
- MyBatis mapper 디렉터리는 Java 패키지명과 대소문자나 이름이 다른 항목이 있습니다. 예를 들어 `roomReservation`은 `roomreservation`, `roomTime`은 `roomtime`, `timeReservation`은 `timereservation`, 조직도는 `organi` mapper를 사용합니다.
- 실제 mapper 디렉터리에는 `attendanceHistory`, `expension`, `schedulecolor`, `vacationhistory`, `vacationstatus`처럼 Java 최상위 패키지와 1:1로 대응하지 않는 항목도 있습니다.
- JSP 주요 화면 디렉터리는 `approval`, `attendance`, `cloud`, `employee`, `error`, `expense`, `hrEmp`, `mail`, `message`, `notice`, `organi`, `payment`, `project`, `provider`, `question`, `room`, `schedule`, `statistics`, `survey`, `vacation`입니다.

## 패키지 구조 기반 작업 규칙

- 새 기능은 먼저 기존 도메인 패키지에 들어갈 수 있는지 판단하세요. 예를 들어 예약 시간 기능은 `roomTime` 또는 `timeReservation`, 조직/부서 기능은 `organitree`, `department`, `position`, 사원 관리 기능은 `employee`의 경계를 먼저 확인합니다.
- 기존 도메인에 맞는 기능을 별도 최상위 패키지로 분리하지 마세요. Controller, Service, DAO, VO, JSP, mapper까지 하나의 업무 영역 안에서 맞춰 추가합니다.
- Controller는 요청 처리, 인증 사용자와 `companyId` 추출, 입력 검증 결과 처리, view name 또는 JSON 응답 조립에 집중하세요. DB 접근이나 외부 API 호출은 Service로 내립니다.
- Service는 업무 규칙과 트랜잭션 경계를 담당합니다. 새 Service가 필요하면 기존 패턴처럼 `XxxService` 인터페이스와 `XxxServiceImpl` 구현체를 같은 `service` 계열 패키지에 둡니다.
- DAO는 MyBatis `@Mapper` 인터페이스로 유지하세요. mapper XML의 namespace는 DAO 인터페이스의 전체 이름과 일치시켜야 합니다.
- VO는 DB 행, 화면 form, MyBatis parameter/result model을 표현하는 기존 스타일을 따릅니다. 화면 전용 요청/응답 객체가 필요하고 기존 VO로 담기 어렵다고 판단될 때만 `dto`를 사용하세요.
- Controller 클래스가 기능별로 `Read`, `Insert`, `Modify`, `File`, `Manage` 등으로 나뉜 도메인은 같은 분리 방식을 유지하세요.
- `hrController`, `systemController`는 관리자 화면용 기존 분리입니다. HR/시스템 관리 기능을 일반 사용자 Controller에 섞지 말고 기존 관리자 패키지와 URL을 확인하세요.
- `commons`, `security`, `resource`, `atch`, `aws`, `gmail` 같은 공통 기능은 여러 도메인에서 호출됩니다. 공개 메서드 시그니처 변경은 호출 지점을 전체 검색한 뒤 최소화하세요.
- 파일 업로드나 첨부 기능은 `atch` 패키지와 각 도메인의 FileController/mapper 사용 방식을 먼저 확인하고, 도메인마다 별도 첨부 로직을 새로 만들지 마세요.
- Gmail/OAuth, AWS/S3, SMS, 메일 발송 같은 외부 서비스 연결 코드를 도메인 패키지 안에 새로 흩뿌리지 말고 기존 전용 패키지의 Service를 통해 호출하세요.
- 패키지 이동이 필요하면 Java package 선언, Spring scan 대상, mapper namespace, XML result type, JSP/JS URL, 테스트 import를 함께 수정해야 합니다.
- MyBatis mapper XML을 새로 만들 때는 `src/main/resources/kr/or/ddit/mybatis/mappers/<domain>` 아래 기존 디렉터리명을 따르고 `context-mapper.xml`의 mapper scan/위치 설정과 맞는지 확인하세요.
- 화면을 추가할 때는 Java 패키지명만 보지 말고 JSP 디렉터리명과 메뉴 링크도 함께 맞추세요. 예를 들어 `organitree` Java 패키지는 JSP와 mapper에서 `organi` 이름을 함께 사용합니다.
- 테스트를 추가할 때는 `src/test/java/kr/or/ddit/<domain>` 아래에 대응 계층과 같은 패키지 구조를 따르고, Spring context가 필요한 테스트는 기존 설정 사용 방식을 확인하세요.

## Spring MVC 규칙

- `kr.or.ddit` 하위의 `@Controller`와 `@ControllerAdvice`만 DispatcherServlet context에서 스캔됩니다. Service, DAO, mapper 설정은 root context XML도 함께 확인하세요.
- JSP view name은 Tiles 정의와 JSP resolver를 모두 고려해야 합니다. `*/*` Tiles 정의는 2-depth view name을 `/WEB-INF/views/{1}/{2}.jsp`로 연결합니다.
- provider 화면은 `provider/*/*` Tiles 정의를 사용하며 `/WEB-INF/views/provider/{1}/{2}` 패턴과 `provIncludee` 레이아웃을 따릅니다.
- 단일 JSP로 직접 이동하는 화면은 `/WEB-INF/views/` prefix와 `.jsp` suffix가 적용됩니다. 새 JSP를 추가할 때는 기존 view name 반환 방식과 Tiles 정의를 먼저 확인하세요.
- 정적 리소스는 `/resources/**`로 제공됩니다. 기능별 JS는 `src/main/webapp/resources/js/app/<domain>` 또는 기존 JS 디렉터리에, CSS는 `src/main/webapp/resources/css/<domain>`에 둡니다.
- 업로드 이미지 리소스는 `/images/**`가 로컬 `D:/multipartDir/saveDir/`에 매핑되어 있습니다. 실행 환경에 직접 영향을 주므로 요청 없이 바꾸지 마세요.
- WebSocket은 `/echo` 경로와 `kr.or.ddit.commons.websocket.EchoHandler` 설정을 사용합니다. WebSocket 변경 시 `dispatcher-servlet.xml`과 관련 JS 호출 경로를 함께 확인하세요.
- `web.xml`에는 `CharacterEncodingFilter`, `FormContentFilter`, `HiddenHttpMethodFilter`, `MultipartFilter`, `springSecurityFilterChain`이 등록되어 있습니다. 요청 메서드, multipart, 보안 관련 변경은 필터 순서의 영향을 확인하세요.
- `web.xml`의 HTTPS `CONFIDENTIAL` 제약은 요청 전송 보안 설정입니다. 로컬 실행 문제를 해결한다는 이유만으로 임의 제거하지 마세요.

## MyBatis와 데이터 접근 규칙

- DAO 인터페이스, mapper XML namespace, statement id, VO/DTO 필드명을 함께 검증하세요.
- mapper XML은 기능별 하위 디렉터리에 있으며 이름 규칙이 완전히 통일되어 있지 않습니다. SQL을 수정하기 전에 호출 DAO 메서드와 테스트를 검색하세요.
- Oracle SQL 문법을 기준으로 작성하세요. 날짜, 페이징, `MERGE`, `ROWNUM` 같은 DB 종속 문법은 기존 mapper 스타일을 따릅니다.
- 동적 SQL을 추가할 때는 null/empty 조건과 화면 검색 조건 이름이 일치하는지 확인하세요.
- DB 접속 정보나 계정 정보는 문서, 로그, 응답에 노출하지 마세요.

## 보안과 민감 정보

- 다음 파일과 경로는 민감 정보로 취급하고 값을 출력하거나 문서에 복사하지 마세요: `src/main/resources/kr/or/ddit/db/DBInfo.properties`, `src/main/webapp/WEB-INF/oauth/credentials.json`, `tokens`, OAuth credential/token 관련 파일.
- AWS/S3, Gmail/OAuth, SMS, 메일, DB, Spring Security 설정을 수정할 때는 호출 지점과 설정 파일을 먼저 검색하고 새 토큰/비밀번호 값을 추가하지 마세요.
- 민감값이 필요한 예시는 실제 값 대신 `<REDACTED>` 또는 환경변수 이름만 사용하세요.
- 로그 추가 시 개인정보, 인증 토큰, 비밀번호, DB 접속 문자열, 메일 본문, 첨부 파일 전체 경로를 남기지 마세요.

## JSP와 프론트엔드 작업 규칙

- JSP는 `WEB-INF/views` 아래에 두고, 공통 레이아웃은 `includee` 또는 `provIncludee`를 사용하세요.
- 기존 JSP가 JSTL, Spring taglib, Spring Security taglib를 사용하는지 먼저 확인하고 같은 방식으로 확장하세요.
- AJAX URL, controller mapping, JSON 응답 구조, JS 파일 위치를 함께 맞추세요.
- 화면별 JS/CSS가 이미 분리되어 있으면 inline script/style보다 기존 파일을 우선 사용하세요.
- Sneat 템플릿과 외부 라이브러리 리소스는 `resources/sneat-1.0.0`, `resources/lib` 아래에 있습니다. 라이브러리 파일을 직접 수정하기보다 기능별 JS/CSS에서 오버라이드하세요.

## 빌드와 검증

- Maven 명령은 `pom.xml`이 있는 프로젝트 루트 `D:\final_project\dditFinal\Team02Final`에서 실행하세요.
- 다른 디렉터리에서 실행해야 한다면 Maven의 `-f` 옵션으로 `pom.xml` 경로를 지정하세요. 예: `mvn -f "D:\final_project\dditFinal\Team02Final\pom.xml" test`
- 전체 빌드: `mvn clean package`
- 테스트 실행: `mvn test`
- 빠른 컴파일 확인이 필요하면 `mvn -DskipTests package`를 사용할 수 있습니다.
- Maven 의존성이 로컬 캐시에 없으면 다운로드를 위해 네트워크 접근이 필요합니다.
- 테스트는 Spring root context(`src/main/resources/kr/or/ddit/spring/context-*.xml`)를 로드하며, 현재 설정은 DB 설정 파일과 연결됩니다. S3 또는 로컬 파일에 의존하는 테스트도 있으므로 실패하면 원인과 환경 의존 여부를 기록하세요.
- 로컬 WAS 실행은 생성된 WAR 또는 Eclipse/WTP 설정을 사용합니다.

## Codex 작업 원칙

- 변경과 관련된 Controller, Service, DAO, mapper XML, VO/DTO, JSP, JS/CSS를 함께 확인하세요.
- 새 기능은 기존 도메인 패키지 구조와 명명 규칙을 따르고, 불필요한 공통화나 대규모 리팩터링을 피하세요.
- 공통 유틸, 보안, 파일 업로드, 메일, AWS/S3, OAuth, WebSocket, DB 설정은 영향 범위가 넓으므로 변경 전 호출 지점을 검색하세요.
- 기존 파일에는 깨진 한글 주석이나 인코딩 문제가 일부 있을 수 있습니다. 기능 수정과 무관한 인코딩 변환 또는 전체 포맷 변경은 하지 마세요.
- Lombok을 사용합니다. getter/setter를 직접 추가하기 전에 기존 Lombok 애너테이션과 IDE/빌드 지원을 고려하세요.
- Maven, XML, JSP, Java 파일을 수정할 때는 기존 들여쓰기와 스타일을 최대한 유지하세요.
- 변경 후 가능한 최소 검증으로 `mvn test` 또는 `mvn clean package`를 실행하세요. 실행하지 못했거나 실패했다면 이유와 실패 지점을 기록하세요.
