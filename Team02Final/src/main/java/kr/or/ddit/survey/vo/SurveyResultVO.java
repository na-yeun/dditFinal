package kr.or.ddit.survey.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import kr.or.ddit.commons.validate.InsertGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class SurveyResultVO {
	@NotBlank(groups = InsertGroup.class)
	private String suritemNo; // 선택한 번호
	@NotBlank(groups = InsertGroup.class)
	private String surquesNo; // 질문(문항)의 번호
	@NotBlank(groups = InsertGroup.class)
	private String sboardNo; // 해당 게시판 번호
	@NotBlank(groups = InsertGroup.class)
	private String empId; // 응답자
	@Size(max=650, message = "글자수는 최대 650자까지 가능합니다.")
	private String resContent; // 비고
	
	private String surquesType;
	private String suritemContent;
	private Integer suritemIndex;
	private String totalResult;
	private Long totalCount;
	private Long participatedCount;
}
