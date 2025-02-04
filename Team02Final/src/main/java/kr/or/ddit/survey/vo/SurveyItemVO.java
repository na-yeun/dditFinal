package kr.or.ddit.survey.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.commons.validate.UpdateGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(of = {"suritemNo", "surquesNo", "sboardNo"})
@NoArgsConstructor
public class SurveyItemVO {
	private String suritemNo;
	private String surquesNo;
	private String sboardNo;
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class})
	@Size(min=1, max = 1000, message = "입력 값은 최소 1글자에서 최대 300글자까지 가능합니다.")
	private String suritemContent;
	private Integer suritemIndex;
	
}
