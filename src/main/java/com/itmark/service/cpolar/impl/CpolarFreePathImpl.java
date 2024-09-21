package com.itmark.service.cpolar.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.itmark.constant.CpolarConstant;
import com.itmark.service.cpolar.CpolarFreePath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.HttpCookie;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: MAKUAN
 * @date: 2024/9/14 15:12
 */
@Slf4j
@Service
public class CpolarFreePathImpl implements CpolarFreePath {

    @Value("${dingTalk.open:false}")
    private Boolean sendToDingTalk;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 拼接消息，此处从REDIS中获取
     *
     * @param tunnelMapHttp
     * @param userName
     * @return Map<String,String>
     */
    public Map<String,String> getStringMessageFromMap(Map<String, String> tunnelMapHttp,String userName) {
        Map<String,String> map = new HashMap<>();
        StringBuffer stringBuffer = new StringBuffer();
        Set<String> keySet = tunnelMapHttp.keySet();
        // 有改变
        Integer hasChangeFlag = CpolarConstant.ZERO;
        for (String key : keySet) {
            String onlineUrl = tunnelMapHttp.get(key);
            String redisUrl = stringRedisTemplate.opsForValue().get(CpolarConstant.REDIS_KEY_EXTERNAL_URL + userName+CpolarConstant.SPLIT_HENG+key);
            if (StringUtils.isEmpty(redisUrl)|| StrUtil.equals(onlineUrl,redisUrl)){
                if (StringUtils.isEmpty(redisUrl)){
                    saveNewOnlineUrlToRedis(userName, key, onlineUrl);
                }
                stringBuffer.append("服务：" + key + "，外链：" + onlineUrl +" "+CpolarConstant.FLAG_NOT_CHANGE+ "\n\n");
            }
            if (!StringUtils.isEmpty(onlineUrl)&&!StringUtils.isEmpty(redisUrl)&&!StrUtil.equals(onlineUrl,redisUrl)){
                saveNewOnlineUrlToRedis(userName, key, onlineUrl);
                hasChangeFlag = CpolarConstant.ONE;
                stringBuffer.append("服务：" + key + "，外链：" + onlineUrl +" "+CpolarConstant.FLAG_CHANGE+ "\n\n");
            }
        }
        map.put(CpolarConstant.MAP_KEY_CHANG_FLAG,hasChangeFlag.toString());
        map.put(CpolarConstant.MAP_KEY_MESSAGE,stringBuffer.toString());
        return map;
    }

    /**
     * 设置新外链至Redis
     * @param userName
     * @param key
     * @param onlineUrl
     */
    private void saveNewOnlineUrlToRedis(String userName, String key, String onlineUrl) {
        stringRedisTemplate.opsForValue().set(CpolarConstant.REDIS_KEY_EXTERNAL_URL + userName +CpolarConstant.SPLIT_HENG+ key, onlineUrl);
    }

    /**
     * 发送消息到钉钉
     *
     * @param message
     * @param keyWord
     * @param customerRobotToken
     */
    private static void sendTunnelMapToDingTalk(String message, String keyWord, String customerRobotToken) {
        try {
            // 1 拼接时间戳参数
            Long timestamp = System.currentTimeMillis();
            String secret = keyWord;
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance(CpolarConstant.ALGORITHM_METHOD);
            mac.init(new SecretKeySpec(secret.getBytes(CpolarConstant.UTF_EIGHT), CpolarConstant.ALGORITHM_METHOD));
            byte[] signData = mac.doFinal(stringToSign.getBytes(CpolarConstant.UTF_EIGHT));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), CpolarConstant.UTF_EIGHT);

