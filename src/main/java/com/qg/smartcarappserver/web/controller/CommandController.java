package com.qg.smartcarappserver.web.controller;


import com.qg.smartcarappserver.config.constant.GlobalConfig;
import com.qg.smartcarappserver.global.cache.OnlineCar;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by 小排骨 on 2017/9/7.
 */
@Controller
@Slf4j
public class CommandController {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommandController.class);

    @GetMapping("onlineCar")
    @ResponseBody
    public List<String> getOnlineCar(HttpServletRequest session) {
        // 一个蹩脚的操作
        if (GlobalConfig.PICTURE_PATH == null) {
            GlobalConfig.PICTURE_PATH = session.getServletContext().getRealPath("/picture/");
            LOGGER.info("图片文件路径初始化成功 >> {}", GlobalConfig.PICTURE_PATH);
        }
        return OnlineCar.getInstance().keySet();
    }
}
