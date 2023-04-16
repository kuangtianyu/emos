package com.example.emos.workflow.bpmn;

import com.example.emos.workflow.db.dao.TbReimDao;
import com.example.emos.workflow.db.dao.TbUserDao;
import com.example.emos.workflow.exception.EmosException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

@Component("notifyReimService")
public class NotifyReimService implements JavaDelegate {

    @Resource
    private HistoryService historyService;

    @Resource
    private TbUserDao userDao;

    @Resource
    private TbReimDao reimDao;

//    @Autowired
//    private EmailTask emailTask;


    @Override
    public void execute(DelegateExecution delegateExecution) {

        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().includeProcessVariables()
                .includeTaskLocalVariables().processInstanceId(delegateExecution.getProcessInstanceId())
                .orderByHistoricTaskInstanceEndTime().desc().list().get(0);
        String result = taskInstance.getTaskLocalVariables().get("result").toString();
        delegateExecution.setVariable("result", result);
        String instanceId = delegateExecution.getProcessInstanceId();
        HashMap param = new HashMap() {{
            put("status", "同意".equals(result) ? 3 : 2);
            put("instanceId", instanceId);
        }};

        int rows = reimDao.updateReimStatus(param);
        if (rows != 1) {
            throw new EmosException("更新报销记录状态失败");
        }

    }
}
