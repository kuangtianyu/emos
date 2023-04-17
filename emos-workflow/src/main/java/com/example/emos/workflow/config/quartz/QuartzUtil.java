package com.example.emos.workflow.config.quartz;


import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author: kty
 */
@Component
@Slf4j
public class QuartzUtil {

    @Resource
    private Scheduler scheduler;

    public void addJob(JobDetail jobDetail, String jobName, String jobGroupName, Date start) {
        try {
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroupName)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                    .startAt(start).build();
            scheduler.scheduleJob(jobDetail, trigger);
            log.debug("成功添加" + jobName + "定时器");
        } catch (SchedulerException e) {
            log.error("定时器添加失败", e);
        }
    }

    public void deleteJob(String jobName, String jobGroupName) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
        try {
            scheduler.resumeTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);
            scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));
            log.debug("成功删除" + jobName + "定时器");
        } catch (SchedulerException e) {
            log.error("定时器删除失败", e);
        }

    }
}
