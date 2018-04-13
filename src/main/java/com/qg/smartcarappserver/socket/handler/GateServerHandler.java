package com.qg.smartcarappserver.socket.handler;

import com.qg.smartcarappserver.config.constant.GlobalConfig;
import com.qg.smartcarappserver.global.cache.OnlineCar;
import com.qg.smartcarappserver.util.FileUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by Dell on 2016/2/1.
 */
@Slf4j
public class GateServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GateServerHandler.class);
    private String carId;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (GlobalConfig.PICTURE_PATH == null) {
            throw new RuntimeException("图片路径未初始化");
        }
        carId = OnlineCar.getInstance().put(ctx.channel());

        LOGGER.info("client has connect. the carId is >> : {}", carId);
        FileUtil.deleteAllFiles(new File(GlobalConfig.PICTURE_PATH + carId));
        FileUtil.createDir(GlobalConfig.PICTURE_PATH + carId);
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        ctx.fireChannelRead(message);
    }


//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
//                .addListener(ChannelFutureListener.CLOSE);
//    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOGGER.info("client has disconnect. the carId is >> : {}", carId);
        OnlineCar.getInstance().remove(carId);
        FileUtil.deleteAllFiles(new File(GlobalConfig.PICTURE_PATH + carId));
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error(cause.getMessage());
        ctx.fireExceptionCaught(cause);
    }


}
