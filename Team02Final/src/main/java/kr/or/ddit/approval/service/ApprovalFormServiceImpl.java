package kr.or.ddit.approval.service;


import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

import kr.or.ddit.approval.dao.*;
import kr.or.ddit.approval.dto.ApprovalDocumentDTO;
import kr.or.ddit.approval.vo.*;
import kr.or.ddit.atch.service.AtchFileService;
import kr.or.ddit.employee.service.EmployeeService;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.expense.dao.ExpenseMapper;
import kr.or.ddit.expense.service.ExpenseService;
import kr.or.ddit.vacation.dao.VacationStatusMapper;
import kr.or.ddit.vacation.service.VacationHistoryService;
import kr.or.ddit.vacation.vo.VacationStatusVO;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Transactional
@Service
public class ApprovalFormServiceImpl implements ApprovalFormService {

    private static final String VAC_TYPE_ANNUAL = "V001";
    private static final String VAC_TYPE_HALF = "V002";


    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ApprovalFormMapper approvalFormMapper;

    @Autowired
    private ApprovalDocumentMapper approvalDocumentMapper;

    @Autowired
    private ApprovalReferenceMapper approvalReferenceMapper;

    @Autowired
    private ApprovalMapper approvalMapper;

    @Autowired
    private VacationHistoryService vacationHistoryService;

    @Autowired
    private VacationStatusMapper vacationStatusMapper;

    @Autowired
    private ExpenseMapper expenseMapper;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private AtchFileService atchFileService;


    //첨부파일 처리
    @Value("#{dirInfo.saveDir}")
    private Resource saveFolderRes;
    private File saveFolder;

    @PostConstruct
    public void init() throws IOException {
        this.saveFolder = saveFolderRes.getFile();
    }

    @Override
    public List<ApprovalFormVO> getApprovalFormTitles() {
        return approvalFormMapper.selectFormTitles();
    }

    @Override
    public String getFormContent(String apprformId) {
        return approvalFormMapper.selectFormContent(apprformId);
    }

    //결재선 문서에 추가하는 로직임
    @Override
    public String generateApprovalLineHtml(String documentHtml,
                                           List<Map<String, Object>> approvers) {
        try {
            // 새로운 결재선 HTML 생성
            StringBuilder appendApproversHtml = new StringBuilder("<ul id=\"approver-list\">");

            for (Map<String, Object> approver : approvers) {
                log.info("현재 결재자 키값들: {}", approver.keySet());
                String name = (String) approver.get("NAME");
                String position = (String) approver.get("POSITION");

                if (name == null || position == null) {
                    log.error("결재자 정보 누락: {}", approver);
                    throw new IllegalArgumentException("결재자 정보가 올바르지 않습니다.");
                }

                appendApproversHtml.append("<li class=\"approver-box\" data-emp-id=\"")
                        .append(approver.get("EMPID"))    // ID 추가
                        .append("\">")
                        .append("<div class=\"stamp-box\"></div>")
                        .append("<div class=\"approver-info\">")
                        .append(position).append(" ")
                        .append(name)
                        .append("</div>")
                        .append("</li>");
            }
            appendApproversHtml.append("</ul>");

            // 정규식을 사용하여 기존 결재선 영역을 찾아서 교체
            String pattern = "<ul id=\"approver-list\">.*?</ul>";
            String result = documentHtml.replaceAll(
                    pattern,
                    Matcher.quoteReplacement(appendApproversHtml.toString())
            );

            // 교체 실패 확인을 위한 로그
            if (result.equals(documentHtml)) {
                log.warn("결재선 교체가 실행되지 않았습니다.");
                log.debug("문서 내용: {}", documentHtml);
                log.debug("새로운 결재선: {}", appendApproversHtml.toString());
            } else {
                log.info("결재선 교체 성공");
            }

            return result;

        } catch (Exception e) {
            log.error("결재선 HTML 생성 중 오류 발생", e);
            throw new RuntimeException("결재선 HTML 생성 실패", e);
        }
    }

