package com.qg.smart.car.socket.handler;

import com.qg.smart.car.global.cache.OnlineCar;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Dell
 * Date 2016/2/1
 */
@Slf4j
public class GateServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 小车ID.
     */
    private String carId;

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        // 生成一个carId并绑定连接通道
        carId = OnlineCar.getInstance().put(ctx.channel());
        log.info("client has connect. the carId is >> : {}", carId);
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object message) {
        ctx.fireChannelRead(message);
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        log.info("client has disconnect. the carId is >> : {}", carId);
        OnlineCar.getInstance().remove(carId);
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        log.error(cause.getMessage());
        ctx.fireExceptionCaught(cause);
    }
}
