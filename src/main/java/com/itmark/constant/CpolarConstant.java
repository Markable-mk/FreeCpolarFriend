package com.itmark.constant;

/**
 * @description:
 * @author: MAKUAN
 * @date: 2024/9/18 22:07
 */
public interface CpolarConstant {

    String LOGIN_URL = "https://dashboard.cpolar.com/login";
    String STATUS_URL = "https://dashboard.cpolar.com/status";
    String DING_TALk_URL = "https://oapi.dingtalk.com/robot/send?sign=";
    String KEY_NAME = "name";
    String KEY_TOKEN = "csrf_token";
    String KEY_VALUE = "value";
    String MSG_TYPE = "text";
    String UTF_EIGHT = "UTF-8";
    String ALGORITHM_METHOD = "HmacSHA256";
    String KEY_MAP_LOGIN = "login";
    String KEY_MAP_PASSWORD = "password";
    Integer ONE = 1;
    Integer ZERO = 0;
    String SPLIT_HENG = "-";
    String FLAG_CHANGE = "已改变";
    String FLAG_NOT_CHANGE = "未改变";
    String MAP_KEY_MESSAGE = "MESSAGE";
    String MAP_KEY_CHANG_FLAG = "CHANG_FLAG";
    String CHINA_TIME_ZONE_ID = "Etc/GMT-8";
    /**
     * cookie redisKey
     */
    String REDIS_KEY_COOKIE = "COPLAR_COOKIE_";
    /**
     * 服务外链地址 redisKey
     */
    String REDIS_KEY_EXTERNAL_URL = "REDIS_KEY_EXTERNAL_URL_";

    /**
     * edge浏览器agent
     */
    String USER_AGENT_EDGE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36 Edg/128.0.0.0";

}
