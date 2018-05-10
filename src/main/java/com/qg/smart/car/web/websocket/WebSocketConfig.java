package com.qg.smart.car.web.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket配置处理器
 *
 * @author 小铭
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CarWebSocketHandler handler;

    @Autowired
    public WebSocketConfig(CarWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws").addInterceptors(new HandShake()).setAllowedOrigins("*");
        registry.addHandler(handler, "/ws/websocket").addInterceptors(new HandShake()).setAllowedOrigins("*").withSockJS();
    }
}
