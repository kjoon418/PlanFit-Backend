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
import success.planfit.user.service.KaKaoAuthorizationService;
import success.planfit.user.service.GoogleAuthorizationService;
import success.planfit.user.service.PlanfitAuthorizationService;
import success.planfit.user.service.UserService;

import java.security.Principal;

import static org.springframework.http.HttpStatus.*;

/**
 * 회원가입/로그인과 관련된 API를 처리하는 컨트롤러
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/authorization")
public class AuthorizationController {

    private final ControllerUtil util;
    private final PlanfitExceptionHandler exceptionHandler;

    private final KaKaoAuthorizationService kaKaoAuthorizationService;
    private final PlanfitAuthorizationService planfitAuthorizationService;
    private final GoogleAuthorizationService googleAuthorizationService;
    private final UserService userService;

    /**
     * 플랜핏 회원가입
     */
    @PostMapping("/planfit")
    public ResponseEntity<TokenResponseDto> planfitSignUp(@Valid @RequestBody PlanfitUserSignUpRequestDto requestDto) {
        log.info("AuthorizationController.planfitSignUp() called");

        TokenResponseDto responseDto = planfitAuthorizationService.planfitSignUp(requestDto);

        return ResponseEntity.status(CREATED).body(responseDto);
    }

    /**
     * 플랜핏 로그인
     */
    @PostMapping("/planfit/signIn")
    public ResponseEntity<TokenResponseDto> planfitSignIn(@Valid @RequestBody PlanfitUserSignInRequestDto requestDto) {
        log.info("AuthorizationController.planfitSignIn() called");

        TokenResponseDto responseDto = planfitAuthorizationService.planfitSignIn(requestDto);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 구글 로그인/회원가입
     */
    @GetMapping("/google")
    public ResponseEntity<TokenResponseDto> googleAuthorization(@RequestParam(name = "code") String code) {
        log.info("AuthorizationController.googleCallback() called");

        String googleAccessToken = googleAuthorizationService.getGoogleAccessToken(code);
        TokenResponseDto responseDto = googleAuthorizationService.signUpOrSignIn(googleAccessToken);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 구글 로그인 화면으로 리다이렉트
     */
    @GetMapping("/google/redirection")
    public ResponseEntity<Void> googleRedirect() {
        log.info("AuthorizationController.googleRedirect() called");

        return ResponseEntity.status(SEE_OTHER)
                .header(HttpHeaders.LOCATION, googleAuthorizationService.getRedirectUrl())
                .build();
    }

    /**
     * 카카오 로그인/회원가입
     */
    @GetMapping("/kakao")
    public ResponseEntity<TokenResponseDto> kakaoAuthorization(@RequestParam(name = "code") String code) {
        log.info("AuthorizationController.kakaoAuthorization() called");

        String kakaoAccessToken = kaKaoAuthorizationService.getKakaoAccessToken(code);
        TokenResponseDto responseDto = kaKaoAuthorizationService.kakaoSignUpOrSignIn(kakaoAccessToken);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 카카오 로그인 화면으로 리다이렉트
     */
    @GetMapping("/kakao/redirection")
    public ResponseEntity<Void> kakaoRedirect() {
        log.info("AuthorizationController.kakaoRedirect() called");

        return ResponseEntity.status(SEE_OTHER)
                .header(HttpHeaders.LOCATION, kaKaoAuthorizationService.getRedirectUrl())
                .build();
    }

    /**
     * 엑세스 토큰 재발급
     */
    @GetMapping("/reissue")
    public ResponseEntity<AccessTokenResponseDto> reissueAccessToken(HttpServletRequest request) {
        log.info("AuthorizationController.reissueAccessToken() called");

        String refreshToken = util.getTokenFromServletRequest(request);
        AccessTokenResponseDto responseDto = userService.reissueAccessToken(refreshToken);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 아이디 중복 여부 확인
     */
    @GetMapping("/duplication/{id}")
    public ResponseEntity<String> idDuplicationCheck(@PathVariable(name = "id") String loginId) {
        log.info("AuthorizationController.idDuplicateCheck() called");

        if (planfitAuthorizationService.isDuplicatedLoginId(loginId)) {
            return ResponseEntity.status(CONFLICT).body("해당 아이디가 이미 존재합니다.");
        }

        return ResponseEntity.ok("아직 사용되지 않은 아이디입니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("AuthorizationController.handleException() called");

        return exceptionHandler.handle(exception);
    }

}
