package success.planfit.controller.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import success.planfit.domain.user.User;
import success.planfit.service.UserService;

import java.security.Principal;

@Slf4j
@Component
@RequiredArgsConstructor
public class ControllerUtil {

    private final UserService userService;

    public User findUserByPrincipal(Principal principal) {
        log.info("ControllerUtil.findUserByPrincipal()");

        long userId = Long.parseLong(principal.getName());

        return userService.findById(userId);
    }
}
