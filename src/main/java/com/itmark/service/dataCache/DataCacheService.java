package com.itmark.service.dataCache;

/**
 * @description:
 * @author: MAKUAN
 * @date: 2024/11/21 20:28
 */
public interface DataCacheService {

    /**
     * 保存值
     * @param key
     * @param val
     */
    void saveValue(String key, String val);

    /**
     * 删除值
     * @param key
     */
    void deleteValue(String key);

    /**
     * 获取值
     * @param key
     * @return
     */
    Object selectValueObject(String key);

    /**
     * 获取字符串值
     * @param key
     * @return
     */
    String selectValueString(String key);
}
