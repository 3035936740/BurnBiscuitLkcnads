package com.binglkcnads.controller;

import com.binglkcnads.common.utils.JWTUtil;
import com.binglkcnads.common.utils.RSAUtil;
import com.binglkcnads.common.utils.StringUtils;
import com.binglkcnads.common.utils.ip.IpUtils;
import com.binglkcnads.dao.AliasToMain;
import com.binglkcnads.dao.Result.ErrResult;
import com.binglkcnads.dao.resource.GlobalStaticClass;
import com.binglkcnads.dao.test.YesNoWtf;
import com.binglkcnads.service.PhigrosServiceBing;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import reactor.util.annotation.Nullable;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** api测试页面 **/
@RestController
@RequestMapping("/api/test")
public class RestControllerClassTest {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PhigrosServiceBing phigrosServiceBing;
    Log log = LogFactory.getLog(RestControllerClassTest.class);

    @GetMapping("Hello")
    public String Hello(){
        System.out.println("调用api Hello");
        return "Hello";
    }

    @PostMapping("Hello_post")
    public String HelloPost(@RequestBody TestMethod method,/*@Nullable */@RequestHeader(value = "user-id",required = false) String userId){
        System.out.println(userId);
        System.out.println("调用api Hello Post");
        return method.getStr() + userId;
    }

    @PostMapping("Hello_post_all")
    public String HelloPostAll(@RequestBody TestMethod method,@RequestHeader(required = false) MultiValueMap<String, String> headers) {
        StringBuilder content = new StringBuilder();
        System.out.println(headers);
        System.out.println("token -> " + headers.get("toekn").get(0));
        headers.forEach((key, value) -> {
            String s = null;
            content.append(key).append("=").append(value).append(";");
        });
        if(!content.isEmpty() && !Objects.isNull(content)) return method.getStr() + "\n\r\n\r\n\rRead HTTP Headers: " + content;
        else return method.getStr();
    }
    @GetMapping("Hello1")
    public String Hello1(){
        System.out.println("调用api Hello1");
        String url = "http://localhost:8081/api/test/Hello/";
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        System.out.println(forEntity);
        String body = forEntity.getBody();
        return body;
    }

    //这个是测试并发api并做包装的
    @GetMapping("Hello2")
    public List<List<Object>> Hello2() throws InterruptedException {
        System.out.println("调用api Hello2");
        List<List<Object>> lists = new ArrayList<>();

        Integer count = 10;

        CountDownLatch countDownLatch=new CountDownLatch(count);
        long start = System.currentTimeMillis();
        ExecutorService executor= Executors.newCachedThreadPool();
        for (int i = 0; i < count; i++) {
            int finalI = i;
            executor.submit(() -> {
                    List<Object> list = new ArrayList<>();
                    String url = "https://yesno.wtf/api";
                    //ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
                    //String body = forEntity.getBody();
                    YesNoWtf yesNoWtf = restTemplate.getForObject(url,YesNoWtf.class);
                    System.out.println(yesNoWtf);
                    list.add("id:"+finalI);
                    list.add(yesNoWtf);
                    System.out.println(list);
                    lists.add(list);
                    System.out.println(lists);
                    //list.clear();
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        System.out.println("Thread use:"+(end-start)+" ms");
        //ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        //String body = forEntity.getBody();


        System.out.println(lists);
        return lists;
    }

    @Resource
    private JWTUtil jwtTokenUtil;
    @GetMapping("test_token")
    public Object TestToken(@Nullable HttpServletRequest request) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKey = GlobalStaticClass.getRSAPrivateKey();
        String token = request.getHeader(jwtTokenUtil.header);
        if (StringUtils.isEmpty(token)) {
            token = request.getParameter(jwtTokenUtil.header);
        }
        token = RSAUtil.privateDecrypt(token, privateKey);
        System.out.println(token);
        Claims tokenClaim = jwtTokenUtil.getTokenClaim(token);
        if (tokenClaim.get("key").equals("0001"))
            return ErrResult.failedWith(new Date(), 401, "输入有误");
        return "Hello!Token!";
    }
    @GetMapping("GetToken")
    public String getToken(@RequestParam final String id) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Map<String,Object> map = new HashMap<>();
        map.put("key",id);
        String publicKey = GlobalStaticClass.getRSAPublicKey();
        String jwt = jwtTokenUtil.createToken("admin",map);
        return RSAUtil.publicEncrypt(jwt, publicKey);
    }
    // 获取每次请求的ip地址



    @GetMapping("TestRewardToken")
    public String getStr(){
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("user-id", "MyValue");
        requestHeaders.set("toekn", "sbsbsb");
        TestMethod testMethod = new TestMethod();
        testMethod.setStr("joker");
        HttpEntity requestEntity = new HttpEntity(testMethod,requestHeaders);

        //String url = "http://localhost:8081/api/test/Hello_post";
        String url = "http://localhost:8081/api/test/Hello_post_all";
        System.out.println("aaa");
        ResponseEntity<String> forEntity = restTemplate.postForEntity(url, requestEntity,String.class);
        System.out.println(forEntity);
        //Book book = restTemplate.postForObject("http://127.0.0.1:8080/getbook", requestEntity, Book.class);
        return forEntity.getBody();
    }

    @ModelAttribute
    public void ModelMethod(HttpServletRequest request){
        log.info("============================");
        log.info("测试api访问");
        log.info("来自IP地址:" + IpUtils.getIpAddr(request));
        log.info("===========================");
    }
}
@Data
@NoArgsConstructor
@AllArgsConstructor
class TestMethod{
    private String str;
}