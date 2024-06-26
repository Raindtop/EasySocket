package cn.raindropair.easysocket.holder;

import cn.raindropair.easysocket.constants.BaseCons;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @description SessionKey生成器
 * @Date 2024/4/3
 * @Author raindrop
 */
public class SessionKeyGen {

    /**
     * key生成器
     *
     * @param webSocketSession
     * @return
     */
    public static String genKey(WebSocketSession webSocketSession) {
        String key = (String) webSocketSession.getAttributes().get(BaseCons.KEY);

        return key;
    }
}
