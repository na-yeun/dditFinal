package kr.or.ddit.aws.dto;

import java.util.List;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MoveObjectDTO {
	private List<String> moveSelected; // 원본 파일 경로 리스트
	private List<String> targetPaths; // 대상 파일 경로 리스트
}
