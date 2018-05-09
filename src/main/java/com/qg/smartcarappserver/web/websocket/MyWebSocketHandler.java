package com.qg.smartcarappserver.web.websocket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qg.smartcarappserver.config.constant.GlobalConfig;
import com.qg.smartcarappserver.global.cache.OnlineCar;
import com.qg.smartcarappserver.util.FileUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket处理器
 *
 * @author LINhunger
 */
@Component
@Slf4j
public class MyWebSocketHandler implements WebSocketHandler {

    private final Gson gson;

    private static ConcurrentHashMap<String, WebSocketSession> onlineSocket = new ConcurrentHashMap<>();

    @Autowired
    public MyWebSocketHandler(Gson gson) {
        this.gson = gson;
    }

    /**
     * 建立连接后
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String uid = (String) session.getAttributes().get("uid");
        log.info("建立连接 >> : {}", uid);
        boolean flag;
        flag = FileUtil.deleteDir(new File(GlobalConfig.PICTURE_PATH + uid));
        FileUtil.deleteDir(new File(GlobalConfig.PICTURE_PATH + uid));
        FileUtil.deleteDir(new File(GlobalConfig.PICTURE_PATH + uid));
        log.info("清空遗留图像 >> : {}", flag);
        onlineSocket.put(uid, session);
        log.info("连接Socket通道数 >> : {}", onlineSocket.size());
    }

    /**
     * 消息处理，在客户端通过WebSocket API发送的消息会经过这里，然后进行相应的处理
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        log.info("接受信息 >> {}", message.getPayload());
        if (message.getPayloadLength() == 0) {
            return;
        }
        message.getPayload();
        if (message.getPayload().toString().startsWith("@")) {
            return;
        }
        Map<String, Object> request;
        try {
            request = gson.fromJson(message.getPayload().toString(), new TypeToken<Map<String, String>>() {
                    }.getType());
        } catch (Exception e) {
            log.error("json转换异常 >> : {}", session.getId());
            return;
        }
        String carId = (String) request.get("carId");
        String content = (String) request.get("content");
        if (carId == null || content == null) {
            log.error("数据为空异常 >> : {}", session.getId());
            return;
        }
        deliverCommand(carId, content);
    }

    /**
     * 向channel转发消息
     *
     * @param carId   carId
     * @param content 控制指令
     */
    private void deliverCommand(String carId, String content) {
        Channel channel = OnlineCar.getInstance().get(carId);
        if (channel == null) {
            return;
        }
        ChannelFuture future = channel.writeAndFlush(Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
        try {
            future.get();
            log.info("转发信息 >> 小车ID ：{}，信息：{}", carId, content);
        } catch (Exception e) {
            log.error("转发信息异常 >> 小车ID ：{}，信息：{}", carId, content);
            e.printStackTrace();
        }
    }

    /**
     * 消息传输错误处理
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        if (session.isOpen()) {
            try {
                session.close();
            } catch (Exception e) {
                log.error("连接关闭 >> : {}", session.getId());
            }
        }
        // 移除Socket会话
        for (Entry<String, WebSocketSession> entry : onlineSocket.entrySet()) {
            if (entry.getValue().getId().equals(session.getId())) {
                onlineSocket.remove(entry.getKey());
                log.info("Socket会话异常移除:用户ID : {}", entry.getKey());
                break;
            }
        }
    }

    /**
     * 关闭连接后
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        String uid = (String) session.getAttributes().get("uid");
        // 关闭并移除相依channel
        Channel channel = OnlineCar.getInstance().get(uid);
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
        log.info("连接已移除 >> : {}", uid);

        // 移除Socket会话
        for (Entry<String, WebSocketSession> entry : onlineSocket.entrySet()) {
            if (entry.getValue().getId().equals(session.getId())) {
                onlineSocket.remove(entry.getKey());
                log.info("Socket会话已经移除:用户ID : {}", entry.getKey());
                break;
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给某个用户发送消息
     *
     * @param message 信息
     * @throws IOException ioException
     */
    public void sendMessageToUser(String uid, TextMessage message) throws IOException {
        WebSocketSession session = onlineSocket.get(uid);
        if (session != null && session.isOpen()) {
            session.sendMessage(message);
        }
    }
}