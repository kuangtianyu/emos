package com.example.emos.workflow.bpmn;

import com.example.emos.workflow.db.dao.TbLeaveDao;
import com.example.emos.workflow.exception.EmosException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @Author: kty
 */
@Component
public class NotifyLeaveService implements JavaDelegate {
    @Resource
    private HistoryService historyService;

    @Resource
    private TbLeaveDao leaveDao;


    @Override
    public void execute(DelegateExecution delegateExecution) {
        //注意,这里的排序orderByHistoricTaskInstanceEndTime需要指定排序desc(),不然会报错
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery()
                .includeProcessVariables()
                .includeTaskLocalVariables()
                .processInstanceId(delegateExecution.getProcessInstanceId())
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .orderByTaskCreateTime()
                .desc()
                .list()
                .get(0);
        String result = taskInstance.getTaskLocalVariables().get("result").toString();
        delegateExecution.setVariable("result", result);
        String instanceId = delegateExecution.getProcessInstanceId();

        HashMap param = new HashMap() {{
            put("status", "同意".equals(result) ? 3 : 2);
            put("instanceId", instanceId);
        }};

        int rows = leaveDao.updateLeaveStatus(param);
        if (rows != 1) {
            throw new EmosException("更新请假记录状态失败");
        }

    }
}
