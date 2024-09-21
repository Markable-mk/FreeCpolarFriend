package com.itmark.entity;

import com.itmark.constant.CpolarConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: MAKUAN
 * @date: 2024/9/21 15:09
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServerExternalPathEntity {
    /**
     * 排序
     */
    private Integer sortNo;
    /**
     * 服务名称
     */
    private String serverName;
    /**
     * 外链
     */
    private String serverUrl;
    /**
     * 是否改变标识 Redis中命中表示改变，否则认为不改变
     */
    private Integer changeTag = CpolarConstant.ZERO;
}
