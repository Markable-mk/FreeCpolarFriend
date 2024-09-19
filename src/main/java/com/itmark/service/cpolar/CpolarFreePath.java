package com.itmark.service.cpolar;

import java.util.Map;

/**
 * @description:
 * @author: MAKUAN
 * @date: 2024/9/14 15:11
 */
public interface CpolarFreePath {

    /**
     * 1 获取隧道map
     * @param userName
     * @param password
     * @return
     */
   Map<String,String> getOnLineTunnelMap(String userName, String password);

    /**
     * 2 发送消息到钉钉
     * @param message 消息txt
     * @param accessToken 机器人TOKEN
     * @param keyWord 关键字
     */
   void sendMsgToDingTalk(String message, String keyWord, String accessToken);

    /**
     * 3 获取隧道并发送
     * @param userName
     * @param password
     * @param accessToken
     * @param keyWord
     */
   void getTunnelAndSendMsgToDingTalk(String userName, String password,String accessToken,String keyWord);

    /**
     * 4 生成消息
     * @param tunnelMapHttp
     * @return
     */
    String getStringMessageFromMap(Map<String,String> tunnelMapHttp);

}
