package com.example.emos.api.db.pojo;

import lombok.Data;

/**
 * @Author: kty
 */
@Data
public class TbMeetingRoom {
    private Integer id;
    private String name;
    private Short max;
    private String desc;
    private Byte status;
}
