package success.planfit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.dto.request.PlanFitUserSignInRequestDto;
import success.planfit.dto.request.PlanFitUserSignUpRequestDto;
import success.planfit.dto.response.TokenResponseDto;
import success.planfit.service.AuthorizationService;

/**
 * 회원가입/로그인과 관련된 API를 처리하는 컨트롤러
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/authorization")
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<TokenResponseDto> planFitSignUp(@RequestBody PlanFitUserSignUpRequestDto requestDto) {
        log.info("UserController.planFitSignUp() called");

        TokenResponseDto responseDto = authorizationService.planFitSignUp(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<TokenResponseDto> planFitSignIn(@RequestBody PlanFitUserSignInRequestDto requestDto) {
        log.info("UserController.planFitSignIn() called");

        TokenResponseDto responseDto = authorizationService.planFitSignIn(requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/google")
    public ResponseEntity<Void> googleRedirect() {
        log.info("UserController.googleRedirect() called");

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, authorizationService.getGoogleRedirectUrl())
                .build();
    }

    /**
     * 사용자가 구글 로그인을 마치면, 구글 측의 리다이렉트로 연결될 컨트롤러
     */
    @GetMapping("/google/callback")
    public ResponseEntity<TokenResponseDto> googleAuthorization(@RequestParam(name = "code") String code) {
        log.info("UserController.googleCallback() called");

        String googleAccessToken = authorizationService.getGoogleAccessToken(code);
        TokenResponseDto responseDto = authorizationService.googleSignUpOrSignIn(googleAccessToken);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/kakao")
    public ResponseEntity<Void> kakaoRedirect() {
        log.info("UserController.kakaoRedirect() called");

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, authorizationService.getKakaoRedirectUrl())
                .build();
    }

    /**
     * 사용자가 카카오 로그인을 마치면, 카카오 측의 리다이렉트로 연결될 컨트롤러
     */
    @GetMapping("/kakao/callback")
    public ResponseEntity<TokenResponseDto> kakaoAuthorization(@RequestParam(name = "code") String code) {
        log.info("UserController.kakaoAuthorization() called");

        String kakaoAccessToken = authorizationService.getKakaoAccessToken(code);
        TokenResponseDto responseDto = authorizationService.kakaoSignUpOrSignIn(kakaoAccessToken);

        return ResponseEntity.ok(responseDto);
    }
}
