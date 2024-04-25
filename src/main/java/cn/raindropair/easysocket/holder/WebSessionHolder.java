package cn.raindropair.easysocket.holder;

import cn.raindropair.easysocket.exception.EasySocketException;
import cn.raindropair.easysocket.message.WebSocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description 标准session管理器
 * @Date 2024/4/3
 * @Author raindrop
 */
public class WebSessionHolder {
    // Socket Session 持有器
    public static final ConcurrentHashMap<String, WebSocketSession> SESSION_MAP = new ConcurrentHashMap<>();
    // Socket Session 信道持有器
    // TODO 单工模式预留，同一时刻只会有一个请求
    // TODO 默认为双工模式，类似netty
    public static final ConcurrentHashMap<String, WebSocketChannel> SESSION_CHANNEL_MAP = new ConcurrentHashMap<>();


    public static boolean putSession(String key, WebSocketSession webSocketSession) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        SESSION_MAP.put(key, webSocketSession);
        SESSION_CHANNEL_MAP.put(key, new WebSocketChannel());
        return true;
    }

    public static WebSocketSession getSession(String key) {
        return SESSION_MAP.get(key);
    }

    public static WebSocketChannel getSessionChannel(String key) {
        return SESSION_CHANNEL_MAP.get(key);
    }

    /**
     * 占用信道
     *
     * @param key
     * @param traceId
     */
    public static void occupyChannel(String key, String traceId) {
        WebSocketChannel webSocketChannel = getSessionChannel(key);

        if (webSocketChannel == null) {
            throw new EasySocketException("连接不存在，occupyChannel");
        }

        webSocketChannel.setTraceId(traceId);
    }

    /**
     * 释放信道资源
     *
     * @param key
     * @param traceId
     */
    public static void releaseChannel(String key, String traceId) {
        WebSocketChannel webSocketChannel = getSessionChannel(key);

        if (webSocketChannel == null) {
            throw new EasySocketException("连接不存在，occupyChannel");
        }

        webSocketChannel.setTraceId(traceId);
    }

    public static boolean delSession(String key) {
        if (StringUtils.isBlank(key)) {
            return true;
        }
        SESSION_MAP.remove(key);
        SESSION_CHANNEL_MAP.remove(key);
        return false;
    }

    public static Set<String> keysSet() {
        return SESSION_MAP.keySet();
    }

    public static Set<WebSocketSession> sessionsSet() {
        return new HashSet<>(SESSION_MAP.values());
    }
}
