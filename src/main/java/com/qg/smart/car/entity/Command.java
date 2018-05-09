package com.qg.smart.car.entity;

import lombok.Data;

/**
 * @author 小排骨
 * @date 2017/9/22
 */
@Data
public class Command {

    /**
     * 小车id
     */
    private long carId;

    /**
     * 控制指令
     */
    private String content;
}
