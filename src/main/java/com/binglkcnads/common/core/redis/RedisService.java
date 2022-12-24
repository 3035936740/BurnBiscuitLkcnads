package com.binglkcnads.common.core.redis;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * spring redis 工具类
 *
 * @author
 **/
@SuppressWarnings(value = "all")
@Component
public class RedisService {

    private static final Logger log = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    public RedisTemplate redisTemplate;



    /**
     *
     *返回存储在键中的列表的指定元素
     *
     */
    public <T> List<T> range(String key, long start, long end) {
        if (RedisStatusTask.redisUp()) {
            try {
                ListOperations<String, T> listOperations = redisTemplate.opsForList();
                return listOperations.range(key,start,end);
            } catch (RedisConnectionFailureException e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
        return null;
    }

    /**
     * 缓存基本的list集合对象等
     */
    public <T> void leftPush(final String key,final T obj) {
        if (RedisStatusTask.redisUp()) {
            try {
                ListOperations<String, T> listOperations = redisTemplate.opsForList();
                listOperations.leftPush(key,obj);
            } catch (RedisConnectionFailureException e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
    }
    /**
     * redis对唯一键值加锁
     * @param lockPrefix
     * @param key
     * @param timeout
     * @return
     */
    public boolean setNx(final String lockPrefix,final String key, long timeout) {
        if (RedisStatusTask.redisUp()) {
            try {
                return redisTemplate.opsForValue().setIfAbsent(lockPrefix + key, System.currentTimeMillis(),timeout,TimeUnit.SECONDS);
            } catch (RedisConnectionFailureException e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
        return true;
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        if (RedisStatusTask.redisUp()) {
            try {
                redisTemplate.opsForValue().set(key, value);
            } catch (RedisConnectionFailureException e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param keys  缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final T value, final String... keys) {
        String key = StringUtils.join(keys, ":");
        setCacheObject(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        if (RedisStatusTask.redisUp()) {
            try {
                redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
            } catch (RedisConnectionFailureException e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
    }

    public <T> void setCacheObject(final T value, final Long timeout, final TimeUnit timeUnit, final String... keys) {
        String key = StringUtils.join(keys, ":");
        setCacheObject(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param keys     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final long timeout,final TimeUnit unit,final String...keys) {
        String key = StringUtils.join(keys, ":");
        return expire(key, timeout, unit);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        if (RedisStatusTask.redisUp()) {
            try {
                return redisTemplate.expire(key, timeout, unit);
            } catch (RedisConnectionFailureException e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
        return false;
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        if (RedisStatusTask.redisUp()) {
            try {
                ValueOperations<String, T> operation = redisTemplate.opsForValue();
                return operation.get(key);
            } catch (RedisConnectionFailureException e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
        return null;
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param keys 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String... keys) {
        String key = StringUtils.join(keys, ":");
        return getCacheObject(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key) {
        if (RedisStatusTask.redisUp()) {
            try {
                return redisTemplate.delete(key);
            } catch (RedisConnectionFailureException e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
        return false;
    }

    /**
     * 删除单个对象
     *
     * @param keys
     */
    public boolean deleteObject(final String... keys) {
        String key = StringUtils.join(keys, ":");
        return deleteObject(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public long deleteObject(final Collection collection) {
        if (RedisStatusTask.redisUp()) {
            try {
                return redisTemplate.delete(collection);
            } catch (RedisConnectionFailureException e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
        return 0l;
    }


    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        if (RedisStatusTask.redisUp()) {
            try {
                return redisTemplate.keys(pattern);
            } catch (RedisConnectionFailureException e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
        return null;
    }


    public Long incrBy(Long incr, String... keys) {
        if (RedisStatusTask.redisUp()) {
            try {
                String key = StringUtils.join(keys, ":");
                return redisTemplate.opsForValue().increment(key, incr);
            } catch (RedisConnectionFailureException e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
        return 0l;
    }


    public Long decrBy(Long decr, String... keys) {
        if (RedisStatusTask.redisUp()) {
            try {
                String key = StringUtils.join(keys, ":");
                return redisTemplate.opsForValue().decrement(key, decr);
            } catch (RedisConnectionFailureException e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
        return 0l;
    }


    /**
     *
     *返回存储在键中的列表的指定元素
     *
     */
    public <T> Set<T> members(final String key) {
        if (RedisStatusTask.redisUp()) {
            try {
                return redisTemplate.opsForSet().members(key);
            } catch (RedisConnectionFailureException e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
        return null;
    }

    /**
     * set中增加元素，支持一次增加多个元素，逗号分隔即可，结果返回添加的个数
     *
     * @param key
     * @param value
     * @return
     */
    public Long addSet(String key, Object... value) {
        if (RedisStatusTask.redisUp()) {
            Long size = null;
            try {
                size = redisTemplate.opsForSet().add(key, value);
                return size;
            } catch (RedisConnectionFailureException e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
        return 0L;
    }

    /**
     * set中移除指定元素
     *
     * @param key
     * @param value
     * @return
     */
    public Long removeSet(String key, Object value) {
        if (RedisStatusTask.redisUp()) {
            Long size = null;
            try {
                size = redisTemplate.opsForSet().remove(key, value);
                return size;
            } catch (Exception e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
        return 0L;
    }

    /**
     * 计算set集合大小
     *
     * @param key
     * @return
     */
    public Long countSet(String key) {
        if (RedisStatusTask.redisUp()) {
            Long size = null;
            try {
                size = redisTemplate.opsForSet().size(key);
                return size;
            } catch (Exception e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
        return null;
    }

    /**
     * 判断set中是否存在某元素
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean hasMemberSet(String key, Object value) {
        if (RedisStatusTask.redisUp()) {
            Boolean exist = false;
            try {
                exist = redisTemplate.opsForSet().isMember(key, value);
                return exist;
            } catch (Exception e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
        return false;
    }



    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    public Boolean hasKey(String key) {
        if (RedisStatusTask.redisUp()) {
            Boolean exist = false;
            try {
                exist = redisTemplate.hasKey(key);
                return exist;
            } catch (Exception e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
        return false;
    }
    /**
     * 获得缓存的基本对象。
     *
     * @param keys 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String... keys) {
        String key = StringUtils.join(keys, ":");
        return getCacheList(key);
    }
    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key) {
        if (RedisStatusTask.redisUp()) {
            try {
                Collection<String> keys = keys(key);
                ValueOperations<String, T> operation = redisTemplate.opsForValue();
                return operation.multiGet(keys);
            } catch (RedisConnectionFailureException e) {
                log.error(e.getMessage());
                RedisStatusTask.redisMayDown();
            }
        }
        return null;
    }
}


