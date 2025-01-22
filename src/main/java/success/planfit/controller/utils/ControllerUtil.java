package success.planfit.controller.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import success.planfit.jwt.TokenProvider;
import success.planfit.service.UserService;

import java.security.Principal;

@Slf4j
@Component
@RequiredArgsConstructor
public class ControllerUtil {

    private final UserService userService;
    private final TokenProvider tokenProvider;

    public Long findUserIdByPrincipal(Principal principal) {
        log.info("ControllerUtil.findUserByPrincipal()");

        return Long.parseLong(principal.getName());
    }

    public String getTokenFromServletRequest(HttpServletRequest request) {
        log.info("ControllerUtil.getTokenFromServletRequest()");

        return tokenProvider.resolveToken(request);
    }
}
