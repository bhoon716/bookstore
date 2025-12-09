package wsd.bookstore.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;
import wsd.bookstore.security.jwt.JwtTokenProvider;
import wsd.bookstore.user.entity.User;
import wsd.bookstore.user.repository.UserRepository;
import wsd.bookstore.user.request.LoginRequest;
import wsd.bookstore.user.request.SignupRequest;
import wsd.bookstore.user.response.LoginResponse;
import wsd.bookstore.user.response.SignupResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .username(request.getUsername())
                .role(request.getRole())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);

        // 회원가입 응답: 토큰 없이 기본 정보만 반환
        return new SignupResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getUsername());
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.generateAccess(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefresh(user.getId());

        return new LoginResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getUsername());
    }
}
