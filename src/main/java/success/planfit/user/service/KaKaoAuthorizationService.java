package success.planfit.user.service;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import success.planfit.entity.user.KakaoUser;
import success.planfit.entity.user.User;
import success.planfit.global.exception.IllegalRequestException;
import success.planfit.global.jwt.TokenProvider;
import success.planfit.global.jwt.TokenType;
import success.planfit.global.jwt.dto.TokenResponseDto;
import success.planfit.global.photo.PhotoProvider;
import success.planfit.repository.UserRepository;
import success.planfit.user.dto.KakaoAccessTokenDto;
import success.planfit.user.dto.KakaoUserInfoDto;

@Service
public class KaKaoAuthorizationService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String SCOPE = "profile_nickname,profile_image,account_email";

    private final String CLIENT_ID;
    private final String CLIENT_SECRET;
    private final String REDIRECT_URI;

    public KaKaoAuthorizationService(
            UserRepository userRepository,
            TokenProvider tokenProvider,
            @Value("${keys.kakao.rest-api-key}") String clientId,
            @Value("${keys.kakao.client-secret}") String clientSecret,
            @Value("${keys.kakao.redirect-uri}") String redirectUri
    ) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;

        this.CLIENT_ID = clientId;
        this.CLIENT_SECRET = clientSecret;
        this.REDIRECT_URI = redirectUri;
    }

    public String getRedirectUrl() {
        return new StringBuilder("https://kauth.kakao.com/oauth/authorize?")
                .append("client_id=")
                .append(CLIENT_ID)
                .append("&redirect_uri=")
                .append(REDIRECT_URI)
                .append("&response_type=code")
                .append("&scope=")
                .append(SCOPE)
                .toString();
    }

    public String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 메시지 body 설정
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        params.add("client_id", CLIENT_ID);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("client_secret", CLIENT_SECRET);

        // POST 요청 전송
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, httpEntity, String.class);

        if (isRequestSuccess(responseEntity)) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();

            return gson.fromJson(json, KakaoAccessTokenDto.class)
                    .getAccessToken();
        }

        throw new RuntimeException("카카오 엑세스 토큰 획득 실패");
    }

    @Transactional
    public TokenResponseDto kakaoSignUpOrSignIn(String kakaoAccessToken) {
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);

        if (!kakaoUserInfo.isValidatedEmail()) {
            throw new IllegalRequestException("이메일 인증이 되지 않은 유저입니다.");
        }

        User user = userRepository.findByKakaoIdentifier(kakaoUserInfo.getId())
                .orElseGet(() -> userRepository.save(createUserByInfo(kakaoUserInfo)));

        String accessTokenValue = tokenProvider.createToken(user, TokenType.ACCESS);
        String refreshTokenValue = tokenProvider.createToken(user, TokenType.REFRESH);
        user.getRefreshToken().setTokenValue(refreshTokenValue);

        return createTokenResponseDto(accessTokenValue, refreshTokenValue);
    }

    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(USER_INFO_URL, HttpMethod.GET, httpEntity, String.class);

        if (isRequestSuccess(responseEntity)) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();
            return gson.fromJson(json, KakaoUserInfoDto.class);
        }

        throw new RuntimeException("카카오 유저 정보 획득 실패");
    }

    private boolean isRequestSuccess(ResponseEntity<String> responseEntity) {
        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    private KakaoUser createUserByInfo(KakaoUserInfoDto kakaoUserInfo) {
        return KakaoUser.builder()
                .kakaoIdentifier(kakaoUserInfo.getId())
                .email(kakaoUserInfo.getEmail())
                .name(kakaoUserInfo.getName())
                .profilePhoto(PhotoProvider.getImageFromUrl(kakaoUserInfo.getProfileUrl()))
                .build();
    }

    private TokenResponseDto createTokenResponseDto(String accessToken, String refreshToken) {
        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
