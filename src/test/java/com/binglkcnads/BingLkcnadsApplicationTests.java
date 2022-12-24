package com.binglkcnads;

import com.binglkcnads.common.core.redis.RedisService;
import com.binglkcnads.common.utils.RSAUtil;
import com.binglkcnads.controller.MultithreadScheduleTask;
import com.binglkcnads.dao.PhigrosRating;
import com.binglkcnads.dao.resource.GlobalStaticClass;
import com.binglkcnads.mappers.PhigrosMainMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@SpringBootTest
@SuppressWarnings("all")
class BingLkcnadsApplicationTests {
    Log log = LogFactory.getLog(BingLkcnadsApplicationTests.class);
    @Autowired
    private PhigrosMainMapper phigrosMainMapper;

    @Test
    void contextLoads() {
        float rating1 = 15.1f;
        List<PhigrosRating> byRatingPhigrosRating = phigrosMainMapper.findByRatingPhigrosRating(rating1 - 0.01f, rating1 + 0.01f);
        System.out.println(byRatingPhigrosRating);
    }

    @Autowired
    private RabbitTemplate template;
    @Test
    @RabbitListener(queuesToDeclare = {@Queue("simple.queue")})
    void AMQPTest(){
        String queueName = "simple.queue";
        String message = "Hi,spring amqp!";
        template.convertAndSend(queueName,message);
    }
    @Test
    @RabbitListener(queuesToDeclare = {@Queue("simple.queue")})
    void AMQPTest2() throws InterruptedException {
        String queueName = "simple.queue";
        String message = "Hi,spring amqp!";
        for (int row = 1; row <= 50; row++) {
            template.convertAndSend(queueName,message+",No."+row);
            Thread.sleep(20);
        }
    }

    @Test
    @RabbitListener(queuesToDeclare = {@Queue("direct.queue1"),@Queue("direct.queue2")})
    void AMQPTest3() throws InterruptedException {
        template.convertAndSend("direct.queue1","yellow","queue1 yellow");
        template.convertAndSend("direct.queue2","yellow","queue2 yellow");
        template.convertAndSend("direct.queue1","blue","queue1 blue");
        template.convertAndSend("direct.queue2","blue","queue2 blue");
        template.convertAndSend("direct.queue1","red","queue1 red");
        template.convertAndSend("direct.queue2","red","queue2 red");
    }

    @Test
    @RabbitListener(queuesToDeclare = {@Queue("topic.queue1"),@Queue("topic.queue2")})
    void AMQPTest4() throws InterruptedException {
        template.convertAndSend("topic.queue1","yellow","topic1 yellow");
        template.convertAndSend("topic.queue2","yellow","topic2 yellow");
        template.convertAndSend("topic.queue1","blue","topic1 blue");
        template.convertAndSend("topic.queue2","blue","topic2 blue");
        template.convertAndSend("topic.queue1","red","topic1 red");
        template.convertAndSend("topic.queue2","red","topic2 red");
    }

    @Test
    @RabbitListener(queuesToDeclare = {@Queue("object0.queue0")})
    void AMQPTest5() throws InterruptedException {
        String k = "object0.queue0";
        Map<String,Object> msg = new HashMap<>();
        msg.put("name","柳岩");
        msg.put("age",21);
        template.convertAndSend(k,msg);
    }

    @Autowired
    RedisService redisService;

    @Test
    void TestRedisSpring(){
        Boolean hasDelete = redisService.deleteObject("song_cache::SimpleKey [null,钟]");
        System.out.println(hasDelete);
    }

    @Test
    void TestAlias(){
        final String temporary = phigrosMainMapper.findByAliasWhereId(649);
        System.out.println(temporary);
    }

    @Test
    void TestAddAndQueryAlias() throws InterruptedException, ExecutionException {
        Byte count = phigrosMainMapper.contentWhetherExist(null, "你游最简单曲");
        System.out.println("[" + count + "]\tb:"+ (count == 0));
        /*CountDownLatch countDownLatch=new CountDownLatch(7);
        long start = System.currentTimeMillis();
        ExecutorService executor= Executors.newCachedThreadPool();
        for (int i = 0; i < 70; i++) {
            int finalI = i;
            executor.submit(() -> {
                for (int j = finalI *10; j < finalI *10+10; j++) {
                    Byte count = phigrosMainMapper.contentWhetherExist(j, null);
                    System.out.println("[" + j + "]\tb:"+ (count == 0) +"\tcount:" + count);
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        System.out.println("Thread use:"+(end-start)+" ms");*/
    }

    @Test
    void RSATest() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKey = GlobalStaticClass.getRSAPublicKey();
        String privateKey = GlobalStaticClass.getRSAPrivateKey();
        String str = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY3MTcxODc4OSwiZXhwIjoxNjcxNzE4ODQ5LCJrZXkiOiIwMDAxIn0.7f51MpAHn1VyZaqOGt7ZdZkLlbPnbF0Q4mtU5roJG9Y";
        System.out.println("\r明文：\r\n" + str);
        System.out.println("\r明文大小：\r\n" + str.getBytes().length);
        String encodedData = RSAUtil.publicEncrypt(str, publicKey);  //传入明文和公钥加密,得到密文
        System.out.println("密文：\r\n" + encodedData);
        String decodedData = RSAUtil.privateDecrypt(encodedData, privateKey); //传入密文和私钥,得到明文
        System.out.println("解密后文字: \r\n" + decodedData);
    }

    @Test
    void BCryptTest(){
        List<String> list = new ArrayList<>();
        // 未加密前
        final String plain_text = "tomato-kawaii";
        // 生成5次加密
        for (int i = 1; i <= 5; ++i) {
            String encodedPassword = BCrypt.hashpw(plain_text, BCrypt.gensalt());
            list.add(encodedPassword);
            System.out.println("编号" + i + ":\t" + encodedPassword);
        }

        for (String s : list) {
            // 使用正确密码验证密码是否正确(去匹配)
            boolean flag = BCrypt.checkpw(plain_text, s);
            System.out.println("匹配是否与原文一致:\t" + flag);
        }
        // 使用错误密码验证密码是否正确
        boolean flag = BCrypt.checkpw("111222", BCrypt.hashpw(plain_text, BCrypt.gensalt()));
        System.out.println("匹配是否与原文一致:\t" + flag);
    }
}
