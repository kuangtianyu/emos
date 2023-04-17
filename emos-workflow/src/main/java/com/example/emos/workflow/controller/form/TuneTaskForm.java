package com.example.emos.workflow.controller.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Author: kty
 */
@Data
public class TuneTaskForm {

    @NotNull(message = "userId不能为空")
    @Min(value = 1, message = "userId不能小于1")
    private Integer userId;

    @NotNull(message = "assigneeId不能为空")
    @Min(value = 1, message = "assigneeId不能小于1")
    private Integer assigneeId;
}
