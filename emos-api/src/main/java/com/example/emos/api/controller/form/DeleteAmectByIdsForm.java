package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author: kty
 */
@Data
@Schema(description = "删除罚款记录")
public class DeleteAmectByIdsForm {

    @NotEmpty(message = "ids不能为空")
    @Schema(description = "罚款记录的主键")
    private Integer[] ids;
}
