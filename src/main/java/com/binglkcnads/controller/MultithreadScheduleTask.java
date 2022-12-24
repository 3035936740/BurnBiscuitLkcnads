package com.binglkcnads.controller;

import com.binglkcnads.common.utils.RSAUtil;
import com.binglkcnads.dao.resource.GlobalStaticClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Component;

//@Component注解用于对那些比较中立的类进行注释；
//相对与在持久层、业务层和控制层分别采用 @Repository、@Service 和 @Controller 对分层中的类进行注释
@Component
@EnableScheduling   // 1.开启定时任务
@EnableAsync        // 2.开启多线程
public class MultithreadScheduleTask {
    Log log = LogFactory.getLog(MultithreadScheduleTask.class);
    /*
    @Async
    @Scheduled(fixedDelay = 1000)  //间隔1秒
    public void first() throws InterruptedException {
        System.out.println("第一个定时任务开始 : " + LocalDateTime.now().toLocalTime() + "\r\n线程 : " + Thread.currentThread().getName());
        System.out.println();
        Thread.sleep(1000 * 10);
    }

    @Async
    @Scheduled(fixedDelay = 2000)
    public void second() {
        System.out.println("第二个定时任务开始 : " + LocalDateTime.now().toLocalTime() + "\r\n线程 : " + Thread.currentThread().getName());
        System.out.println();
    }
    */

    @Async
    @Schedules({
            @Scheduled(cron="0 30 2 * * ?")//每天凌晨2:30执行一次
            //@Scheduled(cron="10 43 15 * * ? "),
            //@Scheduled(cron="40 43 15 * * ? "),
    })
    public void third() {
        log.info("==============");
        GlobalStaticClass.s_rsa_key_map = RSAUtil.createKeys(1024);
        log.info("RSA已更改");
        log.info("公钥:"+GlobalStaticClass.getRSAPublicKey());
        log.info("密钥:"+GlobalStaticClass.getRSAPrivateKey());
        log.info("==============");
    }

}
