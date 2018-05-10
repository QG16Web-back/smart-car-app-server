package com.qg.smart.car.web.controller;


import com.qg.smart.car.global.cache.OnlineCar;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 小排骨
 * @date 2017/9/7
 */
@RestController
public class CommandController {

    @GetMapping("onlineCar")
    public List<String> getOnlineCar() {
        return OnlineCar.getInstance().keySet();
    }
}
