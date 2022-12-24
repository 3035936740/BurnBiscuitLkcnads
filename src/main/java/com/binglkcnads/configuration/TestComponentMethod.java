package com.binglkcnads.configuration;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestComponentMethod {
    @RabbitListener(queues = {"simple.queue"})
    public void listenSimpleQueueMessage(String msg) throws InterruptedException{
        System.out.println("消费1:接收消息:" + msg);
        Thread.sleep(20);
    }

    @RabbitListener(queues = {"simple.queue"})
    public void listenSimpleQueueMessage2(String msg) throws InterruptedException{
        System.err.println("消费2:接收消息:" + msg);
        Thread.sleep(200);
    }

    @RabbitListener(queues = {"object0.queue0"})
    public void listenSimpleQueueMessage6(Map<String,Object> msg){
        System.out.println("接收消息:" + msg);
    }


    //定义directExchange
    //监听交换器中指定的queue
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("direct.queue1"),
            exchange = @Exchange("exchange.direct"),
            key = {"red","blue"}
    ))
    public void listenDirectQueue1(String msg){
        System.out.println("接收消息:"+msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("direct.queue2"),
            exchange = @Exchange(value = "exchange.direct"),
            key = {"red","yellow"}
    ))
    public void listenDirectQueue2(String msg){
        System.out.println("接收消息:"+msg);
    }

    //话题交换机
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("topic0.queue1"),
            exchange = @Exchange(value = "exchange.topic0",type = ExchangeTypes.TOPIC),
            key = "china.#"
    ))
    public void listenDirectQueue3(String msg){
        System.out.println("接收消息:"+msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("topic0.queue2"),
            exchange = @Exchange(value = "exchange.topic0",type = ExchangeTypes.TOPIC),
            key = "#.new"
    ))
    public void listenDirectQueue4(String msg){
        System.out.println("接收消息:"+msg);
    }
}
