package com.io.core.controller;


import com.google.code.kaptcha.Producer;
import com.io.core.common.entity.SysUser;
import com.io.core.common.wrapper.ConstValue;
import com.io.core.common.wrapper.ResultWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.UUID;

@RestController
public class AuthController extends BaseController {

    @Autowired
    Producer producer;


    /**
     * 获取验证码
     * @param
    */
    @GetMapping("/captcha")
    public ResultWrapper captcha() throws IOException {
        HashMap<Object, Object> data = new HashMap<>();
        String key = UUID.randomUUID().toString();
        String code = producer.createText();

        BufferedImage image = producer.createImage(code);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);

        BASE64Encoder encoder = new BASE64Encoder();
        String base64Img = "data:image/jpeg;base64," + encoder.encode(outputStream.toByteArray());

        // 存入redis
        redisUtil.hset(ConstValue.CAPTCHA_KEY, key, code, 120);

        data.put("token", key);
        data.put("captchaImg", base64Img);

        return ResultWrapper.success(data);
    }

    /**
     * 获取用户信息接口
     * @param principal spring-security的登录用户
     */
    @GetMapping("/sys/userInfo")
    public ResultWrapper userInfo(Principal principal) {
        SysUser sysUser = sysUserService.getByUsername(principal.getName());

        HashMap<Object, Object> data = new HashMap<>();

        data.put("id", sysUser.getId());
        data.put("username", sysUser.getUsername());
        data.put("avatar", sysUser.getAvatar());

        return ResultWrapper.success(data);
    }
}
