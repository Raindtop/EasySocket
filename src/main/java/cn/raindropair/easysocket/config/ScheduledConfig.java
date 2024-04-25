package cn.raindropair.easysocket.config;

import cn.raindropair.easysocket.holder.WebSessionHolder;
import cn.raindropair.easysocket.properites.EasySocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableConfigurationProperties(EasySocketProperties.class)
public class ScheduledConfig {

    /**
     * 意外关闭的session清除 每10分钟
     *
     * @param
     * @return
     */
    @Bean
    public ScheduledExecutorService sessionClean() {
        final Logger logger = LoggerFactory.getLogger(ScheduledExecutorService.class);
        // 创建任务队列
        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(1);
        // 执行任务
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("check session");
            try {
                for (WebSocketSession session : WebSessionHolder.sessionsSet()) {
                    String key = (String) session.getAttributes().get("key");
                    log.info("check session name={}", key);
                    // 链接被关闭
                    if (!session.isOpen()) {
                        log.info("session close name={}", key);
                        session.close();
                        WebSessionHolder.delSession(key);
                    }
                }
            } catch (Exception e) {
                logger.error("traceIdClean error", e);
            }
        }, 0, 5, TimeUnit.MINUTES);

        return scheduledExecutorService;
    }
}
