package success.planfit.global.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import success.planfit.global.jwt.TokenProvider;

import java.security.Principal;

@Slf4j
@Component
@RequiredArgsConstructor
public class ControllerUtil {

    private final TokenProvider tokenProvider;

    public long findUserIdByPrincipal(Principal principal) {
        log.info("ControllerUtil.findUserByPrincipal()");

        return Long.parseLong(principal.getName());
    }

    public String getTokenFromServletRequest(HttpServletRequest request) {
        log.info("ControllerUtil.getTokenFromServletRequest()");

        return tokenProvider.resolveToken(request);
    }
}
