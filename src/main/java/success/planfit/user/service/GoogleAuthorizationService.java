package success.planfit.user.service;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import success.planfit.entity.user.GoogleUser;
import success.planfit.entity.user.User;
import success.planfit.global.jwt.TokenProvider;
import success.planfit.global.jwt.TokenType;
import success.planfit.global.jwt.dto.TokenResponseDto;
import success.planfit.global.photo.PhotoProvider;
import success.planfit.repository.UserRepository;
import success.planfit.user.dto.GoogleAccessTokenDto;
import success.planfit.user.dto.GoogleUserInfoDto;

import java.net.URI;
import java.util.Map;

@Service
public class GoogleAuthorizationService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    private final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
    private final String SCOPE = "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";
    private final String CLIENT_ID;
    private final String CLIENT_SECRET;
    private final String REDIRECT_URI;

    public GoogleAuthorizationService(
            UserRepository userRepository,
            TokenProvider tokenProvider,
            @Value("${keys.google.client-id}") String clientId,
            @Value("${keys.google.client-secret}") String clientSecret,
            @Value("${keys.google.redirect-uri}") String redirectUri
    ) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;

        this.CLIENT_ID = clientId;
        this.CLIENT_SECRET = clientSecret;
        this.REDIRECT_URI = redirectUri;
    }

    public String getRedirectUrl() {
        return new StringBuilder("https://accounts.google.com/o/oauth2/v2/auth?")
                .append("client_id=")
                .append(CLIENT_ID)
                .append("&redirect_uri=")
                .append(REDIRECT_URI)
                .append("&response_type=code")
                .append("&scope=")
                .append(SCOPE)
                .toString();
    }

    public String getGoogleAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> params = Map.of(
                "code", code,
                "scope", SCOPE,
                "client_id", CLIENT_ID,
                "client_secret", CLIENT_SECRET,
                "redirect_uri", REDIRECT_URI,
                "grant_type", "authorization_code"
        );

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(TOKEN_URL, params, String.class);

        if (isRequestSuccess(responseEntity)) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();

            return gson.fromJson(json, GoogleAccessTokenDto.class)
                    .getAccessToken();
        }

        throw new RuntimeException("구글 엑세스 토큰 획득 실패");
    }

    @Transactional
    public TokenResponseDto signUpOrSignIn(String googleAccessToken) {
        GoogleUserInfoDto googleUserInfo = getGoogleUserInfo(googleAccessToken);

        User user = userRepository.findByGoogleIdentifier(googleUserInfo.getId())
                .orElseGet(() -> userRepository.save(createUserByInfo(googleUserInfo)));

        String accessTokenValue = tokenProvider.createToken(user, TokenType.ACCESS);
        String refreshTokenValue = tokenProvider.createToken(user, TokenType.REFRESH);

        setRefreshToken(user, refreshTokenValue);

        return createTokenResponseDto(accessTokenValue, refreshTokenValue);
    }

    private GoogleUserInfoDto getGoogleUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = new StringBuilder(USER_INFO_URL)
                .append("?access_token=")
                .append(accessToken)
                .toString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        if (isRequestSuccess(responseEntity)) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();
            return gson.fromJson(json, GoogleUserInfoDto.class);
        }

        throw new RuntimeException("구글 유저 정보 획득 실패");
    }

    private boolean isRequestSuccess(ResponseEntity<String> responseEntity) {
        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    private GoogleUser createUserByInfo(GoogleUserInfoDto googleUserInfo) {
        return GoogleUser.builder()
                .googleIdentifier(googleUserInfo.getId())
                .email(googleUserInfo.getEmail())
                .name(googleUserInfo.getName())
                .profilePhoto(PhotoProvider.getImageFromUrl(googleUserInfo.getPictureUrl()))
                .build();
    }

    private void setRefreshToken(User user, String refreshTokenValue) {
        user.getRefreshToken()
                .setTokenValue(refreshTokenValue);
    }

    private TokenResponseDto createTokenResponseDto(String accessToken, String refreshToken) {
        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
