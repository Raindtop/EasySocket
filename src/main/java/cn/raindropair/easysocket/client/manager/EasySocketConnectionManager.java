package cn.raindropair.easysocket.client.manager;

import cn.raindropair.easysocket.holder.WebSessionHolder;
import org.springframework.context.Lifecycle;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.ConnectionManagerSupport;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.handler.LoggingWebSocketHandlerDecorator;

import java.util.List;

/**
 * @description client客户端连接管理器，代码copy WebSocketConnectionManager，增加了判断连接是否可用的方法，用于实现断线重连功能。
 * @Author raindrop
 * @Date 2024/4/26
 */
public class EasySocketConnectionManager extends ConnectionManagerSupport {

    private final WebSocketClient client;

    private final WebSocketHandler webSocketHandler;

    @Nullable
    private WebSocketSession webSocketSession;

    private WebSocketHttpHeaders headers = new WebSocketHttpHeaders();


    public EasySocketConnectionManager(WebSocketClient client,
                                       WebSocketHandler webSocketHandler, String uriTemplate) {

        super(uriTemplate);
        this.client = client;
        this.webSocketHandler = decorateWebSocketHandler(webSocketHandler);
    }


    /**
     * Decorate the WebSocketHandler provided to the class constructor.
     * <p>By default {@link LoggingWebSocketHandlerDecorator} is added.
     */
    protected WebSocketHandler decorateWebSocketHandler(WebSocketHandler handler) {
        return new LoggingWebSocketHandlerDecorator(handler);
    }

    /**
     * Set the sub-protocols to use. If configured, specified sub-protocols will be
     * requested in the handshake through the {@code Sec-WebSocket-Protocol} header. The
     * resulting WebSocket session will contain the protocol accepted by the server, if
     * any.
     */
    public void setSubProtocols(List<String> protocols) {
        this.headers.setSecWebSocketProtocol(protocols);
    }

    /**
     * Return the configured sub-protocols to use.
     */
    public List<String> getSubProtocols() {
        return this.headers.getSecWebSocketProtocol();
    }

    /**
     * Set the origin to use.
     */
    public void setOrigin(@Nullable String origin) {
        this.headers.setOrigin(origin);
    }

    /**
     * Return the configured origin.
     */
    @Nullable
    public String getOrigin() {
        return this.headers.getOrigin();
    }

    /**
     * Provide default headers to add to the WebSocket handshake request.
     */
    public void setHeaders(HttpHeaders headers) {
        this.headers.clear();
        this.headers.putAll(headers);
    }

    /**
     * Return the default headers for the WebSocket handshake request.
     */
    public HttpHeaders getHeaders() {
        return this.headers;
    }


    @Override
    public void startInternal() {
        if (this.client instanceof Lifecycle && !((Lifecycle) this.client).isRunning()) {
            ((Lifecycle) this.client).start();
        }
        super.startInternal();
    }

    @Override
    public void stopInternal() throws Exception {
        if (this.client instanceof Lifecycle && ((Lifecycle) this.client).isRunning()) {
            ((Lifecycle) this.client).stop();
        }
        super.stopInternal();
    }

    @Override
    protected void openConnection() {
        if (logger.isInfoEnabled()) {
            logger.info("Connecting to WebSocket at " + getUri());
        }

        ListenableFuture<WebSocketSession> future =
                this.client.doHandshake(this.webSocketHandler, this.headers, getUri());

        future.addCallback(new ListenableFutureCallback<WebSocketSession>() {
            @Override
            public void onSuccess(@Nullable WebSocketSession result) {
                webSocketSession = result;
                logger.info("Successfully connected");
            }

            @Override
            public void onFailure(Throwable ex) {
                logger.error("Failed to connect", ex);
            }
        });
    }

    @Override
    protected void closeConnection() throws Exception {
        if (this.webSocketSession != null) {
            this.webSocketSession.close();
        }
    }

    @Override
    protected boolean isConnected() {
        return (this.webSocketSession != null && this.webSocketSession.isOpen());
    }

    public boolean checkIsConnected() {
        return (this.webSocketSession != null && this.webSocketSession.isOpen());
    }
}