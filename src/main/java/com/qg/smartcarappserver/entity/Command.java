package com.qg.smartcarappserver.entity;

/**
 * Created by 小排骨 on 2017/9/22.
 */
public class Command {

    /**
     * 小车id
     */
    private long carId;

    /**
     * 控制指令
     */
    private String content;

    public long getCarId() {
        return carId;
    }

    public void setCarId(long carId) {
        this.carId = carId;
    }

    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Command{" +
                "carId=" + carId +
                ", content='" + content + '\'' +
                '}';
    }
}
