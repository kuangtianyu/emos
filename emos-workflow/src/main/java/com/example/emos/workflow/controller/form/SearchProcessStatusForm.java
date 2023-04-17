package com.example.emos.workflow.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Author: kty
 */
@Data
public class SearchProcessStatusForm {
    @NotBlank(message = "instanceId不能为空")
    private String instanceId;
}
