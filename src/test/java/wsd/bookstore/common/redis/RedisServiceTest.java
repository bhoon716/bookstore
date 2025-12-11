package wsd.bookstore.common.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @InjectMocks
    private RedisService redisService;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Nested
    @DisplayName("값 저장 테스트")
    class SetValuesTest {

        @Test
        @DisplayName("성공: 키와 값을 저장한다")
        void success_simple() {
            // given
            String key = "key";
            String data = "data";
            given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);

            // when
            redisService.setValues(key, data);

            // then
            verify(valueOperations).set(key, data);
        }

        @Test
        @DisplayName("성공: 만료 시간과 함께 키와 값을 저장한다")
        void success_withDuration() {
            // given
            String key = "key";
            String data = "data";
            Duration duration = Duration.ofMinutes(1);
            given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);

            // when
            redisService.setValues(key, data, duration);

            // then
            verify(valueOperations).set(key, data, duration);
        }
    }

    @Nested
    @DisplayName("값 조회 테스트")
    class GetValuesTest {

        @Test
        @DisplayName("성공: 키에 해당하는 값을 조회한다")
        void success() {
            // given
            String key = "key";
            String expectedData = "data";
            given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get(key)).willReturn(expectedData);

            // when
            String result = redisService.getValues(key);

            // then
            assertThat(result).isEqualTo(expectedData);
        }

        @Test
        @DisplayName("성공: 존재하지 않는 키 조회 시 null을 반환한다")
        void success_null() {
            // given
            String key = "nonExistentKey";
            given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get(key)).willReturn(null);

            // when
            String result = redisService.getValues(key);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("값 삭제 테스트")
    class DeleteValuesTest {

        @Test
        @DisplayName("성공: 키에 해당하는 값을 삭제한다")
        void success() {
            // given
            String key = "key";

            // when
            redisService.deleteValues(key);

            // then
            verify(stringRedisTemplate).delete(key);
        }
    }

    @Nested
    @DisplayName("키 존재 여부 확인 테스트")
    class HasKeyTest {

        @Test
        @DisplayName("성공: 키가 존재하면 true를 반환한다")
        void success_true() {
            // given
            String key = "key";
            given(stringRedisTemplate.hasKey(key)).willReturn(true);

            // when
            boolean result = redisService.hasKey(key);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("성공: 키가 존재하지 않으면 false를 반환한다")
        void success_false() {
            // given
            String key = "key";
            given(stringRedisTemplate.hasKey(key)).willReturn(false);

            // when
            boolean result = redisService.hasKey(key);

            // then
            assertThat(result).isFalse();
        }
    }
}
