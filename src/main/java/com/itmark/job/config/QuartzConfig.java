package com.itmark.job.config;

import com.itmark.job.cpolar.CpolarJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 定义任务描述和具体的执行时间
 * @author: MAKUAN
 * @date: 2024/9/18 22:35
 */
@Configuration
public class QuartzConfig {
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
        // 创建触发器
        return TriggerBuilder.newTrigger()
                // 绑定工作任务
                .forJob(jobDetail())
                // 每隔 小时 执行一次，在项目启动时候会自动执行一次
                .withSchedule(SimpleScheduleBuilder.repeatHourlyForever(1))
                .build();
    }
}