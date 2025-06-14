package com.seckill.lab.seckillsystem.controller;

import ch.qos.logback.classic.Logger;
import com.seckill.lab.seckillsystem.result.ResultCode;
import com.seckill.lab.seckillsystem.result.Result;
import com.seckill.lab.seckillsystem.service.SeckillUserService;
import com.seckill.lab.seckillsystem.vo.LoginVo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;


import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.Valid;

@RequestMapping("/api/login")
@Controller
public class LoginController {

    @Autowired
    private SeckillUserService seckillUserService;

    private final Logger logger = (Logger) LoggerFactory.getLogger(Logger.class);

    @RequestMapping("/index")
    public String toLogin() {
        // 返回页面login
        logger.info("tiao zhuan login ...");
        return "login";
    }

    @RequestMapping("/token_test")
    @ResponseBody
    public Result<String> doLoginTest(@Valid LoginVo loginVo,HttpServletResponse response)  {
        String token = seckillUserService.loginTest( loginVo,response);
        return Result.success(token);
    }

    @PostMapping("/do_login")
    @ResponseBody
    public Result<?> doLogin(@Valid @RequestBody LoginVo loginVo, HttpServletResponse response) {
        logger.info("Trying to login " + loginVo.getPhone() + ' ' + loginVo.getPassword());
        // 2. 直接返回 Service 层处理后的结果
        return seckillUserService.login(loginVo, response);
    }
}
