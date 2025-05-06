package success.planfit.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.global.controller.ControllerUtil;
import success.planfit.global.controller.PlanfitExceptionHandler;
import success.planfit.global.jwt.dto.AccessTokenResponseDto;
import success.planfit.global.jwt.dto.TokenResponseDto;
import success.planfit.user.dto.PlanfitUserSignInRequestDto;
import success.planfit.user.dto.PlanfitUserSignUpRequestDto;
import success.planfit.user.service.GoogleAuthorizationService;
import success.planfit.user.service.KaKaoAuthorizationService;
import success.planfit.user.service.PlanfitAuthorizationService;
import success.planfit.user.service.UserService;

import static org.springframework.http.HttpStatus.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/authorization")
@Tag(
    name = "인증/인가 API",
    description = "회원가입, 로그인 관련 기능"
)
public class AuthorizationController {

    private final ControllerUtil util;
    private final PlanfitExceptionHandler exceptionHandler;

    private final KaKaoAuthorizationService kaKaoAuthorizationService;
    private final PlanfitAuthorizationService planfitAuthorizationService;
    private final GoogleAuthorizationService googleAuthorizationService;
    private final UserService userService;

    @PostMapping("/planfit")
    @Operation(
        summary = "플랜핏 자체 회원가입",
        description = "전달받은 정보를 통해 새로운 회원을 생성합니다"
    )
    public ResponseEntity<TokenResponseDto> planfitSignUp(@Valid @RequestBody PlanfitUserSignUpRequestDto requestDto) {
        log.info("AuthorizationController.planfitSignUp() called");

        TokenResponseDto responseDto = planfitAuthorizationService.planfitSignUp(requestDto);
        return ResponseEntity.status(CREATED).body(responseDto);
    }

    @PostMapping("/planfit/signIn")
    @Operation(
        summary = "플랜핏 로그인",
        description = "아이디와 비밀번호를 통해 JWT를 발급합니다"
    )
    public ResponseEntity<TokenResponseDto> planfitSignIn(@Valid @RequestBody PlanfitUserSignInRequestDto requestDto) {
        log.info("AuthorizationController.planfitSignIn() called");

        TokenResponseDto responseDto = planfitAuthorizationService.planfitSignIn(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/google")
    @Operation(
        summary = "구글 소셜 로그인",
        description = "구글을 통해 사용자를 인증하고 JWT를 발급합니다"
    )
    public ResponseEntity<TokenResponseDto> googleAuthorization(@RequestParam(name = "code") String code) {
        log.info("AuthorizationController.googleCallback() called");

        String googleAccessToken = googleAuthorizationService.getGoogleAccessToken(code);
        TokenResponseDto responseDto = googleAuthorizationService.signUpOrSignIn(googleAccessToken);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/google/redirection")
    @Operation(
        summary = "구글 로그인 화면 리다이렉트",
        description = "구글 로그인 화면으로 리다이렉트합니다"
    )
    public ResponseEntity<Void> googleRedirect() {
        log.info("AuthorizationController.googleRedirect() called");

        return ResponseEntity.status(SEE_OTHER)
                .header(HttpHeaders.LOCATION, googleAuthorizationService.getRedirectUrl())
                .build();
    }

    @GetMapping("/kakao")
    @Operation(
        summary = "카카오 소셜 로그인",
        description = "카카오를 통해 사용자를 인증하고 JWT를 발급합니다"
    )
    public ResponseEntity<TokenResponseDto> kakaoAuthorization(@RequestParam(name = "code") String code) {
        log.info("AuthorizationController.kakaoAuthorization() called");

        String kakaoAccessToken = kaKaoAuthorizationService.getKakaoAccessToken(code);
        TokenResponseDto responseDto = kaKaoAuthorizationService.kakaoSignUpOrSignIn(kakaoAccessToken);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/kakao/redirection")
    @Operation(
        summary = "카카오 로그인 화면 리다이렉트",
        description = "카카오 로그인 화면으로 리다이렉트합니다"
    )
    public ResponseEntity<Void> kakaoRedirect() {
        log.info("AuthorizationController.kakaoRedirect() called");

        return ResponseEntity.status(SEE_OTHER)
                .header(HttpHeaders.LOCATION, kaKaoAuthorizationService.getRedirectUrl())
                .build();
    }

    @GetMapping("/reissue")
    @Operation(
        summary = "Access Token 재발급",
        description = "Refresh Token을 통해 Access Token을 재발급합니다"
    )
    public ResponseEntity<AccessTokenResponseDto> reissueAccessToken(HttpServletRequest request) {
        log.info("AuthorizationController.reissueAccessToken() called");

        String refreshToken = util.getTokenFromServletRequest(request);
        AccessTokenResponseDto responseDto = userService.reissueAccessToken(refreshToken);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/duplication/{id}")
    @Operation(
        summary = "아이디 중복 여부 확인",
        description = "해당 ID를 사용하는 회원이 이미 존재하는지를 검사합니다"
    )
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
