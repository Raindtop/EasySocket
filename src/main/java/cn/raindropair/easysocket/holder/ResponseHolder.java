package cn.raindropair.easysocket.holder;

import cn.raindropair.easysocket.response.EasySocketResonse;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description 返回持有装置
 * @Date 2024/4/11
 * @Author raindrop
 */
public class ResponseHolder {
    private static final ConcurrentHashMap<String, EasySocketResonse> RESPONSE_MAP = new ConcurrentHashMap<>();

    /**
     * 存储返回处理器
     *
     * @param traceId
     */
    public static void put(String traceId, EasySocketResonse easySocketResonse) {
        RESPONSE_MAP.put(traceId, easySocketResonse);
    }

    /**
     * 获取等待请求返回的信息
     *
     * @param traceId
     * @return
     */
    public static EasySocketResonse get(String traceId) {
        return RESPONSE_MAP.get(traceId);
    }

    /**
     * 移除处理器
     *
     * @param traceId
     */
    public static void remove(String traceId) {
        RESPONSE_MAP.remove(traceId);
    }
}
