package com.binglkcnads.controller;

import com.binglkcnads.common.utils.ip.IpUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/** 主页面与测试啥啥之类的x **/
@SuppressWarnings("all")
@Controller
public class ControllerClassBing {
    Log log = LogFactory.getLog(ControllerClassBing.class);
    @RequestMapping("manual/page")
    public String page1(){
        return "manual/page.html";
    }

    @RequestMapping("test")
    @ResponseBody
    public String test(){
        return "测试api的用的东西~";
    }

    @ModelAttribute
    public void ModelMethod(HttpServletRequest request){
        log.info("============================");
        log.info("访问前端的用户页面");
        log.info("来自IP地址:" + IpUtils.getIpAddr(request));
        log.info("============================");
    }
}