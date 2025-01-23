package success.planfit.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.controller.utils.ControllerUtil;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class Controller {

    private final ControllerUtil controllerUtil;
    private final UserService userService;



    // 회원 정보 조회 API (기본키로 조회)
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable Long userId) {  // userId를 path parameter로 받음
        UserDto userDto = userService.getUserInfo(userId);
        return ResponseEntity.ok(userDto);
    }

    // 회원 정보 수정 API
    @PatchMapping("/update/{userId}")
    public ResponseEntity<Void> updateUserInfo(Principal principal, @RequestBody UserDto userDto) {
        Long userId = controllerUtil.findUserIdByPrincipal(principal);
        userService.updateUserInfo(userId,userDto);
        return ResponseEntity.ok().build();
    }
}
