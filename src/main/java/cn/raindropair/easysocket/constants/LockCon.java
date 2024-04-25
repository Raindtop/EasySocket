package cn.raindropair.easysocket.constants;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description 锁相关常量
 * @Date 2024/4/24
 * @Author raindrop
 */
public class LockCon {
    /**
     * 心跳检测锁
     */
    public static final Lock CLIENT_HEART_BEATS_LOCK = new ReentrantLock(true);

    /**
     * 断线重连锁
     */
    public static final Lock CLIENT_RECONNECT_LOCK = new ReentrantLock(true);
}