    // 문서 완전 등록
    @Override
    public String processApprovalDraft(ApprovalDocumentDTO dto, List<MultipartFile> attachments, EmployeeVO myEmp) {
        try {
//            List<Map<String,Object>> approvers = dto.getApprovers();
//            for (Map<String, Object> approver : approvers) {
//                log.info("결재자 : {}", approver);
//            }
//
//            List<Map<String, Object>> references = dto.getReferences();
//            for (Map<String, Object> reference : references) {
//                log.info("참조자 : {}", reference);
//            }
//
//            Map<String, Object> formData = dto.getFormData();
////            log.info("양식 데이터: {}", formData);

//            log.info("첨부파일: {}", attachments);
//            for (MultipartFile attachment : attachments) {
//                log.info("첨부파일 상세 : {}", attachment);
//            }

            //객체생성
            ApprovalDocumentVO document = new ApprovalDocumentVO();

            //첨부파일 처리
            if (attachments != null && !attachments.isEmpty()) {
                document.setUploadFiles(attachments.toArray(new MultipartFile[0]));
                log.info("첨부파일 처리 완료");
            }

            //첨부파일 그룹 ID생성
            Integer atchFileId = Optional.ofNullable(document.getAtchFile())
                    .filter(af -> !CollectionUtils.isEmpty(af.getFileDetails()))
                    .map(af -> {
                        atchFileService.createAtchFile(af, saveFolder);
                        log.info("첨부파일 그룹 ID 생성 완료: {}", af.getAtchFileId());
                        return af.getAtchFileId();
                    }).orElse(null);

            //1. 문서번호 생성하기
            String formId = dto.getFormId();
            String documentId = generateDocumentId(formId);
            log.info("생성된 문서번호 : {}", documentId);

            String base64SignImage = processSignImage(myEmp.getEmpSignimg());
            log.info("도장 이미지 처리 완료");

            //3. 문서 HTML 가공(문서번호, 상태 추가)
            String documentHtml = dto.getDocumentHtml();
            documentHtml = documentHtml
                    .replace("<span data-field=\"docId\"></span>",
                            "<span data-field=\"docId\">" + documentId + "</span>")
                    .replace("<span data-field=\"docStatus\"></span>",
                            "<span data-field=\"docStatus\">결재 대기 중</span>")
                    .replace("<span data-field=\"empSignimg\"></span>",
                            "<img src=\"data:image/png;base64," +
                                    (base64SignImage != null ? base64SignImage : myEmp.getEmpName()) +
                                    "\" class=\"sign-image\" style=\"display: inline-block; width: 50px; height: 50px; vertical-align: middle; margin-left: 10px;\" alt=\"서명\" />");

            log.info("HTML 문서 가공 완료");

            //4. db에 document테이블 insert작업

            try {
                //1) 문서번호는 위에서만듦
                document.setDocId(documentId);
                //2) 문서제목은 스크립트 dto에서 넘어온거에서 뽑아서 넣음
                document.setDocTitle((String) dto.getFormData().get("formTitle"));
                //3) 문서 보존 기한은 현재날짜에서 3년뒤까지
                document.setDocPreserve(preserveYearThird());
                //4) 결재상태는 이 메소드에서는 무조건 2번(결재 대기 중)으로 시작
                document.setDocStatus("2");
                //5) 전결허용 여부
                document.setAllowDelegation(dto.getAllowDelegation());
                //6) 기안자(이 메소드를 사용하게된 주체)
                document.setEmpId(myEmp.getEmpId());
                //7) 결재선
                document.setApprlineId(dto.getApprlineId());
                //8) 결재양식번호(ex- 휴가신청서의경우 v01)
                document.setApprformId(formId);
                //9) 결재내용 (위의 문서 다집어넣기)
                document.setDocContent(documentHtml);
                //10) 첨부파일 (nullable)
                //첨부파일 그룹번호 생성하고 VO에 넣어주기
                document.setAtchFileId(atchFileId);
                //11) 문서 생성일자 12월 25일추가

                log.info("문서 객체 세팅 완료: {}", document);

            } catch (Exception e) {
                log.error("문서 객체 세팅중 오류 발생 : ", e);
                throw new RuntimeException("문서 객체 세팅 실패", e);
            }

            try {
                int result = approvalDocumentMapper.insertApprovalDocument(document);
                if (result > 0) {
                    log.info("문서 저장성공 : {}", documentId);

                    List<Map<String, Object>> references = dto.getReferences();
                    if (references != null && !references.isEmpty()) {
                        log.info("참조자 지정시작 ----------------------------------------------------");
                        generateReferenceProcess(documentId, dto);
                        log.info("참조자 지정 성공 ---------------------------------------------------");
                    } else {
                        log.info("참조자가 지정되지 않았습니다.");
                    }

                    log.info("결재 로직 시작");
                    //결재자 결재문서에서 지정한대로 배치하기
                    generateApprovalProcess(documentId, dto);
                    log.info("결재 배치 끝");

                    // 문서 유형별 후처리 분기
                    String docType = documentId.substring(0, 1);  // 문서번호의 첫 글자 추출
                    switch (docType) {
                        case "V":
                            log.info("휴가 신청서 후처리 시작");
                            vacationHistoryService.preProcessVacationDocument(documentId, dto, myEmp.getEmpId());
                            log.info("휴가 신청서 후처리 완료");
                            break;
                        case "E":
                            log.info("지출 결의서 후처리 시작");
                            expenseService.preProcessExpenseDocument(documentId, dto, myEmp);
                            log.info("지출 결의서 후처리 완료");
                            break;
                        case "F":
                            // 자유양식서는 별도 처리 없이 로그만 남기고 진행
                            log.info("자유 양식서 - 추가 처리 없음");
                            break;
                        default:
                            log.warn("알 수 없는 문서 유형: {}", docType);
                    }
                    return documentId;
                } else {
                    throw new RuntimeException("문서 저장 실패");
                }
            } catch (Exception e) {
                log.error("문서 저장 중 오류 발생 : ", e);
                throw new RuntimeException("문서 저장 실패", e);
            }
        } catch (Exception e) {
            log.error("문서 처리 중 오류 발생: ", e);
            throw new RuntimeException("문서 처리 실패", e);
        }
    }

