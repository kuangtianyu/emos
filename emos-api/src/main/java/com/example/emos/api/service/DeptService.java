package com.example.emos.api.service;

import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.pojo.TbDept;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Author: kty
 */
public interface DeptService {
    public ArrayList<HashMap> searchAllDept();

    public PageUtils searchDeptByPage(HashMap param);

    public HashMap searchById(int id);

    public int insert(TbDept dept);

    public int update(TbDept dept);

    public int deleteDeptByIds(Integer[] ids);
}
