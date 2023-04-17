package com.example.emos.api.db.pojo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: kty
 */
@Data
public class TbAmectType {
    private Integer id;
    private String type;
    private BigDecimal money;
    private Boolean systemic;
}
