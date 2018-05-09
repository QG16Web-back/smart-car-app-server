package com.qg.smartcarappserver.entity;

import lombok.Data;

/**
 * Created by 小排骨 on 2017/9/22.
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
