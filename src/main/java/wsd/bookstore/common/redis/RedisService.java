package wsd.bookstore.common.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;

    public void setValues(String key, String data) {
        log.debug("Redis 값 저장: key={}", key);
        ValueOperations<String, String> values = stringRedisTemplate.opsForValue();
        values.set(key, data);
    }

    public void setValues(String key, String data, Duration duration) {
        log.debug("Redis 값 저장 (만료시간 포함): key={}, duration={}", key, duration);
        ValueOperations<String, String> values = stringRedisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    public String getValues(String key) {
        log.debug("Redis 값 조회: key={}", key);
        ValueOperations<String, String> values = stringRedisTemplate.opsForValue();
        return values.get(key);
    }

    public void deleteValues(String key) {
        log.debug("Redis 값 삭제: key={}", key);
        stringRedisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }
}
