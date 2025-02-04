////1. 기본 데이터 로깅하기
//
//import kr.or.ddit.account.vo.AccountVO;
//import kr.or.ddit.commons.principal.AccountVOWrapper;
//import kr.or.ddit.employee.vo.EmployeeVO;
//import org.springframework.http.HttpStatus;
//
//import java.util.List;
//import java.util.Map;log.info("문서 HTML :{} ",requestData.get("documentHtml"));
//        log.info("전결 허용 여부 :{}", requestData.get("allowDelegation"));
//        log.info("양식 ID: {}", requestData.get("formId"));
//
////2. 결재자 정보 로깅하기
//@SuppressWarnings("unchecked")
//List<Map<String,Object>> approvers = (List<Map<String, Object>>) requestData.get("approvers");
//            log.info("결재자 정보 : {}", approvers);
//            for (Map<String, Object> approver : approvers) {
//        log.info("결재자 : {}", approver);
//            }
//
////3. 참조자 정보 로깅하기
//@SuppressWarnings("unchecked")
//List<Map<String,Object>> references = (List<Map<String, Object>>) requestData.get("references");
//            log.info("참조자 정보 : {}", references);
//            for (Map<String, Object> reference : references) {
//        log.info("참조자 : {}", reference);
//            }
//
////4. 양식 데이터 로깅
//@SuppressWarnings("unchecked")
//Map<String, Object> formData = (Map<String, Object>) requestData.get("formData");
//            log.info("양식 데이터: {}", formData);
//
//// 5. 현재 로그인한 사원 정보 가져오기
//AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
//AccountVO account = principal.getAccount();
//EmployeeVO myEmp = (EmployeeVO)account;
//
//            log.info("기안자 정보: {}", myEmp.getEmpId());
//
//        return ResponseEntity.ok("데이터 확인 완료");
//        }catch (Exception e) {
//        log.error("기안 처리 중 오류 발생", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("기안 처리 중 오류가 발생했습니다.");
//        }