package cn.raindropair.easysocket.interceptor;

import cn.raindropair.easysocket.constants.BaseCons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.ServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * @description 千年舟认证方式
 * @Date 2024/4/3
 * @Author raindrop
 */
@Slf4j
public class QnzHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * 握手认证
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> attributes) throws Exception {
        ServletRequest request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();

        log.info("EasySocket create connection beforeHandshake");
        String userName = request.getParameter(BaseCons.REQUEST_ATTR_USERNAME);
        String password = request.getParameter(BaseCons.REQUEST_ATTR_PASSWORD);

        // 认证成功
        if (Objects.equals(password, "zhs135")) {
            attributes.put(BaseCons.KEY, userName);
            log.info("EasySocket create connection auth success");
            return true;
        }

        // 未认证
        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        serverHttpResponse.flush();

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("EasySocket create connection afterHandshake");
    }
}