    // 첫 미리보기 문서 갖고오기(휴가신청서, 지출결의서, 자유양식서 분기갈림)
    @Override
    public String processPreviewFormRenderer(String apprformId,
                                             EmployeeVO myEmp,
                                             Map<String, Object> requestData) {
        try {
            Map<String, Object> name =
                    employeeService.readEmployeePosiNameAndDeptNameByEmpId(myEmp.getEmpId());
            String posiName = (String) name.get("POSI_NAME");
            String departName = (String) name.get("DEPART_NAME");
            //사원정보로 사원명갖고오기
            String empName = myEmp.getEmpName();

            //템플릿 컨텐츠 갖고오기 (분기갈림 v01 e01 f01..등등)
            String templateContent = getFormContent(apprformId);

            Map<String, String> approvalInfoMap = new HashMap<>();
            approvalInfoMap.put("empId", myEmp.getEmpId());
            approvalInfoMap.put("empName", empName);
            approvalInfoMap.put("posiName", posiName);
            approvalInfoMap.put("departName", departName);
            approvalInfoMap.put("templateContent", templateContent);

            char formType = apprformId.charAt(0);
            switch (formType) {
                case 'v': //휴가계열
                    return processVacationForm(approvalInfoMap, requestData);
                case 'e': //지출계열
                    return processExpenseForm(approvalInfoMap, requestData);
                case 'f': //자유계열
                    return processFreeForm(approvalInfoMap, requestData);
                default:
                    throw new IllegalArgumentException(formType+ " : 해당 결재문서 양식 타입은 없습니다");
            }
        } catch (IllegalArgumentException e) {
            throw e; // 유효성 검증 실패는 그대로 전파
        } catch (Exception e) {
            throw new RuntimeException("미리보기 문서 생성 중 오류가 발생했습니다.", e);
        }

    }

