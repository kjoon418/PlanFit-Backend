package success.planfit.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import success.planfit.domain.user.GoogleUser;
import success.planfit.domain.user.KakaoUser;
import success.planfit.domain.user.PlanfitUser;
import success.planfit.domain.user.User;
import success.planfit.dto.google.GoogleAccessTokenDto;
import success.planfit.dto.google.GoogleUserInfoDto;
import success.planfit.dto.kakao.KaKaoUserInfoDto;
import success.planfit.dto.kakao.KakaoAccessTokenDto;
import success.planfit.dto.request.PlanfitUserSignInRequestDto;
import success.planfit.dto.request.PlanfitUserSignUpRequestDto;
import success.planfit.dto.response.AccessTokenResponseDto;
import success.planfit.dto.response.TokenResponseDto;
import success.planfit.exception.EntityNotFoundException;
import success.planfit.exception.IllegalRequestException;
import success.planfit.jwt.TokenProvider;
import success.planfit.jwt.TokenType;
import success.planfit.photo.PhotoProvider;
import success.planfit.repository.UserRepository;

import java.net.URI;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class AuthorizationService {

    // 의존성 관련 필드
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    // 구글 로그인 관련 필드
    private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private final String GOOGLE_CLIENT_ID;
    private final String GOOGLE_CLIENT_SECRET;
    private final String GOOGLE_REDIRECT_URI;

    // 카카오 로그인 관련 필드
    private final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private final String KAKAO_CLIENT_ID;
    private final String KAKAO_CLIENT_SECRET;
    private final String KAKAO_REDIRECT_URL;

    public AuthorizationService(
            UserRepository userRepository,
            TokenProvider tokenProvider,
            @Value("${keys.google.client-id}") String googleClientId,
            @Value("${keys.google.client-secret}") String googleClientSecret,
            @Value("${keys.google.redirect-uri}") String googleRedirectUri,
            @Value("${keys.kakao.rest-api-key}") String kakaoClientId,
            @Value("${keys.kakao.client-secret}") String kakaoClientSecret,
            @Value("${keys.kakao.redirect-uri}") String kakaoRedirectUri
    ) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;

        this.GOOGLE_CLIENT_ID = googleClientId;
        this.GOOGLE_CLIENT_SECRET = googleClientSecret;
        this.GOOGLE_REDIRECT_URI = googleRedirectUri;

        this.KAKAO_CLIENT_ID = kakaoClientId;
        this.KAKAO_CLIENT_SECRET = kakaoClientSecret;
        this.KAKAO_REDIRECT_URL = kakaoRedirectUri;
    }

    public TokenResponseDto planfitSignUp(PlanfitUserSignUpRequestDto requestDto) {
        log.info("AuthorizationService.planfitSignUp() called");

        // User 엔티티를 영속화
        PlanfitUser user = requestDto.toEntity();
        userRepository.save(user);

        // JWT 토큰을 발급 및 RefreshToken 엔티티 값 갱신
        String accessTokenValue = tokenProvider.createToken(user, TokenType.ACCESS);
        String refreshTokenValue = tokenProvider.createToken(user, TokenType.REFRESH);
        user.getRefreshToken().setTokenValue(refreshTokenValue);

        // TokenResponseDto를 반환
        return TokenResponseDto.builder()
                .accessToken(accessTokenValue)
                .refreshToken(refreshTokenValue)
                .build();
    }

    public TokenResponseDto planfitSignIn(PlanfitUserSignInRequestDto requestDto) {
        log.info("AuthorizationService.planfitSignIn() called");

        // DTO로부터 값을 추출
        String loginId = requestDto.getLoginId();
        String password = requestDto.getPassword();

        // 아이디와 비밀번호를 통해 유저 조회
        User user = userRepository.findByLoginIdAndPassword(loginId, password)
                .orElseThrow(() -> new EntityNotFoundException("해당 아이디와 비밀번호를 가진 회원을 찾을 수 없습니다."));

        // JWT 토큰 발급 및 RefreshToken 엔티티 값 갱신
        String accessTokenValue = tokenProvider.createToken(user, TokenType.ACCESS);
        String refreshTokenValue = tokenProvider.createToken(user, TokenType.REFRESH);
        user.getRefreshToken().setTokenValue(refreshTokenValue);

        // TokenResponseDto를 반환
        return TokenResponseDto.builder()
                .accessToken(accessTokenValue)
                .refreshToken(refreshTokenValue)
                .build();
    }

    public String getGoogleRedirectUrl() {
        log.info("AuthorizationService.getGoogleAuthorizationRedirect() called");

        StringBuilder url = new StringBuilder()
                .append("https://accounts.google.com/o/oauth2/v2/auth?client_id=")
                .append(GOOGLE_CLIENT_ID)
                .append("&redirect_uri=")
                .append(GOOGLE_REDIRECT_URI)
                .append("&response_type=code&scope=https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email");

        return url.toString();
    }

    public String getGoogleAccessToken(String code) {
        log.info("AuthorizationService.getGoogleAccessToken() called");

        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = Map.of(
                "code", code,
                "scope", "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email",
                "client_id", GOOGLE_CLIENT_ID,
                "client_secret", GOOGLE_CLIENT_SECRET,
                "redirect_uri", GOOGLE_REDIRECT_URI,
                "grant_type", "authorization_code"
        );

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_URL, params, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();

            return gson.fromJson(json, GoogleAccessTokenDto.class)
                    .getAccessToken();
        }

        throw new RuntimeException("구글 엑세스 토큰 획득 실패");
    }

    public TokenResponseDto googleSignUpOrSignIn(String googleAccessToken) {
        log.info("AuthorizationService.googleSignUpOrSignIn() called");

        GoogleUserInfoDto googleUserInfo = getGoogleUserInfo(googleAccessToken);

        // 유효성 검사
        if (!googleUserInfo.getVerifiedEmail()) {
            throw new IllegalRequestException("이메일 인증이 되지 않은 유저입니다.");
        }

        // 기존에 회원가입 하지 않은 회원이라면 회원가입함
        User user = userRepository.findByGoogleIdentifier(googleUserInfo.getId())
                .orElseGet(() -> userRepository.save(GoogleUser.builder()
                        .googleIdentifier(googleUserInfo.getId())
                        .email(googleUserInfo.getEmail())
                        .name(googleUserInfo.getName())
                        .profilePhoto(PhotoProvider.getImageFromUrl(googleUserInfo.getPictureUrl()))
                        .build())
                );

        // JWT 토큰 생성 및 RefreshToken 엔티티 값 수정
        String accessTokenValue = tokenProvider.createToken(user, TokenType.ACCESS);
        String refreshTokenValue = tokenProvider.createToken(user, TokenType.REFRESH);
        user.getRefreshToken().setTokenValue(refreshTokenValue);

        // TokenResponseDto 반환
        return TokenResponseDto.builder()
                .accessToken(accessTokenValue)
                .refreshToken(refreshTokenValue)
                .build();
    }

    private GoogleUserInfoDto getGoogleUserInfo(String accessToken) {
        log.info("AuthorizationService.getGoogleUserInfo() called");

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();
            return gson.fromJson(json, GoogleUserInfoDto.class);
        }

        throw new RuntimeException("구글 유저 정보 획득 실패");
    }

    public String getKakaoRedirectUrl() {
        log.info("AuthorizationService.getKakaoRedirectUrl() called");

        StringBuilder url = new StringBuilder()
                .append("https://kauth.kakao.com/oauth/authorize?")
                .append("client_id=")
                .append(KAKAO_CLIENT_ID)
                .append("&redirect_uri=")
                .append(KAKAO_REDIRECT_URL)
                .append("&response_type=code")
                .append("&scope=profile_nickname,profile_image,account_email");

        return url.toString();
    }

    public String getKakaoAccessToken(String code) {
        log.info("AuthorizationService.getKakaoAccessToken() called");

        RestTemplate restTemplate = new RestTemplate();

        // 메시지 header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 메시지 body 설정
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_CLIENT_ID);
        params.add("redirect_uri", KAKAO_REDIRECT_URL);
        params.add("client_secret", KAKAO_CLIENT_SECRET);

        // POST 요청 전송
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(KAKAO_TOKEN_URL, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();

            return gson.fromJson(json, KakaoAccessTokenDto.class)
                    .getAccessToken();
        }

        throw new RuntimeException("카카오 엑세스 토큰 획득 실패");
    }

    public TokenResponseDto kakaoSignUpOrSignIn(String kakaoAccessToken) {
        log.info("AuthorizationService.kakaoSignUpOrSignIn() called");

        KaKaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);

        // 유효성 검사
        if (!kakaoUserInfo.isValidatedEmail()) {
            throw new IllegalRequestException("이메일 인증이 되지 않은 유저입니다.");
        }

        // 기존에 회원가입 하지 않은 회원이라면 회원가입함
        User user = userRepository.findByKakaoIdentifier(kakaoUserInfo.getId())
                .orElseGet(() -> userRepository.save(KakaoUser.builder()
                        .kakaoIdentifier(kakaoUserInfo.getId())
                        .email(kakaoUserInfo.getEmail())
                        .name(kakaoUserInfo.getName())
                        .profilePhoto(PhotoProvider.getImageFromUrl(kakaoUserInfo.getProfileUrl()))
                        .build())
                );

        // JWT 토큰 생성 및 RefreshToken 엔티티 값 수정
        String accessTokenValue = tokenProvider.createToken(user, TokenType.ACCESS);
        String refreshTokenValue = tokenProvider.createToken(user, TokenType.REFRESH);
        user.getRefreshToken().setTokenValue(refreshTokenValue);

        // TokenResponseDto 반환
        return TokenResponseDto.builder()
                .accessToken(accessTokenValue)
                .refreshToken(refreshTokenValue)
                .build();
    }

    private KaKaoUserInfoDto getKakaoUserInfo(String accessToken) {
        log.info("AuthorizationService.getKakaoUserInfo() called");

        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(KAKAO_USER_INFO_URL, HttpMethod.GET, httpEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();
            return gson.fromJson(json, KaKaoUserInfoDto.class);
        }

        throw new RuntimeException("카카오 유저 정보 획득 실패");
    }

    public void invalidateRefreshToken(Long userId) {
        log.info("AuthorizationService.invalidateRefreshToken() called");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("ID를 통해 유저 조회 실패"));
        user.getRefreshToken().setTokenValue(null);
    }

    public void deleteUser(Long userId) {
        log.info("AuthorizationService.removeUser() called");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("ID를 통해 유저 조회 실패"));
        userRepository.delete(user);
    }

    public AccessTokenResponseDto reissueAccessToken(String refreshToken) {
        log.info("AuthorizationService.reissueAccessToken() called");

        // 유효성 검사: 토큰 자체가 부적절한지 확인
        long userId = Long.parseLong(tokenProvider.parseClaims(refreshToken).getSubject());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("토큰으로 조회되는 회원 없음"));
        if (!tokenProvider.validateToken(refreshToken, TokenType.REFRESH)) {
            throw new IllegalRequestException("부적절한 토큰");
        }

        // 유효성 검사: DB에 저장된 리프레쉬 토큰과 동일한지 확인
        if (!refreshToken.equals(user.getRefreshToken().getTokenValue())) {
            throw new IllegalRequestException("부적절한 토큰: 다른 곳에서 로그인됨");
        }

        // 엑세스 토큰 생성 및 반환
        return AccessTokenResponseDto.builder()
                .accessToken(tokenProvider.createToken(user, TokenType.ACCESS))
                .build();
    }
}
