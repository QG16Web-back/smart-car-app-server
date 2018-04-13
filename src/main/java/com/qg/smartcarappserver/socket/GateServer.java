package com.qg.smartcarappserver.socket;

import com.qg.smartcarappserver.socket.handler.GateServerHandler;
import com.qg.smartcarappserver.socket.handler.PictureCollectHandler;
import com.qg.smartcarappserver.util.TransUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by hunger on 2017/8/3.
 */
@Slf4j
public class GateServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(GateServer.class);

    public GateServer(int port) {
        startGateServer(port);
//        log.info("netty 服务器已启动");
    }

    /**
     * 配置服务器
     *
     * @param port 端口号
     */
    private static void startGateServer(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        ChannelPipeline pipeline = channel.pipeline();
                        ByteBuf delimiter = Unpooled.copiedBuffer(TransUtil.toByteArray("FFD9"));
                        pipeline.addLast("DelimiterBasedFrameDecoder", new DelimiterBasedFrameDecoder(100 * 1024, delimiter));
                        pipeline.addLast("ClientMessageHandler", new GateServerHandler());
                        pipeline.addLast("PictureCollectHandler", new PictureCollectHandler());
                    }
                });
        // 配置option
        bindConnectionOptions(bootstrap);
        // 绑定端口
        bootstrap.bind(new InetSocketAddress(port)).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                LOGGER.info("[GateServer] Started Successed, registry is complete, waiting for client connect...");
            } else {
                LOGGER.error("[GateServer] Started Failed, registry is incomplete");
            }
        });
    }

    private static void bindConnectionOptions(ServerBootstrap bootstrap) {

        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.childOption(ChannelOption.SO_LINGER, 0);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        // 心跳机制暂时使用TCP选项
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

    }

}
