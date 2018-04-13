package com.qg.smartcarappserver.socket.handler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.qg.smartcarappserver.config.constant.GlobalConfig;
import com.qg.smartcarappserver.global.cache.OnlineCar;
import com.qg.smartcarappserver.util.FileUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by 小排骨 on 2017/9/29.
 */
@Slf4j
public class PictureCollectHandler extends ChannelInboundHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(PictureCollectHandler.class);

    private String carId;
    private AtomicInteger pictureGenerator = new AtomicInteger(0);

    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("car-task-%d").build();

    private static ExecutorService executor = new ThreadPoolExecutor(
            2,
            30,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024),
            namedThreadFactory,
            new ThreadPoolExecutor.AbortPolicy()
    );

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        carId = OnlineCar.getInstance().get(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        ByteBuf buf = (ByteBuf) message;
        WriteTask task = new WriteTask(buf, pictureGenerator, carId);
        Future<Boolean> future = executor.submit(task);
        if (future.get()) {
            if (pictureGenerator.incrementAndGet() % 10 == 0) {
                LOGGER.info("executor >> : execute: " + pictureGenerator.get());
            }
        }
//        else {
//            pictureGenerator.set(0);
//            log.info("图片开始重新计数 ");
//        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOGGER.info("channel is inactive. carId Is : >> : {}", carId);
        ctx.fireChannelInactive();
    }


    private static class WriteTask implements Callable<Boolean> {

        private ByteBuf buf;
        private AtomicInteger pictureGenerator;
        private String carId;

        WriteTask(ByteBuf buf, AtomicInteger pictureGenerator, String carId) {
            this.buf = buf;
            this.pictureGenerator = pictureGenerator;
            this.carId = carId;
        }

        @Override
        public Boolean call() throws Exception {

            File dirname = new File(GlobalConfig.PICTURE_PATH + carId);
            if (!dirname.exists()) {
                LOGGER.info("APP端连接,开始重新计数");
                pictureGenerator.set(0);
                FileUtil.createDir(GlobalConfig.PICTURE_PATH + carId);
                return false;
            }

            File tempFile = new File(GlobalConfig.PICTURE_PATH + carId + "\\" + GlobalConfig.TEMP_NAME);
            tempFile.deleteOnExit();
            tempFile.createNewFile();
            try (
                    OutputStream out = new BufferedOutputStream(
                            new FileOutputStream(tempFile))) {
                out.write(255);
                out.write(216);
                byte b;
                while (buf.readByte() == 0) {
                    ;
                }
                buf.readByte();
                while (buf.isReadable()) {
                    b = buf.readByte();
                    out.write(b);
                }
                out.write(255);
                out.write(217);
                out.close();
                buf.release();
            }
            File pictureFile = new File(GlobalConfig.PICTURE_PATH + carId + "\\" + String.format("%05d", pictureGenerator.get()) + ".jpg");
            pictureFile.delete();
            FileUtil.renameFile(tempFile, pictureFile);
            return true;
        }
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }
}
