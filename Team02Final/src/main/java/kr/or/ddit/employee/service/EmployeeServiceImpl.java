package kr.or.ddit.employee.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.exception.PKNotFoundException;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.vo.CommonCodeVO;
import kr.or.ddit.employee.dao.EmployeeMapper;
import kr.or.ddit.employee.dao.GoogleOAuthMapper;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.employee.vo.OAuthVO;
import kr.or.ddit.organitree.vo.DepartmentVO;
import kr.or.ddit.vacation.vo.VacationStatusVO;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;	

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {
	@Inject
	private EmployeeMapper employeeMapper;
	
	@Inject
	private GoogleOAuthMapper oAuthMapper;
	
	@Autowired
	private AuthenticationManagerBuilder authenticationManagerBuilder;
	
	@Autowired
	private UserDetailsService userDetailsService;
	

	@Override
	public EmployeeVO readEmployee(AccountVO account) {
		EmployeeVO myEmp = employeeMapper.selectEmployee(account);
		
		if(myEmp!=null) {
			return myEmp;
		} else {
			throw new PKNotFoundException();
		}
	}
	
	@Override
	public EmployeeVO readOneEmployee(String empId) {
		return employeeMapper.selectOneEmployee(empId);
	}
	
	@Override
	public EmployeeVO readEmployeeForJoin(String empMail) {
		EmployeeVO emp = employeeMapper.selectEmployeeForJoin(empMail);
		if(emp!=null) {
			return emp;
		} else {
			throw new PKNotFoundException();
		}
	}
	
	

	@Override
	public List<EmployeeVO> readEmployeeList(PaginationInfo<EmployeeVO> paging) {
		if(paging !=null) {
			int totalRecord = employeeMapper.selectTotalRecord(paging);
			paging.setTotalRecord(totalRecord);
		}
		return employeeMapper.selectEmployeeList(paging);
	}
	
	@Override
	public EmployeeVO readEmployeeForAuth(EmployeeVO emp) {
		EmployeeVO rowEmp = employeeMapper.selectEmployeeForAuth(emp);
		if(rowEmp==null) {
			throw new PKNotFoundException();
		}
		return rowEmp;
	}
	
	@Override
	public List<EmployeeVO> readVacationEmployeeList(String departCode){
		return employeeMapper.selectVacationEmployeeList(departCode);
	}

	@Override
	@Transactional
	public ServiceResult modifyEmployeeAtFirst(EmployeeVO emp) {
		
		try {
			emp.setEmpStatus("U");
	        int employeeResult = employeeMapper.updateEmployee(emp); // update 호출
	        int oAuthResult = oAuthMapper.insertOAuth(emp.getOAuth()); // insert 호출

	        
	        if (employeeResult > 0 && oAuthResult > 0) {
	            return ServiceResult.OK; // 성공 시 반환
	        } else {
	            return ServiceResult.FAIL;
	        }
	    } catch (DataIntegrityViolationException e) {
	        // 데이터 무결성 위반 (PK, FK, 유니크 제약 조건 등)
	        return ServiceResult.PKDUPLICATED;
	    } catch (DataAccessException e) {
	        // MyBatis에서 발생한 일반적인 데이터 접근 예외
	        e.printStackTrace(); // 로그 기록
	        return ServiceResult.SERVERERROR;
	    } catch (Exception e) {
	        // 기타 예외 처리
	        e.printStackTrace(); // 로그 기록
	        return ServiceResult.SERVERERROR;
	    }
	}
	

	@Override
	public ServiceResult modifyEmployeePassword(EmployeeVO emp) {
		try {
			emp.setEmpStatus("R");
			employeeMapper.updateEmployee(emp);
			return ServiceResult.OK;
		} catch (DataIntegrityViolationException e) {
	        // 데이터 무결성 위반 (PK, FK, 유니크 제약 조건 등)
	        return ServiceResult.PKDUPLICATED;
	    } catch (DataAccessException e) {
	        // MyBatis에서 발생한 일반적인 데이터 접근 예외
	        e.printStackTrace(); // 로그 기록
	        return ServiceResult.SERVERERROR;
	    } catch (Exception e) {
	        // 기타 예외 처리
	        e.printStackTrace(); // 로그 기록
	        return ServiceResult.SERVERERROR;
	    }
	}
	
	@Transactional
	@Override
	public ServiceResult modifyMyEmployee(EmployeeVO emp) {
		String empBirth = emp.getEmpBirth();
		if(empBirth!=null) {
			if(empBirth.contains("-")) {
				empBirth = empBirth.replace("-", "");
				emp.setEmpBirth(empBirth);
			}
		}
		
		try {
			OAuthVO oauth = emp.getOAuth();
			if(oauth!=null) {
				String myEmpMail = String.format("%s@gmail.com", oauth.getOauthEmpmail());
				oauth.setOauthEmpmail(myEmpMail);
				emp.setEmpMail(myEmpMail);
				oAuthMapper.updateOAuth(oauth);
				employeeMapper.updateEmployee(emp); // update 호출
				
				changeAuthentication(emp.getEmpMail(), emp.getEmpPass()); // 인증 변경
			} else {
				String myEmpMail = String.format("%s@gmail.com", emp.getEmpMail());
				emp.setEmpMail(myEmpMail);
				employeeMapper.updateEmployee(emp);
				changeAuthentication(emp.getEmpMail(), emp.getEmpPass()); // 인증 변경
			}

			return ServiceResult.OK; // 성공 시 반환
	    } catch (DataIntegrityViolationException e) {
	        // 데이터 무결성 위반 (PK, FK, 유니크 제약 조건 등)
	    	e.printStackTrace();
	        return ServiceResult.PKDUPLICATED;
	    } catch (DataAccessException e) {
	        // MyBatis에서 발생한 일반적인 데이터 접근 예외
	        e.printStackTrace();
	        return ServiceResult.SERVERERROR;
	    } catch (Exception e) {
	        // 기타 예외 처리
	        e.printStackTrace();
	        return ServiceResult.SERVERERROR;
	    }
	}
	

	// 나연(회원정보 수정시 사용)
	private void changeAuthentication(String username, String newPassword) {
		 // UserDetailsService를 사용해 새로운 사용자 정보 로드
	    UserDetails newUserDetails = userDetailsService.loadUserByUsername(username);
	    
	    // 새로운 Username과 Password로 인증 객체 생성
	    UsernamePasswordAuthenticationToken newAuthRequest = 
	            new UsernamePasswordAuthenticationToken(newUserDetails, newPassword, newUserDetails.getAuthorities());
	    
	    // AuthenticationManager를 사용해 인증 처리
	    Authentication newAuthentication = authenticationManagerBuilder.getObject().authenticate(newAuthRequest);
	    
	    // 새로운 SecurityContext 생성 및 설정
	    SecurityContext newContext = SecurityContextHolder.createEmptyContext();
	    newContext.setAuthentication(newAuthentication);
	    SecurityContextHolder.setContext(newContext);
	    
	}
	
	@Override
	public ServiceResult modifyEmployee(EmployeeVO emp) {
		try {
			int rowcnt = employeeMapper.updateEmployee(emp);
			
			if(rowcnt>0) {
				return ServiceResult.OK;
			} else {
				return ServiceResult.FAIL;
			}
			
		} catch(DataIntegrityViolationException e) {
			e.printStackTrace();
	        return ServiceResult.PKDUPLICATED;
		} catch (DataAccessException e) {
	        // MyBatis에서 발생한 일반적인 데이터 접근 예외
	        e.printStackTrace();
	        return ServiceResult.SERVERERROR;
	    } catch (Exception e) {
	        // 기타 예외 처리
	        e.printStackTrace();
	        return ServiceResult.SERVERERROR;
	    }
	}
	
	
	
	@Override
	public ServiceResult modifyStatusEmployee(String accountMail, String empStatus) {
		int rowcnt = employeeMapper.updateEmployeeStatus(accountMail, empStatus);
		ServiceResult result = null;
		if(rowcnt!=1) {
			// update 실패했으면
			result = ServiceResult.FAIL;
		} else {
			// update 성공했으면
			result = ServiceResult.OK;
		}
		return result;
	}
	
	@Override
	public ServiceResult deleteEmpImg(String empId) {
		int rowcnt = employeeMapper.deleteEmpImg(empId);
		if(rowcnt>0) return ServiceResult.OK;
		else return ServiceResult.FAIL;
	}
	
	@Override
	public ServiceResult deleteEmpSignImg(String empId) {
		int rowcnt = employeeMapper.deleteEmpSignImg(empId);
		if(rowcnt>0) return ServiceResult.OK;
		else return ServiceResult.FAIL;
	}
	
	@Override
	public SingleMessageSentResponse sendOauthSMS(String phoneNumber, String smsText) {
		// 메세지 전송 정보를 담은 message 객체
		// API_KEY, API_SECRET_KEY ==> CoolSMS 계정 정보에서 확인
		String API_KEY = "NCS0QC9FJCM8VWVY";
		String API_SECRET_KEY = "";
		DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY,
				"https://api.coolsms.co.kr");
		
		Message message = new Message();
		message.setFrom("01056959501");
		message.setTo(phoneNumber);
		message.setText(smsText);
		SingleMessageSentResponse response 
			= messageService.sendOne(new SingleMessageSendingRequest(message));
		return response;
	}
	
	@Override
	public String generateVerificationCode() {
		Random rand = new Random();
		int code = 100000 + rand.nextInt(900000);

		return String.valueOf(code);
	}
	
