package cn.aoe.sb.netty.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供 监控 接口
 *
 * @author zhaoruiqing
 * @version 1.0
 * @since 2022/8/23
 */
@Slf4j
@RestController
@RequestMapping("/monitor")
public class MonitorApi {

    @GetMapping("/")
    public String health() {
        return "health";
    }
    /**
     * 所有可监控信息
     * @return json object
     */
    @GetMapping("/all")
    public Object allMonitor(){
        return "";
    }

}
