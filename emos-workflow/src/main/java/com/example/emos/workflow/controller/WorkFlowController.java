package com.example.emos.workflow.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.workflow.controller.form.*;
import com.example.emos.workflow.service.WorkflowService;
import com.example.emos.workflow.util.R;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流项目的api接口
 */
@Slf4j
@RestController
@RequestMapping("/workflow")
public class WorkFlowController {

    @Resource
    private WorkflowService workflowService;

    @Resource
    private ProcessEngine processEngine;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private HistoryService historyService;

    /**
     * 启动会议室申请流程
     */
    @PostMapping("/startMeetingProcess")
    public R startMeetingProcess(@Valid @RequestBody StartMeetingProcessForm form) {
        log.info("工作流，启动会议申请流程，接受请求数据{}", JSONUtil.toJsonPrettyStr(form));
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        param.put("filing", false);
        param.put("type", "会议申请");
        param.put("createDate", DateUtil.today());

        param.remove("code");
        if (form.getGmId() == null) {
            param.put("identity", "总经理");
            param.put("result", "同意");

        } else {
            param.put("identity", "员工");
        }

        String instanceId = workflowService.startMeetingProcess(param);
        return R.ok().put("instanceId", instanceId);

    }

    /**
     * 启动请假流程
     */
    @PostMapping("/startLeaveProcess")
    public R startLeaveProcess(@Valid @RequestBody StartLeaveProcessForm form) {
        log.info("工作流，启动请假流程，接受请求数据{}", JSONUtil.toJsonPrettyStr(form));
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        param.put("filing", false);
        param.put("type", "员工请假");
        param.put("createDate", DateUtil.today());
        param.remove("code");
        String instanceId = workflowService.startLeaveProcess(param);
        return R.ok().put("instanceId", instanceId);
    }

    /**
     * 启动报销流程
     */
    @PostMapping("/startReimProcess")
    public R startReimProcess(@Valid @RequestBody StartReimProcessForm form) {
        log.info("工作流，启动报销流程，接受请求数据{}", JSONUtil.toJsonPrettyStr(form));
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        param.put("filing", false);
        param.put("type", "报销申请");
        param.put("createDate", DateUtil.today());
        param.remove("code");
        String instanceId = workflowService.startReimProcess(param);
        return R.ok().put("instanceId", instanceId);
    }


    /**
     * 审批任务
     */
    @PostMapping("/approvalTask")
    public R approvalTask(@Valid @RequestBody ApprovalTaskForm form) {
        log.info("工作流，审批任务，接受请求数据{}", JSONUtil.toJsonPrettyStr(form));
        HashMap param = new HashMap();
        param.put("taskId", form.getTaskId());
        param.put("approval", form.getApproval());
        workflowService.approvalTask(param);
        return R.ok();
    }

    /**
     * 文件归档
     */
    @PostMapping("/archiveTask")
    public R archiveTask(@Valid @RequestBody ArchiveTaskForm form) {
        log.info("工作流，文件归档接收请求参数为：{}", JSONUtil.toJsonStr(form));
        HashMap<String, Object> param = new HashMap<String, Object>() {{
            put("taskId", form.getTaskId());
            put("userId", form.getUserId());
            put("files", JSONUtil.parseArray(form.getFiles()));
        }};
        workflowService.archiveTask(param);
        return R.ok();
    }


    @PostMapping("/searchProcessStatus")
    public R searchProcessStatus(@Valid @RequestBody SearchProcessStatusForm form) {
        log.info("工作流，查询任务是否审批完成，接受请求数据{}", JSONUtil.toJsonPrettyStr(form));
        boolean bool = workflowService.searchProcessStatus(form.getInstanceId());
        if (bool == false) {
            //工作流未结束
            return R.ok().put("result", "未结束");
        } else {
            //工作流已经结束
            return R.ok().put("result", "已结束");
        }
    }


    @PostMapping("/deleteProcessById")
    public R deleteProcessById(@Valid @RequestBody DeleteProcessByIdForm form) {
        log.info("工作流，删除流程，接受请求数据{}", JSONUtil.toJsonPrettyStr(form));
        workflowService.deleteProcessById(form.getUuid(), form.getInstanceId(), form.getType(), form.getReason());
        return R.ok();

    }


    @PostMapping("/searchProcessUsers")
    public R searchProcessUsers(@Valid @RequestBody SearchProcessUsersForm form) {
        log.info("工作流，查询审批人列表，接受请求数据{}", JSONUtil.toJsonPrettyStr(form));
        ArrayList list = workflowService.searchProcessUsers(form.getInstanceId());
        return R.ok().put("result", list);
    }

    @PostMapping("/searchTaskByPage")
    public R searchTaskByPage(@Valid @RequestBody SearchTaskByPageForm form) {
        log.info("工作流，分页查询审批任务，接受请求数据{}", JSONUtil.toJsonPrettyStr(form));
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        param.remove("page");
        param.put("start", start);
        HashMap map = workflowService.searchTaskByPage(param);
        return R.ok().put("page", map);
    }

    @PostMapping("/searchApprovalContent")
    public R searchApprovalContent(@Valid @RequestBody SearchApprovalContentForm form) {
        log.info("工作流，查询审批意见，接受请求数据{}", JSONUtil.toJsonPrettyStr(form));
        HashMap map = workflowService.searchApprovalContent(form.getInstanceId(), form.getUserId(), form.getRole(), form.getType(), form.getStatus());
        return R.ok().put("content", map);
    }

    /**
     * 查询bmpn图，根据实例id查询任务，根据任务查询流程定义id
     */
    @PostMapping("/searchApprovalBpmn")
    public void searchApprovalBpmn(@Valid @RequestBody SearchApprovalBpmnForm form, HttpServletResponse response) {
        log.info("工作流，查询Bpmn图，接受请求数据{}", JSONUtil.toJsonPrettyStr(form));
        response.setContentType("image/jpg");
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(form.getInstanceId()).singleResult();
        BpmnModel bpmnModel;
        List activeActivityIds;
        if (task != null) {
            bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
            activeActivityIds = runtimeService.getActiveActivityIds(task.getExecutionId());
        } else {
            HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(form.getInstanceId()).list().get(0);
            bpmnModel = repositoryService.getBpmnModel(taskInstance.getProcessDefinitionId());
            activeActivityIds = new ArrayList<>();
        }

        Map map = bpmnModel.getItemDefinitions();
        DefaultProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
        String os = System.getProperty("os.name").toLowerCase();
        String font = "SimSun";
        if (os.startsWith("win")) {
            font = "宋体";
        }
        try (InputStream in = diagramGenerator.generateDiagram(
                bpmnModel,
                "jpg",
                activeActivityIds,
                activeActivityIds,
                font,
                font,
                font,
                processEngine.getProcessEngineConfiguration().getProcessEngineConfiguration().getClassLoader(),
                1.0);
             BufferedInputStream bin = new BufferedInputStream(in);
             OutputStream out = response.getOutputStream();
             BufferedOutputStream bout = new BufferedOutputStream(out);
        ) {
            IOUtils.copy(bin, bout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}