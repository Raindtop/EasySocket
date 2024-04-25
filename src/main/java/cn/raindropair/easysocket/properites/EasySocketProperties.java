package cn.raindropair.easysocket.properites;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description 配置信息
 * @Date 2024/4/3
 * @Author raindrop
 */
@Data
@ConfigurationProperties(EasySocketProperties.PREFIX)
public class EasySocketProperties {
    public static final String PREFIX = "easysocket";
    /**
     * 是否开启WebSocket
     */
    private boolean open = false;

    /**
     * 允许状态
     * server          服务器模式
     * client          客户端模式
     */
    private String runType = "server";

    // ------------------------ client Info
    private String clientPath = "";
    // ------------------------ client Info


    // ------------------------ server Info

    /**
     * 路径
     */
    private String path = "/es-ws";

    /**
     * 允许访问源
     */
    private String allowOrigins = "*";
}
