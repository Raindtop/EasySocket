package cn.raindropair.easysocket.message;

import cn.raindropair.easysocket.exception.EasySocketException;
import org.apache.commons.lang3.StringUtils;

public class WebSocketChannel {
    private String traceId = "";

    /**
     * 设置信道占用
     * @param traceId
     */
    public synchronized void setTraceId(String traceId){
        if (StringUtils.isNotBlank(this.traceId)){
            throw new EasySocketException("信道被占用, 请稍后再试");
        }

        this.traceId = traceId;
    }

    /**
     * 获取信道占用的traceid
     * @return
     */
    public String getTraceId(){
        return traceId;
    }

    /**
     * 移除traceId
     */
    public void removeTraceId(){
        this.traceId = null;
    }
}
