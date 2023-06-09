package com.groupc.weather.dto.request.board;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PatchCommentRequestDto2 {

    @NotNull
    private Integer commentNumber;
    @NotBlank
    private String commentContent;
}
