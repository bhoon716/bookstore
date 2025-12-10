package wsd.bookstore.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public UserResponse getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.getUser(userDetails.getUserId());
    }

    @PutMapping("/me")
    public UserResponse updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProfileUpdateRequest request) {
        return userService.updateProfile(userDetails.getUserId(), request);
    }

    @PatchMapping("/me/password")
    public void updateMyPassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordUpdateRequest request) {
        userService.updatePassword(userDetails.getUserId(), request.getPassword());
    }

    @DeleteMapping("/me")
    public void withdraw(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.withdraw(userDetails.getUserId());
    }
}
