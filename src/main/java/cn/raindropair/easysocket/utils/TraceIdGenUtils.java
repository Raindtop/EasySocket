package cn.raindropair.easysocket.utils;

import java.util.UUID;

/**
 * @description 请求等待数据池
 * @Date 2024/4/8
 * @Author raindrop
 */
public class TraceIdGenUtils {
    /**
     * TraceId生成器
     *
     * @return
     */
    public static String traceIdGen() {
        String traceId = UUID.randomUUID().toString();
        return traceId;
    }
}
