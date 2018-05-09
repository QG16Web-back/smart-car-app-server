package com.qg.smart.car.web.controller;


import com.qg.smart.car.config.constant.GlobalConfig;
import com.qg.smart.car.global.cache.OnlineCar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 小排骨
 * @date 2017/9/7
 */
@Controller
@Slf4j
public class CommandController {


    @GetMapping("onlineCar")
    @ResponseBody
    public List<String> getOnlineCar(HttpServletRequest session) {
        // 一个蹩脚的操作
        if (GlobalConfig.PICTURE_PATH == null) {
            GlobalConfig.PICTURE_PATH = session.getServletContext().getRealPath("/picture/");
            log.info("图片文件路径初始化成功 >> {}", GlobalConfig.PICTURE_PATH);
        }
        return OnlineCar.getInstance().keySet();
    }
}
