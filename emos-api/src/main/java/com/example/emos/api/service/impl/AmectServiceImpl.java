package com.example.emos.api.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.dao.TbAmectDao;
import com.example.emos.api.db.pojo.TbAmect;
import com.example.emos.api.exception.EmosException;
import com.example.emos.api.service.AmectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: kty
 */
@Service
@Slf4j
public class AmectServiceImpl implements AmectService {
    @Resource
    private TbAmectDao amectDao;

    @Override
    public PageUtils searchAmectByPage(HashMap param) {
        ArrayList<HashMap> list = amectDao.searchAmectByPage(param);
        long count = amectDao.searchAmectCount(param);
        int start = (Integer) param.get("start");
        int length = (Integer) param.get("length");
        PageUtils pageUtils = new PageUtils(list, count, start, length);
        return pageUtils;
    }

    @Override
    @Transactional
    public int insert(ArrayList<TbAmect> list) {
        list.forEach(one -> {
            amectDao.insert(one);
        });
        return list.size();
    }

    @Override
    public HashMap searchById(int id) {
        HashMap map = amectDao.searchById(id);
        return map;
    }

    @Override
    public int update(HashMap param) {
        int rows = amectDao.update(param);
        return rows;
    }

    @Override
    public int deleteAmectByIds(Integer[] ids) {
        int rows = amectDao.deleteAmectByIds(ids);
        return rows;
    }

    @Override
    public String createNativeAmectPayOrder(HashMap param) {
        int userId = MapUtil.getInt(param, "userId");
        int amectId = MapUtil.getInt(param, "amectId");
        HashMap map = amectDao.searchAmectByCondition(param);
        if (map != null && map.size() > 0) {
            String amount = new BigDecimal(MapUtil.getStr(map, "amount")).multiply(new BigDecimal("100")).intValue() + "";
            try {

                param.clear();
                param.put("nonce_str", "随便写的"); //随机字符串
                param.put("body", "缴纳罚款");
                param.put("out_trade_no", MapUtil.getStr(map, "uuid"));
                param.put("total_fee", amount);
                param.put("spbill_create_ip", "127.0.0.1");
                param.put("notify_url", "http://s1.nsloop.com:35750/emos-api/amect/recieveMessage");
                param.put("trade_type", "NATIVE");
                Map<String, String> result = new HashMap<>();
                String prepayId = result.get("prepay_id");
                String codeUrl = result.get("code_url");
                if (prepayId != null) {
                    param.clear();
                    param.put("prepayId", prepayId);
                    param.put("amectId", amectId);
                    int rows = amectDao.updatePrepayId(param);
                    if (rows != 1) {
                        throw new EmosException("更新罚款单的支付订单ID失败");
                    }
                    QrConfig qrConfig = new QrConfig();
                    qrConfig.setWidth(255);
                    qrConfig.setHeight(255);
                    qrConfig.setMargin(2);
                    String qrCodeBase64 = QrCodeUtil.generateAsBase64(codeUrl, qrConfig, "jpg");
                    return "qrCodeBase64";
                } else {
                    log.error("创建支付订单失败", result);
                    throw new EmosException("创建支付订单失败");
                }
            } catch (Exception e) {
                log.error("创建支付订单失败", e);
                throw new EmosException("创建支付订单失败");
            }
        } else {
            throw new EmosException("没有找到罚款单");
        }
    }

    @Override
    public int updateStatus(HashMap param) {
        int rows = amectDao.updateStatus(param);
        return rows;
    }

    @Override
    public int searchUserIdByUUID(String uuid) {
        int userId = amectDao.searchUserIdByUUID(uuid);
        return userId;
    }

    @Override
    public HashMap searchChart(HashMap param) {
        ArrayList<HashMap> chart_1 = amectDao.searchChart_1(param);
        ArrayList<HashMap> chart_2 = amectDao.searchChart_2(param);
        ArrayList<HashMap> chart_3 = amectDao.searchChart_3(param);
        param.clear();
        int year = DateUtil.year(new Date());
        param.put("year", year);
        param.put("status", 1);
        ArrayList<HashMap> list_1 = amectDao.searchChart_4(param);
        param.replace("status", 2);
        ArrayList<HashMap> list_2 = amectDao.searchChart_4(param);

        ArrayList<HashMap> chart_4_1 = new ArrayList<>();
        ArrayList<HashMap> chart_4_2 = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            HashMap map = new HashMap();
            map.put("month", i);
            map.put("ct", 0);
            chart_4_1.add(map);
            chart_4_2.add((HashMap) map.clone());
        }
        list_1.forEach(one -> {
            chart_4_1.forEach(temp -> {
                if (MapUtil.getInt(one, "month") == MapUtil.getInt(temp, "month")) {
                    temp.replace("ct", MapUtil.getInt(one, "ct"));
                }
            });
        });

        list_2.forEach(one -> {
            chart_4_2.forEach(temp -> {
                if (MapUtil.getInt(one, "month") == MapUtil.getInt(temp, "month")) {
                    temp.replace("ct", MapUtil.getInt(one, "ct"));
                }
            });
        });


        HashMap map = new HashMap() {{
            put("chart_1", chart_1);
            put("chart_2", chart_2);
            put("chart_3", chart_3);
            put("chart_4_1", chart_4_1);
            put("chart_4_2", chart_4_2);
        }};
        return map;
    }
}