    //자유 양식서 조합하기 로직
    private String processFreeForm(Map<String, String> approvalInfoMap,
                                   Map<String, Object> requestData) {
        try {
            // 1. 서버 측 유효성 검증하기
            List<String> errors = new ArrayList<>();

            String docTitle = (String) requestData.get("docTitle");
            String purpose = (String) requestData.get("purpose");
            String docContent = (String) requestData.get("docContent");

            // 날짜 정보 처리
            String periodInfo = formatPeriodInfo(requestData);

            // 필수 값 검증
            // 제목 검증
            if (docTitle == null || docTitle.trim().isEmpty()) {
                errors.add("문서 제목이 입력되지 않았습니다.");
            } else if (docTitle.length() > 30) {
                errors.add("문서 제목은 30자를 초과할 수 없습니다.");
            }

            // 목적/용도 검증
            if (purpose == null || purpose.trim().isEmpty()) {
                errors.add("목적/용도가 입력되지 않았습니다.");
            } else if (purpose.length() > 100) {
                errors.add("목적/용도는 100자를 초과할 수 없습니다.");
            }

            if (docContent == null || docContent.trim().isEmpty()) {
                errors.add("상세 내용이 입력되지 않았습니다.");
            }

            if (!errors.isEmpty()) {
                throw new IllegalArgumentException(String.join("\n", errors));
            }

            String template = approvalInfoMap.get("templateContent");

            // 이스케이프된 줄바꿈 문자를 실제 줄바꿈으로 변환
            template = template.replace("\\n", "\n");

            // 템플릿에 데이터 바인딩
            return template
                    .replace("${docTitle}", docTitle)
                    .replace("${empId}", approvalInfoMap.get("empId"))
                    .replace("${employeeName}", approvalInfoMap.get("empName"))
                    .replace("${department}", approvalInfoMap.get("departName"))
                    .replace("${position}", approvalInfoMap.get("posiName"))
                    .replace("${purpose}", purpose)
                    .replace("${period}", periodInfo)
                    .replace("${docContent}", docContent)
                    .replace("${requestDate}", LocalDate.now().toString());

        } catch (IllegalArgumentException e) {
            throw e; // 검증 실패는 그대로 전파
        } catch (Exception e) {
            throw new RuntimeException("자유양식서 처리 중 오류가 발생했습니다.", e);
        }
    }

