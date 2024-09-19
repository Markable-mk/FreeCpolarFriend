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
    /**
     * cookie redisKey
     */
    String REDIS_KEY_COOKIE = "COPLAR_COOKIE_";

    /**
     * edge浏览器agent
     */
    String USER_AGENT_EDGE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36 Edg/128.0.0.0";

}
