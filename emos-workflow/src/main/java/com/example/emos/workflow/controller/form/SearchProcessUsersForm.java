package com.example.emos.workflow.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: kty
 */
@Data
public class SearchProcessUsersForm {
    @NotBlank(message = "instanceId不能为空")
    private String instanceId;
}
