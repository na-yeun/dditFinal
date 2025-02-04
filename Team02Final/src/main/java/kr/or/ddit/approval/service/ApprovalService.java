package kr.or.ddit.approval.service;

import kr.or.ddit.approval.vo.ApprovalVO;
import kr.or.ddit.employee.vo.EmployeeVO;

import java.io.IOException;

public interface ApprovalService {

    /**
     * 승인 처리했을때 이 메소드를 타고감
     * @param docId
     * @param myEmp
     * @param approval
     */
    public String approveDocument(String docId, EmployeeVO myEmp, ApprovalVO approval) throws IOException;

    /**
     * 반려 처리했을때 이 메소드를 타고감
     * @param docId
     * @param myEmp
     * @param approval
     */
     public String approveFinalDocument(String docId, EmployeeVO myEmp, ApprovalVO approval) throws IOException;

    /**
     * 전결 처리했을때 이 메소드를 타고감
     * @param docId
     * @param myEmp
     * @param approval
     */
    public String rejectDocument(String docId, EmployeeVO myEmp, ApprovalVO approval) throws IOException;



}
