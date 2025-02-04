package kr.or.ddit.approval.service;

import kr.or.ddit.approval.dao.ApprovalDocumentMapper;
import kr.or.ddit.approval.dao.ApprovalMapper;
import kr.or.ddit.approval.vo.ApprovalDocumentVO;
import kr.or.ddit.approval.vo.ApprovalVO;
import kr.or.ddit.employee.dao.EmployeeMapper;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.expense.dao.ExpenseMapper;
import kr.or.ddit.expense.service.ExpenseService;
import kr.or.ddit.vacation.service.VacationHistoryService;
import kr.or.ddit.vacation.vo.VacationHistoryVO;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ApprovalServiceImpl implements ApprovalService {

    @Autowired
    ApprovalMapper approvalMapper;

    @Autowired
    ApprovalDocumentMapper approvalDocumentMapper;

    @Autowired
    VacationHistoryService vacationHistoryService;

    @Autowired
    ExpenseService expenseService;

    @Autowired
    EmployeeMapper employeeMapper;

    /**
     * 실시간 알림용
     */
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    // 공통 메서드
    private void processApproval(String docId, String empId, ApprovalVO approval, String statusCode) {
        //1. approval 테이블의 approvalDate를 LocalDate.now()로 세팅
        approval.setApprovalDate(LocalDate.now());
        //2. approval 테이블의 apprlineRid(실제결재자) 자신(결재자)의 사원번호 삽입
        approval.setApprlineRid(empId);
        //3. approval 테이블의 approvalStatus를 "3" 아니면 "4"으로 변경한다 -> 3은 승인 4는 반려
        approval.setApprovalStatus(statusCode);
        //4. approval 테이블의 approvalComment는 이미 담겨있음 근데 승인이라 null일수도있음

        if (approval.getApprovalYn() == null) {
            approval.setApprovalYn("N");
        }
        log.info("approvalComment : {} ", approval.getApprovalComment());


        approvalMapper.updateApproval(docId, empId, approval);

        // 반려, 전결, 마지막결재자가 아닐 때만 다음 결재자 활성화
        if(!statusCode.equals("4") && approval.getApprovalYn().equals("N") && !isLastApprover(docId, empId)) {
            updateNextApprover(docId, empId, statusCode);
        }

        // 반려, 전결, 마지막결재자일 때 후처리 진행 그럼 3이면서 전결사용여부가 Y일것임
        if(statusCode.equals("4") || approval.getApprovalYn().equals("Y") || isLastApprover(docId, empId)) {
            handleDocumentPostProcessing(empId, docId, statusCode);
        }
    }


    //문서 후처리
    private void handleDocumentPostProcessing(String empId, String docId, String statusCode) {
        if (docId == null || docId.isEmpty()) {
            throw new IllegalArgumentException("docId는 null이거나 비어있을 수 없습니다.");
        }

        try {
            // docId의 첫 번째 문자를 가져옴
            char docType = docId.charAt(0);

            // 자유양식서(F)는 별도 처리 없이 early return
            if (docType == 'F') {
                log.info("자유 양식서({}) - 추가 처리 없음", docId);
                return;
            }
            switch (docType) {
                case 'V': // 휴가 신청서 이건 휴가 서비스단에서 처리함
                    vacationHistoryService.processVacationDocument(docId, statusCode);
                    break;
                case 'E': // 지출 결의서
                    expenseService.processExpenseDocument(docId, statusCode);
                    break;
                default: // 처리되지 않는 유형
                    throw new UnsupportedOperationException("지원되지 않는 문서 유형: " + docType);
            }

            /**
             * 최종 처리시 (반려/전결/승인) 마지막 결재자가 기안자에게 메세지를 보내는것
             */
            // 실시간 알림보내기위한 기안자 id갖고오기
            String receiverId = approvalDocumentMapper.selectEmpIdByDocumentId(docId);
            // 최종 승인자 이름갖고오기
            String finalApproverName  = employeeMapper.selectEmployeeNameById(empId);
            // WebSocket 알림 전송
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", "결재 결과 알림");
            payload.put("content", "결재 문서를 확인해주세요.");
            payload.put("senderId", empId);
            payload.put("sender", finalApproverName);
            payload.put("documentId", docId);
            payload.put("receiverId", receiverId);

            messagingTemplate.convertAndSendToUser(
                    receiverId, // 수신자 ID
                    "/queue/LA", // WebSocket 경로 LA : LastApproval
                    payload // 전송할 데이터
            );

        } catch (UnsupportedOperationException e) {
            // 지원되지 않는 문서 유형 처리
            System.err.println("지원되지 않는 문서 유형입니다: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // 일반적인 예외 처리
            System.err.println("문서 후처리 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //승인처리
    @Override
    @Transactional
    public String approveDocument(String docId, EmployeeVO myEmp, ApprovalVO approval) {
        try {

            String empId = myEmp.getEmpId();
            byte[] empSignimg = myEmp.getEmpSignimg();
            log.info("empSignimg size: {}", empSignimg != null ? empSignimg.length : "null");

            // 1. 문서 내용 조회
            ApprovalDocumentVO document = approvalDocumentMapper.selectDocumentDetail(docId);
            String docContent = document.getDocContent();

            // 2. 결재자의 도장 이미지 용량 줄이기 처리
            String base64SignImage = processSignImage(empSignimg);
            log.info("base64SignImage: {}", base64SignImage != null ? "exists" : "null");

            // 3. HTML에서 현재 결재자의 도장 위치 찾아 이미지 삽입
            docContent = insertSignatureImage(docContent, empId, base64SignImage);

            // 4. 상태 코드 결정
            String statusCode = isLastApprover(docId, empId) ? "5" : "3";

            // 5. HTML 문서 상태 텍스트 변경
            docContent = updateDocumentStatus(docContent, statusCode);

            // 문서 상태와 내용 한 번에 업데이트
            document.setDocContent(docContent);
            document.setDocStatus(statusCode);
            approvalDocumentMapper.updateDocumentContent(docId, document);

            // 6. 처리한 뒤에 다음 결재자를 찾아서 statusCode "5"을 "1"로 바꿔야한다
            // 최종승인자의경우 processApproval로직에서 블락당해서 그냥 넘겨도된다
            processApproval(docId, empId, approval, "3");

            return "결재가 정상적으로 처리되었습니다.";
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("승인 처리 중 검증 오류: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("승인 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("결재 처리 중 오류가 발생했습니다.", e);
        }

    }

    // 반려처리 메소드
    @Override
    @Transactional
    public String rejectDocument(String docId, EmployeeVO myEmp, ApprovalVO approval) {

        try {
            // 결재 의견 있는지 검증하기 없으면 throw
            if(approval.getApprovalComment() == null || approval.getApprovalComment().trim().isEmpty()) {
                throw new IllegalArgumentException("반려 시에는 의견 작성이 필수입니다.");
            }

            String empId = myEmp.getEmpId();
            byte[] empSignimg = myEmp.getEmpSignimg();

            // 1. 문서 내용 조회
            ApprovalDocumentVO document = approvalDocumentMapper.selectDocumentDetail(docId);
            String docContent = document.getDocContent();

            // 2. 도장 이미지 처리
            String base64SignImage = processSignImage(empSignimg);

            // 3. 도장 이미지 삽입
            docContent = insertSignatureImage(docContent, empId, base64SignImage);

            // 4. 문서 상태 텍스트 변경
            docContent = updateDocumentStatus(docContent, "4");

            // 5. 변경된 문서 내용 저장 (신규 추가)
            document.setDocContent(docContent);
            document.setDocStatus("4");
            approvalDocumentMapper.updateDocumentContent(docId, document);

            // 6. 결재 처리
            processApproval(docId, empId, approval, "4");
            return "반려되었습니다.";
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("승인 처리 중 검증 오류: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("승인 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("결재 처리 중 오류가 발생했습니다.", e);
        }

    }

    // 전결 처리
    @Override
    @Transactional
    public String approveFinalDocument(String docId, EmployeeVO myEmp, ApprovalVO approval) {

        try{
            String empId = myEmp.getEmpId();
            byte[] empSignimg = myEmp.getEmpSignimg();

            // 1. 문서 내용 조회
            ApprovalDocumentVO document = approvalDocumentMapper.selectDocumentDetail(docId);
            String docContent = document.getDocContent();

            // 2. 도장 이미지 처리
            String base64SignImage = processSignImage(empSignimg);

            // 3. 현재 결재자의 도장 먼저 찍기
            docContent = insertSignatureImage(docContent, empId, base64SignImage);

            // 4. 남은 결재자들 조회
            List<ApprovalVO> remainingApprovers = approvalMapper.selectRemainingApprovers(empId, docId);

            // 5. 현재 결재자의 도장을 모든 남은 결재자의 위치에 삽입
            for (ApprovalVO approver : remainingApprovers) {
                docContent = insertStrikethrough(docContent, approver.getEmpId());
            }
            // 6. 문서 상태 텍스트 변경
            docContent = updateDocumentStatus(docContent, "5");

            // 5. 변경된 문서 내용 저장
            document.setDocContent(docContent);
            document.setDocStatus("5");

            approvalDocumentMapper.updateDocumentContent(docId, document);

            // 1. 내 결재 처리 (해당 차수 결재자)
            approval.setApprovalStatus("3");
            approval.setApprovalYn("Y");  // 전결권 사용 표시
            processApproval(docId, empId, approval, "3");

            // 7. 남은 결재자들 모두 현재 결재자로 변경
            approvalMapper.updateRemainingApprovers(docId, empId);

            return "전결 처리되었습니다.";
        }catch (IllegalArgumentException | IllegalStateException e) {
            log.error("승인 처리 중 검증 오류: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("승인 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("결재 처리 중 오류가 발생했습니다.", e);
        }

    }

    // 전결 처리시 날짜 표시 추가
    private String insertStrikethrough(String docContent, String empId) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String approverBoxPattern = String.format(
                "(<li class=\"approver-box\" data-emp-id=\"%s\">)(.*?<div class=\"stamp-box\">)",
                empId
        );

        String dateAndStrikethroughHtml = String.format(
                "$1<div class=\"approval-date\">%s</div>$2" +
                        "<div class=\"strikethrough-line\"></div>",
                currentDate
        );

        return docContent.replaceAll(approverBoxPattern, dateAndStrikethroughHtml);
    }

    //다음결재자 상태변경시키기 5 -> 1
    private void updateNextApprover(String docId, String currentEmpId, String statusCode) {
        List<ApprovalVO> approvalList = approvalMapper.selectApprovalList(docId);

        // 현재 결재자의 순번 찾기
        int currentOrder = approvalList.stream()
                .filter(a -> a.getEmpId().equals(currentEmpId))
                .map(a -> Integer.parseInt(a.getApprovalNum()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("결재자 정보를 찾을 수 없습니다."));

        // 다음 결재자 찾아서 상태 변경
        approvalList.stream()
                .filter(a -> Integer.parseInt(a.getApprovalNum()) == currentOrder + 1)
                .findFirst()
                .ifPresent(nextApprover -> {
                    approvalMapper.updateMyApprovalStatus(docId, nextApprover.getEmpId(), "1");
                    //이름갖고오기
                    String currentApproverName  = employeeMapper.selectEmployeeNameById(currentEmpId);

                    String receiverId = nextApprover.getEmpId();
                    // WebSocket 알림 전송
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("title", "결재 승인 알림");
                    payload.put("content", "결재 문서를 확인해주세요.");
                    payload.put("senderId", currentEmpId);
                    payload.put("sender", currentApproverName);
                    payload.put("documentId", docId);
                    payload.put("receiverId", nextApprover.getEmpId());

                    messagingTemplate.convertAndSendToUser(
                            receiverId, // 수신자 ID
                            "/queue/NA", // WebSocket 경로 FA : FirstApproval
                            payload // 전송할 데이터
                    );
                });
    }

    // 최종 승인 여부 판단
    private boolean isLastApprover(String docId, String empId) {
        return approvalMapper.isLastApprover(docId, empId);
    }


    // 이미지 압축
    private byte[] compressImage(byte[] imageData) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(imageData);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Thumbnails.of(input)
                .size(50, 50)
                .outputQuality(0.3)
                .outputFormat("png")
                .toOutputStream(output);

        return output.toByteArray();
    }

    // 도장 이미지 처리
    private String processSignImage(byte[] imageData) throws IOException {
        if (imageData == null || imageData.length == 0) {
            return null;
        }
        byte[] compressedImage = compressImage(imageData);
        return Base64.getEncoder().encodeToString(compressedImage);
    }

    // 도장 이미지 HTML 삽입
    private String insertSignatureImage(String docContent, String empId, String base64Image) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String approverBoxPattern = String.format(
                "(<li class=\"approver-box\" data-emp-id=\"%s\">)(.*?<div class=\"stamp-box\">)",
                empId
        );

        String dateAndImageHtml = String.format(
                "$1<div class=\"approval-date\">%s</div>$2" +
                        "<img src=\"data:image/png;base64,%s\" class=\"sign-image\" />",
                currentDate, base64Image
        );

        return docContent.replaceAll(approverBoxPattern, dateAndImageHtml);
    }

    private String updateDocumentStatus(String docContent, String status) {
        String statusText;
        switch(status) {
            case "3":
                statusText = "결재 진행 중";
                break;
            case "4":
                statusText = "결재 반려";
                break;
            case "5":
                statusText = "최종 승인";
                break;
            default:
                throw new IllegalArgumentException("잘못된 결재 상태 코드입니다.");
        }

        return docContent.replaceAll(
                "<span data-field=\"docStatus\">[^<]*</span>",
                String.format("<span data-field=\"docStatus\">%s</span>", statusText)
        );
    }


}