//	나연 에러 없으면 삭제 예정	
//	@Override
//	public ServiceResult deleteEmployee(String accountMail) {
//		int rowcnt = employeeMapper.deleteEmployee(accountMail);
//		if(rowcnt>0) return ServiceResult.OK;
//		else return ServiceResult.FAIL;
//	}
	
	@Override
	public ServiceResult createEmployee(List<Map<String, Object>> empData) {
		boolean fail = false;
	    try {
	        for (Map<String, Object> emp : empData) { // 사원데이터 리스트를 순회 
	            
	            String yyyymm = emp.get("empJoin").toString().replace("-","").substring(0, 6); // empJoin 날짜 문자열에서 연월 yyyymm 추출

	            synchronized (this) { // 동기화 블록 : EMP_ID 중복 생성 방지.
	                String maxEmpId = employeeMapper.selectMaxEmpIdByJoin(yyyymm); // DB에서 yyyymm 기준으로 가장 큰 EMP_ID 조회
	                int nextNumber = 1; 
	                if (maxEmpId != null && !maxEmpId.isEmpty()) {
	                    String numericPart = maxEmpId.substring(6);	// EMP_ID에서 숫자 부분만 추출 
	                    nextNumber = Integer.parseInt(numericPart) + 1; // +1 하여 다음 사번 생성 
	                }

	                // 새로운 EMP_ID 생성 :(yyyymm + 4자리 숫자) 
	                String empId = yyyymm + String.format("%04d", nextNumber);
	                emp.put("empId", empId); 
	                
	           } 

	            // 개별 INSERT 처리
	            int rowCount = employeeMapper.insertEmployee(emp);
	            
	            if(rowCount == 0) {
	            	fail = true;
	            }
	            

	    } 
	    }catch (Exception e) {
	        e.printStackTrace();
	        return ServiceResult.FAIL;
	    }
	    return fail ? ServiceResult.FAIL : ServiceResult.OK;
	}
	
	
	@Override
	public ServiceResult createOneEmployee(EmployeeVO emp) {
	    // empJoin이 date 타입이여서 - 를 replaceAll 처리
	    String yyyymm = String.valueOf(emp.getEmpJoin().replace("-", "").substring(0, 6));

	    // date 타입의 EMP_BIRTH 값을 YYYYMMDD 형식으로 변환 (replaceAll)
	    if (emp.getEmpBirth() != null && !emp.getEmpBirth().isEmpty()) {
	        String formattedBirth = emp.getEmpBirth().replace("-", ""); // "1999-09-08" → "19990908"
	        emp.setEmpBirth(formattedBirth);
	    }
	    
	    // EMP_JOIN 값에서 "-" 제거 → YYYYMMDD 형식으로 변환 (replaceAll)
	    if (emp.getEmpJoin() != null && !emp.getEmpJoin().isEmpty()) {
	    	String formmatedJoin = emp.getEmpJoin().replace("-", "");
	    	emp.setEmpJoin(formmatedJoin);
	    }
	    
	    synchronized (this) { // 동기화 블록 : EMP_ID 중복 생성 방지.
	        String maxEmpId = employeeMapper.selectMaxEmpIdByJoin(yyyymm);
	        int nextNumber = 1; 
	        if (maxEmpId != null && !maxEmpId.isEmpty()) {
	            String numericPart = maxEmpId.substring(6);
	            nextNumber = Integer.parseInt(numericPart) + 1;
	        }

	        String empId = yyyymm + String.format("%04d", nextNumber);
	        emp.setEmpId(empId);
	    }

	    int rowcnt = employeeMapper.insertOneEmployee(emp);
	    return rowcnt > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}
	    		
	    	



	
	
	
	

	
	
	
	
	
	
	
	
	@Override
	public ServiceResult removeOneEmployee(String empId) {
		int rowcnt = employeeMapper.deleteOneEmployee(empId);
		if(rowcnt>0) return ServiceResult.OK;
		else return ServiceResult.FAIL;
	}
	
	
	

    @Override
    public List<Map<String, Object>> readEmployeeListForDept() {

		List<EmployeeVO> employees = employeeMapper.selectEmployeeListForDept();
		//서비스로직에서 데이터가공해서 컨트롤러로 보내기
		//조직도에 넣을 부서명 사원명을 뽑아넣기위함
		return employees.stream()
				.map(emp -> {
					Map<String, Object> empMap = new HashMap<>();
					empMap.put("empId", emp.getEmpId());
					empMap.put("empName", emp.getEmpName());
					empMap.put("posiName", emp.getPosiName());   
					empMap.put("departCode", emp.getDepartmentVO().getDepartCode());
					empMap.put("departName", emp.getDepartmentVO().getDepartName());
					return empMap;
				})
				.collect(Collectors.toList());
    }

	@Override
	public List<DepartmentVO> readAllDeptList() {
		return employeeMapper.selectAllDept();
	}

	@Override
	public List<CommonCodeVO> readAllPosi() {
		return employeeMapper.selectAllPosi();
	}

	@Override
	public Map<String, Object> readEmployeePosiNameAndDeptNameByEmpId(String empId) {
		return employeeMapper.selectEmployeePosiNameAndDeptNameByEmpId(empId);
	}

	@Override
	public List<CommonCodeVO> readStatusType() {
		
		return employeeMapper.selectStatusType();
	}

	@Override
	public VacationStatusVO readOneVacationStatusDetail(String vstaCode,String empId) {
		return employeeMapper.selectOneVacationStatusDetail(vstaCode,empId);
	}

	@Override
	public List<VacationStatusVO> readVstaCodeList() {
		return employeeMapper.selectVstaCodeList();
	}

	@Override
	public ServiceResult modifyVacationStatus(VacationStatusVO vs) {
		return employeeMapper.updateVacationStatus(vs) > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public DepartmentVO readMyDepartName(String empId) {
		DepartmentVO myDepart = (DepartmentVO) employeeMapper.selectMyDepartName(empId);
		return myDepart;
	}

	
		

	


}
