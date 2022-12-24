package com.binglkcnads.configuration;

import com.binglkcnads.common.filter.XssFilter;
import com.binglkcnads.filter.JwtFilter;
import com.binglkcnads.interceptor.InterceptorBing;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;


@Configuration
@ComponentScan(basePackages = "com.binglkcnads.dao")
@ComponentScan(basePackages = "com.binglkcnads.service")
@ComponentScan(basePackages = "com.binglkcnads.mappers")
@ComponentScan(basePackages = "com.binglkcnads.common")
public class ConfigurationClassBing implements WebMvcConfigurer {
    Log log = LogFactory.getLog(ConfigurationClassBing.class);
    public ConfigurationClassBing(){
        log.info("Bean初始化");
    }
    // 这下面都是从application.yaml中得到的数据
    @Value("${proxy.nginx-static-resource.url}")
    private String url;
    @Value("${proxy.nginx-static-resource.target_url}")
    private String targetUrl;
    @Value("${proxy.nginx-static-resource.name}")
    private String name;

    // 这里是反向代理
    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new ProxyServlet(), url);
        servletRegistrationBean.setName(name);
        servletRegistrationBean.addInitParameter("targetUri", targetUrl);
        servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG, String.valueOf(true));
        return servletRegistrationBean;
    }

    // 这个是对XSS的过滤
    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new XssFilter());
        bean.addUrlPatterns("/*");
        return bean;
    }

    // 对api二次处理
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // 跨域访问
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 使用通配符* 允许所有的域请求
        corsConfiguration.addAllowedOrigin("*");
        // 使用通配符* 允许所有请求头字段
        corsConfiguration.addAllowedHeader("*");
        // 使用通配符* 允许所有请求头方法类型
        corsConfiguration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 处理请求映射
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }

    @Resource
    private JwtFilter jwtFilter ;

    // TODO 这里可能会设置一下token拦截啥的(
    //拦截器配置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new InterceptorBing()).
                // 拦截的路径
                addPathPatterns().
                // 放行的路径
                excludePathPatterns("/**");
        registry.addInterceptor(jwtFilter).addPathPatterns("/api/test/test_token");
    }

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    // rabbitmq的队列配置
    @Bean
    public Queue logOpQueue(){
        return new Queue("opLog");
    }

    @Bean
    public Queue logErrQueue(){
        return new Queue("errorLog");
    }
    //不要偷懒 一定要声明
    @Bean
    public Queue chatMessageQueue(){
        return new Queue("chatMessage");
    }

    // RabbitMQ绑定多个
    // 声明一个交换机队列
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("exchange.fanout");
    }
    //声明第一个队列
    @Bean Queue fanoutQueue1(){
        return new Queue("fanout.queue1");
    }
    //绑定队列1和交换机
    @Bean
    public Binding bindingQueue1(Queue fanoutQueue1,FanoutExchange fanoutExchange){
        return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
    }

    @Bean Queue fanoutQueue2(){
        return new Queue("fanout.queue2");
    }
    //绑定队列2和交换机
    @Bean
    public Binding bindingQueue2(Queue fanoutQueue2,FanoutExchange fanoutExchange){
        return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
    }
}
