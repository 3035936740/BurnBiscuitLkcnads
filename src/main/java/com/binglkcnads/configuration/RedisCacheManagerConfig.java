package com.binglkcnads.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.data.redis.serializer.RedisSerializationContext.*;

/** 这个是设置redis过期用的 **/
@Configuration
public class RedisCacheManagerConfig {
    /**
     * redis模板配置以及序列化配置
     *
     * @param factory 工厂
     * @return {@link RedisTemplate}<{@link String}, {@link Object}>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        //key 采用的String 的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        //hashde key 也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        //value 序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        //hash 的 value序列化方式采用jackson
        template.setHashKeySerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        template.afterPropertiesSet();
        return template;
    }


    @Bean
    RedisCacheWriter writer(RedisTemplate<String, Object> redisTemplate) {
        return RedisCacheWriter.nonLockingRedisCacheWriter(Objects.requireNonNull(redisTemplate.getConnectionFactory()));
    }

    @Bean
    CacheManager cacheManager(RedisCacheWriter writer) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();

        // 配置序列化（解决乱码的问题）
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))//time
                .serializeKeysWith(SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();

        //此处可以自定义缓存空间的缓存的过期时间，可以根据自己实际情况进行设置，也可以不设置，用统一过期时间
        configurationMap.put("test_controller", config.entryTtl(Duration.ofHours(24)));
        configurationMap.put("song_cache", config.entryTtl(Duration.ofHours(36)));
        configurationMap.put("song_wrapper_cache", config.entryTtl(Duration.ofHours(48)));

        //解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        return RedisCacheManager.builder(writer)
                .initialCacheNames(configurationMap.keySet())
                .withInitialCacheConfigurations(configurationMap)
                //其他缓存空间缓存过期时间为12小时
                .cacheDefaults(config.entryTtl(Duration.ofHours(12)))
                .build();
    }
}
