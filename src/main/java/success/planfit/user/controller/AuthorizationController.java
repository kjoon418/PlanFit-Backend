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
import success.planfit.user.service.AuthorizationService;

import java.security.Principal;

import static org.springframework.http.HttpStatus.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(
        name = "인증/인가 API",
        description = "회원가입, 로그인 관련 기능"
)
public class AuthorizationController {

    private final AuthorizationService authorizationService;
    private final ControllerUtil util;
    private final PlanfitExceptionHandler exceptionHandler;

    @PostMapping("/authorization/planfit")
    @Operation(
            summary = "플랜핏 자체 회원가입",
            description = "전달받은 정보를 통해 새로운 회원을 생성합니다"
    )
    public ResponseEntity<TokenResponseDto> planfitSignUp(@Valid @RequestBody PlanfitUserSignUpRequestDto requestDto) {
        log.info("UserController.planfitSignUp() called");

        TokenResponseDto responseDto = authorizationService.planfitSignUp(requestDto);

        return ResponseEntity.status(CREATED).body(responseDto);
    }

    @PostMapping("/authorization/planfit/signIn")
    @Operation(
            summary = "플랜핏 로그인",
            description = "아이디와 비밀번호를 통해 JWT를 발급합니다"
    )
    public ResponseEntity<TokenResponseDto> planfitSignIn(@Valid @RequestBody PlanfitUserSignInRequestDto requestDto) {
        log.info("UserController.planfitSignIn() called");

        TokenResponseDto responseDto = authorizationService.planfitSignIn(requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/authorization/google")
    @Operation(
            summary = "구글 소셜 로그인",
            description = "구글을 통해 사용자를 인증하고 JWT를 발급합니다"
    )
    public ResponseEntity<TokenResponseDto> googleAuthorization(@RequestParam(name = "code") String code) {
        log.info("UserController.googleCallback() called");

        String googleAccessToken = authorizationService.getGoogleAccessToken(code);
        TokenResponseDto responseDto = authorizationService.googleSignUpOrSignIn(googleAccessToken);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/authorization/google/redirection")
    @Operation(
            summary = "구글 로그인 화면 리다이렉트",
            description = "구글 로그인 화면으로 리다이렉트합니다"
    )
    public ResponseEntity<Void> googleRedirect() {
        log.info("UserController.googleRedirect() called");

        return ResponseEntity.status(SEE_OTHER)
                .header(HttpHeaders.LOCATION, authorizationService.getGoogleRedirectUrl())
                .build();
    }

    @GetMapping("/authorization/kakao")
    @Operation(
            summary = "카카오 소셜 로그인",
            description = "카카오를 통해 사용자를 인증하고 JWT를 발급합니다"
    )
    public ResponseEntity<TokenResponseDto> kakaoAuthorization(@RequestParam(name = "code") String code) {
        log.info("UserController.kakaoAuthorization() called");

        String kakaoAccessToken = authorizationService.getKakaoAccessToken(code);
        TokenResponseDto responseDto = authorizationService.kakaoSignUpOrSignIn(kakaoAccessToken);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/authorization/kakao/redirection")
    @Operation(
            summary = "카카오 로그인 화면 리다이렉트",
            description = "카카오 로그인 화면으로 리다이렉트합니다"
    )
    public ResponseEntity<Void> kakaoRedirect() {
        log.info("UserController.kakaoRedirect() called");

        return ResponseEntity.status(SEE_OTHER)
                .header(HttpHeaders.LOCATION, authorizationService.getKakaoRedirectUrl())
                .build();
    }

    @DeleteMapping("/user/logout")
    @Operation(
            summary = "로그아웃",
            description = "해당 회원의 Refresh Token을 무효화합니다(Access Token은 자체 폐기 필요)"
    )
    public ResponseEntity<Void> logout(Principal principal) {
        log.info("UserController.logout() called");

        Long userId = util.findUserIdByPrincipal(principal);
        authorizationService.invalidateRefreshToken(userId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user/withdraw")
    @Operation(
            summary = "회원 탈퇴",
            description = "해당 회원 정보를 삭제합니다"
    )
    public ResponseEntity<Void> withdraw(Principal principal) {
        log.info("UserController.withdraw() called");

        Long userId = util.findUserIdByPrincipal(principal);
        authorizationService.deleteUser(userId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/authorization/reissue")
    @Operation(
            summary = "Access Token 재발급",
            description = "Refresh Token을 통해 Access Token을 재발급합니다"
    )
    public ResponseEntity<AccessTokenResponseDto> reissueAccessToken(HttpServletRequest request) {
        log.info("UserController.reissueAccessToken() called");

        String refreshToken = util.getTokenFromServletRequest(request);
        AccessTokenResponseDto responseDto = authorizationService.reissueAccessToken(refreshToken);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/authorization/duplication/{id}")
    @Operation(
            summary = "아이디 중복 여부 확인",
            description = "해당 ID를 사용하는 회원이 이미 존재하는지를 검사합니다"
    )
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
