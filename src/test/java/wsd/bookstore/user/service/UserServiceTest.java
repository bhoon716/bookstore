package wsd.bookstore.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;
import wsd.bookstore.user.entity.UserRole;
import wsd.bookstore.user.entity.User;
import wsd.bookstore.user.repository.UserRepository;
import wsd.bookstore.user.request.ProfileUpdateRequest;
import wsd.bookstore.user.response.UserResponse;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("회원 정보 조회 테스트")
    class GetUserTest {

        @Test
        @DisplayName("성공: 회원 정보를 성공적으로 조회해야 한다")
        void success() {
            // given
            Long userId = 1L;
            User user = User.builder()
                    .email("test@test.com")
                    .role(UserRole.USER)
                    .username("testUser")
                    .build();
            ReflectionTestUtils.setField(user, "id", userId);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when
            UserResponse response = userService.getUser(userId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getEmail()).isEqualTo(user.getEmail());
            assertThat(response.getUsername()).isEqualTo(user.getUsername());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 회원 조회 시 예외가 발생해야 한다")
        void fail_userNotFound() {
            // given
            Long userId = 1L;
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUser(userId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_USER);
        }
    }

    @Nested
    @DisplayName("프로필 수정 테스트")
    class UpdateProfileTest {

        @Test
        @DisplayName("성공: 프로필을 성공적으로 수정해야 한다")
        void success() {
            // given
            Long userId = 1L;
            ProfileUpdateRequest request = new ProfileUpdateRequest();
            ReflectionTestUtils.setField(request, "username", "updatedName");
            ReflectionTestUtils.setField(request, "address", "updatedAddress");
            ReflectionTestUtils.setField(request, "phoneNumber", "010-5678-1234");

            User user = User.builder()
                    .email("test@test.com")
                    .role(UserRole.USER)
                    .username("oldName")
                    .address("oldAddress")
                    .phoneNumber("010-1234-5678")
                    .build();
            ReflectionTestUtils.setField(user, "id", userId);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when
            UserResponse response = userService.updateProfile(userId, request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getUsername()).isEqualTo(request.getUsername());
            assertThat(user.getAddress()).isEqualTo(request.getAddress());
            assertThat(user.getPhoneNumber()).isEqualTo(request.getPhoneNumber());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 회원 프로필 수정 시 예외가 발생해야 한다")
        void fail_userNotFound() {
            // given
            Long userId = 1L;
            ProfileUpdateRequest request = new ProfileUpdateRequest();
            ReflectionTestUtils.setField(request, "username", "updatedName");
            ReflectionTestUtils.setField(request, "address", "updatedAddress");
            ReflectionTestUtils.setField(request, "phoneNumber", "010-5678-1234");

            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.updateProfile(userId, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_USER);
        }
    }

    @Nested
    @DisplayName("비밀번호 변경 테스트")
    class UpdatePasswordTest {

        @Test
        @DisplayName("성공: 비밀번호를 성공적으로 변경해야 한다")
        void success() {
            // given
            Long userId = 1L;
            String newPassword = "newPassword";
            String encodedPassword = "encodedNewPassword";
            User user = User.builder()
                    .email("test@test.com")
                    .password("oldPassword")
                    .role(UserRole.USER)
                    .build();
            ReflectionTestUtils.setField(user, "id", userId);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(passwordEncoder.encode(newPassword)).willReturn(encodedPassword);

            // when
            userService.updatePassword(userId, newPassword);

            // then
            assertThat(user.getPassword()).isEqualTo(encodedPassword);
            verify(passwordEncoder).encode(newPassword);
        }

        @Test
        @DisplayName("성공: 새로운 비밀번호가 없으면 변경하지 않아야 한다")
        void success_noNewPassword() {
            // given
            Long userId = 1L;
            String newPassword = null;
            String oldPassword = "oldPassword";
            User user = User.builder()
                    .email("test@test.com")
                    .password(oldPassword)
                    .role(UserRole.USER)
                    .build();
            ReflectionTestUtils.setField(user, "id", userId);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when
            userService.updatePassword(userId, newPassword);

            // then
            assertThat(user.getPassword()).isEqualTo(oldPassword);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 회원 비밀번호 변경 시 예외가 발생해야 한다")
        void fail_userNotFound() {
            // given
            Long userId = 1L;
            String newPassword = "newPassword";
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.updatePassword(userId, newPassword))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_USER);
        }
    }

    @Nested
    @DisplayName("회원 탈퇴 테스트")
    class WithdrawTest {

        @Test
        @DisplayName("성공: 회원 탈퇴를 성공적으로 처리해야 한다")
        void success() {
            // given
            Long userId = 1L;
            User user = User.builder()
                    .email("test@test.com")
                    .role(UserRole.USER)
                    .build();
            ReflectionTestUtils.setField(user, "id", userId);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when
            userService.withdraw(userId);

            // then
            verify(userRepository).delete(user);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 회원 탈퇴 시 예외가 발생해야 한다")
        void fail_userNotFound() {
            // given
            Long userId = 1L;
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.withdraw(userId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_USER);
        }
    }
}
