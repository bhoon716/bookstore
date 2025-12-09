package wsd.bookstore.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;
import wsd.bookstore.user.entity.User;
import wsd.bookstore.user.repository.UserRepository;
import wsd.bookstore.user.request.UserUpdateRequest;
import wsd.bookstore.user.response.UserResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String newPassword = request.getPassword();
        if (newPassword != null && !newPassword.isBlank()) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            newPassword = encodedPassword;
        }

        user.update(
                newPassword,
                request.getUsername(),
                request.getAddress(),
                request.getPhoneNumber());

        return UserResponse.from(user);
    }
}
