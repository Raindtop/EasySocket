package cn.raindropair.easysocket.handler;

import cn.raindropair.easysocket.constants.BaseCons;
import cn.raindropair.easysocket.holder.ResponseHolder;
import cn.raindropair.easysocket.message.MessageBody;
import cn.raindropair.easysocket.message.MessageSender;
import cn.raindropair.easysocket.message.encoder.AESEncoder;
import cn.raindropair.easysocket.response.EasySocketResonse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

/**
 * @description 文字信息处理
 * @Date 2024/4/3
 * @Author raindrop
 */
@Slf4j
@RequiredArgsConstructor
public class ReceiverMsgHandler extends TextWebSocketHandler {
    private final Map<String, MessageHandler> messageHandlerMap;

    @Override
    public void handleTextMessage(WebSocketSession wsSession, TextMessage message) {
        String msg = message.getPayload();
        log.info("EasySocket receiver Msg");

        MessageBody messageBody;
        try {
            messageBody = MessageBody.conver(AESEncoder.decode(msg));
            // 心跳检测
            if (messageBody.getBizType().equals(BaseCons.BIZ_HEART)) {
                // 收到ping消息，返回pong
                if (BaseCons.PING.equals(messageBody.getData())){
                    log.info("EasySocket receiver ping Key={}, response pong", messageBody.getKey());
                    MessageSender.send(BaseCons.BIZ_HEART, BaseCons.PONG, messageBody.getKey(), messageBody.getTraceId());
                    return;
                }else if (BaseCons.PONG.equals(messageBody.getData())){
                    EasySocketResonse resonse = ResponseHolder.get(messageBody.getTraceId());

                    // 请求超时或者其他情况
                    if (resonse == null) {
                        log.info("EasySocket receiver error response not exists");
                        return;
                    }

                    // 设置请求返回 守护线程查询到有数据之后会自动返回
                    resonse.setResponse(messageBody);
                    log.info("EasySocket receiver pong Key={}", messageBody.getKey());
                    return ;
                }
            }
            log.info("EasySocket receiver messageBody={}", messageBody);

            // 收到响应消息
            if (!messageBody.isRequestFlag()){
                EasySocketResonse resonse = ResponseHolder.get(messageBody.getTraceId());

                // 请求超时或者其他情况
                if (resonse == null) {
                    log.info("EasySocket receiver error response not exists");
                    return;
                }

                // 设置请求返回 守护线程查询到有数据之后会自动返回
                resonse.setResponse(messageBody);

                return ;
            }

            // 收到请求消息
            if (messageBody.isRequestFlag()){
                MessageHandler messageHandler = messageHandlerMap.get(messageBody.getBizType());

                if (messageHandler == null){
                    log.info("EasySocket receiver do not match biz. ");
                }

                messageHandler.handle(messageBody);
            }

        }catch (Exception e) {
            log.error("EasySocket receiver error," + e);
        }
    }
}
