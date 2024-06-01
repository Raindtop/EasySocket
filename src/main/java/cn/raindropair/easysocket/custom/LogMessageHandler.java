package cn.raindropair.easysocket.custom;

import cn.raindropair.easysocket.constants.BaseCons;
import cn.raindropair.easysocket.handler.MessageHandler;
import cn.raindropair.easysocket.message.MessageBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description 日志消息处理器
 * @Date 2024/4/11
 * @Author raindrop
 */
@Slf4j
@Component(BaseCons.BIZ_LOG)
public class LogMessageHandler extends MessageHandler {

    @Override
    protected Object bizDetal(MessageBody request) {
        log.info("LogMessageHandler MessageBody={}", request.toString());

        return request.getData();
    }
}
