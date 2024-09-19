package com.itmark.enums;

/**
 * @description:
 * @author: MAKUAN
 * @date: 2024/9/19 11:22
 */
public enum QuartzTypeEnum {
    /**
     * 小时
     */
    HOUR,
    /**
     * 分钟
     */
    MINUTE,
    /**
     * 秒
     */
    SECOND;

    /**
     * 根据字符串获取对应的枚举值
     *
     * @param value 输入的字符串值
     * @return 对应的QuartzTypeEnum枚举值，如果没有匹配则返回null
     */
    public static QuartzTypeEnum getQuartzTypeEnum(String value) {
        if (value == null) {
            return null; // 或者抛出异常，视情况而定
        }
        try {
            return QuartzTypeEnum.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 如果传入的字符串不匹配任何枚举值，会抛出IllegalArgumentException
            return null; // 或者抛出你自己定义的异常
        }
    }
}
