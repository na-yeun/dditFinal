package kr.or.ddit.survey.vo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.commons.validate.UpdateGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(of = {"surquesNo","sboardNo"})
@NoArgsConstructor
public class SurveyQuestionVO {
	private String surquesNo;
	private String sboardNo;
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class})
	@Size(min=1, max = 1000, message = "입력 값은 최소 1글자에서 최대 300글자까지 가능합니다.")
	private String surquesContent;
	private String surquesDupleyn;
	@NotNull(groups = {InsertGroup.class, UpdateGroup.class})
	private Long surquesOrder;
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class})
	private String surquesType;
	
	// 질문 하나에 아이템(답변 할 수 있는 항목) 여러개
	@Valid
	private List<SurveyItemVO> surveyItemList;
}
