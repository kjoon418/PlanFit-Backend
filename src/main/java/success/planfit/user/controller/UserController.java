package success.planfit.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "회원 API",
        description = "회원 조회/수정/삭제 관련 기능"
)
public class UserController {

    private final ControllerUtil controllerUtil;
    private final UserService userService;
    private final PlanfitExceptionHandler exceptionHandler;

    @GetMapping
    @Operation(
            summary = "회원 정보 조회",
            description = "JWT 토큰을 통해 회원 정보를 조회합니다"
    )
    public ResponseEntity<UserUpdateDto> getUserInfo(Principal principal) {
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        UserUpdateDto userInfo = userService.getUserInfo(userId);
        return ResponseEntity.ok(userInfo);
    }

    @PatchMapping
    @Operation(
            summary = "회원 정보 수정",
            description = "기존의 회원 정보를 새로 덮어씁니다"
    )
    public ResponseEntity<Void> updateUserInfo(Principal principal, @RequestBody UserUpdateDto userDto) {
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        userService.updateUserInfo(userId, userDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/logout")
    @Operation(
            summary = "로그아웃(리프레쉬 토큰 만료)",
            description = "해당 회원에 할당된 리프레쉬 토큰을 만료시킵니다. 엑세스 토큰은 클라이언트에서 별도로 폐기해야 합니다."
    )
    public ResponseEntity<Void> logout(Principal principal) {
        log.info("UserController.logout() called");

        long userId = controllerUtil.findUserIdByPrincipal(principal);
        userService.invalidateRefreshToken(userId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/withdraw")
    @Operation(
            summary = "회원 탈퇴",
            description = "해당 회원 정보를 삭제합니다."
    )
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
