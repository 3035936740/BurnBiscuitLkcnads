package com.binglkcnads.common.core.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.redis.RedisHealthIndicator;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 检测redis状态
 */
@SuppressWarnings(value = "all")
@Component
public class RedisStatusTask {

    private static int status = 3;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Scheduled(fixedDelay = 1*60*1000)
    public void redisOK(){
        RedisHealthIndicator redisHealthIndicator = new RedisHealthIndicator(redisConnectionFactory);
        Health health = redisHealthIndicator.getHealth(false);
        String code = health.getStatus().getCode();
        if(code.equals(Status.UP.getCode()) && status<3){
            status = status + 1;
        }else if(status>0){
            status = status - 1;
        }
    }


    public static boolean redisUp(){
        return RedisStatusTask.status > 0;
    }

    public static void redisMayDown(){
        if(status>0){
            status = status - 1;
        }
    }

}