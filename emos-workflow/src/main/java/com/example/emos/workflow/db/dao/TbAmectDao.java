package com.example.emos.workflow.db.dao;

import com.example.emos.workflow.db.pojo.TbAmect;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: kty
 */
@Mapper
public interface TbAmectDao {
    int insert(TbAmect amect);
}