            // sign字段和timestamp字段必须拼接到请求URL上，否则会出现 310000 的错误信息
            DingTalkClient client = new DefaultDingTalkClient( CpolarConstant.DING_TALk_URL+ sign + "&timestamp=" + timestamp);
            OapiRobotSendRequest req = new OapiRobotSendRequest();
            /**
             * 2 准备发送文本消息
             */
            // 定义文本内容
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            text.setContent(keyWord + "消息：" + "\n\n" + message);
            // 3 定义 @ 对象 可选
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            // at.setAtUserIds(Arrays.asList("cz4-n0h4c6jke"));
            // 设置消息类型
            req.setMsgtype(CpolarConstant.MSG_TYPE);
            req.setText(text);
            req.setAt(at);
            // 4 发送
            OapiRobotSendResponse rsp = client.execute(req, customerRobotToken);
            log.debug("消息发送完毕，响应体为：{}", rsp.getBody());
        } catch (Exception e) {
            log.error("钉钉机器人发送消息失败！", e);
        }
    }

    /**
     * 获取隧道MAP
     *
     * @param statusPage
     * @return
     */
    private static Map<String, String> getTunnelMapByStatusPage(String statusPage, Boolean https) {
        // Parse the HTML
        Document doc = Jsoup.parse(statusPage);

        // Extract the table rows
        Elements rows = doc.select("table tbody tr");

        // Maps to store http and https URLs
        Map<String, String> httpUrlMap = new HashMap<>();

        // Iterate through the rows and extract tunnel names and URLs
        for (Element row : rows) {
            Elements cols = row.select("td");
            String externalUrl = row.select("th").select("a").text();
            if (cols.size() >= 2) {
                // Get the tunnel name
                String tunnelName = cols.get(0).text();
                if (!StringUtils.isEmpty(externalUrl)) {
                    if (externalUrl.startsWith("tcp://")) {
                        httpUrlMap.put(tunnelName, externalUrl);
                    }
                    if (externalUrl.startsWith("http://") && !https) {
                        httpUrlMap.put(tunnelName, externalUrl);
                    }
                    if (externalUrl.startsWith("https://") && https) {
                        httpUrlMap.put(tunnelName, externalUrl);
                    }
                }
            }
        }
        return httpUrlMap;
    }

    /**
     * 获取cookie
     *
     * @param csrfToken
     * @param userName
     * @param password
     * @return
     */
    private List<HttpCookie> getCookieString(String csrfToken, String userName, String password) {
        Map<String, Object> loginMap = new HashMap<>();
        loginMap.put(CpolarConstant.KEY_MAP_LOGIN, userName);
        loginMap.put(CpolarConstant.KEY_MAP_PASSWORD, password);
        loginMap.put(CpolarConstant.KEY_TOKEN, csrfToken);
        String cookieString = stringRedisTemplate.opsForValue().get(CpolarConstant.REDIS_KEY_COOKIE + userName);
        if (StringUtils.isEmpty(cookieString)){
            HttpResponse execute = HttpRequest.post(CpolarConstant.LOGIN_URL).form(loginMap).header(Header.USER_AGENT, CpolarConstant.USER_AGENT_EDGE).timeout(HttpGlobalConfig.getTimeout()).execute();
            List<HttpCookie> cookiesFromReq = execute.getCookies();
            // 59分钟后过期
            stringRedisTemplate.opsForValue().set(CpolarConstant.REDIS_KEY_COOKIE + userName,JSON.toJSONString(cookiesFromReq),59, TimeUnit.MINUTES);
            return cookiesFromReq;
        }
        List<HttpCookie> cookiesFromRedis = JSON.parseObject(cookieString, new TypeReference<List<HttpCookie>>() {
        });
        return cookiesFromRedis;
    }

    /**
     * 获取 csrf_token
     *
     * @param loginPage
     * @return
     */
    private static String getCsrfTokenFromLoginPage(String loginPage) {
        Document parse = Jsoup.parse(loginPage);
        Elements elementsByAttributeValue = parse.getElementsByAttributeValue(CpolarConstant.KEY_NAME, CpolarConstant.KEY_TOKEN);
        if (Objects.nonNull(elementsByAttributeValue)) {
            return elementsByAttributeValue.attr(CpolarConstant.KEY_VALUE);
        }
        return null;
    }

    @Override
    public Map<String, String> getOnLineTunnelMap(String userName, String password) {
        // 1 GET LOGIN PAGE
        String loginPage = HttpRequest.get(CpolarConstant.LOGIN_URL).header(Header.USER_AGENT, CpolarConstant.USER_AGENT_EDGE).execute().body();
        // 2 GET csrf_token FROM PAGE
        String csrfToken = getCsrfTokenFromLoginPage(loginPage);
        // 3 get tunnel
        Map<String, String> tunnelMapHttp = getCookieAndGetTunnelMap(userName, password, csrfToken);
        // 4 retry
        if (CollectionUtil.isEmpty(tunnelMapHttp)){
            tunnelMapHttp = getCookieAndGetTunnelMap(userName, password, csrfToken);
        }
        return tunnelMapHttp;
    }

    /**
     * 优化后
     * @param userName
     * @param password
     * @param csrfToken
     * @return
     */
    private Map<String, String> getCookieAndGetTunnelMap(String userName, String password, String csrfToken) {
        // 3 登陆 获取COOKIE
        List<HttpCookie> cookies = getCookieString(csrfToken, userName, password);
        // 4 请求在线隧道所在页面
        String statusPage = HttpRequest.get(CpolarConstant.STATUS_URL)
                .cookie(cookies).header(Header.USER_AGENT, CpolarConstant.USER_AGENT_EDGE).execute().body();
        // 5 获取隧道MAP
        Map<String, String> tunnelMapHttp = getTunnelMapByStatusPage(statusPage, false);
        if (CollectionUtil.isEmpty(tunnelMapHttp)){
            stringRedisTemplate.delete(CpolarConstant.REDIS_KEY_COOKIE + userName);
        }
        return tunnelMapHttp;
    }

    @Override
    public void sendMsgToDingTalk(String message, String keyWord, String accessToken) {
        sendTunnelMapToDingTalk(message, keyWord, accessToken);
    }

    @Override
    public void getTunnelAndSendMsgToDingTalk(String userName, String password, String robotToken, String keyWord) {
        if (StringUtils.isEmpty(userName)||StringUtils.isEmpty(password)){
            log.warn("CPOLAR用户名密码缺失任务不执行。");
        }
        Map<String, String> tunnelMapHttp = getOnLineTunnelMap(userName, password);
        if (CollectionUtil.isEmpty(tunnelMapHttp)){
            log.warn("消息为空不进行发送。");
        }
        Map<String, String> stringMessageFromMap = getStringMessageFromMap(tunnelMapHttp, userName);
        String message = stringMessageFromMap.get(CpolarConstant.MAP_KEY_MESSAGE);
        String changeFlag = stringMessageFromMap.get(CpolarConstant.MAP_KEY_CHANG_FLAG);
        if (Integer.parseInt(changeFlag)==CpolarConstant.ZERO){
            log.info("当前时间：{}所有服务外链均没有发生改变，不再发送消息。", DateUtil.formatDateTime(new Date()));
        }
        if (sendToDingTalk&&!StringUtils.isEmpty(robotToken)&&Integer.parseInt(changeFlag)==CpolarConstant.ONE){
            sendMsgToDingTalk(message, keyWord, robotToken);
        }
        if (!sendToDingTalk||StringUtils.isEmpty(robotToken)){
            log.info("机器人未开启或者机器人令牌缺失不进行发送：{}", message);
        }
    }

    @Override
    public String getStringMessageFromMap(Map<String, String> tunnelMapHttp) {
        StringBuffer stringBuffer = new StringBuffer();
        Set<String> keySet = tunnelMapHttp.keySet();
        for (String key : keySet) {
            String onlineUrl = tunnelMapHttp.get(key);
            stringBuffer.append("服务：" + key + "，外链：" + onlineUrl + "\n\n");
        }
        return stringBuffer.toString();
    }
}
