package cn.raindropair.easysocket.handler;

import cn.raindropair.easysocket.constants.MessageBodyCon;
import cn.raindropair.easysocket.message.MessageBody;
import cn.raindropair.easysocket.message.MessageSender;
import lombok.extern.slf4j.Slf4j;

/**
 * @description 消息处理类
 * @Date 2024/4/11
 * @Author raindrop
 */
@Slf4j
public abstract class MessageHandler {
    /**
     * 处理器
     *
     * @param request
     */
    public void handle(MessageBody request) {
        MessageBody response = MessageBody.ok(request.getBizType(), request.getKey());
        response.setTraceId(request.getTraceId());
        // 设置为返回消息体
        response.setRequestFlag(false);

        try{
            Object data = bizDetal(request);
            response.setData(data);
        }catch (Exception e){
            response.setCode(MessageBodyCon.REQUEST_CODE_FAIL);
            response.setMsg(e.getMessage());
        }

        // 发送请求回复消息
        MessageSender.send(response);
    }

    /**
     * 业务实现
     *
     * @param request  请求
     * @return 返回数据
     */
    protected abstract Object bizDetal(MessageBody request);
}
