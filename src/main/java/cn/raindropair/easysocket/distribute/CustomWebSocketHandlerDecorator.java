package cn.raindropair.easysocket.distribute;

import cn.raindropair.easysocket.holder.SessionKeyGen;
import cn.raindropair.easysocket.holder.WebSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

/**
 * @description
 * @Date 2024/4/3
 * @Author raindrop
 */
@Slf4j
public class CustomWebSocketHandlerDecorator extends WebSocketHandlerDecorator {

    public CustomWebSocketHandlerDecorator(WebSocketHandler delegate) {
        super(delegate);
    }

    /**
     * websocket 连接时执行的动作
     *
     * @param session websocket session 对象
     * @throws Exception 异常对象
     */
    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        log.info("EastSocket connect success afterConnectionEstablished");
        String sessionKey = SessionKeyGen.genKey(session);
        if (StringUtils.isNotBlank(sessionKey)){
            WebSessionHolder.putSession(sessionKey, session);
        }
    }

    /**
     * websocket 关闭连接时执行的动作
     *
     * @param session     websocket session 对象
     * @param closeStatus 关闭状态对象
     * @throws Exception 异常对象
     */
    @Override
    public void afterConnectionClosed(final WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("EastSocket connect closed afterConnectionClosed");
        String sessionKey = SessionKeyGen.genKey(session);

        if (StringUtils.isNotBlank(sessionKey)){
            WebSessionHolder.putSession(sessionKey, session);
        }
    }
}
