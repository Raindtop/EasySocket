package cn.raindropair.easysocket.config;

import cn.raindropair.easysocket.distribute.CustomWebSocketHandlerDecorator;
import cn.raindropair.easysocket.handler.MessageHandler;
import cn.raindropair.easysocket.handler.ReceiverMsgHandler;
import cn.raindropair.easysocket.interceptor.EasySocketHandshakeInterceptor;
import cn.raindropair.easysocket.properites.EasySocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
public class CommonConfig implements ServletContextInitializer {


    @Resource
    private Map<String, MessageHandler> messageHandlerMap;

    /**
     * 握手处理
     *
     * @return
     */
    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new EasySocketHandshakeInterceptor();
    }

    /**
     * 创建自身的文本处理器
     */
    @Bean
    @ConditionalOnMissingBean({TextWebSocketHandler.class})
    public WebSocketHandler webSocketHandler() {
        ReceiverMsgHandler serverReceiverMsgHandler = new ReceiverMsgHandler(messageHandlerMap);
        return new CustomWebSocketHandlerDecorator(serverReceiverMsgHandler);
    }

    /**
     * 配置最大接收缓存
     *
     * @param servletContext
     * @throws ServletException
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize", "52428800");
        servletContext.setInitParameter("org.apache.tomcat.websocket.binaryBufferSize", "52428800");
        servletContext.setInitParameter("org.apache.tomcat.websocket.DEFAULT_BUFFER_SIZE", "52428800");
    }
}
