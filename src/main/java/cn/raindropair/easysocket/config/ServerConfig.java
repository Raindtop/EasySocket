package cn.raindropair.easysocket.config;

import cn.raindropair.easysocket.client.manager.EasySocketConnectionManager;
import cn.raindropair.easysocket.client.socketclient.EasySocketWebSocketClient;
import cn.raindropair.easysocket.constants.BaseCons;
import cn.raindropair.easysocket.constants.LockCon;
import cn.raindropair.easysocket.constants.MessageBodyCon;
import cn.raindropair.easysocket.holder.WebSessionHolder;
import cn.raindropair.easysocket.interceptor.EasySocketHandshakeInterceptor;
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
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableWebSocket
@EnableConfigurationProperties(EasySocketProperties.class)
@ConditionalOnProperty(prefix = EasySocketProperties.PREFIX, name = "runType",
        havingValue = "server")
public class ServerConfig {

    /**
     * 连接信息
     *
     * @param handshakeInterceptor
     * @param webSocketHandler
     * @return
     */
    @Bean
    public WebSocketConfigurer webSocketConfigurer(List<HandshakeInterceptor> handshakeInterceptor,
                                                   WebSocketHandler webSocketHandler,
                                                   EasySocketProperties easySocketProperties) {
        return registry -> registry.addHandler(webSocketHandler, easySocketProperties.getPath())
                .setAllowedOrigins(easySocketProperties.getAllowOrigins())
                .addInterceptors(handshakeInterceptor.toArray(new HandshakeInterceptor[0]));
    }
}
