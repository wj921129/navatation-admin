package com.navatation.framework.security;

import com.navatation.common.RedisConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Nonce 服务，用于生成和校验一次性挑战码
 * - 生成 UUID v4 nonce，存入 Redis，TTL 5 分钟
 * - 消费 nonce 时原子操作：检查存在 → 删除 → 返回结果
 */
@Service
public class NonceService {

    private static final Logger log = LoggerFactory.getLogger(NonceService.class);

    private static final String NONCE_PREFIX = RedisConstants.KEY_AUTH_NONCE;
    private static final long NONCE_TTL = 5;
    private static final TimeUnit NONCE_TTL_UNIT = TimeUnit.MINUTES;

    private final RedisTemplate<String, Object> redisTemplate;

    public NonceService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 生成一次性 nonce（UUID v4）并存入 Redis
     * @return nonce 字符串
     */
    public String generateNonce() {
        String nonce = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(NONCE_PREFIX + nonce, "1", NONCE_TTL, NONCE_TTL_UNIT);
        log.debug("Nonce 已生成: {}", nonce);
        return nonce;
    }

    /**
     * 校验并消费 nonce
     * 先 get 再 delete（兼容 Redis 6.2.0 以下版本，不支持 GETDEL 命令）
     * @param nonce 待消费的 nonce
     * @return true 表示 nonce 有效且已消费；false 表示 nonce 不存在或已消费
     */
    public boolean consumeNonce(String nonce) {
        String key = NONCE_PREFIX + nonce;
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            redisTemplate.delete(key);
        }
        boolean valid = value != null;
        if (valid) {
            log.debug("Nonce 已消费: {}", nonce);
        } else {
            log.warn("Nonce 无效或已消费: {}", nonce);
        }
        return valid;
    }
}
