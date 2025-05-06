package success.planfit.global.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import success.planfit.global.jwt.TokenProvider;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class ControllerUtil {

    private final TokenProvider tokenProvider;

    public long findUserIdByPrincipal(Principal principal) {
        return Long.parseLong(principal.getName());
    }

    public String getTokenFromServletRequest(HttpServletRequest request) {
        return tokenProvider.resolveToken(request);
    }

}
