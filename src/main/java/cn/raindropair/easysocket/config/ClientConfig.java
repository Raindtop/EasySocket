package cn.raindropair.easysocket.config;

import cn.raindropair.easysocket.client.manager.EasySocketConnectionManager;
import cn.raindropair.easysocket.client.socketclient.EasySocketWebSocketClient;
import cn.raindropair.easysocket.constants.LockCon;
import cn.raindropair.easysocket.constants.MessageBodyCon;
import cn.raindropair.easysocket.constants.BaseCons;
import cn.raindropair.easysocket.holder.WebSessionHolder;
import cn.raindropair.easysocket.message.MessageBody;
import cn.raindropair.easysocket.message.MessageSender;
import cn.raindropair.easysocket.properites.EasySocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableWebSocket
@EnableConfigurationProperties(EasySocketProperties.class)
@ConditionalOnProperty(prefix = EasySocketProperties.PREFIX, name = "runType",
        havingValue = "client")
public class ClientConfig {

    /**
     * 客户端连接器
     * @param easySocketProperties
     * @param webSocketHandler
     * @return
     */
    @Bean
    public EasySocketConnectionManager client(EasySocketProperties easySocketProperties, WebSocketHandler webSocketHandler){
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        Map<String,Object> userProperties = new HashMap<>();
        // 连接超时时间
        userProperties.put(Constants.IO_TIMEOUT_MS_PROPERTY, "300000");
        EasySocketWebSocketClient webSocketClient = new EasySocketWebSocketClient(webSocketContainer);
        webSocketClient.setUserProperties(userProperties);
        EasySocketConnectionManager client = new EasySocketConnectionManager(webSocketClient, webSocketHandler, easySocketProperties.getClientPath());

        client.start();
        return client;
    }

    /**
     * 断线重连
     *
     * @return
     */
    @Bean
    public ScheduledExecutorService reconnectTask(EasySocketConnectionManager client) {
        final Logger logger = LoggerFactory.getLogger(ScheduledExecutorService.class);
        // 创建任务队列
        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(1);
        // 执行任务
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (client.checkIsConnected()){
                logger.info("reconnectScheduledExecutorService client is is running");
                return;
            }

            if (LockCon.CLIENT_RECONNECT_LOCK.tryLock()){
                try{
                    client.stop();
                    client.start();
                    logger.info("reconnectScheduledExecutorService reconnect success");
                }catch (Exception e){
                    logger.error("reconnectScheduledExecutorService reconnect error " + e);
                }finally {
                    LockCon.CLIENT_RECONNECT_LOCK.unlock();
                }
            }else{
                logger.info("reconnectScheduledExecutorService other thread is running");
                return;
            }
        }, 60, 10, TimeUnit.SECONDS);

        return scheduledExecutorService;
    }

    /**
     * 心跳
     *
     * @return
     */
    @Bean
    public ScheduledExecutorService heartBeatsTask(EasySocketConnectionManager client) {
        final Logger logger = LoggerFactory.getLogger(ScheduledExecutorService.class);

        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(1);
        // 执行任务
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (LockCon.CLIENT_HEART_BEATS_LOCK.tryLock()){
                try{
                    for (String key : WebSessionHolder.keysSet()) {
                        try {
                            WebSocketSession webSocketSession = WebSessionHolder.getSession(key);
                            logger.info("Heart Beat session={}", key);
                            if (webSocketSession != null
                                    && webSocketSession.isOpen()) {
                                MessageBody messageBody = MessageBody.ok(BaseCons.BIZ_HEART, BaseCons.PING, key);
                                MessageBody pongResponse = MessageSender.send(messageBody);

                                if (Objects.equals(pongResponse.getCode(), MessageBodyCon.REQUEST_CODE_FAIL)){
                                    log.error("Heart Beat connect fail");
                                    if (BaseCons.TIMEOUT_MSG.equals(pongResponse.getMsg())){
                                        client.stop();
                                        log.info("Heart Beat connect fail");
                                    }
                                }
                            }
                        }catch (Exception e) {
                            logger.error("Heart Beat error", e);
                        }
                    }
                }finally {
                    LockCon.CLIENT_HEART_BEATS_LOCK.unlock();
                }
            }else{
                log.info("Heart Beat connect stop. last thread is running");
            }
        }, 0, 5, TimeUnit.SECONDS);

        return scheduledExecutorService;
    }
}
