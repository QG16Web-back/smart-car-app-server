package com.qg.smartcarappserver.web.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * Socket建立连接（握手）和断开
 *
 * @author LINhunger
 */
@Slf4j
public class HandShake implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            // 标记用户,url“?”后面直接跟要连接的carId
            String connectURI = servletRequest.getURI().toString();
            String uid = connectURI.substring(connectURI.lastIndexOf("?") + 1, connectURI.length());
            if (!"".equals(uid)) {
                attributes.put("uid", uid);
            } else {
                attributes.put("uid", "o1x0C0TjXP62Yn-mqxhVD-mOVAiY");
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {
//		log.info("webscoket 通道开通 >> " );
    }
}