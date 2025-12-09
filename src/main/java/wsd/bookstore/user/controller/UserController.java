package wsd.bookstore.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wsd.bookstore.security.auth.CustomUserDetails;
import wsd.bookstore.user.request.UserUpdateRequest;
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

    @PatchMapping("/me")
    public UserResponse updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userDetails.getUserId(), request);
    }
}
