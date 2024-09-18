package com.itmark.job.cpolar;

import com.itmark.constant.CpolarConstant;
import com.itmark.service.cpolar.CpolarFreePath;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;

/**
 * @description:
 * @author: MAKUAN
 * @date: 2024/9/18 22:35
 */
@Slf4j
public class CpolarJob extends QuartzJobBean {

    @Value("${cpolar.userName}")
    private String userName;
    @Value("${cpolar.password}")
    private String password;
    @Value("${dingTalk.robotToken}")
    private String robotToken;

    @Resource
    private CpolarFreePath cpolarFreePath;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.debug("获取到用户：{}，密码：{}，token:{}。",userName,password,robotToken);
        cpolarFreePath.getTunnelAndSendMsgToDingTalk(userName,password,robotToken,CpolarConstant.DING_TALK_KEY_WORD);
    }
}
