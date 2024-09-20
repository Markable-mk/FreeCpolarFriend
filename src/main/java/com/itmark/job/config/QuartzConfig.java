package com.itmark.job.config;

import com.itmark.constant.CpolarConstant;
import com.itmark.enums.QuartzTypeEnum;
import com.itmark.job.cpolar.CpolarJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @description: 定义任务描述和具体的执行时间
 * @author: MAKUAN
 * @date: 2024/9/18 22:35
 */
@Slf4j
@Configuration
public class QuartzConfig {

    @Value("${task-cycle.type:HOUR}")
    private String taskType;

    @Value("${task-cycle.length:1}")
    private Integer length;

    @Value("${task-cycle.cronExpression}")
    private String cronExpression;

    @Bean
    public JobDetail jobDetail() {
        // 指定任务描述具体的实现类
        return JobBuilder.newJob(CpolarJob.class)
                // 指定任务的名称
                .withIdentity("cpolarJob")
                // 任务描述
                .withDescription("获取最新外链")
                // 每次任务执行后进行存储
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger trigger() {
        QuartzTypeEnum quartzTypeEnum = QuartzTypeEnum.getQuartzTypeEnum(taskType);
        SimpleScheduleBuilder simpleScheduleBuilder = null;

        switch (quartzTypeEnum) {
            case HOUR:
                simpleScheduleBuilder = SimpleScheduleBuilder.repeatHourlyForever(length);
                break;
            case MINUTE:
                simpleScheduleBuilder = SimpleScheduleBuilder.repeatMinutelyForever(length);
                break;
            case SECOND:
                simpleScheduleBuilder = SimpleScheduleBuilder.repeatSecondlyForever(length);
                break;
            default:
                // 默认1小时执行
                simpleScheduleBuilder = SimpleScheduleBuilder.repeatHourlyForever(CpolarConstant.ONE);
                break;
        }
        // 优先使用crone表达式触发器
        if (!StringUtils.isEmpty(cronExpression)){
            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("cronTrigger1")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .forJob(jobDetail())
                    .build();
            log.info("Cron表达式：{}，CronTrigger触发器初始化完毕。",cronExpression);
            return cronTrigger;
        }
        // 次用循环触发器
        SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                .withIdentity("simpleTrigger1")
                // 绑定工作任务
                .forJob(jobDetail())
                .withSchedule(simpleScheduleBuilder)
                .build();
        log.info("触发器循环时间类型：{}，时间长度：{}，SimpleTrigger触发器初始化完毕。",taskType, length);
        return simpleTrigger;
    }
}