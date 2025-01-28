package success.planfit.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.controller.utils.ControllerUtil;
import success.planfit.controller.utils.PlanfitExceptionHandler;
import success.planfit.dto.request.PlanfitUserSignInRequestDto;
import success.planfit.dto.request.PlanfitUserSignUpRequestDto;
import success.planfit.dto.response.AccessTokenResponseDto;
import success.planfit.dto.response.TokenResponseDto;
import success.planfit.service.AuthorizationService;

import java.security.Principal;

/**
 * 회원가입/로그인과 관련된 API를 처리하는 컨트롤러
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthorizationService authorizationService;
    private final ControllerUtil util;
    private final PlanfitExceptionHandler exceptionHandler;

    @PostMapping("/authorization")
    public ResponseEntity<TokenResponseDto> planfitSignUp(@RequestBody PlanfitUserSignUpRequestDto requestDto) {
        log.info("UserController.planfitSignUp() called");

        TokenResponseDto responseDto = authorizationService.planfitSignUp(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/authorization")
    public ResponseEntity<TokenResponseDto> planfitSignIn(@RequestBody PlanfitUserSignInRequestDto requestDto) {
        log.info("UserController.planfitSignIn() called");

        TokenResponseDto responseDto = authorizationService.planfitSignIn(requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/authorization/google")
    public ResponseEntity<Void> googleRedirect() {
        log.info("UserController.googleRedirect() called");

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, authorizationService.getGoogleRedirectUrl())
                .build();
    }

    /**
     * 사용자가 구글 로그인을 마치면, 구글 측의 리다이렉트로 연결될 컨트롤러
     */
    @GetMapping("/authorization/google/callback")
    public ResponseEntity<TokenResponseDto> googleAuthorization(@RequestParam(name = "code") String code) {
        log.info("UserController.googleCallback() called");

        String googleAccessToken = authorizationService.getGoogleAccessToken(code);
        TokenResponseDto responseDto = authorizationService.googleSignUpOrSignIn(googleAccessToken);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/authorization/kakao")
    public ResponseEntity<Void> kakaoRedirect() {
        log.info("UserController.kakaoRedirect() called");

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, authorizationService.getKakaoRedirectUrl())
                .build();
    }

    /**
     * 사용자가 카카오 로그인을 마치면, 카카오 측의 리다이렉트로 연결될 컨트롤러
     */
    @GetMapping("/authorization/kakao/callback")
    public ResponseEntity<TokenResponseDto> kakaoAuthorization(@RequestParam(name = "code") String code) {
        log.info("UserController.kakaoAuthorization() called");

        String kakaoAccessToken = authorizationService.getKakaoAccessToken(code);
        TokenResponseDto responseDto = authorizationService.kakaoSignUpOrSignIn(kakaoAccessToken);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/user/logout")
    public ResponseEntity<Void> logout(Principal principal) {
        log.info("UserController.logout() called");

        Long userId = util.findUserIdByPrincipal(principal);
        authorizationService.invalidateRefreshToken(userId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user/withdraw")
    public ResponseEntity<Void> withdraw(Principal principal) {
        log.info("UserController.withdraw() called");

        Long userId = util.findUserIdByPrincipal(principal);
        authorizationService.deleteUser(userId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/authorization/reissue")
    public ResponseEntity<AccessTokenResponseDto> reissueAccessToken(HttpServletRequest request) {
        log.info("UserController.reissueAccessToken() called");

        String refreshToken = util.getTokenFromServletRequest(request);
        AccessTokenResponseDto responseDto = authorizationService.reissueAccessToken(refreshToken);

        return ResponseEntity.ok(responseDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("UserController.handleException() called");

        return exceptionHandler.handle(exception);
    }
}
