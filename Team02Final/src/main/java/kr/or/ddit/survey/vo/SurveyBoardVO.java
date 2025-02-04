package kr.or.ddit.survey.vo;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.commons.validate.UpdateGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(of = "sboardNo")
@NoArgsConstructor
public class SurveyBoardVO {
	private int rnum;
	
	private String sboardNo;
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class})
	@Size(min=1, max = 1000, message = "입력 값은 최소 1글자에서 최대 300글자까지 가능합니다.")
	private String surboardNm;
	private String surboardContent;
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate surboardWrite;
	@DateTimeFormat(iso = ISO.DATE)
	@NotNull(groups = {InsertGroup.class, UpdateGroup.class})
	private LocalDate surboardStdate;
	@DateTimeFormat(iso = ISO.DATE)
	@NotNull(groups = {InsertGroup.class, UpdateGroup.class})
	private LocalDate surboardEnddate;
	
	
	private String surboardYn;
	private String surboardTarget;
	private String endDateWarning; // 조회용(데이터베이스용 아님)
	private String startDateCheck; // 조회용(데이터베이스용 아님)
	private String surboardTargetName; // 조회용(데이터베이스용 아님)
	private String detailUrl; // 조회용(데이터베이스용 아님)
	private Integer rowExists; // 조회용
	private String empId;
	
	private Integer totalCnt;
	
	// 게시판 하나의 질문 리스트들
	@Valid
	private List<SurveyQuestionVO> surveyQuestionList;
	
	private List<SurveyResultVO> surveyResultList;
}
