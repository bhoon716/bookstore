package wsd.bookstore.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wsd.bookstore.common.response.ApiResponse;
import wsd.bookstore.security.auth.CustomUserDetails;
import wsd.bookstore.user.request.PasswordUpdateRequest;
import wsd.bookstore.user.request.ProfileUpdateRequest;
import wsd.bookstore.user.response.UserResponse;
import wsd.bookstore.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = userService.getUser(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "내 정보 조회 성공"));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProfileUpdateRequest request) {
        userService.updateProfile(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.noContent("내 정보 수정 성공"));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updateMyPassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordUpdateRequest request) {
        userService.updatePassword(userDetails.getUserId(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.noContent("비밀번호 변경 성공"));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.withdraw(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.noContent("회원 탈퇴 성공"));
    }
}
