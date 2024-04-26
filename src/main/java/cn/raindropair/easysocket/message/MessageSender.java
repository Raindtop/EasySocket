package cn.raindropair.easysocket.message;

import cn.raindropair.easysocket.constants.BaseCons;
import cn.raindropair.easysocket.exception.EasySocketException;
import cn.raindropair.easysocket.holder.ResponseHolder;
import cn.raindropair.easysocket.holder.WebSessionHolder;
import cn.raindropair.easysocket.message.encoder.AESEncoder;
import cn.raindropair.easysocket.response.EasySocketResonse;
import cn.raindropair.easysocket.utils.TraceIdGenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Objects;

/**
 * @description 消息发送方
 * @Date 2024/4/3
 * @Author raindrop
 */
@Slf4j
public class MessageSender {

    /**
     * 发送消息
     *
     * @param bizType 业务类型
     * @param data    数据
     * @param key     指定的连接
     */
    public static MessageBody send(String bizType, Object data, String key) {
        MessageBody messageBody = MessageBody.ok(bizType, data, key);
        return send(messageBody);
    }
    /**
     * 发送消息
     *
     * @param bizType 业务类型
     * @param data    数据
     * @param key     指定的连接
     */
    public static MessageBody send(String bizType, Object data, String key, String traceId) {
        MessageBody messageBody = MessageBody.ok(bizType, data, key);
        messageBody.setTraceId(traceId);
        return send(messageBody);
    }

    /**
     * 发送消息 底层方法
     *
     * @param messageBody
     */
    public static MessageBody send(MessageBody messageBody) {
        String key = messageBody.getKey();
        if (StringUtils.isBlank(key)){
            throw new EasySocketException("客户端不能为空");
        }

        // 返回请求不生成traceId
        if (StringUtils.isBlank(messageBody.getTraceId())){
            messageBody.setTraceId(TraceIdGenUtils.traceIdGen());
        }

        WebSocketSession session = WebSessionHolder.getSession(key);
        if (session == null) {
            log.info("EasySocket send msg error. session null");
            messageBody.setMsg("连接地址失败");
            return messageBody;
        }

        if (!session.isOpen()){
            log.info("EasySocket send msg error. session close");
            messageBody.setMsg("连接地址失败");
            return messageBody;
        }

        try {
            session.sendMessage(toTextMsg(messageBody));
        } catch (IOException e) {
            throw new EasySocketException("消息发送失败");
        }

        // 心跳 pong，直接返回成功
        if (BaseCons.BIZ_HEART.equals(messageBody.getBizType())
                && BaseCons.PONG.equals(messageBody.getData())){
            return MessageBody.ok(BaseCons.BIZ_HEART, messageBody.getKey());
        }

        // 返回不处理
        if (!messageBody.isRequestFlag()){
            return MessageBody.ok(BaseCons.BIZ_RESPONSE, messageBody.getKey());
        }

        EasySocketResonse resonse = new EasySocketResonse(messageBody);
        ResponseHolder.put(messageBody.getTraceId(), resonse);
        // 返回Response
        return resonse.get(Objects::nonNull);
    }


    /**
     * 转化为TextMsg
     *
     * @return
     */
    public static TextMessage toTextMsg(MessageBody messageBody) {
        return new TextMessage(AESEncoder.encode(messageBody.toString()));
    }
}
