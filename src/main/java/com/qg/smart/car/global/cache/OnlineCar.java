package com.qg.smart.car.global.cache;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 小排骨
 * Date 2018/1/8
 * 在线小车的缓存
 */
public class OnlineCar {

    /**
     * 使用ConcurrentHashMap缓存小车ID以及对应的通道.
     */
    private ConcurrentHashMap<String, Channel> cache = new ConcurrentHashMap<>();

    /**
     * 因为小车没有唯一标识，先用这个代替（正确的方案是小车端会发送一个唯一的标识符过来作为key）.
     */
    private AtomicInteger carGenerator = new AtomicInteger(0);

    /**
     * 静态内部类存储单例对象.
     */
    private static class OnlineCarHolder {
        /**
         * 小车缓存.
         */
        private static OnlineCar cache = new OnlineCar();
    }

    /**
     * 获取单例对象.
     *
     * @return instance
     */
    public static OnlineCar getInstance() {
        return OnlineCarHolder.cache;
    }

    /**
     * 获得carId并绑定通道.
     *
     * @param channel channel
     * @return carId
     */
    public String put(final Channel channel) {
        String carId = carGenerator.incrementAndGet() + "";
        cache.put(carId, channel);
        return carId;
    }

    /**
     * 移除小车连接.
     *
     * @param carId carId
     */
    public void remove(final String carId) {
        cache.remove(carId);
    }

    /**
     * 获取相应的小车连接.
     *
     * @param carId carId
     * @return 通道连接
     */
    public Channel get(final String carId) {
        return cache.get(carId);
    }

    /**
     * 获取相应的carId.
     *
     * @param channel 通道
     * @return carId
     */
    public String get(final Channel channel) {
        for (Map.Entry<String, Channel> entry : cache.entrySet()) {
            if (entry.getValue().equals(channel)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 获得小车ID列表.
     *
     * @return 小车ID列表
     */
    public List<String> keySet() {
        return new ArrayList<>(cache.keySet());
    }
}
