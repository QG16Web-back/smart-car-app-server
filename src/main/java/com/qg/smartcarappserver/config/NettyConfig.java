package com.qg.smartcarappserver.config;

import com.qg.smartcarappserver.socket.GateServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by 小排骨 on 2018/1/8.
 */
@Configuration
@PropertySource("classpath:netty.properties")
public class NettyConfig {

    @Value("${port}")
    private int port;

    /**
     * netty服务器注册
     *
     * @return Object
     */
    @Bean
    public Object gateServer() {
        return new GateServer(port);
    }
}
