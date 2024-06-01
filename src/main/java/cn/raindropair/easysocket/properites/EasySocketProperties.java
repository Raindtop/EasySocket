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
     * 运行模式
     * server          服务器模式
     * client          客户端模式
     */
    private String runType = "server";

    /**
     * 客户端模式
     * 连接地址
     */
    private String clientPath = "";

    /**
     * 服务端模式
     * webSocket连接地址
     */
    private String path = "/es-ws";

    /**
     * 服务端模式
     * 允许访问源
     */
    private String allowOrigins = "*";
}
