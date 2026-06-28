# Repository Guidelines

## Project Structure & Module Organization

This is a Maven-based Spring MVC web application packaged as a WAR (`work2gether`). Java source lives under `src/main/java/kr/or/ddit`, organized by feature packages such as `account`, `approval`, `gmail`, `project`, `room`, and `survey`. MyBatis mapper XML files and Spring context files are under `src/main/resources/kr/or/ddit`, especially `mybatis/mappers` and `spring`. JSP views are in `src/main/webapp/WEB-INF/views`, shared layout/configuration is in `WEB-INF`, and browser assets are in `src/main/webapp/resources` (`css`, `js`, `images`, `lib`, `sneat-1.0.0`). Tests are under `src/test/java/kr/or/ddit`.

## Build, Test, and Development Commands

- `mvn clean package`: compiles the project and builds `target/work2gether.war`.
- `mvn test`: runs the JUnit test suite in `src/test/java`.
- `mvn -Dtest=SurveyServiceImplTest test`: runs one test class while iterating on a feature.

Deploy the generated WAR to a Servlet 3.1 compatible container such as Tomcat. Keep environment-specific database, OAuth, SMS, and cloud credentials out of commits.

## Coding Style & Naming Conventions

Follow the existing Java package pattern: `controller`, `service`, `serviceImpl`, `dao`, `vo`, and `dto` within each feature area. Use PascalCase for Java classes (`ProjectServiceImpl`), camelCase for methods and variables, and `*Mapper.java` plus matching MyBatis `*Mapper.xml` names. Existing files use tab-based indentation in XML and Java; keep formatting consistent with neighboring code. For JSP, CSS, and JavaScript, place feature-specific files in matching folders such as `views/project` and `resources/js/app/project`.

## Testing Guidelines

Tests use JUnit 5 with Spring test support. Name test classes after the unit or mapper under test, ending in `Test` (for example, `AccountMapperTest` or `RoomServiceImplTest`). Add tests near the corresponding package under `src/test/java/kr/or/ddit`. Prefer focused service and mapper tests for changed business logic and SQL behavior, then run `mvn test` before submitting.

## Commit & Pull Request Guidelines

This checkout contains SVN metadata (`.svn`) and no Git history was available locally, so no repository-specific Git commit convention could be confirmed. Use short, imperative change summaries such as `Add vacation approval validation` and group related changes only. Pull requests should describe the user-facing behavior, list affected modules, mention database/configuration changes, link related issues, and include screenshots for JSP/UI changes.

## Security & Configuration Tips

Treat files under `src/main/resources/kr/or/ddit/db`, `auth`, and OAuth-related `WEB-INF` paths as sensitive configuration areas. Do not hard-code credentials, tokens, bucket names, or personal test accounts in Java, JSP, JavaScript, mapper XML, or committed properties files.
