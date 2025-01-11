package com.umc.yourun.apiPayload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "페이징 처리 응답")
public class PageResponse<T> {
    @Schema(description = "컨텐츠 목록")
    private List<T> content;

    @Schema(description = "총 페이지 수")
    private int totalPages;

    @Schema(description = "총 요소 수")
    private long totalElements;
}