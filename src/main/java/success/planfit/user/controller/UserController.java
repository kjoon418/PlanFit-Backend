package success.planfit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.global.controller.ControllerUtil;
import success.planfit.global.controller.PlanfitExceptionHandler;
import success.planfit.user.dto.UserUpdateDto;
import success.planfit.user.service.UserService;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final ControllerUtil controllerUtil;
    private final UserService userService;
    private final PlanfitExceptionHandler exceptionHandler;

    /**
     * 회원 정보 조회
     */
    @GetMapping
    public ResponseEntity<UserUpdateDto> getUserInfo(Principal principal) {
        Long userId = controllerUtil.findUserIdByPrincipal(principal);
        UserUpdateDto userInfo = userService.getUserInfo(userId);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 회원 정보 수정
     */
    @PatchMapping("/update")
    public ResponseEntity<Void> updateUserInfo(Principal principal, @RequestBody UserUpdateDto userDto) {
        Long userId = controllerUtil.findUserIdByPrincipal(principal);
        userService.updateUserInfo(userId, userDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 로그아웃(리프레쉬 토큰 만료)
     */
    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(Principal principal) {
        log.info("UserController.logout() called");

        long userId = controllerUtil.findUserIdByPrincipal(principal);
        userService.invalidateRefreshToken(userId);

        return ResponseEntity.ok().build();
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(Principal principal) {
        log.info("UserController.withdraw() called");

        long userId = controllerUtil.findUserIdByPrincipal(principal);
        userService.deleteUser(userId);

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("UserController.handleException() called");

        return exceptionHandler.handle(exception);
    }
}
