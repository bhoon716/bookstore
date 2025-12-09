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

    public UserResponse getUser(String email) {
        User user = findUserByEmail(email);
        return toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(String email, UserUpdateRequest request) {
        User user = findUserByEmail(email);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            request.setPassword(encodedPassword);
        }

        user.update(request);

        return UserResponse.from(user);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.from(user);
    }
}
