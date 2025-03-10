package success.planfit.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.global.controller.ControllerUtil;
import success.planfit.global.controller.PlanfitExceptionHandler;
import success.planfit.user.dto.PlanfitUserSignInRequestDto;
import success.planfit.user.dto.PlanfitUserSignUpRequestDto;
import success.planfit.global.jwt.dto.AccessTokenResponseDto;
import success.planfit.global.jwt.dto.TokenResponseDto;
import success.planfit.user.service.AuthorizationService;

import java.security.Principal;

import static org.springframework.http.HttpStatus.*;

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

    @PostMapping("/authorization/planfit")
    public ResponseEntity<TokenResponseDto> planfitSignUp(@Valid @RequestBody PlanfitUserSignUpRequestDto requestDto) {
        log.info("UserController.planfitSignUp() called");

        TokenResponseDto responseDto = authorizationService.planfitSignUp(requestDto);

        return ResponseEntity.status(CREATED).body(responseDto);
    }

    @PostMapping("/authorization/planfit/signIn")
    public ResponseEntity<TokenResponseDto> planfitSignIn(@Valid @RequestBody PlanfitUserSignInRequestDto requestDto) {
        log.info("UserController.planfitSignIn() called");

        TokenResponseDto responseDto = authorizationService.planfitSignIn(requestDto);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 구글 로그인/회원가입
     */
    @GetMapping("/authorization/google")
    public ResponseEntity<TokenResponseDto> googleAuthorization(@RequestParam(name = "code") String code) {
        log.info("UserController.googleCallback() called");

        String googleAccessToken = authorizationService.getGoogleAccessToken(code);
        TokenResponseDto responseDto = authorizationService.googleSignUpOrSignIn(googleAccessToken);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 구글 로그인 화면으로 리다이렉트
     */
    @GetMapping("/authorization/google/redirection")
    public ResponseEntity<Void> googleRedirect() {
        log.info("UserController.googleRedirect() called");

        return ResponseEntity.status(SEE_OTHER)
                .header(HttpHeaders.LOCATION, authorizationService.getGoogleRedirectUrl())
                .build();
    }

    /**
     * 카카오 로그인/회원가입
     */
    @GetMapping("/authorization/kakao")
    public ResponseEntity<TokenResponseDto> kakaoAuthorization(@RequestParam(name = "code") String code) {
        log.info("UserController.kakaoAuthorization() called");

        String kakaoAccessToken = authorizationService.getKakaoAccessToken(code);
        TokenResponseDto responseDto = authorizationService.kakaoSignUpOrSignIn(kakaoAccessToken);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 카카오 로그인 화면으로 리다이렉트
     */
    @GetMapping("/authorization/kakao/redirection")
    public ResponseEntity<Void> kakaoRedirect() {
        log.info("UserController.kakaoRedirect() called");

        return ResponseEntity.status(SEE_OTHER)
                .header(HttpHeaders.LOCATION, authorizationService.getKakaoRedirectUrl())
                .build();
    }

    /**
     * 로그아웃(리프레쉬 토큰 만료)
     */
    @DeleteMapping("/user/logout")
    public ResponseEntity<Void> logout(Principal principal) {
        log.info("UserController.logout() called");

        Long userId = util.findUserIdByPrincipal(principal);
        authorizationService.invalidateRefreshToken(userId);

        return ResponseEntity.ok().build();
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/user/withdraw")
    public ResponseEntity<Void> withdraw(Principal principal) {
        log.info("UserController.withdraw() called");

        Long userId = util.findUserIdByPrincipal(principal);
        authorizationService.deleteUser(userId);

        return ResponseEntity.ok().build();
    }

    /**
     * 엑세스 토큰 재발급
     */
    @GetMapping("/authorization/reissue")
    public ResponseEntity<AccessTokenResponseDto> reissueAccessToken(HttpServletRequest request) {
        log.info("UserController.reissueAccessToken() called");

        String refreshToken = util.getTokenFromServletRequest(request);
        AccessTokenResponseDto responseDto = authorizationService.reissueAccessToken(refreshToken);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 아이디 중복 여부 확인
     */
    @GetMapping("/authorization/duplication/{id}")
    public ResponseEntity<String> idDuplicationCheck(@PathVariable(name = "id") String loginId) {
        log.info("UserController.idDuplicateCheck() called");

        if (authorizationService.isDuplicatedLoginId(loginId)) {
            return ResponseEntity.status(CONFLICT).body("해당 아이디가 이미 존재합니다.");
        }

        return ResponseEntity.ok("아직 사용되지 않은 아이디입니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("UserController.handleException() called");

        return exceptionHandler.handle(exception);
    }
}