    //지출 결의서 조합하기 로직
    private String processExpenseForm(Map<String, String> approvalInfoMap,
                                      Map<String, Object> requestData) {
        try {
            // 1. 서버 측에서 유효성 검증하기
            List<String> errors = new ArrayList<>(); // 에러넣어둘 리스트

            String expenseType = (String) requestData.get("expenseType");
            String expenseName = (String) requestData.get("expenseName");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> expenseItems = (List<Map<String, Object>>) requestData.get("expenseItems");
            String totalAmount = (String) requestData.get("totalAmount");

            // 필수 값 검증하기
            if (requestData.get("expenseName") == null || ((String)requestData.get("expenseName")).trim().isEmpty()) {
                errors.add("지출결의서 제목이 입력되지 않았습니다.");
            } else if (((String)requestData.get("expenseName")).length() > 30) {
                errors.add("지출결의서 제목은 30글자를 초과할 수 없습니다.");
            }
            if (expenseType == null || expenseType.trim().isEmpty()) {
                errors.add("지출 구분이 선택되지 않았습니다.");
            }
            if (expenseItems == null || expenseItems.isEmpty()) {
                errors.add("지출 내역이 입력되지 않았습니다.");
            }

            //2. 항목별 유효성 검사
            if (expenseItems != null) {
                for (int i = 0; i < expenseItems.size(); i++) {

                    Map<String, Object> item = expenseItems.get(i);
                    int lineNum = i + 1; // n번째 항목 (에러 메시지 용)

                    // date
                    String date = (String) item.get("date");
                    if (date == null || date.trim().isEmpty()) {
                        errors.add(lineNum + "번째 항목: 지출일자가 없습니다.");
                    }

                    // category
                    String category = (String) item.get("categoryCode");
                    if (category == null || category.trim().isEmpty()) {
                        errors.add(lineNum + "번째 항목: 지출분류가 선택되지 않았습니다.");
                    }

                    // paymentMethod
                    String itemPaymentMethod = (String) item.get("paymentMethod");
                    if (itemPaymentMethod == null || itemPaymentMethod.trim().isEmpty()) {
                        errors.add(lineNum + "번째 항목: 결제수단이 선택되지 않았습니다.");
                    }

                    // detail
                    String detail = (String) item.get("detail");
                    if (detail == null || detail.trim().isEmpty()) {
                        errors.add(lineNum + "번째 항목: 내용이 없습니다.");
                    }
                    // 내용 검증 부분 (expenseItems 처리 부분에서)
                    if (detail != null && detail.length() > 40) {
                        errors.add(lineNum + "번째 항목: 내용은 40글자를 초과할 수 없습니다.");
                    }

                    // quantity
                    String quantityStr = (String) item.get("quantity");
                    if (quantityStr == null || quantityStr.trim().isEmpty()) {
                        errors.add(lineNum + "번째 항목: 수량이 없습니다.");
                    } else {
                        try {
                            int quantity = Integer.parseInt(quantityStr);
                            if (quantity < 1) {
                                errors.add(lineNum + "번째 항목: 수량은 1 이상이어야 합니다.");
                            }
                        } catch (NumberFormatException e) {
                            errors.add(lineNum + "번째 항목: 수량이 숫자가 아닙니다.");
                        }
                    }

                    // price
                    String priceStr = (String) item.get("price");
                    if (priceStr == null || priceStr.trim().isEmpty()) {
                        errors.add(lineNum + "번째 항목: 단가가 없습니다.");
                    } else {
                        try {
                            int price = Integer.parseInt(priceStr);
                            if (price < 0) {
                                errors.add(lineNum + "번째 항목: 단가는 0 이상이어야 합니다.");
                            }
                        } catch (NumberFormatException e) {
                            errors.add(lineNum + "번째 항목: 단가가 숫자가 아닙니다.");
                        }
                    }

                    // amount
                    String amount = (String) item.get("amount");
                    if (amount == null || amount.trim().isEmpty()) {
                        errors.add(lineNum + "번째 항목: 금액이 계산되지 않았습니다.");
                    }
                }
            }

            if (!errors.isEmpty()) {
                throw new IllegalArgumentException(String.join("\n", errors));
            }

            String template = approvalInfoMap.get("templateContent");

            // 여기서 \n 이스케이프 문자를 실제 줄바꿈 문자로 변환
            template = template.replace("\\n", "\n");

            // expenseItems 처리
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) requestData.get("expenseItems");
            StringBuilder itemsHtml = new StringBuilder();
            for (Map<String, Object> item : items) {
                itemsHtml.append("<tr>")
                        .append("<td>").append(item.get("date")).append("</td>")
                        .append("<td>").append(item.get("categoryName")).append("</td>")
                        .append("<td>").append(item.get("paymentMethod")).append("</td>")
                        .append("<td style=\"text-align: right;\">").append(item.get("quantity")).append("</td>")
                        .append("<td style=\"text-align: right;\">").append(item.get("price")).append("원</td>")
                        .append("<td style=\"text-align: right;\">").append(item.get("amount")).append("</td>")
                        .append("<td>").append(item.get("detail")).append("</td>")
                        .append("</tr>");
            }

            return template
                    .replace("${expenseName}", expenseName)
                    .replace("${empId}", approvalInfoMap.get("empId"))
                    .replace("${employeeName}", approvalInfoMap.get("empName"))
                    .replace("${department}", approvalInfoMap.get("departName"))
                    .replace("${position}", approvalInfoMap.get("posiName"))
                    .replace("${expenseName}", (String) requestData.get("expenseName"))
                    .replace("${expenseType}", expenseType)
                    .replace("${expenseItems}", itemsHtml.toString())
                    .replace("${totalAmount}", totalAmount)
                    .replace("${docContent}", (String) requestData.get("docContent"))
                    .replace("${requestDate}", LocalDate.now().toString());

        } catch (IllegalArgumentException e) {
            throw e; // 검증 실패는 그대로 전파 검증 실패는 그대로 전파검증 실패는 그대로 전파
        } catch (Exception e) {
            throw new RuntimeException("지출결의서 처리 중 오류가 발생했습니다.", e);
        }
    }


    //휴가 신청서 조합하기 로직
    @Override
    public String processVacationForm(Map<String, String> approvalInfoMap,
                                      Map<String, Object> requestData) {
        try{
            // 서버 측 유효성 검증
            List<String> errors = new ArrayList<>();
            String vacationType = (String) requestData.get("vacationType");
            String vacCode = (String)  requestData.get("vacCode");
            String startDateStr = (String) requestData.get("startDate");
            String endDateStr = (String) requestData.get("endDate");

            if (vacationType == null || vacationType.trim().isEmpty() || "선택하세요".equals(vacationType)) {
                errors.add("휴가 종류가 선택되지 않았습니다.");
            }
            if (startDateStr == null || startDateStr.trim().isEmpty()) {
                errors.add("시작일이 입력되지 않았습니다.");
            }
            if (endDateStr == null || endDateStr.trim().isEmpty()) {
                errors.add("종료일이 입력되지 않았습니다.");
            }

            if (!errors.isEmpty()) {
                throw new IllegalArgumentException(String.join("\n", errors));
            }

            String template = approvalInfoMap.get("templateContent");

            // 여기서 \n 이스케이프 문자를 실제 줄바꿈 문자로 변환
            template = template.replace("\\n", "\n");

            // 날짜 파싱 시도
            LocalDate startDate;
            LocalDate endDate;
            try {
                startDate = LocalDate.parse(startDateStr);
                endDate = LocalDate.parse(endDateStr);
            } catch (DateTimeParseException e) { // dateTime 파싱 예외
                throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다.");
            }

            String empId = approvalInfoMap.get("empId");
            // 휴가 일수 계산 (주말 제외)
            double vacationDays = calculateWorkingDays(startDate, endDate, vacCode, empId);

            return template
                    .replace("${empId}", approvalInfoMap.get("empId"))
                    .replace("${employeeName}", approvalInfoMap.get("empName"))
                    .replace("${department}", approvalInfoMap.get("departName"))
                    .replace("${position}", approvalInfoMap.get("posiName"))
                    .replace("${vacationType}", vacationType)
                    .replace("${startDate}", startDateStr)
                    .replace("${endDate}", endDateStr)
                    .replace("${vacationDays}", String.valueOf(vacationDays))
                    .replace("${docContent}", (String) requestData.get("docContent"))
                    .replace("${requestDate}", LocalDate.now().toString());
        } catch (IllegalArgumentException e) {
            throw e;  // 검증 실패
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("휴가 신청서 처리 중 예기치 않은 오류 발생", e);
            throw new RuntimeException("휴가 신청서 처리 중 오류가 발생했습니다.", e);
        }

    }

    // 기간 정보 포맷팅 및 계산
    private String formatPeriodInfo(Map<String, Object> requestData) {
        //데이터 꺼내고
        String dateType = (String) requestData.get("dateType");
        log.info(dateType);
        //startdate만있는지 endDate만있는지 검사하기
        if ("period".equals(dateType)) {
            String startDate = (String) requestData.get("startDate");
            String endDate = (String) requestData.get("endDate");

            if (startDate != null && !startDate.trim().isEmpty()) {
                if (endDate != null && !endDate.trim().isEmpty()) { //둘다있다면 기간으로 포매팅 설정하기
                    long days = calculateDays(startDate, endDate); //calculate로 일자 계산하기
                    return String.format("%s ~ %s (총 %d일)", startDate, endDate, days);
                }
                return startDate + " ~"; //startDate만있으면 이날부터시행되는
            } else if (endDate != null && !endDate.trim().isEmpty()) {
                return "~ " + endDate  ; //endDate만있으면 이날까지만 되는
            }
        } else if ("single".equals(dateType)) { //특정일은 그냥특정일
            String singleDate = (String) requestData.get("singleDate");
            if (singleDate != null && !singleDate.trim().isEmpty()) {
                return singleDate;
            }
        }

        return "-";
    }

    // 날짜 간격 계산 (주말 포함)
    private long calculateDays(String startDateStr, String endDateStr) {
        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);

            //ChronoUnit은 지피티가알려줌
            return ChronoUnit.DAYS.between(startDate, endDate) + 1; // 종료일 포함
        } catch (Exception e) {
            return 0;
        }
    }

    //휴가 일수 계산 메소드(주말 제외하기가 목적임)
    private double calculateWorkingDays(LocalDate startDate, LocalDate endDate, String vacCode, String empId) {
        try {
            double days = 0;
            LocalDate currentDate = startDate;

            while (!currentDate.isAfter(endDate)) {
                if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY
                        && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    days++;
                }
                currentDate = currentDate.plusDays(1);
            }

            // 반차인 경우 0.5일로 계산
            double calculatedDays = "V002".equals(vacCode) ? days * 0.5 : days;

            // 여기서 검증하고 예외를 위로 전파하기
            validateVacationRequest(calculatedDays, vacCode, empId);

            return calculatedDays;

        } catch (IllegalArgumentException e) {
            // 검증 실패 예외를 상위로 전파하기
            throw e;
        } catch (Exception e) {
            log.error("휴가 일수 계산 중 오류 발생", e);
            throw new RuntimeException("휴가 일수 계산 중 오류가 발생했습니다.", e);
        }
    }

    private void validateVacationRequest(double days, String vacCode, String empId) {

        // 연차/반차인 경우에만 검증
        if (VAC_TYPE_ANNUAL.equals(vacCode) || VAC_TYPE_HALF.equals(vacCode)) {
            VacationStatusVO vacStatus = vacationStatusMapper.selectVacationStatusThisYear(empId);
            if (vacStatus == null) {
                throw new IllegalArgumentException("휴가 현황 정보를 찾을 수 없습니다.");
            }

            if (days > vacStatus.getVstaNowcount()) {
                throw new IllegalArgumentException(
                        String.format("신청한 휴가 일수(%.1f일)가 잔여 연차(%.1f일)를 초과합니다.",
                                days, vacStatus.getVstaNowcount())
                );
            }
        }
    }

    //결재 차수별 세팅해놓는 메소드
    private void generateApprovalProcess(String documentId, ApprovalDocumentDTO dto) {
        try {
            List<Map<String, Object>> approvers = dto.getApprovers();

            // 결재자들 정보 stream으로 List<ApprovalVO>에 저장
            List<ApprovalVO> approvalList = IntStream.range(0, approvers.size()) //결재 총 차수까지 반복
                    .mapToObj(index -> {
                        Map<String, Object> approver = approvers.get(index);
                        ApprovalVO approval = new ApprovalVO();

                        // 1) 결재번호는 단순히 순번(1,2,3)으로만 설정
                        approval.setApprovalId(String.valueOf(index + 1));
                        // 2) 문서번호
                        approval.setDocId(documentId);
                        // 3) 결재일자는 null (승인/반려 시 업데이트)
                        approval.setApprovalDate(null);
                        // 4) 결재 순서
                        approval.setApprovalNum(String.valueOf(index + 1));
                        // 5) 결재자 ID

                        // null 체크 추가
                        String empId = approver.get("EMPID") != null ?
                                approver.get("EMPID").toString() : null;
                        if (empId == null) {
                            log.error("결재자 ID가 null입니다. approver: {}", approver);
                            throw new RuntimeException("결재자 정보가 올바르지 않습니다");
                        }
                        approval.setEmpId(approver.get("EMPID").toString());

                        // 6) 결재의견 null
                        approval.setApprovalComment(null);
                        // 7) 결재상태 - 1차는 1(미열람), 나머지는 5(미지정)
                        approval.setApprovalStatus(index == 0 ? "1" : "5");
                        // 8) 전결여부는 N으로 시작
                        approval.setApprovalYn("N");
                        // 9) 실제 결재자는 null로 시작
                        approval.setApprlineRid(null);

                        return approval;
                    })
                    .collect(Collectors.toList());

            // 일괄 저장
            int resultAppr = approvalMapper.insertApprovals(approvalList);
            if (resultAppr != approvers.size()) {
                throw new RuntimeException("결재자 정보 저장 중 오류 발생");
            }
            log.info("결재자 정보 {}건 저장 완료", resultAppr);

        } catch (Exception e) {
            log.error("결재자 정보 처리 중 오류 발생", e);
            throw new RuntimeException("결재자 정보 처리 실패", e);
        }
    }


    //참조자 그룹 테이블 삽입, 참조자 삽입 메소드
    private void generateReferenceProcess(String documentId, ApprovalDocumentDTO dto) {
        //참조자 객체 생성
        ApprovalReferenceVO reference = new ApprovalReferenceVO();
        //참조 ID생성
        String referenceId = generateReferenceId();
        //1) 참조자 ID 넣기
        reference.setRefId(referenceId);
        //2) DOC ID넣기
        reference.setDocId(documentId);

        try {
            //1. 데이터베이스 테이블에 그룹 ID집어넣기
            int resultRef = approvalReferenceMapper.insertDocumentReference(reference);
            if (resultRef <= 0) {
                throw new RuntimeException("참조 그룹 생성 실패");
            }
            log.info("참조 그룹 생성 완료 (ID: {})", referenceId);

            //한번 꺼내기
            List<Map<String, Object>> references = dto.getReferences();
            //2. 참조자들 정보 스트림으로 List<VO>에 저장하기
            List<ReferenceEmployeeVO> refEmployees = references.stream()
                    .map(ref -> {
                        ReferenceEmployeeVO refEmployee = new ReferenceEmployeeVO();
                        refEmployee.setRefId(referenceId);
                        refEmployee.setEmpId(ref.get("empId").toString()); // 참조자 ID빼내고 집어넣기
                        return refEmployee;
                    })
                    .collect(Collectors.toList());
            //2. 참조자 일괄 db저장해보기 insertAll써서
            int resultEmp = approvalReferenceMapper.insertReferenceEmployees(refEmployees);
            // insert된 갯수가 참조자 수랑 안맞는다면
            if (resultEmp != references.size()) {
                throw new RuntimeException("참조자 저장 중 오류 발생");
            }
            log.info("참조자 {}명 저장 완료,", resultEmp);

        } catch (Exception e) {
            log.error("참조자 처리 중 오류 발생", e);
            throw new RuntimeException("참조자 처리 실패 ", e);
        }
    }

    //도장이미지 압축하고 base64화시키기
    private String processSignImage(byte[] storedImage) throws IOException {
        if (storedImage == null || storedImage.length == 0) {
            return null; // 이미지 데이터가 없으면 null 반환
        }
        // byte[] 타입 이미지 압축
        byte[] compressedImage = compressImage(storedImage);

        // 압축된 이미지를 Base64로 변환
        return Base64.getEncoder().encodeToString(compressedImage);
    }

    //보존기한 3년뒤로 설정하는 메소드
    private String preserveYearThird() {
        //현재 날짜 갖고오기
        LocalDate currentDate = LocalDate.now();
        //3년 더하기
        LocalDate preserveDate = currentDate.plusYears(3);
        // 날짜를 문자열로 변환하기 (yyyy-MM-dd)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //반환
        return preserveDate.format(formatter);
    }

    // byte[] 이미지 압축 메서드
    private byte[] compressImage(byte[] imageData) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .size(50, 50)
                .outputQuality(0.3)
                .outputFormat("png")
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    //문서 ID만드는 메소드
    private String generateDocumentId(String formId) {
        // 1. 현재 날짜 정보 가져오기(메소드 체이닝 1번)
        String yearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMM"));
        // 2. 테이블에서 검색용도로쓰일 prefix만들기
        String prefix = String.format("%s-%s", formId.toUpperCase(), yearMonth);

        // 3. 시퀀스 값 가져오기 (매퍼에서 조회해갖고옴 카운트)
        int sequence = approvalDocumentMapper.getNextDocSeq(prefix);

        // 4. 문서번호 생성해서 리턴하기
        return String.format("%s-%04d", prefix, sequence);
    }

    //참조 ID만드는 메소드
    private String generateReferenceId() {
        //만들어지는 구상도 REF-2412-0001
        String yearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMM"));

        int refSeq = approvalReferenceMapper.getNextRefSeq(yearMonth);

        return String.format("REF-%s-%04d", yearMonth, refSeq); // 참조 ID 생성
    }


}
