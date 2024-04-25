package cn.raindropair.easysocket.response;

import cn.raindropair.easysocket.constants.BaseCons;
import cn.raindropair.easysocket.exception.EasySocketException;
import cn.raindropair.easysocket.holder.ResponseHolder;
import cn.raindropair.easysocket.holder.WebSessionHolder;
import cn.raindropair.easysocket.message.MessageBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

/**
 * @description 请求等待装置
 * 使用Guarded Suspension模式实现请求等待
 * @Date 2024/4/11
 * @Author raindrop
 */
@Slf4j
public class EasySocketResonse {

    // 请求内容
    private MessageBody request;

    // 返回体
    private MessageBody response;

    private final Lock lock = new ReentrantLock();

    private final Condition done = lock.newCondition();

    /**
     * 请求等待时间
     */
    private static Integer executeTimes = 10;

    /**
     * 设置等待时间 秒
     *
     * @param time
     */
    public void setWairForResponseTime(Long time) {
        executeTimes = time.intValue() * 2;
    }

    /**
     * 初始化信息内容
     *
     * @param request
     */
    public EasySocketResonse(MessageBody request) {
        this.request = request;
    }

    /**
     * 获取返回
     *
     * @param p
     * @return
     */
    public MessageBody get(Predicate<MessageBody> p) {
        lock.lock();
        try {
            // 等待时间
            int i = 0;
            while (!p.test(response)) {
                // 达到等待时间
                if (i == executeTimes) {
                    response = MessageBody.failed(request.getBizType(), BaseCons.TIMEOUT_MSG, request.getKey());
                    response.setTraceId(request.getTraceId());

                    throw new EasySocketException(BaseCons.TIMEOUT_MSG);
                }
                done.await(500, TimeUnit.MILLISECONDS);
                i++;
            }
        } catch (EasySocketException e) {
            if (BaseCons.TIMEOUT_MSG.equals(e.getMsg())){
                // 心跳检测不通过，关闭session
                if (BaseCons.BIZ_HEART.equals(request.getBizType())){
                    WebSocketSession webSocketSession = WebSessionHolder.getSession(request.getKey());
                    try {
                        webSocketSession.close();
                        WebSessionHolder.delSession(request.getKey());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 移除traceId信息
            ResponseHolder.remove(request.getTraceId());
            lock.unlock();
        }
        return response;
    }

    /**
     * 设置返回体
     *
     * @param response
     */
    public void setResponse(MessageBody response) {
        lock.lock();
        try {
            this.response = response;
            done.signal();
        } finally {
            lock.unlock();
        }
    }
}


