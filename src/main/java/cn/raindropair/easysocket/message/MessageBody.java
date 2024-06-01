package cn.raindropair.easysocket.message;

import cn.raindropair.easysocket.constants.MessageBodyCon;
import cn.raindropair.easysocket.exception.EasySocketException;
import cn.raindropair.easysocket.serialize.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

@Data
@AllArgsConstructor
public class MessageBody {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper() {{
        setSerializationInclusion(JsonInclude.Include.NON_NULL);
        registerModule(new JavaTimeModule());
    }};

    /**
     * 请求状态 0-正常 1-异常
     * {@link MessageBodyCon}
     */
    private Integer code;

    /**
     * 请求Key
     */
    private String key;

    /**
     * 消息信息
     */
    private String msg;

    /**
     * 数据
     */
    private Object data;

    /**
     * 唯一请求码
     */
    private String traceId;

    /**
     * 业务类型 自定义字段
     */
    private String bizType;

    /**
     * 消息时间戳
     */
    private long msgTimeStamp;

    /**
     * 请求标记位
     */
    private boolean requestFlag = true;

    public static MessageBody ok(String bizType, String key) {
        return restResult(bizType, null, MessageBodyCon.REQUEST_CODE_SUCCESS, null, key);
    }

    public static MessageBody ok(String bizType, Object data, String key) {
        return restResult(bizType, data, MessageBodyCon.REQUEST_CODE_SUCCESS, null, key);
    }

    public static MessageBody ok(String bizType, Object data, String msg, String key) {
        return restResult(bizType, data, MessageBodyCon.REQUEST_CODE_SUCCESS, msg, key);
    }

    public static MessageBody failed(String bizType, String key) {
        return restResult(bizType, null, MessageBodyCon.REQUEST_CODE_FAIL, null, key);
    }

    public static MessageBody failed(String bizType, String msg, String key) {
        return restResult(bizType, null, MessageBodyCon.REQUEST_CODE_FAIL, msg, key);
    }

    public static MessageBody failed(String bizType, Object data, String key) {
        return restResult(bizType, data, MessageBodyCon.REQUEST_CODE_FAIL, null, key);
    }

    public static MessageBody failed(String bizType, Object data, String msg, String key) {
        return restResult(bizType, data, MessageBodyCon.REQUEST_CODE_FAIL, msg, key);
    }

    private static MessageBody restResult(String bizType, Object data, int code, String msg, String key) {
        MessageBody messageBody = new MessageBody();
        messageBody.setBizType(bizType);
        messageBody.setCode(code);
        messageBody.setMsg(msg);
        messageBody.setMsgTimeStamp(System.currentTimeMillis());
        messageBody.setKey(key);
        messageBody.setData(data);
        return messageBody;
    }

    @Override
    public String toString() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (Exception e) {
            throw new EasySocketException("Data 粘贴异常");
        }
    }

    /**
     * 将String 转为消息体
     *
     * @param jsonMessageBody
     * @return
     */
    @SneakyThrows
    public static MessageBody conver(String jsonMessageBody) {
        return OBJECT_MAPPER.readValue(jsonMessageBody, MessageBody.class);
    }

    @SneakyThrows
    public <T> T getData(Class<T> valueType) {
        return OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(this.data), valueType);
    }

    public MessageBody() {

    }
}
