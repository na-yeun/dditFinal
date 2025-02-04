package kr.or.ddit.organitree.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.organitree.vo.DepartmentVO;

@Mapper
public interface OrganiTreeMapper {
	
	/**
	 * 조직도의 부서, 팀 조회 (민재)
	 */
	public List<DepartmentVO> selectOrganiList(); 
	
	
	/**
	 * 하나의 부서 소속 직원 (민재)
	 * @param departCode
	 * @return
	 */
	public List<EmployeeVO> selectOneOrganiList(String departCode);
	
	
	/**
	 * 한명의 상세정보 (민재)
	 * @param empId
	 * @return
	 */
	public EmployeeVO selectOneEmployeeDetail(String empId);
	
	
	/**
	 * 검색 조회 ( 동명이인이 있을 수 있기 때문에 List로 받는다 ) (민재)
	 * @param empName
	 * @return
	 */
	public List<EmployeeVO> searchOneEmployeeDetail(String empName);
	
	
	
	/**
	 * 디렉토리 구조 조회 (민재)
	 * @return
	 */
	public List<DepartmentVO> selectDirectory();
	
	
	/**
	 * 하나의 부서 정보 (민재)
	 * @return
	 */
	public DepartmentVO selectOneDepart(String departCode);
	
	
	/**
	 * 하나의 부서 수정 (민재)
	 * @param department
	 * @return
	 */
	public int updateOneDepart(DepartmentVO department);
	
	/**
	 * 하나의 부서 삭제 (민재)
	 * @param departCode
	 * @return
	 */
	public int deleteOneDepart(String departCode);
	
	
	/**
	 * 하나의 부서 추가 (민재)
	 * @param department
	 * @return
	 */
	public int insertOneDepart(DepartmentVO department);
	
	
	/**
	 * 삭제 할 때 하위부서가 있는지 검증 (민재)
	 * @param departCode
	 * @return
	 */
	public List<DepartmentVO> selectChildDepList(String departCode);
	
	
	/**
	 * 관리자가 조직관리에서 부서를 추가/수정할 때 똑같은 이름의 부서가 있는 지 조회 (민재) 
	 * @param departName
	 * @return
	 */
	public int selectDepartNameCheck(String departName);
}

	


