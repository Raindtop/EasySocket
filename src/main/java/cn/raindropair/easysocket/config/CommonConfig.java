package cn.raindropair.easysocket.config;

import cn.raindropair.easysocket.distribute.CustomWebSocketHandlerDecorator;
import cn.raindropair.easysocket.handler.MessageHandler;
import cn.raindropair.easysocket.handler.ReceiverMsgHandler;
import cn.raindropair.easysocket.interceptor.QnzHandshakeInterceptor;
import cn.raindropair.easysocket.properites.EasySocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @description 配置信息
 * @Date 2024/4/3
 * @Author raindrop
 */
@Slf4j
@EnableWebSocket
@EnableConfigurationProperties(EasySocketProperties.class)
@ConditionalOnProperty(prefix = EasySocketProperties.PREFIX, name = "open",
        havingValue = "true")
public class CommonConfig {


    @Resource
    private Map<String, MessageHandler> messageHandlerMap;

    /**
     * 握手处理
     *
     * @return
     */
    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new QnzHandshakeInterceptor();
    }

    /**
     * @param handshakeInterceptor
     * @param webSocketHandler
     * @return
     */
    @Bean
    public WebSocketConfigurer webSocketConfigurer(List<HandshakeInterceptor> handshakeInterceptor,
                                                   WebSocketHandler webSocketHandler,
                                                   EasySocketProperties easySocketProperties) {
        HandshakeInterceptor bizInterceptor = new QnzHandshakeInterceptor();
        return registry -> registry.addHandler(webSocketHandler, easySocketProperties.getPath())
                .setAllowedOrigins(easySocketProperties.getAllowOrigins())
                .addInterceptors(handshakeInterceptor.toArray(new HandshakeInterceptor[0]));
    }

    /**
     * 创建自身的文本处理器
     */
    @Bean
    @ConditionalOnMissingBean({TextWebSocketHandler.class})
    public WebSocketHandler clientReceiverMsgHandler() {
        ReceiverMsgHandler serverReceiverMsgHandler = new ReceiverMsgHandler(messageHandlerMap);
        return new CustomWebSocketHandlerDecorator(serverReceiverMsgHandler);
    }
}
