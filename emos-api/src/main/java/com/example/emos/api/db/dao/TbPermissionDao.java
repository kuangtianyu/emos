package com.example.emos.api.db.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Author: kty
 */
@Mapper
public interface TbPermissionDao {

    public ArrayList<HashMap> searchAllPermission();
}