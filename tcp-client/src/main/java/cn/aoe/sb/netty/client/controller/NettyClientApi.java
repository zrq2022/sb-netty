package cn.aoe.sb.netty.client.controller;

import cn.aoe.sb.netty.client.core.NettyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * @author zhaoruiqing
 * @version 1.0
 * @since 2022/8/23
 */
@Slf4j
@RestController
@RequestMapping("/send")
public class NettyClientApi {
    @Autowired
    private NettyClient nettyClient;

    @GetMapping("/msg")
    public String sendMessage(@RequestParam("msg") String message) {
        nettyClient.sendMsg(message.getBytes(StandardCharsets.UTF_8));
        return "ok";
    }
}
